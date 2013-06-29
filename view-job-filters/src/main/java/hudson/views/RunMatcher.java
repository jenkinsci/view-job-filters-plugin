package hudson.views;

import hudson.model.Run;

public interface RunMatcher {
	 @SuppressWarnings("unchecked")
	boolean matchesRun(Run run);
}
