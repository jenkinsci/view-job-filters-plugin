package hudson.views;

import jenkins.model.Jenkins;

import edu.umd.cs.findbugs.annotations.NonNull;

public class JenkinsUtil {
    @NonNull
    public static Jenkins getInstance() {
        Jenkins jenkins = Jenkins.getInstance();
        if (jenkins != null) {
           return jenkins;
        }
        throw new IllegalStateException("No jenkins instance found");
    }
}
