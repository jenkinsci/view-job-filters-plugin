package hudson.views;

import hudson.model.AbstractProject;
import hudson.model.TopLevelItem;
import hudson.triggers.Trigger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TriggerFilterHelper {

	@SuppressWarnings("unchecked")
	public static List<String> getValues(TopLevelItem item) {
		List<String> values = new ArrayList<String>();
		if (item instanceof AbstractProject) {
			AbstractProject project = (AbstractProject) item;
			Map triggers = project.getTriggers();
			if (triggers != null) {
				Collection list = triggers.values();
				for (Object obj: list) {
					Trigger trigger = (Trigger) obj;
					values.add(trigger.getSpec());
				}
			}
		}
		return values;
	}
}
