package cn.fh.jobdep.task.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TriggerRequest {
    private Integer jobId;

    private List<TriggerData> dataList;
}
