package galaxis.lee.junit;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Lee
 * @Date: Created in 16:05 2020/6/6
 * @Description: TODO
 *      --> Executor
 *          1.ctrl+shift_alt+u
 *          2.ctrl+t /Diagrams 查看继承结构图
 *
 *      -->Executors(工具类)
 *          1.ctrl+n    封装了常用的线程池初始化
 *
 *      -->  public ThreadPoolExecutor(int corePoolSize,            核心线程池大小
 *                               int maximumPoolSize,               线程池内最多创建多少个线程
 *                               long keepAliveTime,                线程空闲状态最长空闲时间
 *                               TimeUnit unit,                     时间单位
 *                               BlockingQueue<Runnable> workQueue, 阻塞队列(FIFO)：1.任意时刻，不管并发有多高，永远只有一个线程能够进行队列的入队或出队操作。线程安全的队列
 *                                                                        --> 有界 ：队列满只能进行出队操作，所以入队操作必须等待，也就是被阻塞；反之队列空...。
 *                                                                        --> 无界 ：
 *                               ThreadFactory threadFactory) {     默认拒绝策略  defaultHandler = new AbortPolicy();
 *         this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
 *              threadFactory, defaultHandler);
 *     }
 *
 *    --> 线程5种生命状态
 *      Running
 *
 *      Shutdown
 *
 *      Stop
 *
 *      Tidying
 *
 *      Terminated
 *
 *
 */
public class MyThreadPool {

    public static void main(String[] args) {

      /*  ThreadPoolExecutor pool = new ThreadPoolExecutor(1,1,60,
                TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(5),
                Executors.defaultThreadFactory());
        for (int i = 0; i < 9; i++) {
            pool.execute(new Task(i));
        }
        pool.shutdown(); //不接受新任务，但会把已有任务以及队列里的等待任务执行完

        pool.shutdownNow(); // 不接受不执行任务，扫尾工作做好（当前已执行任务到一个安全点，线程池中工作数量0）直接退出 ``*/

    }
}
