package com.wshsoft.mybatis.test.h2.entity.persistent;

import java.math.BigDecimal;

import com.wshsoft.mybatis.annotations.TableField;
import com.wshsoft.mybatis.annotations.TableName;
import com.wshsoft.mybatis.annotations.Version;
import com.wshsoft.mybatis.enums.FieldStrategy;
import com.wshsoft.mybatis.test.h2.entity.SuperEntity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 测试用户类
 * </p>
 *
 * @author Carry xie
 */
/* 表名 value 注解【 驼峰命名可无 】, resultMap 注解测试【 映射 xml 的 resultMap 内容 】 */
@Data
@Accessors(chain = true)
@TableName
public class H2User extends SuperEntity {
    /* 测试忽略验证 */
    private String name;

    private Integer age;

    /*BigDecimal 测试*/
    private BigDecimal price;

    /* 测试下划线字段命名类型, 字段填充 */
    @TableField(value = "test_type", strategy = FieldStrategy.IGNORED)
    private Integer testType;

    private String desc;

    @Version
    private Integer version;


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

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getTestType() {
		return testType;
	}

	public void setTestType(Integer testType) {
		this.testType = testType;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public H2User() {

    }

    public H2User(String name) {
        this.name = name;
    }

    public H2User(Integer testType) {
        this.testType = testType;
    }

    public H2User(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public H2User(Long id, String name) {
        this.setId(id);
        this.name = name;
    }

    public H2User(Long id, Integer age) {
        this.setId(id);
        this.age = age;
    }

    public H2User(Long id, String name, Integer age, Integer testType) {
        this.setId(id);
        this.name = name;
        this.age = age;
        this.testType = testType;
    }

    public H2User(String name, Integer age, Integer testType) {
        this.name = name;
        this.age = age;
        this.testType = testType;
    }

}
