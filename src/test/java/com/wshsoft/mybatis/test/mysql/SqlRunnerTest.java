package com.wshsoft.mybatis.test.mysql;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.wshsoft.mybatis.mapper.SqlRunner;
import com.wshsoft.mybatis.plugins.Page;
import com.wshsoft.mybatis.test.CrudTest;
import com.wshsoft.mybatis.toolkit.CollectionUtils;
import com.wshsoft.mybatis.toolkit.TableInfoHelper;

/**
 * <p>
 * 测试SqlRunner
 * </p>
 * 
 * @author Carry xie
 * @date 2016-12-11
 */
public class SqlRunnerTest extends CrudTest {

	@Test
	public void test1() {
		/*
		 * 加载配置文件
		 */
		TableInfoHelper.initSqlSessionFactory(this.sqlSessionFactory());

		boolean b = SqlRunner.db().insert("INSERT INTO `test` (`id`, `type`) VALUES ('107880983085826048', 't1021')");
		System.out.println(b);
		Assert.assertTrue(b);
		boolean b1 = SqlRunner.db().update("UPDATE `test` SET `type`='tttttttt' WHERE (`id`=107880983085826048)");
		System.out.println(b1);

		Assert.assertTrue(b1);
		List<Map<String, Object>> maps = SqlRunner.db()
				.selectList("select * from test WHERE (`id`=107880983085826048)");
		System.out.println(maps);
		String type = (String) maps.get(0).get("type");
		System.out.println(type);
		Assert.assertEquals("tttttttt", type);
		boolean b2 = SqlRunner.db().delete("DELETE from test WHERE (`id`=107880983085826048)");
		System.out.println(b2);
		Assert.assertTrue(b2);
		List<Map<String, Object>> maps1 = SqlRunner.db()
				.selectList("select * from test WHERE (`id`=107880983085826048)");
		System.out.println(maps1);
		if (CollectionUtils.isEmpty(maps1)) {
			maps1 = null;
		}
		Assert.assertNull(maps1);
		Page<Map<String, Object>> mapPage = SqlRunner.db().selectPage(new Page<>(1, 5), "select * from test ");
		System.out.println(mapPage);
		int i = SqlRunner.db().selectCount("select count(0) from test ");
		System.out.println("count:" + i);

	}

}
