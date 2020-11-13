package java8.functional_programming;

import java.util.Arrays;
import java.util.Comparator;

/**
 * description 接口内定义实例方法，并非抽象方法
 *
 * @author 27771
 * create 2020-11-13 16:25
 **/
public class DefaultInterface {

    /**
     * 动物接口
     */
    public interface Animal {

        /**
         * 接口内定义默认方法
         */
        default void breath() {
            System.out.println("Animal breath");
        }
    }

    /**
     * 马接口
     */
    public interface Horse {

        /**
         * eat 抽象方法
         */
        void eat();

        /**
         * 接口内定义默认方法
         */
        default void run() {
            System.out.println("Horse run");
        }
    }

    /**
     * 驴接口
     */
    public interface Donkey {

        /**
         * eat 抽象方法
         */
        void eat();

        /**
         * 接口内定义默认方法
         */
        default void run() {
            System.out.println("Donkey run");
        }
    }

    /**
     *  骡子类
     */
    public static class Mule implements Horse, Donkey, Animal {

        @Override
        public void eat() {
            System.out.println("Mule eat");
        }


        /**
         * 编译器进行方法绑定
         */
        @Override
        public void run() {
            Horse.super.run();
        }
    }

    /**
     * 元素的多条件排序
     */
    static void stringCompare() {
        String[] strings = {"compare","key","caddish"};
        // 先按字符串长度排序，再按大小写不敏感排序
        Comparator<String> cmp = Comparator.comparingInt(String::length)
                .thenComparing(String.CASE_INSENSITIVE_ORDER);
        Arrays.sort(strings, cmp);
        Arrays.stream(strings).map(x -> x += " ").forEach(System.out::print);
        System.out.println();
    }

    public static void main(String[] args) {
        Mule mule = new Mule();
        mule.eat();
        mule.run();
        mule.breath();
        stringCompare();
    }
}