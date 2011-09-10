package hudson.views;

import hudson.model.Descriptor;
import hudson.model.ListView;
import hudson.util.DescribableList;

import java.io.IOException;

import org.jvnet.hudson.test.HudsonTestCase;

public abstract class AbstractHudsonTest extends HudsonTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// create some jobs
		for (int i = 0; i < 10; i++) {
			createFreeStyleProject("Job-" + i);
		}
		addRegexView("View-56", "Job.*[56]");
		addRegexView("View-13", "Job.*[13]");
	}
	private void addRegexView(String name, String regex) throws IOException {
		ListView v = new ListView(name);
		DescribableList<ViewJobFilter, Descriptor<ViewJobFilter>> filters = 
			(DescribableList<ViewJobFilter, Descriptor<ViewJobFilter>>) v.getJobFilters();
		RegExJobFilter regexFilter = new RegExJobFilter(regex, 
				AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched.toString(),
				RegExJobFilter.ValueType.NAME.toString());
		filters.add(regexFilter);
		hudson.addView(v);
	}
}
