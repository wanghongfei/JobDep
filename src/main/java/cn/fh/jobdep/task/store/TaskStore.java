package cn.fh.jobdep.task.store;

import cn.fh.jobdep.graph.Graph;

public interface TaskStore<T extends Graph> {
    /**
     * 根据id查询task
     * @param taskId
     * @return
     */
    T getTaskGraph(Long taskId);

    /**
     * 保存task
     * @param graph
     */
    Long saveTask(T graph);
}
