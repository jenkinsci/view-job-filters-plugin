package hudson.views;

import hudson.Extension;
import hudson.model.BooleanParameterValue;
import hudson.model.ChoiceParameterDefinition;
import hudson.model.Descriptor;
import hudson.model.FileParameterValue;
import hudson.model.Job;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.ParametersDefinitionProperty;
import hudson.model.Run;
import hudson.model.StringParameterValue;
import hudson.model.TopLevelItem;
import hudson.model.View;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.StackTraceElement;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

public class ParameterFilter extends AbstractIncludeExcludeJobFilter {

	private String nameRegex;
	transient private Pattern namePattern;

	private String valueRegex;
	transient private Pattern valuePattern;

	private String descriptionRegex;
	transient private Pattern descriptionPattern;
	
	/**
	 * Default is true to help backwards compatibility when deserializing.
	 */
	private Boolean useDefaultValue = Boolean.TRUE;
	
	private boolean matchBuildsInProgress;
	private boolean matchAllBuilds;
	private int maxBuildsToMatch;

	@DataBoundConstructor
	public ParameterFilter(String includeExcludeTypeString,
			String nameRegex,
			String valueRegex, 
			String descriptionRegex,
			boolean useDefaultValue,
			boolean matchAllBuilds,
			int maxBuildsToMatch,
			boolean matchBuildsInProgress) {
		super(includeExcludeTypeString);
		this.nameRegex = nameRegex;
		this.valueRegex = valueRegex;
		this.descriptionRegex = descriptionRegex;
		this.namePattern = toPattern(nameRegex);
		this.valuePattern = toPattern(valueRegex);
		this.descriptionPattern = toPattern(descriptionRegex);
		this.useDefaultValue = useDefaultValue;
		this.matchAllBuilds = matchAllBuilds;
		this.maxBuildsToMatch = maxBuildsToMatch;
		this.matchBuildsInProgress = matchBuildsInProgress;
	}

    Object readResolve() {
        if (nameRegex != null) {
        	namePattern = toPattern(nameRegex);
        }
        if (valueRegex != null) {
        	valuePattern = toPattern(valueRegex);
        }
        if (descriptionRegex != null) {
        	descriptionPattern = toPattern(descriptionRegex);
        }
        // backwards compatible - only matters for older xml files
        if (useDefaultValue == null) {
        	useDefaultValue = Boolean.TRUE;
        }
        return super.readResolve();
    }
    
    private Pattern toPattern(String regex) {
    	if (StringUtils.isEmpty(regex)) {
    		return null;
    	} else {
    		return Pattern.compile(regex);
    	}
    }
	
	@Override
    protected void doFilter(List<TopLevelItem> filtered, List<TopLevelItem> all, View filteringView) {
		StackTraceElement[] elements = new Throwable().getStackTrace();

		String calleeMethod = elements[0].getMethodName();
		String callerMethodName = elements[1].getMethodName();
		String callerClassName = elements[1].getClassName();

		LOGGER.log(Level.INFO, "CallerClassName {0}", callerClassName);
		LOGGER.log(Level.INFO, "Caller method name: {0}", callerMethodName);
		LOGGER.log(Level.INFO, "Callee method name: {0}", calleeMethod);
		
        for (TopLevelItem item: all) {
        	boolean matched = matches(item);
        	filterItem(filtered, item, matched);
        }
    }
 
