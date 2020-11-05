package patterns_and_algorithms.future_data;

/**
 * description 快速返回 RealData 包装,封装了获取 RealData 的等待过程 <br/>
 * create 2020-11-04 21:50
 *
 * @author 27771
 **/
public class FutureData implements Data{

    protected RealData realData = null;
    protected boolean isReady = false;

    public synchronized void setRealData(RealData realData) {
        this.realData = realData;
        isReady = true;
        //RealData 已经被注入，通知 getResult()
        notifyAll();
    }

    /**
     *  等待 RealData 构造完成
     */
    @Override
    public synchronized String getResult() {
        while (!isReady) {
            try {
                //一直等待，直到 RealData 被注入
                wait();
            } catch (InterruptedException ignored) {
            }
        }
        //返回 RealData 结果
        return realData.getResult();
    }
}