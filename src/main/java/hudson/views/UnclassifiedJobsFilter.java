package hudson.views;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.ListView;
import hudson.model.TopLevelItem;
import hudson.model.View;

import java.io.IOException;
import java.util.*;

import hudson.util.FormValidation;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import jakarta.servlet.ServletException;

import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched;

/**
 * Returns all jobs that don't show up in other jobs, not counting any "all jobs" views
 * and views that contain UnclassifiedJobsFilters.
 * @author jacob
 */
public class UnclassifiedJobsFilter extends AbstractIncludeExcludeJobFilter {

	@DataBoundConstructor
	public UnclassifiedJobsFilter(String includeExcludeTypeString) {
		super(includeExcludeTypeString);
	}
	
    @Override
    protected void doFilter(List<TopLevelItem> filtered, List<TopLevelItem> all, View filteringView) {
    	ViewGraph viewGraph = new ViewGraph();
		List<TopLevelItem> classified = getAllClassifiedItems(viewGraph.getViewsNotInCycles(), all.size(), filteringView);
		
        for (TopLevelItem item: all) {
        	boolean matched = !classified.contains(item);
    		filterItem(filtered, item, matched);
        }
    }

	private List<TopLevelItem> getAllClassifiedItems(Set<View> allViews, int allJobsCount, View filteringView) {
    	List<TopLevelItem> classified = new ArrayList<TopLevelItem>();
		for (View otherView: allViews) {
		    if (otherView != filteringView) {
				Collection<TopLevelItem> items = otherView.getItems();
				// Do not bother looking at views that already contain all items.
				// The advantage of using this strategy is that it covers any type of "All Jobs" views.
				// I care about this, because I have an AllJobsFilter, but anyone could have set up an ".*" regex, etc.
				// The disadvantage is that if some view just happens to cover all views (perhaps it happens over time)
				// then suddenly this filter stops accounting for that view
				if (items.size() < allJobsCount) {
					classified.addAll(items);
				}
			}
		}
    	return classified;
    }
	
	@Extension
	public static class DescriptorImpl extends Descriptor<ViewJobFilter> {
		@Override
		public String getDisplayName() {
			return hudson.views.filters.Messages.UnclassifiedJobsFilter_DisplayName();
		}
        @Override
        public String getHelpFile() {
            return "/plugin/view-job-filters/unclassified-jobs-help.html";
        }

		/*
		 * Checks if the chosen view is valid.
		 */
		public FormValidation doCheck(@QueryParameter String viewName) throws IOException, ServletException, InterruptedException  {
			View thisView = ViewGraph.getView(viewName);
			if (thisView == null) {
				return FormValidation.warning(hudson.views.filters.Messages.UnclassifiedJobsFilter_Validation_CannotValidate());
			}

			ListView thisViewNew = new ListView(thisView.getViewName());
			thisViewNew.getJobFilters().add(new UnclassifiedJobsFilter(includeMatched.name()));

			Map<String, View> views = ViewGraph.getAllViewsByName();
			views.put(ViewGraph.toName(thisView), thisViewNew);

			ViewGraph viewGraph = new ViewGraph(views);
			if (viewGraph.getViewsInCycles().contains(thisViewNew)) {
				List<View> cycle = viewGraph.getFirstCycleWithView(thisViewNew);
				cycle.set(cycle.indexOf(thisViewNew), thisView);
				return FormValidation.error(hudson.views.filters.Messages.UnclassifiedJobsFilter_Validation_CircularViewDefinition(ViewGraph.toName(cycle)));
			}
			return FormValidation.ok();
		}

	}

}
