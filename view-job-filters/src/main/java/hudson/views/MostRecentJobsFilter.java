package hudson.views;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.TopLevelItem;
import hudson.model.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import org.kohsuke.stapler.DataBoundConstructor;

public class MostRecentJobsFilter extends ViewJobFilter {
	
	private int maxToInclude;
	private boolean checkStartTime;
	
	private static final transient Logger log = Logger.getLogger("MostRecentJobsFilter");

	@DataBoundConstructor
	public MostRecentJobsFilter(int maxToInclude, boolean checkStartTime) {
		this.maxToInclude = maxToInclude;
		this.checkStartTime = checkStartTime;
	}

    @Override
    public List<TopLevelItem> filter(List<TopLevelItem> added, List<TopLevelItem> all, View filteringView) {
    	List<TopLevelItem> filtered = new ArrayList<TopLevelItem>(added);
    	
    	Collections.sort(filtered, new MostRecentJobsComparator());
    	
    	// subList was causing null-pointer, not sure why.
//    	filtered = filtered.subList(0, maxToInclude);
    	
    	int max = maxToInclude;
    	if (max > filtered.size()) {
    		max = filtered.size();
    	}
    	List<TopLevelItem> subList = new ArrayList<TopLevelItem>();
    	for (int i = 0; i < max; i++) {
    		subList.add(filtered.get(i));
		}
    	
        return subList;
    }
    
    @SuppressWarnings("rawtypes")
    private class MostRecentJobsComparator implements Comparator<TopLevelItem> {
    	public int compare(TopLevelItem i1, TopLevelItem i2) {
    		if (!(i1 instanceof Job)) {
    			return -1;
    		}
    		if (!(i2 instanceof Job)) {
    			return 1;
    		}
			Job j1 = (Job) i1;
			Job j2 = (Job) i2;
			
			Long t1 = getTime(j1);
			Long t2 = getTime(j2);
			
    		return t2.compareTo(t1);
    	}
    }
    @SuppressWarnings("rawtypes")
    private long getTime(Job job) {
    	Run run = job.getLastBuild();
    	while (run != null) {
    		if (checkStartTime) {
    			return run.getTimeInMillis();
    		} else if (!run.isBuilding()) {
    			return run.getTimeInMillis() + run.getDuration();
    		} else {
    			run = run.getPreviousBuild();
    		}
    	}
    	return Long.MIN_VALUE;
    }
	
	@Extension
	public static class DescriptorImpl extends Descriptor<ViewJobFilter> {
		@Override
		public String getDisplayName() {
			return "Most Recent Jobs Filter";
		}
		@Override
        public String getHelpFile() {
            return "/plugin/view-job-filters/most-recent-help.html";
        }
	}

	public boolean isCheckStartTime() {
		return checkStartTime;
	}
	
	public int getMaxToInclude() {
		return maxToInclude;
	}

}
