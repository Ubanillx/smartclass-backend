package com.ubanillx.smartclass.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;

/**
 * SQL 工具
*/
public class SqlUtils {

    /**
     * 升序
     */
    public static final String SORT_ORDER_ASC = "asc";
    
    /**
     * 降序
     */
    public static final String SORT_ORDER_DESC = "desc";

    /**
     * 校验排序字段是否合法（防止 SQL 注入）
     *
     * @param sortField
     * @return
     */
    public static boolean validSortField(String sortField) {
        if (StringUtils.isBlank(sortField)) {
            return false;
        }
        return !StringUtils.containsAny(sortField, "=", "(", ")", " ");
    }
    
    /**
     * 设置默认排序
     * 
     * @param queryWrapper 查询条件
     * @param sortField 排序字段
     * @param sortOrder 排序顺序
     * @param <T> 查询实体类型
     */
    public static <T> void setDefaultOrder(QueryWrapper<T> queryWrapper, String sortField, String sortOrder) {
        if (SqlUtils.validSortField(sortField)) {
            queryWrapper.orderBy(true, SORT_ORDER_ASC.equalsIgnoreCase(sortOrder), sortField);
        }
    }
}
