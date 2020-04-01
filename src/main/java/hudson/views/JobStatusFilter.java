package hudson.views;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Job;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TopLevelItem;
import jenkins.model.ParameterizedJobMixIn;

import org.kohsuke.stapler.DataBoundConstructor;

public class JobStatusFilter extends AbstractIncludeExcludeJobFilter {
	
	private boolean unstable;
	private boolean failed;
	private boolean aborted;
	private boolean disabled;
	private boolean stable;
	
	@DataBoundConstructor
	public JobStatusFilter(boolean unstable, boolean failed, boolean aborted, 
			boolean disabled, boolean stable, 
			String includeExcludeTypeString) {
		super(includeExcludeTypeString);
		this.unstable = unstable;
		this.failed = failed;
		this.aborted = aborted;
		this.disabled = disabled;
		this.stable = stable;
	}
	@SuppressWarnings("rawtypes")
	protected boolean matches(TopLevelItem item) {
		if (item instanceof ParameterizedJobMixIn.ParameterizedJob) {
			ParameterizedJobMixIn.ParameterizedJob project = (ParameterizedJobMixIn.ParameterizedJob) item;
			if (disabled && project.isDisabled()) {
				return true;
			}
		}
		if (item instanceof Job) {
			Job job = (Job) item;
			Run last = job.getLastCompletedBuild();
			if (last != null) {
				Result result = last.getResult();
				if (stable && result == Result.SUCCESS) {
					return true;
				}
				if (aborted && result == Result.ABORTED) {
					return true;
				}
				if (failed && result == Result.FAILURE) {
					return true;
				}
				if (unstable && result == Result.UNSTABLE) {
					return true;
				}
			}
		}
		return false;
	}

	@Extension
	public static class DescriptorImpl extends Descriptor<ViewJobFilter> {
		@Override
		public String getDisplayName() {
			return hudson.views.filters.Messages.JobStatusFilter_DisplayName();
		}
		@Override
        public String getHelpFile() {
            return "/plugin/view-job-filters/job-status-help.html";
        }
	}

	public boolean isUnstable() {
		return unstable;
	}
	public boolean isFailed() {
		return failed;
	}
	public boolean isAborted() {
		return aborted;
	}
	public boolean isDisabled() {
		return disabled;
	}
	public boolean isStable() {
		return stable;
	}
}
