package java8.functional_programming;

import java.util.*;
import java.util.stream.IntStream;

/**
 * description 并行流
 *
 * @author 27771
 * create 2020-11-13 20:10
 **/
public class ParallelFlow {

    public static class PrimeUtil {

        static boolean isPrime(int number) {
            int two = 2;
            if (number < two) {
                return false;
            }
            for (int i = 2; Math.sqrt(number) >= i; i++) {
                if (number % i == 0) {
                    return false;
                }
            }
            return true;
        }

        void printPrimeCounts() {

            long start = System.currentTimeMillis();

            // 先生成 1 到 100_0000 的数字流，接着使用过滤函数计算数量
            long res = IntStream.range(1, 100_0000).filter(PrimeUtil::isPrime).count();

            long end = System.currentTimeMillis();
            long serializationTimes = end - start;
            System.out.println(res + " serializationTimes : " + serializationTimes + "ms");

            start = System.currentTimeMillis();

            // 将流并行化，parallel() 会得到一个并行流，接着在流上进行过滤
            // PrimeUtil::isPrime 函数会被多线程并发调用，应用于流中的所有元素
            res = IntStream.range(1, 100_0000).parallel().filter(PrimeUtil::isPrime).count();

            end = System.currentTimeMillis();
            long parallelizationTimes = end - start;
            System.out.println(res + " parallelizationTimes : " + parallelizationTimes + "ms");
            System.out.println("Speedup ratio : " + (double)serializationTimes / parallelizationTimes);
        }
    }

    static class Student {

        int score;

        public Student(int score) {
            this.score = score;
        }
    }

    static void getParallelFromSet() {
        List<Student> students = new ArrayList<>();
        students.add(new Student(59));
        students.add(new Student(80));
        students.add(new Student(14));
        double ave = students.stream().mapToInt(s -> s.score).average().getAsDouble();
        System.out.println("ave : " + ave);
        // 从集合得到并行流
        ave = students.parallelStream().mapToInt(s -> s.score).average().getAsDouble();
        System.out.println(ave);
    }

    static void parallelSort() {
        int[] arr = new int[1_0000];
        Random r = new Random();
        // 并设设置所有元素值
        Arrays.parallelSetAll(arr, (i) -> r.nextInt());
        // 并行排序
        Arrays.parallelSort(arr);
        Arrays.stream(arr).forEach(i -> System.out.print(i + " "));
        System.out.println();
    }

    public static void main(String[] args) {
        new PrimeUtil().printPrimeCounts();
        getParallelFromSet();
        parallelSort();
    }
}