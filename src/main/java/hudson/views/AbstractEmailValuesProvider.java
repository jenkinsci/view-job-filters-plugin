package hudson.views;

import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.TopLevelItem;
import hudson.tasks.Publisher;
import hudson.util.DescribableList;
import hudson.views.PluginHelperUtils.PluginHelperTestable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractEmailValuesProvider implements PluginHelperTestable {

	@SuppressWarnings("unchecked")
	public List<String> getValues(TopLevelItem item) {
		List<String> values = new ArrayList<String>();
		Descriptor<Publisher> descriptor = getDescriptor();
		if (item instanceof AbstractProject) {
			AbstractProject project = (AbstractProject) item;
			DescribableList<Publisher,Descriptor<Publisher>> publishers = project.getPublishersList();
			Publisher emailPublisher = publishers.get(descriptor);
			if (emailPublisher != null) {
				String value = getValue(emailPublisher);
				values.add(value);
			}
		}
		return values;
	}

	@SuppressWarnings("unchecked")
	protected Descriptor<Publisher> getDescriptor() {
		return Hudson.getInstance().getDescriptor(getPluginTesterClass());
	}
	protected abstract String getValue(Publisher publisher);
}
