package hudson.views;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Run;

public class BuildDurationFilter extends AbstractBuildTrendFilter {

	private boolean lessThan;
	private float buildDurationMinutes;
	
	@DataBoundConstructor
	public BuildDurationFilter(
			float buildDurationMinutes, boolean lessThan,
		String buildCountTypeString, float amount, String amountTypeString,
		String includeExcludeTypeString) {
			super(buildCountTypeString, amount, amountTypeString, includeExcludeTypeString);
		this.lessThan = lessThan;
		this.buildDurationMinutes = buildDurationMinutes;
	}
	
	@Override
    @SuppressWarnings("unchecked")
	public boolean matchesRun(Run run) {
		long buildDurationMs = (long) (buildDurationMinutes * 60f * 1000f);
		if (lessThan) {
			return run.getDuration() < buildDurationMs;
		} else {
			return run.getDuration() > buildDurationMs;
		}
	}

	public boolean isLessThan() {
		return lessThan;
	}
	public String getBuildDurationMinutes() {
		DecimalFormat format = new DecimalFormat("##########.##########");
		return format.format(buildDurationMinutes);
	}

	@Extension
	public static class DescriptorImpl extends Descriptor<ViewJobFilter> {
		@Override
		public String getDisplayName() {
			return "Build Duration Filter";
		}
		@Override
        public String getHelpFile() {
            return "/plugin/view-job-filters/build-duration-help.html";
        }
	}

}
