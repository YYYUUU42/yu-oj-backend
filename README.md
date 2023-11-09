# OJ 在线判题系统

## 项目介绍

本项目是基于 Spring Boot + Spring Cloud + Docker + RabbitMQ 的 **编程算法题目在线评测系统**（简称OJ）。

OJ（Online Judge）系统是一个在线算法评测系统，用户可以选择题目、编写代码并提交代码进行评测，而且是高效、稳定的 OJ在线判题评测系统，它能够根据用户提交的代码、出题人预先设置的题目输入和输出用例，进行编译代码、运行代码、判断代码运行结果是否正确。


## 模块说明
- common：系统通用模块，比如用户角色权限校验，异常处理，统一返回值，常量，工具类等
- gateway：系统网关模块：实现了给前端返回统一接口路由，聚合文档（Knife4j），全局跨域配置，权限校验等
- model：系统实体模块，比如用户实体类、题目实体类，VO、枚举等
- service
  - service-file：系统文件模块，比如用户头像上传等
  - service-judge：系统判题模块：调用远程代码沙箱接口，实现工厂模式、策略模式、代理模式，验证代码沙箱执行结果是否正确与错误，使用消息队列实现异步处理消息
  - service-problem：系统题目模块：题目的增删改查、题目提交限流、使用消息队列异步处理消息
  - service-user：系统用户模块，管理员对用户的增删改查，用户自己信息查询，修改，头像上传
- servie-client：系统内部调用模块，给内部系统提供调用接口


## 技术栈



- Spring Cloud Gateway：
  - 对各服务接口进行聚合和路由
  - 自定义CrosWebFilter Bean全局解决了跨域问题
  - 自定义GlobalFilter全局请求拦截器集中解决了权限校验问题
- Spring Boot :
  - AOP切面编程搭配自定义异常来对异常做统一处理，以及对用户权限进行判断
  - 定时任务（每天十二点的定时更新对用户的用户推荐
- MySQL：
  - 数据持久化
- Redis：
  - 由于运算推荐题目的算法时间有点久，每天十二点将定时任务得到推荐题目列表，将其存放到Redis中，提高数据响应时间
  - Redisson分布式锁（只用到了定时任务）
  - 本项目使用到令牌桶限流算法，使用Redisson实现简单且高效分布式限流，限制用户每秒只能调用一次提交一次题目，防止用户恶意占用系统资源
- RabbitMQ：
  - 最简单的运用，异步处理判题，提高响应速度
 

    
 
## 项目展示

### 项目首页
![image](https://github.com/Yu6A/yu-oj-backend/assets/97829326/4a66f220-8d35-4ea0-b964-c3fcd94921c7)
![image](https://github.com/Yu6A/yu-oj-backend/assets/97829326/c5a04158-79db-4af5-aea1-4919a322c071)

### 题目详情
![image](https://github.com/Yu6A/yu-oj-backend/assets/97829326/ec56576c-1de7-44bb-9e05-8a2efdebb943)


### 用户登录与注册
![image](https://github.com/Yu6A/yu-oj-backend/assets/97829326/6e6d940f-48a6-432e-a788-8954a896c123)

![image](https://github.com/Yu6A/yu-oj-backend/assets/97829326/f35d18a7-f53d-43ef-8479-d62aa7e7c458)

### 已提交题目列表展示
![image](https://github.com/Yu6A/yu-oj-backend/assets/97829326/efb32d51-9a66-4feb-ac5f-5557814df102)


### 管理员创建题目
![image](https://github.com/Yu6A/yu-oj-backend/assets/97829326/efe312a9-4365-44a3-857f-242de96b8dd9)
![image](https://github.com/Yu6A/yu-oj-backend/assets/97829326/250087cf-386d-440f-8b96-0b4b7ca8de7c)

### 修改题目
![image](https://github.com/Yu6A/yu-oj-backend/assets/97829326/9d15a032-3768-4ed3-9b50-7c31e985766a)

### 管理题目
![image](https://github.com/Yu6A/yu-oj-backend/assets/97829326/97671ec8-9035-4e84-b301-39875496a6e9)

### 用户管理
![image](https://github.com/Yu6A/yu-oj-backend/assets/97829326/f3e103e3-f44c-438b-8e3d-f00cf8dbf725)

### 用户信息展示
![image](https://github.com/Yu6A/yu-oj-backend/assets/97829326/81a01996-7913-425a-a63b-2706fd18448c)

### 用户信息修改
![image](https://github.com/Yu6A/yu-oj-backend/assets/97829326/16b52f60-cad7-4840-bc9f-97dafc3cb499)

