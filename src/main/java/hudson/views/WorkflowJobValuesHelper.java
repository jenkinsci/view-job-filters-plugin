package hudson.views;

import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;

import java.util.Map;

public class WorkflowJobValuesHelper implements PluginHelperUtils.PluginHelperTestable {

    public static final WorkflowJobValuesHelper WORKFLOW_JOB_HELPER = buildWorkflowJobValuesHelper();

    public Map getTriggers(TopLevelItem item) {
        if (item instanceof WorkflowJob) {
            return ((WorkflowJob)item).getTriggers(); // TODO rather use ParameterizedJob
        }
        return null;
    }

    public Class getPluginTesterClass() {
        return WorkflowJob.class;
    }

    private static WorkflowJobValuesHelper buildWorkflowJobValuesHelper() {
        try {
            return PluginHelperUtils.validateAndThrow(new WorkflowJobValuesHelper());
        } catch (Throwable t) {
            return null;
        }
    }
}
