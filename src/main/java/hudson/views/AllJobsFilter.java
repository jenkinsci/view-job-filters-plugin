package hudson.views;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.TopLevelItem;
import hudson.model.View;

import java.util.ArrayList;
import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Returns all jobs.  Easier than clicking all job checkboxes, and easier than using regex of ".*"
 * @author jacob
 *
 */
public class AllJobsFilter extends ViewJobFilter {

	@DataBoundConstructor
	public AllJobsFilter() {
	}
	
	@Override
	public List<TopLevelItem> filter(List<TopLevelItem> added, List<TopLevelItem> all, View filteringView) {
		return new ArrayList<TopLevelItem>(all);
	}
	@Extension
	public static class DescriptorImpl extends Descriptor<ViewJobFilter> {
		@Override
		public String getDisplayName() {
			return "All Jobs";
		}
        @Override
        public String getHelpFile() {
            return "/plugin/view-job-filters/all-jobs-help.html";
        }
	}

}
