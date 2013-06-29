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

public class SecurityFilterTest extends AbstractHudsonTest {

	public void testWorkspace() {
		TestACL acl = new TestACL();
		TestItem item = new TestItem(acl);
		SecurityFilter filter = new SecurityFilter(
				SecurityFilter.ALL, false, false, true, 
				AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched.toString());
		assertFalse(filter.matches(item));
		
		acl.add(Item.WORKSPACE.name);
		assertTrue(filter.matches(item));
	}
	
	public void testViewJobsRestrictedInSomeWay() {
		TestACL acl = new TestACL();
		TestItem item = new TestItem(acl);
		
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
		acl.add(Item.BUILD.name);
		acl.add(Item.READ.name);
		filtered = filter.filter(added, all, addingView);
		assertEquals(1, filtered.size());
		
		// if we add workspace, it will now stop matching
		acl.add(Item.WORKSPACE.name);
		filtered = filter.filter(added, all, addingView);
		assertEquals(0, filtered.size());

		// if we add configure, it will stay the same
		acl.add(Item.CONFIGURE.name);
		filtered = filter.filter(added, all, addingView);
		assertEquals(0, filtered.size());
	}
	
	private class TestItem extends FreeStyleProject {
		private TestACL acl;
		public TestItem(TestACL acl) {
			super(Hudson.getInstance(), "item");
			this.acl = acl;
		}
		@Override
		public ACL getACL() {
			return acl;
		}
	}
	private class TestACL extends ACL {
		private Set<String> perms = new HashSet<String>();
		@Override
		public boolean hasPermission(Authentication a, Permission permission) {
			return perms.contains(permission.name);
		}
		public void add(String perm) {
			perms.add(perm);
		}
	}
	
}
