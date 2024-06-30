package com.taoshao.taobi.model.vo;

import lombok.Data;

/**
 * Bi 的返回结果
 *
 * @Author taoshao
 * @Date 2024/6/25
 */
@Data
public class BiResponse {

    private String genChart;

    private String genResult;

    private Long chartId;
}
