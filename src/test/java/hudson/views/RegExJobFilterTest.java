package hudson.views;

import hudson.maven.MavenModuleSet;
import hudson.maven.reporters.MavenMailer;
import hudson.model.*;
import hudson.plugins.emailext.ExtendedEmailPublisher;
import hudson.plugins.git.Branch;
import hudson.plugins.git.BranchSpec;
import hudson.plugins.git.GitSCM;
import hudson.scm.CVSSCM;
import hudson.scm.CvsRepository;
import hudson.scm.LegacyConvertor;
import hudson.scm.SubversionSCM;
import hudson.tasks.Mailer;
import hudson.triggers.TimerTrigger;
import hudson.triggers.Trigger;
import hudson.triggers.TriggerDescriptor;
import hudson.util.DescribableList;
import hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType;

import java.io.IOException;
import java.util.*;

import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

public class RegExJobFilterTest extends AbstractHudsonTest {

	@Test
	public void testName() {
		assertThat(nameRegex(".*").matches(freeStyleProjectWithName(null)), is(false));
		assertThat(nameRegex(".*").matches(freeStyleProjectWithName("")), is(true));
		assertThat(nameRegex("Foo").matches(freeStyleProjectWithName("Foo")), is(true));
		assertThat(nameRegex("Foo").matches(freeStyleProjectWithName("Foobar")), is(false));
		assertThat(nameRegex("Foo.*").matches(freeStyleProjectWithName("Foobar")), is(true));
		assertThat(nameRegex("bar").matches(freeStyleProjectWithName("Foobar")), is(false));
		assertThat(nameRegex(".*bar").matches(freeStyleProjectWithName("Foobar")), is(true));
		assertThat(nameRegex(".ooba.").matches(freeStyleProjectWithName("Foobar")), is(true));
	}

	@Test
	public void testDescription() {
		assertThat(descRegex(".*").matches(freeStyleProjectWithDesc(null)), is(false));
		assertThat(descRegex(".*").matches(freeStyleProjectWithDesc("")), is(true));
		assertThat(descRegex("Foo").matches(freeStyleProjectWithDesc("Foo")), is(true));
		assertThat(descRegex("Foo").matches(freeStyleProjectWithDesc("Foobar")), is(false));
		assertThat(descRegex("Foo.*").matches(freeStyleProjectWithDesc("Foobar")), is(true));
		assertThat(descRegex("bar").matches(freeStyleProjectWithDesc("Foobar")), is(false));
		assertThat(descRegex(".*bar").matches(freeStyleProjectWithDesc("Foobar")), is(true));
		assertThat(descRegex(".ooba.").matches(freeStyleProjectWithDesc("Foobar")), is(true));

		assertThat(descRegex(".*").matches(freeStyleProjectWithDesc("\n")), is(true));
		assertThat(descRegex("Foo").matches(freeStyleProjectWithDesc("Quux\nFoo")), is(true));
		assertThat(descRegex("Foo").matches(freeStyleProjectWithDesc("Quux\nFoobar")), is(false));
		assertThat(descRegex("Foo.*").matches(freeStyleProjectWithDesc("Quux\nFoobar")), is(true));
		assertThat(descRegex("bar").matches(freeStyleProjectWithDesc("Quux\nFoobar")), is(false));
		assertThat(descRegex(".*bar").matches(freeStyleProjectWithDesc("Quux\nFoobar")), is(true));
		assertThat(descRegex(".ooba.").matches(freeStyleProjectWithDesc("Quux\nFoobar")), is(true));

		assertThat(descRegex(".*desc=test.*").matches(freeStyleProjectWithDesc("")), is(false));
		assertThat(descRegex(".*desc=test.*").matches(freeStyleProjectWithDesc(null)), is(false));
		assertThat(descRegex(".*desc=test.*").matches(freeStyleProjectWithDesc("nothing")), is(false));
		assertThat(descRegex(".*desc=test.*").matches(freeStyleProjectWithDesc("desc=test")), is(true));
		assertThat(descRegex(".*desc=test.*").matches(freeStyleProjectWithDesc("mydesc=test2")), is(true));
		assertThat(descRegex(".*desc=test.*").matches(freeStyleProjectWithDesc("thisis\nmydesc=testn2\nforyou")), is(true));
		assertThat(descRegex(".*desc=test.*").matches(freeStyleProjectWithDesc("1&#xd;\ndesc=test&#xd;\n2")), is(true));
		assertThat(descRegex(".*desc=test.*").matches(freeStyleProjectWithDesc("1 desc=test 2")), is(true));
	}

