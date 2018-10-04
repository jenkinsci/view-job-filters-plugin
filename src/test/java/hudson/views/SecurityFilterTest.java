package hudson.views;

import hudson.model.*;
import hudson.security.ACL;

import java.util.ArrayList;
import java.util.List;

import hudson.views.test.JobType;
import org.junit.Test;

import static hudson.model.Item.*;
import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.*;
import static hudson.views.SecurityFilter.*;
import static hudson.views.test.JobMocker.jobOfType;
import static hudson.views.test.JobType.*;
import static hudson.views.test.ViewJobFilters.security;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SecurityFilterTest extends AbstractHudsonTest {

	@Test
	public void testWorkspace() {
		ACL acl = mock(ACL.class);
		TopLevelItem item = mock(TopLevelItem.class);
		when(item.getACL()).thenReturn(acl);

		SecurityFilter filter = new SecurityFilter(
				ALL, false, false, true,
				AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched.toString());
		assertFalse(filter.matches(item));
		
		when(acl.hasPermission(WORKSPACE)).thenReturn(true);
		assertTrue(filter.matches(item));
	}

	@Test
	public void testViewJobsRestrictedInSomeWay() {
		ACL acl = mock(ACL.class);
		TopLevelItem item = mock(TopLevelItem.class);
		when(item.getACL()).thenReturn(acl);

		List<TopLevelItem> all = new ArrayList<TopLevelItem>();
		all.add(item);

		List<TopLevelItem> added = new ArrayList<TopLevelItem>();
		
		View addingView = null; // don't need to mock out...

		// this filter looks for jobs that do not have even one of either config or workspace
		SecurityFilter filter = new SecurityFilter(
				ONE, true, false, true,
				AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeUnmatched.toString());
		
		// first time will match, because we didn't assign any permissions at all
		boolean matched = filter.matches(item);
		assertFalse(matched);
		List<TopLevelItem> filtered = filter.filter(added, all, addingView);
		assertEquals(1, filtered.size());
		
		// adding build and read won't affect the results
		when(acl.hasPermission(BUILD)).thenReturn(true);
		when(acl.hasPermission(Item.READ)).thenReturn(true);
		filtered = filter.filter(added, all, addingView);
		assertEquals(1, filtered.size());
		
		// if we add workspace, it will now stop matching
		when(acl.hasPermission(WORKSPACE)).thenReturn(true);
		filtered = filter.filter(added, all, addingView);
		assertEquals(0, filtered.size());

		// if we add configure, it will stay the same
		when(acl.hasPermission(CONFIGURE)).thenReturn(true);
		filtered = filter.filter(added, all, addingView);
		assertEquals(0, filtered.size());
	}

	@Test
	public void testMatch() {
		for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET)) {
			assertFalse(security(ONE, false, false, false).matches(jobOfType(type).permissions().asItem()));
			assertFalse(security(ONE, false, false, false).matches(jobOfType(type).permissions(CONFIGURE).asItem()));
			assertFalse(security(ONE, false, false, false).matches(jobOfType(type).permissions(BUILD).asItem()));
			assertFalse(security(ONE, false, false, false).matches(jobOfType(type).permissions(WORKSPACE).asItem()));

			assertFalse(security(ONE, true, false, false).matches(jobOfType(type).permissions().asItem()));
			assertTrue(security(ONE, true, false, false).matches(jobOfType(type).permissions(CONFIGURE).asItem()));
			assertFalse(security(ONE, true, false, false).matches(jobOfType(type).permissions(BUILD).asItem()));
			assertFalse(security(ONE, true, false, false).matches(jobOfType(type).permissions(WORKSPACE).asItem()));

			assertFalse(security(ONE, false, true, false).matches(jobOfType(type).permissions().asItem()));
			assertFalse(security(ONE, false, true, false).matches(jobOfType(type).permissions(CONFIGURE).asItem()));
			assertTrue(security(ONE, false, true, false).matches(jobOfType(type).permissions(BUILD).asItem()));
			assertFalse(security(ONE, false, true, false).matches(jobOfType(type).permissions(WORKSPACE).asItem()));

			assertFalse(security(ONE, false, false, true).matches(jobOfType(type).permissions().asItem()));
			assertFalse(security(ONE, false, false, true).matches(jobOfType(type).permissions(CONFIGURE).asItem()));
			assertFalse(security(ONE, false, false, true).matches(jobOfType(type).permissions(BUILD).asItem()));
			assertTrue(security(ONE, false, false, true).matches(jobOfType(type).permissions(WORKSPACE).asItem()));

			assertFalse(security(ALL, true, true, false).matches(jobOfType(type).permissions().asItem()));
			assertFalse(security(ALL, true, true, false).matches(jobOfType(type).permissions(CONFIGURE).asItem()));
			assertFalse(security(ALL, true, true, false).matches(jobOfType(type).permissions(BUILD).asItem()));
			assertTrue(security(ALL, true, true, false).matches(jobOfType(type).permissions(CONFIGURE, BUILD).asItem()));
			assertTrue(security(ALL, true, true, false).matches(jobOfType(type).permissions(CONFIGURE, BUILD, WORKSPACE).asItem()));

			assertFalse(security(ALL, true, false, true).matches(jobOfType(type).permissions().asItem()));
			assertFalse(security(ALL, true, false, true).matches(jobOfType(type).permissions(CONFIGURE).asItem()));
			assertFalse(security(ALL, true, false, true).matches(jobOfType(type).permissions(WORKSPACE).asItem()));
			assertTrue(security(ALL, true, false, true).matches(jobOfType(type).permissions(CONFIGURE, WORKSPACE).asItem()));
			assertTrue(security(ALL, true, false, true).matches(jobOfType(type).permissions(CONFIGURE, WORKSPACE, BUILD).asItem()));

			assertFalse(security(ALL, false, true, true).matches(jobOfType(type).permissions().asItem()));
			assertFalse(security(ALL, false, true, true).matches(jobOfType(type).permissions(BUILD).asItem()));
			assertFalse(security(ALL, false, true, true).matches(jobOfType(type).permissions(WORKSPACE).asItem()));
			assertTrue(security(ALL, false, true, true).matches(jobOfType(type).permissions(BUILD, WORKSPACE).asItem()));
			assertTrue(security(ALL, false, true, true).matches(jobOfType(type).permissions(BUILD, WORKSPACE, CONFIGURE).asItem()));

			assertFalse(security(ALL, true, true, true).matches(jobOfType(type).permissions().asItem()));
			assertFalse(security(ALL, true, true, true).matches(jobOfType(type).permissions(CONFIGURE).asItem()));
			assertFalse(security(ALL, true, true, true).matches(jobOfType(type).permissions(BUILD).asItem()));
			assertFalse(security(ALL, true, true, true).matches(jobOfType(type).permissions(WORKSPACE).asItem()));
			assertFalse(security(ALL, true, true, true).matches(jobOfType(type).permissions(CONFIGURE, BUILD).asItem()));
			assertFalse(security(ALL, true, true, true).matches(jobOfType(type).permissions(CONFIGURE, WORKSPACE).asItem()));
			assertFalse(security(ALL, true, true, true).matches(jobOfType(type).permissions(BUILD, WORKSPACE).asItem()));
			assertTrue(security(ALL, true, true, true).matches(jobOfType(type).permissions(CONFIGURE, BUILD, WORKSPACE).asItem()));
		}
	}


	@Test
	public void testConfigRoundtrip() throws Exception {
		testConfigRoundtrip(
				"view-1",
				new SecurityFilter(ALL, false, true, false, excludeMatched.name())
		);

		testConfigRoundtrip(
				"view-2",
				new SecurityFilter(ALL, true, false, true, includeUnmatched.name()),
				new SecurityFilter(ONE, false, true, true, excludeMatched.name())
		);

		testConfigRoundtrip(
				"view-3",
				new SecurityFilter(ONE, true, false, true, includeMatched.name()),
				new SecurityFilter(ALL, false, true, true, excludeUnmatched.name()),
				new SecurityFilter(ONE, true, true, false, includeUnmatched.name())
		);
	}

	private void testConfigRoundtrip(String viewName, SecurityFilter... filters) throws Exception {
		List<SecurityFilter> expectedFilters = new ArrayList<SecurityFilter>();
		for (SecurityFilter filter: filters) {
			expectedFilters.add(new SecurityFilter(
					filter.getPermissionCheckType(),
					filter.isConfigure(),
					filter.isBuild(),
					filter.isWorkspace(),
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

	private void assertFilterEquals(List<SecurityFilter> expectedFilters, List<ViewJobFilter> actualFilters) {
		assertThat(actualFilters.size(), is(expectedFilters.size()));
		for (int i = 0; i < actualFilters.size(); i++) {
			ViewJobFilter actualFilter = actualFilters.get(i);
			SecurityFilter expectedFilter = expectedFilters.get(i);
			assertThat(actualFilter, instanceOf(SecurityFilter.class));
			assertThat(((SecurityFilter)actualFilter).isConfigure(), is(expectedFilter.isConfigure()));
			assertThat(((SecurityFilter)actualFilter).isBuild(), is(expectedFilter.isBuild()));
			assertThat(((SecurityFilter)actualFilter).isWorkspace(), is(expectedFilter.isWorkspace()));
			assertThat(((SecurityFilter)actualFilter).getIncludeExcludeTypeString(), is(expectedFilter.getIncludeExcludeTypeString()));
		}
	}
}
