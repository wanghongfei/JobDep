package cn.fh.jobdep.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * 访问所有顶点(job)
 */
public class JobFormatter implements GraphFormatter<List<JobVertex>> {
    @Override
    public List<JobVertex> format(AdjTaskGraph graph) {
        return new ArrayList<>(graph.vertexMap.values());
    }
}
