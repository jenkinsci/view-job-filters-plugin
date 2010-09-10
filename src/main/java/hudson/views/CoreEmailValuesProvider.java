package hudson.views;

import hudson.model.Descriptor;
import hudson.tasks.Mailer;
import hudson.tasks.Publisher;

public class CoreEmailValuesProvider extends AbstractEmailValuesProvider {

	@Override
	protected Descriptor<Publisher> getDescriptor() {
		return Mailer.descriptor();
	}
	@Override
	protected String getValue(Publisher publisher) {
		Mailer mailer = (Mailer) publisher;
		return mailer.recipients;
	}
	
}
