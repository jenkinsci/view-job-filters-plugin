package hudson.views;

import hudson.model.*;
import hudson.views.test.JobType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.excludeMatched;
import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched;
import static hudson.views.test.JobMocker.jobOfType;
import static hudson.views.test.JobType.*;
import static hudson.views.test.ViewJobFilters.buildStatus;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class BuildStatusFilterTest extends AbstractHudsonTest {

	@Test
	public void testMatch() {
		Build build = mock(Build.class);

		assertFalse(buildStatus(true, true, true).matches(mock(TopLevelItem.class)));

		for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET)) {
			assertTrue(buildStatus(true, false, false).matches(jobOfType(type).withLastBuild(null).asItem()));
			assertTrue(buildStatus(true, true, false).matches(jobOfType(type).withLastBuild(null).asItem()));
			assertTrue(buildStatus(true, false, true).matches(jobOfType(type).withLastBuild(null).asItem()));
			assertFalse(buildStatus(true, false, false).matches(jobOfType(type).withLastBuild(build).asItem()));
			assertFalse(buildStatus(false, false, false).matches(jobOfType(type).withLastBuild(null).asItem()));

			assertTrue(buildStatus(false, true, false).matches(jobOfType(type).isBuilding(true).withLastBuild(build).asItem()));
			assertTrue(buildStatus(true, true, false).matches(jobOfType(type).isBuilding(true).withLastBuild(build).asItem()));
			assertTrue(buildStatus(false, true, true).matches(jobOfType(type).isBuilding(true).withLastBuild(build).asItem()));
			assertFalse(buildStatus(false, true, false).matches(jobOfType(type).isBuilding(false).withLastBuild(build).asItem()));

			assertTrue(buildStatus(false, false, true).matches(jobOfType(type).isInQueue(true).withLastBuild(build).asItem()));
			assertTrue(buildStatus(true, false, true).matches(jobOfType(type).isInQueue(true).withLastBuild(build).asItem()));
			assertTrue(buildStatus(false, true, true).matches(jobOfType(type).isInQueue(true).withLastBuild(build).asItem()));
			assertFalse(buildStatus(false, false, true).matches(jobOfType(type).isInQueue(false).withLastBuild(build).asItem()));
		}
	}

	@Test
	public void testConfigRoundtrip() throws Exception {
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
		List<BuildStatusFilter> expectedFilters = new ArrayList<BuildStatusFilter>();
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

	private void assertFilterEquals(List<BuildStatusFilter> expectedFilters, List<ViewJobFilter> actualFilters) {
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
