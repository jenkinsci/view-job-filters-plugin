package hudson.views;

import hudson.Extension;
import hudson.model.*;
import hudson.scm.SCM;

import java.util.ArrayList;
import java.util.Collections;
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
	
	enum ValueType {
		NAME {
			@Override
			List<String> doGetMatchValues(TopLevelItem item) {
				return Collections.singletonList(item.getName());
			}
		},
		DESCRIPTION(AbstractItem.class) {
			@Override
			List<String> doGetMatchValues(TopLevelItem item) {
				String desc = ((AbstractItem) item).getDescription();
				List<String> result = new ArrayList<String>();
				addSplitValues(result, desc);
				return result;
			}
		},
		SCM(SCMedItem.class) {
			@Override
			List<String> doGetMatchValues(TopLevelItem item) {
				SCM scm = ((SCMedItem) item).getScm();
				return ScmFilterHelper.getValues(scm);
			}
		},
		EMAIL {
			@Override
			List<String> doGetMatchValues(TopLevelItem item) {
				return EmailValuesHelper.getValues(item);
			}
		},
		MAVEN {
			@Override
			List<String> doGetMatchValues(TopLevelItem item) {
				return MavenValuesHelper.getValues(item);
			}
		},
		SCHEDULE {
			@Override
			List<String> doGetMatchValues(TopLevelItem item) {
				List<String> result = new ArrayList<String>();
				for (String scheduleValue: TriggerFilterHelper.getValues(item)) {
					// we do this split, because the spec may have multiple lines - especially including the comment
					addSplitValues(result, scheduleValue);
				}
				return result;
			}
		},
		NODE(AbstractProject.class) {
			@Override
			List<String> doGetMatchValues(TopLevelItem item) {
				String node = ((AbstractProject) item).getAssignedLabelString();
				return Collections.singletonList(node);
			}
		},
		DISPLAY_NAME {
			@Override
			List<String> doGetMatchValues(TopLevelItem item) {
				return Collections.singletonList(item.getDisplayName());
			}
		},
		FULL_NAME {
			@Override
			List<String> doGetMatchValues(TopLevelItem item) {
				return Collections.singletonList(item.getFullName());
			}
		},
		FOLDER_NAME {
			@Override
			List<String> doGetMatchValues(TopLevelItem item) {
				if (item.getParent() == null)
					return Collections.emptyList();
				return Collections.singletonList(item.getParent().getFullName());
			}
		};

		/**
		 * An instance that applies only to specific types
		 * @param itemTypeFilter The Item type required for this filter to match at all.
		 */
		ValueType(Class<? extends Item> itemTypeFilter) {
			this.itemTypeFilter = itemTypeFilter;
		}

		/**
		 * An instance with no type filter.
		 */
		ValueType() {
			this(Item.class);
		}

		final Class<? extends Item> itemTypeFilter;

		private static void addSplitValues(List<String> values, String value) {
			if (value != null) {
				String[] split = value.split("\n");
				for (String s : split) {
					// trimming this is necessary to remove odd characters that cause problems
					// the real example here is the description won't work without this trim
					values.add(s.trim());
				}
			}
		}

		/**
		 * Extracts the values to match against for a given Item. The item is guaranteed to match the itemTypeFilter
		 * of the enum, but still needs to be casted.
		 * @param item The item to match against
		 * @return A list containing all values to match against. Never null.
		 */
		abstract List<String> doGetMatchValues(TopLevelItem item);

		/**
		 * Returns a list of the match values for this Enum value. If the filter type of the enum
		 * is not matched, an empty list is returned.
		 * @param item The item to match against.
		 * @return A list containing all values to match against. Never null.
		 */
		public List<String> getMatchValues(TopLevelItem item) {
			if (!itemTypeFilter.isInstance(item))
				return Collections.emptyList();
			return doGetMatchValues(item);
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

	public List<String> getMatchValues(TopLevelItem item) {
		return valueType.doGetMatchValues(item);
    }

    public boolean matches(TopLevelItem item) {
        List<String> matchValues = getMatchValues(item);
        for (String matchValue: matchValues) {
        	// check null here so matchers don't have to
        	if (matchValue != null &&
        				// this doesn't use "find" because that would be too inclusive, 
        				// and at this point it might break existing people's regexes
        				// - just to clarify this a bit more - if someone configures the regex of "Util.*"
        				//		we cannot assume they want to match (find) a value of "SpecialUtil"
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
