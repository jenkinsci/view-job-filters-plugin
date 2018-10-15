package hudson.views;

import hudson.matrix.MatrixProject;
import hudson.maven.MavenModuleSet;
import hudson.model.TopLevelItem;
import hudson.tasks.Builder;

import java.util.ArrayList;
import java.util.List;

/**
 * Looks for matrix-project plugin.
 * 
 * @author Sven Schoenung
 */
public class MatrixProjectBuildersHelper implements PluginHelperUtils.PluginHelperTestable {

	public List<Builder> getBuilders(TopLevelItem item) {
		if (item instanceof MatrixProject) {
			return ((MatrixProject)item).getBuilders();
		}
		return new ArrayList<Builder>(0);
	}

	public Class getPluginTesterClass() {
		return MatrixProject.class;
	}
}
