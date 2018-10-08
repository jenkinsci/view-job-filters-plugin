package hudson.views;

import hudson.model.FreeStyleProject;
import hudson.model.TopLevelItem;
import hudson.model.View;
import hudson.views.test.JobMocker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static hudson.views.test.JobMocker.freeStyleProject;
import static hudson.views.test.ViewJobFilters.upstream;
import static hudson.views.test.ViewJobFilters.upstreamDownstream;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class UpstreamDownstreamJobsFilterTest extends AbstractHudsonTest {
    @Test
    public void testDontIncludeUpstreamOrDownstream() {
        List<TopLevelItem> all = asList(
          freeStyleProject().name("job-0").asItem(),
          freeStyleProject().name("job-1").asItem(),
          freeStyleProject().name("job-2").asItem(),
          freeStyleProject().name("job-3").asItem(),
          freeStyleProject().name("job-4").asItem(),
          freeStyleProject().name("job-5").asItem()
        );
        List<TopLevelItem> jobs = asList(
          all.get(0),
          all.get(2),
          all.get(4)
        );
        assertThat(upstreamDownstream(false, false, false, false).filter(jobs, all, null), is(asList(
          all.get(0),
          all.get(2),
          all.get(4)
        )));
        assertThat(upstreamDownstream(false, false, true, false).filter(jobs, all, null), is(asList(
          all.get(0),
          all.get(2),
          all.get(4)
        )));
        assertThat(upstreamDownstream(false, false, false, true).filter(jobs, all, null), hasSize(0));
        assertThat(upstreamDownstream(false, false, true, true).filter(jobs, all, null), hasSize(0));
    }

    @Test
    public void testUpstream() {
        /*
        job0
          |
        job1----+
          |     |
        job2-+ job5
          |  |  |
        job4 +-job6
        */

        JobMocker<FreeStyleProject> job0 = freeStyleProject().name("job-0");
        JobMocker<FreeStyleProject> job1 = freeStyleProject().name("job-1");
        JobMocker<FreeStyleProject> job2 = freeStyleProject().name("job-2");
        JobMocker<FreeStyleProject> job3 = freeStyleProject().name("job-3");
        JobMocker<FreeStyleProject> job4 = freeStyleProject().name("job-4");
        JobMocker<FreeStyleProject> job5 = freeStyleProject().name("job-5");
        JobMocker<FreeStyleProject> job6 = freeStyleProject().name("job-6");

        job0.upstream();
        job1.upstream(job0.asJob());
        job2.upstream(job1.asJob());
        job3.upstream();
        job4.upstream(job2.asJob());
        job5.upstream(job1.asJob());
        job6.upstream(job2.asJob(), job5.asJob());

        List<TopLevelItem> all = asList(
            job0.asItem(),
            job1.asItem(),
            job2.asItem(),
            job3.asItem(),
            job4.asItem(),
            job5.asItem(),
            job6.asItem()
        );

        View view = null;

        assertThat(upstream(false, false).filter(list(all.get(0)), all, view), is(list(
            all.get(0)
        )));
        assertThat(upstream(false, true).filter(asList(all.get(0)), all, view), is(list(
        )));
        assertThat(upstream(true, false).filter(asList(all.get(0)), all, view), is(list(
            all.get(0)
        )));
        assertThat(upstream(true, true).filter(asList(all.get(0)), all, view), is(list(
        )));

        assertThat(upstream(false, false).filter(asList(all.get(1)), all, view), is(list(
            all.get(0),
            all.get(1)
        )));
        assertThat(upstream(false, true).filter(asList(all.get(1)), all, view), is(list(
            all.get(0)
        )));
        assertThat(upstream(true, false).filter(asList(all.get(1)), all, view), is(list(
            all.get(0),
            all.get(1)
        )));
        assertThat(upstream(true, true).filter(asList(all.get(1)), all, view), is(list(
            all.get(0)
        )));

        assertThat(upstream(false, false).filter(asList(all.get(2)), all, view), is(list(
            all.get(1),
            all.get(2)
        )));
        assertThat(upstream(false, true).filter(asList(all.get(2)), all, view), is(list(
            all.get(1)
        )));
        assertThat(upstream(true, false).filter(asList(all.get(2)), all, view), is(list(
            all.get(0),
            all.get(1),
            all.get(2)
        )));
        assertThat(upstream(true, true).filter(asList(all.get(2)), all, view), is(list(
            all.get(0),
            all.get(1)
        )));

        assertThat(upstream(false, false).filter(asList(all.get(3)), all, view), is(list(
            all.get(3)
        )));
        assertThat(upstream(false, true).filter(asList(all.get(3)), all, view), is(list(
        )));
        assertThat(upstream(true, false).filter(asList(all.get(3)), all, view), is(list(
            all.get(3)
        )));
        assertThat(upstream(true, true).filter(asList(all.get(3)), all, view), is(list(
        )));

        assertThat(upstream(false, false).filter(asList(all.get(4)), all, view), is(list(
            all.get(2),
            all.get(4)
        )));
        assertThat(upstream(false, true).filter(asList(all.get(4)), all, view), is(list(
            all.get(2)
        )));
        assertThat(upstream(true, false).filter(asList(all.get(4)), all, view), is(list(
            all.get(0),
            all.get(1),
            all.get(2),
            all.get(4)
        )));
        assertThat(upstream(true, true).filter(asList(all.get(4)), all, view), is(list(
            all.get(0),
            all.get(1),
            all.get(2)
        )));

        assertThat(upstream(false, false).filter(asList(all.get(5)), all, view), is(list(
            all.get(1),
            all.get(5)
        )));
        assertThat(upstream(false, true).filter(asList(all.get(5)), all, view), is(list(
            all.get(1)
        )));
        assertThat(upstream(true, false).filter(asList(all.get(5)), all, view), is(list(
            all.get(0),
            all.get(1),
            all.get(5)
        )));
        assertThat(upstream(true, true).filter(asList(all.get(5)), all, view), is(list(
            all.get(0),
            all.get(1)
        )));

        assertThat(upstream(false, false).filter(asList(all.get(6)), all, view), is(list(
            all.get(2),
            all.get(5),
            all.get(6)
        )));
        assertThat(upstream(false, true).filter(asList(all.get(6)), all, view), is(list(
            all.get(2),
            all.get(5)
        )));
        assertThat(upstream(true, false).filter(asList(all.get(6)), all, view), is(list(
            all.get(0),
            all.get(1),
            all.get(2),
            all.get(5),
            all.get(6)
        )));
        assertThat(upstream(true, true).filter(asList(all.get(6)), all, view), is(list(
            all.get(0),
            all.get(1),
            all.get(2),
            all.get(5)
        )));

        assertThat(upstream(false, false).filter(asList(all.get(2), all.get(6)), all, view), is(list(
            all.get(1),
            all.get(2),
            all.get(5),
            all.get(6)
        )));
        assertThat(upstream(false, true).filter(asList(all.get(2), all.get(6)), all, view), is(list(
            all.get(1),
            all.get(2),
            all.get(5)
        )));
        assertThat(upstream(true, false).filter(asList(all.get(2), all.get(6)), all, view), is(list(
            all.get(0),
            all.get(1),
            all.get(2),
            all.get(5),
            all.get(6)
        )));
        assertThat(upstream(true, true).filter(asList(all.get(2), all.get(6)), all, view), is(list(
            all.get(0),
            all.get(1),
            all.get(2),
            all.get(5)
        )));

        assertThat(upstream(false, false).filter(asList(all.get(4), all.get(6)), all, view), is(list(
            all.get(2),
            all.get(4),
            all.get(5),
            all.get(6)
        )));
        assertThat(upstream(false, true).filter(asList(all.get(4), all.get(6)), all, view), is(list(
            all.get(2),
            all.get(5)
        )));
        assertThat(upstream(true, false).filter(asList(all.get(4), all.get(6)), all, view), is(list(
            all.get(0),
            all.get(1),
            all.get(2),
            all.get(4),
            all.get(5),
            all.get(6)
        )));
        assertThat(upstream(true, true).filter(asList(all.get(4), all.get(6)), all, view), is(list(
            all.get(0),
            all.get(1),
            all.get(2),
            all.get(5)
        )));
    }

    private List<TopLevelItem> list(TopLevelItem... items) {
        return asList(items);
    }
}
