package com.zzq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.OperateOracle;
import utils.RecordToFile;
import utils.ThreadPoolManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author zhangzhiqiang
 * @date 2018-08-06 17:02
 * &Desc 寻常生活自动签到项目主入口
 */
public class MainEntry extends Thread{

    private static final Logger LOGGER = LoggerFactory.getLogger(MainEntry.class);
    private static ThreadPoolManager threadPoolManager = ThreadPoolManager.getInstance("threadPoolManager");
    public static void main(String[] args) {
        System.out.println("~~~~主函数运行了~~~");
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        Long beginLongTime = c.getTimeInMillis();
        String beginTime = sdf.format(c.getTime());
        RecordToFile.record(new String[] {"菜鸟理财开始时间="+beginTime}, "countTime.txt");

        Uhomecp_flow flow =  new Uhomecp_flow();
//		List<cnUser> cnUsers = ListDom4J.turnDomtoCnUsers();
        OperateOracle op = new OperateOracle();
        List<User> Users = op.getUsers();
        ArrayList<String> arrayList = new ArrayList<>();
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1,true);
        System.out.println("添加任务数量="+Users.size());
        for (User user : Users){
            threadPoolManager.addTask(new UHomecpThread(user));
        }
        while(threadPoolManager.threadPool.getTaskCount()!=threadPoolManager.threadPool.getCompletedTaskCount()){
            try {
                System.out.println("11线程池还有未完成任务（总任务数："+threadPoolManager.threadPool.getTaskCount()+",已完成任务数："+threadPoolManager.threadPool.getCompletedTaskCount()+",活跃线程数："+threadPoolManager.threadPool.getActiveCount()
                        +",线程池大小："+threadPoolManager.threadPool.getPoolSize()+",最大线程数："+threadPoolManager.threadPool.getMaximumPoolSize());
                Thread.sleep(60000);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        threadPoolManager.threadPool.shutdown();

        while(!threadPoolManager.threadPool.isTerminated()){
            System.out.println("等待线程池所有线程结束");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


        Calendar c2 = Calendar.getInstance();
        Long endLongTime = c2.getTimeInMillis();
        String endTime = sdf.format(c2.getTime());
        RecordToFile.record(new String[] {"菜鸟理财结束时间="+endTime}, "countTime.txt");
        RecordToFile.record(new String[] {"----菜鸟理财本次用时="+(endLongTime-beginLongTime)/1000/60+"分钟"}, "countTime.txt");
        System.out.println("~~~~主函数完了~~~");
        System.exit(0);
    }
}
