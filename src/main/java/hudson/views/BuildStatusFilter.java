package hudson.views;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.TopLevelItem;

import org.kohsuke.stapler.DataBoundConstructor;

public class BuildStatusFilter extends AbstractIncludeExcludeJobFilter {
	
	private boolean neverBuilt;
	private boolean building;
	private boolean inBuildQueue;
	private boolean buildable;
	
	@DataBoundConstructor
	public BuildStatusFilter(boolean neverBuilt,
			boolean building, boolean inBuildQueue,
			boolean buildable,
			String includeExcludeTypeString) {
		super(includeExcludeTypeString);
		this.neverBuilt = neverBuilt;
		this.building = building;
		this.inBuildQueue = inBuildQueue;
		this.buildable = buildable;
	}
	@SuppressWarnings("rawtypes")
	protected boolean matches(TopLevelItem item) {
		if (item instanceof Job) {
			Job job = (Job) item;
			if (building && job.isBuilding()) {
				return true;
			}
			if (inBuildQueue && job.isInQueue()) {
				return true;
			}
			if (buildable && job.isBuildable()){
			    return true;
            }
			Run last = job.getLastBuild();
			if (last == null && neverBuilt) {
				return true;
			}
		}
		return false;
	}

	@Extension
	public static class DescriptorImpl extends Descriptor<ViewJobFilter> {
		@Override
		public String getDisplayName() {
			return hudson.views.filters.Messages.BuildStatusFilter_DisplayName();
		}
		@Override
        public String getHelpFile() {
            return "/plugin/view-job-filters/build-status-help.html";
        }
	}

	public boolean isNeverBuilt() {
		return neverBuilt;
	}
	public boolean isBuilding() {
		return building;
	}
	public boolean isInBuildQueue() {
		return inBuildQueue;
	}
	public boolean isBuildable() {
	    return buildable;
    }
}
