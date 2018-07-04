package cn.fh.jobdep.graph;

import cn.fh.jobdep.error.JobException;
import com.alibaba.fastjson.JSONObject;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务(Task), 由多个有依赖关系的Job组成, 对应一张图
 */
@ToString
public class AdjTaskGraph {
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

    /**
     * 整个Task的运行状态
     */
    private volatile JobStatus status = JobStatus.NEW;

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
        int maxVertex = -1;
        for (JobEdge edge : edges) {
            int max = Math.max(edge.getFrom().getIndex(), edge.getTo().getIndex());
            if (max > maxVertex) {
                maxVertex = max;
            }
        }

        this.vertCount = maxVertex + 1;

        this.adj = new JobVertex[vertCount];
        this.reversedAdj = new JobVertex[vertCount];

        // 遍历edge
        for (JobEdge edge : edges) {
            addEdge(edge, adj);
            addEdge(edge.reverse(), reversedAdj);
        }
    }

    /**
     * 得到所有后序顶点
     * @param vertex
     * @return
     */
    public List<JobVertex> getChildren(int vertex) {
        if (!rangeCheck(vertex)) {
            return null;
        }

        return this.adj[vertex].getToList();
    }

    /**
     * 得到所有前序顶点
     * @param vertex
     * @return
     */
    public List<JobVertex> getParents(int vertex) {
        if (!rangeCheck(vertex)) {
            return null;
        }

        return this.reversedAdj[vertex].getToList();
    }

    /**
     * 修改顶点状态
     * @param vertex
     * @param status
     */
    public void changeJobStatus(int vertex, JobStatus status) {
        if (!rangeCheck(vertex)) {
            return;
        }

        this.adj[vertex].setStatus(status);
        this.reversedAdj[vertex].setStatus(status);
    }

    /**
     * 得到所有根顶点, 即没有前序顶点的顶点
     * @return
     */
    public List<JobVertex> getRoots() {
        List<JobVertex> result = new ArrayList<>();
        for (JobVertex job : this.reversedAdj) {
            if (CollectionUtils.isEmpty(job.getToList())) {
                result.add(this.adj[job.getIndex()]);
            }
        }

        return result;
    }

    /**
     * 得到所有终顶点, 即没有后续顶点的顶点
     * @return
     */
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
     * 设置顶点结果
     * @param vertex
     * @param result
     */
    public void setResult(int vertex, String result) {
        doSetResult(vertex, result, this.adj);
        doSetResult(vertex, result, this.reversedAdj);
    }

    /**
     * 根据id得到图中的对应顶点
     * @param vertex
     * @return
     */
    public JobVertex getJobVertex(int vertex) {
        if (!rangeCheck(vertex)) {
            return null;
        }

        return this.adj[vertex];
    }

    /**
     * 判断图是否有环
     * @return
     */
    public boolean hasCircle() {
        List<JobVertex> roots = getRoots();

        for (JobVertex root : roots) {
            boolean[] visited = new boolean[this.vertCount];
            boolean cycle = dfs(root, visited);

            if (cycle) {
                return true;
            }
        }

        return false;
    }

    public void changeTaskStatus(JobStatus status) {
        this.status = status;
    }

    private void doSetResult(int vertex, String result, JobVertex[] adj) {
        for (JobVertex job : adj) {
            if (job.getIndex() == vertex) {
                job.setResult(result);
            }

            for (JobVertex next : job.getToList()) {
                if (next.getIndex() == vertex) {
                    next.setResult(result);
                }
            }
        }
    }

    /**
     * 深度优先遍历
     * @param vert
     * @param visited
     */
    private boolean dfs(JobVertex vert, boolean[] visited) {
        // 标记当前顶点为已访问
        int currentIndex = vert.getIndex();
        visited[currentIndex] = true;

        // 取出后序顶点
        List<JobVertex> nextList = this.adj[currentIndex].getToList();
        if (CollectionUtils.isEmpty(nextList)) {
            visited[currentIndex] = false;
            return false;
        }

        for (JobVertex next : nextList) {
            // 判断下一个节点是否访问过
            if (visited[next.getIndex()]) {
                // 访问过, 有环
                return true;
            }

            boolean circle = dfs(next, visited);
            if (circle) {
                return true;
            }
        }

        visited[currentIndex] = false;
        return false;
    }

    private void addEdge(JobEdge edge, JobVertex[] adj) {
        JobVertex from = edge.getFrom();
        JobVertex to = edge.getTo();

        int slotIndex = from.getIndex();
        if (null == adj[slotIndex]) {
            adj[slotIndex] = from.clone();
        }
        adj[slotIndex].addToVertex(to);

        slotIndex = to.getIndex();
        if (null == adj[slotIndex]) {
            adj[slotIndex] = to.clone();
        }

    }

    private boolean rangeCheck(int vertex) {
        return vertex <= this.adj.length + 1;
    }

    public String toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("adj", this.adj);
        jsonObject.put("vertCount", this.vertCount);
        jsonObject.put("status", this.status);

        return jsonObject.toJSONString();
    }
}
