package java8.functional_programming;

/**
 * description 函数式接口
 *
 * @author 27771
 * create 2020-11-13 15:57
 **/
public class FunctionalInterfaceDemo {

    /**
     * 注释 FunctionalInterface 用来表面接口是一个函数式接口
     */
    @FunctionalInterface
    public interface IntHandler {

        /**
         * 接口只能包含有一个 ！抽象！ 方法，因此符合函数式接口的定义
         * 如果一个函数满足函数式接口的定义，即使不标注@FunctionalInterface
         * 编译器仍然会将其是为函数式接口
         * @param i 参数
         */
        void handle(int i );
    }

    /**
     * NonFunc 不是函数式接口
     */
    interface NonFunc {

        /**
         * 任何被 java.lang.Object 实现的方法都不能视为抽象方法
         * @param o 参数
         * @return  比较结果
         */
        @Override
        boolean equals(Object o);
    }

    /**
     * IntHandler1 符合函数式接口规范
     */
    @FunctionalInterface
    public static interface IntHandler1 {

        /**
         * 接口只能包含有一个抽象方法，而不是一个方法
         * @param i 参数
         */
        void handle(int i );

        /**
         * 一个方法，不是抽象方法
         * @param o 参数
         * @return  比较结果
         */
        @Override
        boolean equals(Object o);
    }
}