package hudson.views;

import hudson.model.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.junit.Test;
import org.jvnet.hudson.test.WithoutJenkins;

import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.*;
import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeUnmatched;
import static hudson.views.test.BuildMocker.build;
import static hudson.views.test.JobMocker.freeStyleProject;
import static hudson.views.test.ViewJobFilters.parameter;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ParameterFilterTest extends AbstractJenkinsTest {

	@Test
	@WithoutJenkins
	public void testDoesntMatchTopLevelItem() {
		assertFalse(parameter(".*", ".*", ".*").matches(mock(TopLevelItem.class)));
	}

	@Test
	@WithoutJenkins
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
	@WithoutJenkins
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
	@WithoutJenkins
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

	@Test
	public void testConfigRoundtrip() throws Exception {
		testConfigRoundtrip(
			"view-1",
			new ParameterFilter(includeUnmatched.name(),"a", "b", "c",
				false, true, 5, true)
		);

		testConfigRoundtrip(
			"view-2",
			new ParameterFilter(excludeMatched.name(),"x", "y", "z",
				true, false, 0, false),
			new ParameterFilter(includeMatched.name(),"", ".*", ".?",
				false, true, 10, false)
		);
	}

	private void testConfigRoundtrip(String viewName, ParameterFilter... filters) throws Exception {
		List<ParameterFilter> expectedFilters = new ArrayList<ParameterFilter>();
		for (ParameterFilter filter: filters) {
			expectedFilters.add(new ParameterFilter(
					filter.getIncludeExcludeTypeString(),
					filter.getNameRegex(),
					filter.getValueRegex(),
					filter.getDescriptionRegex(),
					filter.isUseDefaultValue(),
					filter.isMatchAllBuilds(),
					filter.getMaxBuildsToMatch(),
					filter.isMatchBuildsInProgress()));
		}

		ListView view = createFilteredView(viewName, filters);
		j.configRoundtrip(view);

		ListView viewAfterRoundtrip = (ListView)j.getInstance().getView(viewName);
		assertFilterEquals(expectedFilters, viewAfterRoundtrip.getJobFilters());

		viewAfterRoundtrip.save();
		j.getInstance().reload();

		ListView viewAfterReload = (ListView)j.getInstance().getView(viewName);
		assertFilterEquals(expectedFilters, viewAfterReload.getJobFilters());
	}

	private void assertFilterEquals(List<ParameterFilter> expectedFilters, List<ViewJobFilter> actualFilters) {
		assertThat(actualFilters.size(), is(expectedFilters.size()));
		for (int i = 0; i < actualFilters.size(); i++) {
			ViewJobFilter actualFilter = actualFilters.get(i);
			ParameterFilter expectedFilter = expectedFilters.get(i);
			assertThat(actualFilter, instanceOf(ParameterFilter.class));
			assertThat(((ParameterFilter)actualFilter).getIncludeExcludeTypeString(), is(expectedFilter.getIncludeExcludeTypeString()));
			assertThat(((ParameterFilter)actualFilter).getNameRegex(), is(expectedFilter.getNameRegex()));
			assertThat(((ParameterFilter)actualFilter).getValueRegex(), is(expectedFilter.getValueRegex()));
			assertThat(((ParameterFilter)actualFilter).getDescriptionRegex(), is(expectedFilter.getDescriptionRegex()));
			assertThat(((ParameterFilter)actualFilter).isUseDefaultValue(), is(expectedFilter.isUseDefaultValue()));
			assertThat(((ParameterFilter)actualFilter).isMatchAllBuilds(), is(expectedFilter.isMatchAllBuilds()));
			assertThat(((ParameterFilter)actualFilter).getMaxBuildsToMatch(), is(expectedFilter.getMaxBuildsToMatch()));
			assertThat(((ParameterFilter)actualFilter).isMatchBuildsInProgress(), is(expectedFilter.isMatchBuildsInProgress()));
		}
	}

	
}
