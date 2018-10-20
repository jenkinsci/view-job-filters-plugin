package hudson.views;

import hudson.model.*;
import hudson.plugins.nested_view.NestedView;
import hudson.util.DescribableList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import jenkins.model.Jenkins;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.hudson.test.JenkinsRule;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.annotation.CheckForNull;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AbstractJenkinsTest {

	@Rule
	public JenkinsRule j = new JenkinsRule();

	public void setUp() throws Exception {
		// create some jobs
		for (int i = 0; i < 10; i++) {
			j.createFreeStyleProject("Job-" + i);
		}
		addRegexView("View-56", "Job.*[56]");
		addRegexView("View-13", "Job.*[13]");
	}

	private void addRegexView(String name, String regex) throws IOException {
		ListView v = new ListView(name);
		DescribableList<ViewJobFilter, Descriptor<ViewJobFilter>> filters = v.getJobFilters();
		RegExJobFilter regexFilter = new RegExJobFilter(regex, 
				AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched.toString(),
				RegExJobFilter.ValueType.NAME.toString());
		filters.add(regexFilter);
		j.getInstance().addView(v);
	}

    protected ListView createFilteredView(String name, ViewJobFilter... filters) throws IOException {
        ListView view = new ListView(name, j.getInstance());
        for (ViewJobFilter filter: filters) {
            view.getJobFilters().add(filter);
        }
        j.getInstance().addView(view);
        return view;
    }

	protected ListView createFilteredView(String name, List<TopLevelItem> items, ViewJobFilter... filters) throws IOException {
		ListView view = new ListView(name, j.getInstance());
		for (TopLevelItem item: items) {
			view.add(item);
		}
		for (ViewJobFilter filter: filters) {
			view.getJobFilters().add(filter);
		}
		j.getInstance().addView(view);
		return view;
	}

	protected ListView createListView(String name, TopLevelItem... items) throws IOException {
		ListView view = new ListView(name, j.getInstance().getItemGroup());
		for (TopLevelItem item: items) {
			view.add(item);
		}
		j.getInstance().addView(view);
		return view;
	}

	protected FreeStyleProject createFreeStyleProject(String name) throws IOException {
		return j.createFreeStyleProject(name);
	}

	protected TopLevelItem getItem(String name) {
		return j.getInstance().getItem(name);
	}

	protected View getView(String name) {
		return j.getInstance().getView(name);
	}

	protected <T extends View> T addToNestedView(NestedView nestedView, T view) throws IOException, ServletException, Descriptor.FormException {
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


}
