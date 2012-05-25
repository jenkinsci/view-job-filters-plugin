package hudson.views;

import hudson.Extension;
import hudson.model.TopLevelItem;
import hudson.model.AbstractBuild;
import hudson.model.Cause;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.Run;
import hudson.model.User;
import hudson.scm.ChangeLogSet;
import hudson.scm.ChangeLogSet.Entry;
import hudson.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Filters jobs that are relevant to the user.
 * @author Jacob.Robertson
 */
public class UserRelevanceFilter extends AbstractBuildTrendFilter {

	private static final String ANONYMOUS = Hudson.ANONYMOUS.getName().toUpperCase();
	
	private boolean matchUserId = true;
	private boolean matchUserFullName = true;
	
	private boolean ignoreCase = true;
	private boolean ignoreWhitespace = true;
	private boolean ignoreNonAlphaNumeric = true;
	
	private boolean matchBuilder = true;
	private boolean matchEmail = true;
	private boolean matchScmChanges = true;
	
	@DataBoundConstructor
	public UserRelevanceFilter(
			boolean matchUserId, boolean matchUserFullName,
			boolean ignoreCase, boolean ignoreWhitespace, boolean ignoreNonAlphaNumeric,
			boolean matchBuilder, boolean matchEmail, boolean matchScmChanges,
			String buildCountTypeString,
			float amount, String amountTypeString,
			String includeExcludeTypeString) {
		super(buildCountTypeString, amount, amountTypeString, includeExcludeTypeString);
		this.matchUserId = matchUserId;
		this.matchUserFullName = matchUserFullName;
		if (!matchUserId && !matchUserFullName) {
			// have to have at least one match
			this.matchUserId = true;
		}
		this.ignoreCase = ignoreCase;
		this.ignoreWhitespace = ignoreWhitespace;
		this.ignoreNonAlphaNumeric = ignoreNonAlphaNumeric;
		this.matchBuilder = matchBuilder;
		this.matchEmail = matchEmail;
		this.matchScmChanges = matchScmChanges;
	}

	protected boolean matches(TopLevelItem item) {
		if (matchEmail) {
			boolean matched = matchesEmail(item);
			if (matched) {
				return true;
			}
		}
		return super.matches(item);
	}
	
    @SuppressWarnings("unchecked")
	@Override
    public boolean matchesRun(Run run) {
		User user = getUser();
		if (user == null) {
			return false;
		}
		if (matchUserFullName) {
			String userName = normalize(user.getFullName());
			if (runMatches(userName, true, run)) {
				return true;
			}
		}
		if (matchUserId) {
			String userId = normalize(user.getId());
			if (runMatches(userId, false, run)) {
				return true;
			}
		}
		return false;
    }
    private User getUser() {
    	try {
    		return Hudson.getInstance().getMe();
    	} catch (Exception e) {
        	try {
        		return Hudson.getInstance().getUser(Hudson.ANONYMOUS.getName());
        	} catch (Exception e2) {
            	return null;
        	}
    	}
    }
    @SuppressWarnings("unchecked")
	public boolean runMatches(String userName, boolean matchAgainstFullName, Run run) {
    	if (matchScmChanges) {
    		boolean matches = matchesChangeLog(userName, matchAgainstFullName, run);
    		if (matches) {
    			return true;
    		}
    	}
    	if (matchBuilder) {
    		boolean matches = matchesUserCause(userName, matchAgainstFullName, run);
    		if (matches) {
    			return true;
    		}
    	}
    	return false;
    }
    public String normalize(String userName) {
    	if (ignoreCase) {
    		userName = userName.toUpperCase();
    	}
    	if (!ignoreNonAlphaNumeric && !ignoreWhitespace) {
    		return userName;
    	}
    	StringBuilder buf = new StringBuilder();
    	int len = userName.length();
    	for (int i = 0; i < len; i++) {
			char c = userName.charAt(i);
			if (ignoreNonAlphaNumeric && !Character.isLetterOrDigit(c)) {
				continue;
			}
			if (ignoreWhitespace && Character.isWhitespace(c)) {
				continue;
			}
			buf.append(c);
		}
    	
    	return buf.toString();
    }
    
