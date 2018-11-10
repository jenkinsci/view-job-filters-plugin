package hudson.views;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.DescriptorExtensionList;
import hudson.Extension;
import hudson.model.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Logger;

import hudson.util.RunList;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import net.sf.json.JSONObject;

import org.jenkinsci.plugins.workflow.support.steps.build.RunWrapper;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * This column wraps another column so that when that delegate column renders, it is
 * given a JobWrapper that provides a filtered list of Runs to the column.
 * 
 * @author jacob robertson
 */
public class BuildFilterColumn extends ListViewColumn {

    private static final Logger LOGGER = Logger.getLogger(BuildFilterColumn.class.getName());

    private ListViewColumn delegate;
	private ListView view;

	@DataBoundConstructor
	public BuildFilterColumn(ListViewColumn delegate) {
		this.delegate = delegate;
	}

	public BuildFilterColumn(ListViewColumn delegate, ListView view) {
		this(delegate);
		this.view = view;
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
			return hudson.views.filters.Messages.BuildFilterColumn_DisplayName();
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
			Descriptor desc = JenkinsUtil.getInstance().getDescriptor(staplerClass);
			ListViewColumn col = (ListViewColumn) desc.newInstance(req, delegate);
			return col;
		}
		
		@Override
		public boolean shownByDefault() {
			return false;
		}
	}

	public ListViewColumn getDelegate() {
		return delegate;
	}

	@SuppressWarnings("unchecked")
	public Job getJobWrapper(final Job job) {

		final JobWrapper wrapper = new JobWrapper(job);

		try {
			ProxyFactory factory = new ProxyFactory();
			factory.setSuperclass(job.getClass());

			MethodHandler handler = new MethodHandler() {
				@Override
				public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
					if (matches(thisMethod, args, "_getRuns") && thisMethod.getReturnType().equals(SortedMap.class)) {
						return wrapper._getRuns();
					}
					if (matches(thisMethod, args, "getBuilds")) {
						return wrapper.getBuilds();
					}
					if (matches(thisMethod, args, "getLastBuild")) {
						return wrapper.getLastBuild();
					}
					if (matches(thisMethod, args, "getIconColor")) {
						return wrapper.getIconColor();
					}
					if (matches(thisMethod, args, "getBuildHealthReports")) {
						return wrapper.getBuildHealthReports();
					}
					return thisMethod.invoke(job, args);
				}
			};
			return (Job) factory.create(
				new Class[] { ItemGroup.class, String.class },
				new Object[] { job.getParent(), job.getName() },
				handler
			);
		} catch (Exception e) {
            LOGGER.info("Can't proxy job of type " + job.getClass() + ": " + e.getMessage());
            LOGGER.info("Falling back to simple wrapper object");
		    return wrapper;
		}
	}

    @SuppressFBWarnings(value="REC_CATCH_EXCEPTION")
	public Run getRunWrapper(final Run run) {
		final RunWrapper wrapper = new RunWrapper(run);

		try {
			ProxyFactory factory = new ProxyFactory();
			factory.setSuperclass(run.getClass());

			MethodHandler handler = new MethodHandler() {
				@Override
				public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
				   	if (matches(thisMethod, args, "_this")) {
						return wrapper._this();
					}
					if (matches(thisMethod, args, "getNextBuild")) {
						return wrapper.getNextBuild();
					}
					if (matches(thisMethod, args, "getPreviousBuild")) {
						return wrapper.getPreviousBuild();
					}
					return thisMethod.invoke(run, args);
				}
			};
			return (Run) factory.create(
					new Class[] { run.getParent().getClass() },
					new Object[] { run.getParent() },
					handler
			);
		} catch (Exception e) {
            LOGGER.info("Can't proxy build of type " + run.getClass() + ": " + e.getMessage());
            LOGGER.info("Falling back to simple wrapper object");
			return wrapper;
		}
	}

	public class JobWrapper extends Job {
		private final Job delegate;

		public JobWrapper(Job delegate) {
			super(delegate.getParent(), delegate.getName());
			this.delegate = delegate;
		}

		@Override

		public Run getLastBuild() {
            SortedMap<Integer, Run> runs = _getRuns();
            Integer id = runs.lastKey();
			if (id != null) {
				return runs.get(id);
			}
			return null;
		}

		@Override
		public RunList getBuilds() {
			return RunList.fromRuns(_getRuns().values());
		}

		@Override
		protected SortedMap<Integer, Run> _getRuns() {
			SortedMap<Integer, Run> builds = new TreeMap<Integer, Run>();

			SortedMap<Integer, Run> buildsAsMap = delegate.getBuildsAsMap();
			for (Map.Entry<Integer, Run> entry : buildsAsMap.entrySet()) {
				if (matchesRun(entry.getValue())) {
					builds.put(entry.getKey(), getRunWrapper(entry.getValue()));
				}
			}

			return builds;
		}

		@Override
		public boolean isBuildable() {
			return delegate.isBuildable();
		}

		@Override
		protected void removeRun(Run run) {
		}
	}

	public class RunWrapper extends Run {
		private final Run delegate;

		public RunWrapper(Run delegate) {
			super(delegate.getParent(), delegate.getTimestamp());
			this.delegate = delegate;
		}

		@Nonnull
		@Override
		protected Run _this() {
			return this;
		}

		@CheckForNull
		@Override
		public Run getPreviousBuild() {
			for (Run i = delegate.getPreviousBuild(); i != null; i = i.getPreviousBuild()) {
			    if (matchesRun(i)) {
			        return getRunWrapper(i);
                }
            }
			return null;
		}

		@CheckForNull
		@Override
		public Run getNextBuild() {
  			for (Run i = delegate.getNextBuild(); i != null; i = i.getNextBuild()) {
			    if (matchesRun(i)) {
			        return getRunWrapper(i);
                }
            }
            return null;
		}
	}

	private boolean matches(Method method, Object[] args, String name, Class... argTypes) {
		if (!method.getName().equals(name)) {
			return false;
		}
		if (args.length != argTypes.length) {
			return false;
		}
		for (int i = 0; i < argTypes.length; i++) {
			if (!argTypes[i].isInstance(args[i])) {
				return false;
			}
		}
		return true;
	}

    private boolean matchesRun(Run run) {
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

}
