package hudson.views;

import hudson.model.AllView;
import hudson.model.ListView;
import io.jenkins.plugins.casc.misc.junit.jupiter.AbstractRoundTripTest;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@WithJenkins
class ViewJobFiltersConfigAsCodeTest extends AbstractRoundTripTest {

    @Override
    protected void assertConfiguredAsExpected(JenkinsRule r, String s) {
        assertThat(r.jenkins.getViews().size(), is(2));
        AllView allView = (AllView) r.jenkins.getView("all");
        assertThat(allView, notNullValue());
        ListView listView = (ListView) r.jenkins.getView("test");
        assertThat(listView, notNullValue());
        assertThat(listView.getIncludeRegex(), is(".*"));
        assertThat(listView.getDescription(), is("test view"));
        assertThat(listView.getJobFilters().get(0), is(instanceOf(BuildDurationFilter.class)));
        BuildDurationFilter buildDurationFilter = (BuildDurationFilter) listView.getJobFilters().get(0);
        assertThat( buildDurationFilter.getAmount(), is(24.0F));
        assertThat(buildDurationFilter.getAmountTypeString(), is("Hours"));
        assertThat(buildDurationFilter.isLessThan(), is(true));
        assertThat(buildDurationFilter.getBuildDurationMinutes(), is("10"));
        assertThat(buildDurationFilter.getBuildCountTypeString(), is("Latest"));
        assertThat(buildDurationFilter.getIncludeExcludeTypeString(), is("includeMatched"));
        assertThat(listView.getColumns().size(), is(7));
        assertThat(listView.getColumns().get(0).getDescriptor().getDisplayName(), is("Status"));
        assertThat(listView.getColumns().get(3).getDescriptor().getDisplayName(), is("Last Success"));
    }

    @Override
    protected String stringInLogExpected() {
        return "Setting class hudson.model.ListView.name = test";
    }
}
