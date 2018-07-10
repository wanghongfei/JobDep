package cn.fh.jobdep.graph;

public enum JobStatus {
    NEW(0),
    RUNNING(1),
    FINISHED(2),
    FAILED(3);

    private int code;

    JobStatus(int code) {
        this.code = code;
    }

    public int code() {
        return this.code;
    }
}
