package concurrent_basic;

/**
 * description 线程组
 *
 * @author 27771
 * create 2020-10-20 16:05
 **/
public class ThreadGroupName implements Runnable{
    @Override
    public void run() {
        String groupName = Thread.currentThread().getThreadGroup().getName() + " - " +
                Thread.currentThread().getName();
        System.out.println(groupName);
        try{
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ThreadGroup threadGroup = new ThreadGroup("Print Group");

        Thread thread1 = new Thread(threadGroup, new ThreadGroupName(), "T1");
        Thread thread2 = new Thread(threadGroup, new ThreadGroupName(), "T2");

        thread1.start();
        thread2.start();

        //activeCount()估计活动线程总数
        System.out.println(threadGroup.activeCount());
        //list()打印线程组所有信息
        threadGroup.list();
    }
}