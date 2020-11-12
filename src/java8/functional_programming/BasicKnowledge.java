package java8.functional_programming;

import java.util.Arrays;

/**
 * description 函数式编程基础知识
 *
 * @author 27771
 * create 2020-11-12 20:42
 **/
public class BasicKnowledge {

    private static final int[] ARRAY = {1,2,3,4,5};

    /**
     * 命令式编程，需要明确循环和控制
     */
    private void imperative() {
        System.out.println("imperative......");
        for (int a : ARRAY) {
            System.out.print(a);
        }
        System.out.println();
    }

    /**
     * 函数式编程，循环和判断都封装在程序库里
     */
    private void declarative() {
        System.out.println("declarative......");
        Arrays.stream(ARRAY).forEach(System.out::print);
        System.out.println();
    }

    /**
     * 在函数式编程中，几乎传递的对象都不会轻易被修改
     * 由于对象基本不会被修改，所以函数式编程有益于并行
     */
    private void rejectUpdating() {
        System.out.println("rejectUpdating......");
        Arrays.stream(ARRAY).map(x -> x = x + 1).forEach(System.out::print);
        System.out.println();
        this.declarative();
    }

    /**
     * 函数式编程更紧凑且简洁
     */
    private void lessCode() {
        System.out.println("lessCode......");
        int[] temp = Arrays.copyOf(ARRAY, ARRAY.length);

        for (int i = 0; i < temp.length; i++) {
            if ((temp[i] & 1) == 1) {
                temp[i]++;
            }
            System.out.print(temp[i]);
        }
        System.out.println();

        temp = Arrays.copyOf(ARRAY, ARRAY.length);
        Arrays.stream(temp).map(x -> (x & 1) == 1 ? x + 1 : x).forEach(System.out::print);
        System.out.println();
    }

    public static void main(String[] args) {
        BasicKnowledge bK = new BasicKnowledge();
        bK.imperative();
        bK.declarative();
        bK.rejectUpdating();
        bK.lessCode();
    }
}