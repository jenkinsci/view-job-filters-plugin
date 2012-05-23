package hudson.views;

import hudson.maven.MavenModuleSet;
import hudson.model.AbstractProject;
import hudson.model.TopLevelItem;
import hudson.views.PluginHelperUtils.PluginHelperTestable;

import java.util.List;

public class UpstreamDownstreamMavenHelper implements PluginHelperTestable {

	@SuppressWarnings("unchecked")
	public boolean isFirstUpstreamFromSecond(TopLevelItem first, TopLevelItem second) {
		if (first instanceof MavenModuleSet && second instanceof MavenModuleSet) {
			MavenModuleSet m2 = (MavenModuleSet) second;
			List<AbstractProject> upstream = m2.getUpstreamProjects();
			return upstream.contains(first);
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Class getPluginTesterClass() {
		return MavenModuleSet.class;
	}
	
}
