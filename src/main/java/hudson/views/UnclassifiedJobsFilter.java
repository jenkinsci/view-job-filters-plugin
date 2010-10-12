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
 *
 */
public class UnclassifiedJobsFilter extends AbstractIncludeExcludeJobFilter {

	@DataBoundConstructor
	public UnclassifiedJobsFilter(String includeExcludeTypeString) {
		super(includeExcludeTypeString);
	}
	
    @Override
    public List<TopLevelItem> filter(List<TopLevelItem> added, List<TopLevelItem> all, View filteringView) {
    	List<View> allViews = OtherViewsFilter.getAllViews();
		allViews.remove(filteringView);
		
		int allJobsCount = all.size();
		
    	List<TopLevelItem> filtered = new ArrayList<TopLevelItem>(added);
        for (TopLevelItem item: all) {
        	boolean matched = matches(item, allViews, allJobsCount);
    		if (exclude(matched)) {
    			filtered.remove(item);
    		}
    		if (include(matched) && !filtered.contains(item)) {
    			filtered.add(item);
    		}
        }
        return filtered;
    }

	@Override
	boolean matches(TopLevelItem item) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Does this item show up in any other view?
	 */
	boolean matches(TopLevelItem item, List<View> allViews, int allJobsCount) {
		for (View otherView: allViews) {
			Collection<TopLevelItem> items = otherView.getItems();
			// do not bother looking at views that already contain all items
			// the advantage of using this strategy is that it covers any type of "All Jobs" views
			//		I care about this, because I have an AllJobsFilter, but anyone could have set up an ".*" regex, etc
			// the disadvantage is that if some view just happens to cover all views (perhaps it happens over time)
			//		then suddenly this filter stops accounting for that view
			if (allJobsCount == items.size()) {
				continue;
			}
			for (TopLevelItem otherViewItem: items) {
				if (otherViewItem == item) {
					// we found this item, so we will not match
					return false;
				}
			}
		}
		// if we get here it means this item was not found in any other view
		return true;
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
