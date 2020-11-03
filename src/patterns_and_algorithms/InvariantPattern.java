package patterns_and_algorithms;

/**
 * description 不变模式,一个对象一旦创建则内部状态将永远不会发生改 <br/>
 * 无论出于什么原因，对象创建后，其内部状态和数据保持绝对稳定 <br/>
 * 应用场景需要满足的条件 <br/>
 * 1.对象被创建后其内部状态和数据不再发生任何变化 <br/>
 * 2.对象需要被共享，被多线程访问 <br/>
 * 实现不变模式需要注意 4 点 <br/>
 * 1.去除 setter 及所有修改自身属性的方法 <br/>
 * 2.所有属性私有，用 final 标记确保不可修改 <br/>
 * 3.确保没有子类可以重载其行为 <br/>
 * 4.有一个可以创建完整对象的构造函数 <br/>
 * 不变模式不需要同步操作，需求允许下可以提高系统的并发性能和并发量 <br/>
 * create 2020-11-03 18:59
 *
 * @author 27771
 **/
public class InvariantPattern {

    /**
     * final 确保无子类
     */
    public static final class Product {
        //属性 private， final 确保属性不被二次赋值
        private final Integer no;
        private final String name;

        //创建时指定数据，因为创建完成后属性无法修改
        public Product(Integer no, String name) {
            this.no = no;
            this.name = name;
        }

        public Integer getNo() {
            return no;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "Product{" +
                    "no=" + getNo() +
                    ", name='" + getName() + '\'' +
                    '}';
        }
    }

    public static void main(String[] args) {
        Product product = new Product(1, "product1");
        System.out.println(product.toString());
    }
}