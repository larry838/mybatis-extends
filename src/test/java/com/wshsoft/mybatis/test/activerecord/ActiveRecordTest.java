package com.wshsoft.mybatis.test.activerecord;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.jdbc.SQL;

import com.wshsoft.mybatis.plugins.Page;
import com.wshsoft.mybatis.test.CrudTest;
import com.wshsoft.mybatis.test.mysql.entity.Test;
import com.wshsoft.mybatis.toolkit.IdWorker;
import com.wshsoft.mybatis.toolkit.TableInfoHelper;

/**
 * <p>
 * ActiveRecord 测试
 * </p>
 * 
 * @author Carry xie
 * @date 2016-10-11
 */
public class ActiveRecordTest extends CrudTest {

    @org.junit.Test
    public void test() {
        TableInfoHelper.initSqlSessionFactory(this.sqlSessionFactory());
        // 保存一条记录
        Test t1 = new Test();
        t1.setType("test10");
        boolean rlt = t1.insert();
        print(" ar save=" + rlt + ", id=" + t1.getId());

        // 根据ID更新
        t1.setType("t1001");
        rlt = t1.updateAllColumnById();
        print(" ar updateAllColumnById:" + rlt);

        t1.setType("t1023");
        rlt = t1.updateById();
        print(" ar updateById:" + rlt);

		// 更新 SQL
		Test t11 = new Test();
		t11.setType("123");
		rlt = t11.update("id={0}", t1.getId());
		print("update sql=" + rlt);

		// 查询 SQL
		Test t10 = t1.selectOne("id={0}", t1.getId());
		print("selectOne=" + t10.getType());

		// 插入OR更新
		t1.setType("t1021");
		rlt = t1.insertOrUpdate();
		print(" ar saveOrUpdate:" + rlt);

		// 根据ID查询
		Test t2 = t1.selectById();
		print(" t2 = " + t2.toString());
		t2.setId(IdWorker.getId());
		t2.insert();

		// 查询所有
		List<Test> tl = t2.selectAll();
		for (Test t : tl) {
			print("selectAll=" + t.toString());
		}

		// 查询总记录数
		print(" count=" + t2.selectCount(null));

		// 翻页查询
		Page<Test> page = new Page<Test>(0, 10);
		page = t2.selectPage(page, null);
		print(page.toString());

		// 根据ID删除
		rlt = t2.deleteById();
		print("deleteById=" + rlt + ", id=" + t2.getId());

		// 执行 SQL 查询总数
		List<Map<String, Object>> ul = t2.sql().selectList(new SQL() {
			{
				SELECT("*");
				FROM("test");
				WHERE("type='t1021'");
			}
		}.toString());
		System.err.println("selectList SQL:");
		for (Map<String, Object> map : ul) {
			System.err.println(map);
		}

		// 根据ID查询
		Test t20 = t2.selectById();
		print("t2 删除后是否存在？" + (null != t20));

		// 删除 SQL
		rlt = t2.delete("type={0}", "t1021");
		System.err.println("delete sql=" + rlt);
	}

	/*
	 * 慢点打印
	 */
	private static void print(String text) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.err.println(text);
	}

}
