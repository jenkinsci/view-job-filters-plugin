package hudson.views;

import hudson.plugins.emailext.ExtendedEmailPublisher;
import hudson.tasks.Publisher;

public class EmailExtValuesProvider extends AbstractEmailValuesProvider {

	@Override
	protected String getValue(Publisher publisher) {
		ExtendedEmailPublisher mailer = (ExtendedEmailPublisher) publisher;
		return mailer.recipientList;
	}
	@SuppressWarnings("unchecked")
	public Class getPluginTesterClass() {
		return ExtendedEmailPublisher.class;
	}
}
