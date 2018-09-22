package hudson.views;

import hudson.model.TopLevelItem;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.*;
import static hudson.views.RegExJobFilter.ValueType.NAME;
import static hudson.views.RegExJobFilter.ValueType.SCHEDULE;
import static hudson.views.test.JobMocker.jobOf;
import static hudson.views.test.JobType.FREE_STYLE_PROJECT;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class AbstractIncludeExcludeJobFilterTest extends AbstractHudsonTest {

    private class TestIncludeExcludeJobFilter extends AbstractIncludeExcludeJobFilter {
        public TestIncludeExcludeJobFilter(String includeExcludeTypeString) {
            super(includeExcludeTypeString);
        }

        @Override
        public boolean matches(TopLevelItem item) {
            return item.getName().endsWith("-matched");
        }
    }

    @Test
    public void testIncludeMatched() {
        TestIncludeExcludeJobFilter filter = new TestIncludeExcludeJobFilter(includeMatched.name());
        List<TopLevelItem> all = asList(
            jobOf(FREE_STYLE_PROJECT).withName("0-matched").asItem(),
            jobOf(FREE_STYLE_PROJECT).withName("1-matched").asItem(),
            jobOf(FREE_STYLE_PROJECT).withName("2-matched").asItem(),
            jobOf(FREE_STYLE_PROJECT).withName("3-matched").asItem(),
            jobOf(FREE_STYLE_PROJECT).withName("4-unmatched").asItem(),
            jobOf(FREE_STYLE_PROJECT).withName("5-unmatched").asItem(),
            jobOf(FREE_STYLE_PROJECT).withName("6-unmatched").asItem(),
            jobOf(FREE_STYLE_PROJECT).withName("7-unmatched").asItem()
        );
        List<TopLevelItem> added = asList(
            all.get(0),
            all.get(1),
            all.get(5)
        );
        List<TopLevelItem> expected = asList(
            all.get(0),
            all.get(1),
            all.get(2),
            all.get(3),
            all.get(5)
        );

        List<TopLevelItem> filtered = filter.filter(added, all, null);
        assertThat(filtered, is(expected));
    }

    @Test
    public void testIncludeUnmatched() {
        TestIncludeExcludeJobFilter filter = new TestIncludeExcludeJobFilter(includeUnmatched.name());
        List<TopLevelItem> all = asList(
            jobOf(FREE_STYLE_PROJECT).withName("0-matched").asItem(),
            jobOf(FREE_STYLE_PROJECT).withName("1-matched").asItem(),
            jobOf(FREE_STYLE_PROJECT).withName("2-matched").asItem(),
            jobOf(FREE_STYLE_PROJECT).withName("3-matched").asItem(),
            jobOf(FREE_STYLE_PROJECT).withName("4-unmatched").asItem(),
            jobOf(FREE_STYLE_PROJECT).withName("5-unmatched").asItem(),
            jobOf(FREE_STYLE_PROJECT).withName("6-unmatched").asItem(),
            jobOf(FREE_STYLE_PROJECT).withName("7-unmatched").asItem()
        );
        List<TopLevelItem> added = asList(
            all.get(0),
            all.get(1),
            all.get(5)
        );
        List<TopLevelItem> expected = asList(
            all.get(0),
            all.get(1),
            all.get(4),
            all.get(5),
            all.get(6),
            all.get(7)
        );

        List<TopLevelItem> filtered = filter.filter(added, all, null);
        assertThat(filtered, is(expected));
    }

    @Test
    public void testExcludeMatched() {
        TestIncludeExcludeJobFilter filter = new TestIncludeExcludeJobFilter(excludeMatched.name());
        List<TopLevelItem> all = asList(
            jobOf(FREE_STYLE_PROJECT).withName("0-matched").asItem(),
            jobOf(FREE_STYLE_PROJECT).withName("1-matched").asItem(),
            jobOf(FREE_STYLE_PROJECT).withName("2-matched").asItem(),
            jobOf(FREE_STYLE_PROJECT).withName("3-matched").asItem(),
            jobOf(FREE_STYLE_PROJECT).withName("4-unmatched").asItem(),
            jobOf(FREE_STYLE_PROJECT).withName("5-unmatched").asItem(),
            jobOf(FREE_STYLE_PROJECT).withName("6-unmatched").asItem(),
            jobOf(FREE_STYLE_PROJECT).withName("7-unmatched").asItem()
        );
        List<TopLevelItem> added = asList(
            all.get(0),
            all.get(1),
            all.get(3),
            all.get(4),
            all.get(5),
            all.get(7)
        );
        List<TopLevelItem> expected = asList(
            all.get(4),
            all.get(5),
            all.get(7)
        );

        List<TopLevelItem> filtered = filter.filter(added, all, null);
        assertThat(filtered, is(expected));
    }

    @Test
    public void testExcludeUnmatched() {
        TestIncludeExcludeJobFilter filter = new TestIncludeExcludeJobFilter(excludeUnmatched.name());
        List<TopLevelItem> all = asList(
            jobOf(FREE_STYLE_PROJECT).withName("0-matched").asItem(),
            jobOf(FREE_STYLE_PROJECT).withName("1-matched").asItem(),
            jobOf(FREE_STYLE_PROJECT).withName("2-matched").asItem(),
            jobOf(FREE_STYLE_PROJECT).withName("3-matched").asItem(),
            jobOf(FREE_STYLE_PROJECT).withName("4-unmatched").asItem(),
            jobOf(FREE_STYLE_PROJECT).withName("5-unmatched").asItem(),
            jobOf(FREE_STYLE_PROJECT).withName("6-unmatched").asItem(),
            jobOf(FREE_STYLE_PROJECT).withName("7-unmatched").asItem()
        );
        List<TopLevelItem> added = asList(
            all.get(0),
            all.get(1),
            all.get(3),
            all.get(4),
            all.get(5),
            all.get(7)
        );
        List<TopLevelItem> expected = asList(
            all.get(0),
            all.get(1),
            all.get(3)
        );

        List<TopLevelItem> filtered = filter.filter(added, all, null);
        assertThat(filtered, is(expected));
    }

    /*
     * Tests that the example given in the wiki works as described.
     *
     * https://wiki.jenkins.io/display/JENKINS/Using+the+View+Job+Filters+Match+Type
     */
    @Test
    public void testHelpExample() {
        List<TopLevelItem> all = asList(
            jobOf(FREE_STYLE_PROJECT).withName("0-Test_Job").asItem(),
            jobOf(FREE_STYLE_PROJECT).withName("1-Test_Job").withTrigger("@midnight").asItem(),
            jobOf(FREE_STYLE_PROJECT).withName("2-Job").asItem(),
            jobOf(FREE_STYLE_PROJECT).withName("3-Test_Job").withTrigger("@daily").asItem(),
            jobOf(FREE_STYLE_PROJECT).withName("4-Job").withTrigger("@midnight").asItem(),
            jobOf(FREE_STYLE_PROJECT).withName("5-Test_Job").withTrigger("@midnight").asItem(),
            jobOf(FREE_STYLE_PROJECT).withName("6-Test_Job").asItem()
        );

        List<TopLevelItem> filtered = new ArrayList<TopLevelItem>();

        RegExJobFilter includeTests = new RegExJobFilter(".*Test.*", includeMatched.name(), NAME.name());
        filtered = includeTests.filter(filtered, all, null);
        assertThat(filtered, is(asList(
            all.get(0),
            all.get(1),
            all.get(3),
            all.get(5),
            all.get(6)
        )));

        RegExJobFilter excludeNonNightly = new RegExJobFilter(".*@midnight.*", excludeUnmatched.name(), SCHEDULE.name());
        filtered = excludeNonNightly.filter(filtered, all, null);
        assertThat(filtered, is(asList(
            all.get(1),
            all.get(5)
        )));
    }
}
