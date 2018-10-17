package hudson.views;

import hudson.cli.BuildCommand.CLICause;
import hudson.model.Cause;
import hudson.model.Cause.RemoteCause;
import hudson.model.Cause.UpstreamCause;
import hudson.model.Cause.UpstreamCause.DeeplyNestedUpstreamCause;
import hudson.model.Cause.UserCause;
import hudson.model.Cause.UserIdCause;
import hudson.model.ListView;
import hudson.triggers.SCMTrigger.SCMTriggerCause;
import hudson.triggers.TimerTrigger.TimerTriggerCause;
import hudson.views.BuildTrendFilter.StatusType;
import org.acegisecurity.context.SecurityContextHolder;
import org.junit.Before;
import org.junit.Test;
import org.jvnet.hudson.test.WithoutJenkins;

import java.util.ArrayList;
import java.util.List;

import static hudson.model.Result.*;
import static hudson.views.AbstractBuildTrendFilter.AmountType.Builds;
import static hudson.views.AbstractBuildTrendFilter.AmountType.Days;
import static hudson.views.AbstractBuildTrendFilter.AmountType.Hours;
import static hudson.views.AbstractBuildTrendFilter.BuildCountType.All;
import static hudson.views.AbstractBuildTrendFilter.BuildCountType.AtLeastOne;
import static hudson.views.AbstractBuildTrendFilter.BuildCountType.Latest;
import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.*;
import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeUnmatched;
import static hudson.views.BuildTrendFilter.StatusType.*;
import static hudson.views.test.ViewJobFilters.buildTrend;
import static hudson.views.test.BuildMocker.build;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class BuildTrendFilterTest extends AbstractJenkinsTest {

    @Before
	public void before() throws Exception {
        // Only necessary if run as part of the whole project:
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
	@WithoutJenkins
	public void testMatchRun() {
    	assertTrue(buildTrend(StatusType.Started).matchesRun(build().started(true).create()));
		assertFalse(buildTrend(StatusType.Started).matchesRun(build().started(false).create()));

		assertTrue(buildTrend(Completed).matchesRun(build().result(SUCCESS).create()));
		assertTrue(buildTrend(Completed).matchesRun(build().result(FAILURE).create()));
		assertTrue(buildTrend(Completed).matchesRun(build().result(UNSTABLE).create()));
		assertFalse(buildTrend(Completed).matchesRun(build().result(ABORTED).create()));
		assertFalse(buildTrend(Completed).matchesRun(build().result(NOT_BUILT).create()));

		assertTrue(buildTrend(Stable).matchesRun(build().result(SUCCESS).create()));
		assertFalse(buildTrend(Stable).matchesRun(build().result(FAILURE).create()));
		assertFalse(buildTrend(Stable).matchesRun(build().result(UNSTABLE).create()));
		assertFalse(buildTrend(Stable).matchesRun(build().result(ABORTED).create()));
		assertFalse(buildTrend(Stable).matchesRun(build().result(NOT_BUILT).create()));

		assertFalse(buildTrend(Failed).matchesRun(build().result(SUCCESS).create()));
		assertTrue(buildTrend(Failed).matchesRun(build().result(FAILURE).create()));
		assertFalse(buildTrend(Failed).matchesRun(build().result(UNSTABLE).create()));
		assertFalse(buildTrend(Failed).matchesRun(build().result(ABORTED).create()));
		assertFalse(buildTrend(Failed).matchesRun(build().result(NOT_BUILT).create()));

		assertFalse(buildTrend(Unstable).matchesRun(build().result(SUCCESS).create()));
		assertFalse(buildTrend(Unstable).matchesRun(build().result(FAILURE).create()));
		assertTrue(buildTrend(Unstable).matchesRun(build().result(UNSTABLE).create()));
		assertFalse(buildTrend(Unstable).matchesRun(build().result(ABORTED).create()));
		assertFalse(buildTrend(Unstable).matchesRun(build().result(NOT_BUILT).create()));

		assertFalse(buildTrend(NotStable).matchesRun(build().result(SUCCESS).create()));
		assertTrue(buildTrend(NotStable).matchesRun(build().result(FAILURE).create()));
		assertTrue(buildTrend(NotStable).matchesRun(build().result(UNSTABLE).create()));
		assertTrue(buildTrend(NotStable).matchesRun(build().result(ABORTED).create()));
		assertFalse(buildTrend(NotStable).matchesRun(build().result(NOT_BUILT).create()));

		assertTrue(buildTrend(TriggeredByUser).matchesRun(build().causes(new UserCause()).create()));
		assertTrue(buildTrend(TriggeredByUser).matchesRun(build().causes(mock(Cause.class), new UserCause()).create()));
		assertTrue(buildTrend(TriggeredByUser).matchesRun(build().causes(new UserIdCause()).create()));
		assertTrue(buildTrend(TriggeredByUser).matchesRun(build().causes(mock(Cause.class), new UserIdCause()).create()));
		assertFalse(buildTrend(TriggeredByUser).matchesRun(build().causes(new CLICause()).create()));
		assertFalse(buildTrend(TriggeredByUser).matchesRun(build().causes(new RemoteCause("host", "")).create()));
		assertFalse(buildTrend(TriggeredByUser).matchesRun(build().causes(new SCMTriggerCause("")).create()));
		assertFalse(buildTrend(TriggeredByUser).matchesRun(build().causes(new TimerTriggerCause()).create()));
		assertFalse(buildTrend(TriggeredByUser).matchesRun(build().causes(mock(UpstreamCause.class)).create()));
		assertFalse(buildTrend(TriggeredByUser).matchesRun(build().causes(mock(DeeplyNestedUpstreamCause.class)).create()));
		assertFalse(buildTrend(TriggeredByUser).matchesRun(build().create()));

		assertFalse(buildTrend(TriggeredByCli).matchesRun(build().causes(new UserCause()).create()));
		assertFalse(buildTrend(TriggeredByCli).matchesRun(build().causes(new UserIdCause()).create()));
		assertTrue(buildTrend(TriggeredByCli).matchesRun(build().causes(new CLICause()).create()));
		assertTrue(buildTrend(TriggeredByCli).matchesRun(build().causes(mock(Cause.class), new CLICause()).create()));
		assertFalse(buildTrend(TriggeredByCli).matchesRun(build().causes(new RemoteCause("host", "")).create()));
		assertFalse(buildTrend(TriggeredByCli).matchesRun(build().causes(new SCMTriggerCause("")).create()));
		assertFalse(buildTrend(TriggeredByCli).matchesRun(build().causes(new TimerTriggerCause()).create()));
		assertFalse(buildTrend(TriggeredByCli).matchesRun(build().causes(mock(UpstreamCause.class)).create()));
		assertFalse(buildTrend(TriggeredByCli).matchesRun(build().causes(mock(DeeplyNestedUpstreamCause.class)).create()));
		assertFalse(buildTrend(TriggeredByCli).matchesRun(build().create()));

		assertFalse(buildTrend(TriggeredByRemote).matchesRun(build().causes(new UserCause()).create()));
		assertFalse(buildTrend(TriggeredByRemote).matchesRun(build().causes(new UserIdCause()).create()));
		assertFalse(buildTrend(TriggeredByRemote).matchesRun(build().causes(new CLICause()).create()));
		assertTrue(buildTrend(TriggeredByRemote).matchesRun(build().causes(new RemoteCause("host", "")).create()));
		assertTrue(buildTrend(TriggeredByRemote).matchesRun(build().causes(mock(Cause.class), new RemoteCause("host", "")).create()));
		assertFalse(buildTrend(TriggeredByRemote).matchesRun(build().causes(new SCMTriggerCause("")).create()));
		assertFalse(buildTrend(TriggeredByRemote).matchesRun(build().causes(new TimerTriggerCause()).create()));
		assertFalse(buildTrend(TriggeredByRemote).matchesRun(build().causes(mock(UpstreamCause.class)).create()));
		assertFalse(buildTrend(TriggeredByRemote).matchesRun(build().causes(mock(DeeplyNestedUpstreamCause.class)).create()));
		assertFalse(buildTrend(TriggeredByRemote).matchesRun(build().create()));

		assertFalse(buildTrend(TriggeredByScmPoll).matchesRun(build().causes(new UserCause()).create()));
		assertFalse(buildTrend(TriggeredByScmPoll).matchesRun(build().causes(new UserIdCause()).create()));
		assertFalse(buildTrend(TriggeredByScmPoll).matchesRun(build().causes(new CLICause()).create()));
		assertFalse(buildTrend(TriggeredByScmPoll).matchesRun(build().causes(new RemoteCause("host", "")).create()));
		assertTrue(buildTrend(TriggeredByScmPoll).matchesRun(build().causes(new SCMTriggerCause("")).create()));
		assertTrue(buildTrend(TriggeredByScmPoll).matchesRun(build().causes(mock(Cause.class), new SCMTriggerCause("")).create()));
		assertFalse(buildTrend(TriggeredByScmPoll).matchesRun(build().causes(new TimerTriggerCause()).create()));
		assertFalse(buildTrend(TriggeredByScmPoll).matchesRun(build().causes(mock(UpstreamCause.class)).create()));
		assertFalse(buildTrend(TriggeredByScmPoll).matchesRun(build().causes(mock(DeeplyNestedUpstreamCause.class)).create()));
		assertFalse(buildTrend(TriggeredByScmPoll).matchesRun(build().create()));

		assertFalse(buildTrend(TriggeredByTimer).matchesRun(build().causes(new UserCause()).create()));
		assertFalse(buildTrend(TriggeredByTimer).matchesRun(build().causes(new UserIdCause()).create()));
		assertFalse(buildTrend(TriggeredByTimer).matchesRun(build().causes(new CLICause()).create()));
		assertFalse(buildTrend(TriggeredByTimer).matchesRun(build().causes(new RemoteCause("host", "")).create()));
		assertFalse(buildTrend(TriggeredByTimer).matchesRun(build().causes(new SCMTriggerCause("")).create()));
		assertTrue(buildTrend(TriggeredByTimer).matchesRun(build().causes(new TimerTriggerCause()).create()));
		assertTrue(buildTrend(TriggeredByTimer).matchesRun(build().causes(mock(Cause.class), new TimerTriggerCause()).create()));
		assertFalse(buildTrend(TriggeredByTimer).matchesRun(build().causes(mock(UpstreamCause.class)).create()));
		assertFalse(buildTrend(TriggeredByTimer).matchesRun(build().causes(mock(DeeplyNestedUpstreamCause.class)).create()));
		assertFalse(buildTrend(TriggeredByTimer).matchesRun(build().create()));

		assertFalse(buildTrend(TriggeredByUpstream).matchesRun(build().causes(new UserCause()).create()));
		assertFalse(buildTrend(TriggeredByUpstream).matchesRun(build().causes(new UserIdCause()).create()));
		assertFalse(buildTrend(TriggeredByUpstream).matchesRun(build().causes(new CLICause()).create()));
		assertFalse(buildTrend(TriggeredByUpstream).matchesRun(build().causes(new RemoteCause("host", "")).create()));
		assertFalse(buildTrend(TriggeredByUpstream).matchesRun(build().causes(new SCMTriggerCause("")).create()));
		assertFalse(buildTrend(TriggeredByUpstream).matchesRun(build().causes(new TimerTriggerCause()).create()));
		assertTrue(buildTrend(TriggeredByUpstream).matchesRun(build().causes(mock(UpstreamCause.class)).create()));
		assertTrue(buildTrend(TriggeredByUpstream).matchesRun(build().causes(mock(DeeplyNestedUpstreamCause.class)).create()));
		assertTrue(buildTrend(TriggeredByUpstream).matchesRun(build().causes(mock(Cause.class), mock(UpstreamCause.class)).create()));
		assertTrue(buildTrend(TriggeredByUpstream).matchesRun(build().causes(mock(Cause.class), mock(DeeplyNestedUpstreamCause.class)).create()));
		assertFalse(buildTrend(TriggeredByUpstream).matchesRun(build().create()));
	}

	@Test
	public void testConfigRoundtrip() throws Exception {
		testConfigRoundtrip(
			"build-trend-view-1",
			new BuildTrendFilter(All.name(), Started.name(),0, Builds.name(), includeMatched.name())
		);

		testConfigRoundtrip(
			"build-trend-view-2",
			new BuildTrendFilter(All.name(), Completed.name(),10, Builds.name(), includeUnmatched.name()),
			new BuildTrendFilter(AtLeastOne.name(), Failed.name(),3, Hours.name(), excludeMatched.name())
		);

		testConfigRoundtrip(
			"build-trend-view-3",
			new BuildTrendFilter(Latest.name(), TriggeredByUser.name(),10, Builds.name(), includeMatched.name()),
			new BuildTrendFilter(All.name(), TriggeredByUpstream.name(),0, Days.name(), excludeUnmatched.name()),
			new BuildTrendFilter(AtLeastOne.name(), NotStable.name(),3, Hours.name(), includeUnmatched.name())
		);
	}

	private void testConfigRoundtrip(String viewName, BuildTrendFilter... filters) throws Exception {
		List<BuildTrendFilter> expectedFilters = new ArrayList<BuildTrendFilter>();
		for (BuildTrendFilter filter: filters) {
			expectedFilters.add(new BuildTrendFilter(
					filter.getBuildCountTypeString(),
					filter.getStatusTypeString(),
					filter.getAmount(),
					filter.getAmountTypeString(),
					filter.getIncludeExcludeTypeString()));
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

	private void assertFilterEquals(List<BuildTrendFilter> expectedFilters, List<ViewJobFilter> actualFilters) {
		assertThat(actualFilters.size(), is(expectedFilters.size()));
		for (int i = 0; i < actualFilters.size(); i++) {
			ViewJobFilter actualFilter = actualFilters.get(i);
			BuildTrendFilter expectedFilter = expectedFilters.get(i);
			assertThat(actualFilter, instanceOf(BuildTrendFilter.class));
			assertThat(((BuildTrendFilter)actualFilter).getBuildCountTypeString(), is(expectedFilter.getBuildCountTypeString()));
			assertThat(((BuildTrendFilter)actualFilter).getStatusTypeString(), is(expectedFilter.getStatusTypeString()));
			assertThat(((BuildTrendFilter)actualFilter).getAmount(), is(expectedFilter.getAmount()));
			assertThat(((BuildTrendFilter)actualFilter).getAmountTypeString(), is(expectedFilter.getAmountTypeString()));
			assertThat(((BuildTrendFilter)actualFilter).getIncludeExcludeTypeString(), is(expectedFilter.getIncludeExcludeTypeString()));
		}
	}

}
