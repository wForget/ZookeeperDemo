package cn.wangz.zookeeper.demo.usage.pubsub;

import com.alibaba.fastjson.JSONObject;
import org.apache.zookeeper.*;

import java.io.IOException;

/**
 * Zookeeper 作为配置中心的例子
 * 发布订阅(Publish/Subscribe)系统的实现
 */
public class ConfigPubSubMain {

    public static void main(String[] args) throws Exception {
        String path = "/zk-db-config";
        DatabaseConfig config = new DatabaseConfig();
        config.setDriverClass("com.mysql.jdbc.Driver");
        config.setUri("jdbc:mysql://localhost:3306/samp_db");
        config.setTable("test");

        init(path, config);

        Thread thread1 = new Thread(new Client("Client1", path), "Client1");
        thread1.start();

        Thread thread2 = new Thread(new Client("Client2", path), "Client1");
        thread2.start();

        Thread.sleep(10000);
        config.setTable("online1");
        changeConfig(path, config);

        Thread.sleep(10000);
        config.setTable("online2");
        changeConfig(path, config);

        Thread.sleep(10000);
        config.setTable("online3");
        changeConfig(path, config);

        thread1.join();
        thread2.join();
        clear(path);
    }

    private static void init(String path, DatabaseConfig config) throws IOException, KeeperException, InterruptedException {
        String connectString = "dmp-test01:2181,dmp-test02:2181,dmp-test03:2181";
        int sessionTimeout = 5000;
        Watcher watcher = new Watcher() {
            public void process(WatchedEvent event) {
                // do nothings
            }
        };
        ZooKeeper zooKeeper = new ZooKeeper(connectString, sessionTimeout, watcher);
        zooKeeper.create(path
                , JSONObject.toJSONBytes(config)
                , ZooDefs.Ids.OPEN_ACL_UNSAFE
                , CreateMode.PERSISTENT);
        zooKeeper.close();
    }

    private static void changeConfig(String path, DatabaseConfig config) throws IOException, KeeperException, InterruptedException {
        String connectString = "dmp-test01:2181,dmp-test02:2181,dmp-test03:2181";
        int sessionTimeout = 5000;
        Watcher watcher = new Watcher() {
            public void process(WatchedEvent event) {
                // do nothings
            }
        };
        ZooKeeper zooKeeper = new ZooKeeper(connectString, sessionTimeout, watcher);
        zooKeeper.setData(path
                , JSONObject.toJSONBytes(config)
                , -1);
        zooKeeper.close();
    }

    private static void clear(String path) throws IOException, KeeperException, InterruptedException {
        String connectString = "dmp-test01:2181,dmp-test02:2181,dmp-test03:2181";
        int sessionTimeout = 5000;
        Watcher watcher = new Watcher() {
            public void process(WatchedEvent event) {
                // do nothings
            }
        };
        ZooKeeper zooKeeper = new ZooKeeper(connectString, sessionTimeout, watcher);
        zooKeeper.delete(path
                , -1);
        zooKeeper.close();
    }
}
