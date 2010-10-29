package hudson.views;

import hudson.model.Descriptor;
import hudson.model.ListView;
import hudson.model.TopLevelItem;
import hudson.util.DescribableList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jvnet.hudson.test.HudsonTestCase;

public class OtherViewsFilterTest extends HudsonTestCase {

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
	@SuppressWarnings("unchecked")
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
	public void testSimpleView() throws IOException {
		OtherViewsFilter filter = new OtherViewsFilter(
				AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched.toString(), "View-13");
		
		List<TopLevelItem> added = new ArrayList<TopLevelItem>();
		List<TopLevelItem> all = hudson.getItems();
		List<TopLevelItem> filtered = filter.filter(added, all, null);
		TopLevelItem j1 = hudson.getItem("Job-1");
		assertTrue(filtered.contains(j1));
		TopLevelItem j2 = hudson.getItem("Job-2");
		assertTrue(!filtered.contains(j2));
		TopLevelItem j3 = hudson.getItem("Job-3");
		assertTrue(filtered.contains(j3));
	}
	
}
