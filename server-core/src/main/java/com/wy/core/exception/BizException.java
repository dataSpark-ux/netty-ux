package com.wy.core.exception;

import com.wy.common.enums.ResultCodeEnum;
import lombok.Data;


/**
 * @author wangyi
 * 业务异常
 *
 */
@Data
public class BizException extends RuntimeException {

    private Integer errorCode;

    private String errorMsg;

    public BizException() {
        super();
    }

    public BizException(Integer errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public BizException(Integer errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public BizException(ResultCodeEnum resultCodeEnum) {
        this.errorCode = resultCodeEnum.getCode();
        this.errorMsg = resultCodeEnum.getMsg();
    }
    public BizException(Integer errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }



}
