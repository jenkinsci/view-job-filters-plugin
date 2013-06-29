package hudson.views;

import hudson.tasks.Mailer;
import hudson.tasks.Publisher;

public class CoreEmailValuesProvider extends AbstractEmailValuesProvider {

	@Override
	protected String getValue(Publisher publisher) {
		Mailer mailer = (Mailer) publisher;
		return mailer.recipients;
	}
	@SuppressWarnings("unchecked")
	public Class getPluginTesterClass() {
		return Mailer.class;
	}
}
