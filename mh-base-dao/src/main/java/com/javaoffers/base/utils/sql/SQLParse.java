package com.javaoffers.base.utils.sql;

import com.javaoffers.base.dao.SQL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: 解析sql
 *         String sql = "select 1 from dual where id = #{id}";
 *         String idSQL = SQLParse.startParse(sql, "id", 10 + "").endParse();
 *         最终结果为: select 1 from dual where id = 10
 * @Auther: create by cmj on 2020/11/29 19:08
 */
public class SQLParse {
    private String sql;
    private String param;
    final static  String p = "(\\#\\{[0-9a-zA-Z-]+\\})";
    final static  Pattern compile = Pattern.compile(p);
    private  String getParse(){
        return p;
    }

    private static String parseSql(String sql){
        String s = sql.replaceAll(p, "?");
        return s;
    }

    /**
     * 结束解析
     * @return 返回解析后的url
     */
    public String endParse(){
        return this.sql;
    }


    public static SQL parseSqlParams(String sql, List<Map<String,Object>> paramMap){

        ArrayList<Object[]> objects = new ArrayList<>();
        for(int i=0; paramMap!=null&&i<paramMap.size();i++){
            Map<String, Object> pm = paramMap.get(i);
            ArrayList<Object> params = new ArrayList<>();
            Matcher matcher = compile.matcher(sql);
            while(matcher.find()){
                String result = matcher.group(1);
                String paramKey = result.substring(2, result.length() - 1);
                params.add(pm.get(paramKey));
            }
            objects.add(params.toArray());
        }
        sql = parseSql(sql);
        SQL SQL = new SQL(sql, objects);
        return SQL;

    }

    public static SQL getSQL(String sql, Map<String, Object> map) {
        ArrayList<Map<String, Object>> maps = new ArrayList<>();
        maps.add(map);
        return SQLParse.parseSqlParams(sql, maps);
    }

    

}
