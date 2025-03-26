package hudson.views;

import hudson.model.FreeStyleProject;
import hudson.model.ListView;
import hudson.model.TopLevelItem;
import hudson.model.View;
import hudson.views.test.JobMocker;
import hudson.views.test.JobType;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.WithoutJenkins;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static hudson.views.test.JobMocker.jobOfType;
import static hudson.views.test.JobType.*;
import static hudson.views.test.ViewJobFilters.downstream;
import static hudson.views.test.ViewJobFilters.upstream;
import static hudson.views.test.ViewJobFilters.upstreamDownstream;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

@WithJenkins
class UpstreamDownstreamJobsFilterTest extends AbstractJenkinsTest {
	@Test
	@WithoutJenkins
	void testDontIncludeUpstreamOrDownstream() {
        for (JobType jobType: availableJobTypes(FREE_STYLE_PROJECT, MAVEN_MODULE_SET, MATRIX_PROJECT)) {
            List<TopLevelItem> all = asList(
                    jobOfType(jobType).name("job-0").asItem(),
                    jobOfType(jobType).name("job-1").asItem(),
                    jobOfType(jobType).name("job-2").asItem(),
                    jobOfType(jobType).name("job-3").asItem(),
                    jobOfType(jobType).name("job-4").asItem(),
                    jobOfType(jobType).name("job-5").asItem()
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

	@Test
	@WithoutJenkins
	void testUpstream() {
        for (JobType jobType: availableJobTypes(FREE_STYLE_PROJECT, MAVEN_MODULE_SET, MATRIX_PROJECT)) {
            List<TopLevelItem> all = getUpstreamDownstreamGraph(jobType);

            View view = null;

            assertThat(upstream(false, false).filter(list(all.get(0)), all, view), is(list(
                    all.get(0)
            )));
            assertThat(upstream(false, true).filter(Collections.singletonList(all.get(0)), all, view), is(list(
            )));
            assertThat(upstream(true, false).filter(Collections.singletonList(all.get(0)), all, view), is(list(
                    all.get(0)
            )));
            assertThat(upstream(true, true).filter(Collections.singletonList(all.get(0)), all, view), is(list(
            )));

            assertThat(upstream(false, false).filter(Collections.singletonList(all.get(1)), all, view), is(list(
                    all.get(0),
                    all.get(1)
            )));
            assertThat(upstream(false, true).filter(Collections.singletonList(all.get(1)), all, view), is(list(
                    all.get(0)
            )));
            assertThat(upstream(true, false).filter(Collections.singletonList(all.get(1)), all, view), is(list(
                    all.get(0),
                    all.get(1)
            )));
            assertThat(upstream(true, true).filter(Collections.singletonList(all.get(1)), all, view), is(list(
                    all.get(0)
            )));

            assertThat(upstream(false, false).filter(Collections.singletonList(all.get(2)), all, view), is(list(
                    all.get(1),
                    all.get(2)
            )));
            assertThat(upstream(false, true).filter(Collections.singletonList(all.get(2)), all, view), is(list(
                    all.get(1)
            )));
            assertThat(upstream(true, false).filter(Collections.singletonList(all.get(2)), all, view), is(list(
                    all.get(0),
                    all.get(1),
                    all.get(2)
            )));
            assertThat(upstream(true, true).filter(Collections.singletonList(all.get(2)), all, view), is(list(
                    all.get(0),
                    all.get(1)
            )));

            assertThat(upstream(false, false).filter(Collections.singletonList(all.get(3)), all, view), is(list(
                    all.get(3)
            )));
            assertThat(upstream(false, true).filter(Collections.singletonList(all.get(3)), all, view), is(list(
            )));
            assertThat(upstream(true, false).filter(Collections.singletonList(all.get(3)), all, view), is(list(
                    all.get(3)
            )));
            assertThat(upstream(true, true).filter(Collections.singletonList(all.get(3)), all, view), is(list(
            )));

            assertThat(upstream(false, false).filter(Collections.singletonList(all.get(4)), all, view), is(list(
                    all.get(2),
                    all.get(4)
            )));
            assertThat(upstream(false, true).filter(Collections.singletonList(all.get(4)), all, view), is(list(
                    all.get(2)
            )));
            assertThat(upstream(true, false).filter(Collections.singletonList(all.get(4)), all, view), is(list(
                    all.get(0),
                    all.get(1),
                    all.get(2),
                    all.get(4)
            )));
            assertThat(upstream(true, true).filter(Collections.singletonList(all.get(4)), all, view), is(list(
                    all.get(0),
                    all.get(1),
                    all.get(2)
            )));

            assertThat(upstream(false, false).filter(Collections.singletonList(all.get(5)), all, view), is(list(
                    all.get(1),
                    all.get(5)
            )));
            assertThat(upstream(false, true).filter(Collections.singletonList(all.get(5)), all, view), is(list(
                    all.get(1)
            )));
            assertThat(upstream(true, false).filter(Collections.singletonList(all.get(5)), all, view), is(list(
                    all.get(0),
                    all.get(1),
                    all.get(5)
            )));
            assertThat(upstream(true, true).filter(Collections.singletonList(all.get(5)), all, view), is(list(
                    all.get(0),
                    all.get(1)
            )));

            assertThat(upstream(false, false).filter(Collections.singletonList(all.get(6)), all, view), is(list(
                    all.get(2),
                    all.get(5),
                    all.get(6)
            )));
            assertThat(upstream(false, true).filter(Collections.singletonList(all.get(6)), all, view), is(list(
                    all.get(2),
                    all.get(5)
            )));
            assertThat(upstream(true, false).filter(Collections.singletonList(all.get(6)), all, view), is(list(
                    all.get(0),
                    all.get(1),
                    all.get(2),
                    all.get(5),
                    all.get(6)
            )));
            assertThat(upstream(true, true).filter(Collections.singletonList(all.get(6)), all, view), is(list(
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
    }

	@Test
	@WithoutJenkins
	void testDownstream() {
        for (JobType jobType: availableJobTypes(FREE_STYLE_PROJECT, MAVEN_MODULE_SET, MATRIX_PROJECT)) {
            List<TopLevelItem> all = getUpstreamDownstreamGraph(jobType);

            View view = null;

            assertThat(downstream(false, false).filter(list(all.get(0)), all, view), is(list(
                    all.get(0),
                    all.get(1)
            )));
            assertThat(downstream(false, true).filter(Collections.singletonList(all.get(0)), all, view), is(list(
                    all.get(1)
            )));
            assertThat(downstream(true, false).filter(Collections.singletonList(all.get(0)), all, view), is(list(
                    all.get(0),
                    all.get(1),
                    all.get(2),
                    all.get(4),
                    all.get(5),
                    all.get(6)
            )));
            assertThat(downstream(true, true).filter(Collections.singletonList(all.get(0)), all, view), is(list(
                    all.get(1),
                    all.get(2),
                    all.get(4),
                    all.get(5),
                    all.get(6)
            )));

            assertThat(downstream(false, false).filter(Collections.singletonList(all.get(1)), all, view), is(list(
                    all.get(1),
                    all.get(2),
                    all.get(5)
            )));
            assertThat(downstream(false, true).filter(Collections.singletonList(all.get(1)), all, view), is(list(
                    all.get(2),
                    all.get(5)
            )));
            assertThat(downstream(true, false).filter(Collections.singletonList(all.get(1)), all, view), is(list(
                    all.get(1),
                    all.get(2),
                    all.get(4),
                    all.get(5),
                    all.get(6)
            )));
            assertThat(downstream(true, true).filter(Collections.singletonList(all.get(1)), all, view), is(list(
                    all.get(2),
                    all.get(4),
                    all.get(5),
                    all.get(6)
            )));

            assertThat(downstream(false, false).filter(Collections.singletonList(all.get(2)), all, view), is(list(
                    all.get(2),
                    all.get(4),
                    all.get(6)
            )));
            assertThat(downstream(false, true).filter(Collections.singletonList(all.get(2)), all, view), is(list(
                    all.get(4),
                    all.get(6)
            )));
            assertThat(downstream(true, false).filter(Collections.singletonList(all.get(2)), all, view), is(list(
                    all.get(2),
                    all.get(4),
                    all.get(6)
            )));
            assertThat(downstream(true, true).filter(Collections.singletonList(all.get(2)), all, view), is(list(
                    all.get(4),
                    all.get(6)
            )));

            assertThat(downstream(false, false).filter(Collections.singletonList(all.get(3)), all, view), is(list(
                    all.get(3)
            )));
            assertThat(downstream(false, true).filter(Collections.singletonList(all.get(3)), all, view), is(list(
            )));
            assertThat(downstream(true, false).filter(Collections.singletonList(all.get(3)), all, view), is(list(
                    all.get(3)
            )));
            assertThat(downstream(true, true).filter(Collections.singletonList(all.get(3)), all, view), is(list(
            )));

            assertThat(downstream(false, false).filter(Collections.singletonList(all.get(4)), all, view), is(list(
                    all.get(4)
            )));
            assertThat(downstream(false, true).filter(Collections.singletonList(all.get(4)), all, view), is(list(
            )));
            assertThat(downstream(true, false).filter(Collections.singletonList(all.get(4)), all, view), is(list(
                    all.get(4)
            )));
            assertThat(downstream(true, true).filter(Collections.singletonList(all.get(4)), all, view), is(list(
            )));

            assertThat(downstream(false, false).filter(Collections.singletonList(all.get(5)), all, view), is(list(
                    all.get(5),
                    all.get(6)
            )));
            assertThat(downstream(false, true).filter(Collections.singletonList(all.get(5)), all, view), is(list(
                    all.get(6)
            )));
            assertThat(downstream(true, false).filter(Collections.singletonList(all.get(5)), all, view), is(list(
                    all.get(5),
                    all.get(6)
            )));
            assertThat(downstream(true, true).filter(Collections.singletonList(all.get(5)), all, view), is(list(
                    all.get(6)
            )));

            assertThat(downstream(false, false).filter(Collections.singletonList(all.get(6)), all, view), is(list(
                    all.get(6)
            )));
            assertThat(downstream(false, true).filter(Collections.singletonList(all.get(6)), all, view), is(list(
            )));
            assertThat(downstream(true, false).filter(Collections.singletonList(all.get(6)), all, view), is(list(
                    all.get(6)
            )));
            assertThat(downstream(true, true).filter(Collections.singletonList(all.get(6)), all, view), is(list(
            )));

            assertThat(downstream(false, false).filter(asList(all.get(1), all.get(5)), all, view), is(list(
                    all.get(1),
                    all.get(2),
                    all.get(5),
                    all.get(6)
            )));
            assertThat(downstream(false, true).filter(asList(all.get(1), all.get(5)), all, view), is(list(
                    all.get(2),
                    all.get(5),
                    all.get(6)
            )));
            assertThat(downstream(true, false).filter(asList(all.get(1), all.get(5)), all, view), is(list(
                    all.get(1),
                    all.get(2),
                    all.get(4),
                    all.get(5),
                    all.get(6)
            )));
            assertThat(downstream(true, true).filter(asList(all.get(1), all.get(5)), all, view), is(list(
                    all.get(2),
                    all.get(4),
                    all.get(5),
                    all.get(6)
            )));


            assertThat(downstream(false, false).filter(asList(all.get(2), all.get(5)), all, view), is(list(
                    all.get(2),
                    all.get(4),
                    all.get(5),
                    all.get(6)
            )));
            assertThat(downstream(false, true).filter(asList(all.get(2), all.get(5)), all, view), is(list(
                    all.get(4),
                    all.get(6)
            )));
            assertThat(downstream(true, false).filter(asList(all.get(2), all.get(5)), all, view), is(list(
                    all.get(2),
                    all.get(4),
                    all.get(5),
                    all.get(6)
            )));
            assertThat(downstream(true, true).filter(asList(all.get(2), all.get(5)), all, view), is(list(
                    all.get(4),
                    all.get(6)
            )));
        }
    }

    private static List<TopLevelItem> getUpstreamDownstreamGraph(JobType jobType) {
        /*
        job0
          |
        job1----+
          |     |
        job2-+ job5
          |  |  |
        job4 +-job6
        */

        JobMocker<FreeStyleProject> job0 = jobOfType(jobType).name("job-0");
        JobMocker<FreeStyleProject> job1 = jobOfType(jobType).name("job-1");
        JobMocker<FreeStyleProject> job2 = jobOfType(jobType).name("job-2");
        JobMocker<FreeStyleProject> job3 = jobOfType(jobType).name("job-3");
        JobMocker<FreeStyleProject> job4 = jobOfType(jobType).name("job-4");
        JobMocker<FreeStyleProject> job5 = jobOfType(jobType).name("job-5");
        JobMocker<FreeStyleProject> job6 = jobOfType(jobType).name("job-6");

        job0.upstream();
        job1.upstream(job0.asJob());
        job2.upstream(job1.asJob());
        job3.upstream();
        job4.upstream(job2.asJob());
        job5.upstream(job1.asJob());
        job6.upstream(job2.asJob(), job5.asJob());

        return asList(
            job0.asItem(),
            job1.asItem(),
            job2.asItem(),
            job3.asItem(),
            job4.asItem(),
            job5.asItem(),
            job6.asItem()
        );
    }

    private static List<TopLevelItem> list(TopLevelItem... items) {
        return asList(items);
    }


	@Test
	void testConfigRoundtrip() throws Exception {
        testConfigRoundtrip(
            "view-1",
            new UpstreamDownstreamJobsFilter(true, false, true, false)
        );

        testConfigRoundtrip(
            "view-2",
            new UpstreamDownstreamJobsFilter(false, true, false, true),
            new UpstreamDownstreamJobsFilter(false, false, true, true)
        );

    }

    private void testConfigRoundtrip(String viewName, UpstreamDownstreamJobsFilter... filters) throws Exception {
        List<UpstreamDownstreamJobsFilter> expectedFilters = new ArrayList<>();
        for (UpstreamDownstreamJobsFilter filter : filters) {
            expectedFilters.add(new UpstreamDownstreamJobsFilter(
                filter.isIncludeDownstream(), filter.isIncludeUpstream(), filter.isRecursive(), filter.isExcludeOriginals()));
        }

        ListView view = createFilteredView(viewName, filters);
        j.configRoundtrip(view);

        ListView viewAfterRoundtrip = (ListView) j.getInstance().getView(viewName);
        assertFilterEquals(expectedFilters, viewAfterRoundtrip.getJobFilters());

        viewAfterRoundtrip.save();
        j.getInstance().reload();

        ListView viewAfterReload = (ListView) j.getInstance().getView(viewName);
        assertFilterEquals(expectedFilters, viewAfterReload.getJobFilters());
    }

    private static void assertFilterEquals(List<UpstreamDownstreamJobsFilter> expectedFilters, List<ViewJobFilter> actualFilters) {
        assertThat(actualFilters.size(), is(expectedFilters.size()));
        for (int i = 0; i < actualFilters.size(); i++) {
            ViewJobFilter actualFilter = actualFilters.get(i);
            UpstreamDownstreamJobsFilter expectedFilter = expectedFilters.get(i);
            assertThat(actualFilter, instanceOf(UpstreamDownstreamJobsFilter.class));
            assertThat(((UpstreamDownstreamJobsFilter)actualFilter).isIncludeDownstream(), is(expectedFilter.isIncludeDownstream()));
            assertThat(((UpstreamDownstreamJobsFilter)actualFilter).isIncludeUpstream(), is(expectedFilter.isIncludeUpstream()));
            assertThat(((UpstreamDownstreamJobsFilter)actualFilter).isRecursive(), is(expectedFilter.isRecursive()));
            assertThat(((UpstreamDownstreamJobsFilter)actualFilter).isExcludeOriginals(), is(expectedFilter.isExcludeOriginals()));
        }
    }

}
