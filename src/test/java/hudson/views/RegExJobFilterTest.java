package hudson.views;

import hudson.model.*;

import hudson.views.test.JobMocker;
import hudson.views.test.JobType;
import org.junit.Test;
import org.jvnet.hudson.test.WithoutJenkins;

import java.util.ArrayList;
import java.util.List;

import static hudson.views.test.JobMocker.EmailType.DEFAULT;
import static hudson.views.test.JobMocker.EmailType.EXTENDED;
import static hudson.views.test.JobMocker.freeStyleProject;
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
	@WithoutJenkins
	public void testName() {
		assertFalse(nameRegex(".*").matches(jobOfType(TOP_LEVEL_ITEM).asItem()));

	    for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET)) {
			assertFalse(nameRegex(".*").matches(jobOfType(type).name(null).asItem()));
			assertTrue(nameRegex(".*").matches(jobOfType(type).name("").asItem()));
			assertTrue(nameRegex("Foo").matches(jobOfType(type).name("Foo").asItem()));
			assertFalse(nameRegex("Foo").matches(jobOfType(type).name("Foobar").asItem()));
			assertTrue(nameRegex("Foo.*").matches(jobOfType(type).name("Foobar").asItem()));
			assertFalse(nameRegex("bar").matches(jobOfType(type).name("Foobar").asItem()));
			assertTrue(nameRegex(".*bar").matches(jobOfType(type).name("Foobar").asItem()));
			assertTrue(nameRegex(".ooba.").matches(jobOfType(type).name("Foobar").asItem()));
		}
	}

	@Test
	@WithoutJenkins
	public void testDescription() {
		assertFalse(descRegex(".*").matches(jobOfType(TOP_LEVEL_ITEM).asItem()));

		for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET)) {
			assertFalse(descRegex(".*").matches(jobOfType(type).desc(null).asItem()));
			assertTrue(descRegex(".*").matches(jobOfType(type).desc("").asItem()));
			assertTrue(descRegex("Foo").matches(jobOfType(type).desc("Foo").asItem()));
			assertFalse(descRegex("Foo").matches(jobOfType(type).desc("Foobar").asItem()));
			assertTrue(descRegex("Foo.*").matches(jobOfType(type).desc("Foobar").asItem()));
			assertFalse(descRegex("bar").matches(jobOfType(type).desc("Foobar").asItem()));
			assertTrue(descRegex(".*bar").matches(jobOfType(type).desc("Foobar").asItem()));
			assertTrue(descRegex(".ooba.").matches(jobOfType(type).desc("Foobar").asItem()));

			assertTrue(descRegex(".*").matches(jobOfType(type).desc("\n").asItem()));
			assertTrue(descRegex("Foo").matches(jobOfType(type).desc("Quux\nFoo").asItem()));
			assertFalse(descRegex("Foo").matches(jobOfType(type).desc("Quux\nFoobar").asItem()));
			assertTrue(descRegex("Foo.*").matches(jobOfType(type).desc("Quux\nFoobar").asItem()));
			assertFalse(descRegex("bar").matches(jobOfType(type).desc("Quux\nFoobar").asItem()));
			assertTrue(descRegex(".*bar").matches(jobOfType(type).desc("Quux\nFoobar").asItem()));
			assertTrue(descRegex(".ooba.").matches(jobOfType(type).desc("Quux\nFoobar").asItem()));

			assertFalse(descRegex(".*desc=test.*").matches(jobOfType(type).desc("").asItem()));
			assertFalse(descRegex(".*desc=test.*").matches(jobOfType(type).desc(null).asItem()));
			assertFalse(descRegex(".*desc=test.*").matches(jobOfType(type).desc("nothing").asItem()));
			assertTrue(descRegex(".*desc=test.*").matches(jobOfType(type).desc("desc=test").asItem()));
			assertTrue(descRegex(".*desc=test.*").matches(jobOfType(type).desc("mydesc=test2").asItem()));
			assertTrue(descRegex(".*desc=test.*").matches(jobOfType(type).desc("thisis\nmydesc=testn2\nforyou").asItem()));
			assertTrue(descRegex(".*desc=test.*").matches(jobOfType(type).desc("1&#xd;\ndesc=test&#xd;\n2").asItem()));
			assertTrue(descRegex(".*desc=test.*").matches(jobOfType(type).desc("1 desc=test 2").asItem()));
		}
	}

	@Test
	public void testSCM() {
		assertFalse(scmRegex(".*").matches(jobOfType(TOP_LEVEL_ITEM).asItem()));

		for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET, SCMED_ITEM, SCM_TRIGGER_ITEM)) {
			assertFalse(scmRegex(".*my-office.*").matches(jobOfType(type).cvs("root", "modules", "branch").asItem()));
			assertFalse(scmRegex(".*my-office.*").matches(jobOfType(type).cvs(null, "modules", "branch").asItem()));
			assertFalse(scmRegex(".*my-office.*").matches(jobOfType(type).cvs("root", "modules", null).asItem()));
			assertTrue(scmRegex(".*my-office.*").matches(jobOfType(type).cvs("root/my-office", "modules", "branch").asItem()));
			assertTrue(scmRegex(".*my-office.*").matches(jobOfType(type).cvs("root", "modules/my-office", "branch").asItem()));
			assertTrue(scmRegex(".*my-office.*").matches(jobOfType(type).cvs("root", "modules", "branch/my-office").asItem()));

			assertFalse(scmRegex(".*").matches(jobOfType(type).svn().asItem()));
			assertTrue(scmRegex(".*").matches(jobOfType(type).svn("").asItem()));
			assertTrue(scmRegex("Foo").matches(jobOfType(type).svn("Foo").asItem()));
			assertTrue(scmRegex("Foo.*").matches(jobOfType(type).svn("Foobar").asItem()));
			assertFalse(scmRegex("bar").matches(jobOfType(type).svn("Foobar").asItem()));
			assertTrue(scmRegex(".*bar").matches(jobOfType(type).svn("Foobar").asItem()));
			assertTrue(scmRegex("Bar").matches(jobOfType(type).svn("Foo", "Bar").asItem()));
			assertTrue(scmRegex("B.*").matches(jobOfType(type).svn("Foo", "Bar").asItem()));

			assertFalse(scmRegex(".*").matches(jobOfType(type).gitBranches().asItem()));
			assertTrue(scmRegex(".*").matches(jobOfType(type).gitBranches("").asItem()));
			assertTrue(scmRegex("Foo").matches(jobOfType(type).gitBranches("Foo").asItem()));
			assertTrue(scmRegex("Foo.*").matches(jobOfType(type).gitBranches("Foobar").asItem()));
			assertFalse(scmRegex("bar").matches(jobOfType(type).gitBranches("Foobar").asItem()));
			assertTrue(scmRegex(".*bar").matches(jobOfType(type).gitBranches("Foobar").asItem()));
			assertTrue(scmRegex("Bar").matches(jobOfType(type).gitBranches("Foo", "Bar").asItem()));
			assertTrue(scmRegex("B.*").matches(jobOfType(type).gitBranches("Foo", "Bar").asItem()));

			assertFalse(scmRegex(".*").matches(jobOfType(type).gitRepos().asItem()));
			assertTrue(scmRegex(".*").matches(jobOfType(type).gitRepos("").asItem()));
			assertTrue(scmRegex("Foo").matches(jobOfType(type).gitRepos("Foo").asItem()));
			assertTrue(scmRegex("Foo.*").matches(jobOfType(type).gitRepos("Foobar").asItem()));
			assertFalse(scmRegex("bar").matches(jobOfType(type).gitRepos("Foobar").asItem()));
			assertTrue(scmRegex(".*bar").matches(jobOfType(type).gitRepos("Foobar").asItem()));
			assertTrue(scmRegex("Bar").matches(jobOfType(type).gitRepos("Foo", "Bar").asItem()));
			assertTrue(scmRegex("B.*").matches(jobOfType(type).gitRepos("Foo", "Bar").asItem()));

			assertFalse(scmRegex(".*").matches(jobOfType(type).gitReposLegacy().asItem()));
			assertTrue(scmRegex(".*").matches(jobOfType(type).gitReposLegacy("").asItem()));
			assertTrue(scmRegex("Foo").matches(jobOfType(type).gitReposLegacy("Foo").asItem()));
			assertTrue(scmRegex("Foo.*").matches(jobOfType(type).gitReposLegacy("Foobar").asItem()));
			assertFalse(scmRegex("bar").matches(jobOfType(type).gitReposLegacy("Foobar").asItem()));
			assertTrue(scmRegex(".*bar").matches(jobOfType(type).gitReposLegacy("Foobar").asItem()));
			assertTrue(scmRegex("Bar").matches(jobOfType(type).gitReposLegacy("Foo", "Bar").asItem()));
			assertTrue(scmRegex("B.*").matches(jobOfType(type).gitReposLegacy("Foo", "Bar").asItem()));
		}
	}

	@Test
	public void testEmail() {
		assertFalse(emailRegex(".*").matches(jobOfType(TOP_LEVEL_ITEM).asItem()));

		for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET)) {
			assertFalse(emailRegex(".*").matches(jobOfType(type).email(null, DEFAULT).asItem()));
			assertTrue(emailRegex(".*").matches(jobOfType(type).email("", DEFAULT).asItem()));
			assertTrue(emailRegex(".*").matches(jobOfType(type).email("foo@bar.com, quux@baz.net", DEFAULT).asItem()));
			assertTrue(emailRegex("foo@bar.com").matches(jobOfType(type).email("foo@bar.com", DEFAULT).asItem()));
			assertFalse(emailRegex("foo").matches(jobOfType(type).email("foo@bar.com", DEFAULT).asItem()));
			assertFalse(emailRegex("@bar.com").matches(jobOfType(type).email("foo@bar.com", DEFAULT).asItem()));
			assertTrue(emailRegex("foo@.*").matches(jobOfType(type).email("foo@bar.com", DEFAULT).asItem()));
			assertTrue(emailRegex(".*@bar.com").matches(jobOfType(type).email("foo@bar.com", DEFAULT).asItem()));

			assertFalse(emailRegex(".*").matches(jobOfType(type).email(null, EXTENDED).asItem()));
			assertTrue(emailRegex(".*").matches(jobOfType(type).email("", EXTENDED).asItem()));
			assertTrue(emailRegex(".*").matches(jobOfType(type).email("foo@bar.com, quux@baz.net", EXTENDED).asItem()));
			assertTrue(emailRegex("foo@bar.com").matches(jobOfType(type).email("foo@bar.com", EXTENDED).asItem()));
			assertFalse(emailRegex("foo").matches(jobOfType(type).email("foo@bar.com", EXTENDED).asItem()));
			assertFalse(emailRegex("@bar.com").matches(jobOfType(type).email("foo@bar.com", EXTENDED).asItem()));
			assertTrue(emailRegex("foo@.*").matches(jobOfType(type).email("foo@bar.com", EXTENDED).asItem()));
			assertTrue(emailRegex(".*@bar.com").matches(jobOfType(type).email("foo@bar.com", EXTENDED).asItem()));
		}
	}

	@Test
	@WithoutJenkins
	public void testSchedule() {
		assertFalse(scheduleRegex(".*").matches(jobOfType(TOP_LEVEL_ITEM).asItem()));

		for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET)) {
			assertFalse(scheduleRegex(".*").matches(jobOfType(type).trigger(null).asItem()));
			assertTrue(scheduleRegex(".*").matches(jobOfType(type).trigger("").asItem()));
			assertTrue(scheduleRegex(".*").matches(jobOfType(type).trigger("\n").asItem()));
			assertTrue(scheduleRegex(".*monday.*").matches(jobOfType(type).trigger("# monday").asItem()));
			assertFalse(scheduleRegex(".*monday.*").matches(jobOfType(type).trigger("# tuesday").asItem()));
			assertFalse(scheduleRegex(".*monday.*").matches(jobOfType(type).trigger("* * * * *").asItem()));
			assertFalse(scheduleRegex(".*monday.*").matches(jobOfType(type).trigger("* * * * *\n#").asItem()));
			assertTrue(scheduleRegex(".*monday.*").matches(jobOfType(type).trigger("#monday\n* * * * *").asItem()));
		}
	}

	@Test
	public void testMaven() {
		assertFalse(mavenRegex(".*").matches(jobOfType(TOP_LEVEL_ITEM).asItem()));

		for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET)) {
			assertTrue(mavenRegex(".*").matches(jobOfType(type).mavenBuilder("", "", "", "").asItem()));
			assertTrue(mavenRegex("Foo").matches(jobOfType(type).mavenBuilder("Foo", "", "", "").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOfType(type).mavenBuilder("Foo", "", "", "").asItem()));
			assertTrue(mavenRegex("Foo.*").matches(jobOfType(type).mavenBuilder("Foobar", "", "", "").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOfType(type).mavenBuilder("Foobar", "", "", "").asItem()));
			assertTrue(mavenRegex(".*bar").matches(jobOfType(type).mavenBuilder("Foobar", "", "", "").asItem()));

			assertTrue(mavenRegex("Foo bar").matches(jobOfType(type).mavenBuilder("Foo\nbar", "", "", "").asItem()));
			assertFalse(mavenRegex("Foo").matches(jobOfType(type).mavenBuilder("Foo\nbar", "", "", "").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOfType(type).mavenBuilder("Foo\nbar", "", "", "").asItem()));
			assertTrue(mavenRegex("Foo.*").matches(jobOfType(type).mavenBuilder("Foo\nbar", "", "", "").asItem()));
			assertTrue(mavenRegex(".*bar").matches(jobOfType(type).mavenBuilder("Foo\nbar", "", "", "").asItem()));

			assertTrue(mavenRegex(".*").matches(jobOfType(type).mavenBuilder("", "", "", "").asItem()));
			assertTrue(mavenRegex("Foo").matches(jobOfType(type).mavenBuilder("", "Foo", "", "").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOfType(type).mavenBuilder("", "Foo", "", "").asItem()));
			assertTrue(mavenRegex("Foo.*").matches(jobOfType(type).mavenBuilder("", "Foobar", "", "").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOfType(type).mavenBuilder("", "Foobar", "", "").asItem()));
			assertTrue(mavenRegex(".*bar").matches(jobOfType(type).mavenBuilder("", "Foobar", "", "").asItem()));

			assertTrue(mavenRegex(".*").matches(jobOfType(type).mavenBuilder("", "", "", "").asItem()));
			assertTrue(mavenRegex("Foo").matches(jobOfType(type).mavenBuilder("", "", "Foo", "").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOfType(type).mavenBuilder("", "", "Foo", "").asItem()));
			assertTrue(mavenRegex("Foo.*").matches(jobOfType(type).mavenBuilder("", "", "Foobar", "").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOfType(type).mavenBuilder("", "", "Foobar", "").asItem()));
			assertTrue(mavenRegex(".*bar").matches(jobOfType(type).mavenBuilder("", "", "Foobar", "").asItem()));

			assertTrue(mavenRegex(".*").matches(jobOfType(type).mavenBuilder("", "", "", "").asItem()));
			assertTrue(mavenRegex("Foo").matches(jobOfType(type).mavenBuilder("", "", "", "Foo").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOfType(type).mavenBuilder("", "", "", "Foo").asItem()));
			assertTrue(mavenRegex("Foo.*").matches(jobOfType(type).mavenBuilder("", "", "", "Foobar").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOfType(type).mavenBuilder("", "", "", "Foobar").asItem()));
			assertTrue(mavenRegex(".*bar").matches(jobOfType(type).mavenBuilder("", "", "", "Foobar").asItem()));
		}

		for (JobType<? extends Job> type: availableJobTypes(MAVEN_MODULE_SET)) {
			for (JobMocker.MavenBuildStep step : JobMocker.MavenBuildStep.values()) {
				assertTrue(mavenRegex(".*").matches(jobOfType(type).mavenBuildStep(step, "", "", "", "").asItem()));
				assertTrue(mavenRegex("Foo").matches(jobOfType(type).mavenBuildStep(step, "Foo", "", "", "").asItem()));
				assertFalse(mavenRegex("bar").matches(jobOfType(type).mavenBuildStep(step, "Foo", "", "", "").asItem()));
				assertTrue(mavenRegex("Foo.*").matches(jobOfType(type).mavenBuildStep(step, "Foobar", "", "", "").asItem()));
				assertFalse(mavenRegex("bar").matches(jobOfType(type).mavenBuildStep(step, "Foobar", "", "", "").asItem()));
				assertTrue(mavenRegex(".*bar").matches(jobOfType(type).mavenBuildStep(step, "Foobar", "", "", "").asItem()));

				assertTrue(mavenRegex("Foo bar").matches(jobOfType(type).mavenBuildStep(step, "Foo\nbar", "", "", "").asItem()));
				assertFalse(mavenRegex("Foo").matches(jobOfType(type).mavenBuildStep(step, "Foo\nbar", "", "", "").asItem()));
				assertFalse(mavenRegex("bar").matches(jobOfType(type).mavenBuildStep(step, "Foo\nbar", "", "", "").asItem()));
				assertTrue(mavenRegex("Foo.*").matches(jobOfType(type).mavenBuildStep(step, "Foo\nbar", "", "", "").asItem()));
				assertTrue(mavenRegex(".*bar").matches(jobOfType(type).mavenBuildStep(step, "Foo\nbar", "", "", "").asItem()));

				assertTrue(mavenRegex(".*").matches(jobOfType(type).mavenBuildStep(step, "", "", "", "").asItem()));
				assertTrue(mavenRegex("Foo").matches(jobOfType(type).mavenBuildStep(step, "", "Foo", "", "").asItem()));
				assertFalse(mavenRegex("bar").matches(jobOfType(type).mavenBuildStep(step, "", "Foo", "", "").asItem()));
				assertTrue(mavenRegex("Foo.*").matches(jobOfType(type).mavenBuildStep(step, "", "Foobar", "", "").asItem()));
				assertFalse(mavenRegex("bar").matches(jobOfType(type).mavenBuildStep(step, "", "Foobar", "", "").asItem()));
				assertTrue(mavenRegex(".*bar").matches(jobOfType(type).mavenBuildStep(step, "", "Foobar", "", "").asItem()));

				assertTrue(mavenRegex(".*").matches(jobOfType(type).mavenBuildStep(step, "", "", "", "").asItem()));
				assertTrue(mavenRegex("Foo").matches(jobOfType(type).mavenBuildStep(step, "", "", "Foo", "").asItem()));
				assertFalse(mavenRegex("bar").matches(jobOfType(type).mavenBuildStep(step, "", "", "Foo", "").asItem()));
				assertTrue(mavenRegex("Foo.*").matches(jobOfType(type).mavenBuildStep(step, "", "", "Foobar", "").asItem()));
				assertFalse(mavenRegex("bar").matches(jobOfType(type).mavenBuildStep(step, "", "", "Foobar", "").asItem()));
				assertTrue(mavenRegex(".*bar").matches(jobOfType(type).mavenBuildStep(step, "", "", "Foobar", "").asItem()));

				assertTrue(mavenRegex(".*").matches(jobOfType(type).mavenBuildStep(step, "", "", "", "").asItem()));
				assertTrue(mavenRegex("Foo").matches(jobOfType(type).mavenBuildStep(step, "", "", "", "Foo").asItem()));
				assertFalse(mavenRegex("bar").matches(jobOfType(type).mavenBuildStep(step, "", "", "", "Foo").asItem()));
				assertTrue(mavenRegex("Foo.*").matches(jobOfType(type).mavenBuildStep(step, "", "", "", "Foobar").asItem()));
				assertFalse(mavenRegex("bar").matches(jobOfType(type).mavenBuildStep(step, "", "", "", "Foobar").asItem()));
				assertTrue(mavenRegex(".*bar").matches(jobOfType(type).mavenBuildStep(step, "", "", "", "Foobar").asItem()));
			}
		}
	}

	@Test
	@WithoutJenkins
	public void testNode() {
		assertFalse(nodeRegex(".*").matches(jobOfType(TOP_LEVEL_ITEM).asItem()));

		for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET)) {
			assertTrue(nodeRegex(".*").matches(jobOfType(type).assignedLabel("").asItem()));
			assertTrue(nodeRegex("Foo").matches(jobOfType(type).assignedLabel("Foo").asItem()));
			assertFalse(nodeRegex("bar").matches(jobOfType(type).assignedLabel("Foo").asItem()));
			assertTrue(nodeRegex("Foo.*").matches(jobOfType(type).assignedLabel("Foobar").asItem()));
			assertFalse(nodeRegex("bar").matches(jobOfType(type).assignedLabel("Foobar").asItem()));
			assertTrue(nodeRegex(".*bar").matches(jobOfType(type).assignedLabel("Foobar").asItem()));
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
			freeStyleProject().name("0-Test_Job").asItem(),
			freeStyleProject().name("1-Test_Job").trigger("@midnight").asItem(),
			freeStyleProject().name("2-Job").asItem(),
			freeStyleProject().name("3-Test_Job").trigger("@daily").asItem(),
			freeStyleProject().name("4-Job").trigger("@midnight").asItem(),
			freeStyleProject().name("5-Test_Job").trigger("@midnight").asItem(),
			freeStyleProject().name("6-Test_Job").asItem()
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
