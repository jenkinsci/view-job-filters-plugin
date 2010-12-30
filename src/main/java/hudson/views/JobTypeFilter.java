package hudson.views;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Items;
import hudson.model.TopLevelItem;
import hudson.model.TopLevelItemDescriptor;
import hudson.util.ListBoxModel;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * {@link ViewJobFilter} based on the type of the job.
 *
 * @author Kohsuke Kawaguchi
 */
public class JobTypeFilter extends AbstractIncludeExcludeJobFilter {

	private String jobType;

	@DataBoundConstructor
	public JobTypeFilter(String jobType, String includeExcludeTypeString) {
		super(includeExcludeTypeString);
		this.jobType = jobType;
	}

	/**
	 * Used to display the selected option.
	 * TODO still does not work in IE 7 (Tested on Version 7.0.5730.11)
	 * 	- will work if we change this class/jelly to be exactly the way the ScmTypeFilter is.
	 *  - the observed behavior at this time is that the selection 
	 *  	will always display the first option regardless of what was saved 
	 */
	public String getJobType() {
		for (TopLevelItemDescriptor type: Items.all()) {
			if (matches(type)) {
				return type.clazz.getName();
			}
		}
		return null;
	}


	private boolean matches(TopLevelItemDescriptor type) {
        return type.clazz.getName().equals(jobType);
    }

	@Override
	protected boolean matches(TopLevelItem item) {
        return matches(item.getDescriptor());
	}

	@Extension
	public static class DescriptorImpl extends Descriptor<ViewJobFilter> {
		@Override
		public String getDisplayName() {
			return "Job Type Filter";
		}

		public ListBoxModel doFillJobTypeItems() {
            ListBoxModel r = new ListBoxModel();
            for (TopLevelItemDescriptor type: Items.all()) {
                r.add(type.getDisplayName(), type.clazz.getName());
            }
			return r;
		}
	}
}

