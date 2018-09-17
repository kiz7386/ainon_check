/*******************************************************************************
 ******************************************************************************/
package com.ainon.check_account.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


/**
 * 
 * <P>TODO</P>
 * 
 * @version 1.0
 * @author
 */
public abstract class MapJavaObjectConverter {

	private static String convertToJavaObjectError = "map 转换成 java object错误！";
	private static String convertToMapError = "java object  转换成 错误！map";
	private static String convertToMapStringTypeError = "java object 中value 不是String类型 转换成 错误！map";
	private static String classStr = "class";
	private final static Logger logger = LoggerFactory.getLogger(MapJavaObjectConverter.class);

	/**
	 * 
	 * <p>TODO</p>
	 * 
	 * @param javaObjectType
	 * @param isNullAsEmpty null to ""
	 * @param map
	 * @return
	 * @author 黄雄星（13077862552） 2016-05-07 上午11:43:24
	 */
	public static Map<String, String> object2MapString(final Object o) {
		try {
			Map<String, String> map = new HashMap<String, String>();
			BeanInfo beanInfo = Introspector.getBeanInfo(o.getClass());
			for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
				String name = descriptor.getName();
				if (classStr.equals(name))
					continue;
				Object obj = descriptor.getReadMethod().invoke(o);
				if (BigDecimal.class == descriptor.getPropertyType()) {
					map.put(name, obj == null ? "" : String.valueOf(((BigDecimal) obj).setScale(0)));
				} else {
					map.put(name, obj == null ? "" : String.valueOf(obj));
				}
			}
			return map;
		} catch (IntrospectionException e) {
			logger.error(convertToMapError, e);
		} catch (IllegalArgumentException e) {
			logger.error(convertToMapError, e);
		} catch (IllegalAccessException e) {
			logger.error(convertToMapError, e);
		} catch (InvocationTargetException e) {
			logger.error(convertToMapError, e);
		}
		return null;
	}

	public static <T> T mapToObject(final Class<T> javaObjectType, final Map<String, Object> map,
			final Class<?> listSubClazz) {
		T javaObject = null;
		try {
			final BeanInfo beanInfo = Introspector.getBeanInfo(javaObjectType);
			// 获取类属性
			javaObject = javaObjectType.newInstance();
			for (final PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
				final String propertyName = descriptor.getName();
				if (classStr.equals(propertyName)) {
					continue;
				}
				if (map.containsKey(propertyName)) {
					final Object value = map.get(propertyName);
					if (value == null) {
						continue;
					}
					if (java.lang.Integer.class == descriptor.getPropertyType()) {
						descriptor.getWriteMethod().invoke(javaObject,
								new Integer(value == null ? (String) value : value.toString()));
					} else if (BigDecimal.class == descriptor.getPropertyType()) {
						descriptor.getWriteMethod().invoke(javaObject,
								new BigDecimal(value == null ? (String) value : value.toString()));
					} else if (Long.class == descriptor.getPropertyType()) {
						descriptor.getWriteMethod().invoke(javaObject,
								new Long(value == null ? (String) value : value.toString()));
					} else if (Double.class == descriptor.getPropertyType()) {
						descriptor.getWriteMethod().invoke(javaObject,
								new Double(value == null ? (String) value : value.toString()));
					} else if (Float.class == descriptor.getPropertyType()) {
						descriptor.getWriteMethod().invoke(javaObject,
								new Float(value == null ? (String) value : value.toString()));
					} else if (Boolean.class == descriptor.getPropertyType()) {
						descriptor.getWriteMethod().invoke(javaObject,
								new Boolean(value == null ? (String) value : value.toString()));
					} else if (List.class == descriptor.getPropertyType() && listSubClazz != null) {
						descriptor.getWriteMethod().invoke(javaObject,
								mapToObject(listSubClazz, (List<Map<String, Object>>) value));
					} else {
						descriptor.getWriteMethod().invoke(javaObject, value);
					}
				}
			}
		} catch (final IntrospectionException e) {
			logger.error(convertToJavaObjectError, e);
		} catch (final IllegalArgumentException e) {
			logger.error(convertToJavaObjectError, e);
		} catch (final IllegalAccessException e) {
			logger.error(convertToJavaObjectError, e);
		} catch (final InvocationTargetException e) {
			logger.error(convertToJavaObjectError, e);
		} catch (final InstantiationException e) {
			logger.error(convertToJavaObjectError, e);
		}
		return javaObject;
	}

	/**
	 * 
	 * <p>TODO</p>
	 * 
	 * @param javaObjectType
	 * @param map
	 * @return
	 * @author 黄雄星（13077862552） 2013-12-20 上午11:43:24
	 */
	public static <T> T mapToObject(final Class<T> javaObjectType, final Map<String, Object> map) {
		return mapToObject(javaObjectType, map, null);
	}

	/**
	 * 
	 * <p>TODO</p>
	 * 
	 * @param javaObjectType
	 * @param map
	 * @return
	 * @author 林仙龙（15361632946） 2013-12-20 上午11:43:24
	 */
	public static <T> List<T> mapToObject(final Class<T> javaObjectType, final List<Map<String, Object>> sources) {
		List<T> javaObjectList = Lists.newArrayList();
		try {
			final BeanInfo beanInfo = Introspector.getBeanInfo(javaObjectType);

			for (Map<String, Object> map : sources) {
				T javaObject = javaObjectType.newInstance();
				// 获取类属性
				for (final PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
					final String propertyName = descriptor.getName();
					if (classStr.equals(propertyName)) {
						continue;
					}
					if (map.containsKey(propertyName)) {
						final String value = map.get(propertyName) == null ? null : map.get(propertyName).toString();
						if (value == null) {
							continue;
						}
						if (java.lang.Integer.class == descriptor.getPropertyType()) {
							descriptor.getWriteMethod().invoke(javaObject, new Integer(value));
						} else if (BigDecimal.class == descriptor.getPropertyType()) {
							descriptor.getWriteMethod().invoke(javaObject, new BigDecimal(value));
						} else if (Long.class == descriptor.getPropertyType()) {
							descriptor.getWriteMethod().invoke(javaObject, new Long(value));
						} else if (Double.class == descriptor.getPropertyType()) {
							descriptor.getWriteMethod().invoke(javaObject, new Double(value));
						} else if (Float.class == descriptor.getPropertyType()) {
							descriptor.getWriteMethod().invoke(javaObject, new Float(value));
						} else if (Boolean.class == descriptor.getPropertyType()) {
							descriptor.getWriteMethod().invoke(javaObject, new Boolean(value));
						} else if (String.class == descriptor.getPropertyType()) {
							descriptor.getWriteMethod().invoke(javaObject, new String(value));
						} else {
							descriptor.getWriteMethod().invoke(javaObject, value);
						}
					}
				}
				javaObjectList.add(javaObject);
			}
		} catch (final IntrospectionException e) {
			logger.error(convertToJavaObjectError, e);
		} catch (final IllegalArgumentException e) {
			logger.error(convertToJavaObjectError, e);
		} catch (final IllegalAccessException e) {
			logger.error(convertToJavaObjectError, e);
		} catch (final InvocationTargetException e) {
			logger.error(convertToJavaObjectError, e);
		} catch (final InstantiationException e) {
			logger.error(convertToJavaObjectError, e);
		}
		return javaObjectList;
	}

	/**
	 * <p>String map 转 object</p>
	 * 
	 * @param javaObjectType
	 * @param map
	 * @return
	 * @author 谢志平 2016-1-7 上午11:50:47
	 */
	public static <T> T mapStringToObject(final Class<T> javaObjectType, final Map<String, String> map) {
		T javaObject = null;
		try {
			final BeanInfo beanInfo = Introspector.getBeanInfo(javaObjectType);
			// 获取类属性
			javaObject = javaObjectType.newInstance();
			for (final PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
				final String propertyName = descriptor.getName();
				if (classStr.equals(propertyName)) {
					continue;
				}
				if (map.containsKey(propertyName)) {
					final String value = map.get(propertyName);
					if (value == null) {
						continue;
					}
					if (java.lang.Integer.class == descriptor.getPropertyType()) {
						descriptor.getWriteMethod().invoke(javaObject, new Integer(value));
					} else if (BigDecimal.class == descriptor.getPropertyType()) {
						descriptor.getWriteMethod().invoke(javaObject, new BigDecimal(value));
					} else if (Long.class == descriptor.getPropertyType()) {
						descriptor.getWriteMethod().invoke(javaObject, new Long(value));
					} else if (Double.class == descriptor.getPropertyType()) {
						descriptor.getWriteMethod().invoke(javaObject, new Double(value));
					} else if (Float.class == descriptor.getPropertyType()) {
						descriptor.getWriteMethod().invoke(javaObject, new Float(value));
					} else if (Boolean.class == descriptor.getPropertyType()) {
						descriptor.getWriteMethod().invoke(javaObject, new Boolean(value));
					} else {
						descriptor.getWriteMethod().invoke(javaObject, value);
					}
				}
			}
		} catch (final IntrospectionException e) {
			logger.error(convertToJavaObjectError, e);
		} catch (final IllegalArgumentException e) {
			logger.error(convertToJavaObjectError, e);
		} catch (final IllegalAccessException e) {
			logger.error(convertToJavaObjectError, e);
		} catch (final InvocationTargetException e) {
			logger.error(convertToJavaObjectError, e);
		} catch (final InstantiationException e) {
			logger.error(convertToJavaObjectError, e);
		}
		return javaObject;
	}

	/**
	 * <p>支持 list 转换map</p>
	 * 
	 * @param source
	 * @return
	 * @author 林仙龙（15361632946） 2016-4-25 下午12:42:33
	 */
	public static Map<Object, Object> listToMap(List<Object> source) {
		try {
			Map<Object, Object> objectToMapString = Maps.uniqueIndex(source, new Function<Object, Object>() {
				@Override
				public Object apply(Object column) {
					return column;
				}
			});
			return objectToMapString;
		} catch (Exception e) {
			logger.error(convertToMapError, e);
		}
		return null;
	}

	/**
	 * 
	 * <p>object to map</p>
	 * 
	 * @param o
	 * @param isIgnoreBlankOrNull :忽略null及空字符串 转换成map
	 * @return
	 * @author 黄雄星（13077862552） 2014-5-20 上午10:16:50
	 */
	public static Map<String, Object> objectToMap(final Object o, boolean isIgnoreBlankOrNull) {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			BeanInfo beanInfo = Introspector.getBeanInfo(o.getClass());
			for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
				String name = descriptor.getName();
				if (classStr.equals(name))
					continue;
				Object obj = descriptor.getReadMethod().invoke(o);
				if (isIgnoreBlankOrNull
						&& (obj == null || (obj instanceof String && StringUtils.isBlank((String) obj)))) {

				} else {
					map.put(name, obj);
				}
			}
			return map;
		} catch (IntrospectionException e) {
			logger.error(convertToMapError, e);
		} catch (IllegalArgumentException e) {
			logger.error(convertToMapError, e);
		} catch (IllegalAccessException e) {
			logger.error(convertToMapError, e);
		} catch (InvocationTargetException e) {
			logger.error(convertToMapError, e);
		}
		return null;
	}

	/**
	 * <p>object To Map String</p>
	 * 
	 * @param o
	 * @param isIgnoreBlankOrNull:忽略null及空字符串 转换成map
	 * @return
	 * @author 谢志平 2016-2-1 下午5:27:37
	 */
	public static Map<String, String> objectToMapString(final Object o, boolean isIgnoreBlankOrNull) {
		try {
			Map<String, String> map = new HashMap<String, String>();
			BeanInfo beanInfo = Introspector.getBeanInfo(o.getClass());
			for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
				String name = descriptor.getName();
				if (classStr.equals(name))
					continue;
				Object obj = descriptor.getReadMethod().invoke(o);
				if (isIgnoreBlankOrNull
						&& (obj == null || (obj instanceof String && StringUtils.isBlank((String) obj)))) {

				} else {
					map.put(name, (String) obj);
				}
			}
			return map;
		} catch (IntrospectionException e) {
			logger.error(convertToMapError, e);
		} catch (IllegalArgumentException e) {
			logger.error(convertToMapError, e);
		} catch (IllegalAccessException e) {
			logger.error(convertToMapError, e);
		} catch (InvocationTargetException e) {
			logger.error(convertToMapError, e);
		}
		return null;
	}

	/**
	 * 
	 * <p>Java object to Map<String,String> 转换</p>
	 * 
	 * @param o
	 * @return
	 * @author 房爱文（13590442273） 2014-12-17 下午6:49:52
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> objectToMapString(final Object o) {
		try {
			final Map<String, String> beanMap = BeanUtils.describe(o);
			beanMap.remove(classStr);
			return beanMap;
		} catch (final IllegalAccessException e) {
			logger.error(convertToMapError, e);
		} catch (final InvocationTargetException e) {
			logger.error(convertToMapError, e);
		} catch (final NoSuchMethodException e) {
			logger.error(convertToMapError, e);
		}
		return null;
	}

	/**
	 * 对请求参数排序，并按照接口规范中所述"参数名=参数值"的模式用"&"字符拼接成字符串
	 * 
	 * @param params 需要排序并参与字符拼接的参数
	 * @return 拼接后字符串
	 */
	public static String mapStringKeySortToLinkString(Map<String, String> params) {
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		StringBuilder sb = new StringBuilder();
		int size = keys.size();
		for (int i = 0; i < size; i++) {
			String key = keys.get(i);
			Object obj = params.get(key);
			String value = String.valueOf(obj == null ? "" : obj);
			sb.append(key).append("=").append(value);
			// 最后一组参数,结尾不包括'&'
			if (i < size - 1) {
				sb.append("&");
			}
		}
		return sb.toString();
	}

	/**
	 * 对请求参数排序，并按照接口规范中所述"参数名=参数值"的模式用"&"字符拼接成字符串
	 * 
	 * @param params 需要排序并参与字符拼接的参数
	 * @param isIgnoreBlankOrNull:忽略null及空字符串 转换成map
	 * @return 拼接后字符串
	 */
	public static String mapStringKeySortToLinkString(Map<String, String> params, boolean isIgnoreBlankOrNull) {
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		StringBuilder sb = new StringBuilder();
		int size = keys.size();
		for (int i = 0; i < size; i++) {
			String key = keys.get(i);
			String obj = params.get(key);
			if (!isIgnoreBlankOrNull || StringUtils.isNotBlank(obj)) {
				sb.append(key).append("=").append(obj == null ? "" : obj);
				// 最后一组参数,结尾不包括'&'
				if (i < size - 1) {
					sb.append("&");
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 对请求参数排序，并按照接口规范中所述"参数名=参数值"的模式用"&"字符拼接成字符串
	 * 
	 * @param params 需要排序并参与字符拼接的参数
	 * @return 拼接后字符串
	 */
	public static String mapObjectKeySortToLinkString(Map<String, Object> params, boolean isIgnoreBlankOrNull) {
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		StringBuilder sb = new StringBuilder();
		int size = keys.size();
		for (int i = 0; i < size; i++) {
			String key = keys.get(i);
			Object obj = params.get(key);
			if (!isIgnoreBlankOrNull || obj == null) {
				String value = String.valueOf(obj == null ? "" : obj);
				sb.append(key).append("=").append(value);
				// 最后一组参数,结尾不包括'&'
				if (i < size - 1) {
					sb.append("&");
				}
			}
		}
		return sb.toString();
	}

	public static String mapObjectKeySortToLinkString(Object object, boolean isIgnoreBlankOrNull, String noSortParam) {
		Map<String, Object> params = objectToMap(object, isIgnoreBlankOrNull);
		params.remove(noSortParam);
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		StringBuilder sb = new StringBuilder();
		int size = keys.size();
		for (int i = 0; i < size; i++) {
			String key = keys.get(i);
			Object obj = params.get(key);
			if (!isIgnoreBlankOrNull || obj != null) {
				String value = String.valueOf(obj == null ? "" : obj);
				sb.append(key).append("=").append(value);
				// 最后一组参数,结尾不包括'&'
				if (i < size - 1) {
					sb.append("&");
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 * <p>对Map的空值转成空字符串</p>
	 * 
	 * @param params
	 * @return
	 * @author 房爱文（13590442273） 2015-4-3 上午9:20:13
	 */
	public static Map<String, Object> mapObjectValueNullToEmptyStr(Map<String, Object> params) {
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			String mapKey = entry.getKey();
			String mapValue = entry.getValue() == null ? "" : String.valueOf(entry.getValue());
			params.put(mapKey, mapValue);
		}

		return params;
	}

	/**
	 * 
	 * <p>对Map的值转成字符串类型</p>
	 * 
	 */
	public static Map<String, String> mapObjectValueToStr(Map<String, Object> params) {
		Map<String, String> strMap = new HashMap<String, String>();
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			String mapKey = entry.getKey();
			String mapValue = entry.getValue() == null ? "" : String.valueOf(entry.getValue());
			strMap.put(mapKey, mapValue);
		}

		return strMap;
	}

	/**
	 * 
	 * <p>对Map的空值转成空字符串</p>
	 * 
	 * @param params
	 * @return
	 * @author 房爱文（13590442273） 2015-4-3 上午9:19:34
	 */
	public static Map<String, String> mapStringValueNullToEmptyStr(Map<String, String> params) {
		for (Map.Entry<String, String> entry : params.entrySet()) {
			String mapKey = entry.getKey();
			String mapValue = entry.getValue() == null ? "" : entry.getValue();
			params.put(mapKey, mapValue);
		}

		return params;
	}

}
