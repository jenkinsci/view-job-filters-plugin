package hudson.views;

import org.junit.Test;

public class PluginHelperUtilsTest extends AbstractHudsonTest {
    /*
     * Test all the helpers to see that no exceptions are thrown.
     */
    @Test
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
}
