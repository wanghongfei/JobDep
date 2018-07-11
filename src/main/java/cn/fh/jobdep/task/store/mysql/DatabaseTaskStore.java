package cn.fh.jobdep.task.store.mysql;

import cn.fh.jobdep.graph.AdjTaskGraph;
import cn.fh.jobdep.graph.JobStatus;
import cn.fh.jobdep.task.store.TaskStore;
import cn.fh.jobdep.task.store.mysql.dao.TaskModelMapper;
import cn.fh.jobdep.task.store.mysql.dao.model.TaskModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseTaskStore implements TaskStore<AdjTaskGraph> {
    @Autowired
    private TaskModelMapper taskMapper;

    @Override
    public AdjTaskGraph getTaskGraph(Long taskId) {
        TaskModel model = taskMapper.selectByPrimaryKey(taskId);

        return AdjTaskGraph.fromJson(model.getGraph());
    }

    @Override
    public Long saveTask(AdjTaskGraph graph) {
        TaskModel record = new TaskModel();
        record.setGraph(graph.toJson());
        record.setStatus(JobStatus.NEW.code());

        taskMapper.insertSelective(record);

        return record.getId();
    }

    @Override
    public boolean updateTask(Long taskId, AdjTaskGraph graph, String msg) {
        TaskModel record = new TaskModel();
        record.setId(taskId);
        record.setGraph(graph.toJson());
        record.setStatus(graph.getTaskStatus().code());
        record.setMessage(msg);

        return taskMapper.updateByPrimaryKeySelective(record) > 0;
    }
}
