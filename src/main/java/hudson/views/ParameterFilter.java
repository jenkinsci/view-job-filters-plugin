package hudson.views;

import hudson.Extension;
import hudson.model.BooleanParameterValue;
import hudson.model.ChoiceParameterDefinition;
import hudson.model.Descriptor;
import hudson.model.FileParameterValue;
import hudson.model.Job;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;
import hudson.model.ParametersDefinitionProperty;
import hudson.model.StringParameterValue;
import hudson.model.TopLevelItem;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

public class ParameterFilter extends AbstractIncludeExcludeJobFilter {

	private String nameRegex;
	transient private Pattern namePattern;

	private String valueRegex;
	transient private Pattern valuePattern;

	private String descriptionRegex;
	transient private Pattern descriptionPattern;

	@DataBoundConstructor
	public ParameterFilter(String includeExcludeTypeString,
			String nameRegex,
			String valueRegex, 
			String descriptionRegex) {
		super(includeExcludeTypeString);
		this.nameRegex = nameRegex;
		this.valueRegex = valueRegex;
		this.descriptionRegex = descriptionRegex;
		this.namePattern = toPattern(nameRegex);
		this.valuePattern = toPattern(valueRegex);
		this.descriptionPattern = toPattern(descriptionRegex);
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
        return super.readResolve();
    }
    
    private Pattern toPattern(String regex) {
    	if (StringUtils.isEmpty(regex)) {
    		return null;
    	} else {
    		return Pattern.compile(regex);
    	}
    }
	
	@SuppressWarnings("unchecked")
	@Override
	boolean matches(TopLevelItem item) {
		if (item instanceof Job) {
			Job job = (Job) item;
			ParametersDefinitionProperty property = 
				(ParametersDefinitionProperty) job.getProperty(ParametersDefinitionProperty.class);
			
			if (property != null) {
				List<ParameterDefinition> defs = property.getParameterDefinitions();
				for (ParameterDefinition def: defs) {
					if (matches(def)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	public boolean matches(ParameterDefinition definition) {
		if (!matches(namePattern, definition.getName())) {
			return false;
		}
		String sval = getStringValue(definition);
		if (!matches(valuePattern, sval)) {
			return false;
		}
		if (!matches(descriptionPattern, definition.getDescription())) {
			return false;
		}
		return true;
	}
	
	/**
	 * Do our best to get the value out.
	 * There might be a better way to do this.
	 */
	private String getStringValue(ParameterDefinition definition) {
		if (definition instanceof ChoiceParameterDefinition) {
			return ((ChoiceParameterDefinition) definition).getChoicesText();
		} else {
			ParameterValue value = definition.getDefaultParameterValue();
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
			}
		}
		return null;
	}
	
	private boolean matches(Pattern p, String m) {
		if (p == null) {
			return true;
		} else if (m == null) {
			// only happens if the param is of a type we don't know?
			return false;
		} else {
			// using "find" allows us to work over multi-lines
			return p.matcher(m).find();
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
	
}
