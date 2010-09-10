package hudson.views;

import hudson.model.Descriptor;
import hudson.plugins.emailext.ExtendedEmailPublisher;
import hudson.tasks.Publisher;

public class EmailExtValuesProvider extends AbstractEmailValuesProvider {

	@Override
	protected Descriptor<Publisher> getDescriptor() {
		return ExtendedEmailPublisher.DESCRIPTOR;
	}
	@Override
	protected String getValue(Publisher publisher) {
		ExtendedEmailPublisher mailer = (ExtendedEmailPublisher) publisher;
		return mailer.recipientList;
	}
	
}
