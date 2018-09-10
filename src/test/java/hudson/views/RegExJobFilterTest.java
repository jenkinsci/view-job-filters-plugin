package hudson.views;

import hudson.matrix.MatrixProject;
import hudson.maven.MavenModuleSet;
import hudson.model.*;

import org.junit.Test;

import static hudson.views.test.JobMocker.jobOf;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static hudson.views.test.ViewJobFilters.*;

public class RegExJobFilterTest extends AbstractHudsonTest {

	@Test
	public void testName() {
	    for (Class<? extends Job> type: asList(FreeStyleProject.class, MatrixProject.class, MavenModuleSet.class)) {
			assertFalse(nameRegex(".*").matches(jobOf(type).withName(null).asItem()));
			assertTrue(nameRegex(".*").matches(jobOf(type).withName("").asItem()));
			assertTrue(nameRegex("Foo").matches(jobOf(type).withName("Foo").asItem()));
			assertFalse(nameRegex("Foo").matches(jobOf(type).withName("Foobar").asItem()));
			assertTrue(nameRegex("Foo.*").matches(jobOf(type).withName("Foobar").asItem()));
			assertFalse(nameRegex("bar").matches(jobOf(type).withName("Foobar").asItem()));
			assertTrue(nameRegex(".*bar").matches(jobOf(type).withName("Foobar").asItem()));
			assertTrue(nameRegex(".ooba.").matches(jobOf(type).withName("Foobar").asItem()));
		}
	}

	@Test
	public void testDescription() {
		for (Class<? extends Job> type: asList(FreeStyleProject.class, MatrixProject.class, MavenModuleSet.class)) {
			assertFalse(descRegex(".*").matches(jobOf(type).withDesc(null).asItem()));
			assertTrue(descRegex(".*").matches(jobOf(type).withDesc("").asItem()));
			assertTrue(descRegex("Foo").matches(jobOf(type).withDesc("Foo").asItem()));
			assertFalse(descRegex("Foo").matches(jobOf(type).withDesc("Foobar").asItem()));
			assertTrue(descRegex("Foo.*").matches(jobOf(type).withDesc("Foobar").asItem()));
			assertFalse(descRegex("bar").matches(jobOf(type).withDesc("Foobar").asItem()));
			assertTrue(descRegex(".*bar").matches(jobOf(type).withDesc("Foobar").asItem()));
			assertTrue(descRegex(".ooba.").matches(jobOf(type).withDesc("Foobar").asItem()));

			assertTrue(descRegex(".*").matches(jobOf(type).withDesc("\n").asItem()));
			assertTrue(descRegex("Foo").matches(jobOf(type).withDesc("Quux\nFoo").asItem()));
			assertFalse(descRegex("Foo").matches(jobOf(type).withDesc("Quux\nFoobar").asItem()));
			assertTrue(descRegex("Foo.*").matches(jobOf(type).withDesc("Quux\nFoobar").asItem()));
			assertFalse(descRegex("bar").matches(jobOf(type).withDesc("Quux\nFoobar").asItem()));
			assertTrue(descRegex(".*bar").matches(jobOf(type).withDesc("Quux\nFoobar").asItem()));
			assertTrue(descRegex(".ooba.").matches(jobOf(type).withDesc("Quux\nFoobar").asItem()));

			assertFalse(descRegex(".*desc=test.*").matches(jobOf(type).withDesc("").asItem()));
			assertFalse(descRegex(".*desc=test.*").matches(jobOf(type).withDesc(null).asItem()));
			assertFalse(descRegex(".*desc=test.*").matches(jobOf(type).withDesc("nothing").asItem()));
			assertTrue(descRegex(".*desc=test.*").matches(jobOf(type).withDesc("desc=test").asItem()));
			assertTrue(descRegex(".*desc=test.*").matches(jobOf(type).withDesc("mydesc=test2").asItem()));
			assertTrue(descRegex(".*desc=test.*").matches(jobOf(type).withDesc("thisis\nmydesc=testn2\nforyou").asItem()));
			assertTrue(descRegex(".*desc=test.*").matches(jobOf(type).withDesc("1&#xd;\ndesc=test&#xd;\n2").asItem()));
			assertTrue(descRegex(".*desc=test.*").matches(jobOf(type).withDesc("1 desc=test 2").asItem()));
		}
	}

