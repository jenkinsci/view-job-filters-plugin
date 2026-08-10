package hudson.views;

import org.htmlunit.html.HtmlDivision;
import org.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.Test;
import hudson.model.ListView;
import hudson.model.TopLevelItem;
import hudson.model.View;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@WithJenkins
class UnclassifiedJobsFilterTest extends AbstractJenkinsTest {
	@Test
	void testWithNoJobs() throws IOException {
		ListView filteredView = createFilteredView("unclassified-view", new UnclassifiedJobsFilter(includeMatched.name()));

		List<TopLevelItem> items = filteredView.getItems();
		assertThat(items, hasSize(0));
	}

	@Test
	void testWithNoUnclassifiedJobs() throws IOException {
		createFreeStyleProject("job-1");
		createFreeStyleProject("job-2");
		createFreeStyleProject("job-3");
		createFreeStyleProject("job-4");
		createFreeStyleProject("job-5");
		createFreeStyleProject("job-6");

		createListView("list-view-1",
			getItem("job-1"),
			getItem("job-2"),
			getItem("job-3")
		);

		createListView("list-view-2",
			getItem("job-4"),
			getItem("job-5"),
			getItem("job-6")
		);

		ListView filteredView = createFilteredView("unclassified-view", new UnclassifiedJobsFilter(includeMatched.name()));

		List<TopLevelItem> items = filteredView.getItems();
		assertThat(items, hasSize(0));
	}

	@Test
	void testWithUnclassifiedJobs() throws IOException {
		createFreeStyleProject("job-1");
		createFreeStyleProject("job-2");
		createFreeStyleProject("job-3");
		createFreeStyleProject("job-4");
		createFreeStyleProject("job-5");
		createFreeStyleProject("job-6");

		createListView("list-view-1",
			getItem("job-1"),
			getItem("job-2")
		);

		createListView("list-view-2",
			getItem("job-4"),
			getItem("job-5")
		);

		ListView filteredView = createFilteredView("unclassified-view", new UnclassifiedJobsFilter(includeMatched.name()));

		List<TopLevelItem> items = filteredView.getItems();
		assertThat(items, hasSize(2));
		assertThat(items.get(0).getName(), is("job-3"));
		assertThat(items.get(1).getName(), is("job-6"));
	}

	@Test
	void testWithUnclassifiedJobsAndAllView() throws IOException {
		createFreeStyleProject("job-1");
		createFreeStyleProject("job-2");
		createFreeStyleProject("job-3");
		createFreeStyleProject("job-4");
		createFreeStyleProject("job-5");
		createFreeStyleProject("job-6");

		createListView("list-view-1",
			getItem("job-1"),
			getItem("job-2")
		);

		createListView("list-view-2",
			getItem("job-4"),
			getItem("job-5")
		);

		createListView("list-view-all",
			getItem("job-1"),
			getItem("job-2"),
			getItem("job-3"),
			getItem("job-4"),
			getItem("job-5"),
			getItem("job-6")
		);

		ListView filteredView = createFilteredView("unclassified-view", new UnclassifiedJobsFilter(includeMatched.name()));

		List<TopLevelItem> items = filteredView.getItems();
		assertThat(items, hasSize(2));
		assertThat(items.get(0).getName(), is("job-3"));
		assertThat(items.get(1).getName(), is("job-6"));
	}


	@Test
	void testWithTwoUnclassifiedJobsViews() throws IOException {
		createFreeStyleProject("job-1");
		createFreeStyleProject("job-2");
		createFreeStyleProject("job-3");
		createFreeStyleProject("job-4");
		createFreeStyleProject("job-5");
		createFreeStyleProject("job-6");

		createListView("list-view-1",
			getItem("job-1"),
			getItem("job-2")
		);

		createListView("list-view-2",
			getItem("job-4"),
			getItem("job-5")
		);

		ListView filteredView1 = createFilteredView("unclassified-view-1", new UnclassifiedJobsFilter(includeMatched.name()));
		ListView filteredView2 = createFilteredView("unclassified-view-2", new UnclassifiedJobsFilter(includeMatched.name()));

		List<TopLevelItem> items1 = filteredView1.getItems();
		assertThat(items1, hasSize(2));
		assertThat(items1.get(0).getName(), is("job-3"));
		assertThat(items1.get(1).getName(), is("job-6"));

		List<TopLevelItem> items2 = filteredView2.getItems();
		assertThat(items2, hasSize(2));
		assertThat(items2.get(0).getName(), is("job-3"));
		assertThat(items2.get(1).getName(), is("job-6"));
	}


