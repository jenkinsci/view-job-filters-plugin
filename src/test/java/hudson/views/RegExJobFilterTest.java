package hudson.views;

import hudson.matrix.MatrixProject;
import hudson.maven.MavenModuleSet;
import hudson.model.*;
import hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType;

import org.junit.Test;

import static hudson.views.test.JobMocker.jobOf;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class RegExJobFilterTest extends AbstractHudsonTest {

	@Test
	public void testName() {
	    for (Class<? extends Job> type: asList(FreeStyleProject.class, MatrixProject.class, MavenModuleSet.class)) {
			assertThat(nameRegex(".*").matches(jobOf(type).withName(null).asItem()), is(false));
			assertThat(nameRegex(".*").matches(jobOf(type).withName("").asItem()), is(true));
			assertThat(nameRegex("Foo").matches(jobOf(type).withName("Foo").asItem()), is(true));
			assertThat(nameRegex("Foo").matches(jobOf(type).withName("Foobar").asItem()), is(false));
			assertThat(nameRegex("Foo.*").matches(jobOf(type).withName("Foobar").asItem()), is(true));
			assertThat(nameRegex("bar").matches(jobOf(type).withName("Foobar").asItem()), is(false));
			assertThat(nameRegex(".*bar").matches(jobOf(type).withName("Foobar").asItem()), is(true));
			assertThat(nameRegex(".ooba.").matches(jobOf(type).withName("Foobar").asItem()), is(true));
		}
	}

	@Test
	public void testDescription() {
		for (Class<? extends Job> type: asList(FreeStyleProject.class, MatrixProject.class, MavenModuleSet.class)) {
			assertThat(descRegex(".*").matches(jobOf(type).withDesc(null).asItem()), is(false));
			assertThat(descRegex(".*").matches(jobOf(type).withDesc("").asItem()), is(true));
			assertThat(descRegex("Foo").matches(jobOf(type).withDesc("Foo").asItem()), is(true));
			assertThat(descRegex("Foo").matches(jobOf(type).withDesc("Foobar").asItem()), is(false));
			assertThat(descRegex("Foo.*").matches(jobOf(type).withDesc("Foobar").asItem()), is(true));
			assertThat(descRegex("bar").matches(jobOf(type).withDesc("Foobar").asItem()), is(false));
			assertThat(descRegex(".*bar").matches(jobOf(type).withDesc("Foobar").asItem()), is(true));
			assertThat(descRegex(".ooba.").matches(jobOf(type).withDesc("Foobar").asItem()), is(true));

			assertThat(descRegex(".*").matches(jobOf(type).withDesc("\n").asItem()), is(true));
			assertThat(descRegex("Foo").matches(jobOf(type).withDesc("Quux\nFoo").asItem()), is(true));
			assertThat(descRegex("Foo").matches(jobOf(type).withDesc("Quux\nFoobar").asItem()), is(false));
			assertThat(descRegex("Foo.*").matches(jobOf(type).withDesc("Quux\nFoobar").asItem()), is(true));
			assertThat(descRegex("bar").matches(jobOf(type).withDesc("Quux\nFoobar").asItem()), is(false));
			assertThat(descRegex(".*bar").matches(jobOf(type).withDesc("Quux\nFoobar").asItem()), is(true));
			assertThat(descRegex(".ooba.").matches(jobOf(type).withDesc("Quux\nFoobar").asItem()), is(true));

			assertThat(descRegex(".*desc=test.*").matches(jobOf(type).withDesc("").asItem()), is(false));
			assertThat(descRegex(".*desc=test.*").matches(jobOf(type).withDesc(null).asItem()), is(false));
			assertThat(descRegex(".*desc=test.*").matches(jobOf(type).withDesc("nothing").asItem()), is(false));
			assertThat(descRegex(".*desc=test.*").matches(jobOf(type).withDesc("desc=test").asItem()), is(true));
			assertThat(descRegex(".*desc=test.*").matches(jobOf(type).withDesc("mydesc=test2").asItem()), is(true));
			assertThat(descRegex(".*desc=test.*").matches(jobOf(type).withDesc("thisis\nmydesc=testn2\nforyou").asItem()), is(true));
			assertThat(descRegex(".*desc=test.*").matches(jobOf(type).withDesc("1&#xd;\ndesc=test&#xd;\n2").asItem()), is(true));
			assertThat(descRegex(".*desc=test.*").matches(jobOf(type).withDesc("1 desc=test 2").asItem()), is(true));
		}
	}

	@Test
	public void testSCM() {
		for (Class<? extends Job> type: asList(FreeStyleProject.class, MatrixProject.class, MavenModuleSet.class)) {
			assertThat(scmRegex(".*my-office.*").matches(jobOf(type).withCVS("root", "modules", "branch").asItem()), is(false));
			assertThat(scmRegex(".*my-office.*").matches(jobOf(type).withCVS(null, "modules", "branch").asItem()), is(false));
			assertThat(scmRegex(".*my-office.*").matches(jobOf(type).withCVS("root", "modules", null).asItem()), is(false));
			assertThat(scmRegex(".*my-office.*").matches(jobOf(type).withCVS("root/my-office", "modules", "branch").asItem()), is(true));
			assertThat(scmRegex(".*my-office.*").matches(jobOf(type).withCVS("root", "modules/my-office", "branch").asItem()), is(true));
			assertThat(scmRegex(".*my-office.*").matches(jobOf(type).withCVS("root", "modules", "branch/my-office").asItem()), is(true));

			assertThat(scmRegex(".*").matches(jobOf(type).withSVN().asItem()), is(false));
			assertThat(scmRegex(".*").matches(jobOf(type).withSVN("").asItem()), is(true));
			assertThat(scmRegex("Foo").matches(jobOf(type).withSVN("Foo").asItem()), is(true));
			assertThat(scmRegex("Foo.*").matches(jobOf(type).withSVN("Foobar").asItem()), is(true));
			assertThat(scmRegex("bar").matches(jobOf(type).withSVN("Foobar").asItem()), is(false));
			assertThat(scmRegex(".*bar").matches(jobOf(type).withSVN("Foobar").asItem()), is(true));
			assertThat(scmRegex("Bar").matches(jobOf(type).withSVN("Foo", "Bar").asItem()), is(true));
			assertThat(scmRegex("B.*").matches(jobOf(type).withSVN("Foo", "Bar").asItem()), is(true));

			assertThat(scmRegex(".*").matches(jobOf(type).withGitBranches().asItem()), is(false));
			assertThat(scmRegex(".*").matches(jobOf(type).withGitBranches("").asItem()), is(true));
			assertThat(scmRegex("Foo").matches(jobOf(type).withGitBranches("Foo").asItem()), is(true));
			assertThat(scmRegex("Foo.*").matches(jobOf(type).withGitBranches("Foobar").asItem()), is(true));
			assertThat(scmRegex("bar").matches(jobOf(type).withGitBranches("Foobar").asItem()), is(false));
			assertThat(scmRegex(".*bar").matches(jobOf(type).withGitBranches("Foobar").asItem()), is(true));
			assertThat(scmRegex("Bar").matches(jobOf(type).withGitBranches("Foo", "Bar").asItem()), is(true));
			assertThat(scmRegex("B.*").matches(jobOf(type).withGitBranches("Foo", "Bar").asItem()), is(true));

			assertThat(scmRegex(".*").matches(jobOf(type).withGitRepos().asItem()), is(false));
			assertThat(scmRegex(".*").matches(jobOf(type).withGitRepos("").asItem()), is(true));
			assertThat(scmRegex("Foo").matches(jobOf(type).withGitRepos("Foo").asItem()), is(true));
			assertThat(scmRegex("Foo.*").matches(jobOf(type).withGitRepos("Foobar").asItem()), is(true));
			assertThat(scmRegex("bar").matches(jobOf(type).withGitRepos("Foobar").asItem()), is(false));
			assertThat(scmRegex(".*bar").matches(jobOf(type).withGitRepos("Foobar").asItem()), is(true));
			assertThat(scmRegex("Bar").matches(jobOf(type).withGitRepos("Foo", "Bar").asItem()), is(true));
			assertThat(scmRegex("B.*").matches(jobOf(type).withGitRepos("Foo", "Bar").asItem()), is(true));

			assertThat(scmRegex(".*").matches(jobOf(type).withLegacyGitRepos().asItem()), is(false));
			assertThat(scmRegex(".*").matches(jobOf(type).withLegacyGitRepos("").asItem()), is(true));
			assertThat(scmRegex("Foo").matches(jobOf(type).withLegacyGitRepos("Foo").asItem()), is(true));
			assertThat(scmRegex("Foo.*").matches(jobOf(type).withLegacyGitRepos("Foobar").asItem()), is(true));
			assertThat(scmRegex("bar").matches(jobOf(type).withLegacyGitRepos("Foobar").asItem()), is(false));
			assertThat(scmRegex(".*bar").matches(jobOf(type).withLegacyGitRepos("Foobar").asItem()), is(true));
			assertThat(scmRegex("Bar").matches(jobOf(type).withLegacyGitRepos("Foo", "Bar").asItem()), is(true));
			assertThat(scmRegex("B.*").matches(jobOf(type).withLegacyGitRepos("Foo", "Bar").asItem()), is(true));
		}
	}

	@Test
	public void testEmail() {
		for (Class<? extends Job> type: asList(FreeStyleProject.class, MatrixProject.class, MavenModuleSet.class)) {
			assertThat(emailRegex(".*").matches(jobOf(type).withEmail(null).asItem()), is(false));
			assertThat(emailRegex(".*").matches(jobOf(type).withEmail("").asItem()), is(true));
			assertThat(emailRegex(".*").matches(jobOf(type).withEmail("foo@bar.com, quux@baz.net").asItem()), is(true));
			assertThat(emailRegex("foo@bar.com").matches(jobOf(type).withEmail("foo@bar.com").asItem()), is(true));
			assertThat(emailRegex("foo").matches(jobOf(type).withEmail("foo@bar.com").asItem()), is(false));
			assertThat(emailRegex("@bar.com").matches(jobOf(type).withEmail("foo@bar.com").asItem()), is(false));
			assertThat(emailRegex("foo@.*").matches(jobOf(type).withEmail("foo@bar.com").asItem()), is(true));
			assertThat(emailRegex(".*@bar.com").matches(jobOf(type).withEmail("foo@bar.com").asItem()), is(true));

			assertThat(emailRegex(".*").matches(jobOf(type).withExtEmail(null).asItem()), is(false));
			assertThat(emailRegex(".*").matches(jobOf(type).withExtEmail("").asItem()), is(true));
			assertThat(emailRegex(".*").matches(jobOf(type).withExtEmail("foo@bar.com, quux@baz.net").asItem()), is(true));
			assertThat(emailRegex("foo@bar.com").matches(jobOf(type).withExtEmail("foo@bar.com").asItem()), is(true));
			assertThat(emailRegex("foo").matches(jobOf(type).withExtEmail("foo@bar.com").asItem()), is(false));
			assertThat(emailRegex("@bar.com").matches(jobOf(type).withExtEmail("foo@bar.com").asItem()), is(false));
			assertThat(emailRegex("foo@.*").matches(jobOf(type).withExtEmail("foo@bar.com").asItem()), is(true));
			assertThat(emailRegex(".*@bar.com").matches(jobOf(type).withExtEmail("foo@bar.com").asItem()), is(true));
		}
	}

	@Test
	public void testSchedule() {
		for (Class<? extends Job> type : asList(FreeStyleProject.class, MatrixProject.class, MavenModuleSet.class)) {
			assertThat(scheduleRegex(".*").matches(jobOf(type).withTrigger(null).asItem()), is(false));
			assertThat(scheduleRegex(".*").matches(jobOf(type).withTrigger("").asItem()), is(true));
			assertThat(scheduleRegex(".*").matches(jobOf(type).withTrigger("\n").asItem()), is(true));
			assertThat(scheduleRegex(".*monday.*").matches(jobOf(type).withTrigger("# monday").asItem()), is(true));
			assertThat(scheduleRegex(".*monday.*").matches(jobOf(type).withTrigger("# tuesday").asItem()), is(false));
			assertThat(scheduleRegex(".*monday.*").matches(jobOf(type).withTrigger("* * * * *").asItem()), is(false));
			assertThat(scheduleRegex(".*monday.*").matches(jobOf(type).withTrigger("* * * * *\n#").asItem()), is(false));
			assertThat(scheduleRegex(".*monday.*").matches(jobOf(type).withTrigger("#monday\n* * * * *").asItem()), is(true));
		}
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
