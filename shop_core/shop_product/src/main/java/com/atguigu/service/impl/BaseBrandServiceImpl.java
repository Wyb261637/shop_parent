package com.atguigu.service.impl;

import com.atguigu.entity.BaseBrand;
import com.atguigu.exception.SleepUtils;
import com.atguigu.mapper.BaseBrandMapper;
import com.atguigu.service.BaseBrandService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 品牌表 服务实现类
 * </p>
 *
 * @author WangYiBing
 * @since 2023-01-09
 */
@SuppressWarnings("LanguageDetectionInspection")
@Service
public class BaseBrandServiceImpl extends ServiceImpl<BaseBrandMapper, BaseBrand> implements BaseBrandService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 根据分类id查询品牌列表
     *
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @Override
    public List<BaseBrand> getCategoryByCategoryId(Long category1Id, Long category2Id, Long category3Id) {

        return baseMapper.getCategoryByCategoryId(category1Id, category2Id, category3Id);
    }

    /**
     * 分布式锁测试
     * 未加锁的情况下数据并发拿不到实际结果
     */
//    @Override
    public void setNum1() {
        String value = redisTemplate.opsForValue().get("num");
        if (StringUtils.isEmpty(value)) {
            redisTemplate.opsForValue().set("num", "1");
        } else {
            int num = Integer.parseInt(value);
            redisTemplate.opsForValue().set("num", String.valueOf(++num));
        }
    }


    /**
     * 加锁了，数据访问正确，但是这种方式速度太慢，需要优化
     * 但是这个方法在有多个微服务（集群服务，同一个端口号不同的节点）的时候任然会导致数据错误，
     * 主要是因为有多个本地锁造成的，需要加分布式锁来解决
     * tips：单机模式：多个线程拿到了一把锁；集群模式：多个线程拿到了多把锁
     */
//    @Override
    public synchronized void setNum2() {
        String value = redisTemplate.opsForValue().get("num");
        if (StringUtils.isEmpty(value)) {
            redisTemplate.opsForValue().set("num", "1");
        } else {
            int num = Integer.parseInt(value);
            redisTemplate.opsForValue().set("num", String.valueOf(++num));
        }
    }

    private void doBusiness() {
        String value = redisTemplate.opsForValue().get("num");
        if (StringUtils.isEmpty(value)) {
            redisTemplate.opsForValue().set("num", "1");
        } else {
            int num = Integer.parseInt(value);
            redisTemplate.opsForValue().set("num", String.valueOf(++num));
        }
    }

    /**
     * 分布式锁有多种方案
     * a.基于数据库实现分布式锁，性能低 还可能出现锁表的情况
     * b.zookeeper(可靠性高)
     * c.基于redis
     * d.基于redission(重点使用)
     * <p>
     * 方案一：如果doBusiness出现异常 可能导致锁一直占用 无法释放
     * 解决办法：考虑给锁加过期时间
     */
//    @Override
    public synchronized void setNum3() {
        //利用redis的setNx命令
        boolean acquireLock = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent("lock", "ok"));
        if (acquireLock) {
            //拿到锁 就可以继续执行业务
            doBusiness();
            //做完业务删除锁
            redisTemplate.delete("lock");
        } else {
            //如果拿不到锁就递归等待
            setNum();
        }

    }


    /**
     * 方案二：由于锁的过期时间和业务的过期时间不一致 可能会删除其他线程的锁，
     * 假设线程一执行时间是5秒，但是锁的过期时间是3秒，导致线程提前释放锁，
     * 然后别的线程运行到一半时锁会被线程一给删除
     * 解决办法：设置一个判定值，让线程只能删除自己的锁
     */
//    @Override
    public synchronized void setNum4() {
        //利用redis的setNx命令
        boolean acquireLock = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent("lock", "ok", 3, TimeUnit.SECONDS));
        if (acquireLock) {
            //拿到锁 就可以继续执行业务
            doBusiness();
            //做完业务删除锁
            redisTemplate.delete("lock");
        } else {
            //如果拿不到锁就递归等待
            setNum();
        }
    }

    /**
     * 方案三：判断和删除两个语句不具备原子性
     * 解决办法：设置lua脚本，让判断和删除两个语句具备原子性
     */
//    @Override
    public synchronized void setNum5() {
        //设置一个随机值
        String token = UUID.randomUUID().toString();
        //利用redis的setNx命令
        boolean acquireLock = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent("lock", token, 3, TimeUnit.SECONDS));
        if (acquireLock) {
            //拿到锁 就可以继续执行业务
            doBusiness();
            String redisToken = redisTemplate.opsForValue().get("lock");
            if (token.equals(redisToken)) {
                //做完业务删除锁
                redisTemplate.delete("lock");
            }
        } else {
            //如果拿不到锁就递归等待
            setNum();
        }
    }

    /**
     * 方案四：每一次递归都要执行大量前面的业务代码，浪费性能
     * 解决办法：自旋 只做一件事 拿锁
     */
    //    @Override
    public synchronized void setNum6() {
        //设置一个随机值
        String token = UUID.randomUUID().toString();
        //利用redis的setNx命令
        boolean acquireLock = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent("lock", token, 3, TimeUnit.SECONDS));
        if (acquireLock) {
            //拿到锁 就可以继续执行业务
            doBusiness();
            //定义一个lua脚本
            String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            //把lua脚本塞到redisScript
            redisScript.setScriptText(luaScript);
            //设置执行完脚本的返回类型
            redisScript.setResultType(Long.class);
            //1.脚本对象 2.需要传递的keys 3.对比的值
            redisTemplate.execute(redisScript, Collections.singletonList("lock"), token);

            //用lua脚本取代以下代码
//            String redisToken = redisTemplate.opsForValue().get("lock");
//            if (token.equals(redisToken)) {
//                //做完业务删除锁
//                redisTemplate.delete("lock");
//            }
        } else {
            //如果拿不到锁就递归等待
            setNum();
        }
    }


    /**
     * 分布式锁优化
     * 方案一：会造成死锁
     * 解决办法：重入锁的设计
     */
