package com.wshsoft.mybatis;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import com.wshsoft.mybatis.entity.GlobalConfiguration;
import com.wshsoft.mybatis.entity.TableInfo;
import com.wshsoft.mybatis.enums.IdType;
import com.wshsoft.mybatis.mapper.IMetaObjectHandler;
import com.wshsoft.mybatis.toolkit.IdWorker;
import com.wshsoft.mybatis.toolkit.MapUtils;
import com.wshsoft.mybatis.toolkit.StringUtils;
import com.wshsoft.mybatis.toolkit.TableInfoHelper;

/**
 * <p>
 * 自定义 ParameterHandler 重装构造函数，填充插入方法主键 ID
 * </p>
 * 
 * @author Carry xie
 * @Date 2016-03-11
 */
public class MybatisDefaultParameterHandler extends DefaultParameterHandler {

	/**
	 * @see org.apache.ibatis.mapping.BoundSql
	 */
	private static Field additionalParametersField;

	static {
		try {
			additionalParametersField = BoundSql.class.getDeclaredField("additionalParameters");
			additionalParametersField.setAccessible(true);
		} catch (NoSuchFieldException e) {
			// ignored, Because it will never happen.
		}
	}

	private final TypeHandlerRegistry typeHandlerRegistry;
	private final MappedStatement mappedStatement;
	private final Object parameterObject;
	private BoundSql boundSql;
	private Configuration configuration;

	public MybatisDefaultParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
		super(mappedStatement, processBatch(mappedStatement, parameterObject), boundSql);
		this.mappedStatement = mappedStatement;
		this.configuration = mappedStatement.getConfiguration();
		this.typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
		this.parameterObject = parameterObject;
		this.boundSql = boundSql;
	}

    /**
     * <p>
     * 批量（填充主键 ID）
     * </p>
     *
     * @param ms
     * @param parameterObject 插入数据库对象
     * @return
     */
    protected static Object processBatch(MappedStatement ms, Object parameterObject) {
        /* 只处理插入或更新操作 */
        if (ms.getSqlCommandType() == SqlCommandType.INSERT || ms.getSqlCommandType() == SqlCommandType.UPDATE) {
            Collection<Object> parameters = getParameters(parameterObject);
            if (null != parameters) {
                List<Object> objList = new ArrayList<>();
                for (Object parameter : parameters) {
                    TableInfo tableInfo = TableInfoHelper.getTableInfo(parameter.getClass());
                    if (null != tableInfo) {
                        objList.add(populateKeys(tableInfo, ms, parameter));
                    } else {
                        /*
                         * 非表映射类不处理
						 */
						objList.add(parameter);
					}
				}
				return objList;
			} else {
				TableInfo tableInfo = TableInfoHelper.getTableInfo(parameterObject.getClass());
				return populateKeys(tableInfo, ms, parameterObject);
			}
		}
		return parameterObject;
	}

	/**
	 * <p>
	 * 处理正常批量插入逻辑
	 * </p>
	 * <p>
	 * org.apache.ibatis.session.defaults.DefaultSqlSession$StrictMap 该类方法
	 * wrapCollection 实现 StrictMap 封装逻辑
	 * </p>
	 * 
	 * @param parameter
	 *            插入数据库对象
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected static Collection<Object> getParameters(Object parameter) {
		Collection<Object> parameters = null;
		if (parameter instanceof Collection) {
			parameters = (Collection) parameter;
		} else if (parameter instanceof Map) {
			Map parameterMap = (Map) parameter;
			if (parameterMap.containsKey("collection")) {
				parameters = (Collection) parameterMap.get("collection");
			} else if (parameterMap.containsKey("list")) {
				parameters = (List) parameterMap.get("list");
			} else if (parameterMap.containsKey("array")) {
				parameters = Arrays.asList((Object[]) parameterMap.get("array"));
			}
		}
		return parameters;
	}

    /**
     * <p>
     * 自定义元对象填充控制器
     * </p>
     *
     * @param tableInfo       数据库表反射信息
     * @param ms              MappedStatement
     * @param parameterObject 插入数据库对象
     * @return Object
     */
    protected static Object populateKeys(TableInfo tableInfo, MappedStatement ms, Object parameterObject) {
        if (null == tableInfo || StringUtils.isEmpty(tableInfo.getKeyProperty()) || null == tableInfo.getIdType()) {
            /* 不处理 */
            return parameterObject;
        }
        MetaObject metaObject = ms.getConfiguration().newMetaObject(parameterObject);
         /* 自定义元对象填充控制器 */
        IMetaObjectHandler metaObjectHandler = GlobalConfiguration.getMetaObjectHandler(ms.getConfiguration());
        if(ms.getSqlCommandType() == SqlCommandType.INSERT ){
            if (tableInfo.getIdType().getKey() >= 2) {
                Object idValue = metaObject.getValue(tableInfo.getKeyProperty());
            /* 自定义 ID */
                if (StringUtils.checkValNull(idValue)) {
                    if (tableInfo.getIdType() == IdType.ID_WORKER) {
                        metaObject.setValue(tableInfo.getKeyProperty(), IdWorker.getId());
                    } else if (tableInfo.getIdType() == IdType.UUID) {
                        metaObject.setValue(tableInfo.getKeyProperty(), IdWorker.get32UUID());
                    }
                }
            }
            if (null != metaObjectHandler) {
                metaObjectHandler.insertFill(metaObject);
            }
        } else if(ms.getSqlCommandType() == SqlCommandType.UPDATE ){
            if (null != metaObjectHandler) {
                metaObjectHandler.updateFill(metaObject);
            }
        }
        return metaObject.getOriginalObject();
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void setParameters(PreparedStatement ps) {
		// 反射获取动态参数
		Map<String, Object> additionalParameters = null;
		try {
			additionalParameters = (Map<String, Object>) additionalParametersField.get(boundSql);
		} catch (IllegalAccessException e) {
			// ignored, Because it will never happen.
		}
		ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		if (parameterMappings != null) {
			for (int i = 0; i < parameterMappings.size(); i++) {
				ParameterMapping parameterMapping = parameterMappings.get(i);
				if (parameterMapping.getMode() != ParameterMode.OUT) {
					Object value;
					String propertyName = parameterMapping.getProperty();
					if (boundSql.hasAdditionalParameter(propertyName)) { // issue
																			// #448
																			// ask
																			// first
																			// for
																			// additional
																			// params
						value = boundSql.getAdditionalParameter(propertyName);
					} else if (parameterObject == null) {
						value = null;
					} else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
						value = parameterObject;
					} else {
						MetaObject metaObject = configuration.newMetaObject(parameterObject);
						value = metaObject.getValue(propertyName);
						if (value == null && MapUtils.isNotEmpty(additionalParameters)) {
							// issue #138
							value = additionalParameters.get(propertyName);
						}
					}
					TypeHandler typeHandler = parameterMapping.getTypeHandler();
					JdbcType jdbcType = parameterMapping.getJdbcType();
					if (value == null && jdbcType == null) {
						jdbcType = configuration.getJdbcTypeForNull();
					}
					try {
						typeHandler.setParameter(ps, i + 1, value, jdbcType);
					} catch (TypeException | SQLException e) {
						throw new TypeException(
								"Could not set parameters for mapping: " + parameterMapping + ". Cause: " + e, e);
					}
				}
			}
		}
	}
}
