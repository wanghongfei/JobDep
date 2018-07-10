package cn.fh.jobdep.task.store.mysql;

import cn.fh.jobdep.graph.AdjTaskGraph;
import cn.fh.jobdep.graph.JobStatus;
import cn.fh.jobdep.task.store.TaskStore;
import cn.fh.jobdep.task.store.mysql.dao.TaskModelMapper;
import cn.fh.jobdep.task.store.mysql.dao.model.TaskModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@ConditionalOnProperty(prefix = "jobdep", name = "task-store", havingValue = "mysql", matchIfMissing = false)
public class DatabaseTaskStore implements TaskStore<AdjTaskGraph> {
    @Autowired
    private TaskModelMapper taskMapper;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public AdjTaskGraph getTaskGraph(Long taskId) {
        TaskModel model = taskMapper.selectByPrimaryKey(taskId);

        return AdjTaskGraph.fromJson(model.getGraph());
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Long saveTask(AdjTaskGraph graph) {
        TaskModel record = new TaskModel();
        record.setGraph(graph.toJson());
        record.setStatus(JobStatus.NEW.code());

        taskMapper.insertSelective(record);

        return record.getId();
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean updateTask(Long taskId, AdjTaskGraph graph) {
        TaskModel record = new TaskModel();
        record.setId(taskId);
        record.setGraph(graph.toJson());
        record.setStatus(graph.getTaskStatus().code());

        return taskMapper.updateByPrimaryKeySelective(record) > 0;
    }
}
