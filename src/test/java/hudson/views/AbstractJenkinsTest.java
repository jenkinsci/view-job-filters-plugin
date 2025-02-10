package hudson.views;

import hudson.model.*;
import hudson.plugins.nested_view.NestedView;
import hudson.util.DescribableList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import hudson.views.test.MockServletInputStream;
import hudson.views.test.MockStaplerRequest;
import hudson.views.test.MockStaplerResponse;
import org.junit.Rule;
import org.jvnet.hudson.test.JenkinsRule;
import org.kohsuke.stapler.StaplerRequest2;
import org.kohsuke.stapler.StaplerResponse2;

import jakarta.servlet.ServletException;

public abstract class AbstractJenkinsTest {

	@Rule
	public JenkinsRule j = new JenkinsRule();

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

		StaplerRequest2 request = new MockStaplerRequest("application/xml", Map.of("name", view.getViewName()), new MockServletInputStream(in));
		StaplerResponse2 response = new MockStaplerResponse();
		nestedView.doCreateView(request, response);
		return (T)nestedView.getView(view.getViewName());
	}


}
