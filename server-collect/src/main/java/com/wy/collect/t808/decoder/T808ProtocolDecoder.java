package com.wy.collect.t808.decoder;

import com.sinoxx.sserver.collect.server.t808.T808Filter;
import com.sinoxx.sserver.collect.trace.Trace;
import com.sinoxx.sserver.core.exception.SinoXXException;
import com.sinoxx.sserver.core.protocol.jt808.T808Message;
import com.sinoxx.sserver.core.util.SpringHelper;
import com.wy.collect.collect.trace.Trace;
import com.wy.common.util.SpringHelper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 808协议解析器，专用于 Nettry server  - 解码粘包，808转发
 *
 * @Author 2018-11-23
 */
public class T808ProtocolDecoder extends ByteToMessageDecoder {

    /**
     * 过滤器
     */
    private T808Filter t808Filter;

    /**
     *
     */
    private Trace trace;

    public T808ProtocolDecoder() {
        this.t808Filter = SpringHelper.getBean(T808Filter.class);
        this.trace = SpringHelper.getBean(Trace.class);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in == null) {
            return;
        }

        in.markReaderIndex();
        while (in.isReadable()) {
            in.markReaderIndex();
            int packetBeginIndex = in.readerIndex();
            byte tag = in.readByte();
            /**
             * 解码和粘包
             */
            // 搜索包的开始位置
            if (tag == 0x7E && in.isReadable()) {
                tag = in.readByte();
                // 防止是两个0x7E,取后面的为包的开始位置
                // 寻找包的结束
                while (tag != 0x7E) {
                    if (!in.isReadable()) {
                        // 没有找到结束包，等待下一次包
                        in.resetReaderIndex();
                        return;
                    }
                    tag = in.readByte();
                }
                int pos = in.readerIndex();
                int packetLength = pos - packetBeginIndex;
                if (packetLength > 1) {
                    byte[] tmp = new byte[packetLength];
                    in.resetReaderIndex();
                    in.readBytes(tmp);
                    // 过滤黑名单消息
                    if (t808Filter.isFilterMsg(tmp)) {
                        return;
                    }
                    // 创建消息
                    T808Message message = new T808Message();
                    try {
                        message.readFromBytes(tmp);
                        // 触发接收Message的事件
                        out.add(message);
                        // 发送
                        trace.up(message, tmp);
                    } catch (SinoXXException se) {
                        return;
                    } catch (Exception e) {
                        return;
                    }

                    break;
                } else {
                    // 说明是两个0x7E
                    in.resetReaderIndex();
                    // 两个7E说明前面是包尾，后面是包头
                    in.readByte();
                }
            }
        }
    }

}
