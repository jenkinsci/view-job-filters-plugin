package hudson.views;

import hudson.model.TopLevelItem;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class OtherViewsFilterTest extends AbstractHudsonTest {

	@Before
	public void settUp() throws Exception {
	    super.setUp();
	}

	@Test
	public void testSimpleView() throws IOException {
		OtherViewsFilter filter = new OtherViewsFilter(
				AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched.toString(), "View-13");
		
		List<TopLevelItem> added = new ArrayList<TopLevelItem>();
		List<TopLevelItem> all = j.getInstance().getItems();
		List<TopLevelItem> filtered = filter.filter(added, all, null);
		TopLevelItem j1 = j.getInstance().getItem("Job-1");
		assertTrue(filtered.contains(j1));
		TopLevelItem j2 = j.getInstance().getItem("Job-2");
		assertTrue(!filtered.contains(j2));
		TopLevelItem j3 = j.getInstance().getItem("Job-3");
		assertTrue(filtered.contains(j3));
	}
	
}
