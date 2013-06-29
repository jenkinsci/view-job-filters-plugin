package hudson.views;

import hudson.model.TopLevelItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OtherViewsFilterTest extends AbstractHudsonTest {

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
