package hudson.views;

import static hudson.views.AbstractBuildTrendFilter.AmountType.*;
import static hudson.views.AbstractBuildTrendFilter.BuildCountType.*;
import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.*;
import static hudson.views.test.BuildMocker.build;
import static hudson.views.test.JobMocker.freeStyleProject;
import static org.junit.Assert.*;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import hudson.model.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import hudson.views.AbstractBuildTrendFilter.AmountType;
import hudson.views.test.FixedClock;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.*;

public class AbstractBuildTrendFilterTest extends AbstractHudsonTest {

    @After
    public void afterEachTest() {
        Clock.setInstance(Clock.SYSTEM_CLOCK);
    }

    private class TestBuildTrendFilter extends AbstractBuildTrendFilter {
        public TestBuildTrendFilter(String buildCountTypeString, float amount, String amountTypeString) {
            super(buildCountTypeString, amount, amountTypeString, includeMatched.name());
        }

        @Override
        public boolean matchesRun(Run run) {
            return run.getDescription().equals("match");
        }
    }

    @Test
    @WithoutJenkins
    public void testMatchesLatestBuild() {
        for (AmountType amountType : AmountType.values()) {
            AbstractBuildTrendFilter filter = new TestBuildTrendFilter(Latest.name(), 0, amountType.name());
            assertFalse(filter.matches(mock(TopLevelItem.class)));
            assertFalse(filter.matches(freeStyleProject().lastBuilds().asItem()));
            assertFalse(filter.matches(freeStyleProject().lastBuilds(
                build().desc("no-match").create()
            ).asItem()));
            assertFalse(filter.matches(freeStyleProject().lastBuilds(
                build().desc("no-match").create(),
                build().desc("match").create()
            ).asItem()));
            assertTrue(filter.matches(freeStyleProject().lastBuilds(
                build().desc("match").create(),
                build().desc("no-match").create()
            ).asItem()));
        }
    }

    @Test
    @WithoutJenkins
    public void testMatchesAtLeastOneBuild() {
        for (AmountType amountType : AmountType.values()) {
            AbstractBuildTrendFilter filter = new TestBuildTrendFilter(AtLeastOne.name(), 0, amountType.name());
            assertFalse(filter.matches(mock(TopLevelItem.class)));
            assertFalse(filter.matches(freeStyleProject().lastBuilds().asItem()));
            assertFalse(filter.matches(freeStyleProject().lastBuilds(
                build().desc("no-match").create()
            ).asItem()));
            assertTrue(filter.matches(freeStyleProject().lastBuilds(
                build().desc("no-match").create(),
                build().desc("match").create(),
                build().desc("no-match").create()
            ).asItem()));
        }
    }

    @Test
    @WithoutJenkins
    public void testMatchesAllBuilds() {
        for (AmountType amountType : AmountType.values()) {
            AbstractBuildTrendFilter filter = new TestBuildTrendFilter(All.name(), 0, amountType.name());
            assertFalse(filter.matches(mock(TopLevelItem.class)));
            assertFalse(filter.matches(freeStyleProject().lastBuilds().asItem()));
            assertFalse(filter.matches(freeStyleProject().lastBuilds(
                build().desc("no-match").create()
            ).asItem()));
            assertFalse(filter.matches(freeStyleProject().lastBuilds(
                build().desc("match").create(),
                build().desc("no-match").create(),
                build().desc("match").create()
            ).asItem()));
            assertTrue(filter.matches(freeStyleProject().lastBuilds(
                build().desc("match").create(),
                build().desc("match").create(),
                build().desc("match").create()
            ).asItem()));
        }
    }

    @Test
    @WithoutJenkins
    public void testMatchesLatestBuildOfLastFiveBuilds() {
        AbstractBuildTrendFilter filter = new TestBuildTrendFilter(Latest.name(), 5, Builds.name());
        assertFalse(filter.matches(mock(TopLevelItem.class)));
        assertFalse(filter.matches(freeStyleProject().lastBuilds().asItem()));
        assertFalse(filter.matches(freeStyleProject().lastBuilds(
                build().desc("no-match").create()
        ).asItem()));
        assertFalse(filter.matches(freeStyleProject().lastBuilds(
                build().desc("no-match").create(),
                build().desc("match").create()
        ).asItem()));
        assertTrue(filter.matches(freeStyleProject().lastBuilds(
                build().desc("match").create(),
                build().desc("no-match").create()
        ).asItem()));
    }

