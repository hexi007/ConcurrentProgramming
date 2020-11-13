package java8.functional_programming;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * description Lambda 表达式
 *
 * @author 27771
 * create 2020-11-13 17:06
 **/
public class Lambda {

    static List<Integer> numbers = Arrays.asList(1,2,3,4,5);

    void basicLambda() {
        numbers.forEach((Integer number) -> System.out.print(number + " "));
        System.out.println();
    }

    void accessingExternalVariables() {
        // 外部变量必须申明成 final 保证在 lambda 表达式中合法访问它
        final int num = 2;
        Function<Integer, Integer> numberConvert = (from) -> from * num;
        System.out.println(numberConvert.apply(3));
    }

    void illegalAccessingExternalVariables() {
        // 外部变量不申明成 final 可以编译通过
        int num = 2;
        Function<Integer, Integer> numberConvert = (from) -> from * num;
        // 报错 java: 从lambda 表达式引用的本地变量必须是最终变量或实际上的最终变量
        num++;
        System.out.println(numberConvert.apply(3));
    }

    public static void main(String[] args) {
        Lambda lambda = new Lambda();
        lambda.basicLambda();
        lambda.accessingExternalVariables();
        lambda.illegalAccessingExternalVariables();
    }
}