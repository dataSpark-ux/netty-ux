package com.wy.collect.collect.t808;

import com.wy.collect.collect.ConnectionManager;
import com.wy.common.enums.TerminalCommandStatus;
import com.wy.core.protocol.T808Message;
import com.wy.core.queue.MsgQueueFactory;
import com.wy.service.entity.TerminalCommandEntity;
import com.wy.service.service.TerminalCommandService;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 808消息连接管理器
 *
 * @author
 */
@Slf4j
@Component
public class T808ConnectionManager extends ConnectionManager<T808Message> {

    private final static AtomicInteger ATOMIC_INTEGER = new AtomicInteger();

    @Autowired
    private TerminalCommandService terminalCommandService;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private MsgQueueFactory msgQueueFactory;

    @Override
    public Class<T808Message> support() {
        return T808Message.class;
    }

    @Override
    protected boolean sendInternal(T808Message msg) {
        boolean isCommand = false;
        boolean isSend = false;
        // 如果是下发的指令, 那么没有流水号
        if (msg.getHeader().getMessageSerialNo() == null) {
            msg.getHeader().setMessageSerialNo(getSerialNo());
            isCommand = true;
        }
        Channel channel = getChannel(msg.getOnlineNo());
        if (channel != null) {
            channel.writeAndFlush(msg.writeToBytes());
            isSend = true;
        }
        if (isCommand) {
            updateCommand(msg, isSend);
        }
        if (log.isDebugEnabled()) {
            log.debug(String.format("<== 下发指令到终端 [%s][%x] ==> [%s]", msg.getSession(), msg.getMessageType(), isSend));
        }
        return isSend;
    }

    /**
     * 当首次连接, 查询下发命令表里面未下发的数据, 下发给终端 New -> Wait | offline -> Wait
     *
     * @param onlineNo
     */
    @Override
    protected void connection(String onlineNo) {
        executorService.submit(() -> {
            List<TerminalCommandEntity> commandList = terminalCommandService.getLatestCommand(onlineNo);
            if (commandList.isEmpty()) {
                return;
            }
            // 待处理 [正在解析处理]
            terminalCommandService.updateToWait(commandList);
            commandList.forEach(msgQueueFactory::send);
        });
    }

    /**
     * noop
     *
     * @param channel
     */
    @Override
    protected void update(String channel) {

    }

    /**
     * 命令成功下发, 更新命令状态 wait -> Processing
     *
     * @param t808Message
     * @param send
     */
    public void updateCommand(T808Message t808Message, boolean send) {
        if (!send) {
            return;
        }
        terminalCommandService.updateCommandStatus(
                t808Message.getOnlineNo(),
                t808Message.getHeader().getMessageSerialNo(),
                t808Message.getMessageType(),
                TerminalCommandStatus.Processing
        );
    }

    /**
     * 平台流水号, 循环递增
     *
     * @return
     */
    public static short getSerialNo() {
        return (short) (ATOMIC_INTEGER.incrementAndGet() % Short.MAX_VALUE);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
    }
}