	@Test
	public void testSCM() {
		assertThat(scmRegex(".*my-office.*").matches(freeStyleProjectWithCVS("root", "modules", "branch")), is(false));
		assertThat(scmRegex(".*my-office.*").matches(freeStyleProjectWithCVS(null, "modules", "branch")), is(false));
		assertThat(scmRegex(".*my-office.*").matches(freeStyleProjectWithCVS("root", "modules", null)), is(false));
		assertThat(scmRegex(".*my-office.*").matches(freeStyleProjectWithCVS("root/my-office", "modules", "branch")), is(true));
		assertThat(scmRegex(".*my-office.*").matches(freeStyleProjectWithCVS("root", "modules/my-office", "branch")), is(true));
		assertThat(scmRegex(".*my-office.*").matches(freeStyleProjectWithCVS("root", "modules", "branch/my-office")), is(true));

		assertThat(scmRegex(".*").matches(freeStyleProjectWithSVN()), is(false));
		assertThat(scmRegex(".*").matches(freeStyleProjectWithSVN("")), is(true));
		assertThat(scmRegex("Foo").matches(freeStyleProjectWithSVN("Foo")), is(true));
		assertThat(scmRegex("Foo.*").matches(freeStyleProjectWithSVN("Foobar")), is(true));
		assertThat(scmRegex("bar").matches(freeStyleProjectWithSVN("Foobar")), is(false));
		assertThat(scmRegex(".*bar").matches(freeStyleProjectWithSVN("Foobar")), is(true));
		assertThat(scmRegex("Bar").matches(freeStyleProjectWithSVN("Foo", "Bar")), is(true));
		assertThat(scmRegex("B.*").matches(freeStyleProjectWithSVN("Foo", "Bar")), is(true));

		assertThat(scmRegex(".*").matches(freeStyleProjectWithGitBranches()), is(false));
		assertThat(scmRegex(".*").matches(freeStyleProjectWithGitBranches("")), is(true));
		assertThat(scmRegex("Foo").matches(freeStyleProjectWithGitBranches("Foo")), is(true));
		assertThat(scmRegex("Foo.*").matches(freeStyleProjectWithGitBranches("Foobar")), is(true));
		assertThat(scmRegex("bar").matches(freeStyleProjectWithGitBranches("Foobar")), is(false));
		assertThat(scmRegex(".*bar").matches(freeStyleProjectWithGitBranches("Foobar")), is(true));
		assertThat(scmRegex("Bar").matches(freeStyleProjectWithGitBranches("Foo", "Bar")), is(true));
		assertThat(scmRegex("B.*").matches(freeStyleProjectWithGitBranches("Foo", "Bar")), is(true));

		assertThat(scmRegex(".*").matches(freeStyleProjectWithGitRepos()), is(false));
		assertThat(scmRegex(".*").matches(freeStyleProjectWithGitRepos("")), is(true));
		assertThat(scmRegex("Foo").matches(freeStyleProjectWithGitRepos("Foo")), is(true));
		assertThat(scmRegex("Foo.*").matches(freeStyleProjectWithGitRepos("Foobar")), is(true));
		assertThat(scmRegex("bar").matches(freeStyleProjectWithGitRepos("Foobar")), is(false));
		assertThat(scmRegex(".*bar").matches(freeStyleProjectWithGitRepos("Foobar")), is(true));
		assertThat(scmRegex("Bar").matches(freeStyleProjectWithGitRepos("Foo", "Bar")), is(true));
		assertThat(scmRegex("B.*").matches(freeStyleProjectWithGitRepos("Foo", "Bar")), is(true));

		assertThat(scmRegex(".*").matches(freeStyleProjectWithLegacyGitRepos()), is(false));
		assertThat(scmRegex(".*").matches(freeStyleProjectWithLegacyGitRepos("")), is(true));
		assertThat(scmRegex("Foo").matches(freeStyleProjectWithLegacyGitRepos("Foo")), is(true));
		assertThat(scmRegex("Foo.*").matches(freeStyleProjectWithLegacyGitRepos("Foobar")), is(true));
		assertThat(scmRegex("bar").matches(freeStyleProjectWithLegacyGitRepos("Foobar")), is(false));
		assertThat(scmRegex(".*bar").matches(freeStyleProjectWithLegacyGitRepos("Foobar")), is(true));
		assertThat(scmRegex("Bar").matches(freeStyleProjectWithLegacyGitRepos("Foo", "Bar")), is(true));
		assertThat(scmRegex("B.*").matches(freeStyleProjectWithLegacyGitRepos("Foo", "Bar")), is(true));
	}

