package cn.wangz.zookeeper.demo.client;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;

/**
 * 获取子节点信息和获取节点数据
 *
 * getChildren、getData 参数说明：
 * path: 路径
 * watcher: 注册 Watcher。在获取节点时注册 Watcher，则节点发送变化时，就会向客户端发送通知。
 * watch: 是否需要注册 Watcher。在创建客户端的时候可以设置默认的 Watcher，如果设置为 true 的话，则注册默认的 Watcher，false 则不注册 Watcher。
 * cb: 异步回调函数
 * ctx: 传递上下文信息
 * stat: 指定数据节点的状态信息。用法是在获取节点时传入一个 stat 变量，该 stat 变量会在方法执行的过程时，被来自服务端响应填充 stat 对象，同步获取时使用，异步获取时 callback 中有stat参数。
 *
 * 说明：
 * 1、在 Watcher 中，服务端发送给客户端的通知中不包含最新节点的信息，需要重新获取。
 * 2、getChildren 方法返回的子节点的路径是相对 path 的路径。
 * 3、Watcher 的通知是一次性的，在获取通知后 Watcher 失效，需要反复注册 Watcher。
 *
 * 其他：Watcher通知状态和事件类型.md
 */
public class GetDemo {
    private static ZooKeeper zooKeeper;

    public static void main(String[] args) throws Exception {
        init();

        // 获取子节点信息
        getChildren();

        // 获取节点数据
        getData();

        Thread.sleep(60000);
        zooKeeper.close();
    }

    private static void getChildren() throws KeeperException, InterruptedException {
        zooKeeper.create("/zk-test"
                , "".getBytes()
                , ZooDefs.Ids.OPEN_ACL_UNSAFE
                , CreateMode.PERSISTENT);

        zooKeeper.create("/zk-test/c1"
                , "".getBytes()
                , ZooDefs.Ids.OPEN_ACL_UNSAFE
                , CreateMode.EPHEMERAL);

        final Stat stat = new Stat();
        // 同步获取
        List<String> childrens = zooKeeper.getChildren("/zk-test"
                , new Watcher() {
                    public void process(WatchedEvent event) {
                        System.out.println(event);
                        if (event.getState() == Event.KeeperState.SyncConnected) {
                            if (event.getType() == Event.EventType.NodeChildrenChanged) {
                                try {
                                    System.out.println(zooKeeper.getChildren("/zk-test", false, stat));
                                    System.out.println("get change children stat:" + stat);
                                } catch (Exception e) {

                                }
                            }
                        }
                    }
                }, stat);
        System.out.println(childrens);
        System.out.println("get children stat: " + stat);

        // 异步获取
        zooKeeper.getChildren("/zk-test"
                , new Watcher() {
                    public void process(WatchedEvent event) {
                        System.out.println(event);
                        if (event.getState() == Event.KeeperState.SyncConnected) {
                            if (event.getType() == Event.EventType.NodeChildrenChanged) {
                                try {
                                    zooKeeper.getChildren("/zk-test"
                                            , false
                                            , new AsyncCallback.Children2Callback() {
                                                public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
                                                    System.out.println("ctx: " + ctx + ", childrens: " + children);
                                                    System.out.println("callback get children stat: " + stat);
                                                }
                                            }
                                            , "change callback");
                                } catch (Exception e) {

                                }
                            }
                        }
                    }
                }, new AsyncCallback.Children2Callback() {
                    public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
                        System.out.println("ctx: " + ctx + ", childrens: " + children);
                        System.out.println("callback get children stat: " + stat);
                    }
                }, "callback");


        zooKeeper.create("/zk-test/c2"
                , "".getBytes()
                , ZooDefs.Ids.OPEN_ACL_UNSAFE
                , CreateMode.EPHEMERAL);
    }

    private static void getData() throws KeeperException, InterruptedException {
        zooKeeper.create("/zk-test-ephemeral"
                , "test-data".getBytes()
                , ZooDefs.Ids.OPEN_ACL_UNSAFE
                , CreateMode.EPHEMERAL);

        final Stat stat = new Stat();
        // 同步获取
        byte[] data = zooKeeper.getData("/zk-test-ephemeral"
                , new Watcher() {
                    public void process(WatchedEvent event) {
                        System.out.println(event);
                        if (event.getState() == Event.KeeperState.SyncConnected) {
                            if (event.getType() == Event.EventType.NodeDataChanged) {
                                try {
                                    final Stat changeStat = new Stat();
                                    byte[] changeData = zooKeeper.getData("/zk-test-ephemeral", false, changeStat);
                                    System.out.println("change data: " + new String(changeData));
                                    System.out.println("change data version: " + changeStat.getVersion());
                                    System.out.println("get change data stat: " + changeStat);
                                } catch (Exception e) {

                                }
                            }
                        }
                    }
                }, stat);
        System.out.println("data: " + new String(data));
        System.out.println("data version: " + stat.getVersion());
        System.out.println("get data stat: " + stat);

        // 异步获取
        zooKeeper.getData("/zk-test-ephemeral"
                , new Watcher() {
                    public void process(WatchedEvent event) {
                        System.out.println(event);
                        if (event.getState() == Event.KeeperState.SyncConnected) {
                            if (event.getType() == Event.EventType.NodeDataChanged) {
                                try {
                                    zooKeeper.getData("/zk-test-ephemeral", false, new AsyncCallback.DataCallback() {
                                        public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                                            System.out.println("ctx: " + ctx + ", data: " + new String(data));
                                            System.out.println("change data version: " + stat.getVersion());
                                            System.out.println("get change data stat: " + stat);
                                        }
                                    }, "callback change getData");
                                } catch (Exception e) {

                                }
                            }
                        }
                    }
                }, new AsyncCallback.DataCallback() {
                    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                        System.out.println("ctx: " + ctx + ", data: " + new String(data));
                        System.out.println("data version: " + stat.getVersion());
                        System.out.println("get data stat: " + stat);
                    }
                }, "callback getData");


        zooKeeper.setData("/zk-test-ephemeral"
                , "test-data-changed".getBytes()
                , stat.getVersion());
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
