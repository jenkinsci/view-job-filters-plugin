package hudson.views;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.DescriptorVisibilityFilter;
import hudson.model.ItemGroup;
import hudson.model.Items;
import hudson.model.TopLevelItem;
import hudson.model.TopLevelItemDescriptor;

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
			if (type.getId().equals(jobType)) {
				return type;
			}
		}
		return null;
	}

	@Override
	protected boolean matches(TopLevelItem item) {
        TopLevelItemDescriptor d = getJobType();
        return d != null && d.testInstance(item);
	}

	@Extension
	public static class DescriptorImpl extends Descriptor<ViewJobFilter> {
		@Override
		public String getDisplayName() {
			return "Job Type Filter";
		}
		public List<TopLevelItemDescriptor> getJobTypes(ItemGroup<?> context) {
            return DescriptorVisibilityFilter.apply(context, Items.all());
		}
	}
}

