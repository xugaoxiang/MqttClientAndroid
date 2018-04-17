### 软硬件环境

- ubuntu 16.04 64bit
- Android Studio 2.0
- OTT BOx with android 5.1.1
- mosquitto-1.4.10

### 前言

MQTT(Message Queuing Telemetry Transport),是一个物联网传输协议，它被设计用于轻量级的发布/订阅式消息传输，旨在为低带宽和不稳定的网络环境中的物联网设备提供可靠的网络服务。MQTT是专门针对物联网开发的轻量级传输协议。MQTT协议针对低带宽网络，低计算能力的设备，做了特殊的优化，使得其能适应各种物联网应用场景。本文旨在研究其在消息发布/订阅/接收场景下的应用.

### MQTT协议中的几个重要概念

* 服务端

  是发送消息的客户端和请求订阅的客户端之间的中介,又称为broker.它接收来自客户端的网络连接;接收客户端发布的消息;处理客户端的订阅和取消订阅请求;转发相应消息给符合条件的已订阅客户端.

* 客户端

  订阅相关的消息;发布消息给其它相关的客户端

* 订阅

  订阅包含一个主题过滤器和一个最大的服务质量(QoS)等级.客户端只有订阅了相关主题时,才能接收到对应主题的消息

### mosquitto编译安装及使用

mosquitto是一款实现了MQTT协议v3.1版的开源消息代理软件,下载地址<https://mosquitto.org/download/>,使用的是目前最新的版本1.4.10（ubuntu系统下可以使用apt-get install mosquitto来安装）

```
tar xvf mosquitto-1.4.10.tar.gz
cd mosquitto-1.4.10
make
sudo make install
```

如果不做全局安装的话,需要将lib/libmosquitto.so.1动态库拷贝到/usr/lib/下,然后执行,否则会报动态库无法使用的错误.

安装完毕后,我们来模拟下整个信息推送的过程.

我们用3个Terminal分别表示broker,订阅者和发布者.

Terminal A启动mosquitto broker服务,它可以监听到所有的交互过程

```
mosquitto
```

Terminal B开启订阅服务,之后所有关于该订阅主题的消息,它都能接收到

```
mosquitto_sub -v -t shopping
```

参数-v显示详细信息,-t表示主题

Terminal C发布消息

```
mosquitto_pub -t shopping -m "What a nice day! Go shopping with me?"
```

参数-t表示主题,-m表示具体消息的内容

![mtqq_mosquitto](https://raw.githubusercontent.com/djstava/PostsCollection/master/images/android/mqtt/mqtt_01.png)

### MQTT Androd客户端

利用MQTT3的java实现代码,做了简单的android客户端

![mtqq_mosquitto](https://raw.githubusercontent.com/djstava/PostsCollection/master/images/android/mqtt/mqtt_02.png)

主界面上放了4个按钮,分别对应连接,订阅,发布和断开连接4个操作.在操作之前,必须先启动MQTT服务,我这服务器的ip地址是10.10.10.48,然后在android端按下CONNECT按钮,可以看到logcat的连接成功的打印信息,接着再按下SUBSCRIBE的按钮完成主题为shopping的订阅(作为demo,我这里写死了).为了接收到android端发布的信息,我在服务器端打开了一个终端,同样订阅了shopping为主题的消息.一切准备工作就绪后,按下android端的PUBLISH,完成之后,就可以在订阅的终端看到shopping What a nice day! Go shopping with me?的主题消息.

![mtqq_mosquitto](https://raw.githubusercontent.com/djstava/PostsCollection/master/images/android/mqtt/mqtt_03.png)

![mtqq_mosquitto](https://raw.githubusercontent.com/djstava/PostsCollection/master/images/android/mqtt/mqtt_04.png)

android端除了可以发送消息,当然也可以接收.打开另一个终端,发布一条主题为shopping的消息

```
mosquitto_pub -t shopping -m "Sorry,I have no time."
```

可以看到android中的logcat,显示已经接收到了相应的消息.

![mtqq_mosquitto](https://raw.githubusercontent.com/djstava/PostsCollection/master/images/android/mqtt/mqtt_06.png)

![mtqq_mosquitto](https://raw.githubusercontent.com/djstava/PostsCollection/master/images/android/mqtt/mqtt_05.png)

### Android工程下载

<https://github.com/djstava/MqttClientAndroid>

### broker增加认证

这里以apt安装的来示例，从源码安装的，必须在configure时加上TLS的支持,不然mosquitto_passwd命令行工具是么有的

给用户名为longjing的设置密码

```
sudo mosquitto_passwd -c /etc/mosquitto/passwd longjing
```

然后编辑/etc/mosquitto/mosquitto.conf,增加语句

```
password_file /etc/mosquitto/passwd
allow_anonymous false
```

重新启动

```
mosquitto -c /etc/mosquitto/mosquitto.conf
```

配置完后，mosquitto_sub和mosquitto_pub都需要跟上用户名及密码

```
mosquitto_sub -v -t longjing -u longjing -P longjing
```

```
mosquitto_pub -t longjing -m "Hello mosquitto" -u longjing -P longjing
```

### mosquitto编译错误及解决方法

![mtqq_mosquitto](https://raw.githubusercontent.com/xugaoxiang/material/master/images/android/mqtt/mqtt_07.png)

```
sudo apt install libssl-dev
```

![mtqq_mosquitto](https://raw.githubusercontent.com/xugaoxiang/material/master/images/android/mqtt/mqtt_08.png)

```
sudo apt install libc-ares-dev
```

![mtqq_mosquitto](https://raw.githubusercontent.com/xugaoxiang/material/master/images/android/mqtt/mqtt_10.png)

```
sudo apt install uuid-dev
```

### 参考文献

1 <https://mosquitto.org/documentation/>

2 <http://tokudu.com/post/50024574938/how-to-implement-push-notifications-for-android>

3 <http://blog.csdn.net/xukai871105/article/details/39252653>

4 <http://mqtt.org/>

5 <https://github.com/LichFaker/MqttClientAndroid>