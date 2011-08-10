package hudson.views;

import java.util.List;

import hudson.views.ScmFilterHelper;
import hudson.views.ScmValuesProvider;
import junit.framework.TestCase;

public class GitTest extends TestCase {

	public void testGitMatchers() {
		List<ScmValuesProvider> matchers = ScmFilterHelper.matchers;
		boolean found = false;
		for (ScmValuesProvider provider: matchers) {
			String test = provider.getClass().getName();
			if (test.contains("GitLegacyValuesProvider")) {
				found = true;
			}
			assertFalse(test.contains("GitValuesProvider"));
		}
		assertTrue(found);
	}
	
}
