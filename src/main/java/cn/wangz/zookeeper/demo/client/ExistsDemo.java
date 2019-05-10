package cn.wangz.zookeeper.demo.client;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

/**
 * 检测节点是否存在
 *
 * 参数说明：
 * path: 路径
 * watcher: 注册 Watcher。监听：节点是否被创建、节点是否被删除、节点是否被更新
 * watch: 是否服用创建 zookeeper 时，默认的 Watcher
 * cb: 异步回调函数
 * ctx: 传递上下文信息
 *
 * 说明：
 * 1、不管节点是否存在都可以使用 exists 方法注册 Watcher。
 * 2、exists 注册的 Watcher 监听：节点是否被创建、节点是否被删除、节点是否被更新
 * 3、子节点的变化不会发送通知
 * 4、节点不存在，返回的 stat 为空
 */
public class ExistsDemo {

    private static ZooKeeper zooKeeper;

    public static void main(String[] args) throws Exception {
        init();

        // 更新数据
        exists();

        Thread.sleep(30000);
        zooKeeper.close();
    }

    private static void exists() throws KeeperException, InterruptedException {
        zooKeeper.create("/zk-test-ephemeral"
                , "test-data".getBytes()
                , ZooDefs.Ids.OPEN_ACL_UNSAFE
                , CreateMode.EPHEMERAL);

        // 同步方法
        // 节点存在
        Stat stat = zooKeeper.exists("/zk-test-ephemeral"
                ,false);
        System.out.println("exists stat: " + stat);
        // 节点不存在
        Stat notexiststat = zooKeeper.exists("/zk-test-ephemeral-not-exists"
                ,false);
        System.out.println("not exists stat: " + notexiststat);

        // 异步方法
        // 节点存在
        zooKeeper.exists("/zk-test-ephemeral"
                , false
                , new AsyncCallback.StatCallback() {
                    public void processResult(int rc, String path, Object ctx, Stat stat) {
                        System.out.println("ctx: " + ctx + ", stat: " + stat );
                    }
                }, "exists callback");
        // 节点不存在
        zooKeeper.exists("/zk-test-ephemeral-not-exists"
                , false
                , new AsyncCallback.StatCallback() {
                    public void processResult(int rc, String path, Object ctx, Stat stat) {
                        System.out.println("ctx: " + ctx + ", stat: " + stat );
                    }
                }, "exists callback");
    }

    private static void init() throws IOException {
        String connectString = "dmp-test01:2181,dmp-test02:2181,dmp-test03:2181";
        int sessionTimeout = 5000;
        Watcher watcher = new Watcher() {
            public void process(WatchedEvent event) {
                // do somethings
            }
        };
        zooKeeper = new ZooKeeper(connectString, sessionTimeout, watcher);
    }
}
