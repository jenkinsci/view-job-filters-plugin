package hudson.views;

import hudson.cli.BuildCommand.CLICause;
import hudson.model.Cause;
import hudson.model.Cause.RemoteCause;
import hudson.model.Cause.UserCause;
import hudson.triggers.SCMTrigger.SCMTriggerCause;
import hudson.triggers.TimerTrigger.TimerTriggerCause;
import hudson.views.BuildTrendFilter.StatusType;
import junit.framework.TestCase;

public class BuildTrendFilterTest extends TestCase {

	public void testCauses() {
		doTestCause(StatusType.Completed, null, false);
		doTestCause(StatusType.Completed, new UserCause(), false);
		
		doTestCause(StatusType.TriggeredByUser, new UserCause(), true);
		doTestCause(StatusType.TriggeredByCli, new CLICause(), true);
		doTestCause(StatusType.TriggeredByRemote, new RemoteCause("re", "mote"), true);
		doTestCause(StatusType.TriggeredByScmPoll, new SCMTriggerCause(), true);
		doTestCause(StatusType.TriggeredByTimer, new TimerTriggerCause(), true);
	}

	private void doTestCause(StatusType statusType, Cause cause, boolean expect) {
		boolean matches = statusType.matchesCause(cause);
		assertEquals(expect, matches);
		if (expect) {
			for (StatusType type: StatusType.values()) {
				if (type == statusType) {
					continue;
				}
				doTestCause(type, cause, false);
			}
		}
	}
}
