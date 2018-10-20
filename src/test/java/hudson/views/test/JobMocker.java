package hudson.views.test;

import hudson.matrix.MatrixProject;
import hudson.maven.MavenModuleSet;
import hudson.maven.reporters.MavenMailer;
import hudson.model.*;
import hudson.plugins.emailext.ExtendedEmailPublisher;
import hudson.plugins.git.BranchSpec;
import hudson.plugins.git.GitSCM;
import hudson.plugins.m2extrasteps.M2ExtraStepsWrapper;
import hudson.scm.*;
import hudson.security.ACL;
import hudson.security.Permission;
import hudson.tasks.Builder;
import hudson.tasks.Mailer;
import hudson.tasks.Maven;
import hudson.triggers.Trigger;
import hudson.triggers.TriggerDescriptor;
import hudson.util.DescribableList;
import hudson.views.BuildTrendFilter;
import jenkins.model.Jenkins;
import jenkins.triggers.SCMTriggerItem;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.*;

import static hudson.views.test.JobType.*;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

public class JobMocker<T extends Job> {



    public enum MavenBuildStep {
        PRE, POST
    }

    T job;

    public JobMocker(Class<T> jobClass, Class... interfaces) {
        this.job = Mockito.mock(jobClass, withSettings().extraInterfaces(interfaces).defaultAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                if (DescribableList.class.isAssignableFrom(invocationOnMock.getMethod().getReturnType())) {
                    return new DescribableList(mock(Saveable.class), new ArrayList());
                }
                return null;
            }
        }));
    }

    public static <C extends Job> JobMocker<C> jobOfType(JobType<C> jobType) {
        return new JobMocker(jobType.getJobClass(), jobType.getInterfaces());
    }

    public static JobMocker<FreeStyleProject> freeStyleProject() {
        return jobOfType(FREE_STYLE_PROJECT);
    }

    public JobMocker<T> name(String name) {
        when(job.getName()).thenReturn(name);
        when(job.toString()).thenReturn(name);
        return this;
    }

    public JobMocker<T> fullName(String fullName) {
        when(job.getFullName()).thenReturn(fullName);
        return this;
    }

    public JobMocker<T> displayName(String fullName) {
        when(job.getDisplayName()).thenReturn(fullName);
        return this;
    }

    public JobMocker<T> parent(final ItemGroup parent) {
        when(job.getParent()).thenAnswer(new Answer<ItemGroup>() {
            @Override
            public ItemGroup answer(InvocationOnMock invocationOnMock) throws Throwable {
                return parent;
            }
        });
        return this;
    }

    public JobMocker<T> desc(String name) {
        when(job.getDescription()).thenReturn(name);
        return this;
    }

    public JobMocker<T> result(Result result) {
        Build lastBuild = mock(Build.class);
        when(lastBuild.getResult()).thenReturn(result);
        when(job.getLastCompletedBuild()).thenReturn(lastBuild);
        return this;
    }

    public JobMocker<T> disabled(boolean disabled) {
        if (job instanceof AbstractProject) {
            when(((AbstractProject) job).isDisabled()).thenReturn(disabled);
        }
        return this;
    }

    public JobMocker<T> building(boolean building) {
        when(job.isBuilding()).thenReturn(building);
        return this;
    }

    public JobMocker<T> inQueue(boolean inQueue) {
        when(job.isInQueue()).thenReturn(inQueue);
        return this;
    }

    public JobMocker<T> lastBuild(AbstractBuild build) {
        when(job.getLastBuild()).thenReturn(build);
        return this;
    }

    public JobMocker<T> lastBuilds(AbstractBuild... builds) {
        if (builds.length > 0) {
            lastBuild(builds[0]);
            for (int i = 0; i < builds.length - 1; i++) {
                when(builds[i].getPreviousBuild()).thenReturn(builds[i + 1]);
            }
        }
        return this;
    }

    public JobMocker<T> permissions(Permission... permissions) {
        ACL acl = mock(ACL.class);
        for (Permission permission: permissions) {
            when(acl.hasPermission(permission)).thenReturn(true);
        }
        when(job.getACL()).thenReturn(acl);
        return this;
    }

    public JobMocker<T> cvs(String root, String modules, String branch) {
        List<CvsRepository> cvsRepositories = LegacyConvertor.getInstance().convertLegacyConfigToRepositoryStructure(
                root, modules, branch,
                false, "excludedRegions",
                false, null);

        CVSSCM scm = new CVSSCM(cvsRepositories, false, false, true,  true, false, false, true);
        return scm(scm);
    }

    public JobMocker<T> svn(String... moduleLocations) {
        SubversionSCM.ModuleLocation[] locations = new SubversionSCM.ModuleLocation[moduleLocations.length];
        for (int i = 0; i < moduleLocations.length; i++) {
            locations[i] = mock(SubversionSCM.ModuleLocation.class);
            when(locations[i].getURL()).thenReturn(moduleLocations[i]);
        }

        SubversionSCM scm = mock(SubversionSCM.class);
        when(scm.getLocations()).thenReturn(locations);
        when(scm.getDescriptor()).thenReturn(new SubversionSCM.DescriptorImpl());
        return scm(scm);
    }

    public JobMocker<T> gitBranches(String... branches) {
        List<BranchSpec> branchSpecs = new ArrayList<BranchSpec>();
        for (String branch: branches) {
            BranchSpec branchSpec = mock(BranchSpec.class);
            when(branchSpec.getName()).thenReturn(branch);
            branchSpecs.add(branchSpec);
        }

        GitSCM scm = mock(GitSCM.class);
        when(scm.getBranches()).thenReturn(branchSpecs);
        when(scm.getDescriptor()).thenReturn((SCMDescriptor)new GitSCM.DescriptorImpl());
        return scm(scm);
    }

    public JobMocker<T> gitRepos(String... repos) {
        List<RemoteConfig> remotes = new ArrayList<RemoteConfig>();
        for (String repo: repos) {
            URIish uri = mock(URIish.class);
            when(uri.toPrivateString()).thenReturn(repo);

            RemoteConfig remote = mock(RemoteConfig.class);
            when(remote.getURIs()).thenReturn(Arrays.asList(uri));
            remotes.add(remote);
        }

        GitSCM scm = mock(GitSCM.class);
        when(scm.getRepositories()).thenReturn(remotes);
        when(scm.getDescriptor()).thenReturn((SCMDescriptor)new GitSCM.DescriptorImpl());
        return scm(scm);
    }

    public JobMocker<T> gitReposLegacy(String... repos) {
        List<org.spearce.jgit.transport.RemoteConfig> remotes = new ArrayList<org.spearce.jgit.transport.RemoteConfig>();
        for (String repo: repos) {
            org.spearce.jgit.transport.URIish uri = mock(org.spearce.jgit.transport.URIish.class);
            when(uri.toPrivateString()).thenReturn(repo);

            org.spearce.jgit.transport.RemoteConfig remote = mock(org.spearce.jgit.transport.RemoteConfig.class);
            when(remote.getURIs()).thenReturn(Arrays.asList(uri));
            remotes.add(remote);
        }

        GitSCM scm = mock(GitSCM.class);
        when(scm.getRepositories()).thenReturn((List)remotes);
        return scm(scm);
    }

    public JobMocker scm(SCM scm) {
        if (job instanceof AbstractProject) {
            when(((AbstractProject) job).getScm()).thenReturn(scm);
        }
        if (job instanceof SCMTriggerItem) {
            when(((SCMTriggerItem)job).getSCMs()).thenReturn((Collection)asList(scm));
        }
        if (instanceOf(job, SCMED_ITEM)) {
            when(((SCMedItem)job).getScm()).thenReturn(scm);
        }
        return this;
    }

    public enum EmailType {
        DEFAULT, EXTENDED
    }

    public JobMocker<T> email(String email, EmailType emailType) {
        if (emailType == EmailType.DEFAULT) {
            return email(email);
        }
        if (emailType == EmailType.EXTENDED) {
            return extEmail(email);
        }
        return this;
    }

    private JobMocker<T> email(String email) {
        if (job instanceof AbstractProject) {
            Mailer mailer = new Mailer(email, false, false);

            DescribableList publishers = new DescribableList(mock(Saveable.class), asList(mailer));
            when(((AbstractProject)job).getPublishersList()).thenReturn(publishers);
        }
        if (instanceOf(job, MAVEN_MODULE_SET)) {
            MavenMailer mavenMailer = new MavenMailer();
            mavenMailer.recipients = email;

            DescribableList reporters = new DescribableList(mock(Saveable.class), asList(mavenMailer));
            when(((MavenModuleSet)job).getReporters()).thenReturn(reporters);
        }
        return this;
    }

    private JobMocker<T> extEmail(String email) {
        ExtendedEmailPublisher emailPublisher = new ExtendedEmailPublisher();
        emailPublisher.recipientList = email;

        DescribableList publishers = new DescribableList(mock(Saveable.class), asList(emailPublisher));

        if (job instanceof AbstractProject) {
            when(((AbstractProject)job).getPublishersList()).thenReturn(publishers);
        }
        if (instanceOf(job, MAVEN_MODULE_SET)) {
            DescribableList reporters = new DescribableList(mock(Saveable.class), new ArrayList());
            when(((MavenModuleSet)job).getReporters()).thenReturn(reporters);
        }
        return this;
    }

    public JobMocker<T> trigger(String spec) {
        Trigger trigger = mock(Trigger.class);
        when(trigger.getSpec()).thenReturn(spec);

        Map<TriggerDescriptor, Trigger<?>> triggers = new HashMap<TriggerDescriptor, Trigger<?>>();
        triggers.put(mock(TriggerDescriptor.class), trigger);

        if (job instanceof AbstractProject) {
            when(((AbstractProject)job).getTriggers()).thenReturn(triggers);
        }
        return this;
    }

    public JobMocker<T> mavenBuilder(String targets, final String name, String properties, String opts) {
        Maven maven = mockMaven(targets, name, properties, opts);
        if (job instanceof Project) {
            when(((Project)job).getBuilders()).thenReturn(asList(maven));
        }
        if (instanceOf(job, MATRIX_PROJECT)) {
            when(((MatrixProject)job).getBuilders()).thenReturn(asList((Builder)maven));
        }
        if (instanceOf(job, MAVEN_MODULE_SET)) {
            MavenModuleSet set = (MavenModuleSet)job;
            when(set.getMaven()).thenReturn(maven.getMaven());
            when(set.getMavenOpts()).thenReturn(opts);
            when(set.getAlternateSettings()).thenReturn(properties);
            when(set.getGoals()).thenReturn(targets);
        }
        return this;
    }

    public JobMocker<T> mavenBuildStep(MavenBuildStep step, String targets, final String name, String properties, String opts) {
        if (instanceOf(job, MAVEN_MODULE_SET)) {
            Maven maven = mockMaven(targets, name, properties, opts);
            M2ExtraStepsWrapper wrapper = new M2ExtraStepsWrapper(null);
            wrapper.setPreBuildSteps((step == MavenBuildStep.PRE) ? asList((Builder) maven) : new ArrayList<Builder>());
            wrapper.setPostBuildSteps((step == MavenBuildStep.POST) ? asList((Builder) maven) : new ArrayList<Builder>());
            DescribableList wrappers = new DescribableList(mock(Saveable.class), asList(wrapper));
            when(((MavenModuleSet)job).getBuildWrappers()).thenReturn(wrappers);
        }
        return this;
    }

    public JobMocker<T> mavenPostBuildStep(String targets, final String name, String properties, String opts) {
        if (instanceOf(job, MAVEN_MODULE_SET)) {
            Maven maven = mockMaven(targets, name, properties, opts);
            M2ExtraStepsWrapper wrapper = new M2ExtraStepsWrapper(null);
            wrapper.setPostBuildSteps(asList((Builder)maven));
            DescribableList wrappers = new DescribableList(mock(Saveable.class), asList(wrapper));
            when(((MavenModuleSet)job).getBuildWrappers()).thenReturn(wrappers);
        }
        return this;
    }

    private Maven mockMaven(final String targets, final String name, final String properties, final String opts) {
        final Maven.MavenInstallation mavenInstallation = mock(Maven.MavenInstallation.class);
        when(mavenInstallation.getName()).thenReturn(name);

        return new Maven(targets, name, "", properties, opts) {
            @Override
            public MavenInstallation getMaven() {
                return mavenInstallation;
            }
        };
    }

    public JobMocker<T> assignedLabel(String label) {
        if (job instanceof AbstractProject) {
            when(((AbstractProject)job).getAssignedLabelString()).thenReturn(label);
        }
        return this;
    }

    public JobMocker<T> property(Class<?> clazz, JobProperty property) {
        when(job.getProperty(clazz)).thenReturn(property);
        return this;
    }

    public JobMocker<T> parameters(ParameterDefinition... definitions) {
        ParametersDefinitionProperty prop = new ParametersDefinitionProperty(definitions);
        when(job.getProperty(ParametersDefinitionProperty.class)).thenReturn(prop);
        return this;
    }

    public JobMocker<T> upstream(AbstractProject... upstreamProjects) {
        if (job instanceof AbstractProject) {
            when(((AbstractProject)job).getUpstreamProjects()).thenReturn(asList(upstreamProjects));
        }
        return this;
    }

    public JobMocker<T> downstream(AbstractProject... downstreamProjects) {
        if (job instanceof AbstractProject) {
            when(((AbstractProject)job).getDownstreamProjects()).thenReturn(asList(downstreamProjects));
        }
        return this;
    }

    public T asJob() {
        return job;
    }

    public TopLevelItem asItem() {
        return (TopLevelItem)job;
    }
}
