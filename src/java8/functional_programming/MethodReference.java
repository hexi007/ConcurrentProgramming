package java8.functional_programming;

import java.util.ArrayList;
import java.util.List;

/**
 * description 方法引用
 * 通过类名和方法名来定位一个静态方法或者实例方法
 *
 * @author 27771
 * create 2020-11-13 19:24
 **/
public class MethodReference {

    private static class User {
        int userId;
        String userName;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public User(int userId, String userName) {
            this.userId = userId;
            this.userName = userName;
        }
    }

    /**
     * 方法引用的基本使用
     */
    static void instanceMethodRef() {
        List<User> users = new ArrayList<>();
        int userSize = 10;
        for (int i = 0; i < userSize; i++) {
            users.add(new User(i, "user" + i));
        }

        // User::getUserName 表示类的实例方法
        // Java 会自动识别流中的元素是作为调用目标还是调用方法的参数
        // User::getUserName 显然流内的元素都应该作为调用目标
        // 在这里调用了每一个 User 对象实例的 getUserName() 方法，将 name 作为一个新的流
        // System.out::println 流内的元素会作为方法的参数传入
        users.stream().map(User::getUserName).forEach((String s) -> System.out.print(s + " "));
        System.out.println();
    }

    /**
     * 如果一个类存在同名的实例方法和静态函数，则编译器不知道选择哪个方法调用
     */
    static void badMethodRef() {
        List<Double> doubles = new ArrayList<>();
        int doubleSize = 10;
        for (int i = 0; i < doubleSize; i++) {
            doubles.add((double) i);
        }

        // java.lang.Double 中的方法 toString(double) 和 java.lang.Double 中的方法 toString() 都匹配)
        //doubles.stream().map(Double::toString).forEach(System.out::println)
        doubles.stream().map(Object::toString).forEach((String s) -> System.out.print(s + " "));
        System.out.println();
    }

    static class ConstructMethodRef {

        /**
         * UserFactory 作为 User 工厂类，是一个函数式接口
         */
        @FunctionalInterface
        interface UserFactory<U extends User> {
            /**
             * create() 函数签名
             * @param userId   用户 Id
             * @param userName 用户名
             * @return  U
             */
            U create(int userId, String userName);
        }

        static UserFactory<User> userFactory = User::new;

        void run() {
            List<User> users = new ArrayList<>();
            int userSize = 10;
            for (int i = userSize - 1; i >= 0; i--) {
                users.add(userFactory.create(i, "user" + i));
            }
            users.stream().map(User::getUserName).forEach((String s) -> System.out.print(s + " "));
            System.out.println();
        }
    }

    public static void main(String[] args) {
        instanceMethodRef();
        badMethodRef();
        new ConstructMethodRef().run();
    }
}