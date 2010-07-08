package hudson.views;

import hudson.model.AbstractProject;
import hudson.model.Cause;
import hudson.model.Hudson;
import hudson.model.Job;
import hudson.model.Label;
import hudson.model.Node;
import hudson.model.ResourceList;
import hudson.model.Run;
import hudson.model.SCMedItem;
import hudson.model.TaskListener;
import hudson.model.TopLevelItem;
import hudson.model.TopLevelItemDescriptor;
import hudson.model.Queue.Executable;
import hudson.model.queue.CauseOfBlockage;
import hudson.scm.CVSSCM;
import hudson.scm.PollingResult;
import hudson.scm.SCM;
import hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import junit.framework.TestCase;

public class RegExJobFilterTest extends TestCase {

	/**
	 * Tests that the example given in the help page works as described.
	 */
	public void testHelpExample() {
		List<TopLevelItem> all = toList("Work_Job", "Work_Nightly", "A-utility-job", "My_Job", "Job2_Nightly", "Util_Nightly", "My_Util");
		List<TopLevelItem> filtered = new ArrayList<TopLevelItem>();
		
		RegExJobFilter includeNonNightly = new RegExJobFilter(".*_Nightly", 
				AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeUnmatched.toString(), // true, false, 
				RegExJobFilter.ValueType.NAME.toString());
		filtered = includeNonNightly.filter(filtered, all, null);
		List<TopLevelItem> expected = toList("Work_Job", "A-utility-job", "My_Job", "My_Util");
		assertListEquals(expected, filtered);

		RegExJobFilter excludeUtil = new RegExJobFilter(".*[Uu]til.*", 
				AbstractIncludeExcludeJobFilter.IncludeExcludeType.excludeMatched.toString(), // false, true, 
				RegExJobFilter.ValueType.NAME.toString());
		filtered = excludeUtil.filter(filtered, all, null);
		expected = toList("Work_Job", "My_Job");
		assertListEquals(expected, filtered);
	}
	private void assertListEquals(List<TopLevelItem> l1, List<TopLevelItem> l2) {
		assertEquals(l1.size(), l2.size());
		for (int i = 0; i < l1.size(); i++) {
			TopLevelItem i1 = l1.get(i);
			TopLevelItem i2 = l2.get(i);
			assertEquals(i1.getName(), i2.getName());
		}
	}
	private List<TopLevelItem> toList(String... names) {
		List<TopLevelItem> items = new ArrayList<TopLevelItem>();
		for (String name: names) {
			TopLevelItem item = new TestItem(name);
			items.add(item);
		}
		return items;
	}
	
	public void testIncludeExclude() {
		doTestIncludeExclude("junit", ".*u.*", IncludeExcludeType.includeMatched, true, false);
		doTestIncludeExclude("junit", ".*u.*", IncludeExcludeType.includeUnmatched, false, false);
		doTestIncludeExclude("junit", ".*u.*", IncludeExcludeType.excludeMatched, false, true);
		doTestIncludeExclude("junit", ".*u.*", IncludeExcludeType.excludeUnmatched, false, false);
		
		doTestIncludeExclude("test", ".*u.*", IncludeExcludeType.includeMatched, false, false);
		doTestIncludeExclude("test", ".*u.*", IncludeExcludeType.includeUnmatched, true, false);
		doTestIncludeExclude("test", ".*u.*", IncludeExcludeType.excludeMatched, false, false);
		doTestIncludeExclude("test", ".*u.*", IncludeExcludeType.excludeUnmatched, false, true);
	}
	
	public void doTestIncludeExclude(String jobName, 
			String regex, IncludeExcludeType includeExcludeType, // boolean negate, boolean exclude, 
			boolean expectInclude, boolean expectExclude) {
		TopLevelItem item = new TestItem(jobName);
		RegExJobFilter filter = new RegExJobFilter(regex, includeExcludeType.toString(), RegExJobFilter.ValueType.NAME.toString());
		boolean matched = filter.matches(item);
		assertEquals(expectExclude, filter.exclude(matched));
		assertEquals(expectInclude, filter.include(matched));
	}
	
	public void testScmRegEx() throws IOException {
		doTestScmRegEx("root", "modules", "branch", false);
		doTestScmRegEx(null, "modules", "branch", false);
		doTestScmRegEx("root", "modules", null, false);
		doTestScmRegEx("root/my-office", "modules", "branch", true);
		doTestScmRegEx("root", "modules/my-office", "branch", true);
		doTestScmRegEx("root", "modules", "branch/my-office", true);
	}
	private void doTestScmRegEx(String root, String modules, String branch, boolean expectMatch) throws IOException {
		RegExJobFilter filter = new RegExJobFilter(".*my-office.*", IncludeExcludeType.includeMatched.toString(), RegExJobFilter.ValueType.SCM.toString());
		CVSSCM scm = new CVSSCM(root, modules, branch, null, false, false, false, null);
		TestItem item = new TestItem("name", scm);
		boolean matched = filter.matches(item);
		assertEquals(expectMatch, matched);
	}
	@SuppressWarnings("unchecked")
	private class TestItem extends Job implements SCMedItem, TopLevelItem {

		private SCM scm;
		
		public TestItem(String name) {
			this(name, null);
		}

		public TestItem(String name, SCM scm) {
			super(null, name);
			this.scm = scm;
		}

		public AbstractProject<?, ?> asProject() {
			return null;
		}

		public SCM getScm() {
			return scm;
		}
		
		public TopLevelItemDescriptor getDescriptor() {
			return null;
		}
		@Override
		public Hudson getParent() {
			return null;
		}
		@Override
		protected SortedMap _getRuns() {
			return null;
		}

		@Override
		public boolean isBuildable() {
			return false;
		}

		@Override
		protected void removeRun(Run arg0) {
		}

		public PollingResult poll(TaskListener tasklistener) {
			return null;
		}

		public boolean pollSCMChanges(TaskListener tasklistener) {
			return false;
		}

		public ResourceList getResourceList() {
			return null;
		}

		public boolean scheduleBuild() {
			return false;
		}

		public boolean scheduleBuild(Cause cause) {
			return false;
		}

		public boolean scheduleBuild(int i, Cause cause) {
			return false;
		}

		public boolean scheduleBuild(int i) {
			return false;
		}

		public void checkAbortPermission() {
		}

		public Executable createExecutable() throws IOException {
			return null;
		}

		public Label getAssignedLabel() {
			return null;
		}

		public CauseOfBlockage getCauseOfBlockage() {
			return null;
		}

		public long getEstimatedDuration() {
			return 0;
		}

		public Node getLastBuiltOn() {
			return null;
		}

		public String getWhyBlocked() {
			return null;
		}

		public boolean hasAbortPermission() {
			return false;
		}

		public boolean isBuildBlocked() {
			return false;
		}

		public boolean isConcurrentBuild() {
			return false;
		}
		
	}
}
