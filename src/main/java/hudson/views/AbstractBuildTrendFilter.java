package hudson.views;

import hudson.model.Job;
import hudson.model.Run;
import hudson.model.TopLevelItem;

import org.kohsuke.stapler.DataBoundConstructor;

public abstract class AbstractBuildTrendFilter 
	extends AbstractIncludeExcludeJobFilter implements RunMatcher {

    private static final long ONE_SECOND_MS = 1000;
    private static final long ONE_MINUTE_MS = 60 * ONE_SECOND_MS;
    static final long ONE_HOUR_MS = 60 * ONE_MINUTE_MS;
    static final long ONE_DAY_MS = 24 * ONE_HOUR_MS;
	
	public static enum AmountType {
		
		Days(ONE_DAY_MS), Hours(ONE_HOUR_MS), Builds(-1);
		
		private long divideByAmount;
		AmountType(long divideByAmount) {
			this.divideByAmount = divideByAmount;
		}
		public float convertMillisToAmount(float millis) {
			return millis / divideByAmount;
		}
		
	}
	
	public static enum BuildCountType {
		Latest, All, AtLeastOne
	}
	
	transient private BuildCountType buildCountType;
	private String buildCountTypeString;
	
	transient private AmountType amountType;
	private String amountTypeString;
	
	private float amount;
	
	@DataBoundConstructor
	public AbstractBuildTrendFilter(
			String buildCountTypeString, 
			float amount, String amountTypeString,
			String includeExcludeTypeString) {
		super(includeExcludeTypeString);
		this.buildCountTypeString = buildCountTypeString;
		this.buildCountType = BuildCountType.valueOf(buildCountTypeString);
		this.amount = amount;
		this.amountTypeString = amountTypeString;
		this.amountType = AmountType.valueOf(amountTypeString);
	}
    Object readResolve() {
        if (amountTypeString != null) {
        	amountType = AmountType.valueOf(amountTypeString);
        }
        if (buildCountTypeString != null) {
        	buildCountType = BuildCountType.valueOf(buildCountTypeString);
        }
        return super.readResolve();
    }
	
	@SuppressWarnings("unchecked")
	protected boolean matches(TopLevelItem item) {
		if (item instanceof Job) {
			Job job = (Job) item;
			
			// iterate over runs and check conditions
			Run run = job.getLastBuild();
			boolean oneMatched = false;
			int count = 0;
			
			while (run != null) {
				// check the different types of durations to see if we've checked back far enough
				if (amount > 0 && amountType != AmountType.Builds) {
                        // get the amount of time since it last built
                        long now = System.currentTimeMillis();
                        long then = run.getTimeInMillis();
                        float diff = now - then;
                        diff = amountType.convertMillisToAmount(diff);
                        if (diff > amount) {
                            break;
                        }
				}
				// now evaluate the build status
				boolean runMatches = matchesRun(run);
				if (runMatches) {
					if (buildCountType == BuildCountType.AtLeastOne || buildCountType == BuildCountType.Latest) {
						return true;
					}
				} else {
					if (buildCountType == BuildCountType.All) {
						return false;
					}
				}
				
				// if we got this far, and the type is for "latest" then it's not going to match
				if (buildCountType == BuildCountType.Latest) {
					return false;
				}
				
				oneMatched = true;
                if (amount > 0 && amountType == AmountType.Builds) {
                    if (++count >= amount) {
                        break;
                    }
                }
				run = run.getPreviousBuild();
			}
			// if we're talking about "all builds" and there was at least one build, then
			// it means no builds didn't match the filter
			if (buildCountType == BuildCountType.All && oneMatched) {
				return true;
			}
			
			// means we ran out of builds to check and did not find a match
			return false;
		} else {
			// wasn't of type "job"
			return false;
		}
	}
	
	public String getAmountTypeString() {
		return amountTypeString;
	}
	public float getAmount() {
		return amount;
	}
	public String getBuildCountTypeString() {
		return buildCountTypeString;
	}
	
}
