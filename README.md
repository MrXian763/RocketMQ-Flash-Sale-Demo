## 技术选型

**Spring Boot + Redis + MySQL + RocketMQ**

## 设计(抢优惠券)

设计 **seckill-web** 接收处理秒杀请求
设计 **seckill-service** 处理秒杀真实业务的

## 部署细节

用户量: 50w
日活量: 1w-2w   1%-5%
qps: 2w+   [自己打日志 | nginx(access.log) ]
几台服务器(什么配置):8C16G  4台    seckill-web : 4台    seckill-service 2台
带宽: 100M

## 技术要点

1. 通过 redis 的 setnx 对用户和商品做去重判断，防止用户刷接口的行为
2. 每天晚上 8 点通过定时任务 把 mysql 中参与秒杀的库存商品，同步到 redis 中去，做库存的预扣减，提升接口性能
3. 通过 RocketMq 消息中间件的异步消息，来将秒杀的业务异步化，进一步提升性能
4. seckill-service 使用并发消费模式，并且设置合理的线程数量，快速处理队列中堆积的消息
5. 使用 redis 的分布式锁+自旋锁，对商品的库存进行并发控制，把并发压力转移到程序中和 redis 中去，减少 db 压力
6. 使用声明式事务注解 Transactional，并且设置异常回滚类型，控制数据库的原子性操作
7. 使用 jmeter 压测工具，对秒杀接口进行压力测试，在8C16G的服务器上，qps2k+，达到压测预期
8. 使用 sentinel 的热点参数限流规则，针对爆款商品和普通商品的区别，区分限制
