package hudson.views;

import hudson.model.TopLevelItem;
import hudson.model.View;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Jacob Robertson
 */
public abstract class AbstractIncludeExcludeJobFilter extends ViewJobFilter {
	
	public static enum IncludeExcludeType {
		includeMatched, includeUnmatched, excludeMatched, excludeUnmatched
	}
	transient private IncludeExcludeType includeExcludeType;
	private String includeExcludeTypeString;
	
	public AbstractIncludeExcludeJobFilter(String includeExcludeTypeString) {
		this.includeExcludeTypeString = includeExcludeTypeString;
		this.includeExcludeType = IncludeExcludeType.valueOf(includeExcludeTypeString);
	}
    Object readResolve() {
        if (includeExcludeTypeString != null) {
        	includeExcludeType = IncludeExcludeType.valueOf(includeExcludeTypeString);
        }
        return this;
    }

	public boolean isIncludeMatched() {
		return includeExcludeType == IncludeExcludeType.includeMatched;
	}

	public boolean isIncludeUnmatched() {
		return includeExcludeType == IncludeExcludeType.includeUnmatched;
	}

	public boolean isExcludeMatched() {
		return includeExcludeType == IncludeExcludeType.excludeMatched;
	}

	public boolean isExcludeUnmatched() {
		return includeExcludeType == IncludeExcludeType.excludeUnmatched;
	}
	public String getIncludeExcludeTypeString() {
		return includeExcludeTypeString;
	}

    @Override
    public List<TopLevelItem> filter(List<TopLevelItem> added, List<TopLevelItem> all, View filteringView) {
    	List<TopLevelItem> filtered = new ArrayList<TopLevelItem>(added);
        for (TopLevelItem item: all) {
        	boolean matched = matches(item);
    		if (exclude(matched)) {
    			filtered.remove(item);
    		}
    		if (include(matched) && !filtered.contains(item)) {
    			filtered.add(item);
    		}
        }
        List<TopLevelItem> sorted = sortByAll(filtered, all);
        return sorted;
    }
    
    public static List<TopLevelItem> sortByAll(List<TopLevelItem> filtered, List<TopLevelItem> all) {
    	List<TopLevelItem> sorted = new ArrayList<TopLevelItem>(all);
    	sorted.retainAll(filtered);
    	return sorted;
    }

    public boolean include(boolean matched) {
    	if (isIncludeMatched() && matched) {
    		return true;
    	} else if (isIncludeUnmatched() && !matched) {
    		return true;
    	} else {
    		return false;
    	}
    }

    public boolean exclude(boolean matched) {
    	if (isExcludeMatched() && matched) {
    		return true;
    	} else if (isExcludeUnmatched() && !matched) {
    		return true;
    	} else {
    		return false;
    	}
    }
    abstract boolean matches(TopLevelItem item);

}