	@Test
	public void testSCM() {
		for (Class<? extends Job> type: asList(FreeStyleProject.class, MatrixProject.class, MavenModuleSet.class)) {
			assertFalse(scmRegex(".*my-office.*").matches(jobOf(type).withCVS("root", "modules", "branch").asItem()));
			assertFalse(scmRegex(".*my-office.*").matches(jobOf(type).withCVS(null, "modules", "branch").asItem()));
			assertFalse(scmRegex(".*my-office.*").matches(jobOf(type).withCVS("root", "modules", null).asItem()));
			assertTrue(scmRegex(".*my-office.*").matches(jobOf(type).withCVS("root/my-office", "modules", "branch").asItem()));
			assertTrue(scmRegex(".*my-office.*").matches(jobOf(type).withCVS("root", "modules/my-office", "branch").asItem()));
			assertTrue(scmRegex(".*my-office.*").matches(jobOf(type).withCVS("root", "modules", "branch/my-office").asItem()));

			assertFalse(scmRegex(".*").matches(jobOf(type).withSVN().asItem()));
			assertTrue(scmRegex(".*").matches(jobOf(type).withSVN("").asItem()));
			assertTrue(scmRegex("Foo").matches(jobOf(type).withSVN("Foo").asItem()));
			assertTrue(scmRegex("Foo.*").matches(jobOf(type).withSVN("Foobar").asItem()));
			assertFalse(scmRegex("bar").matches(jobOf(type).withSVN("Foobar").asItem()));
			assertTrue(scmRegex(".*bar").matches(jobOf(type).withSVN("Foobar").asItem()));
			assertTrue(scmRegex("Bar").matches(jobOf(type).withSVN("Foo", "Bar").asItem()));
			assertTrue(scmRegex("B.*").matches(jobOf(type).withSVN("Foo", "Bar").asItem()));

			assertFalse(scmRegex(".*").matches(jobOf(type).withGitBranches().asItem()));
			assertTrue(scmRegex(".*").matches(jobOf(type).withGitBranches("").asItem()));
			assertTrue(scmRegex("Foo").matches(jobOf(type).withGitBranches("Foo").asItem()));
			assertTrue(scmRegex("Foo.*").matches(jobOf(type).withGitBranches("Foobar").asItem()));
			assertFalse(scmRegex("bar").matches(jobOf(type).withGitBranches("Foobar").asItem()));
			assertTrue(scmRegex(".*bar").matches(jobOf(type).withGitBranches("Foobar").asItem()));
			assertTrue(scmRegex("Bar").matches(jobOf(type).withGitBranches("Foo", "Bar").asItem()));
			assertTrue(scmRegex("B.*").matches(jobOf(type).withGitBranches("Foo", "Bar").asItem()));

			assertFalse(scmRegex(".*").matches(jobOf(type).withGitRepos().asItem()));
			assertTrue(scmRegex(".*").matches(jobOf(type).withGitRepos("").asItem()));
			assertTrue(scmRegex("Foo").matches(jobOf(type).withGitRepos("Foo").asItem()));
			assertTrue(scmRegex("Foo.*").matches(jobOf(type).withGitRepos("Foobar").asItem()));
			assertFalse(scmRegex("bar").matches(jobOf(type).withGitRepos("Foobar").asItem()));
			assertTrue(scmRegex(".*bar").matches(jobOf(type).withGitRepos("Foobar").asItem()));
			assertTrue(scmRegex("Bar").matches(jobOf(type).withGitRepos("Foo", "Bar").asItem()));
			assertTrue(scmRegex("B.*").matches(jobOf(type).withGitRepos("Foo", "Bar").asItem()));

			assertFalse(scmRegex(".*").matches(jobOf(type).withLegacyGitRepos().asItem()));
			assertTrue(scmRegex(".*").matches(jobOf(type).withLegacyGitRepos("").asItem()));
			assertTrue(scmRegex("Foo").matches(jobOf(type).withLegacyGitRepos("Foo").asItem()));
			assertTrue(scmRegex("Foo.*").matches(jobOf(type).withLegacyGitRepos("Foobar").asItem()));
			assertFalse(scmRegex("bar").matches(jobOf(type).withLegacyGitRepos("Foobar").asItem()));
			assertTrue(scmRegex(".*bar").matches(jobOf(type).withLegacyGitRepos("Foobar").asItem()));
			assertTrue(scmRegex("Bar").matches(jobOf(type).withLegacyGitRepos("Foo", "Bar").asItem()));
			assertTrue(scmRegex("B.*").matches(jobOf(type).withLegacyGitRepos("Foo", "Bar").asItem()));
		}
	}

