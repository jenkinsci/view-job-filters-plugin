package hudson.views;

import hudson.model.FreeStyleProject;
import hudson.model.View;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.synchronizedSortedMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ViewGraphTest extends AbstractHudsonTest {
    @Test
    public void testNoCyclesWithOtherViewsFilter() throws IOException {
        View view1 = createFilteredView("view-1",
                new OtherViewsFilter(includeMatched.name(), "view-2"));
        View view2 = createFilteredView("view-2",
                new OtherViewsFilter(includeMatched.name(), "view-3"));
        View view3 = createListView("view-3",
                createFreeStyleProject("job-1"),
                createFreeStyleProject("job-2"));

        ViewGraph viewGraph = new ViewGraph(j.getInstance().getViews());
        assertThat(viewGraph.getCycles(), hasSize(0));
        assertThat(viewGraph.getViewsInCycles(), hasSize(0));
        assertThat(viewGraph.getViewsNotInCycles(), containsInAnyOrder(view1, view2, view3, getView("All")));
    }

    @Test
    public void testNoCyclesWithUnclassifiedJobsFilter() throws IOException {
        View view1 = createFilteredView("view-1",
                new UnclassifiedJobsFilter(includeMatched.name()));
        View view2 = createListView("view-2",
                createFreeStyleProject("job-1"),
                createFreeStyleProject("job-2"));
        View view3 = createListView("view-3",
                createFreeStyleProject("job-3"),
                createFreeStyleProject("job-4"));

        ViewGraph viewGraph = new ViewGraph(j.getInstance().getViews());
        assertThat(viewGraph.getCycles(), hasSize(0));
        assertThat(viewGraph.getViewsInCycles(), hasSize(0));
        assertThat(viewGraph.getViewsNotInCycles(), containsInAnyOrder(view1, view2, view3, getView("All")));
    }

    @Test
    public void testNoCyclesWithOtherViewsFilterAndUnclassifiedJobsFilter() throws IOException {
        View view1 = createFilteredView("view-1",
                new UnclassifiedJobsFilter(includeMatched.name()));
        View view2 = createFilteredView("view-2",
                new OtherViewsFilter(includeMatched.name(), "view-3"));
        View view3 = createListView("view-3",
                createFreeStyleProject("job-1"),
                createFreeStyleProject("job-2"));
        View view4 = createListView("view-4",
                createFreeStyleProject("job-3"),
                createFreeStyleProject("job-4"));

        ViewGraph viewGraph = new ViewGraph(j.getInstance().getViews());
        assertThat(viewGraph.getCycles(), hasSize(0));
        assertThat(viewGraph.getViewsInCycles(), hasSize(0));
        assertThat(viewGraph.getViewsNotInCycles(), containsInAnyOrder(view1, view2, view3, view4, getView("All")));
    }

    @Test
    public void testCycleWithOneOtherViewsFilter() throws IOException {
        View view1 = createFilteredView("view-1",
                new OtherViewsFilter(includeMatched.name(), "view-1"));
        View view2 = createFilteredView("view-2",
                new OtherViewsFilter(includeMatched.name(), "view-3"));
        View view3 =createListView("view-3",
                createFreeStyleProject("job-1"),
                createFreeStyleProject("job-2"));

        ViewGraph viewGraph = new ViewGraph(j.getInstance().getViews());
        assertThat(viewGraph.getCycles(), hasSize(1));
        assertThat(viewGraph.getCycles().iterator().next(), containsInAnyOrder(view1));
        assertThat(viewGraph.getViewsInCycles(), containsInAnyOrder(view1));
        assertThat(viewGraph.getViewsNotInCycles(), containsInAnyOrder(view2, view3, getView("All")));
    }


    @Test
    public void testCycleWithTwoOtherViewsFilters() throws IOException {
        View view1 = createFilteredView("view-1",
                new OtherViewsFilter(includeMatched.name(), "view-2"));
        View view2 = createFilteredView("view-2",
                new OtherViewsFilter(includeMatched.name(), "view-1"));
        View view3 =createListView("view-3",
                createFreeStyleProject("job-1"),
                createFreeStyleProject("job-2"));

        ViewGraph viewGraph = new ViewGraph(j.getInstance().getViews());
        assertThat(viewGraph.getCycles(), hasSize(1));
        assertThat(viewGraph.getCycles().iterator().next(), containsInAnyOrder(view1, view2));
        assertThat(viewGraph.getViewsInCycles(), containsInAnyOrder(view1, view2));
        assertThat(viewGraph.getViewsNotInCycles(), containsInAnyOrder(view3, getView("All")));
    }

    @Test
    public void testCycleWithThreeOtherViewsFilters() throws IOException {
        View view1 = createFilteredView("view-1",
                new OtherViewsFilter(includeMatched.name(), "view-2"));
        View view2 = createFilteredView("view-2",
                new OtherViewsFilter(includeMatched.name(), "view-3"));
        View view3 = createFilteredView("view-3",
                new OtherViewsFilter(includeMatched.name(), "view-1"));
        View view4 = createFilteredView("view-4",
                new OtherViewsFilter(includeMatched.name(), "view-1"));
        View view5 = createListView("view-5",
                createFreeStyleProject("job-1"),
                createFreeStyleProject("job-2"));

        ViewGraph viewGraph = new ViewGraph(j.getInstance().getViews());
        assertThat(viewGraph.getCycles(), hasSize(1));
        assertThat(viewGraph.getCycles().iterator().next(), containsInAnyOrder(view1, view2, view3));
        assertThat(viewGraph.getViewsInCycles(), containsInAnyOrder(view1, view2, view3));
        assertThat(viewGraph.getViewsNotInCycles(), containsInAnyOrder(view4, view5, getView("All")));
    }

    @Test
    public void testTwoCyclesWithOtherViewsFilters() throws IOException {
        View view1 = createFilteredView("view-1",
                new OtherViewsFilter(includeMatched.name(), "view-2"));
        View view2 = createFilteredView("view-2",
                new OtherViewsFilter(includeMatched.name(), "view-3"));
        View view3 = createFilteredView("view-3",
                new OtherViewsFilter(includeMatched.name(), "view-1"));
        View view4 = createFilteredView("view-4",
                new OtherViewsFilter(includeMatched.name(), "view-5"));
        View view5 = createFilteredView("view-5",
                new OtherViewsFilter(includeMatched.name(), "view-4"));
        View view6 = createFilteredView("view-6",
                new OtherViewsFilter(includeMatched.name(), "view-1"));
        View view7 = createListView("view-7",
                createFreeStyleProject("job-1"),
                createFreeStyleProject("job-2"));

        ViewGraph viewGraph = new ViewGraph(j.getInstance().getViews());
        assertThat(viewGraph.getCycles(), hasSize(2));
        assertThat(viewGraph.getCycles(), containsInAnyOrder(
            containsInAnyOrder(view1, view2, view3),
            containsInAnyOrder(view4, view5)
        ));
        assertThat(viewGraph.getViewsInCycles(), containsInAnyOrder(view1, view2, view3, view4, view5));
        assertThat(viewGraph.getViewsNotInCycles(), containsInAnyOrder(view6, view7, getView("All")));
    }

    @Test
    public void testCycleWithTwoUnclassifiedJobsFilters() throws IOException {
        View view1 = createFilteredView("view-1",
                new UnclassifiedJobsFilter(includeMatched.name()));
        View view2 = createFilteredView("view-2",
                new UnclassifiedJobsFilter(includeMatched.name()));

        ViewGraph viewGraph = new ViewGraph(j.getInstance().getViews());
        assertThat(viewGraph.getCycles(), hasSize(1));
        assertThat(viewGraph.getCycles().iterator().next(), containsInAnyOrder(view1, view2));
        assertThat(viewGraph.getViewsInCycles(), containsInAnyOrder(view1, view2));
        assertThat(viewGraph.getViewsNotInCycles(), containsInAnyOrder(getView("All")));
    }

    @Test
    public void testCyclesWithThreeUnclassifiedJobsFilters() throws IOException {
        View view1 = createFilteredView("view-1",
                new UnclassifiedJobsFilter(includeMatched.name()));
        View view2 = createFilteredView("view-2",
                new UnclassifiedJobsFilter(includeMatched.name()));
        View view3 = createFilteredView("view-3",
                new UnclassifiedJobsFilter(includeMatched.name()));
        View view4 = createListView("view-4",
                createFreeStyleProject("job-1"),
                createFreeStyleProject("job-2"));

        ViewGraph viewGraph = new ViewGraph(j.getInstance().getViews());
        System.out.println(viewGraph.getCycles());
        assertThat(viewGraph.getCycles(), hasSize(1));
        assertThat(viewGraph.getCycles().iterator().next(), containsInAnyOrder(view1, view2, view3));
        assertThat(viewGraph.getViewsInCycles(), containsInAnyOrder(view1, view2, view3));
        assertThat(viewGraph.getViewsNotInCycles(), containsInAnyOrder(view4, getView("All")));
    }

    @Test
    public void testCycleWithOtherViewsFilterAndUnclassifiedJobsFilter() throws IOException {
        View view1 = createFilteredView("view-1",
                new OtherViewsFilter(includeMatched.name(), "view-2"),
                new OtherViewsFilter(includeMatched.name(), "view-4"));
        View view2 = createListView("view-2",
                createFreeStyleProject("job-1"),
                createFreeStyleProject("job-2"));
        View view3 = createListView("view-3",
                createFreeStyleProject("job-3"),
                createFreeStyleProject("job-4"));
        View view4 = createFilteredView("view-4",
                new UnclassifiedJobsFilter(includeMatched.name()));

        ViewGraph viewGraph = new ViewGraph(j.getInstance().getViews());
        System.out.println(viewGraph.getCycles());
        assertThat(viewGraph.getCycles(), hasSize(1));
        assertThat(viewGraph.getCycles().iterator().next(), containsInAnyOrder(view1, view4));
        assertThat(viewGraph.getViewsInCycles(), containsInAnyOrder(view1, view4));
        assertThat(viewGraph.getViewsNotInCycles(), containsInAnyOrder(view2, view3, getView("All")));
    }
}
