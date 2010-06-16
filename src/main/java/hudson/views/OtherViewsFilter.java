package hudson.views;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.TopLevelItem;
import hudson.model.View;
import hudson.util.ListBoxModel;

import java.util.Collection;

import javax.servlet.ServletException;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

/**
 * TODO known bug - you can select views recursively - will permanently break that view
 * @author jacob.robertson
 */
public class OtherViewsFilter extends AbstractIncludeExcludeJobFilter {

	private String otherView;
	
	@DataBoundConstructor
	public OtherViewsFilter(String includeExcludeTypeString, String otherView) {
		super(includeExcludeTypeString);
		this.otherView = otherView;
	}
	
	@Override
	boolean matches(TopLevelItem item) {
		// look at all views
		Collection<View> views = Hudson.getInstance().getViews();
		for (View view: views) {
			String viewName = view.getViewName();
			// narrow down to my "other view"
			if (viewName.equals(otherView)) {
				Collection<TopLevelItem> items = view.getItems();
				for (TopLevelItem viewItem: items) {
					// see if the item for "that" view matches the one we're checking
					if (viewItem == item) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public String getOtherView() {
		return otherView;
	}
	
	@Extension
	public static class DescriptorImpl extends Descriptor<ViewJobFilter> {
		
		@Override
		public String getDisplayName() {
			return "Other Views Filter";
		}
		@Override
        public String getHelpFile() {
            return "/plugin/view-job-filters/other-views-help.html";
        }
		
        /**
         * This method determines the values of the album drop-down list box.
         */
        public ListBoxModel doFillOtherViewItems(StaplerRequest req, @QueryParameter String name) throws ServletException {
            ListBoxModel m = new ListBoxModel();
			Collection<View> views = Hudson.getInstance().getViews();
			for (View view: views) {
				String viewName = view.getViewName();
				m.add(viewName);
			}
            return m;
        }
	}

}
