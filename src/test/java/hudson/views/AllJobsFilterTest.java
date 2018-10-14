package hudson.views;

import com.google.common.collect.Lists;
import hudson.model.ListView;
import hudson.model.TopLevelItem;
import org.junit.Test;
import org.jvnet.hudson.test.WithoutJenkins;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static hudson.views.test.JobMocker.freeStyleProject;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class AllJobsFilterTest extends AbstractHudsonTest {

	@Test
	@WithoutJenkins
	public void testShouldReturnNoJobsWhenNoJobsPresent() throws Exception {
	    List<TopLevelItem> all = Lists.newArrayList();

		List<TopLevelItem> added = newArrayList();

		List<TopLevelItem> expected = newArrayList(all);
		List<TopLevelItem> filtered = new AllJobsFilter().filter(added, all, null);

		assertThat(filtered, is(expected));
	}

	@Test
	@WithoutJenkins
	public void testShouldReturnAllJobsWhenJobsPresent() throws Exception {
	    List<TopLevelItem> all = Lists.<TopLevelItem>newArrayList(
			freeStyleProject().name("job-0").asItem(),
			freeStyleProject().name("job-1").asItem(),
			freeStyleProject().name("job-2").asItem()
		);

		List<TopLevelItem> added = newArrayList();

		List<TopLevelItem> expected = newArrayList(all);
		List<TopLevelItem> filtered = new AllJobsFilter().filter(added, all, null);

		assertThat(filtered, is(expected));
	}

	@Test
	@WithoutJenkins
	public void testShouldNotReturnDuplicateJobs() throws Exception {
		List<TopLevelItem> all = Lists.<TopLevelItem>newArrayList(
			freeStyleProject().name("job-0").asItem(),
			freeStyleProject().name("job-1").asItem(),
			freeStyleProject().name("job-2").asItem()
		);

		List<TopLevelItem> added = newArrayList(all.get(1));

		List<TopLevelItem> expected = newArrayList(all);
		List<TopLevelItem> filtered = new AllJobsFilter().filter(added, all, null);

		assertThat(filtered, is(expected));
	}

	@Test
	public void testConfigRoundtrip() throws Exception {
		testConfigRoundtrip(
			"view-1",
			new AllJobsFilter()
		);

		testConfigRoundtrip(
			"view-2",
			new AllJobsFilter(),
			new AllJobsFilter()
		);
	}

	private void testConfigRoundtrip(String viewName, AllJobsFilter... filters) throws Exception {
	    List<AllJobsFilter> expectedFilters = new ArrayList<AllJobsFilter>();
	    for (AllJobsFilter filter: filters) {
	    	expectedFilters.add(new AllJobsFilter());
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

	private void assertFilterEquals(List<AllJobsFilter> expectedFilters, List<ViewJobFilter> actualFilters) {
		assertThat(actualFilters.size(), is(expectedFilters.size()));
		for (int i = 0; i < actualFilters.size(); i++) {
			ViewJobFilter actualFilter = actualFilters.get(i);
			AllJobsFilter expectedFilter = expectedFilters.get(i);
			assertThat(actualFilter, instanceOf(AllJobsFilter.class));
		}
	}
}
