package cn.fh.jobdep.api;

import cn.fh.jobdep.api.vo.DepResponse;

public class BaseController {
    protected DepResponse<Object> succResp = new DepResponse<>(0, "succ");

}
