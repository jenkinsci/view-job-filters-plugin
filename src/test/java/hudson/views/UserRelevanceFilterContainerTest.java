package hudson.views;

import java.io.IOException;

import hudson.model.TopLevelItem;
import hudson.views.AbstractBuildTrendFilter.AmountType;
import hudson.views.AbstractBuildTrendFilter.BuildCountType;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import static org.junit.jupiter.api.Assertions.assertTrue;

@WithJenkins
class UserRelevanceFilterContainerTest extends AbstractJenkinsTest {

	@Test
	void testEmailWithNoUser() throws IOException {
		UserRelevanceFilter filter = new UserRelevanceFilter(
				true, true, true, true, true,
				true, true, true,
				BuildCountType.AtLeastOne.toString(), 2, AmountType.Builds.toString(),
				AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched.toString()
				);
		TopLevelItem item = j.createFreeStyleProject();
		filter.matchesEmail(item);
	}
}
