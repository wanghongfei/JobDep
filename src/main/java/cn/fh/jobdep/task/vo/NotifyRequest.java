package cn.fh.jobdep.task.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotifyRequest {
    private int code;

    private Object data;
}
