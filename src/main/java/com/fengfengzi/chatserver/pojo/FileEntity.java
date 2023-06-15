package com.fengfengzi.chatserver.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 王丰
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileEntity {
    //implements Serializable
    //private static final long serialVersionUID = 1L;

    /**
     * 原始名称
     */
    private String original;
    /**
     * 实际名称
     */
    private String actual;
    /**
     * 虚拟路径
     */
    private String url;
}
