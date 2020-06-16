package hudson.views;

import hudson.model.ListView;
import hudson.model.View;
import io.jenkins.plugins.casc.misc.RoundTripAbstractTest;
import org.jvnet.hudson.test.RestartableJenkinsRule;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;


public class ViewJobFiltersConfigAsCodeTest extends RoundTripAbstractTest {
    @Override
    protected void assertConfiguredAsExpected(RestartableJenkinsRule restartableJenkinsRule, String s) {
        assertThat(r.j.getInstance().getViews().size(), is(2));
        ListView listView = (ListView) r.j.getInstance().getView("test");
        assertThat(listView, notNullValue());
        assertThat(listView.getIncludeRegex(), is(".*"));
        assertThat(listView.getDescription(), is("test view"));
        assertThat(listView.getJobFilters().get(0), is(BuildDurationFilter.class));

    }

    @Override
    protected String stringInLogExpected() {
        return null;
    }
}
