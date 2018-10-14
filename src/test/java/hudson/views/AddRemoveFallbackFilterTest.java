package hudson.views;

import hudson.model.ListView;
import hudson.model.TopLevelItem;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.jvnet.hudson.test.WithoutJenkins;

import static com.google.common.collect.Lists.newArrayList;
import static hudson.views.AddRemoveFallbackFilter.FallbackTypes.*;
import static hudson.views.test.JobMocker.freeStyleProject;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class AddRemoveFallbackFilterTest extends AbstractHudsonTest {

	private List<TopLevelItem> all;

	@Before
	public void before() throws Exception {
	    all = Lists.<TopLevelItem>newArrayList(
			freeStyleProject().name("job-0").asItem(),
			freeStyleProject().name("job-1").asItem(),
			freeStyleProject().name("job-2").asItem()
		);
	}

	@Test
	@WithoutJenkins
	public void testShouldAddAllJobsWhenNoJobsPresent() throws Exception {
		ViewJobFilter filter = new AddRemoveFallbackFilter(ADD_ALL_IF_NONE_INCLUDED.name());

		List<TopLevelItem> added = newArrayList();

		List<TopLevelItem> expected = newArrayList(all);
		List<TopLevelItem> filtered = filter.filter(added, all, null);

		assertThat("Expected ALL jobs to be in the filtered list", filtered, is(expected));
	}

	@Test
	@WithoutJenkins
	public void testShouldNotModifyFilteredListWhenSomeJobPresent() throws Exception {
		ViewJobFilter filter = new AddRemoveFallbackFilter(ADD_ALL_IF_NONE_INCLUDED.name());

		List<TopLevelItem> added = newArrayList(all.get(0));

		List<TopLevelItem> expected = newArrayList(added);
		List<TopLevelItem> filtered = filter.filter(added, all, null);

		assertThat("Expected only job-1 to be in the filtered list", filtered, is(expected));
	}

	@Test
	@WithoutJenkins
	public void testShouldRemoveAllJobsWhenAllJobsPresent() throws Exception {
		ViewJobFilter filter = new AddRemoveFallbackFilter(REMOVE_ALL_IF_ALL_INCLUDED.name());

		List<TopLevelItem> added = newArrayList(all);

		List<TopLevelItem> expected = newArrayList();
		List<TopLevelItem> filtered = filter.filter(added, all, null);

		assertThat("Expected NO jobs to be in the filtered list", filtered, is(expected));
	}

	@Test
	@WithoutJenkins
	public void testShouldNotModifyFilteredListWhenAllJobsNotPresent() throws Exception {
		ViewJobFilter filter = new AddRemoveFallbackFilter(REMOVE_ALL_IF_ALL_INCLUDED.name());

		List<TopLevelItem> added = newArrayList(all.get(0));

		List<TopLevelItem> expected = newArrayList(added);
		List<TopLevelItem> filtered = filter.filter(added, all, null);

		assertEquals("Expected only job-1 to be in the filtered list", expected, filtered);
	}

	@Test
	public void testConfigRoundtrip() throws Exception {
		testConfigRoundtrip(
			"view-1",
			new AddRemoveFallbackFilter(ADD_ALL_IF_NONE_INCLUDED.name())
		);

		testConfigRoundtrip(
			"view-2",
			new AddRemoveFallbackFilter(REMOVE_ALL_IF_ALL_INCLUDED.name())
		);

		testConfigRoundtrip(
			"view-3",
			new AddRemoveFallbackFilter(ADD_ALL_IF_NONE_INCLUDED.name()),
			new AddRemoveFallbackFilter(REMOVE_ALL_IF_ALL_INCLUDED.name())
		);
	}

	private void testConfigRoundtrip(String viewName, AddRemoveFallbackFilter... filters) throws Exception {
		List<AddRemoveFallbackFilter> expectedFilters = new ArrayList<AddRemoveFallbackFilter>();
		for (AddRemoveFallbackFilter filter: filters) {
			expectedFilters.add(new AddRemoveFallbackFilter(filter.getFallbackTypeString()));
		}

		ListView view = createFilteredView(viewName, filters);
		j.configRoundtrip(view);

		ListView viewAfterRoundtrip = (ListView)j.getInstance().getView(viewName);
		assertFilterEquals(expectedFilters, viewAfterRoundtrip.getJobFilters());

		viewAfterRoundtrip.save();
		j.getInstance().reload();

		ListView viewAfterReload = (ListView)j.getInstance().getView(viewName);
		assertFilterEquals(expectedFilters, viewAfterReload.getJobFilters());
	}

	private void assertFilterEquals(List<AddRemoveFallbackFilter> expectedFilters, List<ViewJobFilter> actualFilters) {
		assertThat(actualFilters.size(), is(expectedFilters.size()));
		for (int i = 0; i < actualFilters.size(); i++) {
			ViewJobFilter actualFilter = actualFilters.get(i);
			AddRemoveFallbackFilter expectedFilter = expectedFilters.get(i);
			assertThat(actualFilter, instanceOf(AddRemoveFallbackFilter.class));
			assertThat(((AddRemoveFallbackFilter)actualFilter).getFallbackTypeString(), is(expectedFilter.getFallbackTypeString()));
		}
	}
}
