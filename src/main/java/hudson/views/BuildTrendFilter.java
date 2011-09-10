package hudson.views;

import hudson.Extension;
import hudson.cli.BuildCommand.CLICause;
import hudson.model.Cause;
import hudson.model.Descriptor;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.Cause.RemoteCause;
import hudson.model.Cause.UpstreamCause;
import hudson.triggers.SCMTrigger.SCMTriggerCause;
import hudson.triggers.TimerTrigger.TimerTriggerCause;

import org.kohsuke.stapler.DataBoundConstructor;

public class BuildTrendFilter extends AbstractBuildTrendFilter {

	public static enum StatusType {
		Started() {
			@SuppressWarnings("unchecked")
			@Override
			public boolean matches(Run run) {
				return !run.hasntStartedYet();
			}
		},
		Completed() {
			@Override
			public boolean matches(Result result) {
				return !(result == Result.ABORTED || result == Result.NOT_BUILT);
			}
		},
		Stable() {
			@Override
			public boolean matches(Result result) {
				return (result == Result.SUCCESS);
			}
		},
		Unstable() {
			@Override
			public boolean matches(Result result) {
				return (result == Result.UNSTABLE);
			}
		}, 
		Failed() {
			@Override
			public boolean matches(Result result) {
				return (result == Result.FAILURE);
			}
		}, 
		NotStable() {
			@Override
			public boolean matches(Result result) {
				return (
						result == Result.FAILURE 
						|| result == Result.UNSTABLE
						|| result == Result.ABORTED
						);
			}
		},
		TriggeredByScmPoll(true) {
			@Override
			protected boolean matchesCause(Cause cause) {
				return (cause instanceof SCMTriggerCause);
			}
		},
		TriggeredByTimer(true) {
			@Override
			protected boolean matchesCause(Cause cause) {
				return (cause instanceof TimerTriggerCause);
			}
		},
		TriggeredByUser(true) {
			@SuppressWarnings("unchecked")
			@Override
			protected boolean matchesCause(Cause cause) {
				if (cause == null) {
					return false;
				}
				// have to have this check because jenkins 1.427 introduced "UserIdCause" (vs "UserCause")
				// and I don't want to make this class depend on the bleeding edge
				Class cls = cause.getClass();
				String name = cls.getSimpleName();
				return name.startsWith("User");
			}
		},
		TriggeredByRemote(true) {
			@Override
			protected boolean matchesCause(Cause cause) {
				return (cause instanceof RemoteCause);
			}
		},
		TriggeredByUpstream(true) {
			@Override
			protected boolean matchesCause(Cause cause) {
				return (cause instanceof UpstreamCause);
			}
		},
		TriggeredByCli(true) {
			@Override
			protected boolean matchesCause(Cause cause) {
				return (cause instanceof CLICause);
			}
		}
		;
		@SuppressWarnings("unchecked")
		public boolean matchesCause(Run run) {
			for (Object causeObject: run.getCauses()) {
				Cause cause = (Cause) causeObject;
				if (matchesCause(cause)) {
					return true;
				}
			}
			return false;
		}
		protected boolean matchesCause(Cause cause) {
			return false;
		}
		@SuppressWarnings("unchecked")
		public boolean matches(Run run) {
			if (matchCause) {
				return matchesCause(run);
			} else {
				return matches(run.getResult());
			}
		}
		public boolean matches(Result result) {
			return false;
		}
		
		private boolean matchCause = false;
		StatusType() {
		}
		StatusType(boolean matchCause) {
			this.matchCause = matchCause;
		}
	}
	
	transient private StatusType statusType;
	private String statusTypeString;
	
	@DataBoundConstructor
	public BuildTrendFilter(
			String buildCountTypeString, String statusTypeString,
			float amount, String amountTypeString,
			String includeExcludeTypeString) {
		super(buildCountTypeString, amount, amountTypeString, includeExcludeTypeString);
		this.statusTypeString = statusTypeString;
		this.statusType = StatusType.valueOf(statusTypeString);
	}
    Object readResolve() {
        if (statusTypeString != null) {
        	statusType = StatusType.valueOf(statusTypeString);
        }
        return super.readResolve();
    }

    @SuppressWarnings("unchecked")
	@Override
    public boolean matchesRun(Run run) {
    	return statusType.matches(run);
    }
	public String getStatusTypeString() {
		return statusTypeString;
	}

	@Extension
	public static class DescriptorImpl extends Descriptor<ViewJobFilter> {
		@Override
		public String getDisplayName() {
			return "Build Trend Filter";
		}
		@Override
        public String getHelpFile() {
            return "/plugin/view-job-filters/last-build-time-help.html";
        }
	}
	
}
