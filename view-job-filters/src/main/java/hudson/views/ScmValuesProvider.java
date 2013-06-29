package hudson.views;

import java.util.List;

import hudson.scm.SCM;
import hudson.views.PluginHelperUtils.PluginHelperTestable;

public interface ScmValuesProvider extends PluginHelperTestable {

	List<String> getValues(SCM scm);

}
