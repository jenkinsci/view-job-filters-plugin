package hudson.views;

import hudson.maven.MavenReporter;
import hudson.maven.MavenModuleSet;
import hudson.maven.reporters.MavenMailer;
import hudson.model.TopLevelItem;
import hudson.tasks.Publisher;

import java.util.ArrayList;
import java.util.List;

public class EmailMavenValuesProvider extends AbstractEmailValuesProvider {

	@Override
	public List<String> getValues(TopLevelItem item) {
		if (item instanceof MavenModuleSet) {
			List<String> values = new ArrayList<String>();
			MavenModuleSet maven = (MavenModuleSet) item;
			for (MavenReporter reporter: maven.getReporters().toList()) {
				if (reporter instanceof MavenMailer) {
					MavenMailer mailer = (MavenMailer) reporter;
					values.add(mailer.recipients);
				}
			}
			return values;
		} else {
			return null;
		}
	}
	@Override
	protected String getValue(Publisher publisher) {
		throw new UnsupportedOperationException();
	}
	@SuppressWarnings("unchecked")
	public Class getPluginTesterClass() {
		return MavenMailer.class;
	}
}
