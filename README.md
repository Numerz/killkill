# killkill
基于SSM的商城秒杀系统  

在 https://gitee.com/steadyjack/SpringBoot-SecondKill 上做了改进  

未使用消息队列，未完成发邮件功能  


## 用户名和密码
debug用户密码为:12345  

可通过UserController中的main函数手动生成md5加密后的密码，salt在 server/src/main/resources/application.properties 中的 shiro.encrypt.password.salt 属性


### 注意:  
1.修改 server/src/main/resources/application.properties 中相关自定义属性  
2.修改数据库中待秒杀商品的可秒杀时间，以免不可用


## 压测

server/src/test/java/PostSender 使用多线程并发发送post请求（未用线程池）

### 注意:  
1.修改URL及线程数  
2.线程数≠实际并发数
