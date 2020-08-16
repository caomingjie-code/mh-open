package com.mh.base.utils.model;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import java.util.Map.Entry;

import com.mh.base.exception.BaseException;
import com.mh.base.annotation.model.BaseCancel;
import com.mh.base.annotation.model.BaseCancelSuper;
import com.mh.base.annotation.model.BaseModel;
import com.mh.base.annotation.model.BaseUnique;
import com.mh.base.common.converter.TypeCastHelper;
import com.mh.base.utils.pojo.PojoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于解析model
 * @author cmj
 *
 */
public class ModelUtils {

	private static final ThreadLocal<Boolean> cancelSuper = new ThreadLocal<Boolean>(); //取消当前Model的父类属性
	private static Logger logger = LoggerFactory.getLogger(ModelUtils.class);// log4j记录日志

	/**
	 * 解析model
	 * @param clazz
	 * @param list_map
	 * @return
	 */
	public static <E> ArrayList<E> analysisModel(Class<E> clazz, List<Map<String, Object>> list_map){
		ArrayList<E> arrayList = new ArrayList<E>();
		if (list_map == null || list_map.size() == 0||!isBaseModel(clazz)) {
			return arrayList;
		}
		try {
			arrayList.addAll(buildModel(clazz, initData(list_map)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arrayList;
	} 
	
	/**
	 *@Autour  cmj  on 2019/8/14 15:06
	 *@Description :
	 */
	public static <E> E converterMap2Model(Class<E> clazz, Map<String, Object> oneMap) {
		E e = null;
		try {
			e = clazz.newInstance();
		}catch (Exception e2){
			e2.printStackTrace();
		}
		ArrayList<Map<String, Object>> maps = new ArrayList<>();
		maps.add(oneMap);
		List<E> es = analysisModel(clazz, maps);
		if(es!=null&&es.size()==1){
			return es.get(0);
		}
		return e;
	}
	
	/**
	 * 解析Model 和 Pojo
	 * @param clazz
	 * @param list_map
	 * @return
	 */
	public static <E> ArrayList<E> converterMap2T(Class<E> clazz, List<Map<String, Object>> list_map) {
		if (list_map == null || list_map.size() == 0) {
			return new ArrayList<E>();
		}
		ArrayList<E> list = new ArrayList<E>();
		if (clazz.getDeclaredAnnotation(BaseModel.class) != null) {// model
            try {
				list.addAll(buildModel(clazz,initData(list_map)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {// pojo
			PojoUtils.buildPojo(clazz, list_map, list);
		}
		return list;
	}
	
	/**
	 * 下划线转换为驼峰
	 * @param list_map
	 * @return
	 */
	public static  List<Map<String, Object>> initData(List<Map<String, Object>> list_map){
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if(list_map!=null&&list_map.size()>0){
			for(Map<String, Object> data : list_map){
				Map<String, Object> newData = new HashMap<String, Object>();

				final Set<Entry<String, Object>> entries = data.entrySet();
				for(Entry<String, Object> entry : entries){
					 String key = entry.getKey();
					 Object value = entry.getValue();
					if(StringUtils.isNotBlank(key)){
						 String[] split = key.split("_");
						 final StringBuilder sb = new StringBuilder("");
						 for(int i=0; split!=null&&i<split.length;i++){
						 	if(i==0){
								 String firstChar = split[i];
								 sb.append(firstChar);
							}else{
						 		String nextChar = split[i];
								final String upperCase = nextChar.substring(0, 1).toUpperCase();
								sb.append(upperCase);
								if(nextChar.length()>1){
									final String substring = nextChar.substring(1);
									sb.append(substring);
								}
							}
						 }
						 final String newKey = sb.toString();//驼峰Key
						 newData.put(newKey,value);
					 }
				}
				if(newData!=null&&!newData.isEmpty()){
					list.add(newData);
				}
			}
		}
		return list;
	}
	
	/**用于原始数据集转换Model数据集
	 * @param clazz  Model的Class对象
	 * @param list      封装后的Model数据集
	 * @throws Exception 
	 * @throws SecurityException 
	 * 2020-01-08 修改为按照数据库元数据 排序
	 */
	private static <E> List<E> buildModel(Class<E> clazz, List<Map<String, Object>> list_map) throws Exception {
		LinkedList<E> linkedList = new LinkedList<E>();
		if(clazz==null||list_map==null||list_map.size()==0) {
			return linkedList;
		}
		//HashMap 改为 LinkedHashMap，解决排序问题
		Map<String, List<Map<String, Object>>> map_ = new LinkedHashMap<String, List<Map<String, Object>>>();//存放切分后的数据（根据唯一字段的值分组）
		Set<Field> fields = getFields(clazz);
		checkIsExistsSameSuperModel(clazz);
		ArrayList<Field> ones = new ArrayList<Field>();//非数组或集合model,一对一
		ArrayList<Field> arrays = new ArrayList<Field>();//存放数组model
		ArrayList<Field> list_ = new ArrayList<Field>();//存放list集合model
		ArrayList<Field> set_ = new ArrayList<Field>();//存放set集合model
		for(Field fd : fields) {
			fd.setAccessible(true);
			if(fd.getType().isArray()) { 
				arrays.add(fd);
			}else if(List.class.isAssignableFrom(fd.getType())) {
				list_.add(fd);
			}else if(Set.class.isAssignableFrom(fd.getType())) {
				set_.add(fd);
			}else {
				ones.add(fd);
			}
		}
		ArrayList<String> uniqueFieldNameList = new ArrayList<String>();//存放能确定一条主表数据（主model）的字段
		getUniqueFieldNames(ones, uniqueFieldNameList,list_map.get(0));//获取能确定一条主表数据（主model）的字段
		inciseData(list_map, map_, uniqueFieldNameList);//切割数据
		List<E> list = buildData(clazz,map_,ones,arrays,list_,set_);
		linkedList.addAll(list);
		return linkedList;
	}

	/**
	 * 判断当前fd是否存在set方法（public），
	 * @param fd
	 * @return
	 */
	private static boolean isHasSetMethod(Field fd){
		Class<?> type = fd.getType();
		String name = fd.getName();
		try {
			String setMethodName = "set"+name.substring(0, 1).toUpperCase()+name.substring(1, name.length());
			Method method = fd.getDeclaringClass().getMethod(setMethodName, type);
			if(method!=null){
			return  true;
			}
		}catch (Exception e ){
			return false;
		}
		return false;
	}

	private static <E> Set<Field> getFields(Class<E> clazz) {
		
		Set<Field> list = new HashSet<Field>();
		if(clazz.getDeclaredAnnotation(BaseCancel.class)!=null) {return list;}//过滤类型加上BaseCancel的model 类
		if(!clazz.getName().equals("java.lang.Object")) {
		   Field[] fields = clazz.getDeclaredFields();
		   for(Field f : fields) {
			   if(!isHasSetMethod(f)) continue; //如果当前字段没有setter方法，默认失效。
			   if(f.getDeclaredAnnotation(BaseCancel.class) != null) continue;//如果该属性上有BaseCancel注解则忽略该属性
			   list.add(f);
		   }
		   if(!getCancelSuperFieldsStatus()) {  //是否过滤当前类的父类
			   list.addAll(getFields(clazz.getSuperclass()));	
		   }
		}
		return list;
	}
	//获取是否取消父类,true 为过滤，false 为不过滤
	private static boolean getCancelSuperFieldsStatus() {
		Boolean status = cancelSuper.get()==null?false:cancelSuper.get();
		if(status) {
			cancelSuper.set(false);
			return true;
		}
		return false;
	}
	
	/**
	 *  string： mianModel 类名
	 *  String ： 保存当前Model的父类名称，如果存在相同则提示Model存在相同父类。
	 */
	public final static ThreadLocal<Map<String,String>> tl = new ThreadLocal<Map<String,String>>();
	private static <E> void checkIsExistsSameSuperModel(Class<E> clazz) {
		 Map<String, String> map = tl.get();
		final String name = clazz.getSuperclass().getName();
		//如果父类上面存在注解 BaseCancelSuper 则不需要校验父类，因为会直接过滤掉父类的所有属性
		BaseCancel baseCancelSuper = clazz.getSuperclass().getDeclaredAnnotation(BaseCancel.class);
		if(baseCancelSuper!=null ) { return ;}
		
		
		if(map == null){
			map = new HashMap<>();
			tl.set(map);
		}
		if("java.lang.Object".equals(name)){
			return ;
		}else{
			final String superName = map.get(name);
			if(StringUtils.isNotBlank(superName)){
				try {
					throw new BaseException("Model 模型不能存在相同的父类，防止字段重复！！"+clazz.getName());
				} catch (BaseException e) {
					e.printStackTrace();
				}
			}else{
				map.put(name,name);
			}
		}

	}
	
	private static class Model<E>{
		private E model;
		private boolean status = false;//
		public E getModel() {
			return model;
		}
		public E getModelAndSetStatusIsTrue() {
			this.status = true;
			return model;
		}
		public void setModel(E model) {
			this.model = model;
		}
		public boolean isStatus() {
			return status;
		}
		public void setStatus(boolean status) {
			this.status = status;
		}
		public Model(E model) {
			super();
			this.model = model;
		}
		static <E> Model getModel(E model) {
			return new Model<E>(model);
		}
	}
	@SuppressWarnings("unchecked")
	private static <E> List<E> buildData(
			Class<E> clazz,Map<String, 
			List<Map<String, Object>>> map_, 
			ArrayList<Field> ones, 
			ArrayList<Field> arrays,
			ArrayList<Field> list_, 
			ArrayList<Field> set_) throws Exception {
		LinkedList<E> linkedList = new LinkedList<E>();
		
		try {
			Set<Entry<String,List<Map<String,Object>>>> entrySet = map_.entrySet();
	        for(Entry<String,List<Map<String,Object>>> entry : entrySet) {
	        	
	        	List<Map<String,Object>> list = entry.getValue();//获取一切割数据
	        	if(list==null||list.size()==0) {
	        		continue;
	        	}
	        	Map<String, Object> mp = list.get(0);
	        	Model<E> model = Model.getModel(clazz.newInstance());
	        	
        		for(Field fd: ones) {
        			cancelSuperModelFields(fd);
        			if(isBaseModel(fd)) {
        				List ls = buildModel(getModelClass(fd), list);
        				if(ls!=null&&ls.size()==1) {
        					Object object = ls.get(0);
        					fd.set(model.getModelAndSetStatusIsTrue(), object);
        				}
        				continue;
        			}
    				String name = fd.getName();
    				if(mp.get(name)!=null) {
    					Class<?> type = fd.getType();
    					Object object2 = TypeCastHelper.ConverterObject(type, mp.get(name));
    					fd.set(model.getModelAndSetStatusIsTrue(), object2);
    				}
    				
    				continue;
        		}
				for(Field fd: arrays) {
					cancelSuperModelFields(fd);
					if(isBaseModel(fd)) {
						List<E> ls = buildModel(getModelClass(fd), list);
						 if(ls!=null&&!ls.isEmpty()&&ls.size()>0) {
							 Class<? extends Object> class1 = ls.get(0).getClass();
							 Object object = Array.newInstance(class1, ls.size());
							 for(int i=0;i<ls.size();i++) {
								 Array.set(object, i, ls.get(i)); 
							 }
							 if(object!=null) {
								 fd.set(model.getModelAndSetStatusIsTrue(),object );
							 }
						 }
					}else {
						String name = fd.getName();
						LinkedList<Object> arrayData = new LinkedList<Object>();
						for(Map<String, Object> map : list) {
							Object object = map.get(name);
							if(object!=null) {
								arrayData.add(object);
							}
						}
						String typeName = fd.getGenericType().getTypeName();
						Class<?> class1 = Class.forName(typeName.substring(0, typeName.length()-2));
						Object object = Array.newInstance(class1, arrayData.size());
						if(arrayData!=null&&arrayData.size()>0) {
							for(int i=0;i<arrayData.size();i++) {
								Object object2 = TypeCastHelper.ConverterObject(object.getClass(), arrayData.get(i));
								Array.set(object, i, object2); 
							 }
							fd.set(model.getModelAndSetStatusIsTrue(),object ); 
						}
					}
				}
				for(Field fd: list_) {
					cancelSuperModelFields(fd);
					Class<?> type = fd.getType();
					if(type.isInterface()) {
						List ls = buildModel(getModelClass(fd), list);
						if(ls!=null&&ls.size()>0) {
							fd.set(model.getModelAndSetStatusIsTrue(), ls);
						}
					}else {
						List newInstance = (List)type.newInstance();
						List ls = buildModel(getModelClass(fd), list);
						newInstance.addAll(ls);
						if(newInstance!=null&&newInstance.size()>0) {
							fd.set(model.getModelAndSetStatusIsTrue(), newInstance);
						}
					}
					
				}
				for(Field fd: set_) {
					cancelSuperModelFields(fd);
					Class<?> type = fd.getType();
					if(type.isInterface()) {
						HashSet<E> hashSet = new HashSet<E>();
						List ls = buildModel(getModelClass(fd), list);
						hashSet.addAll(ls);
						if(hashSet!=null&&hashSet.size()>0) {
							fd.set(model.getModelAndSetStatusIsTrue(), hashSet);
						}
					}else {
						Set newInstance = (Set)type.newInstance();
						List ls = buildModel(getModelClass(fd), list);
						newInstance.addAll(ls);
						if(newInstance!=null&&newInstance.size()>0) {
							fd.set(model.getModelAndSetStatusIsTrue(), newInstance);
						}
					}
				}
				if(model.isStatus()) {
					linkedList.add(model.getModel());
				}
				
        	}
	        
	        }catch(Exception ex) {
	        	throw ex;
	        } 
	        return linkedList;
		}



	private static void cancelSuperModelFields(Field fd) {
		BaseCancelSuper bcs = fd.getDeclaredAnnotation(BaseCancelSuper.class);
		if(bcs!=null) {
			cancelSuper.set(true);
		}
	}
	private static  Class getModelClass(Field fd) throws Exception {
		Class<?> type2 = fd.getType();
		if(type2.isArray()) {
			String typeName = fd.getGenericType().getTypeName();
			return  Class.forName(typeName.substring(0, typeName.length()-2));
			
		}else if(List.class.isAssignableFrom(type2)) {
			return getGenericityClassOfCollect(fd);
		}else if(Set.class.isAssignableFrom(type2)) {
			
			return getGenericityClassOfCollect(fd);
			
		}
		return type2;
	}
	/**获取集合的泛型类
	 * @param fd
	 * @return
	 * @throws ClassNotFoundException
	 */
	private static Class getGenericityClassOfCollect(Field fd) throws ClassNotFoundException {
		try {
			ParameterizedType listGenericType = (ParameterizedType) fd.getGenericType();
			Type listActualTypeArguments = listGenericType.getActualTypeArguments()[0];
			return Class.forName(listActualTypeArguments.getTypeName());
		} catch (Exception e) {
			new BaseException(e.getMessage()+"\n一对多关系注意引用集合类一定要加上泛型类例如 ：List<Model>,Model不能省去").printStackTrace();

		}
		
		return null;
	}
	/**
	 * 切割数据
	 * @param list_map
	 * @param map_
	 * @param uniqueFieldNameList
	 */
	private static void inciseData(List<Map<String, Object>> list_map, Map<String, List<Map<String, Object>>> map_,
			ArrayList<String> uniqueFieldNameList) {
		for(Map<String, Object> m : list_map) {
			StringBuilder sb = new StringBuilder("_");
			for(int i=0;uniqueFieldNameList!=null&&i<uniqueFieldNameList.size();i++) {
				String uniqueKey = uniqueFieldNameList.get(i);
				String  uniqueValue = m.get(uniqueKey)==null?"":m.get(uniqueKey).toString();
				sb.append(uniqueValue);
			}
			List<Map<String, Object>> dataList = map_.get(sb.toString());
			if(dataList==null) {
				dataList = new LinkedList<Map<String, Object>>();
				map_.put(sb.toString(), dataList);
			}
			dataList.add(m);
		}
	}
	/**获取能确定最外层Model的唯一字段名称
	 * @param ones
	 * @throws Exception 
	 * @throws SecurityException 
	 */
	private static void getUniqueFieldNames(ArrayList<Field> ones, ArrayList<String> uniqueFieldNameList_,Map<String, Object> standardClumMap) throws SecurityException, Exception {
		HashSet<String> hashSet = new HashSet<String>();
		for(Field fd : ones) {//一对一
			if(isBaseModel(fd)) {//
				Field[] bmFs = fd.getType().getDeclaredFields();//model中field
				for(int i=0;bmFs!=null&&i<bmFs.length;i++) {
					Field bmf = bmFs[i];
					BaseUnique[] uniques = bmf.getAnnotationsByType(BaseUnique.class);
					if(uniques!=null&&uniques.length>0) {
						String uniqueFieldName = bmf.getName();
						if(standardClumMap.containsKey(uniqueFieldName)) {
							hashSet.add(uniqueFieldName);
						}
					}
				}
				
			}
			if(isBaseUnique(fd)) {
				String uniqueFieldName = fd.getName();
				//if(standardClumMap.containsKey(uniqueFieldName)) {
					hashSet.add(uniqueFieldName);
				//}
			}
		}
		uniqueFieldNameList_.addAll(hashSet);
	}
	private static boolean isBaseModel(Field fd) throws Exception {
		
		Class<?> type = fd.getType();
		if(type.isArray()) {
			String typeName = fd.getGenericType().getTypeName();
			Class<?> class1 = Class.forName(typeName.substring(0, typeName.length()-2));
			if(isBaseModel(class1)) {
				return true;
			}
		}
		if(List.class.isAssignableFrom(type)) {
			return isModelForListAndSet(fd);
		}
		if(Set.class.isAssignableFrom(type)) {
			return isModelForListAndSet(fd);
		}
		
		BaseModel[] bm = type.getAnnotationsByType(BaseModel.class);
		if(bm!=null&&bm.length>0) {
			return true;
		}
		return false;
	}
	private static boolean isModelForListAndSet(Field fd) {
		try {
			ParameterizedType listGenericType = (ParameterizedType) fd.getGenericType();
			Type listActualTypeArguments = listGenericType.getActualTypeArguments()[0];
			Class<?> forName = Class.forName(listActualTypeArguments.getTypeName());
			if(isBaseModel(forName)) {
				return true;
			}
		} catch (Exception e2) {
			new BaseException(e2.getMessage()+"\n一对多关系注意引用集合类一定要加上泛型类例如 ：List<Model>,Model不能省去").printStackTrace();
		}
		return false;
	}
	/**
	 * 检验clazz 是否是model类型
	 * @param clazz
	 * @return
	 */
	private static boolean isBaseModel(Class clazz) {
		BaseModel[] bm = (BaseModel[]) clazz.getAnnotationsByType(BaseModel.class);
		if(bm!=null&&bm.length>0) {
			return true;
		}
		return false;
	}
	private static boolean isBaseUnique(Field fd) {
		BaseUnique[] bm = fd.getAnnotationsByType(BaseUnique.class);
		if(bm!=null&&bm.length>0) {
			return true;
		}
		return false;
	}
	
	

	/**
	 *@Autour  cmj  on 2019/7/26 17:01
	 *@Description : model 转换为map, 待开发，目前还不支持list等等集合属性
	 */
	public static Map<String,Object> model2Map(Object obj){
		HashMap<String, Object> data = new HashMap<String,Object>();
		try {
			Class<? extends Object> clazz = obj.getClass();
			Field[] fields = clazz.getDeclaredFields();
			for(Field f : fields) {
				f.setAccessible(true);
				String name = f.getName();
				Object value = f.get(obj);
				data.put(name, value);
			}
		} catch (Exception e) {
			logger.info(e.getMessage()+"+++++++++++++++++++出错了！！！");
		}
		return data;
	}
	
	/**
	 *     排序根据主Model字段
	 * @param fieldName
	 * @param modelDatas
	 * @return
	 */
	@Deprecated
	private static <E> LinkedList<E> sortByMainModelOfFieldName(String fieldName,List<E> modelDatas,Class<E> clazz){
		if(modelDatas==null||modelDatas.size()==0) {
			return null;
		}else {
			Object o = new Object();
			try {
				Field declaredField = clazz.getDeclaredField(fieldName);
				declaredField.setAccessible(true);
				if(declaredField==null) {
					throw new BaseException(" 该字段名："+fieldName+" 不存在！！！");
				}
				String value = "";
				TreeMap<Object,String> treeMap = new TreeMap<Object, String>();
				for(E model : modelDatas) {
					Object key = declaredField.get(model)==null?o:declaredField.get(model); //获取该对象的值
					treeMap.put(key, value);
				}
				Set<Entry<Object,String>> entrySet = treeMap.entrySet();
				LinkedList<E> newModels = new LinkedList<E>();
				for(Entry<Object,String> entry : entrySet) {
					Object key = entry.getKey();
					for(E model : modelDatas) {
						Object key_ = declaredField.get(model)==null?o:declaredField.get(model);
						if(key_.equals(key)||key_ == key ) {
							newModels.add(model);
						}
					}
				}
				return  newModels;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	@Deprecated
	public static <E> ArrayList<E> converterMap2TAndSort(Class<E> clazz, List<Map<String, Object>> list_map,String fieldName) {
		if (list_map == null || list_map.size() == 0) {
			return new ArrayList<E>();
		}
		ArrayList<E> list = new ArrayList<E>();
		if (clazz.getDeclaredAnnotation(BaseModel.class) != null) {// model
            try {
				list.addAll(buildModel(clazz,initData(list_map)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {// pojo
			PojoUtils.buildPojo(clazz, list_map, list);
		}
		LinkedList<E> list2 = sortByMainModelOfFieldName(fieldName, list, clazz);
		ArrayList<E> arrayList = new ArrayList<E>();
		arrayList.addAll(list2);
		return arrayList;
	}
	
	
	
}
