package com.wy.core.trace;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import com.wy.core.protocol.IPackage;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author wangyi
 */
@Slf4j
@Service
public class Trace {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * @param pkg
     */
    @Async
    public void up(IPackage pkg, byte[] origin) {
        if (StrUtil.isBlank(pkg.getHash())) {
            return;
        }
        TraceMessage message = new TraceMessage(pkg);
        message.setUp(true);
        // TODO
        message.setOrigin(HexUtil.encodeHexStr(origin));
        String jsonString = message.toString();
        redisTemplate.convertAndSend(message.getNo(), jsonString);
        redisTemplate.convertAndSend("message", jsonString);
    }

    /**
     * @param pkg
     */
    @Async
    public void down(IPackage pkg, byte[] origin, boolean ok) {
        if (StrUtil.isBlank(pkg.getHash())) {
            return;
        }
        TraceMessage message = new TraceMessage(pkg);
        message.setUp(false);
        message.setDownStatus(ok);
        message.setOrigin(HexUtil.encodeHexStr(origin));
        String jsonString = message.toString();
        redisTemplate.convertAndSend(message.getNo(), jsonString);
    }
}
