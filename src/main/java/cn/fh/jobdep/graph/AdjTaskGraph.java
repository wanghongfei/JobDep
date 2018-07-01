package cn.fh.jobdep.graph;

import cn.fh.jobdep.error.JobException;
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
public class AdjTaskGraph implements Graph<JobVertex> {
    /**
     * 邻接表
     */
    private JobVertex[] adj;

    /**
     * 反向adj
     */
    private JobVertex[] reversedAdj;

    /**
     * 顶点数量
     */
    private int vertCount;

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

        this.vertCount = maxVertex + 1;

        this.adj = new JobVertex[vertCount];
        this.reversedAdj = new JobVertex[vertCount];

        // 遍历edge
        for (JobEdge edge : edges) {
            addEdge(edge, adj);
            addEdge(edge.reverse(), reversedAdj);
        }
    }


    @Override
    public List<JobVertex> getChildren(int vertex) {
        if (!rangeCheck(vertex)) {
            return null;
        }

        return this.adj[vertex].getToList();
    }

    @Override
    public List<JobVertex> getParents(int vertex) {
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
    public List<JobVertex> getRoots() {
        List<JobVertex> result = new ArrayList<>();
        for (JobVertex job : this.reversedAdj) {
            if (CollectionUtils.isEmpty(job.getToList())) {
                result.add(this.adj[job.getIndex()]);
            }
        }

        return result;
    }

    @Override
    public List<JobVertex> getLasts() {
        List<JobVertex> result = new ArrayList<>(2);

        for (JobVertex job : this.adj) {
            if (CollectionUtils.isEmpty(job.getToList())) {
                result.add(job);
            }
        }

        return result;
    }

    /**
     * 深度优先遍历
     * @param vert
     * @param visited
     */
    private void bfs(JobVertex vert, boolean[] visited) {
        // 标记当前顶点为已访问
        visited[vert.getIndex()] = true;

        // 取出后序顶点
        List<JobVertex> nextList = vert.getToList();
        if (CollectionUtils.isEmpty(nextList)) {
            return;
        }

        for (JobVertex next : nextList) {
            bfs(next, visited);
        }
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
