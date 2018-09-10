package hudson.views;

import hudson.DescriptorExtensionList;
import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.SCMedItem;
import hudson.model.TopLevelItem;
import hudson.scm.SCM;
import hudson.scm.SCMDescriptor;
import hudson.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;
import org.springframework.util.ClassUtils;

public class ScmTypeFilter extends AbstractIncludeExcludeJobFilter {

	private String scmType;
	
	@DataBoundConstructor
	public ScmTypeFilter(String scmType, String includeExcludeTypeString) {
		super(includeExcludeTypeString);
		this.scmType = scmType;
	}
	
	@SuppressWarnings("rawtypes")
	public SCMDescriptor getScmType() {
		List<SCMDescriptor> types = ((DescriptorImpl) getDescriptor()).getScmTypes();
		for (SCMDescriptor type: types) {
			if (matches(type)) {
				return type;
			}
		}
		return null;
	}
	@SuppressWarnings("rawtypes")
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

	@SuppressWarnings("rawtypes")
	@Override
	protected boolean matches(TopLevelItem item) {
		if (item instanceof SCMedItem) {
			SCMedItem sitem = (SCMedItem) item;
			SCM scm = sitem.getScm();
			SCMDescriptor descriptor = scm.getDescriptor();
			if (matches(descriptor)) {
				return true;
			}
		} else {
	    	/*
    		 * FIX JENKINS-31710
    		 * To keep backward compatibility with the required core '1.509.3' 
    		 * (which doesn't contains the interface jenkins.triggers.SCMTriggerItem),
    		 * we use reflection to get the appropriate method and data.
    		 * This approach is effective but ugly, the best option is to require an newer version of Jenkins core (> 1.568).
    		 * TODO: Update the required core version and simple replace this code with:
    		 * 
    		 * if (item instanceof jenkins.triggers.SCMTriggerItem) {
    		 *    for (SCM scm : ((SCMTriggerItem) item).getSCMs()) {
    		 *        SCMDescriptor descriptor = scm.getDescriptor();
    		 *        // if one of then matches, then include the item
    		 *        if (matches(descriptor)) {
    		 *        		return true;
    		 *        }
    		 *    }
    		 * }
    		 */
    		Class[] interfaces = ClassUtils.getAllInterfaces(item);

    		// check if there are any interfaces
    		if (interfaces != null && interfaces.length > 0) {
	    		for (Class iface : interfaces) {

	    			// check if the item implements the right interface
	    			if (iface.getCanonicalName().equals("jenkins.triggers.SCMTriggerItem")) {

	    				// get the method which returns the list of SCM items
	    				Method getSCMs = ReflectionUtils.findMethod(item.getClass(), "getSCMs");

	    				// get the return and convert it
	    				@SuppressWarnings("unchecked")
						Collection<? extends SCM> scms = (Collection<? extends SCM>)ReflectionUtils.invokeMethod(getSCMs, item);

	    				// normal approach over all items
	    				if (scms != null && !scms.isEmpty()) {
	    		    		for (SCM scm : scms) {
	    		    			SCMDescriptor descriptor = scm.getDescriptor();
	    		    			// if one of then matches, then include the item
	    		    			if (matches(descriptor)) {
	    		    				return true;
	    		    			}
	    		    		}
	    	    		}

	    				break;
	    			}
	    		}
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
		@SuppressWarnings("rawtypes")
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
