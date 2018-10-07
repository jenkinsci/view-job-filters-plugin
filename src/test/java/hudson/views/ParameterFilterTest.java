package hudson.views;

import hudson.model.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.hudson.test.JenkinsRule;

import static hudson.views.test.BuildMocker.build;
import static hudson.views.test.JobMocker.freeStyleProject;
import static hudson.views.test.ViewJobFilters.parameter;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ParameterFilterTest extends AbstractHudsonTest {

	@Test
	public void testDoesntMatchTopLevelItem() {
		assertFalse(parameter(".*", ".*", ".*").matches(mock(TopLevelItem.class)));
	}

	@Test
	public void testMatchesDefaultValue() {
		assertFalse(parameter(".*", ".*", ".*").matches(freeStyleProject().asItem()));

		testMatchesDefaultValue(new StringParameterDefinition("name", "test", "multi-line\ndesc"));
		testMatchesDefaultValue(new BooleanParameterDefinition("name", true, "multi-line\ndesc"));
		testMatchesDefaultValue(new ChoiceParameterDefinition("name", "multi-line\ntest", "multi-line\ndesc"));
		testMatchesDefaultValue(newFileParameterDefinition("name", "test.txt", "multi-line\ndesc"));
		testMatchesDefaultValue(newSimpleParameterDefinition("name", "test", "multi-line\ndesc"));
	}

	private ParameterDefinition newFileParameterDefinition(String name, String file, String desc) {
		FileParameterValue parameterValue = new FileParameterValue(name, new File(file), file);
		parameterValue.setDescription(desc);

		FileParameterDefinition parameter = mock(FileParameterDefinition.class);
		when(parameter.getName()).thenReturn(name);
		when(parameter.getDefaultParameterValue()).thenReturn(parameterValue);
		when(parameter.getDescription()).thenReturn(desc);
		return parameter;
	}

	private ParameterDefinition newSimpleParameterDefinition(String name, String value, String desc) {
		ParameterValue parameterValue = mock(ParameterValue.class);
		when(parameterValue.toString()).thenReturn(value);

		ParameterDefinition parameter = mock(SimpleParameterDefinition.class);
		when(parameter.getName()).thenReturn(name);
		when(parameter.getDefaultParameterValue()).thenReturn(parameterValue);
		when(parameter.getDescription()).thenReturn(desc);
		return parameter;
	}

	public void testMatchesDefaultValue(ParameterDefinition parameter) {
		assertTrue(parameter("n.me", null, null).matches(freeStyleProject().parameters(parameter).asItem()));
	 	assertTrue(parameter("n.me", "", "").matches(freeStyleProject().parameters(parameter).asItem()));

	 	assertTrue(parameter(null,"t.*", null).matches(freeStyleProject().parameters(parameter).asItem()));
	 	assertTrue(parameter("","t.*", "").matches(freeStyleProject().parameters(parameter).asItem()));

	 	assertTrue(parameter(null,null, "desc(ription)?").matches(freeStyleProject().parameters(parameter).asItem()));
	 	assertTrue(parameter("","", "desc(ription)?").matches(freeStyleProject().parameters(parameter).asItem()));

		assertFalse(parameter("nameRegex", ".*", ".*").matches(freeStyleProject().parameters(parameter).asItem()));
		assertFalse(parameter(".*", "valueRegex", ".*").matches(freeStyleProject().parameters(parameter).asItem()));
		assertFalse(parameter(".*", ".*", "descriptionRegex").matches(freeStyleProject().parameters(parameter).asItem()));

		assertTrue(parameter(".*", ".*", ".*").matches(freeStyleProject().parameters(parameter).asItem()));
		assertTrue(parameter("n.me", "t.*", "desc(ription)?").matches(freeStyleProject().parameters(parameter).asItem()));
	}

	@Test
	public void testMatchLastBuild() {
		assertFalse(parameter(".*", ".*", ".*", false, 0, false).matches(
			freeStyleProject().lastBuilds(build().create()).asItem()
		));

		assertFalse(parameter("name\\d", "t.*", ".*", false, 0, false).matches(
			freeStyleProject().lastBuilds(
				build().parameters(new StringParameterValue("nameA", "test", "multi-line\ndesc")).create(),
				build().parameters(new StringParameterValue("name1", "test", "multi-line\ndesc")).create()
			).asItem()
		));

		assertTrue(parameter("name\\d", "t.*", ".*", false, 0, false).matches(
			freeStyleProject().lastBuilds(
				build().parameters(new StringParameterValue("name1", "test", "multi-line\ndesc")).create(),
				build().parameters(new StringParameterValue("nameA", "test", "multi-line\ndesc")).create()
			).asItem()
		));

		assertFalse(parameter("name\\d", "t.*", ".*", false, 0, false).matches(
			freeStyleProject().lastBuilds(
				build().parameters(new StringParameterValue("name1", "test", "multi-line\ndesc")).building(true).create(),
				build().parameters(new StringParameterValue("nameA", "test", "multi-line\ndesc")).create()
			).asItem()
		));

		assertTrue(parameter("name\\d", "t.*", ".*", false, 0, false).matches(
			freeStyleProject().lastBuilds(
				build().parameters(new StringParameterValue("name1", "test", "multi-line\ndesc")).building(true).create(),
				build().parameters(new StringParameterValue("name2", "test", "multi-line\ndesc")).create()
			).asItem()
		));

		assertTrue(parameter("name\\d", "t.*", ".*", false, 0, true).matches(
			freeStyleProject().lastBuilds(
				build().parameters(new StringParameterValue("name1", "test", "multi-line\ndesc")).building(true).create(),
				build().parameters(new StringParameterValue("nameA", "test", "multi-line\ndesc")).create()
			).asItem()
		));
    }

	@Test
	public void testMatchAllBuilds() {
		assertFalse(parameter(".*", ".*", ".*", true, 2, false).matches(
			freeStyleProject().lastBuilds(build().create()).asItem()
		));

		assertTrue(parameter("name\\d", ".*", ".*", true, 2, false).matches(
			freeStyleProject().lastBuilds(
				build().parameters(new StringParameterValue("nameA", "test", "multi-line\ndesc")).create(),
				build().parameters(new StringParameterValue("name1", "test", "multi-line\ndesc")).create()
			).asItem()
		));

		assertFalse(parameter("name\\d", "t.*", "desc", true, 2, false).matches(
			freeStyleProject().lastBuilds(
				build().parameters(new StringParameterValue("nameA", "test", "multi-line\ndesc")).create(),
				build().parameters(new StringParameterValue("nameB", "test", "multi-line\ndesc")).create(),
				build().parameters(new BooleanParameterValue("name1", true, "multi-line\ndesc")).create()
			).asItem()
		));

		assertFalse(parameter("name\\d", "t.*", "desc", true, 5, false).matches(
			freeStyleProject().lastBuilds(
				build().parameters(new StringParameterValue("nameA", "test", "multi-line\ndesc")).create(),
				build().parameters(new StringParameterValue("nameB", "test", "multi-line\ndesc")).create(),
				build().parameters(new BooleanParameterValue("nameC", true, "multi-line\ndesc")).create()
			).asItem()
		));

		assertTrue(parameter("name\\d", "t.*", "desc", true, 5, false).matches(
			freeStyleProject().lastBuilds(
				build().parameters(new StringParameterValue("nameA", "test", "multi-line\ndesc")).create(),
				build().parameters(new StringParameterValue("nameB", "test", "multi-line\ndesc")).create(),
				build().parameters(new BooleanParameterValue("name1", true, "multi-line\ndesc")).create()
			).asItem()
		));

		assertTrue(parameter("name\\d", "t.*", "desc", true, 0, false).matches(
			freeStyleProject().lastBuilds(
				build().parameters(new StringParameterValue("nameA", "test", "multi-line\ndesc")).create(),
				build().parameters(new StringParameterValue("nameB", "test", "multi-line\ndesc")).create(),
				build().parameters(new BooleanParameterValue("name1", true, "multi-line\ndesc")).create()
			).asItem()
		));

		assertFalse(parameter("name\\d", "t.*", "desc", true, 2, false).matches(
			freeStyleProject().lastBuilds(
				build().parameters(new StringParameterValue("name1", "test", "multi-line\ndesc")).building(true).create(),
				build().parameters(new StringParameterValue("name2", "test", "multi-line\ndesc")).building(true).create(),
				build().parameters(new BooleanParameterValue("nameC", true, "multi-line\ndesc")).create()
			).asItem()
		));

		assertFalse(parameter("name\\d", "t.*", "desc", true, 2, false).matches(
			freeStyleProject().lastBuilds(
				build().parameters(new StringParameterValue("name1", "test", "multi-line\ndesc")).building(true).create(),
				build().parameters(new StringParameterValue("name2", "test", "multi-line\ndesc")).building(true).create(),
				build().parameters(new BooleanParameterValue("nameC", true, "multi-line\ndesc")).create()
			).asItem()
		));

		assertTrue(parameter("name\\d", "t.*", "desc", true, 2, true).matches(
			freeStyleProject().lastBuilds(
				build().parameters(new StringParameterValue("name1", "test", "multi-line\ndesc")).building(true).create(),
				build().parameters(new StringParameterValue("name2", "test", "multi-line\ndesc")).building(true).create(),
				build().parameters(new BooleanParameterValue("nameC", true, "multi-line\ndesc")).create()
			).asItem()
		));
	}

	@Test
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

	@Test
	public void testBuildValue() throws Exception {
		ParameterFilter filter = new ParameterFilter(AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched.toString(),
				"N", "V1", "", true, false, 0, false);

		FreeStyleProject proj = j.createFreeStyleProject("P1");

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
