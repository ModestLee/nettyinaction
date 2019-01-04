package cn.icodelife.nettyinaction.chapter2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * @description: 客户端
 * @create: 2018-12-15 21:33
 **/
public class EchoClient {

    private final String host;

    private final int port;

    public EchoClient(String host, int port){
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception{
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            //创建Bootstrap
            Bootstrap b = new Bootstrap();
            b.group(group)//指定EventLoopGroup以处理客户端事件；需要适用于NIO的实现
                    .channel(NioSocketChannel.class)//适用于NIO传输的Channel类型
                    .remoteAddress(new InetSocketAddress(host,port))//设置服务器的InetSocketAddress
                    .handler(new ChannelInitializer<SocketChannel>() {//在创建Channel时，想ChannelPipeline中添加一个EchoClientHandler实例
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoClientHandler());
                        }
                    });

            ChannelFuture f = b.connect().sync();//连接到远程节点，阻塞等待直到连接完成
            f.channel().closeFuture().sync();//阻塞，直到Channel关闭
        }finally {
            group.shutdownGracefully().sync();//光比线程池并且释放所有的资源
        }
    }

    public static void main(String[] args) throws Exception {
        //设置端口号，如果端口号参数格式不正确，那么则抛出一个NumberFormatException
        if(args.length !=2){
            System.err.println("Usage: "+EchoServer.class.getSimpleName()+"<host><port>");
            return;
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        //调用服务器的start()方法
        new EchoClient(host,port).start();
    }

}