package hudson.views;

import java.util.List;

import hudson.views.ScmFilterHelper;
import hudson.views.ScmValuesProvider;
import junit.framework.TestCase;

public class GitTest extends TestCase {

	public void testNoGitMatchers() {
		List<ScmValuesProvider> matchers = ScmFilterHelper.matchers;
		for (ScmValuesProvider provider: matchers) {
			String test = provider.getClass().getName();
			assertFalse(test.contains("Git"));
		}
	}
}
