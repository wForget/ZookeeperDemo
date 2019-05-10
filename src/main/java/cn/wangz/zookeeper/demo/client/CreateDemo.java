package cn.wangz.zookeeper.demo.client;

import org.apache.zookeeper.*;

import java.io.IOException;

/**
 * 创建节点
 *
 * 说明：
 * 1. zookeeper 不支持递归创建，不能父节点不存在的时候创建节点
 * 2. 如果一个节点已经存在的时候会抛出 NodeExistsException 异常
 */
public class CreateDemo {
    private static ZooKeeper zooKeeper;

    public static void main(String[] args) throws Exception {
        init();

        // 创建节点
        create();

        Thread.sleep(60000);
        zooKeeper.close();
    }

    /**
     * 创建节点
     * 相关参数说明：
     * path: 节点路径
     * data[]: 节点数据，byte[]
     * acl: ACL 策略
     * createMode: 节点类型。持久(PERSISTENT)、持久顺序(PERSISTENT_SEQUENTIAL)、临时(EPHEMERAL)、临时顺序(EPHEMERAL_SEQUENTIAL)
     * cb: 回调函数。 实现 AsyncCallback 接口的 processResult 方法，processResult 参数说明：
     *              rc: result code，服务端响应码。0: 成功， -4: 客户端和服务端端口连接，-110: 节点已存在，-112: 会话过期
     *              path: 调用方法时，传入的 path
     *              ctx: 调用方法时传入的 ctx
     *              name: 实际在服务端创建节点的名称。
     * ctx: 与回调函数传递信息的上下文对象。
     */
    private static void create() throws KeeperException, InterruptedException {

        // 创建持久节点
        zooKeeper.create("/zk-test-1"
                , "".getBytes()
                , ZooDefs.Ids.OPEN_ACL_UNSAFE
                , CreateMode.PERSISTENT);
        zooKeeper.create("/zk-test-2"
                , "".getBytes()
                , ZooDefs.Ids.OPEN_ACL_UNSAFE
                , CreateMode.PERSISTENT);

        // 创建临时顺序节点
        zooKeeper.create("/zk-test-ephemeral-"
                , "".getBytes()
                , ZooDefs.Ids.OPEN_ACL_UNSAFE
                , CreateMode.EPHEMERAL_SEQUENTIAL);

        // 异步创建节点
        zooKeeper.create("/zk-test-ephemeral-"
                , "".getBytes()
                , ZooDefs.Ids.OPEN_ACL_UNSAFE
                , CreateMode.EPHEMERAL
                , new AsyncCallback.StringCallback() {
                    public void processResult(int rc, String path, Object ctx, String name) {
                        System.out.println("Create path result: [" + rc + ", " + path + ", " + ctx +"], real path name: " + name);
                    }
                }
                , "i am context");
        zooKeeper.create("/zk-test-ephemeral-"
                , "".getBytes()
                , ZooDefs.Ids.OPEN_ACL_UNSAFE
                , CreateMode.EPHEMERAL
                , new AsyncCallback.StringCallback() {
                    public void processResult(int rc, String path, Object ctx, String name) {
                        System.out.println("Create path result: [" + rc + ", " + path + ", " + ctx +"], real path name: " + name);
                    }
                }
                , "i am context");
        zooKeeper.create("/zk-test-ephemeral-"
                , "".getBytes()
                , ZooDefs.Ids.OPEN_ACL_UNSAFE
                , CreateMode.EPHEMERAL_SEQUENTIAL
                , new AsyncCallback.StringCallback() {
                    public void processResult(int rc, String path, Object ctx, String name) {
                        System.out.println("Create path result: [" + rc + ", " + path + ", " + ctx +"], real path name: " + name);
                    }
                }
                , "i am context");
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
