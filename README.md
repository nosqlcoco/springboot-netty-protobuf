# springboot-netty-protobuf

#### 一、说明
springboot集成netty，使用protobuf作为数据交换格式，可以用于智能终端云端服务脚手架。

- 已完成功能(2017/03/12)
	1. 接入验证
	- 心跳检测
	- 断开重连
	- 上传数据
	- 主动推送功能
- 待完成
	1. SSL验证
	- 上传文件

#### 二、开发环境
- JDK8+
- Eclipse Neon.2 Release
- springboot1.4.2
- Netty4.1.6.Final
- protobuf-java3.0.0


#### 三、使用方法
项目里面包含Socket客户端和服务端
- 找到com.netty.server.Application类右键debug as启动SocketServer
- 找到com.netty.client.NettyClient类右键debug as启动客户端

PS:我的公众号：

![](https://github.com/cocoli/weixin_smallexe/blob/master/screenshot/dingyuhao.JPG?raw=true)
