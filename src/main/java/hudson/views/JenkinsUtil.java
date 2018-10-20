package hudson.views;

import jenkins.model.Jenkins;

import javax.annotation.Nonnull;

public class JenkinsUtil {
    @Nonnull
    public static Jenkins getInstance() {
        Jenkins jenkins = Jenkins.getInstance();
        if (jenkins != null) {
           return jenkins;
        }
        throw new IllegalStateException("No jenkins instance found");
    }
}
