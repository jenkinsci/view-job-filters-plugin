package hudson.views;

import java.util.List;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;

public class GitLegacyValuesProvider extends GitValuesProvider {

	@SuppressWarnings("unchecked")
	public Class getPluginTesterClass() {
		return URIish.class;
	}
	
	public void addRepositoryValues(Object repoObject, List<String> values) {
		// it's possible that the old api is still in the classloader,
		// so let's check the runtime instance as well
		if (repoObject instanceof RemoteConfig) {
			super.addRepositoryValues(repoObject, values);
		} else {
			// use legacy api
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

}
