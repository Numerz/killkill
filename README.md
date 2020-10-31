# killkill
基于SSM的商城秒杀系统  

在 https://gitee.com/steadyjack/SpringBoot-SecondKill 上做了改进  


## 用户名和密码
debug用户密码为:12345  
可通过UserController中的main函数手动生成md5加密后的密码，salt在server/src/main/resources/application.properties中的shiro.encrypt.password.salt属性


PS:  
1.注意修改server/src/main/resources/application.properties中相关自定义属性  
2.注意自己修改数据库中待秒杀商品的可秒杀时间，以免不可用
