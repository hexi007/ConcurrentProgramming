package patterns_and_algorithms;

/**
 * description 单例模式,一种对象创建模式，用于产生一个对象的具体实例 <br/>
 * 可以确保系统中一个类只产生一个实例 <br/>
 * 优点 <br/>
 * 1.省略 new 操作花费的时间 <br/>
 * 2.因为 new 操作次数减少，对内存使用频率降低，减少 GC 压力，缩短 GC 停顿 <br/>
 * create 2020-11-03 15:55
 *
 * @author 27771
 **/
public class SingletonMode {
    static class Singleton {
        public static int STATUS = 1;
        //构造函数设为 private ，提醒不要随便创建此实例
        private Singleton(){
            System.out.println("Singleton is create");
        }
        //instance 必须 private ，否则 instance 安全性无法保证
        //因为工厂方法 getInstance() 必须是 static。 所以 instance 必须 static
        private static Singleton instance = new Singleton();
        public static Singleton getInstance() {
            return instance;
        }
    }

    static class LazySingleton {
        public static int STATUS = 1;
        private LazySingleton(){
            System.out.println("LazySingleton is create");
        }
        private static LazySingleton instance = null;
        //为了防止对象被多次创建需要加锁，利用了延迟加载，只有真正需要时创建对象
        //并发环境下加锁可能对竞争激励的场合对性能可能产生一定影响
        public static synchronized LazySingleton getInstance() {
            if(instance == null){
                instance = new LazySingleton();
            }
            return instance;
        }
    }

    static class StaticSingleton {
        public static int STATUS = 1;
        public StaticSingleton() {
            System.out.println("StaticSingleton is create");
        }
        private static class SingletonHolder{
            private static StaticSingleton instance = new StaticSingleton();
        }
        //无锁，只有 getInstance() 第一次被调用，StaticSingleton 实例才会被创建
        //巧妙地使用了内部类和类的初始化方式
        public static StaticSingleton getInstance() {
            return SingletonHolder.instance;
        }
    }

    public static void main(String[] args) {
        System.out.println("Singleton");
        //任何对 Singleton 方法或者字段的引用，都会导致类初始化，并创建 instance 实例
        System.out.println(Singleton.STATUS);
        //但类初始化只有一次，因此 instance 实例永远只会被创建一次
        System.out.println(Singleton.STATUS);

        System.out.println("LazySingleton");
        System.out.println(LazySingleton.STATUS);

        System.out.println("StaticSingleton");
        System.out.println(StaticSingleton.STATUS);
    }
}