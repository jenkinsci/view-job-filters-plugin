package hudson.views;

import hudson.DescriptorExtensionList;
import hudson.Extension;
import hudson.model.RunMap;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.Job;
import hudson.model.ListView;
import hudson.model.Run;
import hudson.model.View;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * This column wraps another column so that when that delegate column renders, it is
 * given a JobWrapper that provides a filtered list of Runs to the column.
 * 
 * @author jacob robertson
 */
public class BuildFilterColumn extends ListViewColumn {

	private ListViewColumn delegate;
	private ListView view;

	@DataBoundConstructor
	public BuildFilterColumn(ListViewColumn delegate) {
		this.delegate = delegate;
	}

	@SuppressWarnings("unchecked")
	public static List doGetAllColumns() {
		DescriptorExtensionList<ListViewColumn, Descriptor<ListViewColumn>> all = ListViewColumn.all();
		List list = new ArrayList();
		for (Object descriptor: all) {
			if (descriptor instanceof DescriptorImpl) {
				continue;
			}
			list.add(descriptor);
		}
		return list;
	}
	
	@Extension
	public static class DescriptorImpl extends ListViewColumnDescriptor {
		@Override
		public String getDisplayName() {
			return "Build Filter (Wrapper) Column";
		}
		@Override
		public ListViewColumn newInstance(StaplerRequest req, JSONObject obj)
				throws hudson.model.Descriptor.FormException {
			BuildFilterColumn col;
			try {
				col = (BuildFilterColumn) super.newInstance(req, obj);
			} catch (Exception e) {
				// this might have failed because the column didn't have the DataboundConstructor annotation
				ListViewColumn delegate;
				try {
					delegate = newInstanceFromClass(req, obj);
				} catch (Exception e1) {
					throw new IllegalArgumentException(
							"Unable to wrap column:" + e.getMessage() + "/" + obj, e1);
				}
				col = new BuildFilterColumn(delegate);
			}
			if (req != null) {
		        View view = req.findAncestorObject(View.class);
		        if (view instanceof ListView) {
		        	col.view = (ListView) view;
		        } else {
		        	throw new IllegalArgumentException("BuildFilterColumn can only be added to a ListView");
		        }
			}
			return col;
		}
		@SuppressWarnings("unchecked")
		private ListViewColumn newInstanceFromClass(StaplerRequest req, JSONObject obj) 
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, FormException {
			// {"delegate":{"stapler-class":"hudson.plugins.column.console.LastBuildColumn","value":"7"} ...
			JSONObject delegate = obj.getJSONObject("delegate");
			String staplerClass = delegate.getString("stapler-class");
			Descriptor desc = Hudson.getInstance().getDescriptor(staplerClass);
			ListViewColumn col = (ListViewColumn) desc.newInstance(req, delegate);
			return col;
		}
		
		@Override
		public boolean shownByDefault() {
			return false;
		}
	}
	@SuppressWarnings("unchecked")
	public Job getJobWrapper(Job job) {
		return new JobWrapper(job);
	}
	public ListViewColumn getDelegate() {
		return delegate;
	}
	
	@SuppressWarnings("unchecked")
	class JobWrapper extends Job {

		private Job delegate;
		
		public JobWrapper(Job delegate) {
			super(delegate.getParent(), delegate.getName());
			this.delegate = delegate;
		}

		@Override
		protected SortedMap _getRuns() {
			RunMap builds = new RunMap();
			SortedMap map = delegate.getBuildsAsMap();
			for (Object runObject: map.values()) {
				Run run = (Run) runObject;
				if (matchesRun(run)) {
					builds.put(run);
				}
			}
			return builds;
		}
		public boolean matchesRun(Run run) {
			for (ViewJobFilter filter: view.getJobFilters()) {
				if (filter instanceof RunMatcher) {
					RunMatcher matcher = (RunMatcher) filter;
					if (!matcher.matchesRun(run)) {
						return false;
					}
				}
			}
			return true;
		}

		@Override
		public boolean isBuildable() {
			return delegate.isBuildable();
		}

		@Override
		protected void removeRun(Run run) {
			// can't do this - it's protected
//			delegate.removeRun(run);
		}
	}
	
}
