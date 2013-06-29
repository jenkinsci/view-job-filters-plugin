package hudson.views;

import hudson.scm.CVSSCM;
import hudson.scm.SCM;

import java.util.ArrayList;
import java.util.List;

public class CvsValuesProvider implements ScmValuesProvider {

	@SuppressWarnings("unchecked")
	public Class getPluginTesterClass() {
		return CVSSCM.class;
	}
	
	public List<String> getValues(SCM scm) {
		if (!(scm instanceof CVSSCM)) {
			return null;
		}
		CVSSCM cvs = (CVSSCM) scm;
		List<String> values = new ArrayList<String>();
		values.add(cvs.getCvsRoot());
		values.add(cvs.getAllModules());
		values.add(cvs.getBranch());
		return values;
	}

}
