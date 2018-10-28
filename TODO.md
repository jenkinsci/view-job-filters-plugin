# TODO

## General

* [x] Internationalization
* [ ] Run all tests in integration test projects
* [ ] Use static code analysis (PMD)
* [ ] Make sure all filters work with pipeline jobs and matrix projects
* [ ] Add JavaDoc
* [ ] Add license info to all files

## Filters

### OtherViewsFilter

* [ ] Handle view delete
* [ ] Handle view rename

### RegExJobFilter

* [ ] Ignore case
* [ ] Validate that at least one of the name option checkboxes is checked

### UnclassifiedJobsFilter

* [ ] Add option "Ignore views that contain all jobs"

### UserRelevanceFilter

* [ ] Validate that at least one of the user name/id checkboxes is checked
* [ ] Replace reflection code with straight-forward calls
* [ ] Conditionally show/hide build trend type when needed

### ViewGraph

* [ ] Handle possible ProxyView infinite recursion
* [ ] Test infinite recursion with folder plugin

## Ideas

* [ ] New FilteredView without explicit list of jobs (addressing JENKINS-22574)
* [ ] New Filter: Boolean Filter
