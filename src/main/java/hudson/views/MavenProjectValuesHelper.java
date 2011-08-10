package hudson.views;

import hudson.maven.MavenModuleSet;
import hudson.model.TopLevelItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Looks for maven project plugin.
 * 
 * @author jacob.robertson
 */
public class MavenProjectValuesHelper implements PluginHelperUtils.PluginHelperTestable {

	public List<String> getValues(TopLevelItem item) {
		List<String> values = new ArrayList<String>();
		if (item instanceof MavenModuleSet) {
			MavenModuleSet set = (MavenModuleSet) item;
			values.add(set.getMavenOpts());
			if (set.getMaven() != null) {
				values.add(set.getMaven().getName());
			}
			values.add(set.getAlternateSettings());

			String goals = set.getGoals();
			goals = MavenValuesHelper.normalize(goals);
			values.add(goals);
		}
		return values;
	}
	@SuppressWarnings("unchecked")
	public Class getPluginTesterClass() {
		return MavenModuleSet.class;
	}
}
