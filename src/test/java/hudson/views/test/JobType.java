package hudson.views.test;

import hudson.matrix.MatrixProject;
import hudson.maven.MavenModuleSet;
import hudson.model.FreeStyleProject;
import hudson.model.Job;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;

import java.util.ArrayList;
import java.util.List;

public class JobType<T extends Job> {
    public static final JobType<FreeStyleProject> FREE_STYLE_PROJECT = new JobType("hudson.model.FreeStyleProject");
    public static final JobType<MatrixProject> MATRIX_PROJECT = new JobType("hudson.matrix.MatrixProject");
    public static final JobType<MavenModuleSet> MAVEN_MODULE_SET = new JobType("hudson.maven.MavenModuleSet");
    public static final JobType<Job> TOP_LEVEL_ITEM = new JobType("hudson.model.Job", "hudson.model.TopLevelItem" );
    public static final JobType<Job> SCM_TRIGGER_ITEM = new JobType("hudson.model.Job", "hudson.model.TopLevelItem", "jenkins.triggers.SCMTriggerItem");
    public static final JobType<Job> SCMED_ITEM = new JobType("hudson.model.Job", "hudson.model.TopLevelItem", "hudson.model.SCMedItem");
    public static final JobType<WorkflowJob> WORKFLOW_JOB = new JobType("org.jenkinsci.plugins.workflow.job.WorkflowJob");

    private Class<T> jobClass;
    private Class<T>[] interfaces;
    private boolean available;

    private JobType(String className, String... interfaceNames) {
        try {
            jobClass = (Class<T>) Class.forName(className);
            interfaces = new Class[interfaceNames.length];
            for (int i = 0; i < interfaceNames.length; i++) {
                interfaces[i] = (Class<T>) Class.forName(interfaceNames[i]);
            }
            available = true;
        } catch (Throwable t) {
            jobClass = null;
            interfaces = null;
            available = false;
        }
    }

    public boolean isAvailable() {
        return available;
    }

    public Class<T> getJobClass() {
        return jobClass;
    }

    public Class[] getInterfaces() {
        return interfaces;
    }

    public boolean isAssignableFrom(Job job) {
        if (!isAvailable()) {
            return false;
        }
        if (!jobClass.isAssignableFrom(job.getClass())) {
            return false;
        }
        for (Class<T> iface: interfaces) {
            if (!iface.isAssignableFrom(job.getClass())) {
                return false;
            }
        }
        return true;
    }

    public static boolean instanceOf(Job job, JobType<?> jobType) {
        return jobType.isAssignableFrom(job);
    }

    public static List<JobType<?>> availableJobTypes(JobType<?>... types) {
        List<JobType<?>> availableJobTypes = new ArrayList<>();
        for (JobType<?> type: types) {
            if (type.isAvailable()) {
                availableJobTypes.add(type);
            }
        }
        return availableJobTypes;
    }
}
