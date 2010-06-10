package hudson.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.servlet.ServletException;

import hudson.Extension;
import hudson.Util;
import hudson.model.Descriptor;
import hudson.model.TopLevelItem;
import hudson.util.FormValidation;
import hudson.views.ViewJobFilter;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

/**
 * Simple JobFilter that filters jobs based on a regular expression, and
 * making use of negate and exclude flags.
 * @author Jacob Robertson
 */
public class RegExJobFilter extends ViewJobFilter {
	
	private String regex;
	private boolean negated;
	private boolean exclude;
	transient private Pattern pattern;
	
    @DataBoundConstructor
    public RegExJobFilter(String regex, boolean negated, boolean exclude) {
    	this.regex = regex;
    	this.negated = negated;
    	this.exclude = exclude;
    	this.pattern = Pattern.compile(regex);
    }
    
    private Object readResolve() {
        if (regex != null) {
        	pattern = Pattern.compile(regex);
        }
        return this;
    }

    @Override
    public List<TopLevelItem> filter(List<TopLevelItem> added, List<TopLevelItem> all) {
    	List<TopLevelItem> filtered = new ArrayList<TopLevelItem>(added);
        for (TopLevelItem item: all) {
    		if (exclude(item)) {
    			filtered.remove(item);
    		}
    		if (include(item) && !filtered.contains(item)) {
    			filtered.add(item);
    		}
        }
        return filtered;
    }
    
    public boolean include(TopLevelItem item) {
        return checkItem(item, false);
    }

    public boolean exclude(TopLevelItem item) {
        return checkItem(item, true);
    }
    private boolean checkItem(TopLevelItem item, boolean checkExclude) {
    	if (exclude != checkExclude) {
    		return false;
    	}
        String itemName = item.getName();
        if (pattern.matcher(itemName).matches() != negated) {
            return true;
        }
        return false;
    }

	public String getRegex() {
		return regex;
	}
	public boolean isNegated() {
		return negated;
	}
	public boolean isExclude() {
		return exclude;
	}

    @Extension
    public static class DescriptorImpl extends Descriptor<ViewJobFilter> {
        @Override
        public String getDisplayName() {
            return "Regular Expression Job Filter";
        }
        @Override
        public String getHelpFile() {
            return "/plugin/view-job-filters/regex-help.html";
        }
        
        /**
         * Checks if the regular expression is valid.
         */
        public FormValidation doCheckRegex( @QueryParameter String value ) throws IOException, ServletException, InterruptedException  {
            String v = Util.fixEmpty(value);
            if (v != null) {
                try {
                    Pattern.compile(v);
                } catch (PatternSyntaxException pse) {
                    return FormValidation.error(pse.getMessage());
                }
            }
            return FormValidation.ok();
        }
    }

}
