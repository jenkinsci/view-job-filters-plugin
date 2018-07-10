package hudson.views;

import hudson.model.*;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.kohsuke.stapler.RequestImpl;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponseWrapper;


import java.util.ArrayList;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.*;

public class JobStatusFilterTest extends AbstractHudsonTest {


    @Test
    public void matchesDisabled() throws Exception {
        JobStatusFilter filter = new JobStatusFilter(false, false,false, true, false ,"includeMatched");
        jobs.get(0).setDisabled(true);
        assertEquals(true, filter.matches(jobs.get(0)));
        JobStatusFilter filter1 = new JobStatusFilter(false, false, false,true,false, "includeMatched");
        jobs.get(1).setDisabled(false);
        assertEquals(false, filter.matches(jobs.get(1)));
    }

    @Test
    public void matchesFailureFailed() throws Exception{
        JobStatusFilter filter = new JobStatusFilter(false, true, false, false, false,"includeMatched");
        jobs.get(2).scheduleBuild2(0).get();
        Run last = jobs.get(2).getLastBuild();
        Result result = last.getResult();
        assertEquals(true, filter.matches(jobs.get(2)));
        j.assertBuildStatus(Result.FAILURE, jobs.get(2).scheduleBuild2(0));
    }


    @Test
    public void matchesSuccessStable() throws Exception {
        JobStatusFilter filter = new JobStatusFilter(false, false, false, false, true, "includeMatched");
        jobs.get(3).setDefinition(new CpsFlowDefinition(""));
        assertEquals(true, jobs.get(3).isBuildable());
        j.assertBuildStatus(Result.SUCCESS, jobs.get(3).scheduleBuild2(0));
        assertEquals(true, filter.matches(jobs.get(3)));
    }

    @Test
    public void matchesAborted() throws Exception{
        JobStatusFilter filter = new JobStatusFilter(false, false , true, false, false, "includeMatched");
        jobs.get(4).setDefinition(new CpsFlowDefinition("currentBuild.result = 'ABORTED'"));
        j.assertBuildStatus(Result.ABORTED, jobs.get(4).scheduleBuild2(0));
        assertEquals(true, filter.matches(jobs.get(4)));
    }


    @Test
    public void isUnstable() {
        JobStatusFilter filter = new JobStatusFilter(true, false,false, false, false ,"includeMatched");
        assertEquals(true, filter.isUnstable());

        JobStatusFilter filter2 = new JobStatusFilter(false, false,false, false, false ,"includeMatched");
        assertEquals(false, filter2.isUnstable());
    }

    @Test
    public void isFailed() {
        JobStatusFilter filter = new JobStatusFilter(false, false,false, false, false ,"includeMatched");
        assertEquals(false, filter.isFailed());

        JobStatusFilter filter2 = new JobStatusFilter(false, true,false, false, false ,"includeMatched");
        assertEquals(true, filter2.isFailed());
    }

    @Test
    public void isAborted() {
        JobStatusFilter filter = new JobStatusFilter( false, false,false, false, false ,"includeMatched");
        assertEquals(false, filter.isAborted());

        JobStatusFilter filter2 = new JobStatusFilter(false, false,true, false, false ,"includeMatched");
        assertEquals(true, filter2.isAborted());
    }

    @Test
    public void isDisabled() {
        JobStatusFilter filter = new JobStatusFilter(false, false,false, false, false ,"includeMatched");
        assertEquals(false, filter.isDisabled());

        JobStatusFilter filter2 = new JobStatusFilter(false, false,false, true, false ,"includeMatched");
        assertEquals(true, filter2.isDisabled());
    }

    @Test
    public void isStable() {
        JobStatusFilter filter = new JobStatusFilter(false, false,false, false, false ,"includeMatched");
        assertEquals(false, filter.isStable());

        JobStatusFilter filter2 = new JobStatusFilter(false, false,false, false, true ,"includeMatched");
        assertEquals(true, filter2.isStable());
    }


}