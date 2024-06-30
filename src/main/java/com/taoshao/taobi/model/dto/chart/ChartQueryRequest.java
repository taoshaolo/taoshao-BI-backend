package com.taoshao.taobi.model.dto.chart;

import com.taoshao.taobi.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * 图表信息
 * @TableName chart
 */
@Data
public class ChartQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 分析目标
     */
    private String goal;

    /**
     * 图表名称
     */
    private String name;

    /**
     * 图表类型
     */
    private String chartType;

    /**
     * 创建者id
     */
    private Long userId;


    private static final long serialVersionUID = 1L;
}