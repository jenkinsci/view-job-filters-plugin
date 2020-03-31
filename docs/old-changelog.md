For new versions, see [GitHub Releases](https://github.com/jenkinsci/view-job-filters-plugin/releases).

### Version 2.1.1 (Released November 9, 2018)

-   Fix: [JENKINS-43343](https://issues.jenkins-ci.org/browse/JENKINS-43343) Regex
    filter now works with cron schedule for Pipeline jobs

### Version 2.1.0 (Released November 4, 2018)

-   Feature: Regex Filter now supports full name, display name and full
    display name for jobs and folders
-   Feature: Introduces internationalization support and adds German as
    first supported language
-   Improvement: In-application help has been largely rewritten

### Version 2.0.4 (Released October 19, 2018)

-   Fix: [JENKINS-13464](https://issues.jenkins-ci.org/browse/JENKINS-13464) [JENKINS-14916](https://issues.jenkins-ci.org/browse/JENKINS-14916) [JENKINS-32496](https://issues.jenkins-ci.org/browse/JENKINS-32496) Stack
    overflow when using OtherViewsFilter or UnclassifiedJobsFilter
    without View.READ permission

### Version 2.0.3 (Released October 16, 2018)

-   Fix: prevent stack overflow when circular references in Other Views
    and Unclassified Views filter are detected
    -   Display an error message identifying the circular view
        definition on the view config page
-   Fix:
    make [mailer](https://plugins.jenkins.io/mailer), [matrix-auth](https://plugins.jenkins.io/matrix-auth) and [matrix-project](https://plugins.jenkins.io/matrix-project) dependencies
    optional

### Version 2.0.2 (Released September 18, 2018)

-   Fix: prevent StackOverflowException when more than one view uses
    UnclassifiedJobsFilter
-   Fix: validate regex in RegexJobFilter
-   Fix: ensure sorting in MostRecentJobsFilter is stable

### Version 2.0.1 (Released September 13, 2018)

-   Fix: [JENKINS-29991](https://issues.jenkins-ci.org/browse/JENKINS-29991) [JENKINS-31710](https://issues.jenkins-ci.org/browse/JENKINS-31710) RegExJobFilter/ScmTypeFilter
    now work with SCMTriggerItem
-   Fix: inconsistency in multi-line regex matching, see [commit message](https://github.com/jenkinsci/view-job-filters-plugin/commit/cbba158c80d1af91afa7b61cc20858a6c7f8607b) for
    details

### Version 2.0.0 (Released September 9, 2018)

New maintainer and first release in three years. This release merely
puts the project on a newer foundation and updates some dependencies,
but otherwise introduces no new features or bug fixes. Existing filters
from pre-2.x versions should continue to work.

-   Minimal required Jenkins version increased to 1.625.3
-   Switch to [Semantic Versioning](https://semver.org/)
-   Project is now explicitly under the MIT License

### Version 1.27 (Released August 21, 2015)

Switched from Subversion to GitHub so the changelog is tricky to
determine.

-   [JENKINS-20522](https://issues.jenkins-ci.org/browse/JENKINS-20522)
    Proper use of top-level item visibility filter.
-   [JENKINS-29747](https://issues.jenkins-ci.org/browse/JENKINS-29747)
    Fallback filters.
-   Handle recurse in view.
-   [JENKINS-21862](https://issues.jenkins-ci.org/browse/JENKINS-21862)
    Allow parameter filter to filter particular runs correctly.

### Version 1.26 (Released August 14, 2013)

-   [JENKINS-19191](https://issues.jenkins-ci.org/browse/JENKINS-19191)
    (don't just consider build-trigger upstream, but all upstreams
    (including join))
-   [JENKINS-19125](https://issues.jenkins-ci.org/browse/JENKINS-19125)
    (Job SCM Configuration filter fails with CVS plugin disabled.)
-   [JENKINS-18986](https://issues.jenkins-ci.org/browse/JENKINS-18986)
    (Fencepost error in AbstractBuildTrendFilter.amount)

### Version 1.23 (Released June 29, 2013)

-   [JENKINS-18386](https://issues.jenkins-ci.org/browse/JENKINS-18386)
    (more flexible job filter by parameter)
-   [JENKINS-17597](https://issues.jenkins-ci.org/browse/JENKINS-17597)
    (Poor performance using ParameterFilter)
-   [JENKINS-17093](https://issues.jenkins-ci.org/browse/JENKINS-17093)
    (Filter to show N jobs sorted by most recently completed)
-   [JENKINS-18399](https://issues.jenkins-ci.org/browse/JENKINS-18399)
    (possibility to filter jobs by "Restrict where this project can be
    run")

### Version 1.22 (Released May 24, 2012)

-   Re-Fixed
    [JENKINS-13781](https://issues.jenkins-ci.org/browse/JENKINS-13781)
    (NPE in UserRelevanceView when no user is logged on)

### Version 1.21 (Released May 23, 2012)

-   Improved Maven project support by implementing two enhancements
    -   [JENKINS-13846](https://issues.jenkins-ci.org/browse/JENKINS-13846)
        (Upstream filter does not show all dependencies)
    -   [JENKINS-13850](https://issues.jenkins-ci.org/browse/JENKINS-13850)
        (Unable to match emails for "Regular Expression Job Filter" on
        Match Value "Email recipients")

### Version 1.20 (Released May 16, 2012)

-   Fixed
    [JENKINS-13781](https://issues.jenkins-ci.org/browse/JENKINS-13781)
    (NPE in UserRelevanceView when no user is logged on)
-   Improvement -
    [JENKINS-13748](https://issues.jenkins-ci.org/browse/JENKINS-13748)
    (Support for upstream/downstream jobs)

### Version 1.19 (Released March 26, 2012)

-   Fixed
    [JENKINS-13223](https://issues.jenkins-ci.org/browse/JENKINS-13223)
    (Filter jobs that have enabled project-based security)

### Version 1.18 (Released September 10, 2011)

-   Added the [Build Filter (Wrapper) Column](build-filter-wrapper-column.md)

### Version 1.17.2 (Released September 8, 2011)

-   Fixed
    [JENKINS-10935](https://issues.jenkins-ci.org/browse/JENKINS-10935)

### Version 1.17.1 (Released September 5, 2011)

-   Minor fix for Jenkins 1.427 compatibility issue with the
    introduction of "UserIdCause" (vs UserCause)

### Version 1.17 (Released September 3, 2011)

-   Added User Relevance Filter (see documentation on this page)

### Version 1.16 (Released August 16, 2011)

-   Fixed a bug with the way the regex filter handles multi-line
    descriptions (and trigger specs)
    [JENKINS-10716](http://issues.jenkins-ci.org/browse/JENKINS-10716)

### Version 1.15 (Released August 13, 2011)

-   added trigger types to the build trend filter. For example, create a
    view of all jobs that have not been triggered by an SCM change in a
    month.

### Version 1.14 (Released August 10, 2011)

-   fixed regression in the regex filter. version 1.13 unintentionally
    requires git plugin to be installed, or regex on scm will cause an
    exception that cannot be recovered from through the hudson gui.

### Version 1.13 (Released July 23, 2011)

-   add git
-   add chron to regex filter

### Version 1.12

-   Added Matrix Job support to Regular Expressions Maven option
-   Fixed a bug in Regular Expressions Maven option

### Version 1.11

-   Improved Parameterized Jobs Filter - see
    <http://issues.jenkins-ci.org/browse/JENKINS-8944>

### Version 1.9

-   Job Type Filter
-   Parameter Filter improvement to look at build parameter - see
    <http://issues.jenkins-ci.org/browse/JENKINS-7252>
-   Security Filter - see
    <http://issues.jenkins-ci.org/browse/JENKINS-8355>

### Version 1.8

-   Did not release properly due to Hudson infrastructure problems - all
    features scheduled for 1.8 are moved to 1.9

### Version 1.7

-   Under-the-hoods performance improvements such as
    [JENKINS-7956](http://issues.jenkins-ci.org/browse/JENKINS-7956)

### Version 1.6

-   Add Maven support to regular expression filter
-   Fixed bug where filters take jobs out of order.  This is not a
    backwards compatible fix in terms of the way it displays, but fixes
    an obvious bug where the "Job" column shows it is sorted by name,
    but in fact the jobs are obviously not sorted by name.

### Version 1.5

-   Fix
    [JENKINS-7732](http://issues.jenkins-ci.org/browse/JENKINS-7732) -
    "Job Views - Jobs not listed in other views filter"

### Version 1.4

-   Fix
    [JENKINS-7479](http://issues.jenkins-ci.org/browse/JENKINS-7479) -
    "Create View Job Filter that includes/excludes Jobs by their
    Parameters"
-   Fix SCM Type localization bug
-   For "Other Views" filter, added handling of ViewGroups for nested
    views

### Version 1.3

-   Fixed
    [JENKINS-7432](http://issues.jenkins-ci.org/browse/JENKINS-7432) -
    "Provide View Job Filter by Email Recipients"
-   Make SVN and CVS plugins optional instead of required

### Version 1.2

-   Fixed
    [JENKINS-7160](http://issues.jenkins-ci.org/browse/JENKINS-7160) -
    "Provide View Filter for Current Jobs (Jobs build within the last x
    days)"
-   Added Build Trend Filter to provide filtering on what happened to
    the build in recent history (configurable).  See this page for
    details.

### Version 1.1

-   Fixed [JENKINS-6932](http://issues.jenkins-ci.org/browse/JENKINS-6932) -
    "Allow to filter by SCM Branch" - applies to CVS only

### Version 1.0 (initial release)

-   Filter on other "descriptive" things besides job name.  Large
    organizations will have other things that distinguish jobs
    automatically, but Job name isn't one of those things
    -   SCM "URL" (only cvs and svn)
    -   Job description field
-   More comprehensive status filter - would obsolete some other
    features/plugins, but this filter should be more useful/useable
    -   Stable, Unstable, Failed, Aborted
    -   In queue, never built, building
    -   Disabled
-   Common include/exclude drop-down to make filters more easy to
    "chain"
-   All Jobs
-   Filter one view's jobs based on other view's jobs.
    -   Exclude - For example, View 1 will want all jobs with a certain
        regex, except for jobs already shown in View 2.  Then if View 2
        changes what jobs it shows, View 1 is automatically updated.
    -   Include - for a large organization, we might want "composite"
        views (not nested views) of the hierarchical parts of the
        organization
