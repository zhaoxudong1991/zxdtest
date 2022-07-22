package com.seeyon.apps.ncclistencetwo.utils;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.JDBCAgent;



public class JdbcUtil {
	
	public static void main(String[] args){
//		String id="5582726046496869003";
//		JdbcUtil jdbcUtil = new JdbcUtil();
//		String bsql="select code from org_unit where id=?";
//		String jdbcAgent = jdbcUtil.getJdbcAgent(id, bsql, "code");
	}
 /**
   	 * 增删改
   	 * @param sql
   	 */
   	public static int  aud(String sql,List<String> list){
   		JDBCAgent jdbc = new JDBCAgent(true);
		int row = 0;
		try {
			row = jdbc.execute(sql.toString(),list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jdbc != null) {
				jdbc.close();
			}
		}
		return row;
   		
   	}
   
   	public static String jdbcselectone(String sql,List<String> list,String column) {
		JDBCAgent jdbc = new JDBCAgent(true);
		FlipInfo flipInfo = new FlipInfo();
		
		FlipInfo in=null;
		String jidian="";
		try {
			in= jdbc.findByPaging(sql, list, flipInfo);
			String data=in.toJSON();
			JSONObject fromObject = JSONObject.parseObject(data);
			JSONArray json = fromObject.getJSONArray("rows");
			if(json.size()>0){
				JSONObject fromObject3 = json.getJSONObject(0);
				if(!fromObject3.isEmpty()){
					jidian= fromObject3.getString(column);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jdbc != null) {
				jdbc.close();
			}
		}
		return jidian;
	}
   	
}
