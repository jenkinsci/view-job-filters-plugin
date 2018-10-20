# TODO

## General

[ ] Internationalization
[ ] Run all tests in integration test projects
[ ] Use static code analysis (PMD)
[ ] Make sure all filters work with pipeline jobs and matrix projects
[ ] Add JavaDoc
[ ] Add license info to all files

## Filters

### OtherViewsFilter

[ ] Handle view delete
[ ] Handle view rename

### RegExJobFilter

[ ] Test regex validation
[ ] Ignore case

### UnclassifiedJobsFilter

[ ] Add option "Ignore views that contain all jobs"

### UserRelevanceFilter

[ ] Use dropdown instead of checkboxes for `matchUserId` and `matchUserFullName`
[ ] Replace reflection code with straight-forward calls

### ViewGraph

[ ] Handle possible ProxyView infinite recursion
[ ] Test infinite recursion with folder plugin

## Ideas

* Create FilteredView without explicit list of jobs (addressing JENKINS-22574)
