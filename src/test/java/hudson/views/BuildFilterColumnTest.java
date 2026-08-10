package hudson.views;

import org.htmlunit.html.DomNode;
import org.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.Test;
import hudson.Functions;
import hudson.model.*;
import hudson.tasks.BatchFile;
import hudson.tasks.Shell;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;
import org.xml.sax.SAXException;

import java.io.IOException;

import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

@WithJenkins
class BuildFilterColumnTest extends AbstractJenkinsTest {

	@Test
	void testWrappedStatusColumn() throws Exception {
        FreeStyleProject project = createProject();

        ListView view = createViewWithBuildFilterColumn(new StatusColumn());

        runWithParameter(project, Result.SUCCESS, "BRANCH", "master");
        runWithParameter(project, Result.SUCCESS, "BRANCH", "test");

        assertThat(getBuildFilterColumn(view).querySelector("svg").getAttributes().getNamedItem("title").getTextContent(), containsString("Success"));

        runWithParameter(project, Result.FAILURE, "BRANCH", "master");
        runWithParameter(project, Result.SUCCESS, "BRANCH", "test");

        assertThat(getBuildFilterColumn(view).querySelector("svg").getAttributes().getNamedItem("title").getTextContent(), containsString("Failed"));

        runWithParameter(project, Result.SUCCESS, "BRANCH", "master");
        runWithParameter(project, Result.FAILURE, "BRANCH", "test");

        assertThat(getBuildFilterColumn(view).querySelector("svg").getAttributes().getNamedItem("title").getTextContent(), containsString("Success"));
    }

	@Test
	void testWrappedWeatherColumn() throws Exception {
        FreeStyleProject project = createProject();

        ListView view = createViewWithBuildFilterColumn(new WeatherColumn());

        runWithParameter(project, Result.SUCCESS, "BRANCH", "test");
        runWithParameter(project, Result.FAILURE, "BRANCH", "master");

        assertThat(getBuildFilterColumn(view).getAttributes().getNamedItem("data-html-tooltip").getTextContent(), containsString("All recent builds failed"));

        runWithParameter(project, Result.SUCCESS, "BRANCH", "test");
        runWithParameter(project, Result.SUCCESS, "BRANCH", "master");

        assertThat(getBuildFilterColumn(view).getAttributes().getNamedItem("data-html-tooltip").getTextContent(), containsString("1 out of the last 2 builds failed"));

        runWithParameter(project, Result.SUCCESS, "BRANCH", "test");
        runWithParameter(project, Result.SUCCESS, "BRANCH", "master");

        assertThat(getBuildFilterColumn(view).getAttributes().getNamedItem("data-html-tooltip").getTextContent(), containsString("1 out of the last 3 builds failed"));

        runWithParameter(project, Result.SUCCESS, "BRANCH", "test");
        runWithParameter(project, Result.SUCCESS, "BRANCH", "master");

        assertThat(getBuildFilterColumn(view).getAttributes().getNamedItem("data-html-tooltip").getTextContent(), containsString("1 out of the last 4 builds failed"));

        runWithParameter(project, Result.SUCCESS, "BRANCH", "test");
        runWithParameter(project, Result.SUCCESS, "BRANCH", "master");

        assertThat(getBuildFilterColumn(view).getAttributes().getNamedItem("data-html-tooltip").getTextContent(), containsString("1 out of the last 5 builds failed"));

        runWithParameter(project, Result.SUCCESS, "BRANCH", "test");
        runWithParameter(project, Result.SUCCESS, "BRANCH", "master");

        assertThat(getBuildFilterColumn(view).getAttributes().getNamedItem("data-html-tooltip").getTextContent(), containsString("No recent builds failed"));
    }

    private FreeStyleProject createProject() throws IOException {
        FreeStyleProject p = createFreeStyleProject("project");
        p.addProperty(new ParametersDefinitionProperty(new StringParameterDefinition("BRANCH", null)));
        return p;
    }

    private FreeStyleBuild runWithParameter(FreeStyleProject project, Result result, String paramName, String paramValue) throws Exception {
        String command = "exit " + ((result == Result.SUCCESS) ? 0 : 1);
        project.getBuildersList().clear();
        project.getBuildersList().add(Functions.isWindows() ? new BatchFile(command) : new Shell(command));
        return j.assertBuildStatus(result, project.scheduleBuild2(0, (Cause)null, new ParametersAction(new StringParameterValue(paramName, paramValue))));
    }

    private ListView createViewWithBuildFilterColumn(ListViewColumn column) throws IOException {
        ListView view = createFilteredView("view", new ParameterFilter(includeMatched.name(),
                "BRANCH", "master", "",
                false, true, 0, false));
        view.getColumns().clear();
        view.getColumns().add(new BuildFilterColumn(column, view));
        j.getInstance().addView(view);
        return view;
    }

    private DomNode getBuildFilterColumn(ListView view) throws IOException, SAXException {
        HtmlPage viewPage = j.createWebClient().getPage(view);
        return viewPage.querySelectorAll(".jenkins-table > tbody > tr > td").get(0);
    }
}
