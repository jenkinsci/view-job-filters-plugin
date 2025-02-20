package hudson.views;

import com.google.common.collect.Lists;
import hudson.model.ListView;
import hudson.model.TopLevelItem;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.WithoutJenkins;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static hudson.views.test.JobMocker.freeStyleProject;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

@WithJenkins
class AllJobsFilterTest extends AbstractJenkinsTest {

	@Test
	@WithoutJenkins
	void testShouldReturnNoJobsWhenNoJobsPresent() {
	    List<TopLevelItem> all = Lists.newArrayList();

		List<TopLevelItem> added = newArrayList();

		List<TopLevelItem> expected = newArrayList(all);
		List<TopLevelItem> filtered = new AllJobsFilter().filter(added, all, null);

		assertThat(filtered, is(expected));
	}

	@Test
	@WithoutJenkins
	void testShouldReturnAllJobsWhenJobsPresent() {
	    List<TopLevelItem> all = Lists.newArrayList(
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
	void testShouldNotReturnDuplicateJobs() {
		List<TopLevelItem> all = Lists.newArrayList(
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
	void testConfigRoundtrip() throws Exception {
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
	    List<AllJobsFilter> expectedFilters = new ArrayList<>();
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

	private static void assertFilterEquals(List<AllJobsFilter> expectedFilters, List<ViewJobFilter> actualFilters) {
		assertThat(actualFilters.size(), is(expectedFilters.size()));
		for (int i = 0; i < actualFilters.size(); i++) {
			ViewJobFilter actualFilter = actualFilters.get(i);
			AllJobsFilter expectedFilter = expectedFilters.get(i);
			assertThat(actualFilter, instanceOf(AllJobsFilter.class));
		}
	}
}
