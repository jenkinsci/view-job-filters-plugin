package hudson.views;

import hudson.security.AuthorizationMatrixProperty;
import hudson.views.PluginHelperUtils.PluginHelperTestable;


public class MatrixAuthorizationHelper implements PluginHelperTestable {

	@Override
	public Class getPluginTesterClass() {
		return AuthorizationMatrixProperty.class;
	}
	
}
