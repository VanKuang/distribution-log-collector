package cn.van.kuang.log.collector.common;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.compression.JdkZlibDecoder;
import io.netty.handler.codec.compression.JdkZlibEncoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class NettyClient {

    private static final String HOST = "localhost";
    private static final int PORT = 8888;

    private volatile ChannelHandlerContext channelContext;
    private CountDownLatch latch = new CountDownLatch(1);
    private AtomicBoolean isConnected = new AtomicBoolean(false);

    public void connect() throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            final Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)))
                                    .addLast(new ObjectEncoder())
                                    .addLast(new JdkZlibDecoder())
                                    .addLast(new JdkZlibEncoder())
                                    .addLast(new IdleStateHandler(0, 0, 10))
                                    .addLast(new ConnectionHandler());
                        }
                    });

            final ChannelFuture channelFuture = bootstrap.connect(HOST, PORT).sync();

            channelFuture.channel().closeFuture().sync();
        } catch (Throwable throwable) {
            System.out.println("Fail to start Netty client");
            throwable.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public ChannelHandlerContext getChannelContext() throws InterruptedException {
        if (latch.await(10, TimeUnit.SECONDS)) {
            return channelContext;
        } else {
            throw new RuntimeException("Timeout to get channel context");
        }
    }

    public boolean isConnected() {
        return isConnected.get();
    }

    class ConnectionHandler extends ChannelHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("Connected to Netty server");
            channelContext = ctx;
            latch.countDown();
            isConnected.set(true);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("Channel closed");
            isConnected.set(false);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            isConnected.set(false);

            ctx.close();
            cause.printStackTrace();
        }
    }
}
