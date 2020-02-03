package com.wy.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author wy
 * @Description 返回信息枚举
 * @createTime 2019/10/31
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ResultCodeEnum {

    /**
     * 唯一成功标识，其它均为失败
     */
    SUCCESS(200, "success"),

    ARGUMENT_ERROR(100400, "参数异常"),

    UNAUTHORIZED(100401, "Unauthorized"),

    LOGIN_EXPIRED_TIME(100407, "登录信息已过期，请重新登录"),

    AUTH_PASS_ERROR(100402, "账号或密码错误"),

    FORBIDDEN(100403, "没有权限"),

    DATA_NULL(100404, "数据不存在"),

    LIMIT_CONTROL(100405, "频率过高，请稍候尝试"),

    LOGIN_ERROR(100406, "登录异常"),

    REPEAT_SUBMIT(100408, "请勿重复提交"),

    SYSTEM_ERROR(100500, "网络延迟，请稍候尝试"),

    OPERATION_FAILED(100503, "操作失败"),

    SYSTEM_BUSY(100505, "系统繁忙，请稍候尝试"),

    DATA_DISABLE(100505, "数据被禁用"),

    USER_PHONE(100502, "需要绑定手机号"),

    RESTRICTED_OPERATION(100501, "不允许执行此操作");

    private Integer code;

    private String msg;


    public static String getMsg(int code) {
        for (ResultCodeEnum resultCodeEnum : ResultCodeEnum.values()) {
            if (code == resultCodeEnum.getCode()) {
                return resultCodeEnum.msg;
            }
        }
        return null;
    }
}
