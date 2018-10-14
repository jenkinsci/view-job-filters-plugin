package hudson.views;

import org.junit.Test;
import org.jvnet.hudson.test.WithoutJenkins;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class PluginHelperUtilsTest extends AbstractHudsonTest {
    /*
     * Test all the helpers to see that no exceptions are thrown.
     */
    @Test
    @WithoutJenkins
    public void testHelpers() {
        PluginHelperUtils.validateAndThrow(new CoreEmailValuesProvider());
        PluginHelperUtils.validateAndThrow(new CvsValuesProvider());
        PluginHelperUtils.validateAndThrow(new EmailExtValuesProvider());
        PluginHelperUtils.validateAndThrow(new GitLegacyValuesProvider());
        PluginHelperUtils.validateAndThrow(new GitValuesProvider());
        PluginHelperUtils.validateAndThrow(new MavenExtraStepsValuesHelper());
        PluginHelperUtils.validateAndThrow(new MavenProjectValuesHelper());
        PluginHelperUtils.validateAndThrow(new SvnValuesProvider());
    }

    @Test
    @WithoutJenkins
    public void testNull() {
        assertThat(PluginHelperUtils.validateAndThrow(null), is(nullValue()));
    }

    @Test(expected = RuntimeException.class)
    @WithoutJenkins
    public void testThrow() {
        PluginHelperUtils.validateAndThrow(new PluginHelperUtils.PluginHelperTestable() {
            @Override
            public Class getPluginTesterClass() {
                throw new RuntimeException("Test");
            }
        });
    }
}
