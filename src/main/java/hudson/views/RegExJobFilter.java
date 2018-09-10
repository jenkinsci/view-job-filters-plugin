package hudson.views;

import hudson.Extension;
import hudson.model.SCMedItem;
import hudson.model.TopLevelItem;
import hudson.model.AbstractItem;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.scm.SCM;
import hudson.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.kohsuke.stapler.DataBoundConstructor;
import org.springframework.util.ClassUtils;

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
		NAME, DESCRIPTION, SCM, EMAIL, MAVEN, SCHEDULE, NODE
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

    /*
     * TODO this pattern works fine, but it may be better to provide this as a list of helpers.
     */
    @SuppressWarnings("rawtypes")
	public List<String> getMatchValues(TopLevelItem item) {
    	List<String> values = new ArrayList<String>();
    	if (valueType == ValueType.DESCRIPTION) {
    		if (item instanceof AbstractItem) {
    			String desc = ((AbstractItem) item).getDescription();
    			addSplitValues(values, desc);
    		}
    	} else if (valueType == ValueType.SCM) {
	    	if (item instanceof SCMedItem) {
	    		SCM scm = ((SCMedItem) item).getScm();
	    		List<String> scmValues = ScmFilterHelper.getValues(scm);
	    		values.addAll(scmValues);
	    	} else {
		    	/*
	    		 * FIX JENKINS-31710
	    		 * To keep backward compatibility with the required core '1.509.3' 
	    		 * (which doesn't contains the interface jenkins.triggers.SCMTriggerItem),
	    		 * we use reflection to get the appropriate method and data.
	    		 * This approach is effective but ugly, the best option is to require an newer version of Jenkins core (> 1.568).
	    		 * TODO: Update the required core version and simple replace this code with:
	    		 * 
	    		 * if (item instanceof jenkins.triggers.SCMTriggerItem) {
	    		 *    for (SCM scm : ((SCMTriggerItem) item).getSCMs()) {
	    		 *        List<String> scmValues = ScmFilterHelper.getValues(scm);
		    			  values.addAll(scmValues);
	    		 *    }
	    		 * }
	    		 */
	    		Class[] interfaces = ClassUtils.getAllInterfaces(item);

	    		// check if there are any interfaces
	    		if (interfaces != null && interfaces.length > 0) {
		    		for (Class iface : interfaces) {

		    			// check if the item implements the right interface
		    			if (iface.getCanonicalName().equals("jenkins.triggers.SCMTriggerItem")) {

		    				// get the method which returns the list of SCM items
		    				Method getSCMs = ReflectionUtils.findMethod(item.getClass(), "getSCMs");

		    				// get the return and convert it
		    				@SuppressWarnings("unchecked")
							Collection<? extends SCM> scms = (Collection<? extends SCM>)ReflectionUtils.invokeMethod(getSCMs, item);

		    				// normal approach over all items
		    				if (scms != null && !scms.isEmpty()) {
		    		    		for (SCM scm : scms) {
		    		    			List<String> scmValues = ScmFilterHelper.getValues(scm);
		    			    		values.addAll(scmValues);
		    		    		}
		    	    		}

		    				break;
		    			}
		    		}
		    	}
	    	}
	    	/*
	    	 * END FIX JENKINS-31710
	    	 */
    	} else if (valueType == ValueType.NAME) {
    		values.add(item.getName());
    	} else if (valueType == ValueType.EMAIL) {
    		List<String> emailValues = EmailValuesHelper.getValues(item);
    		values.addAll(emailValues);
    	} else if (valueType == ValueType.MAVEN) {
    		List<String> mavenValues = MavenValuesHelper.getValues(item);
    		values.addAll(mavenValues);
    	} else if (valueType == ValueType.SCHEDULE) {
    		List<String> scheduleValues = TriggerFilterHelper.getValues(item);
    		for (String scheduleValue: scheduleValues) {
    			// we do this split, because the spec may have multiple lines - especially including the comment
    			addSplitValues(values, scheduleValue);
    		}
    	} else if (valueType == ValueType.NODE) {
	    	if (item instanceof AbstractProject) {
	    		String node = ((AbstractProject) item).getAssignedLabelString();
    			values.add(node);
	    	}
    	}
    	return values;
    }
    private void addSplitValues(List<String> values, String value) {
    	if (value != null) {
    		String[] split = value.split("\n");
    		for (String s: split) {
    			// trimming this is necessary to remove odd characters that cause problems
    			// the real example here is the description won't work without this trim
    			values.add(s.trim());
    		}
    	}
    }
    public boolean matches(TopLevelItem item) {
        List<String> matchValues = getMatchValues(item);
        boolean matched = false;
        for (String matchValue: matchValues) {
        	// check null here so matchers don't have to
        	if (matchValue != null &&
        				// this doesn't use "find" because that would be too inclusive, 
        				// and at this point it might break existing people's regexes
        				// - just to clarify this a bit more - if someone configures the regex of "Util.*"
        				//		we cannot assume they want to match (find) a value of "SpecialUtil"
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
        
        /*
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
