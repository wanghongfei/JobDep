package cn.fh.jobdep.graph;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Job为Task的一个步骤, 在图中对应为顶点
 */
@Data
public class JobVertex implements Vertex {
    private Integer index;

    private String name;

    private String triggerUrl;

    private String notifyUrl;

    private volatile JobStatus status = JobStatus.NEW;

    private List<JobVertex> toList = new ArrayList<>();

    public JobVertex(Integer index, String triggerUrl, String notifyUrl) {
        this.index = index;
        this.triggerUrl = triggerUrl;
        this.notifyUrl = notifyUrl;
    }

    public JobVertex(Integer index, String name) {
        this.index = index;
        this.name = name;
    }

    public JobVertex() {

    }

    protected void addToVertex(JobVertex vertex) {
        toList.add(vertex);
    }

    @Override
    public String toString() {
        // return index + "," + triggerUrl + "," + notifyUrl;
        return index + "";
    }

    @Override
    public JobVertex clone() {
        JobVertex vertex = new JobVertex(index, triggerUrl, notifyUrl);

        return vertex;
    }
}
