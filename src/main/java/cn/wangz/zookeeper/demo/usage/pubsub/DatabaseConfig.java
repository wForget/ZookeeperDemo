package cn.wangz.zookeeper.demo.usage.pubsub;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DatabaseConfig {
    private String driverClass;
    private String uri;
    private String table;
}
