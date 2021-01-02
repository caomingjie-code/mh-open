package com.javaoffers.base.common.pojo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Id;
import org.apache.commons.lang3.StringUtils;

/**
 * pojo 解析工具
 * @author cmj
 *
 */
public class PojoUtils {
	public static <E> void buildPojo(Class<E> clazz, List<Map<String, Object>> list_map, ArrayList<E> list) {
		Field[] declaredFields = clazz.getDeclaredFields();
		HashMap<String, Field> ls_cn = new HashMap<String, Field>();
		ArrayList<String> ls_cnc = new ArrayList<String>();
		
		if (declaredFields != null && declaredFields.length > 0) {

			for (Field field : declaredFields) {
				String column_name = null;
				Column column = field.getDeclaredAnnotation(Column.class);
				if (column == null) {
					Id id = field.getDeclaredAnnotation(Id.class);
					if (id != null) {
						column_name = field.getName();
					}
				} else {
					column_name = column.name();
					if (StringUtils.isBlank(column_name)) {
						column_name = field.getName();
					}
				}
				if (StringUtils.isNotBlank(column_name)) {
					field.setAccessible(true);
					ls_cn.put(column_name, field);
					ls_cnc.add(column_name);
				}
			}
		}

		if (ls_cnc.size() == 0) {
			return ;
		}

		try {
			for (Map<String, Object> map : list_map) {
				Set<String> keySet = map.keySet();
				HashMap<String, Object> map_ = new HashMap<String, Object>();
				for (String key : keySet) {
					map_.put(key.replaceAll("_", "").toLowerCase(), map.get(key));
				}
				E instance = clazz.newInstance();
				for (String cn : ls_cnc) {
					Field field = ls_cn.get(cn);
					field.set(instance, map_.get(cn.replaceAll("_", "").toLowerCase()));
				}
				list.add(instance);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