	@Test
	public void testEmail() {
		assertThat(emailRegex(".*").matches(freeStyleProjectWithEmail(null)), is(false));
		assertThat(emailRegex(".*").matches(freeStyleProjectWithEmail("")), is(true));
		assertThat(emailRegex(".*").matches(freeStyleProjectWithEmail("foo@bar.com, quux@baz.net")), is(true));
		assertThat(emailRegex("foo@bar.com").matches(freeStyleProjectWithEmail("foo@bar.com")), is(true));
		assertThat(emailRegex("foo").matches(freeStyleProjectWithEmail("foo@bar.com")), is(false));
		assertThat(emailRegex("@bar.com").matches(freeStyleProjectWithEmail("foo@bar.com")), is(false));
		assertThat(emailRegex("foo@.*").matches(freeStyleProjectWithEmail("foo@bar.com")), is(true));
		assertThat(emailRegex(".*@bar.com").matches(freeStyleProjectWithEmail("foo@bar.com")), is(true));

		assertThat(emailRegex(".*").matches(freeStyleProjectWithExtEmail(null)), is(false));
		assertThat(emailRegex(".*").matches(freeStyleProjectWithExtEmail("")), is(true));
		assertThat(emailRegex(".*").matches(freeStyleProjectWithExtEmail("foo@bar.com, quux@baz.net")), is(true));
		assertThat(emailRegex("foo@bar.com").matches(freeStyleProjectWithExtEmail("foo@bar.com")), is(true));
		assertThat(emailRegex("foo").matches(freeStyleProjectWithExtEmail("foo@bar.com")), is(false));
		assertThat(emailRegex("@bar.com").matches(freeStyleProjectWithExtEmail("foo@bar.com")), is(false));
		assertThat(emailRegex("foo@.*").matches(freeStyleProjectWithExtEmail("foo@bar.com")), is(true));
		assertThat(emailRegex(".*@bar.com").matches(freeStyleProjectWithExtEmail("foo@bar.com")), is(true));

		assertThat(emailRegex(".*").matches(mavenModuleSetWithEmail(null)), is(false));
		assertThat(emailRegex(".*").matches(mavenModuleSetWithEmail("")), is(true));
		assertThat(emailRegex(".*").matches(mavenModuleSetWithEmail("foo@bar.com, quux@baz.net")), is(true));
		assertThat(emailRegex("foo@bar.com").matches(mavenModuleSetWithEmail("foo@bar.com")), is(true));
		assertThat(emailRegex("foo").matches(mavenModuleSetWithEmail("foo@bar.com")), is(false));
		assertThat(emailRegex("@bar.com").matches(mavenModuleSetWithEmail("foo@bar.com")), is(false));
		assertThat(emailRegex("foo@.*").matches(mavenModuleSetWithEmail("foo@bar.com")), is(true));
		assertThat(emailRegex(".*@bar.com").matches(mavenModuleSetWithEmail("foo@bar.com")), is(true));
	}

