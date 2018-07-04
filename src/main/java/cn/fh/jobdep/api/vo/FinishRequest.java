package cn.fh.jobdep.api.vo;

import lombok.Data;

@Data
public class FinishRequest {
    private Integer jobId;

    private Long taskId;

    private Boolean success;

    private String result;
}
