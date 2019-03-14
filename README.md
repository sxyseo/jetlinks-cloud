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