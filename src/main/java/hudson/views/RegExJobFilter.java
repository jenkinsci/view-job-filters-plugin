package hudson.views;

import hudson.Extension;
import hudson.Util;
import hudson.model.AbstractItem;
import hudson.model.Descriptor;
import hudson.model.SCMedItem;
import hudson.model.TopLevelItem;
import hudson.scm.SCM;
import hudson.util.FormValidation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.servlet.ServletException;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

/**
 * Simple JobFilter that filters jobs based on a regular expression, and
 * making use of negate and exclude flags.
 * @author Jacob Robertson
 */
public class RegExJobFilter extends ViewJobFilter {
	
	static enum ValueType {
		NAME, DESCRIPTION, SCM
	}
	
	transient private ValueType valueType;
	private String valueTypeString;
	private String regex;
	private boolean negated;
	private boolean exclude;
	transient private Pattern pattern;
	
    @DataBoundConstructor
    public RegExJobFilter(String regex, boolean negated, boolean exclude, String valueTypeString) {
    	this.regex = regex;
    	this.negated = negated;
    	this.exclude = exclude;
    	this.pattern = Pattern.compile(regex);
    	this.valueTypeString = valueTypeString;
    	this.valueType = ValueType.valueOf(valueTypeString);
    }
    
    private Object readResolve() {
        if (regex != null) {
        	pattern = Pattern.compile(regex);
        }
        if (valueTypeString != null) {
        	valueType = ValueType.valueOf(valueTypeString);
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
    
    public List<String> getMatchValues(TopLevelItem item) {
    	List<String> values = new ArrayList<String>();
    	if (valueType == ValueType.DESCRIPTION) {
    		if (item instanceof AbstractItem) {
    			values.add(((AbstractItem) item).getDescription());
    		}
    	} else if (valueType == ValueType.SCM) {
	    	if (item instanceof SCMedItem) {
	    		SCM scm = ((SCMedItem) item).getScm();
	    		List<String> scmvalues = ScmFilterHelper.getValues(scm);
	    		values.addAll(scmvalues);
	    	}
    	} else { // if (valueType == ValueType.NAME) {
    		values.add(item.getName());
    	}
    	return values;
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
        List<String> matchValues = getMatchValues(item);
        for (String matchValue: matchValues) {
	        if (matchValue != null &&
	        			pattern.matcher(matchValue).matches() != negated) {
	            return true;
	        }
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
	public String getValueTypeString() {
		return valueTypeString;
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
