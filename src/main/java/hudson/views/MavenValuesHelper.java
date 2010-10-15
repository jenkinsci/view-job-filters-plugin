package hudson.views;

import hudson.model.Project;
import hudson.model.TopLevelItem;
import hudson.tasks.Builder;
import hudson.tasks.Maven;

import java.util.ArrayList;
import java.util.List;

public class MavenValuesHelper {

	private static MavenProjectValuesHelper HELPER2 = buildMavenProjectValuesHelper();
	
	@SuppressWarnings("unchecked")
	public static List<String> getValues(TopLevelItem item) {
		List<String> values = new ArrayList<String>();
		if (item instanceof Project) {
			Project project = (Project) item;
			List<Builder> builders = project.getBuilders();
			for (Builder builder: builders) {
				if (builder instanceof Maven) {
					Maven maven = (Maven) builder;
					values.add(getTargets(maven));
					values.add(maven.getMaven().getName());
					values.add(maven.jvmOptions);
					values.add(maven.properties);
				}
			}
		}
		if (HELPER2 != null) {
			List<String> more = HELPER2.getValues(item);
			values.addAll(more);
		}
		return values;
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
			return new MavenProjectValuesHelper();
		} catch (Throwable t) {
			// necessary maven plugins not installed
			return null;
		}
	}

}
