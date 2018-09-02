package hudson.views;

import hudson.model.*;
import hudson.model.Queue;
import hudson.model.Queue.Executable;
import hudson.model.Queue.Task;
import hudson.model.queue.CauseOfBlockage;
import hudson.model.queue.SubTask;
import hudson.scm.CVSSCM;
import hudson.scm.CvsRepository;
import hudson.scm.PollingResult;
import hudson.scm.SCM;
import hudson.security.ACL;
import hudson.security.Permission;
import hudson.triggers.TimerTrigger;
import hudson.triggers.Trigger;
import hudson.triggers.TriggerDescriptor;
import hudson.util.DescribableList;
import hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.acegisecurity.Authentication;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.hudson.test.JenkinsRule;
import org.mockito.Mockito;

import javax.annotation.Nonnull;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

public class RegExJobFilterTest extends AbstractHudsonTest {

	/*
	 * Test all the helpers to see that no exceptions are thrown.
	 */
	@Test
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
	
	/*
	 * Tests that the example given in the help page works as described.
	 */
	@Test
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
			Job item =  mock(Job.class, withSettings().extraInterfaces(TopLevelItem.class));
			when(item.getName()).thenReturn(name);
			items.add((TopLevelItem)item);
		}
		return items;
	}

	@Test
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
	
	private void doTestIncludeExclude(String jobName,
			String regex, IncludeExcludeType includeExcludeType, // boolean negate, boolean exclude, 
			boolean expectInclude, boolean expectExclude) {
		Job item =  mock(Job.class, withSettings().extraInterfaces(TopLevelItem.class));
		when(item.getName()).thenReturn(jobName);
		RegExJobFilter filter = new RegExJobFilter(regex, includeExcludeType.toString(), RegExJobFilter.ValueType.NAME.toString());
		boolean matched = filter.matches((TopLevelItem)item);
		assertEquals(expectExclude, filter.exclude(matched));
		assertEquals(expectInclude, filter.include(matched));
	}

	@Test
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
		Job item =  mock(Job.class, withSettings().extraInterfaces(TopLevelItem.class, SCMedItem.class));
		when(item.getName()).thenReturn("name");
		when(((SCMedItem)item).getScm()).thenReturn(scm);
		boolean matched = filter.matches((TopLevelItem)item);
		assertEquals(expectMatch, matched);
	}

	@Test
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

		Job item =  mock(Job.class, withSettings().extraInterfaces(TopLevelItem.class));
		when(item.getName()).thenReturn("name");
		when(item.getDescription()).thenReturn(desc);

		boolean matched = filter.matches((TopLevelItem)item);
		assertEquals(expectMatch, matched);
	}

	@Test
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

		Map<TriggerDescriptor, Trigger<?>> triggers = new HashMap<TriggerDescriptor, Trigger<?>>();
		triggers.put(mock(TriggerDescriptor.class), new TimerTrigger(spec));

		AbstractProject project = mock(AbstractProject.class, withSettings().extraInterfaces(TopLevelItem.class));
		when(project.getTriggers()).thenReturn(triggers);

		boolean matched = filter.matches((TopLevelItem)project);
		assertEquals(expectMatch, matched);
	}
}
