package hudson.views;

import hudson.model.*;

import hudson.views.test.JobMocker;
import hudson.views.test.JobType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static hudson.views.test.JobMocker.EmailType.DEFAULT;
import static hudson.views.test.JobMocker.EmailType.EXTENDED;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.*;
import static hudson.views.RegExJobFilter.ValueType.*;
import static hudson.views.test.JobMocker.jobOfType;
import static hudson.views.test.JobType.*;
import static org.junit.Assert.*;
import static hudson.views.test.ViewJobFilters.*;

public class RegExJobFilterTest extends AbstractHudsonTest {

	@Test
	public void testName() {
		assertFalse(nameRegex(".*").matches(jobOfType(TOP_LEVEL_ITEM).asItem()));

	    for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET)) {
			assertFalse(nameRegex(".*").matches(jobOfType(type).withName(null).asItem()));
			assertTrue(nameRegex(".*").matches(jobOfType(type).withName("").asItem()));
			assertTrue(nameRegex("Foo").matches(jobOfType(type).withName("Foo").asItem()));
			assertFalse(nameRegex("Foo").matches(jobOfType(type).withName("Foobar").asItem()));
			assertTrue(nameRegex("Foo.*").matches(jobOfType(type).withName("Foobar").asItem()));
			assertFalse(nameRegex("bar").matches(jobOfType(type).withName("Foobar").asItem()));
			assertTrue(nameRegex(".*bar").matches(jobOfType(type).withName("Foobar").asItem()));
			assertTrue(nameRegex(".ooba.").matches(jobOfType(type).withName("Foobar").asItem()));
		}
	}

	@Test
	public void testDescription() {
		assertFalse(descRegex(".*").matches(jobOfType(TOP_LEVEL_ITEM).asItem()));

		for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET)) {
			assertFalse(descRegex(".*").matches(jobOfType(type).withDesc(null).asItem()));
			assertTrue(descRegex(".*").matches(jobOfType(type).withDesc("").asItem()));
			assertTrue(descRegex("Foo").matches(jobOfType(type).withDesc("Foo").asItem()));
			assertFalse(descRegex("Foo").matches(jobOfType(type).withDesc("Foobar").asItem()));
			assertTrue(descRegex("Foo.*").matches(jobOfType(type).withDesc("Foobar").asItem()));
			assertFalse(descRegex("bar").matches(jobOfType(type).withDesc("Foobar").asItem()));
			assertTrue(descRegex(".*bar").matches(jobOfType(type).withDesc("Foobar").asItem()));
			assertTrue(descRegex(".ooba.").matches(jobOfType(type).withDesc("Foobar").asItem()));

			assertTrue(descRegex(".*").matches(jobOfType(type).withDesc("\n").asItem()));
			assertTrue(descRegex("Foo").matches(jobOfType(type).withDesc("Quux\nFoo").asItem()));
			assertFalse(descRegex("Foo").matches(jobOfType(type).withDesc("Quux\nFoobar").asItem()));
			assertTrue(descRegex("Foo.*").matches(jobOfType(type).withDesc("Quux\nFoobar").asItem()));
			assertFalse(descRegex("bar").matches(jobOfType(type).withDesc("Quux\nFoobar").asItem()));
			assertTrue(descRegex(".*bar").matches(jobOfType(type).withDesc("Quux\nFoobar").asItem()));
			assertTrue(descRegex(".ooba.").matches(jobOfType(type).withDesc("Quux\nFoobar").asItem()));

			assertFalse(descRegex(".*desc=test.*").matches(jobOfType(type).withDesc("").asItem()));
			assertFalse(descRegex(".*desc=test.*").matches(jobOfType(type).withDesc(null).asItem()));
			assertFalse(descRegex(".*desc=test.*").matches(jobOfType(type).withDesc("nothing").asItem()));
			assertTrue(descRegex(".*desc=test.*").matches(jobOfType(type).withDesc("desc=test").asItem()));
			assertTrue(descRegex(".*desc=test.*").matches(jobOfType(type).withDesc("mydesc=test2").asItem()));
			assertTrue(descRegex(".*desc=test.*").matches(jobOfType(type).withDesc("thisis\nmydesc=testn2\nforyou").asItem()));
			assertTrue(descRegex(".*desc=test.*").matches(jobOfType(type).withDesc("1&#xd;\ndesc=test&#xd;\n2").asItem()));
			assertTrue(descRegex(".*desc=test.*").matches(jobOfType(type).withDesc("1 desc=test 2").asItem()));
		}
	}

	@Test
	public void testSCM() {
		assertFalse(scmRegex(".*").matches(jobOfType(TOP_LEVEL_ITEM).asItem()));

		for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET, SCMED_ITEM, SCM_TRIGGER_ITEM)) {
			assertFalse(scmRegex(".*my-office.*").matches(jobOfType(type).withCVS("root", "modules", "branch").asItem()));
			assertFalse(scmRegex(".*my-office.*").matches(jobOfType(type).withCVS(null, "modules", "branch").asItem()));
			assertFalse(scmRegex(".*my-office.*").matches(jobOfType(type).withCVS("root", "modules", null).asItem()));
			assertTrue(scmRegex(".*my-office.*").matches(jobOfType(type).withCVS("root/my-office", "modules", "branch").asItem()));
			assertTrue(scmRegex(".*my-office.*").matches(jobOfType(type).withCVS("root", "modules/my-office", "branch").asItem()));
			assertTrue(scmRegex(".*my-office.*").matches(jobOfType(type).withCVS("root", "modules", "branch/my-office").asItem()));

			assertFalse(scmRegex(".*").matches(jobOfType(type).withSVN().asItem()));
			assertTrue(scmRegex(".*").matches(jobOfType(type).withSVN("").asItem()));
			assertTrue(scmRegex("Foo").matches(jobOfType(type).withSVN("Foo").asItem()));
			assertTrue(scmRegex("Foo.*").matches(jobOfType(type).withSVN("Foobar").asItem()));
			assertFalse(scmRegex("bar").matches(jobOfType(type).withSVN("Foobar").asItem()));
			assertTrue(scmRegex(".*bar").matches(jobOfType(type).withSVN("Foobar").asItem()));
			assertTrue(scmRegex("Bar").matches(jobOfType(type).withSVN("Foo", "Bar").asItem()));
			assertTrue(scmRegex("B.*").matches(jobOfType(type).withSVN("Foo", "Bar").asItem()));

			assertFalse(scmRegex(".*").matches(jobOfType(type).withGitBranches().asItem()));
			assertTrue(scmRegex(".*").matches(jobOfType(type).withGitBranches("").asItem()));
			assertTrue(scmRegex("Foo").matches(jobOfType(type).withGitBranches("Foo").asItem()));
			assertTrue(scmRegex("Foo.*").matches(jobOfType(type).withGitBranches("Foobar").asItem()));
			assertFalse(scmRegex("bar").matches(jobOfType(type).withGitBranches("Foobar").asItem()));
			assertTrue(scmRegex(".*bar").matches(jobOfType(type).withGitBranches("Foobar").asItem()));
			assertTrue(scmRegex("Bar").matches(jobOfType(type).withGitBranches("Foo", "Bar").asItem()));
			assertTrue(scmRegex("B.*").matches(jobOfType(type).withGitBranches("Foo", "Bar").asItem()));

			assertFalse(scmRegex(".*").matches(jobOfType(type).withGitRepos().asItem()));
			assertTrue(scmRegex(".*").matches(jobOfType(type).withGitRepos("").asItem()));
			assertTrue(scmRegex("Foo").matches(jobOfType(type).withGitRepos("Foo").asItem()));
			assertTrue(scmRegex("Foo.*").matches(jobOfType(type).withGitRepos("Foobar").asItem()));
			assertFalse(scmRegex("bar").matches(jobOfType(type).withGitRepos("Foobar").asItem()));
			assertTrue(scmRegex(".*bar").matches(jobOfType(type).withGitRepos("Foobar").asItem()));
			assertTrue(scmRegex("Bar").matches(jobOfType(type).withGitRepos("Foo", "Bar").asItem()));
			assertTrue(scmRegex("B.*").matches(jobOfType(type).withGitRepos("Foo", "Bar").asItem()));

			assertFalse(scmRegex(".*").matches(jobOfType(type).withLegacyGitRepos().asItem()));
			assertTrue(scmRegex(".*").matches(jobOfType(type).withLegacyGitRepos("").asItem()));
			assertTrue(scmRegex("Foo").matches(jobOfType(type).withLegacyGitRepos("Foo").asItem()));
			assertTrue(scmRegex("Foo.*").matches(jobOfType(type).withLegacyGitRepos("Foobar").asItem()));
			assertFalse(scmRegex("bar").matches(jobOfType(type).withLegacyGitRepos("Foobar").asItem()));
			assertTrue(scmRegex(".*bar").matches(jobOfType(type).withLegacyGitRepos("Foobar").asItem()));
			assertTrue(scmRegex("Bar").matches(jobOfType(type).withLegacyGitRepos("Foo", "Bar").asItem()));
			assertTrue(scmRegex("B.*").matches(jobOfType(type).withLegacyGitRepos("Foo", "Bar").asItem()));
		}
	}

	@Test
	public void testEmail() {
		assertFalse(emailRegex(".*").matches(jobOfType(TOP_LEVEL_ITEM).asItem()));

		for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET)) {
			assertFalse(emailRegex(".*").matches(jobOfType(type).withEmail(null, DEFAULT).asItem()));
			assertTrue(emailRegex(".*").matches(jobOfType(type).withEmail("", DEFAULT).asItem()));
			assertTrue(emailRegex(".*").matches(jobOfType(type).withEmail("foo@bar.com, quux@baz.net", DEFAULT).asItem()));
			assertTrue(emailRegex("foo@bar.com").matches(jobOfType(type).withEmail("foo@bar.com", DEFAULT).asItem()));
			assertFalse(emailRegex("foo").matches(jobOfType(type).withEmail("foo@bar.com", DEFAULT).asItem()));
			assertFalse(emailRegex("@bar.com").matches(jobOfType(type).withEmail("foo@bar.com", DEFAULT).asItem()));
			assertTrue(emailRegex("foo@.*").matches(jobOfType(type).withEmail("foo@bar.com", DEFAULT).asItem()));
			assertTrue(emailRegex(".*@bar.com").matches(jobOfType(type).withEmail("foo@bar.com", DEFAULT).asItem()));

			assertFalse(emailRegex(".*").matches(jobOfType(type).withEmail(null, EXTENDED).asItem()));
			assertTrue(emailRegex(".*").matches(jobOfType(type).withEmail("", EXTENDED).asItem()));
			assertTrue(emailRegex(".*").matches(jobOfType(type).withEmail("foo@bar.com, quux@baz.net", EXTENDED).asItem()));
			assertTrue(emailRegex("foo@bar.com").matches(jobOfType(type).withEmail("foo@bar.com", EXTENDED).asItem()));
			assertFalse(emailRegex("foo").matches(jobOfType(type).withEmail("foo@bar.com", EXTENDED).asItem()));
			assertFalse(emailRegex("@bar.com").matches(jobOfType(type).withEmail("foo@bar.com", EXTENDED).asItem()));
			assertTrue(emailRegex("foo@.*").matches(jobOfType(type).withEmail("foo@bar.com", EXTENDED).asItem()));
			assertTrue(emailRegex(".*@bar.com").matches(jobOfType(type).withEmail("foo@bar.com", EXTENDED).asItem()));
		}
	}

	@Test
	public void testSchedule() {
		assertFalse(scheduleRegex(".*").matches(jobOfType(TOP_LEVEL_ITEM).asItem()));

		for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET)) {
			assertFalse(scheduleRegex(".*").matches(jobOfType(type).withTrigger(null).asItem()));
			assertTrue(scheduleRegex(".*").matches(jobOfType(type).withTrigger("").asItem()));
			assertTrue(scheduleRegex(".*").matches(jobOfType(type).withTrigger("\n").asItem()));
			assertTrue(scheduleRegex(".*monday.*").matches(jobOfType(type).withTrigger("# monday").asItem()));
			assertFalse(scheduleRegex(".*monday.*").matches(jobOfType(type).withTrigger("# tuesday").asItem()));
			assertFalse(scheduleRegex(".*monday.*").matches(jobOfType(type).withTrigger("* * * * *").asItem()));
			assertFalse(scheduleRegex(".*monday.*").matches(jobOfType(type).withTrigger("* * * * *\n#").asItem()));
			assertTrue(scheduleRegex(".*monday.*").matches(jobOfType(type).withTrigger("#monday\n* * * * *").asItem()));
		}
	}

	@Test
	public void testMaven() {
		assertFalse(mavenRegex(".*").matches(jobOfType(TOP_LEVEL_ITEM).asItem()));

		for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET)) {
			assertTrue(mavenRegex(".*").matches(jobOfType(type).withMavenBuilder("", "", "", "").asItem()));
			assertTrue(mavenRegex("Foo").matches(jobOfType(type).withMavenBuilder("Foo", "", "", "").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOfType(type).withMavenBuilder("Foo", "", "", "").asItem()));
			assertTrue(mavenRegex("Foo.*").matches(jobOfType(type).withMavenBuilder("Foobar", "", "", "").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOfType(type).withMavenBuilder("Foobar", "", "", "").asItem()));
			assertTrue(mavenRegex(".*bar").matches(jobOfType(type).withMavenBuilder("Foobar", "", "", "").asItem()));

			assertTrue(mavenRegex("Foo bar").matches(jobOfType(type).withMavenBuilder("Foo\nbar", "", "", "").asItem()));
			assertFalse(mavenRegex("Foo").matches(jobOfType(type).withMavenBuilder("Foo\nbar", "", "", "").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOfType(type).withMavenBuilder("Foo\nbar", "", "", "").asItem()));
			assertTrue(mavenRegex("Foo.*").matches(jobOfType(type).withMavenBuilder("Foo\nbar", "", "", "").asItem()));
			assertTrue(mavenRegex(".*bar").matches(jobOfType(type).withMavenBuilder("Foo\nbar", "", "", "").asItem()));

			assertTrue(mavenRegex(".*").matches(jobOfType(type).withMavenBuilder("", "", "", "").asItem()));
			assertTrue(mavenRegex("Foo").matches(jobOfType(type).withMavenBuilder("", "Foo", "", "").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOfType(type).withMavenBuilder("", "Foo", "", "").asItem()));
			assertTrue(mavenRegex("Foo.*").matches(jobOfType(type).withMavenBuilder("", "Foobar", "", "").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOfType(type).withMavenBuilder("", "Foobar", "", "").asItem()));
			assertTrue(mavenRegex(".*bar").matches(jobOfType(type).withMavenBuilder("", "Foobar", "", "").asItem()));

			assertTrue(mavenRegex(".*").matches(jobOfType(type).withMavenBuilder("", "", "", "").asItem()));
			assertTrue(mavenRegex("Foo").matches(jobOfType(type).withMavenBuilder("", "", "Foo", "").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOfType(type).withMavenBuilder("", "", "Foo", "").asItem()));
			assertTrue(mavenRegex("Foo.*").matches(jobOfType(type).withMavenBuilder("", "", "Foobar", "").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOfType(type).withMavenBuilder("", "", "Foobar", "").asItem()));
			assertTrue(mavenRegex(".*bar").matches(jobOfType(type).withMavenBuilder("", "", "Foobar", "").asItem()));

			assertTrue(mavenRegex(".*").matches(jobOfType(type).withMavenBuilder("", "", "", "").asItem()));
			assertTrue(mavenRegex("Foo").matches(jobOfType(type).withMavenBuilder("", "", "", "Foo").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOfType(type).withMavenBuilder("", "", "", "Foo").asItem()));
			assertTrue(mavenRegex("Foo.*").matches(jobOfType(type).withMavenBuilder("", "", "", "Foobar").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOfType(type).withMavenBuilder("", "", "", "Foobar").asItem()));
			assertTrue(mavenRegex(".*bar").matches(jobOfType(type).withMavenBuilder("", "", "", "Foobar").asItem()));
		}

		for (JobType<? extends Job> type: availableJobTypes(MAVEN_MODULE_SET)) {
			for (JobMocker.MavenBuildStep step : JobMocker.MavenBuildStep.values()) {
				assertTrue(mavenRegex(".*").matches(jobOfType(type).withMavenBuildStep(step, "", "", "", "").asItem()));
				assertTrue(mavenRegex("Foo").matches(jobOfType(type).withMavenBuildStep(step, "Foo", "", "", "").asItem()));
				assertFalse(mavenRegex("bar").matches(jobOfType(type).withMavenBuildStep(step, "Foo", "", "", "").asItem()));
				assertTrue(mavenRegex("Foo.*").matches(jobOfType(type).withMavenBuildStep(step, "Foobar", "", "", "").asItem()));
				assertFalse(mavenRegex("bar").matches(jobOfType(type).withMavenBuildStep(step, "Foobar", "", "", "").asItem()));
				assertTrue(mavenRegex(".*bar").matches(jobOfType(type).withMavenBuildStep(step, "Foobar", "", "", "").asItem()));

				assertTrue(mavenRegex("Foo bar").matches(jobOfType(type).withMavenBuildStep(step, "Foo\nbar", "", "", "").asItem()));
				assertFalse(mavenRegex("Foo").matches(jobOfType(type).withMavenBuildStep(step, "Foo\nbar", "", "", "").asItem()));
				assertFalse(mavenRegex("bar").matches(jobOfType(type).withMavenBuildStep(step, "Foo\nbar", "", "", "").asItem()));
				assertTrue(mavenRegex("Foo.*").matches(jobOfType(type).withMavenBuildStep(step, "Foo\nbar", "", "", "").asItem()));
				assertTrue(mavenRegex(".*bar").matches(jobOfType(type).withMavenBuildStep(step, "Foo\nbar", "", "", "").asItem()));

				assertTrue(mavenRegex(".*").matches(jobOfType(type).withMavenBuildStep(step, "", "", "", "").asItem()));
				assertTrue(mavenRegex("Foo").matches(jobOfType(type).withMavenBuildStep(step, "", "Foo", "", "").asItem()));
				assertFalse(mavenRegex("bar").matches(jobOfType(type).withMavenBuildStep(step, "", "Foo", "", "").asItem()));
				assertTrue(mavenRegex("Foo.*").matches(jobOfType(type).withMavenBuildStep(step, "", "Foobar", "", "").asItem()));
				assertFalse(mavenRegex("bar").matches(jobOfType(type).withMavenBuildStep(step, "", "Foobar", "", "").asItem()));
				assertTrue(mavenRegex(".*bar").matches(jobOfType(type).withMavenBuildStep(step, "", "Foobar", "", "").asItem()));

				assertTrue(mavenRegex(".*").matches(jobOfType(type).withMavenBuildStep(step, "", "", "", "").asItem()));
				assertTrue(mavenRegex("Foo").matches(jobOfType(type).withMavenBuildStep(step, "", "", "Foo", "").asItem()));
				assertFalse(mavenRegex("bar").matches(jobOfType(type).withMavenBuildStep(step, "", "", "Foo", "").asItem()));
				assertTrue(mavenRegex("Foo.*").matches(jobOfType(type).withMavenBuildStep(step, "", "", "Foobar", "").asItem()));
				assertFalse(mavenRegex("bar").matches(jobOfType(type).withMavenBuildStep(step, "", "", "Foobar", "").asItem()));
				assertTrue(mavenRegex(".*bar").matches(jobOfType(type).withMavenBuildStep(step, "", "", "Foobar", "").asItem()));

				assertTrue(mavenRegex(".*").matches(jobOfType(type).withMavenBuildStep(step, "", "", "", "").asItem()));
				assertTrue(mavenRegex("Foo").matches(jobOfType(type).withMavenBuildStep(step, "", "", "", "Foo").asItem()));
				assertFalse(mavenRegex("bar").matches(jobOfType(type).withMavenBuildStep(step, "", "", "", "Foo").asItem()));
				assertTrue(mavenRegex("Foo.*").matches(jobOfType(type).withMavenBuildStep(step, "", "", "", "Foobar").asItem()));
				assertFalse(mavenRegex("bar").matches(jobOfType(type).withMavenBuildStep(step, "", "", "", "Foobar").asItem()));
				assertTrue(mavenRegex(".*bar").matches(jobOfType(type).withMavenBuildStep(step, "", "", "", "Foobar").asItem()));
			}
		}
	}

	@Test
	public void testNode() {
		assertFalse(nodeRegex(".*").matches(jobOfType(TOP_LEVEL_ITEM).asItem()));

		for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET)) {
			assertTrue(nodeRegex(".*").matches(jobOfType(type).withAssignedLabel("").asItem()));
			assertTrue(nodeRegex("Foo").matches(jobOfType(type).withAssignedLabel("Foo").asItem()));
			assertFalse(nodeRegex("bar").matches(jobOfType(type).withAssignedLabel("Foo").asItem()));
			assertTrue(nodeRegex("Foo.*").matches(jobOfType(type).withAssignedLabel("Foobar").asItem()));
			assertFalse(nodeRegex("bar").matches(jobOfType(type).withAssignedLabel("Foobar").asItem()));
			assertTrue(nodeRegex(".*bar").matches(jobOfType(type).withAssignedLabel("Foobar").asItem()));
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

	/*
	 * Tests that the example given in the wiki works as described.
	 *
	 * https://wiki.jenkins.io/display/JENKINS/Using+the+View+Job+Filters+Match+Type
	 */
	@Test
	public void testHelpExample() {
		List<TopLevelItem> all = asList(
			jobOfType(FREE_STYLE_PROJECT).withName("0-Test_Job").asItem(),
			jobOfType(FREE_STYLE_PROJECT).withName("1-Test_Job").withTrigger("@midnight").asItem(),
			jobOfType(FREE_STYLE_PROJECT).withName("2-Job").asItem(),
			jobOfType(FREE_STYLE_PROJECT).withName("3-Test_Job").withTrigger("@daily").asItem(),
			jobOfType(FREE_STYLE_PROJECT).withName("4-Job").withTrigger("@midnight").asItem(),
			jobOfType(FREE_STYLE_PROJECT).withName("5-Test_Job").withTrigger("@midnight").asItem(),
			jobOfType(FREE_STYLE_PROJECT).withName("6-Test_Job").asItem()
		);

		List<TopLevelItem> filtered = new ArrayList<TopLevelItem>();

		RegExJobFilter includeTests = new RegExJobFilter(".*Test.*", includeMatched.name(), NAME.name());
		filtered = includeTests.filter(filtered, all, null);
		assertThat(filtered, is(asList(
			all.get(0),
			all.get(1),
			all.get(3),
			all.get(5),
			all.get(6)
		)));

		RegExJobFilter excludeNonNightly = new RegExJobFilter(".*@midnight.*", excludeUnmatched.name(), SCHEDULE.name());
		filtered = excludeNonNightly.filter(filtered, all, null);
		assertThat(filtered, is(asList(
			all.get(1),
			all.get(5)
		)));
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
