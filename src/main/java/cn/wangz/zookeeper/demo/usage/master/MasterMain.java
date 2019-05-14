package cn.wangz.zookeeper.demo.usage.master;

import java.util.ArrayList;
import java.util.List;

/**
 * Master 选举的例子
 *
 * 工作服务器启动时，会去ZooKeeper的Servers节点下创建临时节点，并把基本信息写入临时节点，这个过程叫服务注册。
 * 系统中的其他服务可以通过获取Servers节点的子节点列表，来了解当前系统哪些服务器可用，这该过程叫做服务发现。
 * 接着这些服务器会尝试创建Master临时节点，谁创建成功谁就是Master，其他的两台就作为Slave。
 * 所有的Work Server必需关注Master节点的删除事件。
 * 通过监听Master节点的删除事件，来了解Master服务器是否宕机（创建临时节点的服务器一旦宕机，它所创建的临时节点即会自动删除）。
 * 一旦Master服务器宕机，必需开始新一轮的Master选举。
 */
public class MasterMain {
    public static void main(String[] args) throws Exception {
        List<WorkerServer> servers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            WorkerServer server = new WorkerServer("Server-" + i);
            servers.add(server);
            server.start();
        }

        Thread.sleep(300000);

        for (WorkerServer server: servers) {
            try {
                server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
