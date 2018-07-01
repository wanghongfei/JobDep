package cn.fh.jobdep.graph;

import cn.fh.jobdep.task.JobStatus;

/**
 * 顶点操作
 */
public interface Vertex {
    Integer getIndex();

    JobStatus getStatus();

    String getTriggerUrl();

    String getNotifyUrl();
}
