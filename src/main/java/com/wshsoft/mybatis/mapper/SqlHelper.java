package com.wshsoft.mybatis.mapper;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;

import com.wshsoft.mybatis.entity.GlobalConfiguration;
import com.wshsoft.mybatis.entity.TableInfo;
import com.wshsoft.mybatis.exceptions.MybatisExtendsException;
import com.wshsoft.mybatis.toolkit.TableInfoHelper;

/**
 * <p>
 * SQL 辅助类
 * </p>
 * 
 * @author Carry xie
 * @Date 2016-11-06
 */
public class SqlHelper {

	/**
	 * 获取Session 默认自动提交
	 * <p>
	 * 特别说明:这里获取SqlSession时这里虽然设置了自动提交但是如果事务托管了的话 是不起作用的 切记!!
	 * <p/>
	 *
	 * @return SqlSession
	 */
	public static SqlSession sqlSession(Class<?> clazz) {
		return sqlSession(clazz, true);
	}

	/**
	 * <p>
	 * 批量操作 SqlSession
	 * </p>
	 * 
	 * @param clazz
	 *            实体类
	 * @return SqlSession
	 */
	public static SqlSession sqlSessionBatch(Class<?> clazz) {
		return GlobalConfiguration.currentSessionFactory(clazz).openSession(ExecutorType.BATCH, false);
	}

	/**
	 * <p>
	 * 获取Session
	 * </p>
	 * 
	 * @param clazz
	 *            实体类
	 * @param autoCommit
	 *            true自动提交false则相反
	 * @return SqlSession
	 */
	public static SqlSession sqlSession(Class<?> clazz, boolean autoCommit) {
		return GlobalConfiguration.currentSessionFactory(clazz).openSession(autoCommit);
	}

	/**
	 * 获取TableInfo
	 * 
	 * @return TableInfo
	 */
	public static TableInfo table(Class<?> clazz) {
		TableInfo tableInfo = TableInfoHelper.getTableInfo(clazz);
		if (null == tableInfo) {
			throw new MybatisExtendsException("Error: Cannot execute table Method, ClassGenricType not found .");
		}
		return tableInfo;
	}

	/**
	 * <p>
	 * 判断数据库操作是否成功
	 * </p>
	 *
	 * @param result
	 *            数据库操作返回影响条数
	 * @return boolean
	 */
	public static boolean retBool(int result) {
		return result >= 1;
	}

}
