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
	
	@SuppressWarnings("unchecked")
	public SCMDescriptor getScmType() {
		List<SCMDescriptor> types = ((DescriptorImpl) getDescriptor()).getScmTypes();
		for (SCMDescriptor type: types) {
			if (matches(type)) {
				return type;
			}
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	private boolean matches(SCMDescriptor type) {
		// this is the correct behavior
		if (type.clazz.getName().equals(scmType)) {
			return true;
		}
		// this is for backwards compatibility,
		// but can fail due to localization
		if (type.getDisplayName().equals(scmType)) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	boolean matches(TopLevelItem item) {
		if (item instanceof SCMedItem) {
			SCMedItem sitem = (SCMedItem) item;
			SCM scm = sitem.getScm();
			SCMDescriptor descriptor = scm.getDescriptor();
			if (matches(descriptor)) {
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
		@SuppressWarnings("unchecked")
		public List<SCMDescriptor> getScmTypes() {
			List<SCMDescriptor> types = new ArrayList<SCMDescriptor>();
			DescriptorExtensionList<SCM, SCMDescriptor<?>> scms = SCM.all();
			for (SCMDescriptor scm: scms) {
				types.add(scm);
			}
			return types;
		}
	}
}
