package hudson.views;

import hudson.plugins.git.BranchSpec;
import hudson.plugins.git.GitSCM;
import hudson.scm.SCM;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGitValuesProvider implements ScmValuesProvider {

	@SuppressWarnings("unchecked")
	public List<String> getValues(SCM scm) {
		if (!(scm instanceof GitSCM)) {
			return null;
		}
		GitSCM git = (GitSCM) scm;
		List<String> values = new ArrayList<String>();
		
		List repos = git.getRepositories();
		if (repos != null) {
			for (Object repo: repos) {
				addRepositoryValues(repo, values);
			}
		}
		
		List<BranchSpec> branches = git.getBranches();
		if (branches != null) {
			for (BranchSpec branch: branches) {
				values.add(branch.getName());
			}
		}
		
		return values;
	}

	abstract public void addRepositoryValues(Object repo, List<String> values);

}
