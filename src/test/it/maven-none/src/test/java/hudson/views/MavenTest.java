package hudson.views;

import java.util.List;

import hudson.views.MavenExtraStepsValuesHelper;
import hudson.views.MavenValuesHelper;
import hudson.views.ScmFilterHelper;
import hudson.views.ScmValuesProvider;
import junit.framework.TestCase;

public class MavenTest extends TestCase {

	public void testNoMavenExtMatchers() {
		assertNull(MavenValuesHelper.EXTRASTEPS_HELPER);
	}
}
