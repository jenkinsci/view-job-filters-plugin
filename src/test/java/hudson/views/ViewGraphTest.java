package hudson.views;

import hudson.model.FreeStyleProject;
import hudson.model.View;
import org.junit.Test;

import java.io.IOException;

import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ViewGraphTest extends AbstractHudsonTest {
    @Test
    public void testNoCycles() throws IOException {
        View view1 = createFilteredView("view-1",
                new OtherViewsFilter(includeMatched.name(), "view-2"));
        View view2 = createFilteredView("view-2",
                new OtherViewsFilter(includeMatched.name(), "view-3"));
        View view3 =createListView("view-3",
                createFreeStyleProject("job-1"),
                createFreeStyleProject("job-2"));

        ViewGraph viewGraph = new ViewGraph(j.getInstance().getViews());
        assertThat(viewGraph.getCycles(), hasSize(0));
        assertThat(viewGraph.getViewsInCycles(), hasSize(0));
        assertThat(viewGraph.getViewsNotInCycles(), containsInAnyOrder(view1, view2, view3, getView("All")));
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
        assertThat(viewGraph.getCycles().get(0), containsInAnyOrder(view1));
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
        assertThat(viewGraph.getCycles().get(0), containsInAnyOrder(view1, view2));
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
        assertThat(viewGraph.getCycles().get(0), containsInAnyOrder(view1, view2, view3));
        assertThat(viewGraph.getViewsInCycles(), containsInAnyOrder(view1, view2, view3));
        assertThat(viewGraph.getViewsNotInCycles(), containsInAnyOrder(view4, view5, getView("All")));
    }
}
