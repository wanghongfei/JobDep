package cn.fh.jobdep.task.store;

import cn.fh.jobdep.graph.AdjTaskGraph;

public class DatabaseTaskStore implements TaskStore<AdjTaskGraph> {
    @Override
    public AdjTaskGraph getTaskGraph(Long taskId) {
        return null;
    }

    @Override
    public Long saveTask(AdjTaskGraph graph) {
        return null;
    }

    @Override
    public boolean updateTask(Long taskId, AdjTaskGraph graph) {
        return false;
    }
}
