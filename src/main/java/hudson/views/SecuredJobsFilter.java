package hudson.views;

import hudson.Extension;
import hudson.model.JobProperty;
import hudson.model.TopLevelItem;
import hudson.model.Descriptor;
import hudson.model.Job;
import hudson.security.AuthorizationMatrixProperty;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Simply filters on whether a job is secured through the "Project-based Matrix Authorization Strategy".
 * 
 * @author jacob
 */
public class SecuredJobsFilter extends AbstractIncludeExcludeJobFilter {
		
	@DataBoundConstructor
	public SecuredJobsFilter(String includeExcludeTypeString) {
		super(includeExcludeTypeString);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected boolean matches(TopLevelItem item) {
		if (item instanceof Job) {
			Job job = (Job) item;
			JobProperty prop = job.getProperty(AuthorizationMatrixProperty.class);
			return (prop != null);
		} else {
			return false;
		}
	}
	
	@Extension
	public static class DescriptorImpl extends Descriptor<ViewJobFilter> {
		@Override
		public String getDisplayName() {
			return "Project-based Secured Jobs";
		}
        @Override
        public String getHelpFile() {
            return "/plugin/view-job-filters/secured-jobs-help.html";
        }
	}

}
