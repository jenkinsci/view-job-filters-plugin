package hudson.views;

import hudson.model.*;
import hudson.security.ACL;
import hudson.security.AuthorizationMatrixProperty;
import hudson.views.test.JobType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static hudson.model.Item.*;
import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.*;
import static hudson.views.SecurityFilter.*;
import static hudson.views.test.JobMocker.jobOf;
import static hudson.views.test.JobType.*;
import static hudson.views.test.ViewJobFilters.secured;
import static hudson.views.test.ViewJobFilters.security;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SecuredJobsFilterTest extends AbstractHudsonTest {

	@Test
	public void testMatch() {
		assertFalse(secured().matches(mock(TopLevelItem.class)));

		for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET)) {
			assertFalse(secured().matches(jobOf(type).withProperty(AuthorizationMatrixProperty.class, null).asItem()));
			assertTrue(secured().matches(jobOf(type).withProperty(AuthorizationMatrixProperty.class, mock(JobProperty.class)).asItem()));
		}
	}

	@Test
	public void testConfigRoundtrip() throws Exception {
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
		List<SecuredJobsFilter> expectedFilters = new ArrayList<SecuredJobsFilter>();
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

	private void assertFilterEquals(List<SecuredJobsFilter> expectedFilters, List<ViewJobFilter> actualFilters) {
		assertThat(actualFilters.size(), is(expectedFilters.size()));
		for (int i = 0; i < actualFilters.size(); i++) {
			ViewJobFilter actualFilter = actualFilters.get(i);
			SecuredJobsFilter expectedFilter = expectedFilters.get(i);
			assertThat(actualFilter, instanceOf(SecuredJobsFilter.class));
			assertThat(((SecuredJobsFilter)actualFilter).getIncludeExcludeTypeString(), is(expectedFilter.getIncludeExcludeTypeString()));
		}
	}
}
