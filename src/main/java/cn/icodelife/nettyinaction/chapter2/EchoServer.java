package cn.icodelife.nettyinaction.chapter2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * @description: Echo服务器
 * @create: 2018-12-13 09:58
 **/
public class EchoServer {

    private final int port;

    public EchoServer (int port){
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
        //设置端口号，如果端口号参数格式不正确，那么则抛出一个NumberFormatException
        if(args.length !=1){
            System.err.println("Usage: "+EchoServer.class.getSimpleName()+"<port>");
        }

        int port = Integer.parseInt(args[0]);
        //调用服务器的start()方法
        new EchoServer(port).start();
    }

    public void start() throws InterruptedException {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        //创建EcentLoopGroup
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            //创建ServerBootstrap
            ServerBootstrap b = new ServerBootstrap();

            b.group(group).
                    channel(NioServerSocketChannel.class)//指定所使用的NIO传输Channel
                    .localAddress(new InetSocketAddress(port))//使用指定的端口设置套接字地址
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel ch) throws Exception {//添加一个EchoserverHandler到子Channel的ChannelPipeline
                    ch.pipeline().addLast(serverHandler);//EchoServerHandler被标注为@Shareable,所以我们可以总是使用同样的实例
                }
            });
            ChannelFuture f = b.bind().sync();//异步地绑定服务器；调用sync方法阻塞等待直到绑定完成
            f.channel().closeFuture().sync();//获取channel的closeFuture，并且阻塞当前线程直到它完成
        }finally {
            group.shutdownGracefully().sync();//关闭EventLoopGroup,释放所有的资源
        }
    }
}
