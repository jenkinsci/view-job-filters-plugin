package hudson.views;

import hudson.model.TopLevelItem;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;


public class UnclassifiedJobsFilterTest extends AbstractHudsonTest {

	@Test
	public void testSimpleView() throws IOException {
		UnclassifiedJobsFilter filter = new UnclassifiedJobsFilter(
				AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched.toString());
		
		List<TopLevelItem> added = new ArrayList<TopLevelItem>();
		List<TopLevelItem> all = j.jenkins.getItems();
		List<TopLevelItem> filtered = filter.filter(added, all, null);
		
		TopLevelItem j1 = j.jenkins.getItem("Job-1");
		assertFalse(filtered.contains(j1));
		TopLevelItem j2 = j.jenkins.getItem("Job-2");
		assertTrue(filtered.contains(j2));
		TopLevelItem j3 = j.jenkins.getItem("Job-3");
		assertFalse(filtered.contains(j3));
		
		TopLevelItem j5 = j.jenkins.getItem("Job-5");
		assertFalse(filtered.contains(j5));
		TopLevelItem j6 = j.jenkins.getItem("Job-6");
		assertFalse(filtered.contains(j6));
		TopLevelItem j7 = j.jenkins.getItem("Job-7");
		assertTrue(filtered.contains(j7));
	}

	
}
