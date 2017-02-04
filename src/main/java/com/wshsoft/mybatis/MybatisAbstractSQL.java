package com.wshsoft.mybatis;

import com.wshsoft.mybatis.toolkit.StringUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 重定义 AbstractSQL ，实现标准TSQL的 查询条件自定义
 * </p>
 *
 * @author Carry xie
 * @Date 2016-08-22
 */
@SuppressWarnings("serial")
public abstract class MybatisAbstractSQL<T> implements Serializable {

    private static final String AND = " AND ";
    private static final String OR = " OR ";
    private static final String AND_NEW = ") \nAND (";
    private static final String OR_NEW = ") \nOR (";

    /**
     * SQL条件
     */
    private SQLCondition sql = new SQLCondition();

    /**
     * 子类泛型实现
     *
     * @return 泛型实例
     */
    public abstract T getSelf();

    public T WHERE(String conditions) {
        sql().where.add(conditions);
        sql().lastList = sql().where;
        return getSelf();
    }

    public T OR() {
        sql().lastList.add(OR);
        return getSelf();
    }

    public T OR_NEW() {
        sql().lastList.add(OR_NEW);
        return getSelf();
    }

    public T AND() {
        sql().lastList.add(AND);
        return getSelf();
    }

    public T AND_NEW() {
        sql().lastList.add(AND_NEW);
        return getSelf();
    }

    public T GROUP_BY(String columns) {
        sql().groupBy.add(columns);
        return getSelf();
    }

    public T HAVING(String conditions) {
        sql().having.add(conditions);
        sql().lastList = sql().having;
        return getSelf();
    }

    public T ORDER_BY(String columns) {
        sql().orderBy.add(columns);
        return getSelf();
    }

    private SQLCondition sql() {
        return sql;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sql().sql(sb);
        return sb.toString();
    }

    /**
     * SQL连接器
     */
	private static class SafeAppendable implements Serializable {
        private final Appendable appendable;
        private boolean empty = true;

        public SafeAppendable(Appendable appendable) {
            super();
            this.appendable = appendable;
        }

        public SafeAppendable append(CharSequence charSequence) {
            try {
                if (empty && charSequence.length() > 0) {
                    empty = false;
                }
                appendable.append(charSequence);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        public boolean isEmpty() {
            return empty;
        }

    }

    /**
     * SQL条件类
     */
	private static class SQLCondition implements Serializable {

        List<String> where = new ArrayList<String>();
        List<String> having = new ArrayList<String>();
        List<String> groupBy = new ArrayList<String>();
        List<String> orderBy = new ArrayList<String>();
        List<String> lastList = new ArrayList<String>();
        List<String> andOr = new ArrayList<String>();

        public SQLCondition() {
            andOr.add(AND);
            andOr.add(OR);
            andOr.add(AND_NEW);
            andOr.add(OR_NEW);
        }

        /**
         * 构建SQL的条件
         *
         * @param builder     连接器
         * @param keyword     TSQL中的关键字
         * @param parts       SQL条件语句集合
         * @param open        起始符号
         * @param close       结束符号
         * @param conjunction 连接条件
         */
        private void sqlClause(SafeAppendable builder, String keyword, List<String> parts, String open, String close,
                               String conjunction) {
            parts = clearNull(parts);
            if (!parts.isEmpty()) {
                if (!builder.isEmpty()) {
                    builder.append("\n");
                }

                builder.append(keyword);
                builder.append(" ");
                builder.append(open);
                String last = "__";
                for (int i = 0, n = parts.size(); i < n; i++) {
                    String part = parts.get(i);
                    if (i > 0) {
                        if (andOr.contains(part) || andOr.contains(last)) {
                            builder.append(part);
                            last = part;
                            continue;
                        } else {
                            builder.append(conjunction);
                        }
                    }
                    builder.append(part);
                }
                builder.append(close);
            }
        }

        /**
         * 清除LIST中的NULL和空字符串
         *
         * @param parts 原LIST列表
         * @return
         */
        private List<String> clearNull(List<String> parts) {
            List<String> temps = new ArrayList<String>();
            for (String part : parts) {
                if (StringUtils.isEmpty(part)) {
                    continue;
                }
                temps.add(part);
            }
            return temps;
        }

        /**
         * 按标准顺序连接并构建SQL
         *
         * @param builder 连接器
         * @return
         */
        private String buildSQL(SafeAppendable builder) {
            sqlClause(builder, "WHERE", where, "(", ")", AND);
            sqlClause(builder, "GROUP BY", groupBy, "", "", ", ");
            sqlClause(builder, "HAVING", having, "(", ")", AND);
            sqlClause(builder, "ORDER BY", orderBy, "", "", ", ");
            return builder.toString();
        }

        public String sql(Appendable appendable) {
            SafeAppendable builder = new SafeAppendable(appendable);
            return buildSQL(builder);
        }
    }
}
