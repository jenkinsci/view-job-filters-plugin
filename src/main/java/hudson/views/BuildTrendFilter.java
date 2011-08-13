package hudson.views;

import hudson.Extension;
import hudson.cli.BuildCommand.CLICause;
import hudson.model.Cause;
import hudson.model.Descriptor;
import hudson.model.Job;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TopLevelItem;
import hudson.model.Cause.RemoteCause;
import hudson.model.Cause.UpstreamCause;
import hudson.model.Cause.UserCause;
import hudson.triggers.SCMTrigger.SCMTriggerCause;
import hudson.triggers.TimerTrigger.TimerTriggerCause;

import org.kohsuke.stapler.DataBoundConstructor;

public class BuildTrendFilter extends AbstractIncludeExcludeJobFilter {

    private static final long ONE_SECOND_MS = 1000;
    private static final long ONE_MINUTE_MS = 60 * ONE_SECOND_MS;
    static final long ONE_HOUR_MS = 60 * ONE_MINUTE_MS;
    static final long ONE_DAY_MS = 24 * ONE_HOUR_MS;
	
	public static enum AmountType {
		
		Days(ONE_DAY_MS), Hours(ONE_HOUR_MS), Builds(-1);
		
		private long divideByAmount;
		AmountType(long divideByAmount) {
			this.divideByAmount = divideByAmount;
		}
		public float convertMillisToAmount(float millis) {
			return millis / divideByAmount;
		}
		
	}
	
	public static enum BuildCountType {
		Latest, All, AtLeastOne
	}
	
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
			@Override
			protected boolean matchesCause(Cause cause) {
				return (cause instanceof UserCause);
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
		},
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
	
	transient private BuildCountType buildCountType;
	private String buildCountTypeString;
	
	transient private StatusType statusType;
	private String statusTypeString;
	
	transient private AmountType amountType;
	private String amountTypeString;
	
	private float amount;
	
	@DataBoundConstructor
	public BuildTrendFilter(
			String buildCountTypeString, String statusTypeString,
			float amount, String amountTypeString,
			String includeExcludeTypeString) {
		super(includeExcludeTypeString);
		this.buildCountTypeString = buildCountTypeString;
		this.buildCountType = BuildCountType.valueOf(buildCountTypeString);
		this.statusTypeString = statusTypeString;
		this.statusType = StatusType.valueOf(statusTypeString);
		this.amount = amount;
		this.amountTypeString = amountTypeString;
		this.amountType = AmountType.valueOf(amountTypeString);
	}
    Object readResolve() {
        if (amountTypeString != null) {
        	amountType = AmountType.valueOf(amountTypeString);
        }
        if (buildCountTypeString != null) {
        	buildCountType = BuildCountType.valueOf(buildCountTypeString);
        }
        if (statusTypeString != null) {
        	statusType = StatusType.valueOf(statusTypeString);
        }
        return super.readResolve();
    }
	
	@SuppressWarnings("unchecked")
	protected boolean matches(TopLevelItem item) {
		if (item instanceof Job) {
			Job job = (Job) item;
			
			// iterate over runs and check conditions
			Run run = job.getLastBuild();
			boolean oneMatched = false;
			int count = 0;
			
			while (run != null) {
				// check the different types of durations to see if we've checked back far enough
				if (amount > 0) {
					count++;
					if (amountType == AmountType.Builds) {
						if (count > amount) {
							break;
						}
					} else {
						// get the amount of time since it last built
						long now = System.currentTimeMillis();
						long then = run.getTimeInMillis();
						float diff = now - then;
						diff = amountType.convertMillisToAmount(diff);
						if (diff > amount) {
							break;
						}
					}
				}
				// now evaluate the build status
				boolean statusMatches = statusType.matches(run);
				if (statusMatches) {
					if (buildCountType == BuildCountType.AtLeastOne || buildCountType == BuildCountType.Latest) {
						return true;
					}
				} else {
					if (buildCountType == BuildCountType.All) {
						return false;
					}
				}
				
				// if we got this far, and the type is for "latest" then it's not going to match
				if (buildCountType == BuildCountType.Latest) {
					return false;
				}
				
				oneMatched = true;
				run = run.getPreviousBuild();
			}
			// if we're talking about "all builds" and there was at least one build, then
			// it means no builds didn't match the filter
			if (buildCountType == BuildCountType.All && oneMatched) {
				return true;
			}
			
			// means we ran out of builds to check and did not find a match
			return false;
		} else {
			// wasn't of type "job"
			return false;
		}
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

	public String getAmountTypeString() {
		return amountTypeString;
	}
	public float getAmount() {
		return amount;
	}
	public String getBuildCountTypeString() {
		return buildCountTypeString;
	}
	public String getStatusTypeString() {
		return statusTypeString;
	}
	
}
