package hudson.views;

import hudson.model.Cause;
import hudson.model.ListView;
import hudson.model.Run;
import hudson.model.User;

import java.util.ArrayList;
import java.util.List;

import hudson.views.test.JobMocker;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.junit.Before;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;

import static hudson.views.AbstractBuildTrendFilter.AmountType.Builds;
import static hudson.views.AbstractBuildTrendFilter.AmountType.Days;
import static hudson.views.AbstractBuildTrendFilter.AmountType.Hours;
import static hudson.views.AbstractBuildTrendFilter.BuildCountType.All;
import static hudson.views.AbstractBuildTrendFilter.BuildCountType.AtLeastOne;
import static hudson.views.AbstractBuildTrendFilter.BuildCountType.Latest;
import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.*;
import static hudson.views.test.BuildMocker.build;
import static hudson.views.test.CauseMocker.cliCause;
import static hudson.views.test.CauseMocker.userCause;
import static hudson.views.test.CauseMocker.userIdCause;
import static hudson.views.test.ChangeLogEntryMocker.entry;
import static hudson.views.test.JobMocker.freeStyleProject;
import static hudson.views.test.UserMocker.user;
import static hudson.views.test.ViewJobFilters.UserRelevanceOption.*;
import static hudson.views.test.ViewJobFilters.userRelevance;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class UserRelevanceFilterTest extends AbstractJenkinsTest {

	@Before
	public void before() {
		j.getInstance().setSecurityRealm(j.createDummySecurityRealm());
	}

	private void setCurrentUser(String id, String fullName) {
		User user = j.getInstance().getUser(id);
		user.setFullName(fullName);
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(id, ""));
	}

	@Test
	public void testUserIdMatchesEmail() throws Exception {
		for (JobMocker.EmailType emailType: JobMocker.EmailType.values()) {
			assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL).matches(freeStyleProject().email("fred.foobar@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL).matches(freeStyleProject().email("fredfoobar@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL).matches(freeStyleProject().email("FRED.FOOBAR@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL).matches(freeStyleProject().email("FREDFOOBAR@acme.inc", emailType).asItem()));
		}

		setCurrentUser("fred.foobar", "fred foobar");

		for (JobMocker.EmailType emailType: JobMocker.EmailType.values()) {
			assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL).matches(freeStyleProject().asItem()));

			assertTrue(userRelevance(MATCH_USER_ID, MATCH_EMAIL).matches(freeStyleProject().email("fred.foobar@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL).matches(freeStyleProject().email("fredfoobar@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL).matches(freeStyleProject().email("FRED.FOOBAR@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL).matches(freeStyleProject().email("FREDFOOBAR@acme.inc", emailType).asItem()));

			assertTrue(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_CASE).matches(freeStyleProject().email("fred.foobar@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_CASE).matches(freeStyleProject().email("fredfoobar@acme.inc", emailType).asItem()));
			assertTrue(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_CASE).matches(freeStyleProject().email("FRED.FOOBAR@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_CASE).matches(freeStyleProject().email("FREDFOOBAR@acme.inc", emailType).asItem()));

			assertTrue(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_NON_ALPHA_NUM).matches(freeStyleProject().email("fred.foobar@acme.inc", emailType).asItem()));
			assertTrue(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_NON_ALPHA_NUM).matches(freeStyleProject().email("fredfoobar@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_NON_ALPHA_NUM).matches(freeStyleProject().email("FRED.FOOBAR@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_NON_ALPHA_NUM).matches(freeStyleProject().email("FREDFOOBAR@acme.inc", emailType).asItem()));

			assertTrue(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_WHITESPACE).matches(freeStyleProject().email("fred.foobar@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_WHITESPACE).matches(freeStyleProject().email("fredfoobar@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_WHITESPACE).matches(freeStyleProject().email("FRED.FOOBAR@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_WHITESPACE).matches(freeStyleProject().email("FREDFOOBAR@acme.inc", emailType).asItem()));

			assertTrue(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_CASE, IGNORE_NON_ALPHA_NUM).matches(freeStyleProject().email("fred.foobar@acme.inc", emailType).asItem()));
			assertTrue(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_CASE, IGNORE_NON_ALPHA_NUM).matches(freeStyleProject().email("fredfoobar@acme.inc", emailType).asItem()));
			assertTrue(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_CASE, IGNORE_NON_ALPHA_NUM).matches(freeStyleProject().email("FRED.FOOBAR@acme.inc", emailType).asItem()));
			assertTrue(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_CASE, IGNORE_NON_ALPHA_NUM).matches(freeStyleProject().email("FREDFOOBAR@acme.inc", emailType).asItem()));
		}
	}

	@Test
	public void testUserFullNameMatchesEmail() {

		for (JobMocker.EmailType emailType: JobMocker.EmailType.values()) {
			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL).matches(freeStyleProject().email("fred.foobar@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL).matches(freeStyleProject().email("fredfoobar@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL).matches(freeStyleProject().email("FRED.FOOBAR@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL).matches(freeStyleProject().email("FREDFOOBAR@acme.inc", emailType).asItem()));
		}

 		setCurrentUser("fred.foobar", "fred foobar");

		for (JobMocker.EmailType emailType: JobMocker.EmailType.values()) {
			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL).matches(freeStyleProject().asItem()));

			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL).matches(freeStyleProject().email("fred.foobar@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL).matches(freeStyleProject().email("fredfoobar@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL).matches(freeStyleProject().email("FRED.FOOBAR@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL).matches(freeStyleProject().email("FREDFOOBAR@acme.inc", emailType).asItem()));

			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_CASE).matches(freeStyleProject().email("fred.foobar@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_CASE).matches(freeStyleProject().email("fredfoobar@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_CASE).matches(freeStyleProject().email("FRED.FOOBAR@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_CASE).matches(freeStyleProject().email("FREDFOOBAR@acme.inc", emailType).asItem()));

			assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_NON_ALPHA_NUM).matches(freeStyleProject().email("fred.foobar@acme.inc", emailType).asItem()));
			assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_NON_ALPHA_NUM).matches(freeStyleProject().email("fredfoobar@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_NON_ALPHA_NUM).matches(freeStyleProject().email("FRED.FOOBAR@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_NON_ALPHA_NUM).matches(freeStyleProject().email("FREDFOOBAR@acme.inc", emailType).asItem()));

			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE).matches(freeStyleProject().email("fred.foobar@acme.inc", emailType).asItem()));
			assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE).matches(freeStyleProject().email("fredfoobar@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE).matches(freeStyleProject().email("FRED.FOOBAR@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE).matches(freeStyleProject().email("FREDFOOBAR@acme.inc", emailType).asItem()));

			assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE, IGNORE_NON_ALPHA_NUM).matches(freeStyleProject().email("fred.foobar@acme.inc", emailType).asItem()));
			assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE, IGNORE_NON_ALPHA_NUM).matches(freeStyleProject().email("fredfoobar@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE, IGNORE_NON_ALPHA_NUM).matches(freeStyleProject().email("FRED.FOOBAR@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE, IGNORE_NON_ALPHA_NUM).matches(freeStyleProject().email("FREDFOOBAR@acme.inc", emailType).asItem()));

			assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE, IGNORE_NON_ALPHA_NUM, IGNORE_CASE).matches(freeStyleProject().email("fred.foobar@acme.inc", emailType).asItem()));
			assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE, IGNORE_NON_ALPHA_NUM, IGNORE_CASE).matches(freeStyleProject().email("fredfoobar@acme.inc", emailType).asItem()));
			assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE, IGNORE_NON_ALPHA_NUM, IGNORE_CASE).matches(freeStyleProject().email("FRED.FOOBAR@acme.inc", emailType).asItem()));
			assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE, IGNORE_NON_ALPHA_NUM, IGNORE_CASE).matches(freeStyleProject().email("FREDFOOBAR@acme.inc", emailType).asItem()));
		}
	}

	@Test
	public void testMatchRunWhenNotMatchingEmail() {
		setCurrentUser("fred.foobar", "fred foobar");

		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER).matches(freeStyleProject().lastBuilds(build().causes(userCause("fred foobar")).create()).asItem()));
		assertTrue(userRelevance(MATCH_USER_ID, MATCH_SCM_LOG).matches(freeStyleProject().lastBuilds(build().changes(entry(user("fred.foobar", "fred foobar"))).create()).asItem()));
	}


	@Test
	public void testUserIdMatchesBuilder() throws Exception {
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER).matchesRun(build().causes(userCause("fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER).matchesRun(build().causes(userCause("FRED FOOBAR")).create()));

		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER).matchesRun(build().causes(userIdCause("fred.foobar", "fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER).matchesRun(build().causes(userIdCause("fredfoobar", "fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER).matchesRun(build().causes(userIdCause("FRED.FOOBAR", "FRED FOOBAR")).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER).matchesRun(build().causes(userIdCause("FREDFOOBAR", "FRED FOOBAR")).create()));

		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER).matchesRun(build().causes(cliCause("fred.foobar", "fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER).matchesRun(build().causes(cliCause("fredfoobar", "fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER).matchesRun(build().causes(cliCause("FRED.FOOBAR", "FRED FOOBAR")).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER).matchesRun(build().causes(cliCause("FREDFOOBAR", "FRED FOOBAR")).create()));
		
		setCurrentUser("fred.foobar", "fred foobar");

		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER).matchesRun(build().create()));

        assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER).matchesRun(build().causes(userCause("fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER).matchesRun(build().causes(userCause("FRED FOOBAR")).create()));

		assertTrue(userRelevance(MATCH_USER_ID, MATCH_BUILDER).matchesRun(build().causes(userIdCause("fred.foobar", "fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER).matchesRun(build().causes(userIdCause("fredfoobar", "fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER).matchesRun(build().causes(userIdCause("FRED.FOOBAR", "FRED FOOBAR")).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER).matchesRun(build().causes(userIdCause("FREDFOOBAR", "FRED FOOBAR")).create()));

		assertTrue(userRelevance(MATCH_USER_ID, MATCH_BUILDER).matchesRun(build().causes(cliCause("fred.foobar", "fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER).matchesRun(build().causes(cliCause("fredfoobar", "fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER).matchesRun(build().causes(cliCause("FRED.FOOBAR", "FRED FOOBAR")).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER).matchesRun(build().causes(cliCause("FREDFOOBAR", "FRED FOOBAR")).create()));


		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_CASE).matchesRun(build().causes(userCause("fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_CASE).matchesRun(build().causes(userCause("FRED FOOBAR")).create()));

		assertTrue(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_CASE).matchesRun(build().causes(userIdCause("fred.foobar", "fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_CASE).matchesRun(build().causes(userIdCause("fredfoobar", "fred foobar")).create()));
		assertTrue(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_CASE).matchesRun(build().causes(userIdCause("FRED.FOOBAR", "FRED FOOBAR")).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_CASE).matchesRun(build().causes(userIdCause("FREDFOOBAR", "FRED FOOBAR")).create()));

		assertTrue(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_CASE).matchesRun(build().causes(cliCause("fred.foobar", "fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_CASE).matchesRun(build().causes(cliCause("fredfoobar", "fred foobar")).create()));
		assertTrue(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_CASE).matchesRun(build().causes(cliCause("FRED.FOOBAR", "FRED FOOBAR")).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_CASE).matchesRun(build().causes(cliCause("FREDFOOBAR", "FRED FOOBAR")).create()));


		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_NON_ALPHA_NUM).matchesRun(build().causes(userCause("fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_NON_ALPHA_NUM).matchesRun(build().causes(userCause("FRED FOOBAR")).create()));

		assertTrue(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_NON_ALPHA_NUM).matchesRun(build().causes(userIdCause("fred.foobar", "fred foobar")).create()));
		assertTrue(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_NON_ALPHA_NUM).matchesRun(build().causes(userIdCause("fredfoobar", "fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_NON_ALPHA_NUM).matchesRun(build().causes(userIdCause("FRED.FOOBAR", "FRED FOOBAR")).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_NON_ALPHA_NUM).matchesRun(build().causes(userIdCause("FREDFOOBAR", "FRED FOOBAR")).create()));

		assertTrue(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_NON_ALPHA_NUM).matchesRun(build().causes(cliCause("fred.foobar", "fred foobar")).create()));
		assertTrue(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_NON_ALPHA_NUM).matchesRun(build().causes(cliCause("fredfoobar", "fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_NON_ALPHA_NUM).matchesRun(build().causes(cliCause("FRED.FOOBAR", "FRED FOOBAR")).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_NON_ALPHA_NUM).matchesRun(build().causes(cliCause("FREDFOOBAR", "FRED FOOBAR")).create()));


		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_WHITESPACE).matchesRun(build().causes(userCause("fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_WHITESPACE).matchesRun(build().causes(userCause("FRED FOOBAR")).create()));

		assertTrue(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_WHITESPACE).matchesRun(build().causes(userIdCause("fred.foobar", "fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_WHITESPACE).matchesRun(build().causes(userIdCause("fredfoobar", "fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_WHITESPACE).matchesRun(build().causes(userIdCause("FRED.FOOBAR", "FRED FOOBAR")).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_WHITESPACE).matchesRun(build().causes(userIdCause("FREDFOOBAR", "FRED FOOBAR")).create()));

		assertTrue(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_WHITESPACE).matchesRun(build().causes(cliCause("fred.foobar", "fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_WHITESPACE).matchesRun(build().causes(cliCause("fredfoobar", "fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_WHITESPACE).matchesRun(build().causes(cliCause("FRED.FOOBAR", "FRED FOOBAR")).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_WHITESPACE).matchesRun(build().causes(cliCause("FREDFOOBAR", "FRED FOOBAR")).create()));


		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_CASE, IGNORE_NON_ALPHA_NUM).matchesRun(build().causes(userCause("fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_CASE, IGNORE_NON_ALPHA_NUM).matchesRun(build().causes(userCause("FRED FOOBAR")).create()));

		assertTrue(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_CASE, IGNORE_NON_ALPHA_NUM).matchesRun(build().causes(userIdCause("fred.foobar", "fred foobar")).create()));
		assertTrue(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_CASE, IGNORE_NON_ALPHA_NUM).matchesRun(build().causes(userIdCause("fredfoobar", "fred foobar")).create()));
		assertTrue(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_CASE, IGNORE_NON_ALPHA_NUM).matchesRun(build().causes(userIdCause("FRED.FOOBAR", "FRED FOOBAR")).create()));
		assertTrue(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_CASE, IGNORE_NON_ALPHA_NUM).matchesRun(build().causes(userIdCause("FREDFOOBAR", "FRED FOOBAR")).create()));

		assertTrue(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_CASE, IGNORE_NON_ALPHA_NUM).matchesRun(build().causes(cliCause("fred.foobar", "fred foobar")).create()));
		assertTrue(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_CASE, IGNORE_NON_ALPHA_NUM).matchesRun(build().causes(cliCause("fredfoobar", "fred foobar")).create()));
		assertTrue(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_CASE, IGNORE_NON_ALPHA_NUM).matchesRun(build().causes(cliCause("FRED.FOOBAR", "FRED FOOBAR")).create()));
		assertTrue(userRelevance(MATCH_USER_ID, MATCH_BUILDER, IGNORE_CASE, IGNORE_NON_ALPHA_NUM).matchesRun(build().causes(cliCause("FREDFOOBAR", "FRED FOOBAR")).create()));
	}


	@Test
	public void testUserFullNameMatchesBuilder() throws Exception {
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER).matchesRun(build().causes(userCause("fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER).matchesRun(build().causes(userCause("FRED FOOBAR")).create()));

		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER).matchesRun(build().causes(userIdCause("fred.foobar", "fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER).matchesRun(build().causes(userIdCause("fredfoobar", "fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER).matchesRun(build().causes(userIdCause("FRED.FOOBAR", "FRED FOOBAR")).create()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER).matchesRun(build().causes(userIdCause("FREDFOOBAR", "FRED FOOBAR")).create()));

		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER).matchesRun(build().causes(cliCause("fred.foobar", "fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER).matchesRun(build().causes(cliCause("fredfoobar", "fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER).matchesRun(build().causes(cliCause("FRED.FOOBAR", "FRED FOOBAR")).create()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER).matchesRun(build().causes(cliCause("FREDFOOBAR", "FRED FOOBAR")).create()));

		setCurrentUser("fred.foobar", "fred foobar");

		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER).matchesRun(build().create()));

		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER).matchesRun(build().causes(userCause("fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER).matchesRun(build().causes(userCause("FRED FOOBAR")).create()));

		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER).matchesRun(build().causes(userIdCause("fred.foobar", "fred foobar")).create()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER).matchesRun(build().causes(userIdCause("fredfoobar", "fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER).matchesRun(build().causes(userIdCause("FRED.FOOBAR", "FRED FOOBAR")).create()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER).matchesRun(build().causes(userIdCause("FREDFOOBAR", "FRED FOOBAR")).create()));

		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER).matchesRun(build().causes(cliCause("fred.foobar", "fred foobar")).create()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER).matchesRun(build().causes(cliCause("fredfoobar", "fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER).matchesRun(build().causes(cliCause("FRED.FOOBAR", "FRED FOOBAR")).create()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER).matchesRun(build().causes(cliCause("FREDFOOBAR", "FRED FOOBAR")).create()));


		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_CASE).matchesRun(build().causes(userCause("fred foobar")).create()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_CASE).matchesRun(build().causes(userCause("FRED FOOBAR")).create()));

		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_CASE).matchesRun(build().causes(userIdCause("fred.foobar", "fred foobar")).create()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_CASE).matchesRun(build().causes(userIdCause("fredfoobar", "fred foobar")).create()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_CASE).matchesRun(build().causes(userIdCause("FRED.FOOBAR", "FRED FOOBAR")).create()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_CASE).matchesRun(build().causes(userIdCause("FREDFOOBAR", "FRED FOOBAR")).create()));

		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_CASE).matchesRun(build().causes(cliCause("fred.foobar", "fred foobar")).create()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_CASE).matchesRun(build().causes(cliCause("fredfoobar", "fred foobar")).create()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_CASE).matchesRun(build().causes(cliCause("FRED.FOOBAR", "FRED FOOBAR")).create()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_CASE).matchesRun(build().causes(cliCause("FREDFOOBAR", "FRED FOOBAR")).create()));


		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_NON_ALPHA_NUM).matchesRun(build().causes(userCause("fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_NON_ALPHA_NUM).matchesRun(build().causes(userCause("FRED FOOBAR")).create()));

		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_NON_ALPHA_NUM).matchesRun(build().causes(userIdCause("fred.foobar", "fred foobar")).create()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_NON_ALPHA_NUM).matchesRun(build().causes(userIdCause("fredfoobar", "fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_NON_ALPHA_NUM).matchesRun(build().causes(userIdCause("FRED.FOOBAR", "FRED FOOBAR")).create()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_NON_ALPHA_NUM).matchesRun(build().causes(userIdCause("FREDFOOBAR", "FRED FOOBAR")).create()));

		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_NON_ALPHA_NUM).matchesRun(build().causes(cliCause("fred.foobar", "fred foobar")).create()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_NON_ALPHA_NUM).matchesRun(build().causes(cliCause("fredfoobar", "fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_NON_ALPHA_NUM).matchesRun(build().causes(cliCause("FRED.FOOBAR", "FRED FOOBAR")).create()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_NON_ALPHA_NUM).matchesRun(build().causes(cliCause("FREDFOOBAR", "FRED FOOBAR")).create()));


		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_WHITESPACE).matchesRun(build().causes(userCause("fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_WHITESPACE).matchesRun(build().causes(userCause("FRED FOOBAR")).create()));

		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_WHITESPACE).matchesRun(build().causes(userIdCause("fred.foobar", "fred foobar")).create()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_WHITESPACE).matchesRun(build().causes(userIdCause("fredfoobar", "fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_WHITESPACE).matchesRun(build().causes(userIdCause("FRED.FOOBAR", "FRED FOOBAR")).create()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_WHITESPACE).matchesRun(build().causes(userIdCause("FREDFOOBAR", "FRED FOOBAR")).create()));

		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_WHITESPACE).matchesRun(build().causes(cliCause("fred.foobar", "fred foobar")).create()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_WHITESPACE).matchesRun(build().causes(cliCause("fredfoobar", "fred foobar")).create()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_WHITESPACE).matchesRun(build().causes(cliCause("FRED.FOOBAR", "FRED FOOBAR")).create()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_WHITESPACE).matchesRun(build().causes(cliCause("FREDFOOBAR", "FRED FOOBAR")).create()));


		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_CASE, IGNORE_WHITESPACE).matchesRun(build().causes(userCause("fred foobar")).create()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_CASE, IGNORE_WHITESPACE).matchesRun(build().causes(userCause("FRED FOOBAR")).create()));

		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_CASE, IGNORE_WHITESPACE).matchesRun(build().causes(userIdCause("fred.foobar", "fred foobar")).create()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_CASE, IGNORE_WHITESPACE).matchesRun(build().causes(userIdCause("fredfoobar", "fred foobar")).create()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_CASE, IGNORE_WHITESPACE).matchesRun(build().causes(userIdCause("FRED.FOOBAR", "FRED FOOBAR")).create()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_CASE, IGNORE_WHITESPACE).matchesRun(build().causes(userIdCause("FREDFOOBAR", "FRED FOOBAR")).create()));

		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_CASE, IGNORE_WHITESPACE).matchesRun(build().causes(cliCause("fred.foobar", "fred foobar")).create()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_CASE, IGNORE_WHITESPACE).matchesRun(build().causes(cliCause("fredfoobar", "fred foobar")).create()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_CASE, IGNORE_WHITESPACE).matchesRun(build().causes(cliCause("FRED.FOOBAR", "FRED FOOBAR")).create()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_BUILDER, IGNORE_CASE, IGNORE_WHITESPACE).matchesRun(build().causes(cliCause("FREDFOOBAR", "FRED FOOBAR")).create()));
	}

	@Test
	public void testUserIdMatchesScmChanges() {
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_SCM_LOG).matchesRun(build().changes(entry(user("fred.foobar", "Fred Foobar"))).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_SCM_LOG).matchesRun(build().changes(entry(user("fredfoobar", "Fred Foobar"))).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_SCM_LOG).matchesRun(build().changes(entry(user("FRED.FOOBAR", "FRED FOOBAR"))).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_SCM_LOG).matchesRun(build().changes(entry(user("FREDFOOBAR", "FRED FOOBAR"))).create()));

		setCurrentUser("fred.foobar", "fred foobar");

		assertFalse(userRelevance(MATCH_USER_ID, MATCH_SCM_LOG).matchesRun(mock(Run.class)));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_SCM_LOG).matchesRun(build().changes().create()));

		assertTrue(userRelevance(MATCH_USER_ID, MATCH_SCM_LOG).matchesRun(build().changes(entry(user("fred.foobar", "fred foobar"))).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_SCM_LOG).matchesRun(build().changes(entry(user("fredfoobar", "fred foobar"))).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_SCM_LOG).matchesRun(build().changes(entry(user("FRED.FOOBAR", "FRED FOOBAR"))).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_SCM_LOG).matchesRun(build().changes(entry(user("FREDFOOBAR", "FRED FOOBAR"))).create()));

		assertTrue(userRelevance(MATCH_USER_ID, MATCH_SCM_LOG, IGNORE_CASE).matchesRun(build().changes(entry(user("fred.foobar", "fred foobar"))).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_SCM_LOG, IGNORE_CASE).matchesRun(build().changes(entry(user("fredfoobar", "fred foobar"))).create()));
		assertTrue(userRelevance(MATCH_USER_ID, MATCH_SCM_LOG, IGNORE_CASE).matchesRun(build().changes(entry(user("FRED.FOOBAR", "FRED FOOBAR"))).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_SCM_LOG, IGNORE_CASE).matchesRun(build().changes(entry(user("FREDFOOBAR", "FRED FOOBAR"))).create()));

		assertTrue(userRelevance(MATCH_USER_ID, MATCH_SCM_LOG, IGNORE_NON_ALPHA_NUM).matchesRun(build().changes(entry(user("fred.foobar", "fred foobar"))).create()));
		assertTrue(userRelevance(MATCH_USER_ID, MATCH_SCM_LOG, IGNORE_NON_ALPHA_NUM).matchesRun(build().changes(entry(user("fredfoobar", "fred foobar"))).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_SCM_LOG, IGNORE_NON_ALPHA_NUM).matchesRun(build().changes(entry(user("FRED.FOOBAR", "FRED FOOBAR"))).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_SCM_LOG, IGNORE_NON_ALPHA_NUM).matchesRun(build().changes(entry(user("FREDFOOBAR", "FRED FOOBAR"))).create()));

		assertTrue(userRelevance(MATCH_USER_ID, MATCH_SCM_LOG, IGNORE_WHITESPACE).matchesRun(build().changes(entry(user("fred.foobar", "fred foobar"))).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_SCM_LOG, IGNORE_WHITESPACE).matchesRun(build().changes(entry(user("fredfoobar", "fred foobar"))).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_SCM_LOG, IGNORE_WHITESPACE).matchesRun(build().changes(entry(user("FRED.FOOBAR", "FRED FOOBAR"))).create()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_SCM_LOG, IGNORE_WHITESPACE).matchesRun(build().changes(entry(user("FREDFOOBAR", "FRED FOOBAR"))).create()));

		assertTrue(userRelevance(MATCH_USER_ID, MATCH_SCM_LOG, IGNORE_CASE, IGNORE_NON_ALPHA_NUM).matchesRun(build().changes(entry(user("fred.foobar", "fred foobar"))).create()));
		assertTrue(userRelevance(MATCH_USER_ID, MATCH_SCM_LOG, IGNORE_CASE, IGNORE_NON_ALPHA_NUM).matchesRun(build().changes(entry(user("fredfoobar", "fred foobar"))).create()));
		assertTrue(userRelevance(MATCH_USER_ID, MATCH_SCM_LOG, IGNORE_CASE, IGNORE_NON_ALPHA_NUM).matchesRun(build().changes(entry(user("FRED.FOOBAR", "FRED FOOBAR"))).create()));
		assertTrue(userRelevance(MATCH_USER_ID, MATCH_SCM_LOG, IGNORE_CASE, IGNORE_NON_ALPHA_NUM).matchesRun(build().changes(entry(user("FREDFOOBAR", "FRED FOOBAR"))).create()));
	}

	@Test
	public void testUserFullNameMatchesScmChanges() {
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_SCM_LOG).matchesRun(build().changes(entry(user("fred.foobar", "fred foobar"))).create()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_SCM_LOG).matchesRun(build().changes(entry(user("fredfoobar", "fred foobar"))).create()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_SCM_LOG).matchesRun(build().changes(entry(user("FRED.FOOBAR", "FRED FOOBAR"))).create()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_SCM_LOG).matchesRun(build().changes(entry(user("FREDFOOBAR", "FRED FOOBAR"))).create()));

		setCurrentUser("fred.foobar", "fred foobar");

		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_SCM_LOG).matchesRun(mock(Run.class)));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_SCM_LOG).matchesRun(build().changes().create()));

		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_SCM_LOG).matchesRun(build().changes(entry(user("fred.foobar", "fred foobar"))).create()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_SCM_LOG).matchesRun(build().changes(entry(user("fredfoobar", "fred foobar"))).create()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_SCM_LOG).matchesRun(build().changes(entry(user("FRED.FOOBAR", "FRED FOOBAR"))).create()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_SCM_LOG).matchesRun(build().changes(entry(user("FREDFOOBAR", "FRED FOOBAR"))).create()));

		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_SCM_LOG, IGNORE_CASE).matchesRun(build().changes(entry(user("fred.foobar", "fred foobar"))).create()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_SCM_LOG, IGNORE_CASE).matchesRun(build().changes(entry(user("fredfoobar", "fred foobar"))).create()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_SCM_LOG, IGNORE_CASE).matchesRun(build().changes(entry(user("FRED.FOOBAR", "FRED FOOBAR"))).create()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_SCM_LOG, IGNORE_CASE).matchesRun(build().changes(entry(user("FREDFOOBAR", "FRED FOOBAR"))).create()));

		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_SCM_LOG, IGNORE_NON_ALPHA_NUM).matchesRun(build().changes(entry(user("fred.foobar", "fred foobar"))).create()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_SCM_LOG, IGNORE_NON_ALPHA_NUM).matchesRun(build().changes(entry(user("fredfoobar", "fred foobar"))).create()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_SCM_LOG, IGNORE_NON_ALPHA_NUM).matchesRun(build().changes(entry(user("FRED.FOOBAR", "FRED FOOBAR"))).create()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_SCM_LOG, IGNORE_NON_ALPHA_NUM).matchesRun(build().changes(entry(user("FREDFOOBAR", "FRED FOOBAR"))).create()));

		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_SCM_LOG, IGNORE_WHITESPACE).matchesRun(build().changes(entry(user("fred.foobar", "fred foobar"))).create()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_SCM_LOG, IGNORE_WHITESPACE).matchesRun(build().changes(entry(user("fredfoobar", "fred foobar"))).create()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_SCM_LOG, IGNORE_WHITESPACE).matchesRun(build().changes(entry(user("FRED.FOOBAR", "FRED FOOBAR"))).create()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_SCM_LOG, IGNORE_WHITESPACE).matchesRun(build().changes(entry(user("FREDFOOBAR", "FRED FOOBAR"))).create()));

		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_SCM_LOG, IGNORE_CASE, IGNORE_WHITESPACE).matchesRun(build().changes(entry(user("fred.foobar", "fred foobar"))).create()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_SCM_LOG, IGNORE_CASE, IGNORE_WHITESPACE).matchesRun(build().changes(entry(user("fredfoobar", "fred foobar"))).create()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_SCM_LOG, IGNORE_CASE, IGNORE_WHITESPACE).matchesRun(build().changes(entry(user("FRED.FOOBAR", "FRED FOOBAR"))).create()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_SCM_LOG, IGNORE_CASE, IGNORE_WHITESPACE).matchesRun(build().changes(entry(user("FREDFOOBAR", "FRED FOOBAR"))).create()));
	}

	@Test
	public void testGetUserValue() {
		UserRelevanceFilter filter = userRelevance(
			IGNORE_WHITESPACE, IGNORE_CASE, IGNORE_NON_ALPHA_NUM
		);

		assertThat(filter.getUserValue(mock(Cause.class), false), is("ANONYMOUS"));
		assertThat(filter.getUserValue(mock(Cause.class), true), is("ANONYMOUS"));

		assertThat(filter.getUserValue(userCause("User Name"), false), is("ANONYMOUS"));
		assertThat(filter.getUserValue(userCause("User Name"), true), is("USERNAME"));

		assertThat(filter.getUserValue(userIdCause("user.id", "User Name"), false), is("USERID"));
		assertThat(filter.getUserValue(userIdCause("user.id", "User Name"), true), is("USERNAME"));

		assertThat(filter.getUserValue(cliCause("user.id", "User Name"), false), is("USERID"));
		assertThat(filter.getUserValue(cliCause("user.id", "User Name"), true), is("USERNAME"));
	}

	@Test
	public void testMatchesEmail() {
		List<String> emails = asList("user1@gmail.com, user.2@gmail.com  @us.er3", "_user_4@gmail.gov;user5a@gmail.com ; us-er6");
	    UserRelevanceFilter filter = userRelevance(
			MATCH_USER_ID, MATCH_USER_FULL_NAME,
			MATCH_EMAIL, MATCH_BUILDER, MATCH_SCM_LOG,
			IGNORE_WHITESPACE, IGNORE_CASE, IGNORE_NON_ALPHA_NUM
		);

		assertTrue(filter.matchesEmail(emails, "USER1"));
		assertFalse(filter.matchesEmail(emails, "user1@gmail.com"));
		assertTrue(filter.matchesEmail(emails, "USER2"));
		assertTrue(filter.matchesEmail(emails, "USER3"));
		assertTrue(filter.matchesEmail(emails, "USER4"));
		assertFalse(filter.matchesEmail(emails, "USER5"));
		assertTrue(filter.matchesEmail(emails, "USER6"));
	}

	@Test
	public void testNormalize() {
		assertThat(userRelevance().normalize("u se-r"), is("u se-r"));

		assertThat(userRelevance(IGNORE_CASE).normalize("u se-r"), is("U SE-R"));
		assertThat(userRelevance(IGNORE_WHITESPACE).normalize("u se-r"), is("use-r"));
		assertThat(userRelevance(IGNORE_NON_ALPHA_NUM).normalize("u se-r"), is("user"));

		assertThat(userRelevance(IGNORE_CASE, IGNORE_WHITESPACE).normalize("u se-r"), is("USE-R"));
		assertThat(userRelevance(IGNORE_CASE, IGNORE_NON_ALPHA_NUM).normalize("u se-r"), is("USER"));
		assertThat(userRelevance(IGNORE_WHITESPACE, IGNORE_NON_ALPHA_NUM).normalize("u se-r"), is("user"));

	    assertThat(userRelevance(IGNORE_CASE, IGNORE_WHITESPACE, IGNORE_NON_ALPHA_NUM).normalize("u se-r"), is("USER"));
	}

	@Issue("JENKINS-13781")
	@Test
	public void testNullDoesntMatchEmail() {
		// FIXED: would throw null-pointer
	    UserRelevanceFilter filter = userRelevance(
    		MATCH_USER_ID, MATCH_USER_FULL_NAME,
			MATCH_EMAIL, MATCH_BUILDER, MATCH_SCM_LOG,
			IGNORE_WHITESPACE, IGNORE_CASE, IGNORE_NON_ALPHA_NUM
		);
		assertFalse(filter.matchesEmail(null));
	}

	@Test
	public void testConfigRoundtrip() throws Exception {
		testConfigRoundtrip(
			"view-1",
			new UserRelevanceFilter(true, false,
				true, false, true,
				false, true, false,
				All.name(), 10, Builds.name(), includeUnmatched.name())
		);

		testConfigRoundtrip(
			"view-2",
			new UserRelevanceFilter(false, true,
				false, true, false,
				true, false, true,
				AtLeastOne.name(), 5, Days.name(), excludeMatched.name()),
			new UserRelevanceFilter(true, true,
				false, false, false,
				true, true, true,
				Latest.name(), 1, Hours.name(), includeMatched.name())
		);

	}

	private void testConfigRoundtrip(String viewName, UserRelevanceFilter... filters) throws Exception {
		List<UserRelevanceFilter> expectedFilters = new ArrayList<UserRelevanceFilter>();
		for (UserRelevanceFilter filter : filters) {
			expectedFilters.add(new UserRelevanceFilter(filter.isMatchUserId(), filter.isMatchUserFullName(),
					filter.isIgnoreCase(), filter.isIgnoreWhitespace(), filter.isIgnoreNonAlphaNumeric(),
					filter.isMatchBuilder(), filter.isMatchEmail(), filter.isMatchScmChanges(),
					filter.getBuildCountTypeString(), filter.getAmount(), filter.getAmountTypeString(), filter.getIncludeExcludeTypeString()));
		}

		ListView view = createFilteredView(viewName, filters);
		j.configRoundtrip(view);

		ListView viewAfterRoundtrip = (ListView) j.getInstance().getView(viewName);
		assertFilterEquals(expectedFilters, viewAfterRoundtrip.getJobFilters());

		viewAfterRoundtrip.save();
		j.getInstance().reload();

		ListView viewAfterReload = (ListView) j.getInstance().getView(viewName);
		assertFilterEquals(expectedFilters, viewAfterReload.getJobFilters());
	}

	private void assertFilterEquals(List<UserRelevanceFilter> expectedFilters, List<ViewJobFilter> actualFilters) {
		assertThat(actualFilters.size(), is(expectedFilters.size()));
		for (int i = 0; i < actualFilters.size(); i++) {
			ViewJobFilter actualFilter = actualFilters.get(i);
			UserRelevanceFilter expectedFilter = expectedFilters.get(i);
			assertThat(actualFilter, instanceOf(UserRelevanceFilter.class));
			assertThat(((UserRelevanceFilter)actualFilter).isMatchUserId(), is(expectedFilter.isMatchUserId()));
			assertThat(((UserRelevanceFilter)actualFilter).isMatchUserFullName(), is(expectedFilter.isMatchUserFullName()));
			assertThat(((UserRelevanceFilter)actualFilter).isIgnoreCase(), is(expectedFilter.isIgnoreCase()));
			assertThat(((UserRelevanceFilter)actualFilter).isIgnoreWhitespace(), is(expectedFilter.isIgnoreWhitespace()));
			assertThat(((UserRelevanceFilter)actualFilter).isIgnoreNonAlphaNumeric(), is(expectedFilter.isIgnoreNonAlphaNumeric()));
			assertThat(((UserRelevanceFilter)actualFilter).isMatchBuilder(), is(expectedFilter.isMatchBuilder()));
			assertThat(((UserRelevanceFilter)actualFilter).isMatchEmail(), is(expectedFilter.isMatchEmail()));
			assertThat(((UserRelevanceFilter)actualFilter).isMatchScmChanges(), is(expectedFilter.isMatchScmChanges()));
			assertThat(((UserRelevanceFilter)actualFilter).getBuildCountTypeString(), is(expectedFilter.getBuildCountTypeString()));
			assertThat(((UserRelevanceFilter)actualFilter).getAmount(), is(expectedFilter.getAmount()));
			assertThat(((UserRelevanceFilter)actualFilter).getAmountTypeString(), is(expectedFilter.getAmountTypeString()));
			assertThat(((UserRelevanceFilter)actualFilter).getIncludeExcludeTypeString(), is(expectedFilter.getIncludeExcludeTypeString()));
		}
	}

}
