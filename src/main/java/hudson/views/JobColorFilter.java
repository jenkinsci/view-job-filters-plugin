package hudson.views;


import hudson.Extension;

import hudson.model.*;
import org.kohsuke.stapler.DataBoundConstructor;

public class JobColorFilter extends AbstractIncludeExcludeJobFilter {

    private BallColor ballColor;

    @DataBoundConstructor
    public JobColorFilter(String ballColorTypeString,
                          String includeExcludeTypeString) {
        super(includeExcludeTypeString);
        this.ballColor = BallColor.valueOf(ballColorTypeString.toUpperCase());
    }

    @SuppressWarnings("rawtypes")
    protected boolean matches(TopLevelItem item) {

        if (item instanceof Job) {
            Job job = (Job) item;
            Run last = job.getLastCompletedBuild();
            if (last != null) {
                BallColor jobBallColor = last.getIconColor();
                if (ballColor == jobBallColor) {
                    return true;
                }
            }
        }
        return false;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<ViewJobFilter> {
        @Override
        public String getDisplayName() {
            return "Job Color Filter";
        }

        @Override
        public String getHelpFile() {
            return "/plugin/view-color-filters/include-exclude-help.html";
        }
    }
}
