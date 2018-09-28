package hudson.views;

import hudson.matrix.MatrixProject;
import hudson.maven.MavenModuleSet;
import hudson.model.*;
import hudson.views.AbstractBuildTrendFilter.AmountType;
import hudson.views.test.FixedClock;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.FailureBuilder;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.RunLoadCounter;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import static hudson.views.AbstractBuildTrendFilter.AmountType.*;
import static hudson.views.AbstractBuildTrendFilter.BuildCountType.*;
import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.*;
import static hudson.views.test.BuildMocker.build;
import static hudson.views.test.JobMocker.jobOf;
import static hudson.views.test.JobType.FREE_STYLE_PROJECT;
import static hudson.views.test.JobType.MATRIX_PROJECT;
import static hudson.views.test.JobType.MAVEN_MODULE_SET;
import static hudson.views.test.ViewJobFilters.buildDuration;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class BuildDurationFilterTest extends AbstractHudsonTest {

    @Test
    public void testMatchRun() {
        assertTrue(buildDuration(10, "<").matchesRun(build().durationInMinutes(9).create()));
        assertTrue(buildDuration(10, "<").matchesRun(build().durationInSeconds(10 * 60 - 1).create()));
        assertTrue(buildDuration(10, "<").matchesRun(build().durationInMillis(10 * 60 * 1000 - 1).create()));

        assertFalse(buildDuration(10, "<").matchesRun(build().durationInMinutes(10).create()));
        assertFalse(buildDuration(10, "<").matchesRun(build().durationInSeconds(10 * 60 + 1).create()));
        assertFalse(buildDuration(10, "<").matchesRun(build().durationInMillis(10 * 60 * 1000 + 1).create()));

        assertTrue(buildDuration(10, ">").matchesRun(build().durationInMinutes(11).create()));
        assertTrue(buildDuration(10, ">").matchesRun(build().durationInSeconds(10 * 60 + 1).create()));
        assertTrue(buildDuration(10, ">").matchesRun(build().durationInMillis(10 * 60 * 1000 + 1).create()));

        assertFalse(buildDuration(10, ">").matchesRun(build().durationInMinutes(9).create()));
        assertFalse(buildDuration(10, ">").matchesRun(build().durationInSeconds(10 * 60 - 1).create()));
        assertFalse(buildDuration(10, ">").matchesRun(build().durationInMillis(10 * 60 * 1000 - 1).create()));
    }

    @Test
    public void testConfigRoundtrip() throws Exception {
        testConfigRoundtrip(
           "build-duration-view-1",
            new BuildDurationFilter(10, true,
               All.name(), 0, Builds.name(), includeMatched.name())
        );

        testConfigRoundtrip(
           "build-duration-view-2",
            new BuildDurationFilter(10, true,
               All.name(), 10, Builds.name(), includeUnmatched.name()),
            new BuildDurationFilter(4, false,
               AtLeastOne.name(), 3, Hours.name(), excludeMatched.name())
        );

        testConfigRoundtrip(
           "build-duration-view-3",
            new BuildDurationFilter(10, true,
               Latest.name(), 10, Builds.name(), includeMatched.name()),
            new BuildDurationFilter(4, false,
               All.name(), 0, Days.name(), excludeUnmatched.name()),
            new BuildDurationFilter(123432, true,
               AtLeastOne.name(), 3, Hours.name(), includeUnmatched.name())
        );
    }

    private void testConfigRoundtrip(String viewName, BuildDurationFilter... filters) throws Exception {
        List<BuildDurationFilter> expectedFilters = new ArrayList<BuildDurationFilter>();
        for (BuildDurationFilter filter: filters) {
            expectedFilters.add(new BuildDurationFilter(
                new BigDecimal(filter.getBuildDurationMinutes()).floatValue(),
                filter.isLessThan(), filter.getBuildCountTypeString(),
                filter.getAmount(), filter.getAmountTypeString(), filter.getIncludeExcludeTypeString()));
        }

        ListView view = createFilteredView(viewName, filters);
        j.configRoundtrip(view);

        ListView viewAfterRoundtrip = (ListView)j.getInstance().getView(viewName);
        assertFilterEquals(expectedFilters, viewAfterRoundtrip.getJobFilters());

        viewAfterRoundtrip.save();
        j.getInstance().reload();

        ListView viewAfterReload = (ListView)j.getInstance().getView(viewName);
        assertFilterEquals(expectedFilters, viewAfterReload.getJobFilters());
    }

    private void assertFilterEquals(List<BuildDurationFilter> expectedFilters, List<ViewJobFilter> actualFilters) {
        assertThat(actualFilters.size(), is(expectedFilters.size()));
        for (int i = 0; i < actualFilters.size(); i++) {
            ViewJobFilter actualFilter = actualFilters.get(i);
            BuildDurationFilter expectedFilter = expectedFilters.get(i);
            assertThat(actualFilter, instanceOf(BuildDurationFilter.class));
            assertThat(((BuildDurationFilter)actualFilter).getBuildDurationMinutes(), is(expectedFilter.getBuildDurationMinutes()));
            assertThat(((BuildDurationFilter)actualFilter).isLessThan(), is(expectedFilter.isLessThan()));
            assertThat(((BuildDurationFilter)actualFilter).getBuildCountTypeString(), is(expectedFilter.getBuildCountTypeString()));
            assertThat(((BuildDurationFilter)actualFilter).getAmount(), is(expectedFilter.getAmount()));
            assertThat(((BuildDurationFilter)actualFilter).getAmountTypeString(), is(expectedFilter.getAmountTypeString()));
            assertThat(((BuildDurationFilter)actualFilter).getIncludeExcludeTypeString(), is(expectedFilter.getIncludeExcludeTypeString()));
        }
    }

}