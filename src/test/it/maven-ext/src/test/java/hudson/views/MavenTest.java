package hudson.views;

import java.util.List;

import hudson.views.MavenExtraStepsValuesHelper;
import hudson.views.MavenValuesHelper;
import hudson.views.ScmFilterHelper;
import hudson.views.ScmValuesProvider;
import junit.framework.TestCase;
import org.junit.Test;

import static junit.framework.TestCase.*;

public class MavenTest {

	@Test
	public void testMavenExtMatcher() {
		assertTrue(MavenValuesHelper.EXTRASTEPS_HELPER != null);
	}
}
