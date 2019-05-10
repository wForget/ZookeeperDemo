package cn.wangz.zookeeper.demo.client;

import org.apache.zookeeper.*;

import java.io.IOException;

/**
 * 删除节点
 *
 * 说明：
 * 1. 删除操作只能删除叶子节点，就是说只能删除最后一层路径，不能递归删除。
 */
public class DeleteDemo {
    private static ZooKeeper zooKeeper;

    public static void main(String[] args) throws Exception {
        init();

        // 删除节点
        delete();

        Thread.sleep(60000);
        zooKeeper.close();
    }

    /**
     * 参数说明：
     * path: 删除路径
     * version: 删除指定的版本， -1 表示所有的版本
     * cb: 异步时的回调函数，需要实现 AsyncCallback.VoidCallback 接口
     * ctx: 与回调函数传递上下文
     */
    private static void delete() throws KeeperException, InterruptedException {
        // 同步删除
        zooKeeper.delete("/zk-test-1", -1);

        // 异步删除
        zooKeeper.delete("/zk-test-2"
                , -1
                , new AsyncCallback.VoidCallback() {
                    public void processResult(int rc, String path, Object ctx) {
                        System.out.println("delete path result: [" + rc + ", " + path + ", " + ctx +"]");
                    }
                }
                ,"context");
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
