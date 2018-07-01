package cn.fh.jobdep.graph;

import java.util.List;

/**
 * 图操作接口;
 * @param <T> 顶点类型
 */
public interface Graph<T extends Vertex> {
    /**
     * 得到指定节点的所有后序节点
     * @param vertex
     * @return
     */
    List<T> getChildren(int vertex);

    /**
     * 得到指定节点的所有父节点
     * @param vertex
     * @return
     */
    List<T> getParents(int vertex);

    /**
     * 得到全部开始节点
     * @return
     */
    List<T> getRoots();

    /**
     * 得到全部叶子结点
     * @return
     */
    List<T> getLasts();

    /**
     * 改变指定节点状态
     * @param vertex
     * @param status
     */
    void changeStatus(int vertex, JobStatus status);
}
