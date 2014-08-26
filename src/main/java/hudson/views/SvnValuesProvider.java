package hudson.views;

import hudson.scm.SCM;
import hudson.scm.SubversionSCM;
import hudson.scm.SubversionSCM.ModuleLocation;

import java.util.ArrayList;
import java.util.List;

public class SvnValuesProvider extends AbstractScmValuesProvider {

	@SuppressWarnings("unchecked")
	public Class getPluginTesterClass() {
		return ModuleLocation.class;
	}
	
	public List<String> getValues(SCM scm) {
		if (!(scm instanceof SubversionSCM)) {
			return null;
		}
		SubversionSCM svn = (SubversionSCM) scm;
		List<String> values = new ArrayList<String>();
		ModuleLocation[] locations = svn.getLocations();
		if (locations != null) {
			for (ModuleLocation location: locations) {
				String value = location.getURL();
				values.add(value);
			}
		}
		return values;
	}

}
