package hudson.views;

import hudson.model.Item;
import hudson.model.TopLevelItem;
import hudson.model.FreeStyleProject;
import hudson.model.Hudson;
import hudson.model.View;
import hudson.security.ACL;
import hudson.security.Permission;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.acegisecurity.Authentication;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SecurityFilterTest extends AbstractHudsonTest {

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@Test
	public void testWorkspace() {
		ACL acl = mock(ACL.class);
		TopLevelItem item = mock(TopLevelItem.class);
		when(item.getACL()).thenReturn(acl);

		SecurityFilter filter = new SecurityFilter(
				SecurityFilter.ALL, false, false, true, 
				AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched.toString());
		assertFalse(filter.matches(item));
		
		when(acl.hasPermission(Item.WORKSPACE)).thenReturn(true);
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
				SecurityFilter.ONE, true, false, true,
				AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeUnmatched.toString());
		
		// first time will match, because we didn't assign any permissions at all
		boolean matched = filter.matches(item);
		assertFalse(matched);
		List<TopLevelItem> filtered = filter.filter(added, all, addingView);
		assertEquals(1, filtered.size());
		
		// adding build and read won't affect the results
		when(acl.hasPermission(Item.BUILD)).thenReturn(true);
		when(acl.hasPermission(Item.READ)).thenReturn(true);
		filtered = filter.filter(added, all, addingView);
		assertEquals(1, filtered.size());
		
		// if we add workspace, it will now stop matching
		when(acl.hasPermission(Item.WORKSPACE)).thenReturn(true);
		filtered = filter.filter(added, all, addingView);
		assertEquals(0, filtered.size());

		// if we add configure, it will stay the same
		when(acl.hasPermission(Item.CONFIGURE)).thenReturn(true);
		filtered = filter.filter(added, all, addingView);
		assertEquals(0, filtered.size());
	}
}