	@Test
	public void testEmail() {
		for (Class<? extends Job> type: asList(FreeStyleProject.class, MatrixProject.class, MavenModuleSet.class)) {
			assertFalse(emailRegex(".*").matches(jobOf(type).withEmail(null).asItem()));
			assertTrue(emailRegex(".*").matches(jobOf(type).withEmail("").asItem()));
			assertTrue(emailRegex(".*").matches(jobOf(type).withEmail("foo@bar.com, quux@baz.net").asItem()));
			assertTrue(emailRegex("foo@bar.com").matches(jobOf(type).withEmail("foo@bar.com").asItem()));
			assertFalse(emailRegex("foo").matches(jobOf(type).withEmail("foo@bar.com").asItem()));
			assertFalse(emailRegex("@bar.com").matches(jobOf(type).withEmail("foo@bar.com").asItem()));
			assertTrue(emailRegex("foo@.*").matches(jobOf(type).withEmail("foo@bar.com").asItem()));
			assertTrue(emailRegex(".*@bar.com").matches(jobOf(type).withEmail("foo@bar.com").asItem()));

			assertFalse(emailRegex(".*").matches(jobOf(type).withExtEmail(null).asItem()));
			assertTrue(emailRegex(".*").matches(jobOf(type).withExtEmail("").asItem()));
			assertTrue(emailRegex(".*").matches(jobOf(type).withExtEmail("foo@bar.com, quux@baz.net").asItem()));
			assertTrue(emailRegex("foo@bar.com").matches(jobOf(type).withExtEmail("foo@bar.com").asItem()));
			assertFalse(emailRegex("foo").matches(jobOf(type).withExtEmail("foo@bar.com").asItem()));
			assertFalse(emailRegex("@bar.com").matches(jobOf(type).withExtEmail("foo@bar.com").asItem()));
			assertTrue(emailRegex("foo@.*").matches(jobOf(type).withExtEmail("foo@bar.com").asItem()));
			assertTrue(emailRegex(".*@bar.com").matches(jobOf(type).withExtEmail("foo@bar.com").asItem()));
		}
	}

	@Test
	public void testSchedule() {
		for (Class<? extends Job> type : asList(FreeStyleProject.class, MatrixProject.class, MavenModuleSet.class)) {
			assertFalse(scheduleRegex(".*").matches(jobOf(type).withTrigger(null).asItem()));
			assertTrue(scheduleRegex(".*").matches(jobOf(type).withTrigger("").asItem()));
			assertTrue(scheduleRegex(".*").matches(jobOf(type).withTrigger("\n").asItem()));
			assertTrue(scheduleRegex(".*monday.*").matches(jobOf(type).withTrigger("# monday").asItem()));
			assertFalse(scheduleRegex(".*monday.*").matches(jobOf(type).withTrigger("# tuesday").asItem()));
			assertFalse(scheduleRegex(".*monday.*").matches(jobOf(type).withTrigger("* * * * *").asItem()));
			assertFalse(scheduleRegex(".*monday.*").matches(jobOf(type).withTrigger("* * * * *\n#").asItem()));
			assertTrue(scheduleRegex(".*monday.*").matches(jobOf(type).withTrigger("#monday\n* * * * *").asItem()));
		}
	}
}
