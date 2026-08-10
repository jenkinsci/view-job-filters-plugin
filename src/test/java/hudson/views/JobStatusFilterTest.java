package hudson.views;

import hudson.model.Job;
import hudson.model.ListView;
import hudson.model.Result;
import hudson.model.TopLevelItem;
import hudson.views.test.JobType;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.WithoutJenkins;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import java.util.ArrayList;
import java.util.List;

import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.*;
import static hudson.views.test.JobMocker.jobOfType;
import static hudson.views.test.JobType.*;
import static hudson.views.test.ViewJobFilters.jobStatus;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;

@WithJenkins
class JobStatusFilterTest extends AbstractJenkinsTest {

	@Test
	@WithoutJenkins
	void testMatch() {
		assertFalse(jobStatus(true, true, true, true, true).matches(mock(TopLevelItem.class)));

		for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET, WORKFLOW_JOB)) {
			assertFalse(jobStatus(true, true, true, true, true).matches(jobOfType(type).asItem()));

			assertTrue(jobStatus(true, false, false, false, false).matches(jobOfType(type).result(Result.UNSTABLE).asItem()));
			assertFalse(jobStatus(true, false, false, false, false).matches(jobOfType(type).result(Result.FAILURE).asItem()));
			assertFalse(jobStatus(true, false, false, false, false).matches(jobOfType(type).result(Result.ABORTED).asItem()));
			assertFalse(jobStatus(true, false, false, false, false).matches(jobOfType(type).result(Result.SUCCESS).asItem()));
			assertFalse(jobStatus(true, false, false, false, false).matches(jobOfType(type).disabled(true).asItem()));

			assertFalse(jobStatus(false, true, false, false, false).matches(jobOfType(type).result(Result.UNSTABLE).asItem()));
			assertTrue(jobStatus(false, true, false, false, false).matches(jobOfType(type).result(Result.FAILURE).asItem()));
			assertFalse(jobStatus(false, true, false, false, false).matches(jobOfType(type).result(Result.ABORTED).asItem()));
			assertFalse(jobStatus(false, true, false, false, false).matches(jobOfType(type).result(Result.SUCCESS).asItem()));
			assertFalse(jobStatus(false, true, false, false, false).matches(jobOfType(type).disabled(true).asItem()));

			assertFalse(jobStatus(false, false, true, false, false).matches(jobOfType(type).result(Result.UNSTABLE).asItem()));
			assertFalse(jobStatus(false, false, true, false, false).matches(jobOfType(type).result(Result.FAILURE).asItem()));
			assertTrue(jobStatus(false, false, true, false, false).matches(jobOfType(type).result(Result.ABORTED).asItem()));
			assertFalse(jobStatus(false, false, true, false, false).matches(jobOfType(type).result(Result.SUCCESS).asItem()));
			assertFalse(jobStatus(false, false, true, false, false).matches(jobOfType(type).disabled(true).asItem()));

			assertFalse(jobStatus(false, false, false, false, true).matches(jobOfType(type).result(Result.UNSTABLE).asItem()));
			assertFalse(jobStatus(false, false, false, false, true).matches(jobOfType(type).result(Result.FAILURE).asItem()));
			assertFalse(jobStatus(false, false, false, false, true).matches(jobOfType(type).result(Result.ABORTED).asItem()));
			assertTrue(jobStatus(false, false, false, false, true).matches(jobOfType(type).result(Result.SUCCESS).asItem()));
			assertFalse(jobStatus(false, false, false, false, true).matches(jobOfType(type).disabled(true).asItem()));

			assertTrue(jobStatus(false, false, false, true, false).matches(jobOfType(type).disabled(true).asItem()), "works on " + type.getJobClass().getSimpleName());
			assertFalse(jobStatus(false, false, false, true, false).matches(jobOfType(type).disabled(false).asItem()));

			assertTrue(jobStatus(true, true, false, false, false).matches(jobOfType(type).result(Result.UNSTABLE).asItem()));
			assertTrue(jobStatus(true, true, false, false, false).matches(jobOfType(type).result(Result.FAILURE).asItem()));
			assertFalse(jobStatus(true, true, false, false, false).matches(jobOfType(type).result(Result.ABORTED).asItem()));
			assertFalse(jobStatus(true, true, false, false, false).matches(jobOfType(type).result(Result.SUCCESS).asItem()));
		}
	}

	@Test
	void testConfigRoundtrip() throws Exception {
		testConfigRoundtrip(
			"view-1",
			new JobStatusFilter(false, true, false, true, false,  excludeMatched.name())
		);

		testConfigRoundtrip(
			"view-2",
			new JobStatusFilter(true, false, true, false, true,  includeMatched.name()),
			new JobStatusFilter(true, true, false, false, false,  excludeMatched.name())
		);
	}

	private void testConfigRoundtrip(String viewName, JobStatusFilter... filters) throws Exception {
		List<JobStatusFilter> expectedFilters = new ArrayList<>();
		for (JobStatusFilter filter: filters) {
			expectedFilters.add(new JobStatusFilter(
				filter.isUnstable(),
				filter.isFailed(),
				filter.isAborted(),
				filter.isDisabled(),
				filter.isStable(),
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

	private static void assertFilterEquals(List<JobStatusFilter> expectedFilters, List<ViewJobFilter> actualFilters) {
		assertThat(actualFilters.size(), is(expectedFilters.size()));
		for (int i = 0; i < actualFilters.size(); i++) {
			ViewJobFilter actualFilter = actualFilters.get(i);
			JobStatusFilter expectedFilter = expectedFilters.get(i);
			assertThat(actualFilter, instanceOf(JobStatusFilter.class));
			assertThat(((JobStatusFilter)actualFilter).isAborted(), is(expectedFilter.isAborted()));
			assertThat(((JobStatusFilter)actualFilter).isDisabled(), is(expectedFilter.isDisabled()));
			assertThat(((JobStatusFilter)actualFilter).isFailed(), is(expectedFilter.isFailed()));
			assertThat(((JobStatusFilter)actualFilter).isStable(), is(expectedFilter.isStable()));
			assertThat(((JobStatusFilter)actualFilter).isUnstable(), is(expectedFilter.isUnstable()));
			assertThat(((JobStatusFilter)actualFilter).getIncludeExcludeTypeString(), is(expectedFilter.getIncludeExcludeTypeString()));
		}
	}

}
