package com.wy.common.enums;

/**
 * 终端命令生命周期状态明细 说明
 * <p>
 * 1、New            # 新建状态, 等待服务器处理
 * 2、Wait           # 服务器已经读取, 正在进行解析
 * 3、Processing     # 命令解析成功, 已将命令发送, 等待终端应答
 * <p>
 * 4、Success        # 终端已经应答命令, 结果为, 命令执行成功
 * 5、Invalid        # 命令解析失败, 下发命令格式错误
 * 6、Offline        # 命令解析完毕, 下发时终端不在线, 发送失败
 * <p>
 * 7、Failed         # 终端已经应答命令, 结果为, 命令执行失败
 * 8、NotSupport     # 终端已经应答命令, 结果为, 设备不支持此命令
 * 9、MessageIsError # 终端已经应答命令, 结果为, 设备不支持此命令
 *
 * @author jiayuan 2019-03-06
 */
public enum TerminalCommandStatus {

    // 创建命令未处理
    New("初始化"),
    // 系统已经读取, 正在处理
    Wait("等待响应"),
    // 已经处理, 且成功发送到终端
    Processing("发送完毕"),
    Invalid("命令格式错误"),
    // 终端相应命令处理结果
    Success("成功"),

    MessageIsError("终端应答 , 设备不支持该指令 2"),
    NotSupport("终端应答 , 设备不支持该指令 3"),
    Failed("终端应答 , 执行失败 1"),

    Offline("解析完成, 发送失败"),

    /**
     * 终端主动上报信息, 构建虚拟的命令记录
     */
    CommandFormTerminal("terminal"),
    /**
     * 平台信息点播服务, 构建的信息命令
     */
    CommandFormTimer("timer"),

    // 多媒体
    Uploaded("多媒体数据上传完毕");

    private String value;

    TerminalCommandStatus(String value) {
        this.value = value;
    }

    public String getTip() {
        return value;
    }
}
