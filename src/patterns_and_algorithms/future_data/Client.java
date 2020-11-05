package patterns_and_algorithms.future_data;

/**
 * description 模拟客户端程序 <br/>
 * create 2020-11-05 15:00
 *
 * @author 27771
 **/
public class Client {
    /**
     * 获取 FutureData 并开始构造 FutureData 的线程 <br/>
     * 注意它不会真的构造完毕再返回，而是理解返回 FutureData <br/>
     * 即使 FutureData 里面没有真是数据 <br/>
     * @param queryStr 请求参数
     * @return         FutureData
     */
    public Data request(final String queryStr){
        final FutureData futureData = new FutureData();
        new Thread () {
            @Override
            public void run() {
                //RealData 构建很慢，所以再单独线程中进行
                RealData realData = new RealData(queryStr);
                //完成 RealData 注入
                futureData.setRealData(realData);
            }
        }.start();
        //FutureData 会立即返回
        return futureData;
    }

    public static void main(String[] args) {
        Client client = new Client();
        //这里立即返回，得到的是 FutureData 而不是 RealData
        Data data = client.request("name");
        System.out.println("请求完毕");
        //提前请求结果会被阻塞
        //System.out.println("尝试提前请求数据 " + data.getResult())
        try{
            //模拟一个与 Data 无关的其他业务逻辑
            //处理这个业务逻辑过程中， RealData 被创建，从而充分利用了等待时间
            System.out.println("处理业务逻辑中。。。");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //使用真实数据
        System.out.println("真实数据 = " + data.getResult());
    }
}