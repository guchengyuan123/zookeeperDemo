package cn.canton.JavaAPI;
import java.util.concurrent.CountDownLatch;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

// ZooKeeper API 获取节点数据内容，使用同步(sync)接口。
public class GetData_API_Sync_Usage implements Watcher {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zk = null;
    private static Stat stat = new Stat();

    public static void main(String[] args) throws Exception {

    	String path = "/zk-book";
    	zk = new ZooKeeper("127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183",
				5000, //
				new GetData_API_Sync_Usage());
        connectedSemaphore.await();
        zk.create( path, "123".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL );
        
        System.out.println(new String(zk.getData( path, true, stat )));
        System.out.println(stat.getCzxid()+","+stat.getMzxid()+","+stat.getVersion());
        
        zk.setData( path, "123".getBytes(), -1 );
        
        Thread.sleep( Integer.MAX_VALUE );
    }
    public void process(WatchedEvent event) {
        if (KeeperState.SyncConnected == event.getState()) {
  	      if (EventType.None == event.getType() && null == event.getPath()) {
  	          connectedSemaphore.countDown();
  	      } else if (event.getType() == EventType.NodeDataChanged) {
  	          try {
  	              System.out.println(new String(zk.getData( event.getPath(), true, stat )));
  	              System.out.println(stat.getCzxid()+","+
  	                                 stat.getMzxid()+","+
  	            		             stat.getVersion());
  	          } catch (Exception e) {}
  	      }
  	    }
       }
}