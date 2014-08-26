package hudson.views;

import hudson.scm.SCM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScmFilterHelper {

	public static List<ScmValuesProvider> matchers = buildMatchers();
	
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
	private static ScmValuesProvider buildGit() throws Exception {
		// try both providers to allow legacy data format for git api
		// look for the legacy provider first, because it is backwards compatible
		ScmValuesProvider provider = null;
		try {
			provider = PluginHelperUtils.validateAndThrow(new GitLegacyValuesProvider());
		} catch (Throwable e) {
			// if we get here it means the legacy api isn't present
			// if this constructor fails too, it means git isn't present at all
			provider = PluginHelperUtils.validateAndThrow(new GitValuesProvider());
		}
		return provider;
	}
	
}
