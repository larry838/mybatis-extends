package com.wshsoft.mybatis.plugins.tenant;

import net.sf.jsqlparser.expression.Expression;

/**
 * <p>
 * 租户处理器
 * </p>
 *
 * @author Carry xie
 * @since 2017-08-31
 */
public interface TenantHandler {

    Expression getTenantId();

    String getTenantIdColumn();

    boolean doTableFilter(String tableName);
}