//        @Override
    public synchronized void setNum7() {
        //假设这里有两百行代码要执行，每次递归都要执行的话，严重损耗性能
        String token = UUID.randomUUID().toString();
        boolean acquireLock = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent("lock", token, 30, TimeUnit.MINUTES));
        if (acquireLock) {
            doBusiness();
            String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(luaScript);
            redisScript.setResultType(Long.class);
            redisTemplate.execute(redisScript, Collections.singletonList("lock"), token);
        } else {
            //自旋 只做一件事 拿锁
            while (true) {
                SleepUtils.millis(50);
                //重试拿锁
                boolean reTryAcquireLock = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent("lock", token, 30, TimeUnit.MINUTES));
                if (reTryAcquireLock) {
                    //拿到锁以后就不要自旋了
                    break;
                }
            }
            setNum();
        }
    }

    /**
     * 方案二：解决了死锁问题，但是没有释放掉锁，原因是在重试拿锁以后token没有传给线程所以线程token为null
     * 解决办法：把token传上来
     */
    public Map<Thread, Boolean> threadMap = new HashMap<>();

    //    @Override
    public synchronized void setNum8() {
        //还有很多操作要执行 去查缓存 去判断 去过滤等操作 200行代码要执行
        Boolean flag = threadMap.get(Thread.currentThread());
        String token = null;
        boolean acquireLock = false;
        //代表第一次来
        if (flag == null || !flag) {
            token = UUID.randomUUID().toString();
            acquireLock = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent("lock", token, 30, TimeUnit.MINUTES));

        } else {
            //代表已经拿到锁了
            acquireLock = true;
        }
        if (acquireLock) {
            doBusiness();
            String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(luaScript);
            redisScript.setResultType(Long.class);
            redisTemplate.execute(redisScript, Collections.singletonList("lock"), token);
        } else {
            //自旋 只做一件事 拿锁
            while (true) {
                SleepUtils.millis(50);
                //重试拿锁
                boolean reTryAcquireLock = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent("lock", token, 30, TimeUnit.MINUTES));
                if (reTryAcquireLock) {
                    //拿到锁以后就不要去自旋了
                    threadMap.put(Thread.currentThread(), true);
                    break;
                }
            }
            setNum();
        }
    }

    /**
     * 方案三：存在内存泄露问题
     * 解决方案：a.通过线上日志以及抓取当前应用的内存模型
     * b.jvisualvm连上应用程序进行观测 发现有个map在不断地进行上涨
     */
    public Map<Thread, String> threadMap1 = new HashMap<>();

    //    @Override
    public synchronized void setNum9() {
        //还有很多操作要执行 去查缓存 去判断 去过滤等操作 200行代码要执行
        String token = threadMap1.get(Thread.currentThread());
        boolean acquireLock = false;
        //代表第一次来
        if (token == null) {
            token = UUID.randomUUID().toString();
            acquireLock = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent("lock", token, 30, TimeUnit.MINUTES));

        } else {
            //代表已经拿到锁了
            acquireLock = true;
        }
        if (acquireLock) {
            doBusiness();
            String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(luaScript);
            redisScript.setResultType(Long.class);
            redisTemplate.execute(redisScript, Collections.singletonList("lock"), token);
            //擦屁股 防止内存泄漏
            threadMap1.remove(Thread.currentThread());
        } else {
            //自旋 只做一件事 拿锁
            while (true) {
                SleepUtils.millis(50);
                //重试拿锁
                boolean reTryAcquireLock = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent("lock", token, 30, TimeUnit.MINUTES));
                if (reTryAcquireLock) {
                    //拿到锁以后就不要去自旋了
                    threadMap1.put(Thread.currentThread(), token);
                    break;
                }
            }
            setNum();
        }
    }

    /**
     * 用线程安全类 ThreadLocal来替代map 其实ThreadLocal的底层用的就是map
     */
    public ThreadLocal<String> threadLocal = new ThreadLocal<>();

    //    @Override
    public synchronized void setNum10() {
        //还有很多操作要执行 去查缓存 去判断 去过滤等操作 200行代码要执行
        String token = threadLocal.get();
        boolean acquireLock = false;
        //代表第一次来
        if (token == null) {
            token = UUID.randomUUID().toString();
            acquireLock = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent("lock", token, 30, TimeUnit.MINUTES));

        } else {
            //代表已经拿到锁了
            acquireLock = true;
        }
        if (acquireLock) {
            doBusiness();
            String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(luaScript);
            redisScript.setResultType(Long.class);
            redisTemplate.execute(redisScript, Collections.singletonList("lock"), token);
            //擦屁股 防止内存泄漏
            threadLocal.remove();
        } else {
            //自旋 只做一件事 拿锁
            while (true) {
                SleepUtils.millis(50);
                //重试拿锁
                boolean reTryAcquireLock = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent("lock", token, 30, TimeUnit.MINUTES));
                if (reTryAcquireLock) {
                    //拿到锁以后就不要去自旋了
                    threadLocal.set(token);
                    break;
                }
            }
            setNum();
        }
    }

    @Override
    public synchronized void setNum() {
        RLock lock = redissonClient.getLock("lock");
        lock.lock();
        try {
            doBusiness();
        } finally {
            lock.unlock();
        }
    }
}
