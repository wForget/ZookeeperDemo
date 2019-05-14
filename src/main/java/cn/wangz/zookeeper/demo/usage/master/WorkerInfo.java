package cn.wangz.zookeeper.demo.usage.master;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 工作节点注册信息
 */
@Getter
@Setter
@ToString
public class WorkerInfo implements Serializable {

    private String cid;
    private String name;

}
