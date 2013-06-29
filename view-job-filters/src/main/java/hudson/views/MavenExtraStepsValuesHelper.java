package hudson.views;

import hudson.maven.MavenModuleSet;
import hudson.model.TopLevelItem;
import hudson.plugins.m2extrasteps.M2ExtraStepsWrapper;
import hudson.tasks.BuildWrapper;
import hudson.views.PluginHelperUtils.PluginHelperTestable;

import java.util.ArrayList;
import java.util.List;

/**
 * Looks for maven extra steps plugin.
 * 
 * @author jacob.robertson
 */
public class MavenExtraStepsValuesHelper implements PluginHelperTestable {

	public List<String> getValues(TopLevelItem item) {
		List<String> values = new ArrayList<String>();
		if (item instanceof MavenModuleSet) {
			MavenModuleSet set = (MavenModuleSet) item;

			List<BuildWrapper> wrappers = set.getBuildWrappersList().toList();
			for (BuildWrapper wrapper : wrappers) {
				if (wrapper instanceof M2ExtraStepsWrapper) {
					M2ExtraStepsWrapper mwrap = (M2ExtraStepsWrapper) wrapper;
					MavenValuesHelper.addValues(values, mwrap
							.getPreBuildSteps());
					MavenValuesHelper.addValues(values, mwrap
							.getPostBuildSteps());
				}
			}
		}
		return values;
	}
	@SuppressWarnings("unchecked")
	public Class getPluginTesterClass() {
		return M2ExtraStepsWrapper.class;
	}
}
