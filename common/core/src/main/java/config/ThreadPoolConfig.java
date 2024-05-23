package config;

import config.properties.ThreadPoolProperties;
import utils.ThreadsUtils;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池
 * @author hxy
 * @date 2024/5/21
 **/
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(ThreadPoolProperties.class)
public class ThreadPoolConfig {
    /**
     * LinkedBlockingQueue：
     * 一个基于链表结构的阻塞队列，此队列按 FIFO（先进先出）排序元素。
     * 如果初始化时没有定义容量，它等同于一个无界队列，但实际会受到内存限制。
     * 适用场景：当您希望队列能够处理未知数量的任务时。
     * ArrayBlockingQueue：
     * 一个由数组支持的有界阻塞队列。
     * 队列的容量在初始化时指定，并且一旦创建，容量不能更改。
     * 适用场景：当您希望队列有一个固定大小时。
     * PriorityBlockingQueue：
     * 一个支持优先级排序的无界阻塞队列。
     * 元素按照其自然顺序或者根据构造时所提供的 Comparator 进行排序。
     * 适用场景：当您希望能够根据元素的优先级处理任务时。
     * SynchronousQueue：
     * 一个不存储元素的阻塞队列。
     * 每个插入操作必须等到另一个线程调用移除操作，反之亦然。
     * 适用场景：当您希望直接将任务交给线程而不保留它们时，通常用于任务的即时传递。
     * DelayedQueue：
     * 一个使用元素的延迟时间来排序元素的无界阻塞队列。
     * 只有延迟过期时元素才能从队列中取出。
     * 适用场景：当您需要在指定的延迟后执行任务时。
     * LinkedBlockingDeque：
     * 一个基于链表结构的可选有界阻塞双端队列。
     * 此队列按 FIFO（先进先出）排序元素，但也可以支持 LIFO（后进先出）。
     * 适用场景：当您需要额外的双端操作时，例如在工作窃取算法中。
     */
    /**
     * CallerRunsPolicy：在任务被拒绝添加后，该策略会直接在调用者线程中运行被拒绝的任务。这种策略不会抛弃任务，也不会抛出异常，但可能会降低调用者线程的性能。适用于希望保证任务执行，且可以接受调用者线程性能降低的场景。
     *
     * AbortPolicy：这是默认的拒绝策略。当线程池无法处理新任务时，这个策略会抛出一个RejectedExecutionException异常。适用于那些希望在任务被拒绝时立即得到通知的场景。
     *
     * DiscardPolicy：该策略会默默地丢弃无法处理的任务，不抛出异常，也不提供任何通知。适用于任务丢失对系统影响不大的情况。
     *
     * DiscardOldestPolicy：此策略将丢弃线程池中最旧的一个任务，然后尝试再次提交新任务。适用于那些希望牺牲一些旧任务以继续执行新任务的场景。
     */

    /**
     * 核心线程数 = cpu 核心数 + 1
     */
    private final int core = Runtime.getRuntime().availableProcessors() + 1;

    private ScheduledExecutorService scheduledExecutorService;

    @Bean(name = "threadPoolTaskExecutor")
    @ConditionalOnProperty(prefix = "thread-pool", name = "enabled", havingValue = "true")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor(ThreadPoolProperties threadPoolProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(core);
        executor.setMaxPoolSize(core * 2);
        executor.setQueueCapacity(threadPoolProperties.getQueueCapacity());
        executor.setKeepAliveSeconds(threadPoolProperties.getKeepAliveSeconds());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }
    /**
     * 执行周期性或定时任务
     */
    @Bean(name = "scheduledExecutorService")
    protected ScheduledExecutorService scheduledExecutorService() {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(core,
            new BasicThreadFactory.Builder().namingPattern("schedule-pool-%d").daemon(true).build(),
            new ThreadPoolExecutor.CallerRunsPolicy()) {
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                ThreadsUtils.printException(r, t);
            }
        };
        this.scheduledExecutorService = scheduledThreadPoolExecutor;
        return scheduledThreadPoolExecutor;
    }
    /**
     * 销毁事件
     */
    @PreDestroy
    public void destroy() {
        try {
            log.info("====关闭后台任务任务线程池====");
            ThreadsUtils.shutdownAndAwaitTermination(scheduledExecutorService);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
