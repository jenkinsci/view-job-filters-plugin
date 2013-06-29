package hudson.views;

import java.util.List;

import org.spearce.jgit.transport.URIish;

public class GitLegacyValuesProvider extends GitValuesProvider {

	@SuppressWarnings("unchecked")
	public Class getPluginTesterClass() {
		return URIish.class;
	}
	
	public void addRepositoryValues(Object repoObject, List<String> values) {
		// it's possible that the old api is still in the classloader,
		// so let's check the runtime instance as well
		if (repoObject instanceof org.eclipse.jgit.transport.RemoteConfig) {
			super.addRepositoryValues(repoObject, values);
		} else {
			// use legacy api
			org.spearce.jgit.transport.RemoteConfig repo = (org.spearce.jgit.transport.RemoteConfig) repoObject;
			List<URIish> uris = repo.getURIs();
			if (uris != null) {
				for (URIish uri: uris) {
					values.add(uri.toPrivateString());
				}
			}
			values.add(repo.getName());
		}
	}

}
