package hudson.views;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.TopLevelItem;

import org.kohsuke.stapler.DataBoundConstructor;

import static hudson.model.Item.*;

/**
 *  The purpose of this filter is to identify items that have certain privileges granted for the current user, and match those items.
 *  For example, I may have set up security so that certain users can only read certain jobs;
 *  that's fine from a security standpoint, but I also want to leverage that security to create useful views;
 *  for example, one view that shows me all jobs I can configure, and another view that shows me all jobs I can only read.
 * 
 *  There's no point in matching by whether you can read a job, because if you can't see it, it won't show up anyways.
 * 
 * @author jacob
 */
public class SecurityFilter extends AbstractIncludeExcludeJobFilter {
	
	public static final String ALL = "MustMatchAll";
	public static final String ONE = "AtLeastOne";
	
	private boolean configure;
	private boolean build;
	private boolean workspace;
	private String permissionCheckType;
	
	@DataBoundConstructor
	public SecurityFilter(String permissionCheckType, boolean configure,
			boolean build, boolean workspace, String includeExcludeTypeString) {
		super(includeExcludeTypeString);
		this.configure = configure;
		this.build = build;
		this.workspace = workspace;
		this.permissionCheckType = permissionCheckType;
	}

	protected boolean matches(TopLevelItem item) {
		boolean all = permissionCheckType.equals(ALL);

		if (all) {
			return
				(!configure || item.getACL().hasPermission(CONFIGURE)) &&
				(!build || item.getACL().hasPermission(BUILD)) &&
				(!workspace || item.getACL().hasPermission(WORKSPACE));
		} else {
			return
				(configure && item.getACL().hasPermission(CONFIGURE)) ||
				(build && item.getACL().hasPermission(BUILD)) ||
				(workspace && item.getACL().hasPermission(WORKSPACE));
		}
	}

	public String getPermissionCheckType() {
		return permissionCheckType;
	}
	
	@Extension
	public static class DescriptorImpl extends Descriptor<ViewJobFilter> {
		@Override
		public String getDisplayName() {
			return hudson.views.filters.Messages.SecurityFilter_DisplayName();
		}
        @Override
        public String getHelpFile() {
            return "/plugin/view-job-filters/security-help.html";
        }
	}

	public boolean isConfigure() {
		return configure;
	}

	public boolean isBuild() {
		return build;
	}

	public boolean isWorkspace() {
		return workspace;
	}

}
