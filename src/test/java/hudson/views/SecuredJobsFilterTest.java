package hudson.views;

import hudson.model.*;
import hudson.security.AuthorizationMatrixProperty;
import hudson.views.test.JobType;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.WithoutJenkins;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import java.util.ArrayList;
import java.util.List;

import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.*;
import static hudson.views.test.JobMocker.jobOfType;
import static hudson.views.test.JobType.*;
import static hudson.views.test.ViewJobFilters.secured;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;

@WithJenkins
class SecuredJobsFilterTest extends AbstractJenkinsTest {

	@Test
	@WithoutJenkins
	void testMatch() {
		assertFalse(secured().matches(mock(TopLevelItem.class)));

		for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET)) {
			assertFalse(secured().matches(jobOfType(type).property(AuthorizationMatrixProperty.class, null).asItem()));
			assertTrue(secured().matches(jobOfType(type).property(AuthorizationMatrixProperty.class, mock(JobProperty.class)).asItem()));
		}
	}

	@Test
	void testConfigRoundtrip() throws Exception {
		testConfigRoundtrip(
			"view-1",
			new SecuredJobsFilter(excludeMatched.name())
		);

		testConfigRoundtrip(
			"view-2",
			new SecuredJobsFilter(includeMatched.name()),
			new SecuredJobsFilter(excludeUnmatched.name())
		);
	}

	private void testConfigRoundtrip(String viewName, SecuredJobsFilter... filters) throws Exception {
		List<SecuredJobsFilter> expectedFilters = new ArrayList<>();
		for (SecuredJobsFilter filter: filters) {
			expectedFilters.add(new SecuredJobsFilter(filter.getIncludeExcludeTypeString()));
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

	private static void assertFilterEquals(List<SecuredJobsFilter> expectedFilters, List<ViewJobFilter> actualFilters) {
		assertThat(actualFilters.size(), is(expectedFilters.size()));
		for (int i = 0; i < actualFilters.size(); i++) {
			ViewJobFilter actualFilter = actualFilters.get(i);
			SecuredJobsFilter expectedFilter = expectedFilters.get(i);
			assertThat(actualFilter, instanceOf(SecuredJobsFilter.class));
			assertThat(((SecuredJobsFilter)actualFilter).getIncludeExcludeTypeString(), is(expectedFilter.getIncludeExcludeTypeString()));
		}
	}
}