    @Test
    @WithoutJenkins
    public void testMatchesAtLeastOneOfLastFiveBuilds() {
        AbstractBuildTrendFilter filter = new TestBuildTrendFilter(AtLeastOne.name(), 5, Builds.name());
        assertFalse(filter.matches(mock(TopLevelItem.class)));
        assertFalse(filter.matches(freeStyleProject().lastBuilds().asItem()));
        assertFalse(filter.matches(freeStyleProject().lastBuilds(
            build().desc("no-match").create(),
            build().desc("no-match").create(),
            build().desc("no-match").create(),
            build().desc("no-match").create(),
            build().desc("no-match").create()
        ).asItem()));
        assertFalse(filter.matches(freeStyleProject().lastBuilds(
            build().desc("no-match").create(),
            build().desc("no-match").create(),
            build().desc("no-match").create(),
            build().desc("no-match").create(),
            build().desc("no-match").create(),
            build().desc("match").create()
        ).asItem()));
        assertTrue(filter.matches(freeStyleProject().lastBuilds(
            build().desc("no-match").create(),
            build().desc("no-match").create(),
            build().desc("match").create(),
            build().desc("no-match").create(),
            build().desc("no-match").create(),
            build().desc("match").create()
        ).asItem()));
    }

    @Test
    @WithoutJenkins
    public void testMatchesAllOfLastFiveBuilds() {
        AbstractBuildTrendFilter filter = new TestBuildTrendFilter(All.name(), 5, Builds.name());
        assertFalse(filter.matches(mock(TopLevelItem.class)));
        assertFalse(filter.matches(freeStyleProject().lastBuilds().asItem()));
        assertFalse(filter.matches(freeStyleProject().lastBuilds(
            build().desc("no-match").create(),
            build().desc("no-match").create(),
            build().desc("no-match").create(),
            build().desc("no-match").create(),
            build().desc("no-match").create()
        ).asItem()));
        assertFalse(filter.matches(freeStyleProject().lastBuilds(
            build().desc("no-match").create(),
            build().desc("no-match").create(),
            build().desc("match").create(),
            build().desc("no-match").create(),
            build().desc("no-match").create()
        ).asItem()));
        assertFalse(filter.matches(freeStyleProject().lastBuilds(
            build().desc("match").create(),
            build().desc("no-match").create(),
            build().desc("match").create(),
            build().desc("match").create(),
            build().desc("match").create()
        ).asItem()));
        assertTrue(filter.matches(freeStyleProject().lastBuilds(
            build().desc("match").create()
        ).asItem()));
        assertTrue(filter.matches(freeStyleProject().lastBuilds(
            build().desc("match").create(),
            build().desc("match").create(),
            build().desc("match").create()
        ).asItem()));
        assertTrue(filter.matches(freeStyleProject().lastBuilds(
            build().desc("match").create(),
            build().desc("match").create(),
            build().desc("match").create(),
            build().desc("match").create(),
            build().desc("match").create(),
            build().desc("no-match").create()
        ).asItem()));
    }

    @Test
    @WithoutJenkins
    public void testMatchesLatestBuildInLastSixHours() throws ParseException {
        Clock.setInstance(new FixedClock("2018-01-01 12:00:00"));
        AbstractBuildTrendFilter filter = new TestBuildTrendFilter(Latest.name(), 6, Hours.name());
        assertFalse(filter.matches(mock(TopLevelItem.class)));
        assertFalse(filter.matches(freeStyleProject().lastBuilds().asItem()));
        assertFalse(filter.matches(freeStyleProject().lastBuilds(
            build().desc("match").startTime("2018-01-01 05:00:00").create()
        ).asItem()));
        assertFalse(filter.matches(freeStyleProject().lastBuilds(
            build().desc("no-match").startTime("2018-01-01 10:00:00").create(),
            build().desc("match").startTime("2018-01-01 05:00:00").create()
        ).asItem()));
        assertFalse(filter.matches(freeStyleProject().lastBuilds(
            build().desc("no-match").startTime("2018-01-01 11:00:00").create(),
            build().desc("match").startTime("2018-01-01 10:00:00").create()
        ).asItem()));
        assertTrue(filter.matches(freeStyleProject().lastBuilds(
            build().desc("match").startTime("2018-01-01 11:00:00").create(),
            build().desc("no-match").startTime("2018-01-01 10:00:00").create()
        ).asItem()));
    }

