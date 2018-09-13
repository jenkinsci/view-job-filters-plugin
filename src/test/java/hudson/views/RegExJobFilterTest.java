package hudson.views;

import hudson.model.*;

import hudson.views.test.JobMocker;
import hudson.views.test.JobType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.*;
import static hudson.views.RegExJobFilter.ValueType.*;
import static hudson.views.test.JobMocker.jobOf;
import static hudson.views.test.JobType.*;
import static org.junit.Assert.*;
import static hudson.views.test.ViewJobFilters.*;

public class RegExJobFilterTest extends AbstractHudsonTest {

	@Test
	public void testName() {
		assertFalse(nameRegex(".*").matches(jobOf(TOP_LEVEL_ITEM).asItem()));

	    for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET)) {
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
		assertFalse(descRegex(".*").matches(jobOf(TOP_LEVEL_ITEM).asItem()));

		for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET)) {
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
		assertFalse(scmRegex(".*").matches(jobOf(TOP_LEVEL_ITEM).asItem()));

		for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET, SCMED_ITEM, SCM_TRIGGER_ITEM)) {
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
		assertFalse(emailRegex(".*").matches(jobOf(TOP_LEVEL_ITEM).asItem()));

		for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET)) {
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
		assertFalse(scheduleRegex(".*").matches(jobOf(TOP_LEVEL_ITEM).asItem()));

		for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET)) {
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

	@Test
	public void testMaven() {
		assertFalse(mavenRegex(".*").matches(jobOf(TOP_LEVEL_ITEM).asItem()));

		for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET)) {
			assertTrue(mavenRegex(".*").matches(jobOf(type).withMavenBuilder("", "", "", "").asItem()));
			assertTrue(mavenRegex("Foo").matches(jobOf(type).withMavenBuilder("Foo", "", "", "").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOf(type).withMavenBuilder("Foo", "", "", "").asItem()));
			assertTrue(mavenRegex("Foo.*").matches(jobOf(type).withMavenBuilder("Foobar", "", "", "").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOf(type).withMavenBuilder("Foobar", "", "", "").asItem()));
			assertTrue(mavenRegex(".*bar").matches(jobOf(type).withMavenBuilder("Foobar", "", "", "").asItem()));

			assertTrue(mavenRegex("Foo bar").matches(jobOf(type).withMavenBuilder("Foo\nbar", "", "", "").asItem()));
			assertFalse(mavenRegex("Foo").matches(jobOf(type).withMavenBuilder("Foo\nbar", "", "", "").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOf(type).withMavenBuilder("Foo\nbar", "", "", "").asItem()));
			assertTrue(mavenRegex("Foo.*").matches(jobOf(type).withMavenBuilder("Foo\nbar", "", "", "").asItem()));
			assertTrue(mavenRegex(".*bar").matches(jobOf(type).withMavenBuilder("Foo\nbar", "", "", "").asItem()));

			assertTrue(mavenRegex(".*").matches(jobOf(type).withMavenBuilder("", "", "", "").asItem()));
			assertTrue(mavenRegex("Foo").matches(jobOf(type).withMavenBuilder("", "Foo", "", "").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOf(type).withMavenBuilder("", "Foo", "", "").asItem()));
			assertTrue(mavenRegex("Foo.*").matches(jobOf(type).withMavenBuilder("", "Foobar", "", "").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOf(type).withMavenBuilder("", "Foobar", "", "").asItem()));
			assertTrue(mavenRegex(".*bar").matches(jobOf(type).withMavenBuilder("", "Foobar", "", "").asItem()));

			assertTrue(mavenRegex(".*").matches(jobOf(type).withMavenBuilder("", "", "", "").asItem()));
			assertTrue(mavenRegex("Foo").matches(jobOf(type).withMavenBuilder("", "", "Foo", "").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOf(type).withMavenBuilder("", "", "Foo", "").asItem()));
			assertTrue(mavenRegex("Foo.*").matches(jobOf(type).withMavenBuilder("", "", "Foobar", "").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOf(type).withMavenBuilder("", "", "Foobar", "").asItem()));
			assertTrue(mavenRegex(".*bar").matches(jobOf(type).withMavenBuilder("", "", "Foobar", "").asItem()));

			assertTrue(mavenRegex(".*").matches(jobOf(type).withMavenBuilder("", "", "", "").asItem()));
			assertTrue(mavenRegex("Foo").matches(jobOf(type).withMavenBuilder("", "", "", "Foo").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOf(type).withMavenBuilder("", "", "", "Foo").asItem()));
			assertTrue(mavenRegex("Foo.*").matches(jobOf(type).withMavenBuilder("", "", "", "Foobar").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOf(type).withMavenBuilder("", "", "", "Foobar").asItem()));
			assertTrue(mavenRegex(".*bar").matches(jobOf(type).withMavenBuilder("", "", "", "Foobar").asItem()));
		}

		for (JobType<? extends Job> type: availableJobTypes(MAVEN_MODULE_SET)) {
			for (JobMocker.MavenBuildStep step : JobMocker.MavenBuildStep.values()) {
				assertTrue(mavenRegex(".*").matches(jobOf(type).withMavenBuildStep(step, "", "", "", "").asItem()));
				assertTrue(mavenRegex("Foo").matches(jobOf(type).withMavenBuildStep(step, "Foo", "", "", "").asItem()));
				assertFalse(mavenRegex("bar").matches(jobOf(type).withMavenBuildStep(step, "Foo", "", "", "").asItem()));
				assertTrue(mavenRegex("Foo.*").matches(jobOf(type).withMavenBuildStep(step, "Foobar", "", "", "").asItem()));
				assertFalse(mavenRegex("bar").matches(jobOf(type).withMavenBuildStep(step, "Foobar", "", "", "").asItem()));
				assertTrue(mavenRegex(".*bar").matches(jobOf(type).withMavenBuildStep(step, "Foobar", "", "", "").asItem()));

				assertTrue(mavenRegex("Foo bar").matches(jobOf(type).withMavenBuildStep(step, "Foo\nbar", "", "", "").asItem()));
				assertFalse(mavenRegex("Foo").matches(jobOf(type).withMavenBuildStep(step, "Foo\nbar", "", "", "").asItem()));
				assertFalse(mavenRegex("bar").matches(jobOf(type).withMavenBuildStep(step, "Foo\nbar", "", "", "").asItem()));
				assertTrue(mavenRegex("Foo.*").matches(jobOf(type).withMavenBuildStep(step, "Foo\nbar", "", "", "").asItem()));
				assertTrue(mavenRegex(".*bar").matches(jobOf(type).withMavenBuildStep(step, "Foo\nbar", "", "", "").asItem()));

				assertTrue(mavenRegex(".*").matches(jobOf(type).withMavenBuildStep(step, "", "", "", "").asItem()));
				assertTrue(mavenRegex("Foo").matches(jobOf(type).withMavenBuildStep(step, "", "Foo", "", "").asItem()));
				assertFalse(mavenRegex("bar").matches(jobOf(type).withMavenBuildStep(step, "", "Foo", "", "").asItem()));
				assertTrue(mavenRegex("Foo.*").matches(jobOf(type).withMavenBuildStep(step, "", "Foobar", "", "").asItem()));
				assertFalse(mavenRegex("bar").matches(jobOf(type).withMavenBuildStep(step, "", "Foobar", "", "").asItem()));
				assertTrue(mavenRegex(".*bar").matches(jobOf(type).withMavenBuildStep(step, "", "Foobar", "", "").asItem()));

				assertTrue(mavenRegex(".*").matches(jobOf(type).withMavenBuildStep(step, "", "", "", "").asItem()));
				assertTrue(mavenRegex("Foo").matches(jobOf(type).withMavenBuildStep(step, "", "", "Foo", "").asItem()));
				assertFalse(mavenRegex("bar").matches(jobOf(type).withMavenBuildStep(step, "", "", "Foo", "").asItem()));
				assertTrue(mavenRegex("Foo.*").matches(jobOf(type).withMavenBuildStep(step, "", "", "Foobar", "").asItem()));
				assertFalse(mavenRegex("bar").matches(jobOf(type).withMavenBuildStep(step, "", "", "Foobar", "").asItem()));
				assertTrue(mavenRegex(".*bar").matches(jobOf(type).withMavenBuildStep(step, "", "", "Foobar", "").asItem()));

				assertTrue(mavenRegex(".*").matches(jobOf(type).withMavenBuildStep(step, "", "", "", "").asItem()));
				assertTrue(mavenRegex("Foo").matches(jobOf(type).withMavenBuildStep(step, "", "", "", "Foo").asItem()));
				assertFalse(mavenRegex("bar").matches(jobOf(type).withMavenBuildStep(step, "", "", "", "Foo").asItem()));
				assertTrue(mavenRegex("Foo.*").matches(jobOf(type).withMavenBuildStep(step, "", "", "", "Foobar").asItem()));
				assertFalse(mavenRegex("bar").matches(jobOf(type).withMavenBuildStep(step, "", "", "", "Foobar").asItem()));
				assertTrue(mavenRegex(".*bar").matches(jobOf(type).withMavenBuildStep(step, "", "", "", "Foobar").asItem()));
			}
		}
	}

	@Test
	public void testNode() {
		assertFalse(nodeRegex(".*").matches(jobOf(TOP_LEVEL_ITEM).asItem()));

		for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET)) {
			assertTrue(nodeRegex(".*").matches(jobOf(type).withAssignedLabel("").asItem()));
			assertTrue(nodeRegex("Foo").matches(jobOf(type).withAssignedLabel("Foo").asItem()));
			assertFalse(nodeRegex("bar").matches(jobOf(type).withAssignedLabel("Foo").asItem()));
			assertTrue(nodeRegex("Foo.*").matches(jobOf(type).withAssignedLabel("Foobar").asItem()));
			assertFalse(nodeRegex("bar").matches(jobOf(type).withAssignedLabel("Foobar").asItem()));
			assertTrue(nodeRegex(".*bar").matches(jobOf(type).withAssignedLabel("Foobar").asItem()));
		}
	}

	@Test
	public void testConfigRoundtrip() throws Exception {
		testConfigRoundtrip(
			"regex-view-1",
			new RegExJobFilter("NaMeRegEx", excludeMatched.name(), NAME.name())
		);

		testConfigRoundtrip(
			"regex-view-2",
			new RegExJobFilter("DeScriptionRegEx", excludeMatched.name(), DESCRIPTION.name()),
			new RegExJobFilter("EmailRegEx", includeUnmatched.name(), EMAIL.name())
		);

		testConfigRoundtrip(
			"regex-view-3",
			new RegExJobFilter("MavenRegEx", excludeUnmatched.name(), MAVEN.name()),
			new RegExJobFilter("NodeRegEx", excludeMatched.name(), NODE.name()),
			new RegExJobFilter("ScmRegEx", excludeMatched.name(), SCM.name())
		);
	}

	private void testConfigRoundtrip(String viewName, RegExJobFilter... filters) throws Exception {
		List<RegExJobFilter> expectedFilters = new ArrayList<RegExJobFilter>();
		for (RegExJobFilter filter: filters) {
			expectedFilters.add(new RegExJobFilter(filter.getRegex(), filter.getIncludeExcludeTypeString(), filter.getValueTypeString()));
		}

		ListView view = createFilteredView(viewName, filters);
		j.configRoundtrip(view);

		ListView viewAfterRoundtrip = (ListView)j.getInstance().getView(viewName);
		assertFilterEquals(expectedFilters, viewAfterRoundtrip.getJobFilters());

		viewAfterRoundtrip.save();
		j.getInstance().reload();

		ListView viewAfterReload = (ListView)j.getInstance().getView(viewName);
		assertFilterEquals(expectedFilters, viewAfterReload.getJobFilters());
	}

	private void assertFilterEquals(List<RegExJobFilter> expectedFilters, List<ViewJobFilter> actualFilters) {
		assertThat(actualFilters.size(), is(expectedFilters.size()));
		for (int i = 0; i < actualFilters.size(); i++) {
			ViewJobFilter actualFilter = actualFilters.get(i);
		    RegExJobFilter expectedFilter = expectedFilters.get(i);
			assertThat(actualFilter, instanceOf(RegExJobFilter.class));
			assertThat(((RegExJobFilter)actualFilter).getRegex(), is(expectedFilter.getRegex()));
			assertThat(((RegExJobFilter)actualFilter).getIncludeExcludeTypeString(), is(expectedFilter.getIncludeExcludeTypeString()));
			assertThat(((RegExJobFilter)actualFilter).getValueTypeString(), is(expectedFilter.getValueTypeString()));
		}
	}

}
