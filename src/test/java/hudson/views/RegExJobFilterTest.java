package hudson.views;

import hudson.model.AbstractProject;
import hudson.model.Cause;
import hudson.model.DependencyGraph;
import hudson.model.Hudson;
import hudson.model.Item;
import hudson.model.ItemGroup;
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
import hudson.model.Queue.Task;
import hudson.model.queue.CauseOfBlockage;
import hudson.model.queue.SubTask;
import hudson.scm.CVSSCM;
import hudson.scm.CvsRepository;
import hudson.scm.PollingResult;
import hudson.scm.SCM;
import hudson.security.Permission;
import hudson.triggers.TimerTrigger;
import hudson.util.DescribableList;
import hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;

import org.jvnet.hudson.test.HudsonTestCase;

public class RegExJobFilterTest extends HudsonTestCase {

	/**
	 * Test all the helpers to see that no exceptions are thrown.
	 */
	public void testHelpers() {
		PluginHelperUtils.validateAndThrow(new CoreEmailValuesProvider());
		PluginHelperUtils.validateAndThrow(new CvsValuesProvider());
		PluginHelperUtils.validateAndThrow(new EmailExtValuesProvider());
		PluginHelperUtils.validateAndThrow(new GitLegacyValuesProvider());
		PluginHelperUtils.validateAndThrow(new GitValuesProvider());
		PluginHelperUtils.validateAndThrow(new MavenExtraStepsValuesHelper());
		PluginHelperUtils.validateAndThrow(new MavenProjectValuesHelper());
		PluginHelperUtils.validateAndThrow(new SvnValuesProvider());
	}
	
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
		CVSSCM scm = new CVSSCM(root, modules, branch, "cvsRsh", false, false, false, false, "excludedRegions", null);
		TestItem item = new TestItem("name", scm);
		boolean matched = filter.matches(item);
		assertEquals(expectMatch, matched);
	}
	
	public void testDescription() throws IOException {
		doTestDescription("", false);
		doTestDescription(null, false);
		doTestDescription("nothing", false);
		doTestDescription("desc=test", true);
		doTestDescription("mydesc=test2", true);
		doTestDescription("thisis\nmydesc=testn2\nforyou", true);
		doTestDescription("1&#xd;\ndesc=test&#xd;\n2", true);
		doTestDescription("1 desc=test 2", true);
	}
	private void doTestDescription(String desc, boolean expectMatch) throws IOException {
		RegExJobFilter filter = new RegExJobFilter(".*desc=test.*", IncludeExcludeType.includeMatched.toString(), RegExJobFilter.ValueType.DESCRIPTION.toString());
		TestItem item = new TestItem("name");
		item.setDescription(desc);
		boolean matched = filter.matches(item);
		assertEquals(expectMatch, matched);
	}
	public void testTrigger() throws Exception {
		doTestTrigger("# monday", true);
		doTestTrigger("# tuesday", false);
		doTestTrigger("* * * * *", false);
		doTestTrigger("* * * * *\n#monday", true);
		doTestTrigger("#monday\n* * * * *", true);
	}
	@SuppressWarnings("unchecked")
	private void doTestTrigger(String spec, boolean expectMatch) throws Exception {
		RegExJobFilter filter = new RegExJobFilter(".*monday.*", IncludeExcludeType.includeMatched.toString(), RegExJobFilter.ValueType.SCHEDULE.toString());
		TestProject proj = new TestProject("proj");
		proj.addTrigger(new TimerTrigger(spec));
		boolean matched = filter.matches(proj);
		assertEquals(expectMatch, matched);
	}
	@SuppressWarnings({ "unchecked" })
	private class TestItem extends Job implements SCMedItem, TopLevelItem {

		private String description;
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

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}
		
		@Override
		public Collection<? extends SubTask> getSubTasks() {
			return null;
		}

		@Override
		public Task getOwnerTask() {
			return null;
		}

		@Override
		public Object getSameNodeConstraint() {
			return null;
		}
		
	}
	@SuppressWarnings("unchecked")
	static class TestProject extends AbstractProject implements TopLevelItem {

		public TestProject(String name) {
			super(new TestItemGroup(), name);
		}
		@Override
		protected void buildDependencyGraph(DependencyGraph graph) {
		}
		@Override
		protected Class getBuildClass() {
			return null;
		}
		@Override
		public DescribableList getPublishersList() {
			return null;
		}
		@Override
		public boolean isFingerprintConfigured() {
			return false;
		}
		protected void removeRun(Run run) {
		}
		@Override
		protected synchronized void saveNextBuildNumber() throws IOException {
		}
		@Override
		public void checkPermission(Permission p) {
			super.checkPermission(p);
		}
		public TopLevelItemDescriptor getDescriptor() {
			return null;
		}
		@Override
		public Hudson getParent() {
			return null;
		}
		@Override
		public synchronized void save() throws IOException {
			// do nothing!
		}
	}
	@SuppressWarnings("unchecked")
	static class TestItemGroup implements ItemGroup {
		public String getDisplayName() {
			return null;
		}
		public void save() throws IOException {
		}
		public String getFullDisplayName() {
			return null;
		}
		public String getFullName() {
			return null;
		}
		public Item getItem(String name) {
			return null;
		}
		public Collection getItems() {
			return null;
		}
		public File getRootDirFor(Item child) {
			return null;
		}
		public String getUrl() {
			return null;
		}
		public String getUrlChildPrefix() {
			return null;
		}
		public File getRootDir() {
			return null;
		}
		@Override
		public void onDeleted(Item arg0) throws IOException {
			
		}
		@Override
		public void onRenamed(Item arg0, String arg1, String arg2)
				throws IOException {
		}
		
	}
}
