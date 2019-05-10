package cn.wangz.zookeeper.demo.client;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

/**
 * 创建 zookeeper 实例
 */
public class ConstructDemo {

    /**
     * 参数说明：
     * connectString:   zookeeper 服务器地址
     * sessionTimeout:  会话超时时间，毫秒为单位。在一个会话周期内，Zookeeper 客户端和服务端通过心跳检测机制维持会话的有效性。
     * watcher:         设置默认的 Watcher 事件通知处理器
     * canBeReadOnly:   是否支持"read-only(只读)"模式。默认情况下，在ZooKeeper集群中，一个机器如果和集群中过半以上机器失去了网络连接，那么这个机器将不再处理客户端请求（包括读写请求）。
     *                  但是在某些使用场景下，当ZooKeeper服务器发生此类故障的时候，我们还是希望ZooKeeper服务器能够提供读服务（当然写服务肯定无法提供）——这就是ZooKeeper的“read-only”模式。
     * sessionId/sessionPasswd  分别代表会话ID和会话秘钥。
     *                          这两个参数能够唯一确定一个会话，同时客户端使用这两个参数实现客户端会话复用，从而达到恢复会话的效果，具体使用方法是，第一次连接上ZooKeeper服务器时，
     *                          通过调用ZooKeeper对象实现的一下两个接口，即可获得当前会话的ID和秘钥：
     *                              long getSessionId(); byte[] getSessionPasswd();
     *                          获取到这两个参数值后，就可以在下次创建ZooKeeper对象实例的时候传入构造方法了。
     */
    public static void main(String[] args) throws Exception {
        String connectString = "dmp-test01:2181,dmp-test02:2181,dmp-test03:2181";
        int sessionTimeout = 5000;
        Watcher watcher = new Watcher() {
            public void process(WatchedEvent event) {
                // do somethings
            }
        };

        ZooKeeper zooKeeper1 = new ZooKeeper(connectString, sessionTimeout, watcher);

        boolean canBeReadOnly = true;
        ZooKeeper zooKeeper2 = new ZooKeeper(connectString, sessionTimeout, watcher, canBeReadOnly);

        long sessionId = zooKeeper2.getSessionId();
        byte[] sessionPasswd = zooKeeper2.getSessionPasswd();
        ZooKeeper zooKeeper3 = new ZooKeeper(connectString, sessionTimeout, watcher, sessionId, sessionPasswd);

        ZooKeeper zooKeeper4 = new ZooKeeper(connectString, sessionTimeout, watcher, sessionId, sessionPasswd, canBeReadOnly);
    }


}
