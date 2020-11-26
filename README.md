mybatis-generator-gui-plus
==============

mybatis-generator-gui-plus是基于 [mybatis generator-gui](https://github.com/zouzg/mybatis-generator-gui) 扩展的工具，区别在于功能上的扩展了批量操作的方法，其他几乎没有差别。

增加的方法说明：

批量更新 batchUpdate

批量可选更新 batchUpdateSelective

批量插入 batchInsert

条件查询返回一个 selectFirstByExample

![image](https://user-images.githubusercontent.com/3505708/49334784-1a42c980-f619-11e8-914d-9ea85db9cec3.png)


![basic](https://user-images.githubusercontent.com/3505708/51911610-45754980-240d-11e9-85ad-643e55cafab2.png)


![overSSH](https://user-images.githubusercontent.com/3505708/51911646-5920b000-240d-11e9-9048-738306a56d14.png)

### 核心特性
* 按照界面步骤轻松生成代码，省去XML繁琐的学习与配置过程
* 保存数据库连接与Generator配置，每次代码生成轻松搞定
* 内置常用插件，比如分页插件
* 支持OverSSH 方式，通过SSH隧道连接至公司内网访问数据库
* 把数据库中表列的注释生成为Java实体的注释，生成的实体清晰明了
* 可选的去除掉对版本管理不友好的注释，这样新增或删除字段重新生成的文件比较过来清楚
* 目前已经支持Mysql、Mysql8、Oracle、PostgreSQL与SQL Server，暂不对其他非主流数据库提供支持。(MySQL支持的比较好，其他数据库有什么问题可以在issue中反馈)

### 要求
本工具由于使用了JavaFX 特性，需要JDK <strong>1.8</strong>及以上版本，支持 OracleJDK、OpenJDK。

### 下载
推荐使用 Git 工具克隆项目到本地。


### 启动本软件

* 方法一：IDE中运行
  
   在 Eclipse 或 IntelliJ IDEA 中打开项目，该项目下依赖两个模块，其中一个 `mybatis-generator-core` 未建立关联，需要手动导入 `mybatis-generator-core` 的 pom 文件，在`mybatis-generator-gui`模块下找到`com.zzg.mybatis.generator.MainUI`类并运行就可以了（检查你的IED运行的jdk版本是否符合要求）。

* 其他方法: 可参考原项目中的其他方式，其他方式本项目均为测试过是否可用，不保证可以启动。推荐在IDE中启动。


### 注意事项
* 本自动生成代码工具只适合生成单表的增删改查，对于需要做数据库联合查询的，请自行写新的XML与Mapper；
* 部分系统在中文输入方法时输入框中无法输入文字，请切换成英文输入法；
* 如果不明白对应字段或选项是什么意思的时候，把光标放在对应字段或Label上停留一会然后如果有解释会出现解释；


### 贡献
目前本工具只是本人项目人使用到了并且觉得非常有用所以把它开源，如果你觉得有用并且想改进本软件，你可以：
* 对于你认为有用的功能，你可以在Issue提，我可以开发的尽量满足
* 对于有Bug的地方，请按如下方式在Issue中提bug
    * 如何重现你的bug，包括你使用的系统，JDK版本，数据库类型及版本
    * 如果有任何的错误截图会更好
    * 如果你是一些常见的数据库连接、软件启动不了等问题，请先仔细阅读上面的文档（问问题的时候尽量把各种信息都提供好，否则只是几行文字是没有人愿意为你解答的）。
    
- - -
Licensed under the Apache 2.0 License

Copyright 2020 by derotyoung
