package com.netty.common.codec;

import com.alibaba.fastjson.JSON;
import com.netty.common.message.Invocation;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wy
 * @Description 实现将 Invocation 序列化，并写入到 TCP Socket 中
 * ① MessageToByteEncoder 是 Netty 定义的编码 ChannelHandler 抽象类，将泛型 <I> 消息转换成字节数组。
 *
 * @createTime 2021/03/15
 */
@Slf4j
public class InvocationEncoder extends MessageToByteEncoder<Invocation> {

    /**
    * ② #encode(ChannelHandlerContext ctx, Invocation invocation, ByteBuf out) 方法，进行编码的逻辑。
    */
    @Override
    protected void encode(ChannelHandlerContext ctx, Invocation msg, ByteBuf out) throws Exception {
        // <2.1> 将 Invocation 转换成 byte[] 数组
        byte[] content = JSON.toJSONBytes(msg);
        // <2.2> 写入 length ，写入到 TCP Socket 当中。这样，后续「3.4 InvocationDecoder」
        // 可以根据该长度，解析到消息，解决粘包和拆包的问题。
        out.writeInt(content.length);
        // <2.3> 写入内容
        out.writeBytes(content);
        log.info("[encode][连接({}) 编码了一条消息({})]", ctx.channel().id(), msg.toString());
    }
}