	@Test
	public void testSchedule() {
		assertThat(scheduleRegex(".*").matches(freeStyleProjectWithTrigger(null)), is(false));
		assertThat(scheduleRegex(".*").matches(freeStyleProjectWithTrigger("")), is(true));
		assertThat(scheduleRegex(".*").matches(freeStyleProjectWithTrigger("\n")), is(true));

		assertThat(scheduleRegex(".*monday.*").matches(freeStyleProjectWithTrigger("# monday")), is(true));
		assertThat(scheduleRegex(".*monday.*").matches(freeStyleProjectWithTrigger("# tuesday")), is(false));
		assertThat(scheduleRegex(".*monday.*").matches(freeStyleProjectWithTrigger("* * * * *")), is(false));
		assertThat(scheduleRegex(".*monday.*").matches(freeStyleProjectWithTrigger("* * * * *\n#")), is(false));
		assertThat(scheduleRegex(".*monday.*").matches(freeStyleProjectWithTrigger("#monday\n* * * * *")), is(true));
	}

	private FreeStyleProject freeStyleProjectWithName(String name) {
		FreeStyleProject freeStyleProject = mock(FreeStyleProject.class);
		when(freeStyleProject.getName()).thenReturn(name);
		return freeStyleProject;
	}

	private FreeStyleProject freeStyleProjectWithDesc(String desc) {
		FreeStyleProject freeStyleProject = mock(FreeStyleProject.class);
		when(freeStyleProject.getDescription()).thenReturn(desc);
		return freeStyleProject;
	}

	private FreeStyleProject freeStyleProjectWithEmail(String email) {
	  	Mailer mailer = mock(Mailer.class);
	  	mailer.recipients = email;

	  	DescribableList publishers = mock(DescribableList.class);
		when(publishers.get(Mailer.descriptor())).thenReturn(mailer);

		FreeStyleProject freeStyleProject = mock(FreeStyleProject.class);
		when(freeStyleProject.getPublishersList()).thenReturn(publishers);
		return freeStyleProject;
	}

	private FreeStyleProject freeStyleProjectWithExtEmail(String email) {
		ExtendedEmailPublisher emailPublisher = new ExtendedEmailPublisher();
		emailPublisher.recipientList = email;

		DescribableList publishers = new DescribableList(mock(Saveable.class), asList(emailPublisher));

		FreeStyleProject freeStyleProject = mock(FreeStyleProject.class);
		when(freeStyleProject.getPublishersList()).thenReturn(publishers);
		return freeStyleProject;
	}

	private MavenModuleSet mavenModuleSetWithEmail(String email) {
		MavenMailer mavenMailer = new MavenMailer();
		mavenMailer.recipients = email;

		DescribableList reporters = new DescribableList(mock(Saveable.class), asList(mavenMailer));

		MavenModuleSet item = mock(MavenModuleSet.class);
		when(item.getReporters()).thenReturn(reporters);
		return item;
	}

	private FreeStyleProject freeStyleProjectWithTrigger(String spec) {
		Trigger trigger = mock(Trigger.class);
		when(trigger.getSpec()).thenReturn(spec);

		Map<TriggerDescriptor, Trigger<?>> triggers = new HashMap<TriggerDescriptor, Trigger<?>>();
		triggers.put(mock(TriggerDescriptor.class), trigger);

		FreeStyleProject freeStyleProject = mock(FreeStyleProject.class);
		when(freeStyleProject.getTriggers()).thenReturn(triggers);
		return freeStyleProject;
	}

