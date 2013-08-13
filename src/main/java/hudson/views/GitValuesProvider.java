package hudson.views;

import java.util.List;

import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;

public class GitValuesProvider extends AbstractGitValuesProvider {

	@Override
	public boolean checkLoaded() {
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public Class getPluginTesterClass() {
		return URIish.class;
	}
	
	public void addRepositoryValues(Object repoObject, List<String> values) {
		RemoteConfig repo = (RemoteConfig) repoObject;
		List<URIish> uris = repo.getURIs();
		if (uris != null) {
			for (URIish uri: uris) {
				values.add(uri.toPrivateString());
			}
		}
		values.add(repo.getName());
	}

}
