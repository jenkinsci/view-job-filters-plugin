package hudson.views;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.TopLevelItem;
import hudson.model.View;
import hudson.model.ViewGroup;
import hudson.util.ListBoxModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * TODO bug - deleting a view doesn't work - for now, this view just doesn't do any filtering
 * TODO bug - renaming a view only works some of the time because if this filter isn't used between
 * 				the time the view was renamed and a save takes place, this filter won't know about
 * 				the rename.
 * TODO bug - you can select a view "recursively" - need a way to avoid this completely rather than just
 * 				do damage control when they do select it.
 * TODO limitation - cannot perform validations in hetero list?
 * 					
 * @author jacob.robertson
 */
public class OtherViewsFilter extends AbstractIncludeExcludeJobFilter {

	private String otherViewName;
	private transient View otherView;
	
	/**
	 * Constructor called by stapler to inject fields.
	 */
	@DataBoundConstructor
	public OtherViewsFilter(String includeExcludeTypeString, String otherViewName) {
		super(includeExcludeTypeString);
		this.otherViewName = otherViewName;
		if (otherViewName != null){
			this.otherView = getView(otherViewName);
		}
	}
	
	@Override
    public List<TopLevelItem> filter(List<TopLevelItem> added, List<TopLevelItem> all, View filteringView) {
    	if (getOtherView() == null) {
    		// happens when a view is deleted and this filter doesn't know about it (known issue)
    		return added;
    	} else if (getOtherView() == filteringView) {
    		// happens when you select the view recursively
        	return added;
    	} else {
    		return super.filter(added, all, filteringView);
    	}
    }
	
	@Override
	boolean matches(TopLevelItem item) {
		View otherView = getOtherView();
		Collection<TopLevelItem> items = otherView.getItems();
		for (TopLevelItem viewItem: items) {
			// see if the item for "that" view matches the one we're checking
			// TODO evaluate recursing into ViewGroups here as well
			//		not sure we want that, and it's not a backwards compatible change
			if (viewItem == item) {
				return true;
			}
		}
		return false;
	}
	Object writeReplace() {
		// Right before persisting, try to account for any view name changes 
		if (otherView != null) {
			otherViewName = toName(otherView);
		}
		return this;
	}
	public View getOtherView() {
		if (otherView == null && otherViewName != null) {
			otherView = getView(otherViewName);
		}
		return otherView;
	}
	public String getOtherViewName() {
		View got = getOtherView();
		if (got != null) {
			return toName(got);
		} else {
			return null;
		}
	}
	
	@Extension
	public static class DescriptorImpl extends Descriptor<ViewJobFilter> {
		
		// TODO messages
		private static final String NO_VIEW_SELECTED = "<select a view other than this one>";
		
		@Override
		public String getDisplayName() {
			return "Other Views Filter";
		}
		@Override
        public String getHelpFile() {
            return "/plugin/view-job-filters/other-views-help.html";
        }
		

        /**
         * This method determines the values of the other views drop-down list box.
         */
        public ListBoxModel doFillOtherViewNameItems() throws ServletException {
            ListBoxModel m = new ListBoxModel();
			List<View> views = getAllViews();
			
			m.add(NO_VIEW_SELECTED);
			for (View view: views) {
				String viewName = toName(view);
				m.add(viewName);
			}
            return m;
        }
        
        /*
         * Checks if the chosen view is valid.
         *
         * Does not work in hetero-list?
         *
        public FormValidation doCheckOtherViewName(@QueryParameter String otherViewName) throws IOException, ServletException, InterruptedException  {
        	if (NO_VIEW_SELECTED.equals(otherViewName)) {
        		return FormValidation.error("You must select a view");
        	}
            View view = Hudson.getInstance().getView(otherViewName);
            if (view == null) {
                return FormValidation.error("The view you selected (\"" + otherViewName + "\") has been deleted or renamed");
            }
            return FormValidation.ok();
        }
        */
        
	}
	private static void addViews(View view, List<View> views) {
		if (view instanceof ViewGroup) {
			ViewGroup group = (ViewGroup) view;
			Collection<View> subViews = group.getViews();
			for (View sub: subViews) {
				addViews(sub, views);
			}
		} else {
			views.add(view);
		}
	}
	
	public static List<View> getAllViews() {
		Collection<View> baseViews = Hudson.getInstance().getViews();
		
		// build comprehensive list
		List<View> views = new ArrayList<View>();
		for (View view: baseViews) {
			addViews(view, views);
		}
		return views;
	}
	
	/**
	 * Takes into account nested names.
	 */
	public static View getView(String name) {
		Collection<View> views = getAllViews();
		for (View view: views) {
			String otherName = toName(view);
			if (otherName.equals(name)) {
				return view;
			}
		}
		return null;
	}
	/**
	 * Alternate strategy for getting name, to handle nested views.
	 */
	private static String toName(View view) {
		String name = view.getViewName();
		ViewGroup owner = view.getOwner();
		if (owner instanceof View && owner != view) {
			String parentName = toName((View) owner);
			name = parentName + " / " + name;
		}
		return name;
	}

}
