package hudson.views;

import hudson.model.TopLevelItem;

import org.kohsuke.stapler.DataBoundConstructor;

public class SecurityFilter extends AbstractIncludeExcludeJobFilter {
	
	@DataBoundConstructor
	public SecurityFilter(String includeExcludeTypeString) {
		super(includeExcludeTypeString);
	}
	
	@SuppressWarnings("unchecked")
	protected boolean matches(TopLevelItem item) {
		
		// purpose is to identify items that have certain privileges granted for the current user, and match those items.
		// for example, I may have set up security so that certain users can only read certain jobs;
		// that's fine from a security standpoint, but I also want to leverage that security to create useful views;
		// for example, one view that shows me all jobs I can configure, and another view that shows me all jobs I can only read
		
		// there's no point in matching by whether you can read a job, because if you can't see it, it won't show up anyways
		
		// TODO identify all the out-of-the-box configurations (from main admin screen) - will be things like read/configure/build
		// TODO research the way this works - are these names hard-coded somewhere I can use?
		// TODO need to verify with JIRA submitter that this is what he means, because looking at his ticket that's not what he says,
		//		but what he says doesn't quite make sense
		
//		item
//		Hudson.getInstance().getACL().hasPermission(item)
		return false;
	}

}
