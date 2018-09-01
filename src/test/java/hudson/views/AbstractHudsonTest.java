package hudson.views;

import hudson.model.Descriptor;
import hudson.model.ListView;
import hudson.util.DescribableList;

import java.io.IOException;

import jenkins.model.Jenkins;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.hudson.test.JenkinsRule;

import javax.annotation.CheckForNull;

public abstract class AbstractHudsonTest {

	@Rule
	public	 JenkinsRule j = new JenkinsRule();

	public void setUp() throws Exception {
		// create some jobs
		for (int i = 0; i < 10; i++) {
			j.createFreeStyleProject("Job-" + i);
		}
		addRegexView("View-56", "Job.*[56]");
		addRegexView("View-13", "Job.*[13]");
	}

	private void addRegexView(String name, String regex) throws IOException {
		ListView v = new ListView(name);
		DescribableList<ViewJobFilter, Descriptor<ViewJobFilter>> filters = 
			(DescribableList<ViewJobFilter, Descriptor<ViewJobFilter>>) v.getJobFilters();
		RegExJobFilter regexFilter = new RegExJobFilter(regex, 
				AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched.toString(),
				RegExJobFilter.ValueType.NAME.toString());
		filters.add(regexFilter);
		j.getInstance().addView(v);
	}
}
