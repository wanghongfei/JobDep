package cn.fh.jobdep.test;

import cn.fh.jobdep.graph.Graph;
import cn.fh.jobdep.task.AdjTaskGraph;
import cn.fh.jobdep.task.JobEdge;
import cn.fh.jobdep.task.JobVertex;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class AdjGraphTest {
    @Test
    public void testBuildAdj() {
        List<JobEdge> edges = Arrays.asList(
                new JobEdge(
                        new JobVertex(0, "-", "-"),
                        new JobVertex(1, "-", "-")
                ),
                new JobEdge(
                        new JobVertex(0, "-", "-"),
                        new JobVertex(2, "-", "-")
                ),
                new JobEdge(
                        new JobVertex(1, "-", "-"),
                        new JobVertex(3, "-", "-")
                ),
                new JobEdge(
                        new JobVertex(2, "-", "-"),
                        new JobVertex(3, "-", "-")
                )
        );

        Graph graph = new AdjTaskGraph(edges);
        System.out.println(graph);

        System.out.println("the child of 0:" + graph.getChildren(0));
        System.out.println("the parents of 3:" + graph.getParents(3));
        System.out.println("the parents of 2:" + graph.getParents(2));
        System.out.println("the parents of 1:" + graph.getParents(1));
        System.out.println("the child of 3:" + graph.getChildren(3));

        System.out.println("the roots:" + graph.getRoots());
        System.out.println("the end:" + graph.getLast());
    }

    @Test
    public void testBuildComplexAdj() {
        List<JobEdge> edges = Arrays.asList(
                new JobEdge(
                        new JobVertex(0, "-", "-"),
                        new JobVertex(2, "-", "-")
                ),
                new JobEdge(
                        new JobVertex(0, "-", "-"),
                        new JobVertex(3, "-", "-")
                ),
                new JobEdge(
                        new JobVertex(1, "-", "-"),
                        new JobVertex(3, "-", "-")
                ),
                new JobEdge(
                        new JobVertex(2, "-", "-"),
                        new JobVertex(4, "-", "-")
                ),
                new JobEdge(
                        new JobVertex(3, "-", "-"),
                        new JobVertex(5, "-", "-")
                ),
                new JobEdge(
                        new JobVertex(4, "-", "-"),
                        new JobVertex(6, "-", "-")
                ),
                new JobEdge(
                        new JobVertex(5, "-", "-"),
                        new JobVertex(6, "-", "-")
                )
        );

        Graph graph = new AdjTaskGraph(edges);
        System.out.println(graph);

        System.out.println("the child of 0:" + graph.getChildren(0));
        System.out.println("the parents of 3:" + graph.getParents(3));
        System.out.println("the parents of 2:" + graph.getParents(2));
        System.out.println("the parents of 1:" + graph.getParents(1));
        System.out.println("the child of 3:" + graph.getChildren(3));

        System.out.println("the roots:" + graph.getRoots());
        System.out.println("the end:" + graph.getLast());
    }
}
