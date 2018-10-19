package hudson.views;

import hudson.model.ListView;
import hudson.model.View;
import hudson.model.ViewGroup;
import org.jgrapht.alg.cycle.TarjanSimpleCycles;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

import static java.lang.Boolean.TRUE;

public class ViewGraph {
    private final Map<String, View> views;
    private DefaultDirectedGraph<String, DefaultEdge> graph;
    private Set<List<View>> cycles;
    private Set<View> viewsInCycles;
    private Set<View> viewsNotInCycles;

    private static final Comparator<List<String>> SORT_BY_SIZE_DESC = new Comparator<List<String>>() {
        @Override
        public int compare(List<String> o1, List<String> o2) {
            return Integer.valueOf(o2.size()).compareTo(o1.size());
        }
    };

    public ViewGraph() {
        this(getAllViewsByName());
    }

    public ViewGraph(Map<String, View> views) {
        this.views = views;
    }

    private DefaultDirectedGraph<String, DefaultEdge> getGraph() {
        if (this.graph != null) {
            return this.graph;
        }
        this.graph = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
        for (String viewName: views.keySet()) {
            this.graph.addVertex(viewName);
        }
        for (Map.Entry<String, View> entry: views.entrySet()) {
            String viewName = entry.getKey();
            View view = entry.getValue();
            for (ViewJobFilter filter: getViewJobFilters(view)) {
                if (filter instanceof UnclassifiedJobsFilter) {
                    for (String otherViewName : views.keySet()) {
                        if (!otherViewName.equals(viewName)) {
                            this.graph.addEdge(viewName, otherViewName);
                        }
                    }
                } else if (filter instanceof OtherViewsFilter) {
                    String otherViewName = ((OtherViewsFilter) filter).getOtherViewName();
                    if (otherViewName != null) {
                        this.graph.addEdge(viewName, otherViewName);
                    }
                }
            }
        }
        return this.graph;
    }

    private List<ViewJobFilter> getViewJobFilters(View view) {
        if (view instanceof ListView) {
           return ((ListView)view).getJobFilters();
        }
        return Collections.emptyList();
    }


    public Set<List<View>> getCycles() {
        if (this.cycles == null) {
            List<List<String>> cycles = new TarjanSimpleCycles<String, DefaultEdge>(getGraph()).findSimpleCycles();
            Collections.sort(cycles, SORT_BY_SIZE_DESC);

            Set<List<String>> uniqueCycles = new HashSet<List<String>>();
            for (List<String> cycle : cycles) {
                if (!cycleIsSubsetOfOtherCycle(cycle, uniqueCycles)) {
                    uniqueCycles.add(cycle);
                }
            }
            this.cycles = toViewCyles(uniqueCycles);
        }
        return this.cycles;
    }

    private Set<List<View>> toViewCyles(Set<List<String>> cycles) {
        Set<List<View>> viewCyles = new HashSet<List<View>>();
        for (List<String> cycle: cycles) {
            List<View> viewCycle = new ArrayList<View>();
            for (String viewName : cycle) {
                viewCycle.add(this.views.get(viewName));
            }
            viewCyles.add(viewCycle);
        }
        return viewCyles;
    }

    public List<View> getFirstCycleWithView(View view) {
        Set<List<View>> cycles = getCycles();
        for (List<View> cycle: cycles) {
            if (cycle.contains(view)) {
               return cycle;
            }
        }
        return null;
    }

    private boolean cycleIsSubsetOfOtherCycle(List<String> cycle, Set<List<String>> otherCycles) {
        for (List<String> otherCycle : otherCycles) {
            if (otherCycle.containsAll(cycle)) {
                return true;
            }
        }
        return false;
    }

    public Set<View> getViewsInCycles() {
        if (viewsInCycles == null) {
            Set<View> viewsInCycles = new HashSet<View>();
            for (List<View> cycle : getCycles()) {
                for (View view: cycle) {
                   viewsInCycles.add(view);
                }
            }
            this.viewsInCycles = viewsInCycles;
        }
        return this.viewsInCycles;
    }

    public Set<View> getViewsNotInCycles() {
        if (viewsNotInCycles == null) {
            Set<View> viewsNotInCycles = new HashSet<View>();
            for (View view: views.values()) {
                if (!getViewsInCycles().contains(view)) {
                    viewsNotInCycles.add(view);
                }
            }
            this.viewsNotInCycles = viewsNotInCycles;
        }
        return this.viewsNotInCycles;
    }

    private static void addViews(View view, List<View> views) {
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            Collection<View> subViews = group.getViews();
            for (View sub: subViews) {
                addViews(sub, views);
            }
        } else {
            views.add(view);
        }
    }

    /*
     * JENKINS-13464, JENKINS-14916, JENKINS-32496
     *
     * Both OtherViewsFilter and UnclassifiedJobsFilter need to get all views via the getAllViews()
     * method below which relies on Jenkins.getViews(). If a user doesn't have the View.READ permission
     * for a view then Jenkins.getViews() calls view.getItems() in order to determine if the view
     * contains any items at all. But since view.getItems() filters the items through OtherViewsFilter
     * or UnclassifiedJobsFilter we end up calling getAllViews() again. This leads to on endless recursive
     * loop and eventually a StackOverflowException.
     *
     * We could check if the current user has global View.READ permission, but that might break down for
     * view-specific access control.
     *
     * The easiest, though admittedly hackish, solution is to detect the endless recursion by setting
     * a thread-local flag indicating that the getAllViews() method is already running and short-circuiting
     * the recursion in case we find ourselves being called repeatedly.
     */
    private static final ThreadLocal<Boolean> isGetAllViewsAlreadyRunning = new ThreadLocal<Boolean>();

    public static List<View> getAllViews() {
        List<View> views = new ArrayList<View>();

        if (TRUE.equals(isGetAllViewsAlreadyRunning.get())) {
            return views;
        }
        isGetAllViewsAlreadyRunning.set(TRUE);

        try {
            Collection<View> baseViews = JenkinsUtil.getInstance().getViews();
            for (View view : baseViews) {
                addViews(view, views);
            }

            return views;
        } finally {
            isGetAllViewsAlreadyRunning.remove();
        }
    }

    public static Map<String, View> getAllViewsByName() {
        Map<String, View> views = new HashMap<String, View>();
        for (View view: getAllViews()) {
            views.put(toName(view), view);
        }
        return views;
    }

    /*
     * Takes into account nested names.
     */
    public static View getView(String name) {
        Collection<View> views = getAllViews();
        for (View view: views) {
            String otherName = toName(view);
            if (otherName.equals(name)) {
                return view;
            }
        }
        return null;
    }

    /*
     * Alternate strategy for getting name, to handle nested views.
     */
    public static String toName(View view) {
        String name = view.getViewName();
        ViewGroup owner = view.getOwner();
        if (owner instanceof View) {
            View ownerView = (View)owner;
            if (!ownerView.equals(view)) {
                String parentName = toName((View) owner);
                name = parentName + " / " + name;
            }
        }
        return name;
    }

    public static String toName(List<View> cycle) {
        StringBuilder builder = new StringBuilder();
        for (View view: cycle) {
            builder.append(toName(view));
            builder.append(" -> ");
        }
        builder.append(toName(cycle.get(0)));
        return builder.toString();
    }
}
