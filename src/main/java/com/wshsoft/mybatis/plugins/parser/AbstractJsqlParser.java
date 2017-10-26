
package com.wshsoft.mybatis.plugins.parser;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.reflection.MetaObject;

import com.wshsoft.mybatis.exceptions.MybatisExtendsException;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.update.Update;

/**
 * <p>
 * 抽象 SQL 解析类
 * </p>
 *
 * @author Carry xie
 * @Date 2017-06-20
 */
public abstract class AbstractJsqlParser implements ISqlParser {

	// 日志
	protected final Log logger = LogFactory.getLog(this.getClass());

	/**
	 * <p>
	 * 获取优化 SQL 方法
	 * </p>
	 *
	 * @param metaObject
	 *            元对象
	 * @param sql
	 *            SQL 语句
	 * @return SQL 信息
	 */

	@Override
	public SqlInfo optimizeSql(MetaObject metaObject, String sql) {
		if (this.allowProcess(metaObject)) {
			try {
				Statement statement = CCJSqlParserUtil.parse(sql);
				logger.debug("Original SQL: " + sql);
				if (null != statement) {
					return this.processParser(statement);
				}
			} catch (JSQLParserException e) {
				throw new MybatisExtendsException(
						"Failed to process, please exclude the tableName or statementId.\n Error SQL: " + sql, e);
			}
		}
		return null;
	}

	/**
	 * <p>
	 * 执行 SQL 解析
	 * </p>
	 *
	 * @param statement
	 *            JsqlParser Statement
	 * @return
	 */
	public SqlInfo processParser(Statement statement) {
		if (statement instanceof Insert) {
			this.processInsert((Insert) statement);
		} else if (statement instanceof Select) {
			this.processSelectBody(((Select) statement).getSelectBody());
		} else if (statement instanceof Update) {
			this.processUpdate((Update) statement);
		} else if (statement instanceof Delete) {
			this.processDelete((Delete) statement);
		}
		logger.debug("parser sql: " + statement.toString());
		return SqlInfo.newInstance().setSql(statement.toString());
	}

	// 新增
	public abstract void processInsert(Insert insert);

	// 删除
	public abstract void processDelete(Delete delete);

	// 更新
	public abstract void processUpdate(Update update);

	// 查询
	public abstract void processSelectBody(SelectBody selectBody);

	/**
	 * <p>
	 * 判断是否允许执行<br>
	 * 例如：逻辑删除只解析 delete , update 操作
	 * </p>
	 *
	 * @param metaObject
	 *            元对象
	 * @return true
	 */
	public boolean allowProcess(MetaObject metaObject) {
		return true;
	}
}
