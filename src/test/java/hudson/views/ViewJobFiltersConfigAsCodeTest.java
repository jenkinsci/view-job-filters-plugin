package hudson.views;

import hudson.model.ListView;
import io.jenkins.plugins.casc.misc.RoundTripAbstractTest;
import org.jvnet.hudson.test.RestartableJenkinsRule;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class ViewJobFiltersConfigAsCodeTest extends RoundTripAbstractTest {
    @Override
    protected void assertConfiguredAsExpected(RestartableJenkinsRule restartableJenkinsRule, String s) {
        assertThat(r.j.getInstance().getViews().size(), is(2));
        ListView listView = (ListView) r.j.getInstance().getView("test");
        assertThat(listView, notNullValue());
        assertThat(listView.getIncludeRegex(), is(".*"));
        assertThat(listView.getDescription(), is("test view"));
        assertThat(listView.getJobFilters().get(0), is(instanceOf(BuildDurationFilter.class)));

    }

    @Override
    protected String stringInLogExpected() {
        return "Setting class hudson.model.ListView.name = test";
    }
}
