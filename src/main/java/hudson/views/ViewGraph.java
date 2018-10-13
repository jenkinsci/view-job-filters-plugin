package hudson.views;

import hudson.model.ListView;
import hudson.model.View;
import org.jgrapht.alg.cycle.TarjanSimpleCycles;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

public class ViewGraph {
    private final Collection<View> views;
    private DefaultDirectedGraph<View, DefaultEdge> graph;
    private Set<List<View>> cycles;
    private Set<View> viewsInCycles;
    private Set<View> viewsNotInCycles;

    private static final Comparator<List<View>> SORT_BY_SIZE_DESC = new Comparator<List<View>>() {
        @Override
        public int compare(List<View> o1, List<View> o2) {
            return  new Integer(o2.size()).compareTo(o1.size());
        }
    };

    public ViewGraph(Collection<View> views) {
        this.views = views;
    }

    private DefaultDirectedGraph<View, DefaultEdge> getGraph() {
        if (this.graph != null) {
            return this.graph;
        }
        this.graph = new DefaultDirectedGraph<View, DefaultEdge>(DefaultEdge.class);
        for (View view: views) {
            this.graph.addVertex(view);
        }
        for (View view: views) {
            for (ViewJobFilter filter: getViewJobFilters(view)) {
                if (filter instanceof UnclassifiedJobsFilter) {
                    for (View otherView : views) {
                        if (otherView != view) {
                            this.graph.addEdge(view, otherView);
                        }
                    }
                } else if (filter instanceof OtherViewsFilter) {
                    View otherView = ((OtherViewsFilter) filter).getOtherView();
                    this.graph.addEdge(view, otherView);
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
            List<List<View>> cycles = new TarjanSimpleCycles<View, DefaultEdge>(getGraph()).findSimpleCycles();
            Collections.sort(cycles, SORT_BY_SIZE_DESC);

            Set<List<View>> uniqueCycles = new HashSet<List<View>>();
            for (List<View> cycle : cycles) {
                if (!cycleIsSubsetOfOtherCycle(cycle, uniqueCycles)) {
                    uniqueCycles.add(cycle);
                }
            }
            this.cycles = uniqueCycles;
        }
        return this.cycles;
    }

    private boolean cycleIsSubsetOfOtherCycle(List<View> cycle, Set<List<View>> otherCycles) {
        for (List<View> otherCycle : otherCycles) {
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
            for (View view: views) {
                if (!getViewsInCycles().contains(view)) {
                    viewsNotInCycles.add(view);
                }
            }
            this.viewsNotInCycles = viewsNotInCycles;
        }
        return this.viewsNotInCycles;
    }
}
