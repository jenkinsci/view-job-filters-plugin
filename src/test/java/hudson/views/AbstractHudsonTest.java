package hudson.views;

import hudson.model.Descriptor;
import hudson.model.ListView;
import hudson.util.DescribableList;

import java.io.IOException;
import java.util.ArrayList;

import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Before;
import org.junit.Rule;
import org.jvnet.hudson.test.JenkinsRule;

public abstract class AbstractHudsonTest {
    ArrayList<WorkflowJob> jobs = new ArrayList<>();
	@Rule
	public JenkinsRule j = new JenkinsRule();

	@Before
	public void setUp() throws Exception {

		// create some jobs
		for (int i = 0; i < 10; i++) {
			j.createFreeStyleProject("Job-" + i);
			jobs.add(j.createProject(WorkflowJob.class, "Workflow-" + i));
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
		j.jenkins.addView(v);
	}
}
