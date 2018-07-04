package cn.fh.jobdep.task.store;

public interface TaskStore<T> {
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

    boolean updateTask(Long taskId, T graph);
}