    @Test
    @WithoutJenkins
    public void testMatchesAtLastOneBuildInLastSixHours() throws ParseException {
        Clock.setInstance(new FixedClock("2018-01-01 12:00:00"));
        AbstractBuildTrendFilter filter = new TestBuildTrendFilter(AtLeastOne.name(), 6, Hours.name());
        assertFalse(filter.matches(mock(TopLevelItem.class)));
        assertFalse(filter.matches(freeStyleProject().lastBuilds().asItem()));
        assertFalse(filter.matches(freeStyleProject().lastBuilds(
            build().desc("match").startTime("2018-01-01 05:00:00").create()
        ).asItem()));
        assertFalse(filter.matches(freeStyleProject().lastBuilds(
            build().desc("no-match").startTime("2018-01-01 10:00:00").create(),
            build().desc("match").startTime("2018-01-01 05:00:00").create()
        ).asItem()));
        assertTrue(filter.matches(freeStyleProject().lastBuilds(
            build().desc("no-match").startTime("2018-01-01 11:00:00").create(),
            build().desc("match").startTime("2018-01-01 10:00:00").create(),
            build().desc("no-match").startTime("2018-01-01 09:00:00").create()
        ).asItem()));
    }


    @Test
    @WithoutJenkins
    public void testMatchesAllBuildsInLastSixHours() throws ParseException {
        Clock.setInstance(new FixedClock("2018-01-01 12:00:00"));
        AbstractBuildTrendFilter filter = new TestBuildTrendFilter(All.name(), 6, Hours.name());
        assertFalse(filter.matches(mock(TopLevelItem.class)));
        assertFalse(filter.matches(freeStyleProject().lastBuilds().asItem()));
        assertFalse(filter.matches(freeStyleProject().lastBuilds(
            build().desc("match").startTime("2018-01-01 05:00:00").create()
        ).asItem()));
        assertFalse(filter.matches(freeStyleProject().lastBuilds(
            build().desc("no-match").startTime("2018-01-01 10:00:00").create(),
            build().desc("match").startTime("2018-01-01 05:00:00").create()
        ).asItem()));
        assertFalse(filter.matches(freeStyleProject().lastBuilds(
            build().desc("match").startTime("2018-01-01 11:00:00").create(),
            build().desc("no-match").startTime("2018-01-01 10:00:00").create(),
            build().desc("match").startTime("2018-01-01 05:00:00").create()
        ).asItem()));
        assertFalse(filter.matches(freeStyleProject().lastBuilds(
            build().desc("no-match").startTime("2018-01-01 11:00:00").create(),
            build().desc("match").startTime("2018-01-01 10:00:00").create(),
            build().desc("no-match").startTime("2018-01-01 09:00:00").create()
        ).asItem()));
        assertTrue(filter.matches(freeStyleProject().lastBuilds(
            build().desc("match").startTime("2018-01-01 11:00:00").create(),
            build().desc("match").startTime("2018-01-01 10:00:00").create(),
            build().desc("no-match").startTime("2018-01-01 05:00:00").create()
        ).asItem()));
    }


    @Test
    @WithoutJenkins
    public void testMatchesLatestBuildInLastSixDays() throws ParseException {
        Clock.setInstance(new FixedClock("2018-01-12 00:00:00"));
        AbstractBuildTrendFilter filter = new TestBuildTrendFilter(Latest.name(), 6, Days.name());
        assertFalse(filter.matches(mock(TopLevelItem.class)));
        assertFalse(filter.matches(freeStyleProject().lastBuilds().asItem()));
        assertFalse(filter.matches(freeStyleProject().lastBuilds(
            build().desc("match").startTime("2018-01-05 00:00:00").create()
        ).asItem()));
        assertFalse(filter.matches(freeStyleProject().lastBuilds(
            build().desc("no-match").startTime("2018-01-10 00:00:00").create(),
            build().desc("match").startTime("2018-01-05 00:00:00").create()
        ).asItem()));
        assertFalse(filter.matches(freeStyleProject().lastBuilds(
            build().desc("no-match").startTime("2018-01-11 00:00:00").create(),
            build().desc("match").startTime("2018-01-10 00:00:00").create()
        ).asItem()));
        assertTrue(filter.matches(freeStyleProject().lastBuilds(
            build().desc("match").startTime("2018-01-11 00:00:00").create(),
             build().desc("no-match").startTime("2018-01-10 00:00:00").create()
        ).asItem()));
    }

