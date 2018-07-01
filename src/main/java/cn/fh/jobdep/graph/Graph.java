package cn.fh.jobdep.graph;

import cn.fh.jobdep.task.JobStatus;

import java.util.List;

/**
 * 图操作接口
 */
public interface Graph {
    /**
     * 得到指定节点的所有后序节点
     * @param vertex
     * @return
     */
    List<? extends Vertex> getChildren(int vertex);

    /**
     * 得到指定节点的所有父节点
     * @param vertex
     * @return
     */
    List<? extends Vertex> getParents(int vertex);

    /**
     * 得到全部开始节点
     * @return
     */
    List<? extends Vertex> getRoots();

    /**
     * 得到终结点
     * @return
     */
    Vertex getLast();

    /**
     * 改变指定节点状态
     * @param vertex
     * @param status
     */
    void changeStatus(int vertex, JobStatus status);
}
