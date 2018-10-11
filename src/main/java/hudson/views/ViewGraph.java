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
    private List<List<View>> cycles;
    private Set<View> viewsInCycles;
    private Set<View> viewsNotInCycles;

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


    public List<List<View>> getCycles() {
        if (this.cycles == null) {
            this.cycles = new TarjanSimpleCycles<View, DefaultEdge>(getGraph()).findSimpleCycles();
        }
        return this.cycles;
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
