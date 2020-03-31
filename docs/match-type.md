View Job Filters
plugin uses a Match Type to allow you to chain together as many filters
as you like. This is extremely useful, but needs to be used properly.

# The Match Types

The match types are named and described in an attempt to make it
intuitive as to what they are doing. The key to understanding them is
that they are meant to be chained together. In other words, the order is
significant, and each filter will either "include" (i.e. add) or
"exclude" (i.e. filter out, or take away) from the current accumulated
filter, depending on which match type you use.

-   Include Matched - Add jobs that match this filter
-   Include Unmatched - Add jobs that don't match this filter
-   Exclude Matched - Filter out jobs that match this filter
-   Exclude Unmatched - Filter out jobs that don't match this filter

# Example - All nightly test jobs

This example will create a view with all Test jobs that are run at
night. It counts on a job naming convention, as well as a convention of
putting a descriptive comment in the SCM polling schedule. The following
filters can be interpreted as "show all 'Test' jobs but only if they
also are 'nightly'".

#### Filter 1 - Regular Expression Job Filter

-   Regular Expression - .\*Test.\*
-   Match Value - Job name
-   Match Type - Include Matched - Add jobs that match this filter

#### Filter 2 - Regular Expression Job Filter

-   Regular Expression - .\*nightly.\*
-   Match Value - Job schedule
-   Match Type - Exclude Unmatched - Filter out jobs that don't match
    this filter
