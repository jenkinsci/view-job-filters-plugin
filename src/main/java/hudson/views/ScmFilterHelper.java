package hudson.views;

import hudson.scm.SCM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScmFilterHelper {

	public static final List<ScmValuesProvider> matchers = buildMatchers();
	
	public static List<String> getValues(SCM scm) {
		List<String> values = new ArrayList<String>();
		if (scm == null) {
			return values;
		}
		for (ScmValuesProvider matcher: matchers) {
			if (matcher.checkLoaded()) {
				List<String> some = matcher.getValues(scm);
				if (some != null) {
					values.addAll(some);
				}
			}
		}
		return values;
	}
	
	private static List<ScmValuesProvider> buildMatchers() {
		List<ScmValuesProvider> matchers = new ArrayList<ScmValuesProvider>();
		try {
			matchers.add(buildSvn());
		} catch (Throwable e) {
			// probably not loaded
		}
		try {
			matchers.add(buildCvs());
		} catch (Throwable e) {
			// probably not loaded
		}
		try {
			matchers.add(buildGit());
		} catch (Throwable e) {
			// probably not loaded
		}
		return Collections.unmodifiableList(matchers);
	}
	private static ScmValuesProvider buildSvn() {
		return new SvnValuesProvider();
	}
	private static ScmValuesProvider buildCvs() {
		return new CvsValuesProvider();
	}
	private static ScmValuesProvider buildGit() {
        return new GitValuesProvider();
	}
	
}
