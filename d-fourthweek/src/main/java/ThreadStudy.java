import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 目标：多种方法实现获取其他线程执行结果后，再继续执行主线程
 * <p>
 * <p>
 * 具体实现方式：
 * 0.死循环等待获取到结果再执行主线程，这里线程的创建可以 new 或者通过 Timer 实现
 * 1.通过countDownLatch实现
 * 2.通过 wait notify实现
 * 3.通过join实现
 * 4.线程池的submit方法，分别对应不同的传参：传入 Callable 或者传入 Runnable 和 result
 * 5.通过interrupt实现，主线程调用wait或者sleep进行无限期中断状态，新建线程执行完计算任务，调用interrupt方法打断主线程的中断状态，完成
 * 6.synchronized关键字实现
 * 7.lock实现
 * <p>
 * 简单总结：感觉无非就是写法上的差异，无论是线程池还是直接new Thread，都是要等待线程A执行完成，再执行线程B。
 * 本质上都是通过join wait notify interrupt之间的配合实现，或者直接死循环等待，这种浪费资源。
 *
 * @author czj
 * @date 2021/8/28 13:59
 */
public class ThreadStudy {

    private static final int a = 20;

    public static void main(String[] args) {

        System.out.println("===============死循环实现===============");
        AtomicReference<Integer> reference = new AtomicReference<>();

        /**
         * 说明：新建线程计算菲波那切数列第n个数 新建线程的方式多种多样...
         */
        //new Thread(() -> {
        //    int i = fiBo(a);
        //    reference.set(i);
        //}).start();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                int i = fiBo(a);
                reference.set(i);
            }
        }, 0);

        //死循环等待线程计算完毕
        while (reference.get() == null) {
            //避免打印太多信息 休息一下
            try {
                Thread.currentThread().sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("主线程等待计算中...");
        }

        //计算完毕 线程池之类的可关闭资源
        timer.cancel();
        System.out.println("计算结果=" + reference.get());

        System.out.println("===============interrupt实现===============");

        long start = System.currentTimeMillis();
        //获取主线程
        Thread mainThread = Thread.currentThread();

        //接收返回值
        AtomicReference<Integer> reference2 = new AtomicReference<>();

        /**
         * 说明：这里不一定要new Thread 直接通过线程池执行也是可以的
         */
        new Thread(() -> {
            int i = fiBo(a);
            reference2.set(i);

            //计算执行完毕 打断主线程睡眠状态
            mainThread.interrupt();
        }).start();

        /**
         * 说明：这里需要让主线程睡眠或者阻塞，直到上面的计算完成，计算完成 打断主线程的中断状态，执行完毕
         * wait或者sleep都行 一样的效果
         */
        //wait方式主线程中断
        Object lock = new Object();
        synchronized (lock) {
            try {
                lock.wait(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                System.out.println("e=" + e);
            }
        }

        //sleep方式主线程中断
        //try {
        //    mainThread.sleep(Integer.MAX_VALUE);
        //} catch (InterruptedException e) {
        //    System.out.println("e= " + e.getMessage());
        //}
        System.out.println("执行结果=" + reference2.get());
        System.out.println("使用时间：" + (System.currentTimeMillis() - start) + " ms");

        System.out.println("===============synchronized实现===============");
        AtomicReference<Integer> result = new AtomicReference<>();
        Object obj = new Object();
        synchronized (obj) {
            new Thread(() -> {
                result.set(fiBo(a));
            }).start();
        }
        synchronized (obj) {
            System.out.println("synchronized实现 执行结果=" + reference.get());
        }

        System.out.println("===============lock实现===============");
        AtomicReference<Integer> result2 = new AtomicReference<>();
        Lock reentrantLock = new ReentrantLock();
        new Thread(() -> {
            reentrantLock.lock();
            try {
                result2.set(fiBo(a));
            } finally {
                reentrantLock.unlock();
            }
        }).start();

        try {
            //主线程休息一下 确保计算线程先拿到锁
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            boolean b = reentrantLock.tryLock(Long.MAX_VALUE, TimeUnit.SECONDS);
            if (b) {
                System.out.println("reentrantLock实现 执行结果=" + result2.get());
            }
        } catch (InterruptedException e) {
            reentrantLock.unlock();
            System.out.println("lock实现 执行异常 e=" + e.getMessage());
        }

        System.out.println("===============countDownLatch实现===============");
        System.out.println("执行结果=" + countDownLatchComplete());
        System.out.println("===============wait/notify实现===============");
        System.out.println("执行结果=" + threadWaitNotifyComplete());
        System.out.println("===============join实现===============");
        System.out.println("执行结果=" + threadJoinComplete());
        System.out.println("===============线程池等待(Callable)实现===============");
        System.out.println("执行结果=" + threadPoolComplete(Executors.newFixedThreadPool(1)));
        System.out.println("===============线程池等待(Runnable result)实现===============");
        System.out.println("执行结果=" + threadPoolComplete2(Executors.newFixedThreadPool(1)));
    }


    /**
     * 通过创建单个线程 countDownLatch 完成
     *
     * @return
     */
    private static Integer countDownLatchComplete() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicReference<Integer> reference = new AtomicReference<>();
        new Thread(() -> {
            int i = fiBo(a);
            reference.set(i);

            countDownLatch.countDown();
        }).start();

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return reference.get();
    }

    /**
     * 通过创建单个线程 wait notify 完成
     *
     * @return
     */
    private static Integer threadWaitNotifyComplete() {
        Object lock = new Object();
        AtomicReference<Integer> reference = new AtomicReference<>();
        Thread thread = new Thread(() -> {
            //模拟耗时操作 确保主线程一定先 wait 住
            try {
                Thread.currentThread().sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (lock) {
                int i = fiBo(a);
                reference.set(i);

                lock.notifyAll();
            }
        });
        thread.start();

        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return reference.get();
    }

    /**
     * 通过创建单个线程 join 完成
     *
     * @return
     */
    private static Integer threadJoinComplete() {
        AtomicReference<Integer> reference = new AtomicReference<>();
        Thread thread = new Thread(() -> {
            int i = fiBo(a);
            reference.set(i);
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            //这里会返回null
            System.out.println("e=" + e.getMessage());
        }
        return reference.get();
    }


    /**
     * 通过线程池完成1 调用线程池的 submit(Callable<T> task) 方法
     *
     * @param threadPool Executors.创建 或者 new ThreadPoolExecutor 或者 ThreadPoolTaskExecutor(Spring环境)
     * @return Integer
     */
    private static Integer threadPoolComplete(ExecutorService threadPool) {
        Integer result;
        Future<Integer> future = threadPool.submit(() -> fiBo(a));
        //为了确保拿到结果 设置无限长 实际应该设置超时时间
        try {
            result = future.get(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.err.println("e=" + e.getMessage());
            return null;
        } finally {
            //关闭线程池
            threadPool.shutdown();
        }
        return result;
    }

    /**
     * 通过线程池完成2 调用线程池的 submit(Runnable task, T result) 方法
     *
     * @param threadPool Executors.创建 或者 new ThreadPoolExecutor 或者 ThreadPoolTaskExecutor(Spring环境)
     * @return Integer
     */
    private static Integer threadPoolComplete2(ExecutorService threadPool) {
        AtomicReference<Integer> resultReference = new AtomicReference<>();
        Future<Integer> future = threadPool.submit(() -> {
            int i = fiBo(a);
            resultReference.set(i);
        }, resultReference.get());
        //为了确保拿到结果 设置无限长 实际应该设置超时时间
        try {
            future.get(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.err.println("e=" + e.getMessage());
            return null;
        } finally {
            //关闭线程池
            threadPool.shutdown();
        }
        return resultReference.get();
    }

    private static int fiBo(int a) {
        if (a < 2)
            return 1;
        return fiBo(a - 1) + fiBo(a - 2);
    }

    /**
     * for循环实现，更快且不会因为调用层级过深导致栈内存溢出
     * 这里第一，二个数都是1   ->  1 1 2 3 5
     */
    private static long fiBo2(int n) {
        if (n < 1) {
            throw new IllegalArgumentException(n + "");  //从第1个数开始计算
        }
        if (n == 1 || n == 2) {
            return 1;
        }

        long a = 1L, b = 1L, c = 0L;        //定义三个long类型整数
        for (int i = 0; i < n - 2; i++) {
            c = a + b;        //第3个数的值等于前两个数的和
            a = b;            //第2个数的值赋值给第1个数
            b = c;            //第3个数的值赋值给第2个数
        }
        return c;
    }

}
