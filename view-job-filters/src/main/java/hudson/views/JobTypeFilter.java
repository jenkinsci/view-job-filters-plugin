package hudson.views;

import hudson.DescriptorExtensionList;
import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Items;
import hudson.model.TopLevelItem;
import hudson.model.TopLevelItemDescriptor;

import java.util.ArrayList;
import java.util.List;

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

	public TopLevelItemDescriptor getJobType() {
		for (TopLevelItemDescriptor type: Items.all()) {
			if (matches(type)) {
				return type;
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
		public List<TopLevelItemDescriptor> getJobTypes() {
			List<TopLevelItemDescriptor> types = new ArrayList<TopLevelItemDescriptor>();
			DescriptorExtensionList<TopLevelItem, TopLevelItemDescriptor> all = Items.all();
			for (TopLevelItemDescriptor one: all) {
				types.add(one);
			}
			return types;
		}
	}
}

