package hudson.views;

import hudson.model.*;
import hudson.views.test.JobType;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.WithoutJenkins;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import java.util.ArrayList;
import java.util.List;

import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.excludeMatched;
import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched;
import static hudson.views.test.JobMocker.jobOfType;
import static hudson.views.test.JobType.*;
import static hudson.views.test.ViewJobFilters.buildStatus;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;

@WithJenkins
class BuildStatusFilterTest extends AbstractJenkinsTest {

	@Test
	@WithoutJenkins
	void testMatch() {
		Build build = mock(Build.class);

		assertFalse(buildStatus(true, true, true).matches(mock(TopLevelItem.class)));

		for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET)) {
			assertTrue(buildStatus(true, false, false).matches(jobOfType(type).lastBuild(null).asItem()));
			assertTrue(buildStatus(true, true, false).matches(jobOfType(type).lastBuild(null).asItem()));
			assertTrue(buildStatus(true, false, true).matches(jobOfType(type).lastBuild(null).asItem()));
			assertFalse(buildStatus(true, false, false).matches(jobOfType(type).lastBuild(build).asItem()));
			assertFalse(buildStatus(false, false, false).matches(jobOfType(type).lastBuild(null).asItem()));

			assertTrue(buildStatus(false, true, false).matches(jobOfType(type).building(true).lastBuild(build).asItem()));
			assertTrue(buildStatus(true, true, false).matches(jobOfType(type).building(true).lastBuild(build).asItem()));
			assertTrue(buildStatus(false, true, true).matches(jobOfType(type).building(true).lastBuild(build).asItem()));
			assertFalse(buildStatus(false, true, false).matches(jobOfType(type).building(false).lastBuild(build).asItem()));

			assertTrue(buildStatus(false, false, true).matches(jobOfType(type).inQueue(true).lastBuild(build).asItem()));
			assertTrue(buildStatus(true, false, true).matches(jobOfType(type).inQueue(true).lastBuild(build).asItem()));
			assertTrue(buildStatus(false, true, true).matches(jobOfType(type).inQueue(true).lastBuild(build).asItem()));
			assertFalse(buildStatus(false, false, true).matches(jobOfType(type).inQueue(false).lastBuild(build).asItem()));
		}
	}

	@Test
	void testConfigRoundtrip() throws Exception {
		testConfigRoundtrip(
			"view-1",
			new BuildStatusFilter(false, true, false, excludeMatched.name())
		);

		testConfigRoundtrip(
			"view-2",
			new BuildStatusFilter(true, false, true, includeMatched.name()),
			new BuildStatusFilter(true, true, false, excludeMatched.name())
		);
	}

	private void testConfigRoundtrip(String viewName, BuildStatusFilter... filters) throws Exception {
		List<BuildStatusFilter> expectedFilters = new ArrayList<>();
		for (BuildStatusFilter filter: filters) {
			expectedFilters.add(new BuildStatusFilter(
				filter.isNeverBuilt(),
				filter.isBuilding(),
				filter.isInBuildQueue(),
				filter.getIncludeExcludeTypeString()));
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

	private static void assertFilterEquals(List<BuildStatusFilter> expectedFilters, List<ViewJobFilter> actualFilters) {
		assertThat(actualFilters.size(), is(expectedFilters.size()));
		for (int i = 0; i < actualFilters.size(); i++) {
			ViewJobFilter actualFilter = actualFilters.get(i);
			BuildStatusFilter expectedFilter = expectedFilters.get(i);
			assertThat(actualFilter, instanceOf(BuildStatusFilter.class));
			assertThat(((BuildStatusFilter)actualFilter).isNeverBuilt(), is(expectedFilter.isNeverBuilt()));
			assertThat(((BuildStatusFilter)actualFilter).isBuilding(), is(expectedFilter.isBuilding()));
			assertThat(((BuildStatusFilter)actualFilter).isInBuildQueue(), is(expectedFilter.isInBuildQueue()));
			assertThat(((BuildStatusFilter)actualFilter).getIncludeExcludeTypeString(), is(expectedFilter.getIncludeExcludeTypeString()));
		}
	}

}
