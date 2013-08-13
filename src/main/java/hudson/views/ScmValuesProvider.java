package hudson.views;

import java.util.List;

import hudson.scm.SCM;
import hudson.views.PluginHelperUtils.PluginHelperTestable;

public interface ScmValuesProvider extends PluginHelperTestable {

	List<String> getValues(SCM scm);
	
	/**
	 * svn and cvs can be disabled, although they are part of the core.  For this reason,
	 * we want to be able to check for this condition.
	 */
	boolean checkLoaded();

}
