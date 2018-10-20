package hudson.views;

import java.util.List;

import hudson.views.ScmFilterHelper;
import hudson.views.ScmValuesProvider;
import junit.framework.TestCase;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.*;

public class GitTest {

	@Test
	public void testGitMatchers() {
		List<ScmValuesProvider> matchers = ScmFilterHelper.matchers;
		boolean found = false;
		for (ScmValuesProvider provider: matchers) {
			String test = provider.getClass().getName();
			if (test.contains("GitValuesProvider")) {
				found = true;
			}
			assertFalse(test.contains("GitLegacyValuesProvider"));
		}
		assertTrue(found);
	}
	
}
