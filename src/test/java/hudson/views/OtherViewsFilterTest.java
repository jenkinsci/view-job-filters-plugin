package hudson.views;

import org.htmlunit.html.*;
import org.junit.jupiter.api.Test;
import hudson.model.*;
import hudson.plugins.nested_view.NestedView;

import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.*;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;

import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@WithJenkins
class OtherViewsFilterTest extends AbstractJenkinsTest {

	@Test
	void testIncludeMatched() throws IOException {
	    TopLevelItem job1 = createFreeStyleProject("job-1");
		TopLevelItem job2 = createFreeStyleProject("job-2");
		TopLevelItem job3 = createFreeStyleProject("job-3");
		TopLevelItem job4 = createFreeStyleProject("job-4");
		View view1 = createListView("view-1", job1, job2);
		View view2 = createFilteredView("view-2", new OtherViewsFilter(includeMatched.name(), "view-1"));

		assertThat(view2.getItems(), contains(job1, job2));
	}

	@Test
	void testIncludeUnmatched() throws IOException {
		TopLevelItem job1 = createFreeStyleProject("job-1");
		TopLevelItem job2 = createFreeStyleProject("job-2");
		TopLevelItem job3 = createFreeStyleProject("job-3");
		TopLevelItem job4 = createFreeStyleProject("job-4");
		View view1 = createListView("view-1", job1, job2);
		View view2 = createFilteredView("view-2", new OtherViewsFilter(includeUnmatched.name(), "view-1"));

		assertThat(view2.getItems(), contains(job3, job4));
	}

	@Test
	void testExcludeMatched() throws IOException {
		TopLevelItem job1 = createFreeStyleProject("job-1");
		TopLevelItem job2 = createFreeStyleProject("job-2");
		TopLevelItem job3 = createFreeStyleProject("job-3");
		TopLevelItem job4 = createFreeStyleProject("job-4");
		View view1 = createListView("view-1", job1, job2);
		View view2 = createFilteredView("view-2", asList(job1, job2, job3),
			new OtherViewsFilter(excludeMatched.name(), "view-1"));

		assertThat(view2.getItems(), contains(job3));
	}

	@Test
	void testExcludeUnmatched() throws IOException {
		TopLevelItem job1 = createFreeStyleProject("job-1");
		TopLevelItem job2 = createFreeStyleProject("job-2");
		TopLevelItem job3 = createFreeStyleProject("job-3");
		TopLevelItem job4 = createFreeStyleProject("job-4");
		View view1 = createListView("view-1", job1, job2);
		View view2 = createFilteredView("view-2", asList(job1, job2, job3),
			new OtherViewsFilter(excludeUnmatched.name(), "view-1"));

		assertThat(view2.getItems(), contains(job1, job2));
	}

	@Test
	void testValidation() throws IOException, SAXException {
		TopLevelItem job1 = createFreeStyleProject("job-1");
		TopLevelItem job2 = createFreeStyleProject("job-2");
		TopLevelItem job3 = createFreeStyleProject("job-3");

		View view1 = createFilteredView("view-1", new OtherViewsFilter(includeMatched.name(), "view-2"));
		View view2 = createFilteredView("view-2", new OtherViewsFilter(includeMatched.name(), "view-3"));
		View view3 = createFilteredView("view-3", new OtherViewsFilter(includeMatched.name(), "all"));
		View view4 = createFilteredView("view-4", new OtherViewsFilter(includeMatched.name(), "non-existent-view"));

		testValidation(view1, null, null);
		testValidation(view1, "", "You must select a view");
		testValidation(view1, "all", null);
		testValidation(view1, "view-1", ".*view-1 -> view-1.*");
		testValidation(view2, "view-1", ".*view-\\d -> view-\\d -> view-\\d.*");
		testValidation(view3, "view-1", ".*view-\\d -> view-\\d -> view-\\d -> view-\\d.*");
	}

	private void testValidation(View view, String otherViewName, String expectedError) throws IOException, SAXException {
		JenkinsRule.WebClient webClient = j.createWebClient();
		webClient.addRequestHeader("Accept-Language", "en");

		HtmlPage page = webClient.getPage(view, "configure");
		HtmlDivision filter = page.querySelector("div[descriptorid='hudson.views.OtherViewsFilter']");

		if (otherViewName != null){
			HtmlSelect select = filter.querySelector("select");
			HtmlOption option = select.getOptionByValue(otherViewName);
			select.setSelectedAttribute(option, true);
			webClient.waitForBackgroundJavaScript(2000);
		}
		HtmlDivision error = filter.querySelector("div[class='error']");

		if (expectedError != null) {
			assertThat(error, is(not(nullValue())));
			assertTrue(error.getTextContent().matches(expectedError));
		} else {
			assertThat(error, is(nullValue()));
		}
	}

	@Test
	void testConfigRoundtrip() throws Exception {
		createListView("list-view-1");
		createListView("list-view-2");

		NestedView nestedView1 = new NestedView("nested-view-1");
		j.getInstance().addView(nestedView1);

		addToNestedView(nestedView1, new ListView("list-view-3"));
		addToNestedView(nestedView1, new ListView("list-view-4"));

		NestedView nestedView2 = addToNestedView(nestedView1, new NestedView("nested-view-2"));

		addToNestedView(nestedView2, new ListView("list-view-5"));
		addToNestedView(nestedView2, new ListView("list-view-6"));

		testConfigRoundtrip(
			"view-1",
			new OtherViewsFilter(includeUnmatched.name(),"All")
		);

		testConfigRoundtrip(
			"view-2",
			new OtherViewsFilter(includeUnmatched.name(),"list-view-1"),
			new OtherViewsFilter(excludeMatched.name(),"view-1")
		);
	}

	private void testConfigRoundtrip(String viewName, OtherViewsFilter... filters) throws Exception {
		List<OtherViewsFilter> expectedFilters = new ArrayList<>();
		for (OtherViewsFilter filter: filters) {
			expectedFilters.add(new OtherViewsFilter(
					filter.getIncludeExcludeTypeString(),
					filter.getOtherViewName()));
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

	private static void assertFilterEquals(List<OtherViewsFilter> expectedFilters, List<ViewJobFilter> actualFilters) {
		assertThat(actualFilters.size(), is(expectedFilters.size()));
		for (int i = 0; i < actualFilters.size(); i++) {
			ViewJobFilter actualFilter = actualFilters.get(i);
			OtherViewsFilter expectedFilter = expectedFilters.get(i);
			assertThat(actualFilter, instanceOf(OtherViewsFilter.class));
			assertThat(((OtherViewsFilter)actualFilter).getIncludeExcludeTypeString(), is(expectedFilter.getIncludeExcludeTypeString()));
			assertThat(((OtherViewsFilter)actualFilter).getOtherViewName(), is(expectedFilter.getOtherViewName()));
		}
	}


}
