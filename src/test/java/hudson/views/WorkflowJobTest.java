package hudson.views;

import hudson.model.ItemGroup;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class WorkflowJobTest {
    JobStatusFilter filter = new JobStatusFilter(false, false,false, true, false ,"includeMatched");

    @Test
    public void testThatDisabledWorkflowJobMatchesFilterWithDisabledJobs() {
        WorkflowJob workflowJob = createWorkdflowJob();
        workflowJob.setDisabled(true);
        assertTrue(filter.matches(workflowJob));
    }

    @Test
    public void testThatEnabledWorkflowJobDoesNotMatchFilterWithDisabledJobs() {
        WorkflowJob workflowJob = createWorkdflowJob();
        workflowJob.setDisabled(false);
        assertFalse(filter.matches(workflowJob));
    }

    WorkflowJob createWorkdflowJob() {
        ItemGroup itemGroup = mock(ItemGroup.class);
        return new WorkflowJob(itemGroup, "workflowjob");
    }
}
