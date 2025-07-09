package hudson.views.test;

import hudson.model.TopLevelItemDescriptor;
import hudson.scm.SCMDescriptor;
import hudson.views.*;

import java.util.Arrays;
import java.util.List;

import static hudson.views.AbstractBuildTrendFilter.AmountType.Builds;
import static hudson.views.AbstractBuildTrendFilter.BuildCountType.All;
import static hudson.views.AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched;
import static hudson.views.test.ViewJobFilters.UserRelevanceOption.*;
import static java.util.Arrays.asList;

public class ViewJobFilters {

    public enum NameOptions {
        MATCH_NAME, MATCH_FULL_NAME, MATCH_DISPLAY_NAME, MATCH_FULL_DISPLAY_NAME
    }

    public static RegExJobFilter nameRegex(String regex, NameOptions... options) {
        return new RegExJobFilter(
                regex,
                includeMatched.name(),
                RegExJobFilter.ValueType.NAME.name(),
                Arrays.asList(options).contains(NameOptions.MATCH_NAME),
                Arrays.asList(options).contains(NameOptions.MATCH_FULL_NAME),
                Arrays.asList(options).contains(NameOptions.MATCH_DISPLAY_NAME),
                Arrays.asList(options).contains(NameOptions.MATCH_FULL_DISPLAY_NAME));
    }

    public static RegExJobFilter folderNameRegex(String regex, NameOptions... options) {
        return new RegExJobFilter(
                regex,
                includeMatched.name(),
                RegExJobFilter.ValueType.FOLDER_NAME.name(),
                Arrays.asList(options).contains(NameOptions.MATCH_NAME),
                Arrays.asList(options).contains(NameOptions.MATCH_FULL_NAME),
                Arrays.asList(options).contains(NameOptions.MATCH_DISPLAY_NAME),
                Arrays.asList(options).contains(NameOptions.MATCH_FULL_DISPLAY_NAME));
    }

    public static RegExJobFilter descRegex(String regex) {
        return new RegExJobFilter(
                regex,
                includeMatched.name(),
                RegExJobFilter.ValueType.DESCRIPTION.name());
    }

    public static RegExJobFilter emailRegex(String regex) {
        return new RegExJobFilter(
                regex,
                includeMatched.name(),
                RegExJobFilter.ValueType.EMAIL.name());
    }

    public static RegExJobFilter scheduleRegex(String regex) {
        return new RegExJobFilter(
                regex,
                includeMatched.name(),
                RegExJobFilter.ValueType.SCHEDULE.name());
    }

    public static RegExJobFilter scmRegex(String regex) {
        return new RegExJobFilter(
                regex,
                includeMatched.name(),
                RegExJobFilter.ValueType.SCM.name());
    }

    public static RegExJobFilter mavenRegex(String regex) {
        return new RegExJobFilter(
                regex,
                includeMatched.name(),
                RegExJobFilter.ValueType.MAVEN.name());
    }

    public static RegExJobFilter nodeRegex(String regex) {
        return new RegExJobFilter(
                regex,
                includeMatched.name(),
                RegExJobFilter.ValueType.NODE.name());
    }


    public static BuildStatusFilter buildStatus(
            boolean neverBuild,
            boolean building,
            boolean inBuildQueue) {
        return new BuildStatusFilter(
                neverBuild,
                building,
                inBuildQueue,
                includeMatched.name());
    }

    public static JobStatusFilter jobStatus(
            boolean unstable,
            boolean failed,
            boolean aborted,
            boolean disabled,
            boolean stable,
            boolean notBuilt,
            String includeExcludeTypeString) {
        JobStatusFilter jobStatusFilter = new JobStatusFilter(
                unstable,
                failed,
                aborted,
                disabled,
                stable,
                includeExcludeTypeString);
        jobStatusFilter.setNotBuilt(notBuilt);
        return jobStatusFilter;
    }

