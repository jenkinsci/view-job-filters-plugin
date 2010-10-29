package hudson.views;

import hudson.model.TopLevelItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class UnclassifiedJobsFilterTest extends AbstractHudsonTest {

	public void testSimpleView() throws IOException {
		UnclassifiedJobsFilter filter = new UnclassifiedJobsFilter(
				AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched.toString());
		
		List<TopLevelItem> added = new ArrayList<TopLevelItem>();
		List<TopLevelItem> all = hudson.getItems();
		List<TopLevelItem> filtered = filter.filter(added, all, null);
		
		TopLevelItem j1 = hudson.getItem("Job-1");
		assertFalse(filtered.contains(j1));
		TopLevelItem j2 = hudson.getItem("Job-2");
		assertTrue(filtered.contains(j2));
		TopLevelItem j3 = hudson.getItem("Job-3");
		assertFalse(filtered.contains(j3));
		
		TopLevelItem j5 = hudson.getItem("Job-5");
		assertFalse(filtered.contains(j5));
		TopLevelItem j6 = hudson.getItem("Job-6");
		assertFalse(filtered.contains(j6));
		TopLevelItem j7 = hudson.getItem("Job-7");
		assertTrue(filtered.contains(j7));
	}

	
}
