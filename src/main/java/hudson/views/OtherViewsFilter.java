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

/**
 * TODO known bug - you can select views recursively - will permanently break that view
 * @author jacob.robertson
 */
public class OtherViewsFilter extends AbstractIncludeExcludeJobFilter {

	private View otherView;
	
	/**
	 * Constructor called by stapler to inject fields.
	 */
	@DataBoundConstructor
	public OtherViewsFilter(String includeExcludeTypeString, String otherViewName) {
		super(includeExcludeTypeString);
		if (otherViewName != null){
			this.otherView = Hudson.getInstance().getView(otherViewName);
		}
	}
	
	@Override
	boolean matches(TopLevelItem item) {
		// look at all views
		Collection<View> views = Hudson.getInstance().getViews();
		for (View view: views) {
			String viewName = view.getViewName();
			// narrow down to my "other view"
			if (viewName.equals(getOtherViewName())) {
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
	public View getOtherView() {
		return otherView;
	}
	public String getOtherViewName() {
		return otherView.getViewName();
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
        public ListBoxModel doFillOtherViewNameItems() throws ServletException {
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
