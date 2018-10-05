package hudson.views;

import hudson.model.Cause;
import hudson.model.User;
import hudson.views.AbstractBuildTrendFilter.AmountType;
import hudson.views.AbstractBuildTrendFilter.BuildCountType;

import java.util.ArrayList;
import java.util.List;

import hudson.views.test.JobMocker;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.junit.Before;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;

import static hudson.views.test.BuildMocker.build;
import static hudson.views.test.CauseMocker.cliCause;
import static hudson.views.test.CauseMocker.userCause;
import static hudson.views.test.CauseMocker.userIdCause;
import static hudson.views.test.JobMocker.freeStyleProject;
import static hudson.views.test.ViewJobFilters.UserRelevanceOption.*;
import static hudson.views.test.ViewJobFilters.userRelevance;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class UserRelevanceFilterTest extends AbstractHudsonTest {

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
	
}
