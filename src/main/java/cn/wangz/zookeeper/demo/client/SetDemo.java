package cn.wangz.zookeeper.demo.client;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

/**
 * 设置节点数据
 *
 * 参数说明：
 * path: 路径
 * data[]: 数据，字节数组
 * version: 数据版本
 * cb: 异步回调函数
 * ctx: 传递上下文信息
 *
 * 说明:
 * 1、setData 中需要指定 version，-1 表示匹配任意版本。如果设置的 version 跟节点当前的版本不匹配，则会抛出 KeeperException$BadVersionException 异常
 */
public class SetDemo {
    private static ZooKeeper zooKeeper;

    public static void main(String[] args) throws Exception {
        init();

        // 更新数据
        setData();

        Thread.sleep(30000);
        zooKeeper.close();
    }

    private static void setData() throws KeeperException, InterruptedException {
        zooKeeper.create("/zk-test-ephemeral"
                , "test-data".getBytes()
                , ZooDefs.Ids.OPEN_ACL_UNSAFE
                , CreateMode.EPHEMERAL);

        // 同步更新
        Stat stat = zooKeeper.setData("/zk-test-ephemeral"
                ,"test-data-setdata1".getBytes()
                , -1);
        System.out.println(stat);

        // 异步更新
        zooKeeper.setData("/zk-test-ephemeral"
                ,"test-data-callback-setdata1".getBytes()
                , -1
                , new AsyncCallback.StatCallback() {
                    public void processResult(int rc, String path, Object ctx, Stat stat) {
                        System.out.println("ctx: " + ctx + ", stat: " + stat);
                    }
                }, "set data");

        // 指定错误版本更新 抛出 KeeperException$BadVersionException 异常
        // org.apache.zookeeper.KeeperException$BadVersionException: KeeperErrorCode = BadVersion for /zk-test-ephemeral
        try {
            Stat stat2 = zooKeeper.setData("/zk-test-ephemeral"
                    ,"test-data-setdata2".getBytes()
                    , 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
