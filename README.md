# JobDep

JobDep是一个支持任务拓扑依赖且专注于调度的系统，通过为每个`Task`建立一个由多个`Job`组成的有向无环图(DAG)来维护依赖关系，只有当所有前序任务都完成时才会触发后序任务。同时JobDep只负责调度不负责执行，`Job`的触发和通知都通过HTTP接口完成。



对于下图中的Task, JobDep会首先触发A, B, 全部完成后触发C, 再触发D, E, 最后触发F。

![](http://ovbyjzegm.bkt.clouddn.com/jobdep-job.png)