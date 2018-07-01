package cn.fh.jobdep.task;

import cn.fh.jobdep.error.JobException;
import cn.fh.jobdep.graph.Graph;
import cn.fh.jobdep.graph.Vertex;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 任务(Task), 由多个有依赖关系的Job组成, 对应一张图
 */
@ToString
public class AdjTaskGraph implements Graph {
    /**
     * 邻接表
     */
    private JobVertex[] adj;

    /**
     * 反向adj
     */
    private JobVertex[] reversedAdj;

    private AdjTaskGraph() {

    }

    /**
     * 通过边构造一张图
     * @param edges
     */
    public AdjTaskGraph(List<JobEdge> edges) {
        if (CollectionUtils.isEmpty(edges)) {
            throw new JobException("edges is empty");
        }

        // 找出最大的定点, 这个值决定了adj索引的长度
        int maxVertex = edges.stream()
                .flatMap( edge -> Arrays.stream(new JobVertex[]{edge.getFrom(), edge.getTo()}))
                .max(Comparator.comparingInt(JobVertex::getIndex))
                .get()
                .getIndex();

        int maxLen = maxVertex + 1;
        this.adj = new JobVertex[maxLen];
        this.reversedAdj = new JobVertex[maxLen];

        // 遍历edge
        for (JobEdge edge : edges) {
            addEdge(edge, adj);
            addEdge(edge.reverse(), reversedAdj);
        }
    }


    @Override
    public List<? extends Vertex> getChildren(int vertex) {
        if (!rangeCheck(vertex)) {
            return null;
        }

        return this.adj[vertex].getToList();
    }

    @Override
    public List<? extends Vertex> getParents(int vertex) {
        if (!rangeCheck(vertex)) {
            return null;
        }

        return this.reversedAdj[vertex].getToList();
    }

    @Override
    public void changeStatus(int vertex, JobStatus status) {
        if (!rangeCheck(vertex)) {
            return;
        }

        this.adj[vertex].setStatus(status);
        this.reversedAdj[vertex].setStatus(status);
    }

    @Override
    public List<? extends Vertex> getRoots() {
        List<JobVertex> result = new ArrayList<>();
        for (JobVertex job : this.reversedAdj) {
            if (CollectionUtils.isEmpty(job.getToList())) {
                result.add(job);
            }
        }

        return result;
    }

    @Override
    public Vertex getLast() {
        for (JobVertex job : this.adj) {
            if (CollectionUtils.isEmpty(job.getToList())) {
                return job;
            }
        }

        return null;
    }

    private void addEdge(JobEdge edge, JobVertex[] adj) {
        JobVertex from = edge.getFrom().clone();
        JobVertex to = edge.getTo().clone();

        int slotIndex = from.getIndex();
        if (null == adj[slotIndex]) {
            adj[slotIndex] = from;
        }
        adj[slotIndex].addToVertex(to);

        slotIndex = to.getIndex();
        if (null == adj[slotIndex]) {
            adj[slotIndex] = to;
        }

    }

    private boolean rangeCheck(int vertex) {
        return vertex <= this.adj.length + 1;
    }
}
