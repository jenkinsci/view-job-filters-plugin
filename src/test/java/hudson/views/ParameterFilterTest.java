package hudson.views;

import hudson.model.Action;
import hudson.model.FreeStyleBuild;
import hudson.model.ParameterValue;
import hudson.model.Cause;
import hudson.model.ChoiceParameterDefinition;
import hudson.model.FreeStyleProject;
import hudson.model.ParameterDefinition;
import hudson.model.ParametersAction;
import hudson.model.ParametersDefinitionProperty;
import hudson.model.StringParameterValue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.jvnet.hudson.test.HudsonTestCase;

public class ParameterFilterTest extends HudsonTestCase {

	public void testMatchesParameter() {
		doTestMatchesParameter("N1", null, null, "N2", null, null, false);
		doTestMatchesParameter("N.*", null, null,"N2", null, null, true);
		doTestMatchesParameter("N.*", null, null, "AN2", null, null, false);
	}
	private void doTestMatchesParameter(String nameRegex, String valueRegex, String descRegex, 
			String name, String value, String desc, boolean expectMatched) {
		ParameterFilter filter = new ParameterFilter(AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched.toString(),
				nameRegex, valueRegex, descRegex, false, false, 0, false);
		boolean matched = filter.matchesParameter(name, value, false, desc);
		assertEquals(expectMatched, matched);
	}
	
	public void testBuildValue() throws Exception {
		ParameterFilter filter = new ParameterFilter(AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched.toString(),
				"N", "V1", "", true, false, 0, false);

		FreeStyleProject proj = createFreeStyleProject("P1");

		List<ParameterDefinition> defs = new ArrayList<ParameterDefinition>();
		ParametersDefinitionProperty prop = new ParametersDefinitionProperty(defs);
		
		proj.addProperty(prop);
		
		boolean matches = filter.matches(proj);
		assertFalse(matches);
		
		ChoiceParameterDefinition def = new ChoiceParameterDefinition("N", new String[]{"V1", "V2"}, "the description");
		defs.add(def);
		
		// will match, because it looks over all choices 
		matches = filter.matches(proj);
		assertTrue(matches);
		
		
		ParameterFilter filter2 = new ParameterFilter(AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched.toString(),
				"N", "V1", "", false, false, 0, false);
		// won't match, because it's not a build param
		matches = filter2.matches(proj);
		assertFalse(matches);
		
		List<ParameterValue> vals = new ArrayList<ParameterValue>();
		vals.add(new StringParameterValue("N", "V1"));
		ParametersAction action = new ParametersAction(vals);
		Action[] actions = {action};
		Future<FreeStyleBuild> future = proj.scheduleBuild2(0, new Cause.UserCause(), actions);
		future.get();

		// will match this time because we added a build
		matches = filter2.matches(proj);
		assertTrue(matches);
		
		vals = new ArrayList<ParameterValue>();
		vals.add(new StringParameterValue("N", "V2"));
		action = new ParametersAction(vals);
		actions = new Action[]{action};
		future = proj.scheduleBuild2(0, new Cause.UserCause(), actions);
		future.get();

		// will NOT match this time because we added a build that had a different value
		matches = filter2.matches(proj);
		assertFalse(matches);
	}
	
}
