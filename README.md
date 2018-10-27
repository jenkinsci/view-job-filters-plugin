[![Jenkins Plugins](https://img.shields.io/jenkins/plugin/v/view-job-filters.svg)](https://plugins.jenkins.io/view-job-filters)
[![Jenkins Plugins](https://img.shields.io/jenkins/plugin/i/view-job-filters.svg)](https://plugins.jenkins.io/view-job-filters)
[![MIT License](https://img.shields.io/github/license/jenkinsci/view-job-filters-plugin.svg)](LICENSE)
[![Build Status](https://ci.jenkins.io/buildStatus/icon?job=Plugins/view-job-filters-plugin/master)](https://ci.jenkins.io/blue/organizations/jenkins/Plugins%2Fview-job-filters-plugin/branches)
[![javadoc](https://img.shields.io/badge/javadoc-available-brightgreen.svg)](https://javadoc.jenkins.io/plugin/view-job-filters/)

# Jenkins View Job Filters Plugin

Manage multiple views and hundreds of jobs much more easily. This plug-in provides more ways to
include/exclude jobs from a view, including filtering by SCM path, and by any job or build status type, as well
as "chaining" of filters and negating filters.

## Development

* Start a local jenkins instance with the view-job-filter plugin included:

  ```
  $ mvn hpi:run
  ```

* Run tests and create a code coverage report:
 
  ```
  $ mvn test jacoco:report
  ```
  
  The code coverage will be in `target/site/jacoco/index.html`.

* Before submitting a pull request, run a full build including integration tests and findbugs:

  ```
  $ mvn install
  ```
  
* Publish a release (only for maintainers):

  ```
  $ mvn release:prepare release:perform
  ```

## License

MIT License

## Links

* [Jenkins CI](https://ci.jenkins.io/job/Plugins/job/view-job-filters-plugin/) ([Blue Ocean](https://ci.jenkins.io/blue/organizations/jenkins/Plugins%2Fview-job-filters-plugin/branches))
* [Wiki](https://wiki.jenkins.io/display/JENKINS/View+Job+Filters)
* [Plugin Site](https://plugins.jenkins.io/view-job-filters)
* JIRA: [Unresolved Issues](https://issues.jenkins-ci.org/issues/?filter=18844) | [All Issues](https://issues.jenkins-ci.org/issues/?filter=18843)

