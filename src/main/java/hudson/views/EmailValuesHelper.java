package hudson.views;

import hudson.model.TopLevelItem;

import java.util.ArrayList;
import java.util.List;

public class EmailValuesHelper {

	private static List<AbstractEmailValuesProvider> matchers = buildMatchers();
	
	public static List<String> getValues(TopLevelItem item) {
		List<String> values = new ArrayList<String>();
		if (item == null) {
			return values;
		}
		for (AbstractEmailValuesProvider matcher: matchers) {
			List<String> some = matcher.getValues(item);
			if (some != null) {
				values.addAll(some);
			}
		}
		return values;
	}
	
	private static List<AbstractEmailValuesProvider> buildMatchers() {
		List<AbstractEmailValuesProvider> matchers = new ArrayList<AbstractEmailValuesProvider>();
		matchers.add(new CoreEmailValuesProvider());
		try {
			matchers.add(buildEmailExt());
		} catch (Throwable e) {
			// plug-in probably not installed
		}
		return matchers;
	}
	private static AbstractEmailValuesProvider buildEmailExt() {
		return new EmailExtValuesProvider();
	}
	
}
