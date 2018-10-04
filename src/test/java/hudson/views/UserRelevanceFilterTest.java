package hudson.views;

import hudson.model.Cause;
import hudson.model.Cause.UserIdCause;
import hudson.model.User;
import hudson.views.AbstractBuildTrendFilter.AmountType;
import hudson.views.AbstractBuildTrendFilter.BuildCountType;

import java.util.ArrayList;
import java.util.List;

import hudson.views.test.JobMocker;
import jenkins.model.Jenkins;
import junit.framework.TestCase;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.TestingAuthenticationToken;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.junit.Before;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;

import static hudson.views.test.BuildMocker.build;
import static hudson.views.test.CauseMocker.cliCause;
import static hudson.views.test.CauseMocker.userCause;
import static hudson.views.test.CauseMocker.userIdCause;
import static hudson.views.test.JobMocker.jobOf;
import static hudson.views.test.JobType.FREE_STYLE_PROJECT;
import static hudson.views.test.ViewJobFilters.UserRelevanceOption.*;
import static hudson.views.test.ViewJobFilters.userRelevance;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class UserRelevanceFilterTest extends AbstractHudsonTest {

	@Before
	public void before() {
		j.getInstance().setSecurityRealm(j.createDummySecurityRealm());
	}

	private void setCurrentUser(String id, String fullName) {
		User xyz = j.getInstance().getUser(id);
		xyz.setFullName(fullName);
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(id, ""));
	}

	@Test
	public void testUserIdMatchesEmail() throws Exception {
		for (JobMocker.EmailType emailType: JobMocker.EmailType.values()) {
			assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fred.foobar@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fredfoobar@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FRED.FOOBAR@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FREDFOOBAR@acme.inc", emailType).asItem()));
		}

		setCurrentUser("fred.foobar", "fred foobar");

		for (JobMocker.EmailType emailType: JobMocker.EmailType.values()) {
			assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL).matches(jobOf(FREE_STYLE_PROJECT).asItem()));

			assertTrue(userRelevance(MATCH_USER_ID, MATCH_EMAIL).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fred.foobar@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fredfoobar@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FRED.FOOBAR@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FREDFOOBAR@acme.inc", emailType).asItem()));

			assertTrue(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_CASE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fred.foobar@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_CASE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fredfoobar@acme.inc", emailType).asItem()));
			assertTrue(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_CASE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FRED.FOOBAR@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_CASE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FREDFOOBAR@acme.inc", emailType).asItem()));

			assertTrue(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_NON_ALPHA_NUM).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fred.foobar@acme.inc", emailType).asItem()));
			assertTrue(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_NON_ALPHA_NUM).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fredfoobar@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_NON_ALPHA_NUM).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FRED.FOOBAR@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_NON_ALPHA_NUM).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FREDFOOBAR@acme.inc", emailType).asItem()));

			assertTrue(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_WHITESPACE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fred.foobar@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_WHITESPACE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fredfoobar@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_WHITESPACE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FRED.FOOBAR@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_WHITESPACE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FREDFOOBAR@acme.inc", emailType).asItem()));

			assertTrue(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_CASE, IGNORE_NON_ALPHA_NUM).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fred.foobar@acme.inc", emailType).asItem()));
			assertTrue(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_CASE, IGNORE_NON_ALPHA_NUM).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fredfoobar@acme.inc", emailType).asItem()));
			assertTrue(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_CASE, IGNORE_NON_ALPHA_NUM).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FRED.FOOBAR@acme.inc", emailType).asItem()));
			assertTrue(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_CASE, IGNORE_NON_ALPHA_NUM).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FREDFOOBAR@acme.inc", emailType).asItem()));
		}
	}

	@Test
	public void testUserFullNameMatchesEmail() {

		for (JobMocker.EmailType emailType: JobMocker.EmailType.values()) {
			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fred.foobar@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fredfoobar@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FRED.FOOBAR@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FREDFOOBAR@acme.inc", emailType).asItem()));
		}

 		setCurrentUser("fred.foobar", "fred foobar");

		for (JobMocker.EmailType emailType: JobMocker.EmailType.values()) {
			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL).matches(jobOf(FREE_STYLE_PROJECT).asItem()));

			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fred.foobar@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fredfoobar@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FRED.FOOBAR@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FREDFOOBAR@acme.inc", emailType).asItem()));

			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_CASE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fred.foobar@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_CASE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fredfoobar@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_CASE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FRED.FOOBAR@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_CASE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FREDFOOBAR@acme.inc", emailType).asItem()));

			assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_NON_ALPHA_NUM).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fred.foobar@acme.inc", emailType).asItem()));
			assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_NON_ALPHA_NUM).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fredfoobar@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_NON_ALPHA_NUM).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FRED.FOOBAR@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_NON_ALPHA_NUM).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FREDFOOBAR@acme.inc", emailType).asItem()));

			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fred.foobar@acme.inc", emailType).asItem()));
			assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fredfoobar@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FRED.FOOBAR@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FREDFOOBAR@acme.inc", emailType).asItem()));

			assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE, IGNORE_NON_ALPHA_NUM).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fred.foobar@acme.inc", emailType).asItem()));
			assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE, IGNORE_NON_ALPHA_NUM).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fredfoobar@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE, IGNORE_NON_ALPHA_NUM).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FRED.FOOBAR@acme.inc", emailType).asItem()));
			assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE, IGNORE_NON_ALPHA_NUM).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FREDFOOBAR@acme.inc", emailType).asItem()));

			assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE, IGNORE_NON_ALPHA_NUM, IGNORE_CASE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fred.foobar@acme.inc", emailType).asItem()));
			assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE, IGNORE_NON_ALPHA_NUM, IGNORE_CASE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fredfoobar@acme.inc", emailType).asItem()));
			assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE, IGNORE_NON_ALPHA_NUM, IGNORE_CASE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FRED.FOOBAR@acme.inc", emailType).asItem()));
			assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE, IGNORE_NON_ALPHA_NUM, IGNORE_CASE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FREDFOOBAR@acme.inc", emailType).asItem()));
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

		assertFalse(userRelevance(MATCH_USER_ID, MATCH_BUILDER).matches(jobOf(FREE_STYLE_PROJECT).asItem()));

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
	public void testCauses() {
		doTestCause(new TestCause() {
			@SuppressWarnings("unused")
			public String getUserBad() { return "bad";}
		}, "ANONYMOUS", "ANONYMOUS");
		doTestCause(new TestCause() {
			@SuppressWarnings("unused")
			public String getUserId() { return "user-id";}
		}, "USERID", "ANONYMOUS");
		doTestCause(new TestCause() {
			@SuppressWarnings("unused")
			public String getUserName() { return "User Name";}
		}, "ANONYMOUS", "USERNAME");
		doTestCause(new TestCause() {
			@SuppressWarnings("unused")
			public String getUserId() { return "user-id";}
			@SuppressWarnings("unused")
			public String getUserName() { return "User Name";}
		}, "USERID", "USERNAME");
	}

	private class TestCause extends Cause {
		@Override
		public String getShortDescription() {
			return null;
		}
	}

	private void doTestCause(Cause cause, String expectedId, String expectedName) {
		doTestCause(cause, false, expectedId);
		doTestCause(cause, true, expectedName);
	}

	private void doTestCause(Cause cause, boolean matchAgainstFullName, String expected) {
		UserRelevanceFilter filter = new UserRelevanceFilter(
				true, true, true, true, true,
				true, true, true,
				BuildCountType.AtLeastOne.toString(), 2, AmountType.Builds.toString(),
				AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched.toString()
				);
		String value = filter.getUserValue(cause, matchAgainstFullName);
		if (expected == null) {
			assertNull(value);
		} else {
			assertEquals(expected, value);
		}
	}

	@Test
	public void testEmailFilter() {
		List<String> emails = new ArrayList<String>();
		emails.add("user1@gmail.com, user.2@gmail.com");
		emails.add("_user_3@gmail.gov;user4a@gmail.com");
		UserRelevanceFilter filter = new UserRelevanceFilter(
				true, true, true, true, true,
				true, true, true,
				BuildCountType.AtLeastOne.toString(), 2, AmountType.Builds.toString(),
				AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched.toString()
				);
		assertEquals(true, filter.matchesEmail(emails, "USER1"));
		assertEquals(true, filter.matchesEmail(emails, "USER2"));
		assertEquals(true, filter.matchesEmail(emails, "USER3"));
		assertEquals(false, filter.matchesEmail(emails, "USER4"));
	}

	@Test
	public void testNormalize() {
		doTestNormalize("u se-r", "USER", true, true, true);
		doTestNormalize("u se-r", "user", false, true, true);
		// will still normalize the whitespace because that is non-alphanumeric
		doTestNormalize("u se-r", "USER", true, false, true);
		doTestNormalize("u se-r", "use-r", false, true, false);
		doTestNormalize("u se-r", "U SE-R", true, false, false);
	}

	private void doTestNormalize(String input, String output,
			boolean ignoreCase, boolean ignoreWhitespace, boolean ignoreNonAlphanumeric) {
		UserRelevanceFilter filter = new UserRelevanceFilter(
				true, true, ignoreCase, ignoreWhitespace, ignoreNonAlphanumeric,
				true, true, true,
				BuildCountType.AtLeastOne.toString(), 2, AmountType.Builds.toString(),
				AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched.toString()
				);
		String normalized = filter.normalize(input);
		assertEquals(output, normalized);
	}

	@Issue("JENKINS-13781")
	@Test
	public void testMatchesEmail() {
		UserRelevanceFilter filter = new UserRelevanceFilter(
				true, true, true, true, true,
				true, true, true,
				BuildCountType.AtLeastOne.toString(), 2, AmountType.Builds.toString(),
				AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched.toString()
				);
		// FIXED: would throw null-pointer
		boolean matched = filter.matchesEmail(null);
		assertFalse(matched);
	}
	
}
