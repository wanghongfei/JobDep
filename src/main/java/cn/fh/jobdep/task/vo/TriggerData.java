package cn.fh.jobdep.task.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TriggerData {
    private String jobName;
    private String body;
}
