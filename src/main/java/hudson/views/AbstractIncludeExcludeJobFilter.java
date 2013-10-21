package hudson.views;

import hudson.model.Item;
import hudson.model.ItemGroup;
import hudson.model.TopLevelItem;
import hudson.model.View;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
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

        if (isRecurse(filteringView))
            all = expand(all, new ArrayList<TopLevelItem>());

    	List<TopLevelItem> filtered = new ArrayList<TopLevelItem>(added);
    	doFilter(filtered, all, filteringView);
    	
    	List<TopLevelItem> sorted = new ArrayList<TopLevelItem>(all);
    	sorted.retainAll(filtered);
        return sorted;
    }

    protected boolean isRecurse(View v) {
        try {
            Method m = v.getClass().getMethod("isRecurse", null);
            if (m != null)
                return (Boolean) m.invoke(v, null);
        } catch (Exception e) {
            // Not a 1.515+ ListView
        }
        return false;
    }

    private List<TopLevelItem> expand(Collection<TopLevelItem> items, List<TopLevelItem> allItems) {
        for (Item item : items) {
            if (item instanceof ItemGroup) {
                ItemGroup<TopLevelItem> ig = (ItemGroup<TopLevelItem>) item;
                expand(ig.getItems(), allItems);
            }
            if (item instanceof TopLevelItem) {
                allItems.add((TopLevelItem) item);
            }
        }
        return allItems;
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
