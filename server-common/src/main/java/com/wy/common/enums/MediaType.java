package com.wy.common.enums;

/**
 * 多媒体类型定义
 *
 * @author yuan
 */
public enum MediaType {

    // 拍照命令
    cmd_take_photo(0x8801),
    // 录音命令
    cmd_audio_recorder(0x8804),
    // 单条存储多媒体上传命令
    cmd_media_upload_single(0x8805),
    // 媒体上传
    cmd_media_upload(0x8803);

    private Integer value;

    MediaType(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

}
