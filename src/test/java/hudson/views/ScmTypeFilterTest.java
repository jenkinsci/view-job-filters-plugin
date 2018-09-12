package hudson.views;

import hudson.model.Job;
import hudson.model.ListView;
import hudson.model.TopLevelItem;
import hudson.plugins.git.GitSCM;
import hudson.scm.CVSSCM;
import hudson.scm.SCMDescriptor;
import hudson.scm.SubversionSCM;
import hudson.views.test.JobType;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;

import java.util.ArrayList;
import java.util.List;

import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.*;
import static hudson.views.test.JobMocker.jobOf;
import static hudson.views.test.JobType.*;
import static hudson.views.test.ViewJobFilters.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class ScmTypeFilterTest extends AbstractHudsonTest {

	@Test
	@Issue({"JENKINS-29991", "JENKINS-31710"})
	public void testMatch() {
		SCMDescriptor cvs = new CVSSCM.DescriptorImpl();
		SCMDescriptor svn = new SubversionSCM.DescriptorImpl();
		SCMDescriptor git = new GitSCM.DescriptorImpl();

		for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET, SCMED_ITEM, SCM_TRIGGER_ITEM)) {
			TopLevelItem cvsJob = jobOf(type).withCVS("root", "modules", "branch").asItem();
			assertTrue(scmType(cvs).matches(cvsJob));
			assertFalse(scmType(svn).matches(cvsJob));
			assertFalse(scmType(git).matches(cvsJob));
			assertFalse(scmType("CVSSCM").matches(cvsJob)); // need package
			assertTrue(scmType("CVS").matches(cvsJob)); // getDisplayName() returns "CVS"

			TopLevelItem svnJob = jobOf(type).withSVN("svn://").asItem();
			assertTrue(scmType(svn).matches(svnJob));
			assertFalse(scmType(cvs).matches(svnJob));
			assertFalse(scmType(git).matches(svnJob));
			assertFalse(scmType("SubversionSCM").matches(svnJob)); // need package
			assertTrue(scmType("Subversion").matches(svnJob)); // getDisplayName() returns "Subversion"

			TopLevelItem gitJob = jobOf(type).withGitRepos("git://").asItem();
			assertTrue(scmType(git).matches(gitJob));
			assertFalse(scmType(cvs).matches(gitJob));
			assertFalse(scmType(svn).matches(gitJob));
			assertFalse(scmType("GitSCM").matches(gitJob)); // need package
			assertTrue(scmType("Git").matches(gitJob)); // getDisplayName() returns "Git"
		}
	}

	@Test
	public void testGetScmTypes() {
		assertThat(scmType("hudson.scm.CVSSCM").getScmType(), instanceOf(CVSSCM.DescriptorImpl.class));
		assertThat(scmType("CVS").getScmType(), instanceOf(CVSSCM.DescriptorImpl.class));

		assertThat(scmType("hudson.scm.SubversionSCM").getScmType(), instanceOf(SubversionSCM.DescriptorImpl.class));
		assertThat(scmType("Subversion").getScmType(), instanceOf(SubversionSCM.DescriptorImpl.class));

		assertThat(scmType("hudson.plugins.git.GitSCM").getScmType(), instanceOf(GitSCM.DescriptorImpl.class));
		assertThat(scmType("Git").getScmType(), instanceOf(GitSCM.DescriptorImpl.class));

		assertThat(scmType("CVSSCM").getScmType(), is(nullValue()));
		assertThat(scmType("SubversionSCM").getScmType(), is(nullValue()));
		assertThat(scmType("GitSCM").getScmType(), is(nullValue()));
	}

	@Test
	public void testConfigRoundtrip() throws Exception {
		testConfigRoundtrip(
			"scm-type-view-1",
			new ScmTypeFilter("hudson.scm.CVSSCM", excludeMatched.name())
		);

		testConfigRoundtrip(
			"scm-type-view-2",
			new ScmTypeFilter("hudson.scm.SubversionSCM", excludeMatched.name()),
			new ScmTypeFilter("hudson.plugins.git.GitSCM", includeUnmatched.name())
		);
	}

	private void testConfigRoundtrip(String viewName, ScmTypeFilter... filters) throws Exception {
		List<ScmTypeFilter> expectedFilters = new ArrayList<ScmTypeFilter>();
		for (ScmTypeFilter filter: filters) {
			expectedFilters.add(new ScmTypeFilter(filter.getScmType().clazz.getName(), filter.getIncludeExcludeTypeString()));
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

	private void assertFilterEquals(List<ScmTypeFilter> expectedFilters, List<ViewJobFilter> actualFilters) {
		assertThat(actualFilters.size(), is(expectedFilters.size()));
		for (int i = 0; i < actualFilters.size(); i++) {
			ViewJobFilter actualFilter = actualFilters.get(i);
			ScmTypeFilter expectedFilter = expectedFilters.get(i);
			assertThat(actualFilter, instanceOf(ScmTypeFilter.class));
			assertThat(((ScmTypeFilter)actualFilter).getScmType(), is(expectedFilter.getScmType()));
			assertThat(((ScmTypeFilter)actualFilter).getIncludeExcludeTypeString(), is(expectedFilter.getIncludeExcludeTypeString()));
		}
	}

}
