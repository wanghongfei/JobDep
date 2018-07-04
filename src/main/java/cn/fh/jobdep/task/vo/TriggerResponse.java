package cn.fh.jobdep.task.vo;

import lombok.Data;

@Data
public class TriggerResponse<T> {
    private int code;

    private T data;
}
