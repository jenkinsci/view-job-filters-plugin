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
