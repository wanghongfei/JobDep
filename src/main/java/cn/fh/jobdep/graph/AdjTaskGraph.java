package cn.fh.jobdep.graph;

import cn.fh.jobdep.error.JobException;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 任务(Task), 由多个有依赖关系的Job组成, 对应一张图
 */
@ToString
public class AdjTaskGraph {
    /**
     * 邻接表
     */
    private Matrix adj;

    /**
     * 反向adj
     */
    private Matrix reversedAdj;

    /**
     * 顶点数量
     */
    private int vertCount;

    /**
     * 保存所有顶点
     */
    private Map<Integer, JobVertex> vertexMap = new HashMap<>();

    /**
     * 整个Task的运行状态
     */
    private volatile JobStatus status = JobStatus.NEW;

    private AdjTaskGraph() {

    }

    private AdjTaskGraph(GraphWrapper wrapper) {
        this.adj = wrapper.adj;
        this.reversedAdj = wrapper.reversedAdj;
        this.vertCount = wrapper.vertCount;
        this.vertexMap = wrapper.vertexMap;
        this.status = wrapper.status;
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

        this.adj = new Matrix(vertCount);
        this.reversedAdj = new Matrix(vertCount);

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
        return adj.getRows(vertex).stream()
                .map(id -> vertexMap.get(id))
                .collect(Collectors.toList());
    }

    /**
     * 得到所有前序顶点
     * @param vertex
     * @return
     */
    public List<JobVertex> getParents(int vertex) {
        return reversedAdj.getRows(vertex).stream()
                .map(id -> vertexMap.get(id))
                .collect(Collectors.toList());
    }

    /**
     * 修改顶点状态
     * @param vertex
     * @param status
     */
    public void changeJobStatus(int vertex, JobStatus status) {
        JobVertex job = vertexMap.get(vertex);
        if (null == job) {
            return;
        }

        job.setStatus(status);
    }

    /**
     * 得到所有根顶点, 即没有前序顶点的顶点
     * @return
     */
    public List<JobVertex> getRoots() {
//        List<JobVertex> result = new ArrayList<>();
//        for (JobVertex job : this.reversedAdj) {
//            if (CollectionUtils.isEmpty(job.getToList())) {
//                result.add(this.adj[job.getIndex()]);
//            }
//        }
//
//        return result;

        List<Integer> idList = new ArrayList<>();
        for (Matrix.MatrixRow row : this.reversedAdj) {
            if (row.getList().isEmpty()) {
                idList.add(row.getIndex());
            }
        }

        return idList.stream()
                .map(id -> vertexMap.get(id))
                .collect(Collectors.toList());
    }

    /**
     * 得到所有终顶点, 即没有后续顶点的顶点
     * @return
     */
    public List<JobVertex> getLasts() {

//        for (JobVertex job : this.adj) {
//            if (CollectionUtils.isEmpty(job.getToList())) {
//                result.add(job);
//            }
//        }

        List<Integer> idList = new ArrayList<>();
        for (Matrix.MatrixRow row : this.adj) {
            if (row.getList().isEmpty()) {
                idList.add(row.getIndex());
            }
        }

        return idList.stream()
                .map(id -> vertexMap.get(id))
                .collect(Collectors.toList());
    }

    /**
     * 设置顶点结果
     * @param vertex
     * @param result
     */
    public void setResult(int vertex, String result) {
//        doSetResult(vertex, result, this.adj);
//        doSetResult(vertex, result, this.reversedAdj);

        JobVertex job = vertexMap.get(vertex);
        if (null == job) {
            return;
        }

        job.setResult(result);
    }

    /**
     * 根据id得到图中的对应顶点
     * @param vertex
     * @return
     */
    public JobVertex getJobVertex(int vertex) {
//        if (!rangeCheck(vertex)) {
//            return null;
//        }
//
//        return this.adj[vertex];
        return vertexMap.get(vertex);
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

    /**
     * 将adj转成json表示形式
     * @return
     */
    public String toJson() {
        GraphWrapper wrapper =
                new GraphWrapper(this.adj, this.reversedAdj, this.vertCount, this.vertexMap, this.status);
        return JSON.toJSONString(wrapper);
    }

    /**
     * 将json解析成adj
     * @param json
     * @return
     */
    public static AdjTaskGraph fromJson(String json) {
        GraphWrapper wrapper = JSON.parseObject(json, GraphWrapper.class);
        return wrapper.toGraph();

    }

    public JobStatus getTaskStatus() {
        return this.status;
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
        List<JobVertex> nextList = this.adj.getRows(currentIndex).stream()
                .map(id -> vertexMap.get(id))
                .collect(Collectors.toList());

        // List<JobVertex> nextList = this.adj[currentIndex].getToList();
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

    private void addEdge(JobEdge edge, Matrix adj) {
        JobVertex from = edge.getFrom();
        JobVertex to = edge.getTo();

        // 保存顶点
        vertexMap.putIfAbsent(from.getIndex(), from);
        vertexMap.putIfAbsent(to.getIndex(), to);

        // 建立边关系
        adj.addY(from.getIndex(), to.getIndex());
    }

    /**
     * 对图进行json序列化时需要构造此类的对象进行序列化
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GraphWrapper {
        private Matrix adj;

        private Matrix reversedAdj;

        private int vertCount;

        private Map<Integer, JobVertex> vertexMap;

        private volatile JobStatus status;

        public AdjTaskGraph toGraph() {
            return new AdjTaskGraph(this);
        }
    }

}
