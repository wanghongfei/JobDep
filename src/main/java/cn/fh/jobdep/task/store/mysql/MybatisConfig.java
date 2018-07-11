package cn.fh.jobdep.task.store.mysql;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("cn.fh.jobdep.task.store.mysql.dao")
public class MybatisConfig {
}
