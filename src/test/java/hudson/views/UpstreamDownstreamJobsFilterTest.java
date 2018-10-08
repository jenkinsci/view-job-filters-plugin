package hudson.views;

import hudson.model.TopLevelItem;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static hudson.views.test.JobMocker.freeStyleProject;
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
}
