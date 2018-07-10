package cn.fh.jobdep.task.store.mysql;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("cn.fh.jobdep.task.store.mysql.dao")
@ConditionalOnProperty(prefix = "jobdep", name = "task-store", havingValue = "mysql", matchIfMissing = false)
public class MybatisConfig {
}
