package cn.fh.jobdep.task.store;

import cn.fh.jobdep.graph.AdjTaskGraph;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
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
