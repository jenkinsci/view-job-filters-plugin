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

	/**
	 * Subclasses should not have to override this method.
	 */
    @Override
    public List<TopLevelItem> filter(List<TopLevelItem> added, List<TopLevelItem> all, View filteringView) {
    	List<TopLevelItem> filtered = new ArrayList<TopLevelItem>(added);
    	doFilter(filtered, all, filteringView);
    	
    	List<TopLevelItem> sorted = new ArrayList<TopLevelItem>(all);
    	sorted.retainAll(filtered);
        return sorted;
    }
    /**
     * Subclasses needing more control over how the lists are filtered should override this method.
     */
    protected void doFilter(List<TopLevelItem> filtered, List<TopLevelItem> all, View filteringView) {
        for (TopLevelItem item: all) {
        	boolean matched = matches(item);
        	filterItem(filtered, item, matched);
        }
    }
    protected final void filterItem(List<TopLevelItem> filtered, TopLevelItem item, boolean matched) {
		if (exclude(matched)) {
			filtered.remove(item);
		}
		if (include(matched) && !filtered.contains(item)) {
			filtered.add(item);
		}
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
    protected boolean matches(TopLevelItem item) {
    	return false;
    }

}
