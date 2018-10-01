package hudson.views;

import hudson.model.Cause;
import hudson.model.User;
import hudson.views.AbstractBuildTrendFilter.AmountType;
import hudson.views.AbstractBuildTrendFilter.BuildCountType;

import java.util.ArrayList;
import java.util.List;

import jenkins.model.Jenkins;
import junit.framework.TestCase;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.TestingAuthenticationToken;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.junit.Before;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;

import static hudson.views.test.JobMocker.EmailType.DEFAULT;
import static hudson.views.test.JobMocker.jobOf;
import static hudson.views.test.JobType.FREE_STYLE_PROJECT;
import static hudson.views.test.ViewJobFilters.UserRelevanceOption.*;
import static hudson.views.test.ViewJobFilters.userRelevance;
import static org.junit.Assert.*;

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
	public void testEmail() throws Exception {
		setCurrentUser("fred.foobar", "fred foobar");

		assertTrue(userRelevance(MATCH_USER_ID, MATCH_EMAIL).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fred.foobar@acme.inc", DEFAULT).asItem()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fredfoobar@acme.inc", DEFAULT).asItem()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FRED.FOOBAR@acme.inc", DEFAULT).asItem()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FREDFOOBAR@acme.inc", DEFAULT).asItem()));

		assertTrue(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_CASE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fred.foobar@acme.inc", DEFAULT).asItem()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_CASE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fredfoobar@acme.inc", DEFAULT).asItem()));
		assertTrue(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_CASE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FRED.FOOBAR@acme.inc", DEFAULT).asItem()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_CASE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FREDFOOBAR@acme.inc", DEFAULT).asItem()));

		assertTrue(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_NON_ALPHA_NUM).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fred.foobar@acme.inc", DEFAULT).asItem()));
		assertTrue(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_NON_ALPHA_NUM).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fredfoobar@acme.inc", DEFAULT).asItem()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_NON_ALPHA_NUM).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FRED.FOOBAR@acme.inc", DEFAULT).asItem()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_NON_ALPHA_NUM).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FREDFOOBAR@acme.inc", DEFAULT).asItem()));

		assertTrue(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_WHITESPACE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fred.foobar@acme.inc", DEFAULT).asItem()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_WHITESPACE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fredfoobar@acme.inc", DEFAULT).asItem()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_WHITESPACE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FRED.FOOBAR@acme.inc", DEFAULT).asItem()));
		assertFalse(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_WHITESPACE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FREDFOOBAR@acme.inc", DEFAULT).asItem()));

		assertTrue(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_CASE, IGNORE_NON_ALPHA_NUM).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fred.foobar@acme.inc", DEFAULT).asItem()));
		assertTrue(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_CASE, IGNORE_NON_ALPHA_NUM).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fredfoobar@acme.inc", DEFAULT).asItem()));
		assertTrue(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_CASE, IGNORE_NON_ALPHA_NUM).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FRED.FOOBAR@acme.inc", DEFAULT).asItem()));
		assertTrue(userRelevance(MATCH_USER_ID, MATCH_EMAIL, IGNORE_CASE, IGNORE_NON_ALPHA_NUM).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FREDFOOBAR@acme.inc", DEFAULT).asItem()));

		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fred.foobar@acme.inc", DEFAULT).asItem()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fredfoobar@acme.inc", DEFAULT).asItem()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FRED.FOOBAR@acme.inc", DEFAULT).asItem()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FREDFOOBAR@acme.inc", DEFAULT).asItem()));

		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_CASE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fred.foobar@acme.inc", DEFAULT).asItem()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_CASE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fredfoobar@acme.inc", DEFAULT).asItem()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_CASE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FRED.FOOBAR@acme.inc", DEFAULT).asItem()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_CASE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FREDFOOBAR@acme.inc", DEFAULT).asItem()));

		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_NON_ALPHA_NUM).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fred.foobar@acme.inc", DEFAULT).asItem()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_NON_ALPHA_NUM).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fredfoobar@acme.inc", DEFAULT).asItem()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_NON_ALPHA_NUM).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FRED.FOOBAR@acme.inc", DEFAULT).asItem()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_NON_ALPHA_NUM).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FREDFOOBAR@acme.inc", DEFAULT).asItem()));

		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fred.foobar@acme.inc", DEFAULT).asItem()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fredfoobar@acme.inc", DEFAULT).asItem()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FRED.FOOBAR@acme.inc", DEFAULT).asItem()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FREDFOOBAR@acme.inc", DEFAULT).asItem()));

		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE, IGNORE_NON_ALPHA_NUM).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fred.foobar@acme.inc", DEFAULT).asItem()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE, IGNORE_NON_ALPHA_NUM).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fredfoobar@acme.inc", DEFAULT).asItem()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE, IGNORE_NON_ALPHA_NUM).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FRED.FOOBAR@acme.inc", DEFAULT).asItem()));
		assertFalse(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE, IGNORE_NON_ALPHA_NUM).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FREDFOOBAR@acme.inc", DEFAULT).asItem()));

		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE, IGNORE_NON_ALPHA_NUM, IGNORE_CASE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fred.foobar@acme.inc", DEFAULT).asItem()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE, IGNORE_NON_ALPHA_NUM, IGNORE_CASE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("fredfoobar@acme.inc", DEFAULT).asItem()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE, IGNORE_NON_ALPHA_NUM, IGNORE_CASE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FRED.FOOBAR@acme.inc", DEFAULT).asItem()));
		assertTrue(userRelevance(MATCH_USER_FULL_NAME, MATCH_EMAIL, IGNORE_WHITESPACE, IGNORE_NON_ALPHA_NUM, IGNORE_CASE).matches(jobOf(FREE_STYLE_PROJECT).withEmail("FREDFOOBAR@acme.inc", DEFAULT).asItem()));
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