	@Test
	void testValidationNoCycle() throws IOException, SAXException {
		View view1 = createFilteredView("view-1", new OtherViewsFilter(includeMatched.name(), "all"));
		View view2 = createFilteredView("view-2", new UnclassifiedJobsFilter(includeMatched.name()));

		testValidation(view1, OtherViewsFilter.class, null);
		testValidation(view2, UnclassifiedJobsFilter.class, null);
	}

	@Test
	void testValidationWithCycle() throws IOException, SAXException {
		View view1 = createFilteredView("view-1", new OtherViewsFilter(includeMatched.name(), "view-2"));
		View view2 = createFilteredView("view-2", new UnclassifiedJobsFilter(includeMatched.name()));

		testValidation(view1, OtherViewsFilter.class, ".*view-\\d -> view-\\d -> view-\\d.*");
		testValidation(view2, UnclassifiedJobsFilter.class, ".*view-\\d -> view-\\d -> view-\\d.*");
	}

	@Test
	void testValidationWithUnclassifiedJobsCycle() throws IOException, SAXException {
		View view1 = createFilteredView("view-1", new UnclassifiedJobsFilter(includeMatched.name()));
		View view2 = createFilteredView("view-2", new UnclassifiedJobsFilter(includeMatched.name()));

		testValidation(view1, UnclassifiedJobsFilter.class, ".*view-\\d -> view-\\d -> view-\\d.*");
		testValidation(view2, UnclassifiedJobsFilter.class, ".*view-\\d -> view-\\d -> view-\\d.*");
	}

	private <T extends ViewJobFilter> void testValidation(View view, Class<T> filterClass, String expectedError) throws IOException, SAXException {
		JenkinsRule.WebClient webClient = j.createWebClient();
		webClient.addRequestHeader("Accept-Language", "en");

		HtmlPage page = webClient.getPage(view, "configure");
		HtmlDivision filter = page.querySelector("div[descriptorid='" + filterClass.getCanonicalName() + "']");

		HtmlDivision error = filter.querySelector("div[class='error']");

		if (expectedError != null) {
			assertThat(error, is(not(nullValue())));
			assertTrue(error.getTextContent().matches(expectedError));
		} else {
			assertThat(error != null ? error.getTextContent() : "<none>", error, is(nullValue()));
		}
	}

	@Test
	void testConfigRoundtrip() throws Exception {
		testConfigRoundtrip(
			"view-1",
			new UnclassifiedJobsFilter(excludeMatched.name())
		);

		testConfigRoundtrip(
			"view-2",
			new UnclassifiedJobsFilter(includeMatched.name()),
			new UnclassifiedJobsFilter(excludeUnmatched.name())
		);
	}

	private void testConfigRoundtrip(String viewName, UnclassifiedJobsFilter... filters) throws Exception {
		List<UnclassifiedJobsFilter> expectedFilters = new ArrayList<>();
		for (UnclassifiedJobsFilter filter : filters) {
			expectedFilters.add(new UnclassifiedJobsFilter(filter.getIncludeExcludeTypeString()));
		}

		ListView view = createFilteredView(viewName, filters);
		j.configRoundtrip(view);

		ListView viewAfterRoundtrip = (ListView) j.getInstance().getView(viewName);
		assertFilterEquals(expectedFilters, viewAfterRoundtrip.getJobFilters());

		viewAfterRoundtrip.save();
		j.getInstance().reload();

		ListView viewAfterReload = (ListView) j.getInstance().getView(viewName);
		assertFilterEquals(expectedFilters, viewAfterReload.getJobFilters());
	}

	private static void assertFilterEquals(List<UnclassifiedJobsFilter> expectedFilters, List<ViewJobFilter> actualFilters) {
		assertThat(actualFilters.size(), is(expectedFilters.size()));
		for (int i = 0; i < actualFilters.size(); i++) {
			ViewJobFilter actualFilter = actualFilters.get(i);
			UnclassifiedJobsFilter expectedFilter = expectedFilters.get(i);
			assertThat(actualFilter, instanceOf(UnclassifiedJobsFilter.class));
			assertThat(((UnclassifiedJobsFilter) actualFilter).getIncludeExcludeTypeString(), is(expectedFilter.getIncludeExcludeTypeString()));
		}
	}
}
