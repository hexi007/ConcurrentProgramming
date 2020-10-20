package basic;

/**
 * description 线程优先级，java线程优先级是映射在操作系统上的，设置优先级也可能出现优先级反转
 *  优先级反转是指一个低优先级的任务持有一个被高优先级任务所需要的共享资源。
 *  高优先任务由于因资源缺乏而处于受阻状态，一直等到低优先级任务释放资源为止。
 *  而低优先级获得的CPU时间少，如果此时有优先级处于两者之间的任务，并且不需要那个共享资源，
 *  则该中优先级的任务反而超过这两个任务而获得CPU时间。
 *  如果高优先级等待资源时不是阻塞等待，而是忙循环，则可能永远无法获得资源，
 *  因为此时低优先级进程无法与高优先级进程争夺CPU时间，从而无法执行，
 *  进而无法释放资源，造成的后果就是高优先级任务无法获得资源而继续推进。
 *
 * @author 27771
 * create 2020-10-20 16:43
 **/
public class PriorityDemo {
    static int maxRounds = 10000000;

    private static class Priority extends Thread{

        public Priority(String name) {
            super(name);
        }

        private int count = 0;
        @Override
        public void run() {
            while (true){
                synchronized (PriorityDemo.class){
                    count++;
                    if(count > maxRounds){
                        System.out.println(this.getName() + " is complete");
                        break;
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        Thread highPriority = new Priority("HighPriority");
        Thread lowPriority = new Priority("LowPriority");
        highPriority.setPriority(Thread.MAX_PRIORITY);
        lowPriority.setPriority(Thread.MIN_PRIORITY);
        //一般来说优先级高的线程相比优先级低的线程会优先执行，但不能保证
        highPriority.start();
        lowPriority.start();
    }
}