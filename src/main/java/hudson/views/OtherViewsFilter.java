package hudson.views;

import hudson.Extension;
import hudson.model.*;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;

import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched;

/**
 * TODO bug - deleting a view doesn't work - for now, this view just doesn't do any filtering
 * TODO bug - renaming a view only works some of the time because if this filter isn't used between
 * 				the time the view was renamed and a save takes place, this filter won't know about
 * 				the rename.
 * @author jacob.robertson
 */
public class OtherViewsFilter extends AbstractIncludeExcludeJobFilter {

	private String otherViewName;
	private transient View otherView;
	
	/*
	 * Constructor called by stapler to inject fields.
	 */
	@DataBoundConstructor
	public OtherViewsFilter(String includeExcludeTypeString, String otherViewName) {
		super(includeExcludeTypeString);
		this.otherViewName = otherViewName;
		if (otherViewName != null){
			this.otherView = ViewGraph.getView(otherViewName);
		}
	}

    @Override
    protected void doFilter(List<TopLevelItem> filtered, List<TopLevelItem> all, View filteringView) {
    	if (getOtherView() == null) {
    		// happens when a view is deleted and this filter doesn't know about it (known issue)
    		return;
    	}

    	ViewGraph viewGraph = new ViewGraph();
    	if (viewGraph.getViewsInCycles().contains(getOtherView())) {
			return;
		}

        Collection<TopLevelItem> otherViewItems = getOtherView().getItems();
        for (TopLevelItem item: all) {
            boolean matched = otherViewItems.contains(item);
            filterItem(filtered, item, matched);
        }
    }
	
	Object writeReplace() {
		// Right before persisting, try to account for any view name changes 
		if (otherView != null) {
			otherViewName = ViewGraph.toName(otherView);
		}
		return this;
	}

	public View getOtherView() {
		if (otherView == null && otherViewName != null) {
			otherView = ViewGraph.getView(otherViewName);
		}
		return otherView;
	}

	public String getOtherViewName() {
		View got = getOtherView();
		if (got != null) {
			return ViewGraph.toName(got);
		} else {
			return null;
		}
	}
	
	@Extension
	public static class DescriptorImpl extends Descriptor<ViewJobFilter> {
		
		private static final String NO_VIEW_SELECTED = "";
		
		@Override
		public String getDisplayName() {
			return "Other Views Filter";
		}
		@Override
        public String getHelpFile() {
            return "/plugin/view-job-filters/other-views-help.html";
        }
		

        /*
         * This method determines the values of the other views drop-down list box.
         */
        public ListBoxModel doFillOtherViewNameItems() throws ServletException {
            ListBoxModel m = new ListBoxModel();
			List<View> views = ViewGraph.getAllViews();
			
			m.add(NO_VIEW_SELECTED);
			for (View view: views) {
				String viewName = ViewGraph.toName(view);
				m.add(viewName);
			}
            return m;
        }
        
        /*
         * Checks if the chosen view is valid.
         */
        public FormValidation doCheckOtherViewName(@QueryParameter String otherViewName, @QueryParameter String viewName) throws IOException, ServletException, InterruptedException  {
        	if (NO_VIEW_SELECTED.equals(otherViewName)) {
        		return FormValidation.error("You must select a view");
        	}

			View otherView = ViewGraph.getView(otherViewName);
            if (otherView == null) {
                return FormValidation.error("The view you selected (\"" + otherViewName + "\") has been deleted or renamed");
            }

            View thisView = ViewGraph.getView(viewName);
            if (thisView == null) {
				return FormValidation.warning("Unable to validate filter");
			}

			ListView thisViewNew = new ListView(thisView.getViewName());
			thisViewNew.getJobFilters().add(new OtherViewsFilter(includeMatched.name(), otherViewName));

			Map<String, View> views = ViewGraph.getAllViewsByName();
			views.put(ViewGraph.toName(thisView), thisViewNew);

			ViewGraph viewGraph = new ViewGraph(views);
			if (viewGraph.getViewsInCycles().contains(thisViewNew)) {
				List<View> cycle = viewGraph.getFirstCycleWithView(thisViewNew);
				cycle.set(cycle.indexOf(thisViewNew), thisView);
				return FormValidation.error("Circular view definition: " + ViewGraph.toName(cycle));
			}
            return FormValidation.ok();
        }

	}
}
