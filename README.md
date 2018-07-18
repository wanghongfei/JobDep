# JobDep

JobDep是一个支持任务拓扑依赖且专注于调度的系统，通过为每个`Task`建立一个由多个`Job`组成的有向无环图(DAG)来维护依赖关系，只有当所有前序任务都完成时才会触发后序任务。同时JobDep只负责调度不负责执行，`Job`的触发和通知都通过HTTP接口完成，通知时会在请求参数中携带所有前序任务的返回结果。



JobDep与任务执行节点之间的关系如下：

![](http://ovbyjzegm.bkt.clouddn.com/job-action2.jpg)





对于下图中的Task, JobDep会首先触发A, B, 全部完成后触发C, 再触发D, E, 最后触发F。

![](http://ovbyjzegm.bkt.clouddn.com/jobdep-job.png)



## 集群

JobDep可以集群部署，实例相互之间无影响，访问任意一个实例均可提交、通知任务。JobDep会将Task状态信息用一张表保存到MySQL中, 使用数据库事务实现多线程、多实例对状态的安全访问。



## 任务定义

任务通过`yaml`文件定义:

```yaml
# job名, 任意
job0:
  # 触发URL
  triggerUrl: "http://job0.com/trigger"
  # 以下job依赖job0
  next:
    - job2
    - job3

job1:
  triggerUrl: "http://job1.com/trigger"
  next:
    - job3

job2:
  triggerUrl: "http://job2.com/trigger"
  next:
    - job4

job3:
  triggerUrl: "http://job3.com/trigger"
  next:
    - job5

job4:
  triggerUrl: "http://job4.com/trigger"
  next:
    - job6

job5:
  triggerUrl: "http://job5.com/trigger"
  next:
    - job6

job6:
  triggerUrl: "http://job6.com/trigger"
  # job6完成时通知Task成功的地址
  notifyUrl: "http://job6.com/notify"
```

上面的配置定义了如下拓扑结构:

![](http://ovbyjzegm.bkt.clouddn.com/job-dag.jpg)

任务依赖拓扑有两个限制：

- 不能有环
- 整个Task最终都要收敛于一个Job, 即必须有且只有一个最终Job, 同时此job必须有`notifyUrl`属性

如果不符合要求，提交任务时会返回错误信息。