	public boolean matchesEmail(TopLevelItem item) {
		User user = getUser();
		if (user == null) {
			return false;
		}
		if (matchUserFullName) {
			String userName = normalize(user.getFullName());
			if (matchesEmail(item, userName)) {
				return true;
			}
		}
		if (matchUserId) {
			String userId = normalize(user.getId());
			if (matchesEmail(item, userId)) {
				return true;
			}
		}
		return false;
	}
	public boolean matchesEmail(TopLevelItem item, String user) {
		List<String> emails = EmailValuesHelper.getValues(item);
		return matchesEmail(emails, user);
	}
	public boolean matchesEmail(List<String> emails, String user) {
		for (String email: emails) {
			boolean matched = matchSplitEmailToUserNames(user, email);
			if (matched) {
				return true;
			}
		}
		return false;
	}
	public boolean matchSplitEmailToUserNames(String user, String email) {
		String[] split = email.split("[\\s;,]+");
		for (String one: split) {
			int index = one.indexOf("@");
			if (index > 0) {
				one = one.substring(0, index);
			}
			one = normalize(one);
			if (one.equals(user)) {
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public boolean matchesUserCause(String userName, boolean matchAgainstFullName, Run run) {
		for (Object causeObject: run.getCauses()) {
			Cause cause = (Cause) causeObject;
			String builderName = getUserValue(cause, matchAgainstFullName);
			if (builderName != null) {
				boolean matches = userName.equals(builderName);
				if (matches) {
					return true;
				}
			} 
		}
		return false;
	}
	public String getUserValue(Cause cause, boolean matchAgainstFullName) {
		String builderName;
		if (matchAgainstFullName) {
			builderName = getUserValue(cause, "getUserName");
		} else {
			builderName = getUserValue(cause, "getUserId");
		}
		if (builderName == null) {
			builderName = ANONYMOUS;
		}
		return builderName;
	}
	public String getUserValue(Cause cause, String methodName) {
		Method m = ReflectionUtils.getPublicMethodNamed(cause.getClass(), methodName);
		if (m == null) {
			return null;
		}
		try {
			String value = (String) m.invoke(cause, (Object[]) null);
			if (value != null) {
				value = normalize(value);
			}
			return value;
		} catch (Exception e) {
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	public boolean matchesChangeLog(String userName, boolean matchAgainstFullName, Run run) {
		if (run instanceof AbstractBuild) {
			AbstractBuild build = (AbstractBuild) run;
			ChangeLogSet<Entry> set = build.getChangeSet();
			for (Entry entry: set) {
				User changeUser = entry.getAuthor();
				String userMatchPart;
				if (matchAgainstFullName) {
					userMatchPart = changeUser.getFullName();
				} else {
					userMatchPart = changeUser.getId();
				}
				String changeUserName = normalize(userMatchPart);
				if (userName.equals(changeUserName)) {
					return true;
				}
			}
			return false;
		} else {
			return false;
		}
	}

	@Extension
	public static class DescriptorImpl extends Descriptor<ViewJobFilter> {
		@Override
		public String getDisplayName() {
			return "Logged-in User Relevance Filter";
		}
		@Override
        public String getHelpFile() {
            return "/plugin/view-job-filters/user-relevance-help.html";
        }
	}

	public boolean isMatchUserId() {
		return matchUserId;
	}

	public boolean isMatchUserFullName() {
		return matchUserFullName;
	}

	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	public boolean isIgnoreWhitespace() {
		return ignoreWhitespace;
	}

	public boolean isIgnoreNonAlphaNumeric() {
		return ignoreNonAlphaNumeric;
	}

	public boolean isMatchBuilder() {
		return matchBuilder;
	}

	public boolean isMatchEmail() {
		return matchEmail;
	}

	public boolean isMatchScmChanges() {
		return matchScmChanges;
	}
	
}
