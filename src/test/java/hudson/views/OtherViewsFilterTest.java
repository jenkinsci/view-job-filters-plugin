package hudson.views;

import hudson.model.*;
import hudson.plugins.nested_view.NestedView;

import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.*;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;

import hudson.security.Permission;
import org.junit.Before;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockAuthorizationStrategy;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.xml.sax.SAXException;

import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OtherViewsFilterTest extends AbstractHudsonTest {

	@Test
	public void testIncludeMatched() throws IOException {
	    TopLevelItem job1 = createFreeStyleProject("job-1");
		TopLevelItem job2 = createFreeStyleProject("job-2");
		TopLevelItem job3 = createFreeStyleProject("job-3");
		TopLevelItem job4 = createFreeStyleProject("job-4");
		View view1 = createListView("view-1", job1, job2);
		View view2 = createFilteredView("view-2", new OtherViewsFilter(includeMatched.name(), "view-1"));

		assertThat(view2.getItems(), contains(job1, job2));
	}

	@Test
	public void testIncludeUnmatched() throws IOException {
		TopLevelItem job1 = createFreeStyleProject("job-1");
		TopLevelItem job2 = createFreeStyleProject("job-2");
		TopLevelItem job3 = createFreeStyleProject("job-3");
		TopLevelItem job4 = createFreeStyleProject("job-4");
		View view1 = createListView("view-1", job1, job2);
		View view2 = createFilteredView("view-2", new OtherViewsFilter(includeUnmatched.name(), "view-1"));

		assertThat(view2.getItems(), contains(job3, job4));
	}

	@Test
	public void testExcludeMatched() throws IOException {
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
	public void testExcludeUnmatched() throws IOException {
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
	public void testGetAllViews() throws IOException, SAXException, ServletException, Descriptor.FormException {
		View listView1 = createListView("list-view-1");
		View listView2 = createListView("list-view-2");

		NestedView nestedView1 = new NestedView("nested-view-1");
		j.getInstance().addView(nestedView1);

		View listView3 = addToNestedView(nestedView1, new ListView("list-view-3"));
		View listView4 = addToNestedView(nestedView1, new ListView("list-view-4"));

		NestedView nestedView2 = addToNestedView(nestedView1, new NestedView("nested-view-2"));

		View listView5 = addToNestedView(nestedView2, new ListView("list-view-5"));
		View listView6 = addToNestedView(nestedView2, new ListView("list-view-6"));

		assertThat(OtherViewsFilter.getAllViews(), is(asList(
			getView("All"),
			listView1,
			listView2,
			listView3,
			listView4,
			listView5,
			listView6
		)));
	}

	private <T extends View> T addToNestedView(NestedView nestedView, T view) throws IOException, ServletException, Descriptor.FormException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
	    view.writeXml(out);

	    final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());

		StaplerRequest request = mock(StaplerRequest.class);
		when(request.getContentType()).thenReturn("application/xml");
		when(request.getParameter("name")).thenReturn(view.getViewName());
		when(request.getInputStream()).thenReturn(new ServletInputStream() {

			@Override
			public int read() throws IOException {
				return in.read();
			}

			@Override
			public boolean isFinished() {
				return false;
			}

			@Override
			public boolean isReady() {
			    return true;
			}

			@Override
			public void setReadListener(ReadListener readListener) {
			}

			@Override
			public void close() throws IOException {
				in.close();
			}
		});

		nestedView.doCreateView(request, mock(StaplerResponse.class));
		return (T)nestedView.getView(view.getViewName());
	}


	@Test
	public void testConfigRoundtrip() throws Exception {
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
		List<OtherViewsFilter> expectedFilters = new ArrayList<OtherViewsFilter>();
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

	private void assertFilterEquals(List<OtherViewsFilter> expectedFilters, List<ViewJobFilter> actualFilters) {
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
