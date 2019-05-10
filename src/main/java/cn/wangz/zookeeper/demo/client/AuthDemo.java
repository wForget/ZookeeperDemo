package cn.wangz.zookeeper.demo.client;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

/**
 * 权限控制
 *
 * addAuthInfo 参数说明:
 * scheme: 权限控制模式：world、auth、digest、ip、super
 * auth: 权限信息
 *
 * 说明：
 * 1、使用权限控制时，需要注意创建节点时的 ACL 设置。
 * 2、没有权限时访问，抛出 KeeperException$NoAuthException 异常， KeeperErrorCode = NoAuth。
 */
public class AuthDemo {
    private static ZooKeeper authZooKeeper;
    private static ZooKeeper zooKeeper;
    private static ZooKeeper zooKeeper2;

    public static void main(String[] args) throws Exception {
        init();

        // check
        check();

        Thread.sleep(30000);
        zooKeeper.close();
        zooKeeper2.close();
        authZooKeeper.close();
    }

    private static void check() throws KeeperException, InterruptedException {
        authZooKeeper.create("/zk-test-ephemeral"
                , "test-data".getBytes()
                , ZooDefs.Ids.CREATOR_ALL_ACL   // 此ACL提供创建者身份验证id的所有权限。
                , CreateMode.EPHEMERAL);

        // 使用没有身份验证的客户端访问，抛出 KeeperException$NoAuthException 异常
        // org.apache.zookeeper.KeeperException$NoAuthException: KeeperErrorCode = NoAuth for /zk-test-ephemeral
        Stat stat = new Stat();
        try {
            byte[] data = zooKeeper.getData("/zk-test-ephemeral", false, stat);
            System.out.println("not auth data: " + new String(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(stat);

        // 使用另外一个权限认证的客户端访问
        Stat stat2 = new Stat();
        try {
            byte[] data = zooKeeper2.getData("/zk-test-ephemeral", false, stat2);
            System.out.println("auth data: " + new String(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(stat2);
    }

    private static void init() throws IOException {
        String connectString = "dmp-test01:2181,dmp-test02:2181,dmp-test03:2181";
        int sessionTimeout = 5000;
        Watcher watcher = new Watcher() {
            public void process(WatchedEvent event) {
                // do somethings
            }
        };
        authZooKeeper = new ZooKeeper(connectString, sessionTimeout, watcher);
        // 添加权限
        authZooKeeper.addAuthInfo("digest", "username:password".getBytes());

        zooKeeper = new ZooKeeper(connectString, sessionTimeout, watcher);

        zooKeeper2 = new ZooKeeper(connectString, sessionTimeout, watcher);
        zooKeeper2.addAuthInfo("digest", "username:password".getBytes());
    }
}
