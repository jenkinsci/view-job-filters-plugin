package hudson.views.test;

import hudson.model.Descriptor;
import hudson.model.TopLevelItemDescriptor;
import hudson.scm.SCMDescriptor;
import hudson.views.*;

import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched;

public class ViewJobFilters {

    public static RegExJobFilter nameRegex(String regex) {
        return new RegExJobFilter(
                regex,
                AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched.name(),
                RegExJobFilter.ValueType.NAME.name());
    }

    public static RegExJobFilter descRegex(String regex) {
        return new RegExJobFilter(
                regex,
                AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched.name(),
                RegExJobFilter.ValueType.DESCRIPTION.name());
    }

    public static RegExJobFilter emailRegex(String regex) {
        return new RegExJobFilter(
                regex,
                AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched.name(),
                RegExJobFilter.ValueType.EMAIL.name());
    }

    public static RegExJobFilter scheduleRegex(String regex) {
        return new RegExJobFilter(
                regex,
                AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched.name(),
                RegExJobFilter.ValueType.SCHEDULE.name());
    }

    public static RegExJobFilter scmRegex(String regex) {
        return new RegExJobFilter(
                regex,
                AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched.name(),
                RegExJobFilter.ValueType.SCM.name());
    }

    public static RegExJobFilter mavenRegex(String regex) {
        return new RegExJobFilter(
                regex,
                AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched.name(),
                RegExJobFilter.ValueType.MAVEN.name());
    }

    public static RegExJobFilter nodeRegex(String regex) {
        return new RegExJobFilter(
                regex,
                AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched.name(),
                RegExJobFilter.ValueType.NODE.name());
    }

    public static JobTypeFilter jobType(TopLevelItemDescriptor descriptor) {
        return jobType(descriptor.getId());
    }

    public static JobTypeFilter jobType(String type) {
        return new JobTypeFilter(
                type,
                AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched.name());
    }

    public static ScmTypeFilter scmType(String type) {
        return new ScmTypeFilter(type, AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched.name());
    }

    public static ScmTypeFilter scmType(SCMDescriptor<?> descriptor) {
        return scmType(descriptor.clazz.getName());
    }

}