	@SuppressWarnings("unchecked")
	@Override
	protected boolean matches(TopLevelItem item) {
		
		StackTraceElement[] elements = new Throwable().getStackTrace();

		String calleeMethod = elements[0].getMethodName();
		String callerMethodName = elements[1].getMethodName();
		String callerClassName = elements[1].getClassName();

		LOGGER.log(Level.INFO, "CallerClassName {0}", callerClassName);
		LOGGER.log(Level.INFO, "Caller method name: {0}", callerMethodName);
		LOGGER.log(Level.INFO, "Callee method name: {0}", calleeMethod);
		
		
		LOGGER.log(Level.INFO, "Item is a(n) {0} ", item.getClass().getName());		
		if (item instanceof Job) {
			Job job = (Job) item;
			LOGGER.log(Level.INFO, "Item is a job called {0} ", job.getName());
			
			if (useDefaultValue) {
				return matchesDefaultValue(job);
			} else {
				return matchesBuildValue(job);
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	protected boolean matchesDefaultValue(Job job) {
		ParametersDefinitionProperty property = 
			(ParametersDefinitionProperty) job.getProperty(ParametersDefinitionProperty.class);
		if (property != null) {
			List<ParameterDefinition> defs = property.getParameterDefinitions();
			for (ParameterDefinition def: defs) {
				boolean multiline = isValueMultiline(def);
				String svalue = getStringValue(def);
				boolean matches = matchesParameter(def.getName(), svalue, multiline, def.getDescription());
				if (matches) {
					return true;
				}
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	protected boolean matchesBuildValue(Job job) {
		boolean matched = false;
		int count = 1;
		Run run = job.getLastBuild();
		while (run != null && !matched) {
			// only match against the builds we care about
			boolean isBuilding = run.isBuilding();
			if (matchBuildsInProgress || !isBuilding) {
				matched = matchesRun(run);
				if (matched) {
					LOGGER.log(Level.INFO, "Matched run {0}", run.getNumber());
				} else {
					LOGGER.log(Level.INFO, "Didn\'t match run {0}", run.getNumber());
				}
				// now that we've checked one build, see if we should stop
				if (!matchAllBuilds || (maxBuildsToMatch > 0 && count >= maxBuildsToMatch)) {
					break;
				}
			}
			run = run.getPreviousBuild();
			count++;
		}
		
		if (matched) {
			LOGGER.log(Level.INFO, "Returning matched job {0}", job.getName());
		}
		return matched;
	}
	@SuppressWarnings("unchecked")
	public boolean matchesRun(Run run) {
		ParametersAction action = run.getAction(ParametersAction.class);
		if (action == null) {
			return false;
		}
		//LOGGER.log(Level.INFO, "action toString: {0}", action.toString());
		//LOGGER.log(Level.INFO, "action getDisplayName: {0}", action.getDisplayName());
		//LOGGER.log(Level.INFO, "action getUrlName: {0}", action.getUrlName());
		
		// look for one parameter value that matches our criteria
		for (ParameterValue value: action.getParameters()) {
			String sval = getStringValue(value);
			if (matchesParameter(value.getName(), sval, false, null)) {
				//LOGGER.log(Level.INFO, "Parameter matched: {0}", sval);
				return true;
			}
		}
		// no parameters matched the criteria
		return false;
	}
	public boolean matchesParameter(String name, String value, boolean isValueMultiline, String description) {
		if (!matches(namePattern, name, false)) {
			return false;
		}
		if (!matches(valuePattern, value, isValueMultiline)) {
			return false;
		}
		// description will be null if we're looking at the build (as opposed to the job)
		if (description != null && !matches(descriptionPattern, description, true)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Do our best to get the value out.
	 * There might be a better way to do this.
	 */
	protected String getStringValue(ParameterDefinition definition) {
		if (definition instanceof ChoiceParameterDefinition) {
			return ((ChoiceParameterDefinition) definition).getChoicesText();
		} else {
			ParameterValue value = definition.getDefaultParameterValue();
			return getStringValue(value);
		}
	}
	private boolean isValueMultiline(ParameterDefinition def) {
		return (def instanceof ChoiceParameterDefinition);
	}
	
	protected String getStringValue(ParameterValue value) {
		if (value instanceof StringParameterValue) {
			return ((StringParameterValue) value).value;
		} else if (value instanceof BooleanParameterValue) {
			boolean bval = ((BooleanParameterValue) value).value;
			return String.valueOf(bval);
		} else if (value instanceof FileParameterValue) {
			// not the full path - just the name
			// this is the only public value available to us
			String file = ((FileParameterValue) value).getOriginalFileName();
			return file;
		} else {
			// means we can match on "null" - not sure that's useful though
			return String.valueOf(value);
		}
	}
	
	private boolean matches(Pattern p, String m, boolean multiline) {
		if (p == null) {
			return true;
		} else if (m == null) {
			// only happens if the param is of a type we don't know?
			return false;
		} else {
			Matcher matcher = p.matcher(m);
			if (multiline) {
				// using "find" allows us to work over multi-lines
				return matcher.find();
			} else {
				return matcher.matches();
			}
		}
	}

	public String getNameRegex() {
		return nameRegex;
	}

	public String getValueRegex() {
		return valueRegex;
	}

	public String getDescriptionRegex() {
		return descriptionRegex;
	}
	
	public boolean isUseDefaultValue() {
		return useDefaultValue;
	}
	
	public boolean isMatchAllBuilds() {
		return matchAllBuilds;
	}
	
	public int getMaxBuildsToMatch() {
		return maxBuildsToMatch;
	}
	
	public boolean isMatchBuildsInProgress() {
		return matchBuildsInProgress;
	}

	@Extension
    public static class DescriptorImpl extends Descriptor<ViewJobFilter> {
        @Override
        public String getDisplayName() {
            return "Parameterized Jobs Filter";
        }
        @Override
        public String getHelpFile() {
            return "/plugin/view-job-filters/parameter-help.html";
        }
	}
	
	private static final Logger LOGGER = Logger.getLogger(ParameterFilter.class.getName());

}
