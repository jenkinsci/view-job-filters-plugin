package hudson.views;

import hudson.model.AbstractProject;
import hudson.model.TopLevelItem;
import hudson.triggers.Trigger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static hudson.views.WorkflowJobValuesHelper.WORKFLOW_JOB_HELPER;

public class TriggerFilterHelper {

	@SuppressWarnings("unchecked")
	public static List<String> getValues(TopLevelItem item) {
		List<String> values = new ArrayList<String>();
		Map triggers = null;
		if (item instanceof AbstractProject) {
			triggers = ((AbstractProject) item).getTriggers();

		}
		if (triggers == null && WORKFLOW_JOB_HELPER != null) {
			triggers = WORKFLOW_JOB_HELPER.getTriggers(item);
		}

		if (triggers != null) {
			Collection list = triggers.values();
			for (Object obj: list) {
				Trigger trigger = (Trigger) obj;
				values.add(trigger.getSpec());
			}
		}

		return values;
	}
}
