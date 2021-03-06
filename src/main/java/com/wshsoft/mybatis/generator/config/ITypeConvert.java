package com.wshsoft.mybatis.generator.config;

import com.wshsoft.mybatis.generator.config.rules.DbColumnType;

/**
 * <p>
 * 数据库字段类型转换
 * </p>
 * 
 * @author Carry xie
 * @date 2017-01-20
 */
public interface ITypeConvert {

	/**
	 * <p>
	 * 执行类型转换
	 * </p>
	 * 
	 * @param fieldType
	 *            字段类型
	 * @return
	 */
	DbColumnType processTypeConvert(String fieldType);

}
