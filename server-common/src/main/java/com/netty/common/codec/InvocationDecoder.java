package com.netty.common.codec;

import com.alibaba.fastjson.JSON;
import com.netty.common.message.Invocation;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author wy
 * @Description 实现从 TCP Socket 读取字节数组，反序列化成 Invocation。
 * ① ByteToMessageDecoder 是 Netty 定义的解码 ChannelHandler 抽象类
 * ，在 TCP Socket 读取到新数据时，触发进行解码。
 * @createTime 2021/03/15
 */
@Slf4j
public class InvocationDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 标记当前读取位置
        in.markReaderIndex();
        // 判断是否能够读取length 长度
        if (in.readableBytes() <= 4) {
            return;
        }
        // 读取长度
        int length = in.readInt();
        if (length < 0) {
            throw new CorruptedFrameException("negative length: " + length);
        }
        // 如果 message 不够可读，则退回到原读取位置
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }
        // 读取内容
        byte[] content = new byte[length];
        in.readBytes(content);
        Invocation invocation = JSON.parseObject(content, Invocation.class);
        // 添加 List<Object> out 中，交给后续的 ChannelHandler 进行处理
        out.add(invocation);
        log.info("[decode][连接({}) 解析到一条消息({})]", ctx.channel().id(), invocation.toString());

    }
}
