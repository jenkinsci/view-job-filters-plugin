package hudson.views;

import hudson.model.*;
import hudson.plugins.nested_view.NestedView;
import hudson.security.GlobalMatrixAuthorizationStrategy;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ViewGraphTest extends AbstractJenkinsTest {
    @Test
    public void testNoCyclesWithOtherViewsFilter() throws IOException {
        TopLevelItem job1 = createFreeStyleProject("job-1");
        TopLevelItem job2 = createFreeStyleProject("job-2");
        TopLevelItem job3 = createFreeStyleProject("job-3");

        View view1 = createFilteredView("view-1", new OtherViewsFilter(includeMatched.name(), "view-2"));
        View view2 = createFilteredView("view-2", new OtherViewsFilter(includeMatched.name(), "view-3"));
        View view3 = createListView("view-3", job2, job3);

        ViewGraph viewGraph = new ViewGraph();
        assertThat(viewGraph.getCycles(), hasSize(0));
        assertThat(viewGraph.getViewsInCycles(), hasSize(0));
        assertThat(viewGraph.getViewsNotInCycles(), containsInAnyOrder(view1, view2, view3, getView("All")));

        assertThat(view1.getItems(), containsInAnyOrder(job2, job3));
        assertThat(view2.getItems(), containsInAnyOrder(job2, job3));
        assertThat(view2.getItems(), containsInAnyOrder(job2, job3));
    }

    @Test
    public void testNoCyclesWithUnclassifiedJobsFilter() throws IOException {
        TopLevelItem job1 = createFreeStyleProject("job-1");
        TopLevelItem job2 = createFreeStyleProject("job-2");
        TopLevelItem job3 = createFreeStyleProject("job-3");
        TopLevelItem job4 = createFreeStyleProject("job-4");
        TopLevelItem job5 = createFreeStyleProject("job-5");
        TopLevelItem job6 = createFreeStyleProject("job-6");

        View view1 = createFilteredView("view-1", new UnclassifiedJobsFilter(includeMatched.name()));
        View view2 = createListView("view-2", job1, job2);
        View view3 = createListView("view-3", job3, job4);

        ViewGraph viewGraph = new ViewGraph();
        assertThat(viewGraph.getCycles(), hasSize(0));
        assertThat(viewGraph.getViewsInCycles(), hasSize(0));
        assertThat(viewGraph.getViewsNotInCycles(), containsInAnyOrder(view1, view2, view3, getView("All")));

        assertThat(view1.getItems(), containsInAnyOrder(job5, job6));
        assertThat(view2.getItems(), containsInAnyOrder(job1, job2));
        assertThat(view3.getItems(), containsInAnyOrder(job3, job4));
    }

    @Test
    public void testNoCyclesWithOtherViewsFilterAndUnclassifiedJobsFilter() throws IOException {
        TopLevelItem job1 = createFreeStyleProject("job-1");
        TopLevelItem job2 = createFreeStyleProject("job-2");
        TopLevelItem job3 = createFreeStyleProject("job-3");
        TopLevelItem job4 = createFreeStyleProject("job-4");
        TopLevelItem job5 = createFreeStyleProject("job-5");
        TopLevelItem job6 = createFreeStyleProject("job-6");

        View view1 = createFilteredView("view-1", new UnclassifiedJobsFilter(includeMatched.name()));
        View view2 = createFilteredView("view-2", new OtherViewsFilter(includeMatched.name(), "view-3"));
        View view3 = createListView("view-3", job1, job2);
        View view4 = createListView("view-4", job3, job4);

        ViewGraph viewGraph = new ViewGraph();
        assertThat(viewGraph.getCycles(), hasSize(0));
        assertThat(viewGraph.getViewsInCycles(), hasSize(0));
        assertThat(viewGraph.getViewsNotInCycles(), containsInAnyOrder(view1, view2, view3, view4, getView("All")));

        assertThat(view1.getItems(), containsInAnyOrder(job5, job6));
        assertThat(view2.getItems(), containsInAnyOrder(job1, job2));
        assertThat(view3.getItems(), containsInAnyOrder(job1, job2));
        assertThat(view4.getItems(), containsInAnyOrder(job3, job4));
    }

    @Test
    public void testCycleWithOneOtherViewsFilter() throws IOException {
        TopLevelItem job1 = createFreeStyleProject("job-1");
        TopLevelItem job2 = createFreeStyleProject("job-2");
        TopLevelItem job3 = createFreeStyleProject("job-3");
        TopLevelItem job4 = createFreeStyleProject("job-4");

        View view1 = createFilteredView("view-1", asList(job1, job2), new OtherViewsFilter(includeMatched.name(), "view-1"));
        View view2 = createFilteredView("view-2", asList(job1), new OtherViewsFilter(includeMatched.name(), "view-3"));
        View view3 = createListView("view-3", job3, job4);

        ViewGraph viewGraph = new ViewGraph();
        assertThat(viewGraph.getCycles(), hasSize(1));
        assertThat(viewGraph.getCycles().iterator().next(), containsInAnyOrder(view1));
        assertThat(viewGraph.getViewsInCycles(), containsInAnyOrder(view1));
        assertThat(viewGraph.getViewsNotInCycles(), containsInAnyOrder(view2, view3, getView("All")));

        assertThat(view1.getItems(), containsInAnyOrder(job1, job2));
        assertThat(view2.getItems(), containsInAnyOrder(job1, job3, job4));
        assertThat(view3.getItems(), containsInAnyOrder(job3, job4));
    }

    @Test
    public void testCycleWithTwoOtherViewsFilters() throws IOException {
        TopLevelItem job1 = createFreeStyleProject("job-1");
        TopLevelItem job2 = createFreeStyleProject("job-2");
        TopLevelItem job3 = createFreeStyleProject("job-3");
        TopLevelItem job4 = createFreeStyleProject("job-4");

        View view1 = createFilteredView("view-1", asList(job1), new OtherViewsFilter(includeMatched.name(), "view-2"));
        View view2 = createFilteredView("view-2", asList(job2), new OtherViewsFilter(includeMatched.name(), "view-1"));
        View view3 =createListView("view-3", job3, job4);

        ViewGraph viewGraph = new ViewGraph();
        assertThat(viewGraph.getCycles(), hasSize(1));
        assertThat(viewGraph.getCycles().iterator().next(), containsInAnyOrder(view1, view2));
        assertThat(viewGraph.getViewsInCycles(), containsInAnyOrder(view1, view2));
        assertThat(viewGraph.getViewsNotInCycles(), containsInAnyOrder(view3, getView("All")));

        assertThat(view1.getItems(), containsInAnyOrder(job1));
        assertThat(view2.getItems(), containsInAnyOrder(job2));
        assertThat(view3.getItems(), containsInAnyOrder(job3, job4));
    }

    @Test
    public void testCycleWithThreeOtherViewsFilters() throws IOException {
        TopLevelItem job1 = createFreeStyleProject("job-1");
        TopLevelItem job2 = createFreeStyleProject("job-2");
        TopLevelItem job3 = createFreeStyleProject("job-3");
        TopLevelItem job4 = createFreeStyleProject("job-4");
        TopLevelItem job5 = createFreeStyleProject("job-5");
        TopLevelItem job6 = createFreeStyleProject("job-6");

        View view1 = createFilteredView("view-1", asList(job1), new OtherViewsFilter(includeMatched.name(), "view-2"));
        View view2 = createFilteredView("view-2", asList(job2),new OtherViewsFilter(includeMatched.name(), "view-3"));
        View view3 = createFilteredView("view-3", asList(job3),new OtherViewsFilter(includeMatched.name(), "view-1"));
        View view4 = createFilteredView("view-4", asList(job4), new OtherViewsFilter(includeMatched.name(), "view-1"));
        View view5 = createListView("view-5", job5, job6);

        ViewGraph viewGraph = new ViewGraph();
        assertThat(viewGraph.getCycles(), hasSize(1));
        assertThat(viewGraph.getCycles().iterator().next(), containsInAnyOrder(view1, view2, view3));
        assertThat(viewGraph.getViewsInCycles(), containsInAnyOrder(view1, view2, view3));
        assertThat(viewGraph.getViewsNotInCycles(), containsInAnyOrder(view4, view5, getView("All")));

        assertThat(view1.getItems(), containsInAnyOrder(job1));
        assertThat(view2.getItems(), containsInAnyOrder(job2));
        assertThat(view3.getItems(), containsInAnyOrder(job3));
        assertThat(view4.getItems(), containsInAnyOrder(job4));
        assertThat(view5.getItems(), containsInAnyOrder(job5, job6));
    }

    @Test
    public void testTwoCyclesWithOtherViewsFilters() throws IOException {
        TopLevelItem job1 = createFreeStyleProject("job-1");
        TopLevelItem job2 = createFreeStyleProject("job-2");
        TopLevelItem job3 = createFreeStyleProject("job-3");
        TopLevelItem job4 = createFreeStyleProject("job-4");
        TopLevelItem job5 = createFreeStyleProject("job-5");
        TopLevelItem job6 = createFreeStyleProject("job-6");
        TopLevelItem job7 = createFreeStyleProject("job-7");
        TopLevelItem job8 = createFreeStyleProject("job-8");

        View view1 = createFilteredView("view-1", asList(job1), new OtherViewsFilter(includeMatched.name(), "view-2"));
        View view2 = createFilteredView("view-2", asList(job2), new OtherViewsFilter(includeMatched.name(), "view-3"));
        View view3 = createFilteredView("view-3", asList(job3), new OtherViewsFilter(includeMatched.name(), "view-1"));
        View view4 = createFilteredView("view-4", asList(job4), new OtherViewsFilter(includeMatched.name(), "view-5"));
        View view5 = createFilteredView("view-5", asList(job5), new OtherViewsFilter(includeMatched.name(), "view-4"));
        View view6 = createFilteredView("view-6", asList(job6), new OtherViewsFilter(includeMatched.name(), "view-1"));
        View view7 = createListView("view-7", job7, job8);

        ViewGraph viewGraph = new ViewGraph();
        assertThat(viewGraph.getCycles(), hasSize(2));
        assertThat(viewGraph.getCycles(), containsInAnyOrder(
            containsInAnyOrder(view1, view2, view3),
            containsInAnyOrder(view4, view5)
        ));
        assertThat(viewGraph.getViewsInCycles(), containsInAnyOrder(view1, view2, view3, view4, view5));
        assertThat(viewGraph.getViewsNotInCycles(), containsInAnyOrder(view6, view7, getView("All")));

        assertThat(view1.getItems(), containsInAnyOrder(job1));
        assertThat(view2.getItems(), containsInAnyOrder(job2));
        assertThat(view3.getItems(), containsInAnyOrder(job3));
        assertThat(view4.getItems(), containsInAnyOrder(job4));
        assertThat(view5.getItems(), containsInAnyOrder(job5));
        assertThat(view6.getItems(), containsInAnyOrder(job6));
        assertThat(view7.getItems(), containsInAnyOrder(job7, job8));
    }

    @Test
    public void testCycleWithTwoUnclassifiedJobsFilters() throws IOException {
        TopLevelItem job1 = createFreeStyleProject("job-1");
        TopLevelItem job2 = createFreeStyleProject("job-2");
        TopLevelItem job3 = createFreeStyleProject("job-3");
        TopLevelItem job4 = createFreeStyleProject("job-4");
        TopLevelItem job5 = createFreeStyleProject("job-5");

        View view1 = createFilteredView("view-1", asList(job1), new UnclassifiedJobsFilter(includeMatched.name()));
        View view2 = createFilteredView("view-2", asList(job2), new UnclassifiedJobsFilter(includeMatched.name()));
        View view3 = createListView("view-3", job3, job4);

        ViewGraph viewGraph = new ViewGraph();
        assertThat(viewGraph.getCycles(), hasSize(1));
        assertThat(viewGraph.getCycles().iterator().next(), containsInAnyOrder(view1, view2));
        assertThat(viewGraph.getViewsInCycles(), containsInAnyOrder(view1, view2));
        assertThat(viewGraph.getViewsNotInCycles(), containsInAnyOrder(view3, getView("All")));

        assertThat(view1.getItems(), containsInAnyOrder(job1, job2, job5));
        assertThat(view2.getItems(), containsInAnyOrder(job1, job2, job5));
        assertThat(view3.getItems(), containsInAnyOrder(job3, job4));
    }

    @Test
    public void testCyclesWithThreeUnclassifiedJobsFilters() throws IOException {
        TopLevelItem job1 = createFreeStyleProject("job-1");
        TopLevelItem job2 = createFreeStyleProject("job-2");
        TopLevelItem job3 = createFreeStyleProject("job-3");
        TopLevelItem job4 = createFreeStyleProject("job-4");
        TopLevelItem job5 = createFreeStyleProject("job-5");
        TopLevelItem job6 = createFreeStyleProject("job-6");

        View view1 = createFilteredView("view-1", asList(job1), new UnclassifiedJobsFilter(includeMatched.name()));
        View view2 = createFilteredView("view-2", asList(job2), new UnclassifiedJobsFilter(includeMatched.name()));
        View view3 = createFilteredView("view-3", asList(job3), new UnclassifiedJobsFilter(includeMatched.name()));
        View view4 = createListView("view-4", job4, job5);

        ViewGraph viewGraph = new ViewGraph();
        assertThat(viewGraph.getCycles(), hasSize(1));
        assertThat(viewGraph.getCycles().iterator().next(), containsInAnyOrder(view1, view2, view3));
        assertThat(viewGraph.getViewsInCycles(), containsInAnyOrder(view1, view2, view3));
        assertThat(viewGraph.getViewsNotInCycles(), containsInAnyOrder(view4, getView("All")));

        assertThat(view1.getItems(), containsInAnyOrder(job1, job2, job3, job6));
        assertThat(view2.getItems(), containsInAnyOrder(job1, job2, job3, job6));
        assertThat(view3.getItems(), containsInAnyOrder(job1, job2, job3, job6));
        assertThat(view4.getItems(), containsInAnyOrder(job4, job5));
    }

    @Test
    public void testCycleWithOtherViewsFilterAndUnclassifiedJobsFilter() throws IOException {
        TopLevelItem job1 = createFreeStyleProject("job-1");
        TopLevelItem job2 = createFreeStyleProject("job-2");
        TopLevelItem job3 = createFreeStyleProject("job-3");
        TopLevelItem job4 = createFreeStyleProject("job-4");
        TopLevelItem job5 = createFreeStyleProject("job-5");
        TopLevelItem job6 = createFreeStyleProject("job-6");
        TopLevelItem job7 = createFreeStyleProject("job-7");

        View view1 = createFilteredView("view-1",
                asList(job1),
                new OtherViewsFilter(includeMatched.name(), "view-2"),
                new OtherViewsFilter(includeMatched.name(), "view-4"));
        View view2 = createListView("view-2", job2, job3);
        View view3 = createListView("view-3", job4, job5);
        View view4 = createFilteredView("view-4", asList(job6), new UnclassifiedJobsFilter(includeMatched.name()));

        ViewGraph viewGraph = new ViewGraph();
        assertThat(viewGraph.getCycles(), hasSize(1));
        assertThat(viewGraph.getCycles().iterator().next(), containsInAnyOrder(view1, view4));
        assertThat(viewGraph.getViewsInCycles(), containsInAnyOrder(view1, view4));
        assertThat(viewGraph.getViewsNotInCycles(), containsInAnyOrder(view2, view3, getView("All")));

        assertThat(view1.getItems(), containsInAnyOrder(job1, job2, job3));
        assertThat(view2.getItems(), containsInAnyOrder(job2, job3));
        assertThat(view3.getItems(), containsInAnyOrder(job4, job5));
        assertThat(view4.getItems(), containsInAnyOrder(job1, job6, job7));
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

        Assert.assertThat(ViewGraph.getAllViews(), is(asList(
                getView("All"),
                listView1,
                listView2,
                listView3,
                listView4,
                listView5,
                listView6
        )));
    }

    @Test
    public void testGetAllViewsByName() throws IOException, SAXException, ServletException, Descriptor.FormException {
        View listView1 = createListView("list-view-1");
        View listView2 = createListView("list-view-2");

        NestedView nestedView1 = new NestedView("nested-view-1");
        j.getInstance().addView(nestedView1);

        View listView3 = addToNestedView(nestedView1, new ListView("list-view-3"));
        View listView4 = addToNestedView(nestedView1, new ListView("list-view-4"));

        NestedView nestedView2 = addToNestedView(nestedView1, new NestedView("nested-view-2"));

        View listView5 = addToNestedView(nestedView2, new ListView("list-view-5"));
        View listView6 = addToNestedView(nestedView2, new ListView("list-view-6"));

        Map<String, View> viewsByName = new HashMap<String, View>();
        viewsByName.put("All", getView("All"));
        viewsByName.put("list-view-1", listView1);
        viewsByName.put("list-view-2", listView2);
        viewsByName.put("nested-view-1 / list-view-3", listView3);
        viewsByName.put("nested-view-1 / list-view-4", listView4);
        viewsByName.put("nested-view-1 / nested-view-2 / list-view-5", listView5);
        viewsByName.put("nested-view-1 / nested-view-2 / list-view-6", listView6);

        Assert.assertThat(ViewGraph.getAllViewsByName(), is(viewsByName));
    }

    @Issue({"JENKINS-13464", "JENKINS-14916"})
    @Test
    public void testGetAllViewsWithUnclassifiedJobsFilter() throws IOException {
        testGetAllViews(new UnclassifiedJobsFilter(includeMatched.name()));
    }

    @Issue({"JENKINS-13464", "JENKINS-14916"})
    @Test
    public void testGetAllViewsWithOtherViewsFilter() throws IOException {
        testGetAllViews(new OtherViewsFilter(includeMatched.name(), "other-view"));
    }

    public void testGetAllViews(ViewJobFilter filter) throws IOException {
        TopLevelItem job1 = createFreeStyleProject("job-1");
        TopLevelItem job2 = createFreeStyleProject("job-2");
        createListView("other-view", job1);
        View view = createFilteredView("filtered-view", filter);

        GlobalMatrixAuthorizationStrategy strategy = new GlobalMatrixAuthorizationStrategy();
        j.getInstance().setAuthorizationStrategy(strategy);

        User user = j.getInstance().getUser("test");
        user.setFullName("test");
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("test", "", new GrantedAuthority[0]);
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThat(view.getItems(), hasSize(0));
    }
}
