package lock_optimization;

/**
 * description 哲学家进餐
 * create 2020-11-03 15:05
 *
 * @author 27771
 **/
public class DeadLock extends Thread{
    protected Object tool;
    static final Object FORK1 = new Object();
    static final Object FORK2 = new Object();

    public DeadLock(Object obj){
        this.tool = obj;
        if(tool == FORK1){
            this.setName("哲学家 A ");
        } else if(tool == FORK2){
            this.setName("哲学家 B ");
        }
    }

    /**
     *  哲学家 A 先持有锁 FORK1 再申请锁 FORK2
     *  哲学家 B 先持有锁 FORK2 再申请锁 FORK1
     *  产生死锁
     */
    @Override
    public void run() {
        if(tool == FORK1){
            synchronized (FORK1) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (FORK2) {
                    System.out.println(this.getName() + "开始进餐");
                }
            }
        }else if (tool == FORK2){
            synchronized (FORK2) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (FORK1) {
                    System.out.println(this.getName() + "开始进餐");
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        DeadLock philosopherA = new DeadLock(FORK1);
        DeadLock philosopherB = new DeadLock(FORK2);

        philosopherA.start();
        philosopherB.start();

        Thread.sleep(1000);
    }
}