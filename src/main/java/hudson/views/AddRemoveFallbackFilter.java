package hudson.views;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.TopLevelItem;
import hudson.model.View;

import java.util.ArrayList;
import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Returns all jobs if no jobs added, or all jobs if no jobs added, depending on configuration.
 * Does not modify the list of added jobs in the other cases.
 *
 * @author davidparsson
 */
public class AddRemoveFallbackFilter extends ViewJobFilter {

	private String fallbackTypeString;
	private FallbackTypes fallbackType;

	static enum FallbackTypes {
		ADD_ALL_IF_NONE_INCLUDED,
		REMOVE_ALL_IF_ALL_INCLUDED
	}

	@DataBoundConstructor
	public AddRemoveFallbackFilter(String fallbackTypeString) {
		this.fallbackTypeString = fallbackTypeString;
		this.fallbackType = FallbackTypes.valueOf(fallbackTypeString);
	}

	Object readResolve() {
		if (fallbackTypeString != null) {
			this.fallbackType = FallbackTypes.valueOf(fallbackTypeString);
		}
		return this;
	}

	public String getFallbackTypeString() {
		return this.fallbackTypeString;
	}

	@Override
	public List<TopLevelItem> filter(List<TopLevelItem> added, List<TopLevelItem> all, View filteringView) {
		if (fallbackType == FallbackTypes.ADD_ALL_IF_NONE_INCLUDED && added.size() == 0) {
			return new ArrayList<TopLevelItem>(all);
		} else if (fallbackType == FallbackTypes.REMOVE_ALL_IF_ALL_INCLUDED && added.size() == all.size()) {
			return new ArrayList<TopLevelItem>();
		} else {
			return added;
		}
	}

	@Extension
	public static class DescriptorImpl extends Descriptor<ViewJobFilter> {
		@Override
		public String getDisplayName() {
		    return hudson.views.filters.Messages.AddRemoveFallbackFilter_DisplayName();
		}

		@Override
		public String getHelpFile() {
			return "/plugin/view-job-filters/add-remove-fallback-help.html";
		}
	}

}