    public static JobStatusFilter jobStatus(
            boolean unstable,
            boolean failed,
            boolean aborted,
            boolean disabled,
            boolean stable,
            boolean notBuilt) {
        return jobStatus(unstable, failed, aborted, disabled, stable, notBuilt, includeMatched.name());
    }

    public static JobTypeFilter jobType(TopLevelItemDescriptor descriptor) {
        return jobType(descriptor.getId());
    }

    public static JobTypeFilter jobType(String type) {
        return new JobTypeFilter(
                type,
                includeMatched.name());
    }

    public static ScmTypeFilter scmType(String type) {
        return new ScmTypeFilter(type, includeMatched.name());
    }

    public static SecurityFilter security(
            String permissionCheckType,
            boolean configure,
            boolean build,
            boolean workspace) {
        return new SecurityFilter(
                permissionCheckType,
                configure,
                build,
                workspace,
                includeMatched.name());
    }

    public static SecuredJobsFilter secured() {
        return new SecuredJobsFilter(includeMatched.name());
    }

    public static ScmTypeFilter scmType(SCMDescriptor<?> descriptor) {
        return scmType(descriptor.clazz.getName());
    }

    public static BuildDurationFilter buildDuration(float minutes, String lessThan) {
        return new BuildDurationFilter(minutes, "<".equals(lessThan),
                All.name(), 0, Builds.name(), includeMatched.name());
    }

    public static BuildTrendFilter buildTrend(BuildTrendFilter.StatusType statusType) {
        return new BuildTrendFilter(All.name(), statusType.name(), 0, Builds.name(), includeMatched.name());
    }

    public enum UserRelevanceOption {
        MATCH_USER_ID, MATCH_USER_FULL_NAME,
        IGNORE_CASE, IGNORE_WHITESPACE, IGNORE_NON_ALPHA_NUM,
        MATCH_BUILDER, MATCH_EMAIL, MATCH_SCM_LOG
    }

    public static UserRelevanceFilter userRelevance(UserRelevanceOption... options) {
        List<UserRelevanceOption> optionsList = asList(options);
        return new UserRelevanceFilter(
                optionsList.contains(MATCH_USER_ID),
                optionsList.contains(MATCH_USER_FULL_NAME),
                optionsList.contains(IGNORE_CASE),
                optionsList.contains(IGNORE_WHITESPACE),
                optionsList.contains(IGNORE_NON_ALPHA_NUM),
                optionsList.contains(MATCH_BUILDER),
                optionsList.contains(MATCH_EMAIL),
                optionsList.contains(MATCH_SCM_LOG),
                All.name(), 0, AbstractBuildTrendFilter.AmountType.Builds.name(), includeMatched.name());
    }

    public static ParameterFilter parameter(String nameRegex,
                                            String valueRegex,
                                            String descriptionRegex) {
        return new ParameterFilter(includeMatched.name(), nameRegex, valueRegex, descriptionRegex,
                true, false, 0, false);
    }


    public static ParameterFilter parameter(String nameRegex,
                                            String valueRegex,
                                            String descriptionRegex,
                                            boolean matchAllBuilds,
                                            int maxBuildsToMatch,
                                            boolean matchBuildsInProgress) {
        return new ParameterFilter(includeMatched.name(), nameRegex, valueRegex, descriptionRegex,
                false, matchAllBuilds, maxBuildsToMatch, matchBuildsInProgress);
    }

    public static UpstreamDownstreamJobsFilter upstreamDownstream(
            boolean includeDownstream, boolean includeUpstream,
            boolean recursive, boolean excludeOriginals) {
        return new UpstreamDownstreamJobsFilter(includeDownstream, includeUpstream, recursive, excludeOriginals);
    }

    public static UpstreamDownstreamJobsFilter upstream(
            boolean recursive, boolean excludeOriginals) {
        return new UpstreamDownstreamJobsFilter(false, true, recursive, excludeOriginals);
    }

    public static UpstreamDownstreamJobsFilter downstream(
            boolean recursive, boolean excludeOriginals) {
        return new UpstreamDownstreamJobsFilter(true, false, recursive, excludeOriginals);
    }
}
