package hudson.views;

import hudson.model.Job;
import hudson.model.TopLevelItem;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

public class AbstractIncludeExcludeJobFilterTest extends AbstractHudsonTest {

    @Test
    public void testIncludeExclude() {
        doTestIncludeExclude("junit", ".*u.*", AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched, true, false);
        doTestIncludeExclude("junit", ".*u.*", AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeUnmatched, false, false);
        doTestIncludeExclude("junit", ".*u.*", AbstractIncludeExcludeJobFilter.IncludeExcludeType.excludeMatched, false, true);
        doTestIncludeExclude("junit", ".*u.*", AbstractIncludeExcludeJobFilter.IncludeExcludeType.excludeUnmatched, false, false);

        doTestIncludeExclude("test", ".*u.*", AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched, false, false);
        doTestIncludeExclude("test", ".*u.*", AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeUnmatched, true, false);
        doTestIncludeExclude("test", ".*u.*", AbstractIncludeExcludeJobFilter.IncludeExcludeType.excludeMatched, false, false);
        doTestIncludeExclude("test", ".*u.*", AbstractIncludeExcludeJobFilter.IncludeExcludeType.excludeUnmatched, false, true);
    }

    private void doTestIncludeExclude(String jobName,
                                      String regex, AbstractIncludeExcludeJobFilter.IncludeExcludeType includeExcludeType, // boolean negate, boolean exclude,
                                      boolean expectInclude, boolean expectExclude) {
        Job item = mock(Job.class, withSettings().extraInterfaces(TopLevelItem.class));
        when(item.getName()).thenReturn(jobName);
        RegExJobFilter filter = new RegExJobFilter(regex, includeExcludeType.toString(), RegExJobFilter.ValueType.NAME.toString());
        boolean matched = filter.matches((TopLevelItem) item);
        assertEquals(expectExclude, filter.exclude(matched));
        assertEquals(expectInclude, filter.include(matched));

    }

    /*
     * Tests that the example given in the help page works as described.
     */
    @Test
    public void testHelpExample() {
        List<TopLevelItem> all = toList("Work_Job", "Work_Nightly", "A-utility-job", "My_Job", "Job2_Nightly", "Util_Nightly", "My_Util");
        List<TopLevelItem> filtered = new ArrayList<TopLevelItem>();

        RegExJobFilter includeNonNightly = new RegExJobFilter(".*_Nightly",
                AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeUnmatched.toString(), // true, false,
                RegExJobFilter.ValueType.NAME.toString());
        filtered = includeNonNightly.filter(filtered, all, null);
        List<TopLevelItem> expected = toList("Work_Job", "A-utility-job", "My_Job", "My_Util");
        assertListEquals(expected, filtered);

        RegExJobFilter excludeUtil = new RegExJobFilter(".*[Uu]til.*",
                AbstractIncludeExcludeJobFilter.IncludeExcludeType.excludeMatched.toString(), // false, true,
                RegExJobFilter.ValueType.NAME.toString());
        filtered = excludeUtil.filter(filtered, all, null);
        expected = toList("Work_Job", "My_Job");
        assertListEquals(expected, filtered);
    }
    private void assertListEquals(List<TopLevelItem> l1, List<TopLevelItem> l2) {
        assertEquals(l1.size(), l2.size());
        for (int i = 0; i < l1.size(); i++) {
            TopLevelItem i1 = l1.get(i);
            TopLevelItem i2 = l2.get(i);
            assertEquals(i1.getName(), i2.getName());
        }
    }
    private List<TopLevelItem> toList(String... names) {
        List<TopLevelItem> items = new ArrayList<TopLevelItem>();
        for (String name: names) {
            Job item =  mock(Job.class, withSettings().extraInterfaces(TopLevelItem.class));
            when(item.getName()).thenReturn(name);
            items.add((TopLevelItem)item);
        }
        return items;
    }
}
