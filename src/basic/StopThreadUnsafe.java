package basic;

/**
 * 强制停止线程不安全
 *
 * @author 27771
 * create 2020-10-19 16:16
 **/
public class StopThreadUnsafe {

    private static User user = new User();

    private static class User{
        private int id;
        private String name;

        public User() {
            this.id = 0;
            this.name = "0";
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    /**
     * Description 读user的id和name，不一致则输出user
     */
    public static class ReadUserThread extends Thread{
        @Override
        public void run(){
            while (true){
                synchronized (user){
                    if(user.getId() != Integer.parseInt(user.getName())){
                        System.out.println(user.toString());
                    }
                }
                Thread.yield();
            }
        }
    }

    /**
     * Description  改写user的id和name，中间等待100ms，故意存在一个写到一半的user
     */
    public static class ChangeUserThread extends Thread{
        @Override
        public void run(){
            while(true){
                int value = (int)(System.currentTimeMillis() / 1000);
                user.setId(value);
                try{
                    Thread.sleep(100);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
                user.setName(String.valueOf(value));
                Thread.yield();
            }
        }
    }

    /**
     * Description  一个线程负责读，不断创建线程改变user并结束时暴力stop.
     * 当线程写对象时，读线程必须等待，所以读线程看不到写了一半的对象
     * 写线程写到一半即写了id被暴力stop，都对象那个则对象id和name就处于不一致的状态
     */
    public static void main(String[] args) throws InterruptedException {
        new ReadUserThread().start();
        while (true){
            Thread thread = new ChangeUserThread();
            thread.start();
            Thread.sleep(100);
            thread.stop();
        }
    }
}