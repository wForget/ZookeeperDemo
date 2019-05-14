package cn.wangz.zookeeper.demo.usage.pubsub;

import com.alibaba.fastjson.JSONObject;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

public class Client implements Runnable {
    private String name;
    private ZooKeeper zooKeeper;
    private Stat stat;
    private DatabaseConfig config;
    private String zkConfigPath;

    public Client(String name, String zkConfigPath) throws IOException {
        this.name = name;
        this.stat = new Stat();
        this.zooKeeper = new ZooKeeper("dmp-test01:2181,dmp-test02:2181,dmp-test03:2181"
                , 5000
                , new ConfigChangedWathcer());
        this.zkConfigPath = zkConfigPath;
        initConfig(zkConfigPath);
    }

    private void initConfig(String path) {
        try {
            byte[] data = zooKeeper.getData(path, true, stat);
            config = JSONObject.parseObject(data, DatabaseConfig.class);
            System.out.println("init config, client: " + name + ", config: " + config);
            System.out.println("stat: " + stat);
        } catch (Exception e) {
            e.printStackTrace();
            // exception process
        }
    }

    private class ConfigChangedWathcer implements Watcher {
        @Override
        public void process(WatchedEvent event) {
            if (event.getState() == Event.KeeperState.SyncConnected) {
                if (event.getType() == Event.EventType.NodeDataChanged) {
                    initConfig(event.getPath());
                }
            }
        }
    }

    @Override
    public void run() {
        System.out.println("client: " + name + " start run!!!\n");
        try {
            Thread.sleep(60000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("client: " + name + " end run!!!\n");
    }
}
