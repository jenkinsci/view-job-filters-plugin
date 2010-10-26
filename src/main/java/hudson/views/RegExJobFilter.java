package hudson.views;

import hudson.Extension;
import hudson.model.AbstractItem;
import hudson.model.Descriptor;
import hudson.model.SCMedItem;
import hudson.model.TopLevelItem;
import hudson.scm.SCM;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Simple JobFilter that filters jobs based on a regular expression, and
 * making use of negate and exclude flags.
 * 
 * TODO limitation - cannot perform validations in hetero list?
 * 
 * @author Jacob Robertson
 */
public class RegExJobFilter extends AbstractIncludeExcludeJobFilter {
	
	static enum ValueType {
		NAME, DESCRIPTION, SCM, EMAIL, MAVEN
	}
	
	transient private ValueType valueType;
	private String valueTypeString;
	private String regex;
	transient private Pattern pattern;
	
    @DataBoundConstructor
    public RegExJobFilter(String regex, String includeExcludeTypeString, String valueTypeString) {
    	super(includeExcludeTypeString);
    	this.regex = regex;
    	this.pattern = Pattern.compile(regex);
    	this.valueTypeString = valueTypeString;
    	this.valueType = ValueType.valueOf(valueTypeString);
    }
    
    Object readResolve() {
        if (regex != null) {
        	pattern = Pattern.compile(regex);
        }
        if (valueTypeString != null) {
        	valueType = ValueType.valueOf(valueTypeString);
        }
        return super.readResolve();
    }

    /**
     * TODO this pattern works fine, but it may be better to provide this as a list of helpers.
     */
    public List<String> getMatchValues(TopLevelItem item) {
    	List<String> values = new ArrayList<String>();
    	if (valueType == ValueType.DESCRIPTION) {
    		if (item instanceof AbstractItem) {
    			values.add(((AbstractItem) item).getDescription());
    		}
    	} else if (valueType == ValueType.SCM) {
	    	if (item instanceof SCMedItem) {
	    		SCM scm = ((SCMedItem) item).getScm();
	    		List<String> scmValues = ScmFilterHelper.getValues(scm);
	    		values.addAll(scmValues);
	    	}
    	} else if (valueType == ValueType.NAME) {
    		values.add(item.getName());
    	} else if (valueType == ValueType.EMAIL) {
    		List<String> emailValues = EmailValuesHelper.getValues(item);
    		values.addAll(emailValues);
    	} else if (valueType == ValueType.MAVEN) {
    		List<String> mavenValues = MavenValuesHelper.getValues(item);
    		values.addAll(mavenValues);
    	}
    	return values;
    }
    
    public boolean matches(TopLevelItem item) {
        List<String> matchValues = getMatchValues(item);
        boolean matched = false;
        for (String matchValue: matchValues) {
        	// check null here so matchers don't have to
        	if (matchValue != null &&
        				// this doesn't use "find" because that would be too inclusive, 
        				// and at this point it might break existing people's regexes
	        			pattern.matcher(matchValue).matches()) {
        		matched = true;
        		break;
        	}
        }
        return matched;
    }

	public String getRegex() {
		return regex;
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
         * 
         * Does not work in hetero-list?
         *
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
        */
    }

}
