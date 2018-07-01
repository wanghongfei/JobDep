package cn.fh.jobdep.graph;

/**
 * 顶点操作
 */
public interface Vertex {
    Integer getIndex();

    String getName();

    JobStatus getStatus();

    String getTriggerUrl();

    String getNotifyUrl();
}
