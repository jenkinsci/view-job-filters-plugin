package hudson.views;

import java.io.IOException;

import hudson.model.TopLevelItem;
import hudson.views.AbstractBuildTrendFilter.AmountType;
import hudson.views.AbstractBuildTrendFilter.BuildCountType;

import org.jvnet.hudson.test.HudsonTestCase;

public class UserRelevanceFilterContainerTest extends HudsonTestCase {
	public void testEmailWithNoUser() throws IOException {
		UserRelevanceFilter filter = new UserRelevanceFilter(
				true, true, true, true, true,
				true, true, true,
				BuildCountType.AtLeastOne.toString(), 2, AmountType.Builds.toString(),
				AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched.toString()
				);
		TopLevelItem item = createFreeStyleProject();
		filter.matchesEmail(item);
	}
}
