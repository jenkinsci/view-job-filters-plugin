package hudson.views;

import hudson.model.TopLevelItem;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


public class UnclassifiedJobsFilterTest extends AbstractHudsonTest {

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@Test
	public void testSimpleView() throws IOException {
		UnclassifiedJobsFilter filter = new UnclassifiedJobsFilter(
				AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched.toString());
		
		List<TopLevelItem> added = new ArrayList<TopLevelItem>();
		List<TopLevelItem> all = j.getInstance().getItems();
		List<TopLevelItem> filtered = filter.filter(added, all, null);
		
		TopLevelItem j1 = j.getInstance().getItem("Job-1");
		assertFalse(filtered.contains(j1));
		TopLevelItem j2 = j.getInstance().getItem("Job-2");
		assertTrue(filtered.contains(j2));
		TopLevelItem j3 = j.getInstance().getItem("Job-3");
		assertFalse(filtered.contains(j3));
		
		TopLevelItem j5 = j.getInstance().getItem("Job-5");
		assertFalse(filtered.contains(j5));
		TopLevelItem j6 = j.getInstance().getItem("Job-6");
		assertFalse(filtered.contains(j6));
		TopLevelItem j7 = j.getInstance().getItem("Job-7");
		assertTrue(filtered.contains(j7));
	}

	
}
