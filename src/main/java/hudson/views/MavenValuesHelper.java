package hudson.views;

import hudson.model.Project;
import hudson.model.TopLevelItem;
import hudson.tasks.Builder;
import hudson.tasks.Maven;
import hudson.tasks.Maven.MavenInstallation;

import java.util.ArrayList;
import java.util.List;

public class MavenValuesHelper {

	/*
	 * If I add any more helpers, switch to a better design first.
	 */
	public static final MavenProjectValuesHelper MODULESET_HELPER = buildMavenProjectValuesHelper();
	public static final MatrixProjectBuildersHelper MATRIX_PROJECT_HELPER = buildMatrixProjectBuildersHelper();

	@SuppressWarnings({ "unchecked" })
	public static List<String> getValues(TopLevelItem item) {
		List<String> values = new ArrayList<String>();
		if (item instanceof Project) {
			Project project = (Project) item;
			List<Builder> builders = project.getBuilders();
			addValues(values, builders);
		} else if (MATRIX_PROJECT_HELPER != null) {
			List<Builder> builders = MATRIX_PROJECT_HELPER.getBuilders(item);
			addValues(values, builders);
		}
		if (MODULESET_HELPER != null) {
			List<String> more = MODULESET_HELPER.getValues(item);
			values.addAll(more);
		}
		return values;
	}

	public static void addValues(List<String> values, List<Builder> builders) {
		if (builders != null) {
			for (Builder builder : builders) {
				if (builder instanceof Maven) {
					Maven maven = (Maven) builder;
					values.add(getTargets(maven));
					values.add(maven.jvmOptions);
					values.add(maven.properties);

					MavenInstallation install = maven.getMaven();
					if (install != null) {
						values.add(install.getName());
					}
				}
			}
		}
	}

	private static String getTargets(Maven maven) {
		String t = maven.getTargets();
		t = normalize(t);
		return t;
	}

	static String normalize(String s) {
		if (s != null) {
			// required to match regex
			s = s.replace('\n', ' ');
		}
		return s;
	}

	private static MavenProjectValuesHelper buildMavenProjectValuesHelper() {
		try {
			return PluginHelperUtils.validateAndThrow(new MavenProjectValuesHelper());
		} catch (Throwable t) {
			// necessary maven plugins not installed
			return null;
		}
	}

	private static MatrixProjectBuildersHelper buildMatrixProjectBuildersHelper() {
		try {
			return PluginHelperUtils.validateAndThrow(new MatrixProjectBuildersHelper());
		} catch (Throwable t) {
			// necessary maven plugins not installed
			return null;
		}
	}
}
