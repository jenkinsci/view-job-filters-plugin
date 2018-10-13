package hudson.views;

import hudson.model.TopLevelItem;
import hudson.model.View;
import org.junit.Test;

import java.io.IOException;

import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ViewGraphTest extends AbstractHudsonTest {
    @Test
    public void testNoCyclesWithOtherViewsFilter() throws IOException {
        TopLevelItem job1 = createFreeStyleProject("job-1");
        TopLevelItem job2 = createFreeStyleProject("job-2");
        TopLevelItem job3 = createFreeStyleProject("job-3");

        View view1 = createFilteredView("view-1", new OtherViewsFilter(includeMatched.name(), "view-2"));
        View view2 = createFilteredView("view-2", new OtherViewsFilter(includeMatched.name(), "view-3"));
        View view3 = createListView("view-3", job2, job3);

        ViewGraph viewGraph = new ViewGraph(j.getInstance().getViews());
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

        ViewGraph viewGraph = new ViewGraph(j.getInstance().getViews());
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

        ViewGraph viewGraph = new ViewGraph(j.getInstance().getViews());
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

        ViewGraph viewGraph = new ViewGraph(j.getInstance().getViews());
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

        ViewGraph viewGraph = new ViewGraph(j.getInstance().getViews());
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

        ViewGraph viewGraph = new ViewGraph(j.getInstance().getViews());
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

        ViewGraph viewGraph = new ViewGraph(j.getInstance().getViews());
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

        ViewGraph viewGraph = new ViewGraph(j.getInstance().getViews());
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

        ViewGraph viewGraph = new ViewGraph(j.getInstance().getViews());
        System.out.println(viewGraph.getCycles());
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

        ViewGraph viewGraph = new ViewGraph(j.getInstance().getViews());
        System.out.println(viewGraph.getCycles());
        assertThat(viewGraph.getCycles(), hasSize(1));
        assertThat(viewGraph.getCycles().iterator().next(), containsInAnyOrder(view1, view4));
        assertThat(viewGraph.getViewsInCycles(), containsInAnyOrder(view1, view4));
        assertThat(viewGraph.getViewsNotInCycles(), containsInAnyOrder(view2, view3, getView("All")));

        assertThat(view1.getItems(), containsInAnyOrder(job1, job2, job3));
        assertThat(view2.getItems(), containsInAnyOrder(job2, job3));
        assertThat(view3.getItems(), containsInAnyOrder(job4, job5));
        assertThat(view4.getItems(), containsInAnyOrder(job1, job6, job7));
    }
}
