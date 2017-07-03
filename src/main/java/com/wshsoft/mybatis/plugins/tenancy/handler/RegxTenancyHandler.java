package com.wshsoft.mybatis.plugins.tenancy.handler;

import java.util.Properties;
import java.util.regex.Pattern;

import com.wshsoft.mybatis.toolkit.PluginUtils;

/**
 * <p>
 * 租户信息正则处理器
 * </p>
 *
 * @author Carry xie
 * @since 2017-06-20
 */
public class RegxTenancyHandler implements TenancyHandler {
    //默认过滤还是忽略 true表示按租户过滤
    private boolean filterDefault = false;
    private Pattern tablePatterns[];
    private Pattern statementPatterns[];

    @Override
    public void setConfig(Properties properties) {
        this.setFilterStatementRegexStr(PluginUtils.getProperty(properties, "filterStatementRegexStr"));
        this.setFilterTableRegexStr(PluginUtils.getProperty(properties, "filterTableRegexStr"));
        String filterDefault = PluginUtils.getProperty(properties, "filterDefault");
        if (filterDefault != null) this.setFilterDefault(Boolean.valueOf(filterDefault));
    }

    @Override
    public boolean doTable(String table) {
        boolean isOk = filterDefault;
        if (this.tablePatterns != null) {
            for (Pattern p : this.tablePatterns) {
                if (p.matcher(table).find()) {
                    isOk = !filterDefault;
                    break;
                }
            }
        }
        // logger.debug("table:{}  isOK:{}   tablePatterns:{}",table,isOk,tablePatterns);
        return isOk;
    }

    @Override
    public boolean doStatement(String statementId) {
        boolean isOk = filterDefault;
        if (this.statementPatterns != null) {
            for (Pattern p : this.statementPatterns) {
                if (p.matcher(statementId).find()) {
                    isOk = !filterDefault;
                    break;
                }
            }
        }
        //logger.debug("statementId:{}  isOK:{}   statementPatterns:{}",statementId,isOk,statementPatterns);
        return isOk;
    }

    public static Pattern[] compile(String patterString) {
        if (patterString == null) return new Pattern[]{};
        String[] patterStrings = patterString.split(",");
        return compile(patterStrings);
    }

    public static Pattern[] compile(String[] patterStrings) {
        if (patterStrings == null) return new Pattern[]{};
        Pattern[] patterns = new Pattern[patterStrings.length];
        for (int i = 0; i < patterStrings.length; i++) {
            Pattern pattern = Pattern.compile(patterStrings[i]);
            patterns[i] = pattern;
        }
        return patterns;
    }

    public RegxTenancyHandler setFilterTableRegexStr(String tableRegexStr) {
        if (tableRegexStr == null) {
            return this;
        }
        this.tablePatterns = compile(tableRegexStr);
        return this;
    }

    public RegxTenancyHandler setFilterTableRegexArr(String[] tableRegexArr) {
        if (tableRegexArr == null) return this;
        this.tablePatterns = compile(tableRegexArr);
        return this;
    }

    public RegxTenancyHandler setFilterStatementRegexStr(String statementRegexStr) {
        if (statementRegexStr == null) {
            return this;
        }
        this.statementPatterns = compile(statementRegexStr);
        return this;
    }

    public RegxTenancyHandler setFilterStatementRegexArr(String[] statementRegexArr) {
        if (statementRegexArr == null) {
            return this;
        }
        this.statementPatterns = compile(statementRegexArr);
        return this;
    }

    public RegxTenancyHandler setFilterDefault(boolean filterDefault) {
        this.filterDefault = filterDefault;
        return this;
    }
}
