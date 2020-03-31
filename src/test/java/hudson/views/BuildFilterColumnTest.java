package hudson.views;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import hudson.Functions;
import hudson.model.*;
import hudson.tasks.BatchFile;
import hudson.tasks.Shell;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class BuildFilterColumnTest extends AbstractJenkinsTest {

    @Test
    public void testWrappedStatusColumn() throws Exception {
        FreeStyleProject project = createFreeStyleProject("project");

        ListView view = createViewWithBuildFilterColumn(new StatusColumn());

        runWithParameter(project, Result.SUCCESS, "BRANCH", "master");
        runWithParameter(project, Result.SUCCESS, "BRANCH", "test");

        assertThat(getBuildFilterColumn(view).querySelector("img").getAttributes().getNamedItem("class").getTextContent(), containsString("icon-blue"));

        runWithParameter(project, Result.FAILURE, "BRANCH", "master");
        runWithParameter(project, Result.SUCCESS, "BRANCH", "test");

        assertThat(getBuildFilterColumn(view).querySelector("img").getAttributes().getNamedItem("class").getTextContent(), containsString("icon-red"));

        runWithParameter(project, Result.SUCCESS, "BRANCH", "master");
        runWithParameter(project, Result.FAILURE, "BRANCH", "test");

        assertThat(getBuildFilterColumn(view).querySelector("img").getAttributes().getNamedItem("class").getTextContent(), containsString("icon-blue"));
    }

    @Test
    public void testWrappedWeatherColumn() throws Exception {
        FreeStyleProject project = createFreeStyleProject("project");

        ListView view = createViewWithBuildFilterColumn(new WeatherColumn());

        runWithParameter(project, Result.SUCCESS, "BRANCH", "test");
        runWithParameter(project, Result.FAILURE, "BRANCH", "master");

        assertThat(getBuildFilterColumn(view).querySelector("img").getAttributes().getNamedItem("class").getTextContent(), containsString("00to19"));

        runWithParameter(project, Result.SUCCESS, "BRANCH", "test");
        runWithParameter(project, Result.SUCCESS, "BRANCH", "master");

        assertThat(getBuildFilterColumn(view).querySelector("img").getAttributes().getNamedItem("class").getTextContent(), containsString("40to59"));

        runWithParameter(project, Result.SUCCESS, "BRANCH", "test");
        runWithParameter(project, Result.SUCCESS, "BRANCH", "master");

        assertThat(getBuildFilterColumn(view).querySelector("img").getAttributes().getNamedItem("class").getTextContent(), containsString("60to79"));

        runWithParameter(project, Result.SUCCESS, "BRANCH", "test");
        runWithParameter(project, Result.SUCCESS, "BRANCH", "master");

        assertThat(getBuildFilterColumn(view).querySelector("img").getAttributes().getNamedItem("class").getTextContent(), containsString("60to79"));

        runWithParameter(project, Result.SUCCESS, "BRANCH", "test");
        runWithParameter(project, Result.SUCCESS, "BRANCH", "master");

        assertThat(getBuildFilterColumn(view).querySelector("img").getAttributes().getNamedItem("class").getTextContent(), containsString("60to79"));

        runWithParameter(project, Result.SUCCESS, "BRANCH", "test");
        runWithParameter(project, Result.SUCCESS, "BRANCH", "master");

        assertThat(getBuildFilterColumn(view).querySelector("img").getAttributes().getNamedItem("class").getTextContent(), containsString("80plus"));
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
        return viewPage.querySelectorAll(".bigtable > tbody > tr > td").get(0); // TODO broken
    }
}
