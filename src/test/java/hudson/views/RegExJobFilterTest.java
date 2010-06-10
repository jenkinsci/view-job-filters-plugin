package hudson.views;

import java.util.ArrayList;
import java.util.List;

import hudson.model.ExternalJob;
import hudson.model.TopLevelItem;
import junit.framework.TestCase;

public class RegExJobFilterTest extends TestCase {

	/**
	 * Tests that the example given in the help page works as described.
	 */
	public void testHelpExample() {
		List<TopLevelItem> all = toList("Work_Job", "Work_Nightly", "A-utility-job", "My_Job", "Job2_Nightly", "Util_Nightly", "My_Util");
		List<TopLevelItem> filtered = new ArrayList<TopLevelItem>();
		
		RegExJobFilter includeNonNightly = new RegExJobFilter(".*_Nightly", true, false);
		filtered = includeNonNightly.filter(filtered, all);
		List<TopLevelItem> expected = toList("Work_Job", "A-utility-job", "My_Job", "My_Util");
		assertListEquals(expected, filtered);

		RegExJobFilter excludeUtil = new RegExJobFilter(".*[Uu]til.*", false, true);
		filtered = excludeUtil.filter(filtered, all);
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
			TopLevelItem item = new ExternalJob(name);
			items.add(item);
		}
		return items;
	}
	
	public void testIncludeExclude() {
		doTestIncludeExclude("junit", ".*u.*", false, false, true, false);
		doTestIncludeExclude("junit", ".*u.*", true, false, false, false);
		doTestIncludeExclude("junit", ".*u.*", false, true, false, true);
		doTestIncludeExclude("junit", ".*u.*", true, true, false, false);
		
		doTestIncludeExclude("test", ".*u.*", false, false, false, false);
		doTestIncludeExclude("test", ".*u.*", true, false, true, false);
		doTestIncludeExclude("test", ".*u.*", false, true, false, false);
		doTestIncludeExclude("test", ".*u.*", true, true, false, true);
	}
	
	public void doTestIncludeExclude(String jobName, 
			String regex, boolean negate, boolean exclude, 
			boolean expectInclude, boolean expectExclude) {
		TopLevelItem item = new ExternalJob(jobName);
		RegExJobFilter filter = new RegExJobFilter(regex, negate, exclude);
		assertEquals(expectExclude, filter.exclude(item));
		assertEquals(expectInclude, filter.include(item));
	}
	
}
