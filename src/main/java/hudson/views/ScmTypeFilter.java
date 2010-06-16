package hudson.views;

import hudson.DescriptorExtensionList;
import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.SCMedItem;
import hudson.model.TopLevelItem;
import hudson.scm.SCM;
import hudson.scm.SCMDescriptor;

import java.util.ArrayList;
import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;

public class ScmTypeFilter extends AbstractIncludeExcludeJobFilter {

	private String scmType;
	
	@DataBoundConstructor
	public ScmTypeFilter(String scmType, String includeExcludeTypeString) {
		super(includeExcludeTypeString);
		this.scmType = scmType;
	}
	
	public String getScmType() {
		return scmType;
	}

	@Override
	boolean matches(TopLevelItem item) {
		if (item instanceof SCMedItem) {
			SCMedItem sitem = (SCMedItem) item;
			SCM scm = sitem.getScm();
			Descriptor<SCM> descriptor = scm.getDescriptor();
			String name = descriptor.getDisplayName();
			if (scmType.equals(name)) {
				return true;
			}
		}
		return false;
	}
	@Extension
	public static class DescriptorImpl extends Descriptor<ViewJobFilter> {
		@Override
		public String getDisplayName() {
			return "SCM Type Filter";
		}
		@Override
        public String getHelpFile() {
            return "/plugin/view-job-filters/scm-help.html";
        }
		public String getScmTestString() {
			return getScmTypes().toString();
		}
		public List<String> getScmTypes() {
			List<String> types = new ArrayList<String>();
			DescriptorExtensionList<SCM, SCMDescriptor<?>> scms = SCM.all();
			for (SCMDescriptor<?> scm: scms) {
				String name = scm.getDisplayName();
				types.add(name);
			}
			return types;
		}
	}
}
