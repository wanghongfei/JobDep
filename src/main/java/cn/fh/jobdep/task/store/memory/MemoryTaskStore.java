package cn.fh.jobdep.task.store.memory;

import cn.fh.jobdep.graph.AdjTaskGraph;
import cn.fh.jobdep.task.store.TaskStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
@ConditionalOnProperty(prefix = "jobdep", name = "task-store", havingValue = "memory", matchIfMissing = true)
public class MemoryTaskStore implements TaskStore<AdjTaskGraph> {
    private static Map<Long, AdjTaskGraph> taskMap = new HashMap<>();

    private AtomicLong aLong = new AtomicLong(0);

    @Override
    public AdjTaskGraph getTaskGraph(Long taskId) {
        return taskMap.get(taskId);
    }

    @Override
    public Long saveTask(AdjTaskGraph graph) {
        Long id = aLong.getAndIncrement();
        taskMap.put(id, graph);

        return id;
    }

    @Override
    public boolean updateTask(Long taskId, AdjTaskGraph graph) {
        return false;
    }
}
