package hudson.views.test;

import hudson.views.AbstractIncludeExcludeJobFilter;
import hudson.views.RegExJobFilter;

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
}
