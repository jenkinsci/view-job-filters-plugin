package hudson.views;

import hudson.scm.CVSSCM;
import hudson.scm.CvsModule;
import hudson.scm.CvsRepository;
import hudson.scm.CvsRepositoryItem;
import hudson.scm.SCM;

import java.util.ArrayList;
import java.util.List;

public class CvsValuesProvider extends AbstractScmValuesProvider {

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
		CvsRepository[] repos = cvs.getRepositories();
		if (repos != null) {
			for (CvsRepository repo: repos) {
				values.add(repo.getCvsRoot());
				CvsRepositoryItem[] items = repo.getRepositoryItems();
				if (items != null) {
					for (CvsRepositoryItem item: items) {
						values.add(item.getLocation().getLocationName());
						CvsModule[] modules = item.getModules();
						if (modules != null) {
							for (CvsModule module: modules) {
								values.add(module.getCheckoutName());
								values.add(module.getLocalName());
								values.add(module.getProjectsetFileName());
								values.add(module.getRemoteName());
							}
						}
					}
				}
			}
		}
		return values;
	}

}
