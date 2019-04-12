# jetlinks-cloud

基于jetlinks和hsweb的物联网基础云平台

# 结构
```text
--jetlinks-cloud
-------|------common-components                 #通用组件
--------------------|------logging-component    #日志组件
--------------------|------redis-component      #redis组件
--------------------|------service-dependencies #微服务依赖
-------|------dashboard-gateway-service         #后台管理网关服务
-------|------device-gateway-service            #设备网管服务
-------|------device-manager-service            #设备管理服务
-------|------user-service                      #用户服务，鉴权中心

```

# 主要技术栈

1. spring-cloud         微服务框架
2. hsweb-framework      业务基础框架
3. alibaba-nacos        配置中心及服务发现
4. redis                缓存，设备注册中心
5. postgres             业务数据库,可换成mysql 5.7+或者oracle数据库
6. influxDB             时序数据库，存储设备上报事件数据
7. rabbitMQ             消息中间件，微服务间消息，数据传递，
8. spring-cloud-stream  解耦消息中间件，可将rabbitMQ替换为kafka或者rocketMQ
9. jetlinks-gateway     基于vertx的设备连接网关服务
10. rule-engine         规则引擎

# 启动开发环境

要运行完整的功能，需要启动以下服务:

1. nacos-server 1.0.0  端口: 8848
2. redis    端口: 6379
3. postgres 端口: 5432
4. influxdb 端口: 8086
5. rabbitmq 端口: 5672

我们提供了一键启动开发环境的脚本：`docker/dev/run-dev-env.sh`
此脚本需要`docker`以及`docker-compose`支持.
        
        Windows下请使用虚拟机或者直接安装上述服务