package hudson.views;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.TopLevelItem;
import hudson.model.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Returns all jobs that don't show up in other jobs, not counting any "all jobs" views.
 * @author jacob
 */
public class UnclassifiedJobsFilter extends AbstractIncludeExcludeJobFilter {

	@DataBoundConstructor
	public UnclassifiedJobsFilter(String includeExcludeTypeString) {
		super(includeExcludeTypeString);
	}
	
    @Override
    protected void doFilter(List<TopLevelItem> filtered, List<TopLevelItem> all, View filteringView) {
    	List<View> allViews = OtherViewsFilter.getAllViews();
		allViews.remove(filteringView);
		
		int allJobsCount = all.size();
		List<TopLevelItem> classified = getAllClassifiedItems(allViews, allJobsCount);
		
        for (TopLevelItem item: all) {
        	boolean matched = !classified.contains(item);
    		filterItem(filtered, item, matched);
        }
    }
    private List<TopLevelItem> getAllClassifiedItems(List<View> allViews, int allJobsCount) {
    	List<TopLevelItem> classified = new ArrayList<TopLevelItem>();
		for (View otherView: allViews) {
			Collection<TopLevelItem> items = otherView.getItems();
			// do not bother looking at views that already contain all items
			// the advantage of using this strategy is that it covers any type of "All Jobs" views
			//		I care about this, because I have an AllJobsFilter, but anyone could have set up an ".*" regex, etc
			// the disadvantage is that if some view just happens to cover all views (perhaps it happens over time)
			//		then suddenly this filter stops accounting for that view
			if (items.size() < allJobsCount) {
				classified.addAll(items);
			}
		}
    	return classified;
    }
	
	@Extension
	public static class DescriptorImpl extends Descriptor<ViewJobFilter> {
		@Override
		public String getDisplayName() {
			return "Unclassified Jobs";
		}
        @Override
        public String getHelpFile() {
            return "/plugin/view-job-filters/unclassified-jobs-help.html";
        }
	}

}
