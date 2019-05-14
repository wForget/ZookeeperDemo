package cn.wangz.zookeeper.demo.usage.master;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.apache.zookeeper.CreateMode;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 工作服务器
 */
public class WorkerServer {

    private ZkClient zkClient;
    // Master节点对应zookeeper中的节点路径
    private static final String MASTER_PATH = "/wz-master";
    // 监听Master节点删除事件
    private IZkDataListener dataListener;
    // 当前节点信息
    private WorkerInfo workerInfo;
    // 记录集群中 Master 节点的基本信息
    private WorkerInfo masterInfo;

    private AtomicBoolean running;

    private ScheduledExecutorService delayExector = Executors.newScheduledThreadPool(1);
    private int delayTime = 5;

    public WorkerServer(String name) {
        this.workerInfo = new WorkerInfo();
        this.workerInfo.setCid(UUID.randomUUID().toString());
        this.workerInfo.setName(name);
        // 创建zkClient
        this.zkClient = new ZkClient("dmp-test01:2181,dmp-test02:2181,dmp-test03:2181", 5000, 5000, new SerializableSerializer());
        // 监听Master节点删除事件
        this.dataListener = new IZkDataListener() {
            @Override
            public void handleDataDeleted(String s) throws Exception {
                System.out.println("master deleted, will take master! workerInfo: " + WorkerServer.this.workerInfo);
                if (masterInfo != null && masterInfo.getName().equals(workerInfo.getName())) {
                    // 自己是之前的 master 直接抢占 master
                    takeMaster();
                } else {    // 不是之前的 master 延迟 5s 后抢占 master。给上一轮的Master服务器优先抢占 master 的权利，避免不必要的数据迁移开销
                    delayExector.schedule(() -> takeMaster(), 5, TimeUnit.SECONDS);
                }
            }

            @Override
            public void handleDataChange(String s, Object o) throws Exception {

            }
        };
        this.running = new AtomicBoolean(false);
    }

    public void start() throws Exception {
        if (!this.running.compareAndSet(false, true)) {   // 已经启动
            throw new Exception("WorkerServer already started, WorkerInfo: " + this.workerInfo);
        }
        // 注册 master 节点删除监听
        zkClient.subscribeDataChanges(MASTER_PATH, this.dataListener);
        // 抢占 master
        takeMaster();
    }

    public void stop() throws Exception {
        if (!this.running.compareAndSet(true, false)) {   // 已经关闭
            throw new Exception("WorkerServer already stopped, WorkerInfo: " + this.workerInfo);
        }
        if (this.delayExector != null) {
            this.delayExector.shutdown();
        }
        if (this.zkClient != null) {
            this.zkClient.close();
        }
    }

    private void takeMaster() {
        if (!this.running.get()) return;

        try {
            zkClient.create(MASTER_PATH, this.workerInfo, CreateMode.EPHEMERAL);
            // 抢占 master 成功
            this.masterInfo = this.workerInfo;
            System.out.println("takeMaster success! masterInfo: " + this.masterInfo);

            // TODO 只是用于观察效果 实际使用不需要
            // 注册 master 后 30s 停止 master，用于观察效果
            delayExector.schedule(() -> {
                try {
                    System.out.println("master will stop, masterInfo: " + this.masterInfo);
                    stop();
                } catch (Exception e) {

                }
            }, 30, TimeUnit.SECONDS);
        } catch (ZkNodeExistsException e) { // 抢占 master 失败
            WorkerInfo info = zkClient.readData(MASTER_PATH, true);
            if (info == null) {
                takeMaster();   // 没读到，读取瞬间Master节点宕机了，有机会再次争抢
            } else {
                this.masterInfo = info;
            }
        }
    }

}
