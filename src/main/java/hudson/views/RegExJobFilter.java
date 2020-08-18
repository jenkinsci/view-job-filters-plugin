package hudson.views;

import hudson.Extension;
import hudson.Util;
import hudson.model.*;
import hudson.scm.SCM;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
 * @author Jacob Robertson
 */
public class RegExJobFilter extends AbstractIncludeExcludeJobFilter {

	public enum ValueType {
		NAME {
			@Override
			void doGetMatchValues(TopLevelItem item, Options options, List<String> values) {
			    if (options.matchName) {
					values.add(item.getName());
				}
				if (options.matchFullName) {
					values.add(item.getFullName());
				}
				if (options.matchDisplayName) {
					values.add(item.getDisplayName());
				}
				if (options.matchFullDisplayName) {
					values.add(item.getFullDisplayName());
				}
			}
		},
		DESCRIPTION {
			@Override
			void doGetMatchValues(TopLevelItem item, Options options, List<String> values) {
			   	if (item instanceof AbstractItem) {
					addSplitValues(values, ((AbstractItem) item).getDescription());
				}
			}
		},
		SCM {
			@Override
			void doGetMatchValues(TopLevelItem item, Options options, List<String> values) {
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
			void doGetMatchValues(TopLevelItem item, Options options, List<String> values) {
				values.addAll(EmailValuesHelper.getValues(item));
			}
		},
		MAVEN {
			@Override
			void doGetMatchValues(TopLevelItem item, Options options, List<String> values) {
				values.addAll(MavenValuesHelper.getValues(item));
			}
		},
		SCHEDULE {
			@Override
			void doGetMatchValues(TopLevelItem item, Options options, List<String> values) {
				for (String scheduleValue: TriggerFilterHelper.getValues(item)) {
					// we do this split, because the spec may have multiple lines - especially including the comment
					addSplitValues(values, scheduleValue);
				}
			}
		},
		NODE {
			@Override
			void doGetMatchValues(TopLevelItem item, Options options, List<String> values) {
			    if (item instanceof AbstractProject) {
					String node = ((AbstractProject) item).getAssignedLabelString();
					values.add(node);
				}
			}
		},
		FOLDER_NAME {
			@Override
			void doGetMatchValues(TopLevelItem item, Options options, List<String> values) {
				if (item.getParent() != null) {
					if (options.matchName && item.getParent() instanceof Item) {
						values.add(((Item)item.getParent()).getName());
					}
					if (options.matchFullName) {
						values.add(item.getParent().getFullName());
					}
					if (options.matchDisplayName) {
						values.add(item.getParent().getDisplayName());
					}
					if (options.matchFullDisplayName) {
						values.add(item.getParent().getFullDisplayName());
					}
				}
			}
		},
		BUILD_NAME {
			@Override
			void doGetMatchValues(TopLevelItem item, Options options, List<String> values) {
				if (item.getAllJobs() != null) {
					ArrayList<Job> jobs = new ArrayList<>(item.getAllJobs());
					for (Job job : jobs) {
						for (Iterator iterator = job.getBuilds().listIterator(); iterator.hasNext(); ) {
							Run run = (Run) iterator.next();
							if (options.matchName || options.matchDisplayName) {
								values.add(run.getDisplayName());
							}
							if (options.matchFullName || options.matchFullDisplayName) {
								values.add(run.getFullDisplayName());
							}
						}
					}
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

		abstract void doGetMatchValues(TopLevelItem item, Options options, List<String> values);

		public List<String> getMatchValues(TopLevelItem item, Options options) {
		    List<String> values = new ArrayList<String>();
			doGetMatchValues(item, options, values);
			return values;
		}
	}

	public static class Options {
		public final boolean matchName;
		public final boolean matchFullName;
		public final boolean matchDisplayName;
		public final boolean matchFullDisplayName;

	    public Options(boolean matchName, boolean matchFullName, boolean matchDisplayName, boolean matchFullDisplayName) {
			this.matchName = matchName;
			this.matchFullName = matchFullName;
			this.matchDisplayName = matchDisplayName;
			this.matchFullDisplayName = matchFullDisplayName;
		}
	}

	transient private ValueType valueType;
	private String valueTypeString;
	private String regex;
	transient private Pattern pattern;
	private boolean matchName;
	private boolean matchFullName;
	private boolean matchDisplayName;
	private boolean matchFullDisplayName;

	public RegExJobFilter(String regex, String includeExcludeTypeString, String valueTypeString) {
	    this(regex, includeExcludeTypeString, valueTypeString, true, false, false, false);
	}

    @DataBoundConstructor
    public RegExJobFilter(String regex, String includeExcludeTypeString, String valueTypeString,
			boolean matchName, boolean matchFullName, boolean matchDisplayName, boolean matchFullDisplayName) {
    	super(includeExcludeTypeString);
    	this.regex = regex;
    	this.pattern = Pattern.compile(regex);
    	this.valueTypeString = valueTypeString;
    	this.valueType = ValueType.valueOf(valueTypeString);
    	this.matchName = matchName;
    	this.matchFullName = matchFullName;
    	this.matchDisplayName = matchDisplayName;
    	this.matchFullDisplayName = matchFullDisplayName;
    	initOptions();
    }

    Object readResolve() {
        if (regex != null) {
        	pattern = Pattern.compile(regex);
        }
        if (valueTypeString != null) {
        	valueType = ValueType.valueOf(valueTypeString);
        }
        initOptions();
        return super.readResolve();
    }

    private void initOptions() {
		if (!this.matchName && !this.matchFullName && !this.matchDisplayName && !this.matchFullDisplayName) {
			this.matchName = true;
		}
	}

    public boolean matches(TopLevelItem item) {
        List<String> matchValues = valueType.getMatchValues(item, getOptions());
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
	public boolean isMatchName() {
		return matchName;
	}
	public boolean isMatchFullName() {
		return matchFullName;
	}
	public boolean isMatchDisplayName() {
		return matchDisplayName;
	}
	public boolean isMatchFullDisplayName() {
		return matchFullDisplayName;
	}

	public Options getOptions() {
		return new Options(matchName, matchFullName, matchDisplayName, matchFullDisplayName);
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
