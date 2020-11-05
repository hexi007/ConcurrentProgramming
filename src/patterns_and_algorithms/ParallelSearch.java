package patterns_and_algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * description 并行搜索
 * create 2020-11-05 20:16
 *
 * @author 27771
 **/
public class ParallelSearch implements Callable<Integer> {
    private static int[] arr;
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    private static final int THREAD_SIZE = 2;
    /**
     * 存放结果变量，默认 -1 为没有找到
     */
    private final AtomicInteger result = new AtomicInteger(-1);

    /**
     * searchValue 要找的数
     * beginPos    开始找的位置
     * endPos      结束找的位置
     */
    int searchValue, beginPos, endPos;

    public ParallelSearch(int searchValue, int beginPos, int endPos) {
        this.searchValue = searchValue;
        this.beginPos = beginPos;
        this.endPos = endPos;
    }

    public int search(int searchValue, int beginPos, int endPos){
        for(int i = beginPos; i < endPos; i++){
            //如果有结果，返回 result 结果下标
            if(result.get() > -1){
                return result.get();
            }
            //找到了该数
            if(arr[i] == searchValue){
                //如果修改 result 不成功
                if(!result.compareAndSet(-1, i)){
                    return result.get();
                }
                //修改成功，返回下标
                return i;
            }
        }
        return  -1;
    }

    @Override
    public Integer call() {
        return search(searchValue, beginPos, endPos);
    }

    /**
     * 并行查找函数
     * @param searchValue 目标数
     * @return            目标所在下标
     */
    public static int pSearch(int searchValue) throws ExecutionException, InterruptedException {
        int subArraySize = arr.length / THREAD_SIZE + 1;
        List<Future<Integer>> res = new ArrayList<>();
        //切分数组，建立相应任务提交给线程池处理
        for(int i = 0; i < arr.length; i += subArraySize){
            int endPos = Math.min(i + subArraySize, arr.length);
            res.add(EXECUTOR.submit(new ParallelSearch(searchValue, i , endPos)));
        }
        //获取结果
        for(Future<Integer> future : res){
            if(future.get() > -1){
                EXECUTOR.shutdown();
                return future.get();
            }
        }
        return -1;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        arr = new int[]{1, 2, 3, 4, 5};
        System.out.println(pSearch(3));
    }
}