    @Test
    @WithoutJenkins
    public void testMatchesAtLastOneBuildInLastSixDays() throws ParseException {
        Clock.setInstance(new FixedClock("2018-01-12 00:00:00"));
        AbstractBuildTrendFilter filter = new TestBuildTrendFilter(AtLeastOne.name(), 6, Days.name());
        assertFalse(filter.matches(mock(TopLevelItem.class)));
        assertFalse(filter.matches(freeStyleProject().lastBuilds().asItem()));
        assertFalse(filter.matches(freeStyleProject().lastBuilds(
            build().desc("match").startTime("2018-01-05 00:00:00").create()
        ).asItem()));
        assertFalse(filter.matches(freeStyleProject().lastBuilds(
            build().desc("no-match").startTime("2018-01-10 00:00:00").create(),
            build().desc("match").startTime("2018-01-05 00:00:00").create()
        ).asItem()));
        assertTrue(filter.matches(freeStyleProject().lastBuilds(
            build().desc("no-match").startTime("2018-01-11 00:00:00").create(),
            build().desc("match").startTime("2018-01-10 00:00:00").create(),
            build().desc("no-match").startTime("2018-01-09 00:00:00").create()
        ).asItem()));
    }

    @Test
    @WithoutJenkins
    public void testMatchesAllBuildsInLastSixDays() throws ParseException {
        Clock.setInstance(new FixedClock("2018-01-12 00:00:00"));
        AbstractBuildTrendFilter filter = new TestBuildTrendFilter(All.name(), 6, Days.name());
        assertFalse(filter.matches(mock(TopLevelItem.class)));
        assertFalse(filter.matches(freeStyleProject().lastBuilds().asItem()));
        assertFalse(filter.matches(freeStyleProject().lastBuilds(
            build().desc("match").startTime("2018-01-05 00:00:00").create()
        ).asItem()));
        assertFalse(filter.matches(freeStyleProject().lastBuilds(
            build().desc("no-match").startTime("2018-01-10 00:00:00").create(),
            build().desc("match").startTime("2018-01-05 00:00:00").create()
        ).asItem()));
        assertFalse(filter.matches(freeStyleProject().lastBuilds(
            build().desc("match").startTime("2018-01-11 00:00:00").create(),
            build().desc("no-match").startTime("2018-01-10 00:00:00").create(),
            build().desc("match").startTime("2018-01-05 00:00:00").create()
        ).asItem()));
        assertFalse(filter.matches(freeStyleProject().lastBuilds(
            build().desc("no-match").startTime("2018-01-11 00:00:00").create(),
            build().desc("match").startTime("2018-01-10 00:00:00").create(),
            build().desc("no-match").startTime("2018-01-09 00:00:00").create()
        ).asItem()));
        assertTrue(filter.matches(freeStyleProject().lastBuilds(
            build().desc("match").startTime("2018-01-11 00:00:00").create(),
            build().desc("match").startTime("2018-01-10 00:00:00").create(),
            build().desc("no-match").startTime("2018-01-05 00:00:00").create()
        ).asItem()));
    }

    @Issue("JENKINS-18986")
    @Test
    public void lazyLoading() throws Exception {
        final FreeStyleProject p1 = j.createFreeStyleProject("p1");
        RunLoadCounter.prepare(p1);
        p1.getBuildersList().add(new FailureBuilder());
        for (int i = 0; i < 5; i++) {
            j.assertBuildStatus(Result.FAILURE, p1.scheduleBuild2(0).get());
        }
        final FreeStyleProject p2 = j.createFreeStyleProject("p2");
        j.assertBuildStatusSuccess(p2.scheduleBuild2(0));
        final ViewJobFilter filter = new BuildTrendFilter("AtLeastOne", "Stable", 3, "Builds", "includeMatched");
        assertEquals(Collections.singletonList(p2), RunLoadCounter.assertMaxLoads(p1, 3, new Callable<List<TopLevelItem>>() {
            @Override public List<TopLevelItem> call() throws Exception {
                return filter.filter(new ArrayList<TopLevelItem>(), Arrays.<TopLevelItem>asList(p1, p2), new AllView("_"));
            }
        }));
    }
}