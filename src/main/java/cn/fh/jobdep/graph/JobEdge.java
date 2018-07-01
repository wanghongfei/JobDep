package cn.fh.jobdep.graph;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 图的一条边
 */
@Data
@AllArgsConstructor
public class JobEdge {
    private JobVertex from;

    private JobVertex to;

    public JobEdge reverse() {
        return new JobEdge(to, from);
    }
}
