package hudson.views;

import hudson.matrix.MatrixProject;
import hudson.maven.MavenModuleSet;
import hudson.model.FreeStyleProject;
import hudson.model.ListView;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import java.util.ArrayList;
import java.util.List;

import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.*;
import static hudson.views.test.JobMocker.freeStyleProject;
import static hudson.views.test.JobMocker.jobOfType;
import static hudson.views.test.JobType.*;
import static hudson.views.test.ViewJobFilters.jobType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@WithJenkins
class JobTypeFilterTest extends AbstractJenkinsTest {

	@Test
	void testMatch() {

		assertTrue(jobType(new FreeStyleProject.DescriptorImpl()).matches(freeStyleProject().asItem()));
		assertFalse(jobType("freestyleproject").matches(freeStyleProject().asItem()));

		if (MATRIX_PROJECT.isAvailable()) {
			assertTrue(jobType(new MatrixProject.DescriptorImpl()).matches(jobOfType(MATRIX_PROJECT).asItem()));
			assertFalse(jobType(new FreeStyleProject.DescriptorImpl()).matches(jobOfType(MATRIX_PROJECT).asItem()));
		}

		if (MAVEN_MODULE_SET.isAvailable()) {
			assertTrue(jobType(new MavenModuleSet.DescriptorImpl()).matches(jobOfType(MAVEN_MODULE_SET).asItem()));
			assertFalse(jobType(new FreeStyleProject.DescriptorImpl()).matches(jobOfType(MAVEN_MODULE_SET).asItem()));
		}
	}

	@Test
	void testGetJobType() {
		assertThat(jobType(new FreeStyleProject.DescriptorImpl()).getJobType(), instanceOf(FreeStyleProject.DescriptorImpl.class));

		if (MATRIX_PROJECT.isAvailable()) {
			assertThat(jobType(new MatrixProject.DescriptorImpl()).getJobType(), instanceOf(MatrixProject.DescriptorImpl.class));
			assertThat(jobType(new MatrixProject.DescriptorImpl()).getJobType(), not(instanceOf(FreeStyleProject.DescriptorImpl.class)));
        }

		if (MAVEN_MODULE_SET.isAvailable()) {
			assertThat(jobType(new MavenModuleSet.DescriptorImpl()).getJobType(), instanceOf(MavenModuleSet.DescriptorImpl.class));
			assertThat(jobType(new MavenModuleSet.DescriptorImpl()).getJobType(), not(instanceOf(FreeStyleProject.DescriptorImpl.class)));
		}
	}

	@Test
	void testConfigRoundtrip() throws Exception {
		testConfigRoundtrip(
			"job-type-view-1",
			new JobTypeFilter(new FreeStyleProject.DescriptorImpl().getId(), excludeMatched.name())
		);

		if (MATRIX_PROJECT.isAvailable() && MAVEN_MODULE_SET.isAvailable()) {
			testConfigRoundtrip(
					"job-type-view-2",
					new JobTypeFilter(new MatrixProject.DescriptorImpl().getId(), excludeMatched.name()),
					new JobTypeFilter(new MavenModuleSet.DescriptorImpl().getId(), includeUnmatched.name())
			);
		}
	}

	private void testConfigRoundtrip(String viewName, JobTypeFilter... filters) throws Exception {
		List<JobTypeFilter> expectedFilters = new ArrayList<>();
		for (JobTypeFilter filter: filters) {
			expectedFilters.add(new JobTypeFilter(filter.getJobType().clazz.getName(), filter.getIncludeExcludeTypeString()));
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

	private static void assertFilterEquals(List<JobTypeFilter> expectedFilters, List<ViewJobFilter> actualFilters) {
		assertThat(actualFilters.size(), is(expectedFilters.size()));
		for (int i = 0; i < actualFilters.size(); i++) {
			ViewJobFilter actualFilter = actualFilters.get(i);
			JobTypeFilter expectedFilter = expectedFilters.get(i);
			assertThat(actualFilter, instanceOf(JobTypeFilter.class));
			assertThat(((JobTypeFilter)actualFilter).getJobType(), is(expectedFilter.getJobType()));
			assertThat(((JobTypeFilter)actualFilter).getIncludeExcludeTypeString(), is(expectedFilter.getIncludeExcludeTypeString()));
		}
	}

}
