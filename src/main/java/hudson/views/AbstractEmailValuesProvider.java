package hudson.views;

import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.TopLevelItem;
import hudson.tasks.Publisher;
import hudson.util.DescribableList;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractEmailValuesProvider {

	@SuppressWarnings("unchecked")
	public List<String> getValues(TopLevelItem item) {
		List<String> values = new ArrayList<String>();
		if (item instanceof AbstractProject) {
			AbstractProject project = (AbstractProject) item;
			DescribableList<Publisher,Descriptor<Publisher>> publishers = project.getPublishersList();
			Descriptor<Publisher> descriptor = getDescriptor();
			Publisher emailPublisher = publishers.get(descriptor);
			if (emailPublisher != null) {
				String value = getValue(emailPublisher);
				values.add(value);
			}
		}
		return values;
	}

	protected abstract Descriptor<Publisher> getDescriptor();
	protected abstract String getValue(Publisher publisher);
}