	private FreeStyleProject freeStyleProjectWithCVS(String root, String modules, String branch) {
		List<CvsRepository> cvsRepositories = LegacyConvertor.getInstance().convertLegacyConfigToRepositoryStructure(
			root, modules, branch,
			false, "excludedRegions",
			false, null);

		CVSSCM scm = new CVSSCM(cvsRepositories, false, false, true,  true, false, false, true);
		FreeStyleProject freeStyleProject = mock(FreeStyleProject.class);
		when(freeStyleProject.getScm()).thenReturn(scm);
		return freeStyleProject;
	}

	private FreeStyleProject freeStyleProjectWithSVN(String... moduleLocations) {
		SubversionSCM.ModuleLocation[] locations = new SubversionSCM.ModuleLocation[moduleLocations.length];
		for (int i = 0; i < moduleLocations.length; i++) {
			locations[i] = mock(SubversionSCM.ModuleLocation.class);
			when(locations[i].getURL()).thenReturn(moduleLocations[i]);
		}

		SubversionSCM scm = mock(SubversionSCM.class);
		when(scm.getLocations()).thenReturn(locations);

		FreeStyleProject freeStyleProject = mock(FreeStyleProject.class);
		when(freeStyleProject.getScm()).thenReturn(scm);
		return freeStyleProject;
	}

	private FreeStyleProject freeStyleProjectWithGitBranches(String... branches) {
		List<BranchSpec> branchSpecs = new ArrayList<BranchSpec>();
		for (String branch: branches) {
		    BranchSpec branchSpec = mock(BranchSpec.class);
			when(branchSpec.getName()).thenReturn(branch);
			branchSpecs.add(branchSpec);
		}

		GitSCM scm = mock(GitSCM.class);
		when(scm.getBranches()).thenReturn(branchSpecs);

		FreeStyleProject freeStyleProject = mock(FreeStyleProject.class);
		when(freeStyleProject.getScm()).thenReturn(scm);
		return freeStyleProject;
	}

	private FreeStyleProject freeStyleProjectWithGitRepos(String... repos) {
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

		FreeStyleProject freeStyleProject = mock(FreeStyleProject.class);
		when(freeStyleProject.getScm()).thenReturn(scm);
		return freeStyleProject;
	}

	private FreeStyleProject freeStyleProjectWithLegacyGitRepos(String... repos) {
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

		FreeStyleProject freeStyleProject = mock(FreeStyleProject.class);
		when(freeStyleProject.getScm()).thenReturn(scm);
		return freeStyleProject;
	}

	private FreeStyleProject freeStyleProjectWithMaven(String... repos) {
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

		FreeStyleProject freeStyleProject = mock(FreeStyleProject.class);
		when(freeStyleProject.getScm()).thenReturn(scm);
		return freeStyleProject;
	}

	private	RegExJobFilter nameRegex(String regex) {
		return new RegExJobFilter(
			regex,
			IncludeExcludeType.includeMatched.name(),
			RegExJobFilter.ValueType.NAME.name());
	}

	private	RegExJobFilter descRegex(String regex) {
		return new RegExJobFilter(
				regex,
				IncludeExcludeType.includeMatched.name(),
				RegExJobFilter.ValueType.DESCRIPTION.name());
	}

	private	RegExJobFilter emailRegex(String regex) {
		return new RegExJobFilter(
				regex,
				IncludeExcludeType.includeMatched.name(),
				RegExJobFilter.ValueType.EMAIL.name());
	}

	private	RegExJobFilter scheduleRegex(String regex) {
		return new RegExJobFilter(
				regex,
				IncludeExcludeType.includeMatched.name(),
				RegExJobFilter.ValueType.SCHEDULE.name());
	}

	private	RegExJobFilter scmRegex(String regex) {
		return new RegExJobFilter(
				regex,
				IncludeExcludeType.includeMatched.name(),
				RegExJobFilter.ValueType.SCM.name());
	}

	private	RegExJobFilter mavenRegex(String regex) {
		return new RegExJobFilter(
				regex,
				IncludeExcludeType.includeMatched.name(),
				RegExJobFilter.ValueType.MAVEN.name());
	}
}
