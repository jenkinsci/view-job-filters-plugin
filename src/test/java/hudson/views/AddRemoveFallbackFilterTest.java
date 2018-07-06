package hudson.views;

import hudson.model.TopLevelItem;

import java.util.List;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import static hudson.views.AddRemoveFallbackFilter.FallbackTypes;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class AddRemoveFallbackFilterTest extends AbstractHudsonTest {

	private AddRemoveFallbackFilter filter;
	private List<TopLevelItem> all;
	private List<TopLevelItem> added;
	private List<TopLevelItem> filtered;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		all = j.jenkins.getItems();
		assertFalse("Jenkins jobs are required to run these tests", all.isEmpty());
	}

	@Test
	public void testShouldAddAllJobsWhenNoJobsPresent() throws Exception {
		filter = new AddRemoveFallbackFilter(FallbackTypes.ADD_ALL_IF_NONE_INCLUDED.toString());
		added = Lists.newArrayList();
		filtered = filter.filter(added, all, null);

		assertEquals("Expected ALL jobs to be in the filtered list", all, filtered);
	}

	@Test
	public void testShouldNotModifyFilteredListWhenSomeJobPresent() throws Exception {
		filter = new AddRemoveFallbackFilter(FallbackTypes.ADD_ALL_IF_NONE_INCLUDED.toString());
		added = Lists.newArrayList(j.jenkins.getItem("Job-1"));
		filtered = filter.filter(added, all, null);

		List<TopLevelItem> expected = Lists.newArrayList(j.jenkins.getItem("Job-1"));
		assertEquals("Expected only Job-1 to be in the filtered list", expected, filtered);
	}

	@Test
	public void testShouldRemoveAllJobsWhenAllJobsPresent() throws Exception {
		filter = new AddRemoveFallbackFilter(FallbackTypes.REMOVE_ALL_IF_ALL_INCLUDED.toString());
		added = j.jenkins.getItems();
		filtered = filter.filter(added, all, null);

		assertTrue("Expected NO jobs to be in the filtered list", filtered.isEmpty());
	}

	@Test
	public void testShouldNotModifyFilteredListWhenAllJobsNotPresent() throws Exception {
		filter = new AddRemoveFallbackFilter(FallbackTypes.REMOVE_ALL_IF_ALL_INCLUDED.toString());
		added = Lists.newArrayList(j.jenkins.getItem("Job-1"));
		filtered = filter.filter(added, all, null);

		List<TopLevelItem> expected = Lists.newArrayList(j.jenkins.getItem("Job-1"));
		assertEquals("Expected only Job-1 to be in the filtered list", expected, filtered);
	}

}
