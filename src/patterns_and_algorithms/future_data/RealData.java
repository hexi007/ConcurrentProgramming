package patterns_and_algorithms.future_data;

/**
 * description 最终使用数据模型 <br/>
 * create 2020-11-05 14:54
 *
 * @author 27771
 **/
public class RealData {

    protected final String result;

    /**
     * 构造函数， RealData 的构造可能很慢
     * @param para 请求参数
     */
    public RealData(String para) {
        StringBuilder sb = new StringBuilder();
        int taskRound = 10;
        for(int i = 0; i < taskRound; i++){
            sb.append(para).append(" ");
            try{
                //模拟耗时任务
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.result = sb.toString();
    }

    public String getResult() {
        return result;
    }
}