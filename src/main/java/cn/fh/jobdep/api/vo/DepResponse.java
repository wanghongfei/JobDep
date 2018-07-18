package cn.fh.jobdep.api.vo;

import lombok.Data;

@Data
public class DepResponse<T> {
    private int code = 0;

    private String msg = "succ";

    private T data;

    public DepResponse(T data) {
        this.data = data;
    }

    public DepResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
