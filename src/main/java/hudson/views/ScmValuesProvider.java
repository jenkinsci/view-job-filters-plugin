package hudson.views;

import java.util.List;

import hudson.scm.SCM;

public interface ScmValuesProvider {

	List<String> getValues(SCM scm);
	
}
