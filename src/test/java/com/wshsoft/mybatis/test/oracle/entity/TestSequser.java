package com.wshsoft.mybatis.test.oracle.entity;

import java.io.Serializable;

import com.wshsoft.mybatis.annotations.TableField;
import com.wshsoft.mybatis.annotations.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户表
 */
@Data
@Accessors(chain = true)
@TableName("TEST_SEQUSER")
// @KeySequence("SEQ_TEST")
public class TestSequser extends BaseTestEntity implements Serializable {

	@TableField(exist = false)
	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	// @TableId(value = "TEST_ID", type = IdType.INPUT)
	// private Long id;
	/**
	 * 主键ID
	 */
	// @TableId(value = "ID")
	// private Long id;

	/**
	 * 名称
	 */
	@TableField(value = "NAME")
	private String name;

	/**
	 * 年龄
	 */
	@TableField(value = "age")
	private Integer age;

	/**
	 * 测试下划线字段命名类型
	 */
	@TableField(value = "TEST_TYPE")
	private Integer testType;

	public TestSequser() {

	}

	public TestSequser(String name, Integer age, Integer testType) {
		this.name = name;
		this.age = age;
		this.testType = testType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Integer getTestType() {
		return testType;
	}

	public void setTestType(Integer testType) {
		this.testType = testType;
	}

	// public Long getId() {
	// return id;
	// }

	// public void setId(Long id) {
	// this.id = id;
	// }

}
