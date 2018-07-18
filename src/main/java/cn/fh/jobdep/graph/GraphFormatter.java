package cn.fh.jobdep.graph;

public interface GraphFormatter<T> {
    T format(AdjTaskGraph graph);
}
