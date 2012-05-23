package hudson.views;

import java.util.List;
import java.util.logging.Logger;

import hudson.maven.MavenModuleSet;
import hudson.model.AbstractProject;
import hudson.model.TopLevelItem;
import hudson.views.PluginHelperUtils.PluginHelperTestable;

public class UpstreamDownstreamMavenHelper implements PluginHelperTestable {

	private static final transient Logger log = Logger.getLogger("updownmaven");

	@SuppressWarnings("unchecked")
	public boolean isFirstUpstreamFromSecond(TopLevelItem first, TopLevelItem second) {
		log.warning("buildMavenHelper.isFirstUpstreamFromSecond." + first.getName() + "." + second.getName());
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
