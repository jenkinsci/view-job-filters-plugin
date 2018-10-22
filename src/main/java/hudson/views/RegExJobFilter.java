package hudson.views;

import hudson.Extension;
import hudson.Util;
import hudson.model.*;
import hudson.scm.SCM;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import hudson.util.FormValidation;
import jenkins.triggers.SCMTriggerItem;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;

/**
 * Simple JobFilter that filters jobs based on a regular expression, and
 * making use of negate and exclude flags.
 *
 * TODO limitation - cannot perform validations in hetero list?
 *
 * @author Jacob Robertson
 */
public class RegExJobFilter extends AbstractIncludeExcludeJobFilter {

	public enum ValueType {
		NAME {
			@Override
			void doGetMatchValues(TopLevelItem item, List<String> values) {
				values.addAll(Collections.singletonList(item.getName()));
			}
		},
		DESCRIPTION {
			@Override
			void doGetMatchValues(TopLevelItem item, List<String> values) {
			   	if (item instanceof AbstractItem) {
					addSplitValues(values, ((AbstractItem) item).getDescription());
				}
			}
		},
		SCM {
			@Override
			void doGetMatchValues(TopLevelItem item, List<String> values) {
				if (item instanceof AbstractProject) {
					SCM scm = ((AbstractProject)item).getScm();
					values.addAll(ScmFilterHelper.getValues(scm));
				}
				if (item instanceof SCMTriggerItem) {
					for (SCM scm: ((SCMTriggerItem)item).getSCMs()) {
						values.addAll(ScmFilterHelper.getValues(scm));
					}
				}
				if (item instanceof SCMedItem) {
					SCM scm = ((SCMedItem) item).getScm();
					values.addAll(ScmFilterHelper.getValues(scm));
				}
            }
		},
		EMAIL {
			@Override
			void doGetMatchValues(TopLevelItem item, List<String> values) {
				values.addAll(EmailValuesHelper.getValues(item));
			}
		},
		MAVEN {
			@Override
			void doGetMatchValues(TopLevelItem item, List<String> values) {
				values.addAll(MavenValuesHelper.getValues(item));
			}
		},
		SCHEDULE {
			@Override
			void doGetMatchValues(TopLevelItem item, List<String> values) {
				for (String scheduleValue: TriggerFilterHelper.getValues(item)) {
					// we do this split, because the spec may have multiple lines - especially including the comment
					addSplitValues(values, scheduleValue);
				}
			}
		},
		NODE {
			@Override
			void doGetMatchValues(TopLevelItem item, List<String> values) {
			    if (item instanceof AbstractProject) {
					String node = ((AbstractProject) item).getAssignedLabelString();
					values.add(node);
				}
			}
		},
		DISPLAY_NAME {
			@Override
			void doGetMatchValues(TopLevelItem item, List<String> values) {
				values.add(item.getDisplayName());
			}
		},
		FULL_NAME {
			@Override
			void doGetMatchValues(TopLevelItem item, List<String> values) {
				values.add(item.getFullName());
			}
		},
		FOLDER_NAME {
			@Override
			void doGetMatchValues(TopLevelItem item, List<String> values) {
				if (item.getParent() != null) {
					values.add(item.getParent().getFullName());
				}
			}
		};

		private static void addSplitValues(List<String> values, String value) {
			if (value != null) {
				String[] split = value.split("\n", -1);
				for (String s : split) {
					// trimming this is necessary to remove odd characters that cause problems
					// the real example here is the description won't work without this trim
					values.add(s.trim());
				}
			}
		}

		/**
		 * Extracts the values to match against for a given Item.
		 * @param item The item to match against
		 * @param values The list of values to add to
		 */
		abstract void doGetMatchValues(TopLevelItem item, List<String> values);

		/**
		 * Returns a list of the match values for this Enum value.
		 * @param item The item to match against.
		 * @return A list containing all values to match against. Never null.
		 */
		public List<String> getMatchValues(TopLevelItem item) {
		    List<String> values = new ArrayList<String>();
			doGetMatchValues(item, values);
			return values;
		}
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

    public boolean matches(TopLevelItem item) {
        List<String> matchValues = valueType.getMatchValues(item);
        for (String matchValue: matchValues) {
        	// check null here so matchers don't have to
        	if (matchValue != null &&
        				// this doesn't use "find" because that would be too inclusive,
        				// and at this point it might break existing people's regexes
        				// - just to clarify this a bit more - if someone configures the regex of "Util.*"
        				// we cannot assume they want to match (find) a value of "SpecialUtil"
	        			pattern.matcher(matchValue).matches()) {
        		return true;
        	}
        }
        return false;
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
            return hudson.views.filters.Messages.RegExJobFilter_DisplayName();
        }
        @Override
        public String getHelpFile() {
            return "/plugin/view-job-filters/regex-help.html";
        }
        
        public FormValidation doCheckRegex(@QueryParameter String value ) throws IOException, ServletException, InterruptedException  {
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
