package hudson.views;

import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.WithoutJenkins;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WithJenkins
class PluginHelperUtilsTest extends AbstractJenkinsTest {
	/*
     * Test all the helpers to see that no exceptions are thrown.
     */
	@Test
	@WithoutJenkins
	void testHelpers() {
        PluginHelperUtils.validateAndThrow(new CoreEmailValuesProvider());
        PluginHelperUtils.validateAndThrow(new CvsValuesProvider());
        PluginHelperUtils.validateAndThrow(new EmailExtValuesProvider());
        PluginHelperUtils.validateAndThrow(new GitValuesProvider());
        PluginHelperUtils.validateAndThrow(new MavenProjectValuesHelper());
        PluginHelperUtils.validateAndThrow(new SvnValuesProvider());
    }

	@Test
	@WithoutJenkins
	void testNull() {
        assertThat(PluginHelperUtils.validateAndThrow(null), is(nullValue()));
    }

	@Test
	@WithoutJenkins
	void testThrow() {
		assertThrows(RuntimeException.class, () ->
			PluginHelperUtils.validateAndThrow(() -> {
                throw new RuntimeException("Test");
            }));
	}
}
