# MCToKook Plugin

这是一个可以让MC的聊天与指定的一个Kook的频道互通的mc服务器插件\
版本：Minecraft 1.19.3\
基于Spigot api 与 JKook api 与 KookBC

<p>
    <a  href="https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html">
		<img src="https://img.shields.io/badge/JDK-8+-green.svg"  alt=""/>
	</a>
    <a href="https://www.spigotmc.org">
        <img src="https://img.shields.io/badge/-Bukkit-yellow" alt=""/>
    </a>
    <a href= "https://github.com/SNWCreations/KookBC">
        <img src="https://img.shields.io/badge/-KookBC-red" alt="">
    </a>
    <a href= "https://github.com/SNWCreations/JKook">
        <img src="https://img.shields.io/badge/-JKook-red" alt="">
    </a>
</p>
<p>
    <a href="https://mit-license.org/">
        <img src="https://img.shields.io/badge/license-MIT-green" alt="">
    </a>
</p>

## 插件用法

将插件放到Spigot服务端(理应支持Spigot api的服务端也可以加载)的plugins文件夹里,启动服务器.\
第一次启动会生成配置文件,但是会直接变成禁用,因为此时配置文件里并没有提供bot的token与你想要实现互通的Kook频道ID

获取bot-token: 访问[kook开发者中心](https://developer.kookapp.cn/app/index) ,
新建应用,在应用详情里点左边的机器人,此刻你将会看到你机器人的token,将其填入至插件的配置文件的对于位置当中.\
具体的机器人相关问题请好好查阅[kook开发者中心](https://developer.kookapp.cn/app/index)的文档 \
注意: 请保管好机器人的token,不要随意泄露,虽然它可以随意重置,但是请还是做好相关工作.

获取Kook频道ID: 在Kook的设置里找到高级设置,将开发者模式启用\
然后回到你想实现互通的服务器频道,右键频道,你会发现多了一个选项叫做：复制ID 点击就可以将频道ID复制至剪贴板\
然后你就可以将它填入至插件的配置文件的对于位置当中.

# 注意！

插件的配置文件需要严格遵循yml文件格式！\
插件的配置文件需要严格遵循yml文件格式！\
插件的配置文件需要严格遵循yml文件格式！\
重要的事情说三遍
例如: bot-token:处填写正确的样子应该为 bot-token: dlksaj3k2j4hkjfdkjashj2k4 \
(这里的这个token并不存在,是我脸滚键盘打出来的)\
注意:的后面有个空格,相信拥有开服经验的人很清楚,在此只为提醒经验不多的人

# 插件有什么功能？

让MC的聊天与指定的一个Kook的频道互通\
可以自定义样式的信息,请查阅配置文件里的注释以使用\
可开关的功能(虽然功能不多),请查阅配置文件里的注释以配置\
在mc中点击来自Kook频道的消息可快速@Kook频道中的用户\

# 关于开发

理论上这个代码也可以用于其他版本的MC上，只需要导入工程,然后修改pom.xml里的spigot-api版本\
然后重新打包即可
另外如果没法打包,请确认新旧版本的spigot-api的区别\
以及maven-shade-plugin的版本是否在3.2.4(低于这个版本可能会有问题)\
另外,在迁移至旧版本的时候,应该使用shade的relocation功能\
重定向org.yaml.snakeyaml与com.google.gson(因为旧版本的spigot-api里的这两个库相对较旧,缺失新版的方法)\
(我测试的版本为1.12.2,在更低的版本中也许有更多需要重定向的依赖)\
这是一个例子:
```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.3.0</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
            <configuration>
                <createDependencyReducedPom>false</createDependencyReducedPom>
                <relocations>
                    <relocation>
                        <pattern>org.yaml.snakeyaml</pattern>
                        <shadedPattern>com.xiaoace.include.org.yaml.snakeyaml</shadedPattern>
                    </relocation>
                    <relocation>
                        <pattern>com.google.gson</pattern>
                        <shadedPattern>com.xiaoace.include.com.google.gson</shadedPattern>
                    </relocation>
                </relocations>
           </configuration>
        </execution>
    </executions>
</plugin>
```
重定向的部分为:
```
<relocations>
    <relocation>
        <pattern>org.yaml.snakeyaml</pattern>
        <shadedPattern>com.xiaoace.include.org.yaml.snakeyaml</shadedPattern>
    </relocation>
    <relocation>
        <pattern>com.google.gson</pattern>
        <shadedPattern>com.xiaoace.include.com.google.gson</shadedPattern>
    </relocation>
</relocations>
```

# 插件的实现原理

通过将KookBC嵌入插件当中,监听mc玩家消息然后通过机器人发送到频道中,监听Kook频道的消息将其发送到MC服务器中.

# 最后

如果你有什么想法与建议,请直接与我交流,不要害羞.\
另外,这是我写的第一个MC插件,如果哪位大佬有代码上的建议,请指出我做的不好的地方,谢谢.


