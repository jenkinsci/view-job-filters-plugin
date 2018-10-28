# Filters

The following is an overview of all the filters this plugin provides. 
See also the [class hierarchy](https://javadoc.jenkins.io/plugin/view-job-filters/hudson/views/package-tree.html) in the Javadocs.

## Abstract Filters

| Class | Description | Dependencies |
|-------|-------------|--------------|
| [`AbstractBuildTrendFilter`](src/main/java/hudson/views/AbstractBuildTrendFilter.java) | Base for including/excluding jobs depending on their builds | *none*
| [`AbstractIncludeExcludeJobFilter`](src/main/java/hudson/views/AbstractIncludeExcludeJobFilter.java) | Base for including/excluding jobs | *none*

## Include/Exclude Filters

| Class | Description | Dependencies |
|-------|-------------|--------------|
| [`BuildStatusFilter`](src/main/java/hudson/views/BuildStatusFilter.java) | Include/exclude jobs if they have been built/are building | *none*
| [`JobStatusFilter`](src/main/java/hudson/views/JobStatusFilter.java) | Include/exclude jobs based on result of last build | *none*
| [`JobTypeFilter`](src/main/java/hudson/views/JobTypeFilter.java) | Include/exclude jobs of a certain type | *none*
| [`OtherViewsFilter`](src/main/java/hudson/views/OtherViewsFilter.java) | Include/exclude jobs based on the view they are in | *none*
| [`ParameterFilter`](src/main/java/hudson/views/ParameterFilter.java) | Include/exclude jobs based on their parameterization | *none*
| [`RegExJobFilter`](src/main/java/hudson/views/RegExJobFilter.java) | Include/exclude jobs where a job property matches a regular expression | [cvs](https://github.com/jenkinsci/cvs-plugin)<br>[email&#8288;-&#8288;ext](https://github.com/jenkinsci/email-ext-plugin)<br>[git](https://github.com/jenkinsci/git-plugin)<br>[m2&#8288;-&#8288;extra&#8288;-&#8288;steps](https://github.com/jenkinsci/m2-extra-steps-plugin)<br>[mailer](https://github.com/jenkinsci/mailer-plugin)<br>[matrix&#8288;-&#8288;project](https://github.com/jenkinsci/matrix-project-plugin)<br>[subversion](https://github.com/jenkinsci/subversion-plugin)<br>
| [`ScmTypeFilter`](src/main/java/hudson/views/ScmTypeFilter.java) | Include/exclude jobs with a certain type of SCM | [cvs](https://github.com/jenkinsci/cvs-plugin)<br>[git](https://github.com/jenkinsci/git-plugin)<br>[subversion](https://github.com/jenkinsci/subversion-plugin)<br>
| [`SecuredJobsFilter`](src/main/java/hudson/views/SecuredJobsFilter.java) | Include/exclude jobs that use matrix-based security | [matrix&#8288;-&#8288;auth](https://github.com/jenkinsci/matrix-auth-plugin)<br>
| [`SecurityFilter`](src/main/java/hudson/views/SecurityFilter.java) | Include/exclude jobs based on the current user's permissions | *none*
| [`UnclassifiedJobsFilter`](src/main/java/hudson/views/UnclassifiedJobsFilter.java) | Include/exclude jobs that are not shown in other views | *none*

### Built Trend Filters

| Class | Description | Dependencies |
|-------|-------------|--------------|
| [`BuildDurationFilter`](src/main/java/hudson/views/BuildDurationFilter.java) | Include/exclude jobs based on their build duration | *none*
| [`BuildTrendFilter`](src/main/java/hudson/views/BuildTrendFilter.java) | Include/exclude jobs based on their build stability and cause | *none*
| [`UserRelevanceFilter`](src/main/java/hudson/views/UserRelevanceFilter.java) | Include/exclude jobs when they involve the currently logged in user | [email&#8288;-&#8288;ext](https://github.com/jenkinsci/email-ext-plugin)<br>[mailer](https://github.com/jenkinsci/mailer-plugin)

## Other Filters

| Class | Description | Dependencies |
|-------|-------------|--------------|
| [`AddRemoveFallbackFilter`](src/main/java/hudson/views/AddRemoveFallbackFilter.java) | Show all or no jobs based on results of earlier filters | *none*
| [`AllJobsFilter`](src/main/java/hudson/views/AllJobsFilter.java) | Show all jobs | *none*
| [`MostRecentJobsFilter`](src/main/java/hudson/views/MostRecentJobsFilter.java) | Sort jobs by most recently build | *none*
| [`UpstreamDownstreamJobsFilter`](src/main/java/hudson/views/UpstreamDownstreamJobsFilter.java) | Filter jobs based on inter-project dependencies | *none*
