package hudson.views;

import com.cloudbees.hudson.plugins.folder.Folder;
import hudson.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import hudson.views.test.JobType;
import jenkins.model.Jenkins;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.WithoutJenkins;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import static hudson.views.test.JobMocker.EmailType.DEFAULT;
import static hudson.views.test.JobMocker.EmailType.EXTENDED;
import static hudson.views.test.JobMocker.freeStyleProject;
import static hudson.views.test.ViewJobFilters.NameOptions.*;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.*;
import static hudson.views.RegExJobFilter.ValueType.*;
import static hudson.views.test.JobMocker.jobOfType;
import static hudson.views.test.JobType.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static hudson.views.test.ViewJobFilters.*;

@WithJenkins
class RegExJobFilterTest extends AbstractJenkinsTest {

	@Test
	@WithoutJenkins
	void testName() {
		assertFalse(nameRegex(".*", MATCH_NAME).matches(jobOfType(TOP_LEVEL_ITEM).asItem()));
		assertFalse(nameRegex(".*", MATCH_FULL_NAME).matches(jobOfType(TOP_LEVEL_ITEM).asItem()));
		assertFalse(nameRegex(".*", MATCH_DISPLAY_NAME).matches(jobOfType(TOP_LEVEL_ITEM).asItem()));
		assertFalse(nameRegex(".*", MATCH_FULL_DISPLAY_NAME).matches(jobOfType(TOP_LEVEL_ITEM).asItem()));

		for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET)) {
			assertFalse(nameRegex(".*", MATCH_NAME).matches(jobOfType(type).name(null).asItem()));
			assertTrue(nameRegex(".*", MATCH_NAME).matches(jobOfType(type).name("").asItem()));
			assertTrue(nameRegex("Foo", MATCH_NAME).matches(jobOfType(type).name("Foo").asItem()));
			assertFalse(nameRegex("Foo", MATCH_NAME).matches(jobOfType(type).name("Foobar").asItem()));
			assertTrue(nameRegex("Foo.*", MATCH_NAME).matches(jobOfType(type).name("Foobar").asItem()));
			assertFalse(nameRegex("bar", MATCH_NAME).matches(jobOfType(type).name("Foobar").asItem()));
			assertTrue(nameRegex(".*bar", MATCH_NAME).matches(jobOfType(type).name("Foobar").asItem()));
			assertTrue(nameRegex(".ooba.", MATCH_NAME).matches(jobOfType(type).name("Foobar").asItem()));

			assertFalse(nameRegex(".*", MATCH_FULL_NAME).matches(jobOfType(type).fullName(null).asItem()));
			assertTrue(nameRegex(".*", MATCH_FULL_NAME).matches(jobOfType(type).fullName("").asItem()));
			assertTrue(nameRegex("folder/Foo", MATCH_FULL_NAME).matches(jobOfType(type).fullName("folder/Foo").asItem()));
			assertFalse(nameRegex("folder/Foo", MATCH_FULL_NAME).matches(jobOfType(type).fullName("folder/Foobar").asItem()));
			assertTrue(nameRegex("folder/Foo.*", MATCH_FULL_NAME).matches(jobOfType(type).fullName("folder/Foobar").asItem()));
			assertFalse(nameRegex("folder/bar", MATCH_FULL_NAME).matches(jobOfType(type).fullName("folder/Foobar").asItem()));
			assertTrue(nameRegex("folder/.*bar", MATCH_FULL_NAME).matches(jobOfType(type).fullName("folder/Foobar").asItem()));
			assertTrue(nameRegex("folder/.ooba.", MATCH_FULL_NAME).matches(jobOfType(type).fullName("folder/Foobar").asItem()));
			assertTrue(nameRegex(".*der/Foobar", MATCH_FULL_NAME).matches(jobOfType(type).fullName("folder/Foobar").asItem()));

			assertFalse(nameRegex(".*", MATCH_DISPLAY_NAME).matches(jobOfType(type).displayName(null).asItem()));
			assertTrue(nameRegex(".*", MATCH_DISPLAY_NAME).matches(jobOfType(type).displayName("").asItem()));
			assertTrue(nameRegex("Foo", MATCH_DISPLAY_NAME).matches(jobOfType(type).displayName("Foo").asItem()));
			assertFalse(nameRegex("Foo", MATCH_DISPLAY_NAME).matches(jobOfType(type).displayName("Foobar").asItem()));
			assertTrue(nameRegex("Foo.*", MATCH_DISPLAY_NAME).matches(jobOfType(type).displayName("Foobar").asItem()));
			assertFalse(nameRegex("bar", MATCH_DISPLAY_NAME).matches(jobOfType(type).displayName("Foobar").asItem()));
			assertTrue(nameRegex(".*bar", MATCH_DISPLAY_NAME).matches(jobOfType(type).displayName("Foobar").asItem()));
			assertTrue(nameRegex(".ooba.", MATCH_DISPLAY_NAME).matches(jobOfType(type).displayName("Foobar").asItem()));

			assertFalse(nameRegex(".*", MATCH_FULL_DISPLAY_NAME).matches(jobOfType(type).fullDisplayName(null).asItem()));
			assertTrue(nameRegex(".*", MATCH_FULL_DISPLAY_NAME).matches(jobOfType(type).fullDisplayName("").asItem()));
			assertTrue(nameRegex("folder » Foo", MATCH_FULL_DISPLAY_NAME).matches(jobOfType(type).fullDisplayName("folder » Foo").asItem()));
			assertFalse(nameRegex("folder » Foo", MATCH_FULL_DISPLAY_NAME).matches(jobOfType(type).fullDisplayName("folder » Foobar").asItem()));
			assertTrue(nameRegex("folder » Foo.*", MATCH_FULL_DISPLAY_NAME).matches(jobOfType(type).fullDisplayName("folder » Foobar").asItem()));
			assertFalse(nameRegex("folder » bar", MATCH_FULL_DISPLAY_NAME).matches(jobOfType(type).fullDisplayName("folder » Foobar").asItem()));
			assertTrue(nameRegex("folder » .*bar", MATCH_FULL_DISPLAY_NAME).matches(jobOfType(type).fullDisplayName("folder » Foobar").asItem()));
			assertTrue(nameRegex("folder » .ooba.", MATCH_FULL_DISPLAY_NAME).matches(jobOfType(type).fullDisplayName("folder » Foobar").asItem()));
			assertTrue(nameRegex(".*der » Foobar", MATCH_FULL_DISPLAY_NAME).matches(jobOfType(type).fullDisplayName("folder » Foobar").asItem()));
		}
	}

	@Test
	void testFolderName() {
		Jenkins jenkins = j.getInstance();

		assertFalse(folderNameRegex(".*", MATCH_NAME).matches(jobOfType(TOP_LEVEL_ITEM).asItem()));
		assertFalse(folderNameRegex(".*", MATCH_FULL_NAME).matches(jobOfType(TOP_LEVEL_ITEM).asItem()));
		assertFalse(folderNameRegex(".*", MATCH_DISPLAY_NAME).matches(jobOfType(TOP_LEVEL_ITEM).asItem()));
		assertFalse(folderNameRegex(".*", MATCH_FULL_DISPLAY_NAME).matches(jobOfType(TOP_LEVEL_ITEM).asItem()));

		for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET)) {
			assertFalse(folderNameRegex(".*", MATCH_NAME).matches(jobOfType(type).parent(new Folder(jenkins, null)).asItem()));
			assertTrue(folderNameRegex(".*", MATCH_NAME).matches(jobOfType(type).parent(new Folder(jenkins, "")).asItem()));
			assertTrue(folderNameRegex("Foo", MATCH_NAME).matches(jobOfType(type).parent(new Folder(jenkins, "Foo")).asItem()));
			assertFalse(folderNameRegex("Foo", MATCH_NAME).matches(jobOfType(type).parent(new Folder(jenkins, "Foobar")).asItem()));
			assertTrue(folderNameRegex("Foo.*", MATCH_NAME).matches(jobOfType(type).parent(new Folder(jenkins, "Foobar")).asItem()));
			assertFalse(folderNameRegex("bar", MATCH_NAME).matches(jobOfType(type).parent(new Folder(jenkins, "Foobar")).asItem()));
			assertTrue(folderNameRegex(".*bar", MATCH_NAME).matches(jobOfType(type).parent(new Folder(jenkins, "Foobar")).asItem()));
			assertTrue(folderNameRegex(".ooba.", MATCH_NAME).matches(jobOfType(type).parent(new Folder(jenkins, "Foobar")).asItem()));

			assertFalse(folderNameRegex(".*", MATCH_FULL_NAME).matches(jobOfType(type).parent(new Folder(jenkins, null)).asItem()));
			assertTrue(folderNameRegex(".*", MATCH_FULL_NAME).matches(jobOfType(type).parent(new Folder(jenkins, "")).asItem()));
			assertTrue(folderNameRegex("Foo", MATCH_FULL_NAME).matches(jobOfType(type).parent(new Folder(jenkins, "Foo")).asItem()));
			assertFalse(folderNameRegex("Foo", MATCH_FULL_NAME).matches(jobOfType(type).parent(new Folder(jenkins, "Foobar")).asItem()));
			assertTrue(folderNameRegex("Foo.*", MATCH_FULL_NAME).matches(jobOfType(type).parent(new Folder(jenkins, "Foobar")).asItem()));
			assertFalse(folderNameRegex("bar", MATCH_FULL_NAME).matches(jobOfType(type).parent(new Folder(jenkins, "Foobar")).asItem()));
			assertTrue(folderNameRegex(".*bar", MATCH_FULL_NAME).matches(jobOfType(type).parent(new Folder(jenkins, "Foobar")).asItem()));
			assertTrue(folderNameRegex(".ooba.", MATCH_FULL_NAME).matches(jobOfType(type).parent(new Folder(jenkins, "Foobar")).asItem()));

			assertTrue(folderNameRegex("folder/Foo", MATCH_FULL_NAME).matches(jobOfType(type).parent(new Folder(new Folder(jenkins, "folder"), "Foo")).asItem()));
			assertFalse(folderNameRegex("folder/Foo", MATCH_FULL_NAME).matches(jobOfType(type).parent(new Folder(new Folder(jenkins, "folder"), "Foobar")).asItem()));
			assertTrue(folderNameRegex("folder/Foo.*", MATCH_FULL_NAME).matches(jobOfType(type).parent(new Folder(new Folder(jenkins, "folder"), "Foobar")).asItem()));
			assertFalse(folderNameRegex("folder/bar", MATCH_FULL_NAME).matches(jobOfType(type).parent(new Folder(new Folder(jenkins, "folder"), "Foobar")).asItem()));
			assertTrue(folderNameRegex("folder/.*bar", MATCH_FULL_NAME).matches(jobOfType(type).parent(new Folder(new Folder(jenkins, "folder"), "Foobar")).asItem()));
			assertTrue(folderNameRegex("folder/.ooba.", MATCH_FULL_NAME).matches(jobOfType(type).parent(new Folder(new Folder(jenkins, "folder"), "Foobar")).asItem()));
			assertTrue(folderNameRegex(".*der/Foobar", MATCH_FULL_NAME).matches(jobOfType(type).parent(new Folder(new Folder(jenkins, "folder"), "Foobar")).asItem()));

			assertFalse(folderNameRegex(".*", MATCH_DISPLAY_NAME).matches(jobOfType(type).parent(new Folder(jenkins, null)).asItem()));
			assertTrue(folderNameRegex(".*", MATCH_DISPLAY_NAME).matches(jobOfType(type).parent(new Folder(jenkins, "")).asItem()));
			assertTrue(folderNameRegex("Foo", MATCH_DISPLAY_NAME).matches(jobOfType(type).parent(new Folder(jenkins, "Foo")).asItem()));
			assertFalse(folderNameRegex("Foo", MATCH_DISPLAY_NAME).matches(jobOfType(type).parent(new Folder(jenkins, "Foobar")).asItem()));
			assertTrue(folderNameRegex("Foo.*", MATCH_DISPLAY_NAME).matches(jobOfType(type).parent(new Folder(jenkins, "Foobar")).asItem()));
			assertFalse(folderNameRegex("bar", MATCH_DISPLAY_NAME).matches(jobOfType(type).parent(new Folder(jenkins, "Foobar")).asItem()));
			assertTrue(folderNameRegex(".*bar", MATCH_DISPLAY_NAME).matches(jobOfType(type).parent(new Folder(jenkins, "Foobar")).asItem()));
			assertTrue(folderNameRegex(".ooba.", MATCH_DISPLAY_NAME).matches(jobOfType(type).parent(new Folder(jenkins, "Foobar")).asItem()));

			assertFalse(folderNameRegex(".*", MATCH_FULL_DISPLAY_NAME).matches(jobOfType(type).parent(new Folder(jenkins, null)).asItem()));
			assertTrue(folderNameRegex(".*", MATCH_FULL_DISPLAY_NAME).matches(jobOfType(type).parent(new Folder(jenkins, "")).asItem()));
			assertTrue(folderNameRegex("Foo", MATCH_FULL_DISPLAY_NAME).matches(jobOfType(type).parent(new Folder(jenkins, "Foo")).asItem()));
			assertFalse(folderNameRegex("Foo", MATCH_FULL_DISPLAY_NAME).matches(jobOfType(type).parent(new Folder(jenkins, "Foobar")).asItem()));
			assertTrue(folderNameRegex("Foo.*", MATCH_FULL_DISPLAY_NAME).matches(jobOfType(type).parent(new Folder(jenkins, "Foobar")).asItem()));
			assertFalse(folderNameRegex("bar", MATCH_FULL_DISPLAY_NAME).matches(jobOfType(type).parent(new Folder(jenkins, "Foobar")).asItem()));
			assertTrue(folderNameRegex(".*bar", MATCH_FULL_DISPLAY_NAME).matches(jobOfType(type).parent(new Folder(jenkins, "Foobar")).asItem()));
			assertTrue(folderNameRegex(".ooba.", MATCH_FULL_DISPLAY_NAME).matches(jobOfType(type).parent(new Folder(jenkins, "Foobar")).asItem()));

			assertTrue(folderNameRegex("folder » Foo", MATCH_FULL_DISPLAY_NAME).matches(jobOfType(type).parent(new Folder(new Folder(jenkins, "folder"), "Foo")).asItem()));
			assertFalse(folderNameRegex("folder » Foo", MATCH_FULL_DISPLAY_NAME).matches(jobOfType(type).parent(new Folder(new Folder(jenkins, "folder"), "Foobar")).asItem()));
			assertTrue(folderNameRegex("folder » Foo.*", MATCH_FULL_DISPLAY_NAME).matches(jobOfType(type).parent(new Folder(new Folder(jenkins, "folder"), "Foobar")).asItem()));
			assertFalse(folderNameRegex("folder » bar", MATCH_FULL_DISPLAY_NAME).matches(jobOfType(type).parent(new Folder(new Folder(jenkins, "folder"), "Foobar")).asItem()));
			assertTrue(folderNameRegex("folder » .*bar", MATCH_FULL_DISPLAY_NAME).matches(jobOfType(type).parent(new Folder(new Folder(jenkins, "folder"), "Foobar")).asItem()));
			assertTrue(folderNameRegex("folder » .ooba.", MATCH_FULL_DISPLAY_NAME).matches(jobOfType(type).parent(new Folder(new Folder(jenkins, "folder"), "Foobar")).asItem()));
			assertTrue(folderNameRegex(".*der » Foobar", MATCH_FULL_DISPLAY_NAME).matches(jobOfType(type).parent(new Folder(new Folder(jenkins, "folder"), "Foobar")).asItem()));
		}
	}

	@Test
	@WithoutJenkins
	void testDescription() {
		assertFalse(descRegex(".*").matches(jobOfType(TOP_LEVEL_ITEM).asItem()));

		for (JobType<? extends Job> type : availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET)) {
			assertFalse(descRegex(".*").matches(jobOfType(type).desc(null).asItem()));
			assertTrue(descRegex(".*").matches(jobOfType(type).desc("").asItem()));
			assertTrue(descRegex("Foo").matches(jobOfType(type).desc("Foo").asItem()));
			assertFalse(descRegex("Foo").matches(jobOfType(type).desc("Foobar").asItem()));
			assertTrue(descRegex("Foo.*").matches(jobOfType(type).desc("Foobar").asItem()));
			assertFalse(descRegex("bar").matches(jobOfType(type).desc("Foobar").asItem()));
			assertTrue(descRegex(".*bar").matches(jobOfType(type).desc("Foobar").asItem()));
			assertTrue(descRegex(".ooba.").matches(jobOfType(type).desc("Foobar").asItem()));

			assertTrue(descRegex(".*").matches(jobOfType(type).desc("\n").asItem()));
			assertTrue(descRegex("Foo").matches(jobOfType(type).desc("Quux\nFoo").asItem()));
			assertFalse(descRegex("Foo").matches(jobOfType(type).desc("Quux\nFoobar").asItem()));
			assertTrue(descRegex("Foo.*").matches(jobOfType(type).desc("Quux\nFoobar").asItem()));
			assertFalse(descRegex("bar").matches(jobOfType(type).desc("Quux\nFoobar").asItem()));
			assertTrue(descRegex(".*bar").matches(jobOfType(type).desc("Quux\nFoobar").asItem()));
			assertTrue(descRegex(".ooba.").matches(jobOfType(type).desc("Quux\nFoobar").asItem()));

			assertFalse(descRegex(".*desc=test.*").matches(jobOfType(type).desc("").asItem()));
			assertFalse(descRegex(".*desc=test.*").matches(jobOfType(type).desc(null).asItem()));
			assertFalse(descRegex(".*desc=test.*").matches(jobOfType(type).desc("nothing").asItem()));
			assertTrue(descRegex(".*desc=test.*").matches(jobOfType(type).desc("desc=test").asItem()));
			assertTrue(descRegex(".*desc=test.*").matches(jobOfType(type).desc("mydesc=test2").asItem()));
			assertTrue(descRegex(".*desc=test.*").matches(jobOfType(type).desc("thisis\nmydesc=testn2\nforyou").asItem()));
			assertTrue(descRegex(".*desc=test.*").matches(jobOfType(type).desc("1&#xd;\ndesc=test&#xd;\n2").asItem()));
			assertTrue(descRegex(".*desc=test.*").matches(jobOfType(type).desc("1 desc=test 2").asItem()));
		}
	}


	@Test
	void testSCM() {
		assertFalse(scmRegex(".*").matches(jobOfType(TOP_LEVEL_ITEM).asItem()));

		for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET, SCMED_ITEM, SCM_TRIGGER_ITEM)) {
			assertFalse(scmRegex(".*my-office.*").matches(jobOfType(type).cvs("root", "modules", "branch").asItem()));
			assertFalse(scmRegex(".*my-office.*").matches(jobOfType(type).cvs(null, "modules", "branch").asItem()));
			assertFalse(scmRegex(".*my-office.*").matches(jobOfType(type).cvs("root", "modules", null).asItem()));
			assertTrue(scmRegex(".*my-office.*").matches(jobOfType(type).cvs("root/my-office", "modules", "branch").asItem()));
			assertTrue(scmRegex(".*my-office.*").matches(jobOfType(type).cvs("root", "modules/my-office", "branch").asItem()));
			assertTrue(scmRegex(".*my-office.*").matches(jobOfType(type).cvs("root", "modules", "branch/my-office").asItem()));

			assertFalse(scmRegex(".*").matches(jobOfType(type).svn().asItem()));
			assertTrue(scmRegex(".*").matches(jobOfType(type).svn("").asItem()));
			assertTrue(scmRegex("Foo").matches(jobOfType(type).svn("Foo").asItem()));
			assertTrue(scmRegex("Foo.*").matches(jobOfType(type).svn("Foobar").asItem()));
			assertFalse(scmRegex("bar").matches(jobOfType(type).svn("Foobar").asItem()));
			assertTrue(scmRegex(".*bar").matches(jobOfType(type).svn("Foobar").asItem()));
			assertTrue(scmRegex("Bar").matches(jobOfType(type).svn("Foo", "Bar").asItem()));
			assertTrue(scmRegex("B.*").matches(jobOfType(type).svn("Foo", "Bar").asItem()));

			assertFalse(scmRegex(".*").matches(jobOfType(type).gitBranches().asItem()));
			assertTrue(scmRegex(".*").matches(jobOfType(type).gitBranches("").asItem()));
			assertTrue(scmRegex("Foo").matches(jobOfType(type).gitBranches("Foo").asItem()));
			assertTrue(scmRegex("Foo.*").matches(jobOfType(type).gitBranches("Foobar").asItem()));
			assertFalse(scmRegex("bar").matches(jobOfType(type).gitBranches("Foobar").asItem()));
			assertTrue(scmRegex(".*bar").matches(jobOfType(type).gitBranches("Foobar").asItem()));
			assertTrue(scmRegex("Bar").matches(jobOfType(type).gitBranches("Foo", "Bar").asItem()));
			assertTrue(scmRegex("B.*").matches(jobOfType(type).gitBranches("Foo", "Bar").asItem()));

			assertFalse(scmRegex(".*").matches(jobOfType(type).gitRepos().asItem()));
			assertTrue(scmRegex(".*").matches(jobOfType(type).gitRepos("").asItem()));
			assertTrue(scmRegex("Foo").matches(jobOfType(type).gitRepos("Foo").asItem()));
			assertTrue(scmRegex("Foo.*").matches(jobOfType(type).gitRepos("Foobar").asItem()));
			assertFalse(scmRegex("bar").matches(jobOfType(type).gitRepos("Foobar").asItem()));
			assertTrue(scmRegex(".*bar").matches(jobOfType(type).gitRepos("Foobar").asItem()));
			assertTrue(scmRegex("Bar").matches(jobOfType(type).gitRepos("Foo", "Bar").asItem()));
			assertTrue(scmRegex("B.*").matches(jobOfType(type).gitRepos("Foo", "Bar").asItem()));
		}
	}

	@Test
	void testEmail() {
		assertFalse(emailRegex(".*").matches(jobOfType(TOP_LEVEL_ITEM).asItem()));

		for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET)) {
			assertFalse(emailRegex(".*").matches(jobOfType(type).email(null, DEFAULT).asItem()));
			assertTrue(emailRegex(".*").matches(jobOfType(type).email("", DEFAULT).asItem()));
			assertTrue(emailRegex(".*").matches(jobOfType(type).email("foo@bar.com, quux@baz.net", DEFAULT).asItem()));
			assertTrue(emailRegex("foo@bar.com").matches(jobOfType(type).email("foo@bar.com", DEFAULT).asItem()));
			assertFalse(emailRegex("foo").matches(jobOfType(type).email("foo@bar.com", DEFAULT).asItem()));
			assertFalse(emailRegex("@bar.com").matches(jobOfType(type).email("foo@bar.com", DEFAULT).asItem()));
			assertTrue(emailRegex("foo@.*").matches(jobOfType(type).email("foo@bar.com", DEFAULT).asItem()));
			assertTrue(emailRegex(".*@bar.com").matches(jobOfType(type).email("foo@bar.com", DEFAULT).asItem()));

			assertFalse(emailRegex(".*").matches(jobOfType(type).email(null, EXTENDED).asItem()));
			assertTrue(emailRegex(".*").matches(jobOfType(type).email("", EXTENDED).asItem()));
			assertTrue(emailRegex(".*").matches(jobOfType(type).email("foo@bar.com, quux@baz.net", EXTENDED).asItem()));
			assertTrue(emailRegex("foo@bar.com").matches(jobOfType(type).email("foo@bar.com", EXTENDED).asItem()));
			assertFalse(emailRegex("foo").matches(jobOfType(type).email("foo@bar.com", EXTENDED).asItem()));
			assertFalse(emailRegex("@bar.com").matches(jobOfType(type).email("foo@bar.com", EXTENDED).asItem()));
			assertTrue(emailRegex("foo@.*").matches(jobOfType(type).email("foo@bar.com", EXTENDED).asItem()));
			assertTrue(emailRegex(".*@bar.com").matches(jobOfType(type).email("foo@bar.com", EXTENDED).asItem()));
		}
	}

	@Test
	@WithoutJenkins
	void testSchedule() {
		assertFalse(scheduleRegex(".*").matches(jobOfType(TOP_LEVEL_ITEM).asItem()));

		for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET, WORKFLOW_JOB)) {
			assertFalse(scheduleRegex(".*").matches(jobOfType(type).trigger(null).asItem()));
			assertTrue(scheduleRegex(".*").matches(jobOfType(type).trigger("").asItem()));
			assertTrue(scheduleRegex(".*").matches(jobOfType(type).trigger("\n").asItem()));
			assertTrue(scheduleRegex(".*monday.*").matches(jobOfType(type).trigger("# monday").asItem()));
			assertFalse(scheduleRegex(".*monday.*").matches(jobOfType(type).trigger("# tuesday").asItem()));
			assertFalse(scheduleRegex(".*monday.*").matches(jobOfType(type).trigger("* * * * *").asItem()));
			assertFalse(scheduleRegex(".*monday.*").matches(jobOfType(type).trigger("* * * * *\n#").asItem()));
			assertTrue(scheduleRegex(".*monday.*").matches(jobOfType(type).trigger("#monday\n* * * * *").asItem()));
		}
	}

	@Test
	void testMaven() {
		assertFalse(mavenRegex(".*").matches(jobOfType(TOP_LEVEL_ITEM).asItem()));

		for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET)) {
			assertTrue(mavenRegex(".*").matches(jobOfType(type).mavenBuilder("", "", "", "").asItem()));
			assertTrue(mavenRegex("Foo").matches(jobOfType(type).mavenBuilder("Foo", "", "", "").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOfType(type).mavenBuilder("Foo", "", "", "").asItem()));
			assertTrue(mavenRegex("Foo.*").matches(jobOfType(type).mavenBuilder("Foobar", "", "", "").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOfType(type).mavenBuilder("Foobar", "", "", "").asItem()));
			assertTrue(mavenRegex(".*bar").matches(jobOfType(type).mavenBuilder("Foobar", "", "", "").asItem()));

			assertTrue(mavenRegex("Foo bar").matches(jobOfType(type).mavenBuilder("Foo\nbar", "", "", "").asItem()));
			assertFalse(mavenRegex("Foo").matches(jobOfType(type).mavenBuilder("Foo\nbar", "", "", "").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOfType(type).mavenBuilder("Foo\nbar", "", "", "").asItem()));
			assertTrue(mavenRegex("Foo.*").matches(jobOfType(type).mavenBuilder("Foo\nbar", "", "", "").asItem()));
			assertTrue(mavenRegex(".*bar").matches(jobOfType(type).mavenBuilder("Foo\nbar", "", "", "").asItem()));

			assertTrue(mavenRegex(".*").matches(jobOfType(type).mavenBuilder("", "", "", "").asItem()));
			assertTrue(mavenRegex("Foo").matches(jobOfType(type).mavenBuilder("", "Foo", "", "").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOfType(type).mavenBuilder("", "Foo", "", "").asItem()));
			assertTrue(mavenRegex("Foo.*").matches(jobOfType(type).mavenBuilder("", "Foobar", "", "").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOfType(type).mavenBuilder("", "Foobar", "", "").asItem()));
			assertTrue(mavenRegex(".*bar").matches(jobOfType(type).mavenBuilder("", "Foobar", "", "").asItem()));

			assertTrue(mavenRegex(".*").matches(jobOfType(type).mavenBuilder("", "", "", "").asItem()));
			assertTrue(mavenRegex("Foo").matches(jobOfType(type).mavenBuilder("", "", "Foo", "").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOfType(type).mavenBuilder("", "", "Foo", "").asItem()));
			assertTrue(mavenRegex("Foo.*").matches(jobOfType(type).mavenBuilder("", "", "Foobar", "").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOfType(type).mavenBuilder("", "", "Foobar", "").asItem()));
			assertTrue(mavenRegex(".*bar").matches(jobOfType(type).mavenBuilder("", "", "Foobar", "").asItem()));

			assertTrue(mavenRegex(".*").matches(jobOfType(type).mavenBuilder("", "", "", "").asItem()));
			assertTrue(mavenRegex("Foo").matches(jobOfType(type).mavenBuilder("", "", "", "Foo").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOfType(type).mavenBuilder("", "", "", "Foo").asItem()));
			assertTrue(mavenRegex("Foo.*").matches(jobOfType(type).mavenBuilder("", "", "", "Foobar").asItem()));
			assertFalse(mavenRegex("bar").matches(jobOfType(type).mavenBuilder("", "", "", "Foobar").asItem()));
			assertTrue(mavenRegex(".*bar").matches(jobOfType(type).mavenBuilder("", "", "", "Foobar").asItem()));
		}
	}

	@Test
	@WithoutJenkins
	void testNode() {
		assertFalse(nodeRegex(".*").matches(jobOfType(TOP_LEVEL_ITEM).asItem()));

		for (JobType<? extends Job> type: availableJobTypes(FREE_STYLE_PROJECT, MATRIX_PROJECT, MAVEN_MODULE_SET)) {
			assertTrue(nodeRegex(".*").matches(jobOfType(type).assignedLabel("").asItem()));
			assertTrue(nodeRegex("Foo").matches(jobOfType(type).assignedLabel("Foo").asItem()));
			assertFalse(nodeRegex("bar").matches(jobOfType(type).assignedLabel("Foo").asItem()));
			assertTrue(nodeRegex("Foo.*").matches(jobOfType(type).assignedLabel("Foobar").asItem()));
			assertFalse(nodeRegex("bar").matches(jobOfType(type).assignedLabel("Foobar").asItem()));
			assertTrue(nodeRegex(".*bar").matches(jobOfType(type).assignedLabel("Foobar").asItem()));
		}
	}

	@Test
	@WithoutJenkins
	void testBackwardsCompatibleDeserialization() throws IOException {
		InputStream xml = RegExJobFilter.class.getResourceAsStream("/RegExJobFilterTest/view.xml");
		ListView listView = (ListView) View.createViewFromXML("foo", xml);

		RegExJobFilter filter = (RegExJobFilter) listView.getJobFilters().iterator().next();
		assertThat(filter.getIncludeExcludeTypeString(), is(includeMatched.name()));
		assertThat(filter.getValueTypeString(), is(NAME.name()));
		assertThat(filter.getRegex(), is(".*"));
		assertThat(filter.isMatchName(), is(true));
		assertThat(filter.isMatchFullName(), is(false));
		assertThat(filter.isMatchDisplayName(), is(false));
		assertThat(filter.isMatchFullDisplayName(), is(false));
	}

	@Test
	void testConfigRoundtrip() throws Exception {
		testConfigRoundtrip(
				"regex-view-1",
				new RegExJobFilter("NaMeRegEx", excludeMatched.name(), NAME.name(),
					false, true, false, true)
		);

		testConfigRoundtrip(
				"regex-view-2",
				new RegExJobFilter("DeScriptionRegEx", excludeMatched.name(), DESCRIPTION.name(),
					true, true, false, false),
				new RegExJobFilter("EmailRegEx", includeUnmatched.name(), EMAIL.name(),
					false, false, true, true)
		);

		testConfigRoundtrip(
				"regex-view-3",
				new RegExJobFilter("MavenRegEx", excludeUnmatched.name(), MAVEN.name(),
					false, false, false, true),
				new RegExJobFilter("NodeRegEx", excludeMatched.name(), NODE.name(),
					false, false, true, false),
				new RegExJobFilter("ScmRegEx", excludeMatched.name(), SCM.name(),
					false, true, false, false)
		);

		testConfigRoundtrip(
				"regex-view-4",
				new RegExJobFilter("FullNameRegEx", includeMatched.name(), NAME.name(),
					true, false, false, true),
				new RegExJobFilter("FolderNameRegEx", excludeUnmatched.name(), FOLDER_NAME.name(),
					true, false, true, true)
		);
	}

	/*
	 * Tests that the example given in the wiki works as described.
	 *
	 * https://wiki.jenkins.io/display/JENKINS/Using+the+View+Job+Filters+Match+Type
	 */
	@Test
	void testHelpExample() {
		List<TopLevelItem> all = asList(
				freeStyleProject().name("0-Test_Job").asItem(),
				freeStyleProject().name("1-Test_Job").trigger("@midnight").asItem(),
				freeStyleProject().name("2-Job").asItem(),
				freeStyleProject().name("3-Test_Job").trigger("@daily").asItem(),
				freeStyleProject().name("4-Job").trigger("@midnight").asItem(),
				freeStyleProject().name("5-Test_Job").trigger("@midnight").asItem(),
				freeStyleProject().name("6-Test_Job").asItem()
		);

		List<TopLevelItem> filtered = new ArrayList<>();

		RegExJobFilter includeTests = new RegExJobFilter(".*Test.*", includeMatched.name(), NAME.name());
		filtered = includeTests.filter(filtered, all, null);
		assertThat(filtered, is(asList(
				all.get(0),
				all.get(1),
				all.get(3),
				all.get(5),
				all.get(6)
		)));

		RegExJobFilter excludeNonNightly = new RegExJobFilter(".*@midnight.*", excludeUnmatched.name(), SCHEDULE.name());
		filtered = excludeNonNightly.filter(filtered, all, null);
		assertThat(filtered, is(asList(
				all.get(1),
				all.get(5)
		)));
	}

	private void testConfigRoundtrip(String viewName, RegExJobFilter... filters) throws Exception {
		List<RegExJobFilter> expectedFilters = new ArrayList<>();
		for (RegExJobFilter filter: filters) {
			expectedFilters.add(new RegExJobFilter(filter.getRegex(),
				filter.getIncludeExcludeTypeString(),
				filter.getValueTypeString(),
				filter.isMatchName(),
				filter.isMatchFullName(),
				filter.isMatchDisplayName(),
				filter.isMatchFullDisplayName()));
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

	private static void assertFilterEquals(List<RegExJobFilter> expectedFilters, List<ViewJobFilter> actualFilters) {
		assertThat(actualFilters.size(), is(expectedFilters.size()));
		for (int i = 0; i < actualFilters.size(); i++) {
			ViewJobFilter actualFilter = actualFilters.get(i);
			RegExJobFilter expectedFilter = expectedFilters.get(i);
			assertThat(actualFilter, instanceOf(RegExJobFilter.class));
			assertThat(((RegExJobFilter)actualFilter).getRegex(), is(expectedFilter.getRegex()));
			assertThat(((RegExJobFilter)actualFilter).getIncludeExcludeTypeString(), is(expectedFilter.getIncludeExcludeTypeString()));
			assertThat(((RegExJobFilter)actualFilter).getValueTypeString(), is(expectedFilter.getValueTypeString()));
			assertThat(((RegExJobFilter)actualFilter).isMatchName(), is(expectedFilter.isMatchName()));
			assertThat(((RegExJobFilter)actualFilter).isMatchFullName(), is(expectedFilter.isMatchFullName()));
			assertThat(((RegExJobFilter)actualFilter).isMatchDisplayName(), is(expectedFilter.isMatchDisplayName()));
			assertThat(((RegExJobFilter)actualFilter).isMatchFullDisplayName(), is(expectedFilter.isMatchFullDisplayName()));
		}
	}
}
