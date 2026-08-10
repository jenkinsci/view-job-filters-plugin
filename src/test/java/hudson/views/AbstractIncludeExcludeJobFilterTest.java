package hudson.views;

import hudson.model.TopLevelItem;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.WithoutJenkins;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import java.util.List;

import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.*;
import static hudson.views.test.JobMocker.freeStyleProject;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@WithJenkins
class AbstractIncludeExcludeJobFilterTest extends AbstractJenkinsTest {

    private static class TestIncludeExcludeJobFilter extends AbstractIncludeExcludeJobFilter {
        public TestIncludeExcludeJobFilter(String includeExcludeTypeString) {
            super(includeExcludeTypeString);
        }

        @Override
        public boolean matches(TopLevelItem item) {
            return item.getName().endsWith("-matched");
        }
    }

	@Test
	@WithoutJenkins
	void testIncludeMatched() {
        TestIncludeExcludeJobFilter filter = new TestIncludeExcludeJobFilter(includeMatched.name());
        List<TopLevelItem> all = asList(
            freeStyleProject().name("0-matched").asItem(),
            freeStyleProject().name("1-matched").asItem(),
            freeStyleProject().name("2-matched").asItem(),
            freeStyleProject().name("3-matched").asItem(),
            freeStyleProject().name("4-unmatched").asItem(),
            freeStyleProject().name("5-unmatched").asItem(),
            freeStyleProject().name("6-unmatched").asItem(),
            freeStyleProject().name("7-unmatched").asItem()
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
	@WithoutJenkins
	void testIncludeUnmatched() {
        TestIncludeExcludeJobFilter filter = new TestIncludeExcludeJobFilter(includeUnmatched.name());
        List<TopLevelItem> all = asList(
            freeStyleProject().name("0-matched").asItem(),
            freeStyleProject().name("1-matched").asItem(),
            freeStyleProject().name("2-matched").asItem(),
            freeStyleProject().name("3-matched").asItem(),
            freeStyleProject().name("4-unmatched").asItem(),
            freeStyleProject().name("5-unmatched").asItem(),
            freeStyleProject().name("6-unmatched").asItem(),
            freeStyleProject().name("7-unmatched").asItem()
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
	@WithoutJenkins
	void testExcludeMatched() {
        TestIncludeExcludeJobFilter filter = new TestIncludeExcludeJobFilter(excludeMatched.name());
        List<TopLevelItem> all = asList(
            freeStyleProject().name("0-matched").asItem(),
            freeStyleProject().name("1-matched").asItem(),
            freeStyleProject().name("2-matched").asItem(),
            freeStyleProject().name("3-matched").asItem(),
            freeStyleProject().name("4-unmatched").asItem(),
            freeStyleProject().name("5-unmatched").asItem(),
            freeStyleProject().name("6-unmatched").asItem(),
            freeStyleProject().name("7-unmatched").asItem()
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
	@WithoutJenkins
	void testExcludeUnmatched() {
        TestIncludeExcludeJobFilter filter = new TestIncludeExcludeJobFilter(excludeUnmatched.name());
        List<TopLevelItem> all = asList(
            freeStyleProject().name("0-matched").asItem(),
            freeStyleProject().name("1-matched").asItem(),
            freeStyleProject().name("2-matched").asItem(),
            freeStyleProject().name("3-matched").asItem(),
            freeStyleProject().name("4-unmatched").asItem(),
            freeStyleProject().name("5-unmatched").asItem(),
            freeStyleProject().name("6-unmatched").asItem(),
            freeStyleProject().name("7-unmatched").asItem()
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


}
