package hudson.views;

import java.util.List;

import hudson.views.ScmFilterHelper;
import hudson.views.ScmValuesProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GitTest {

	@Test
	void testNoGitMatchers() {
		List<ScmValuesProvider> matchers = ScmFilterHelper.matchers;
		for (ScmValuesProvider provider: matchers) {
			String test = provider.getClass().getName();
			if (test.contains("Git")) {
                assertFalse(provider.checkLoaded());
            }
		}
	}
}
