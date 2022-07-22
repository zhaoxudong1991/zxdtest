package com.seeyon.apps.ncclistencetwo.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.common.AppContext;

public class LogInfo {
	private static final Log log = LogFactory.getLog(LogInfo.class);

	public static String getNowDateTime() {
		String temp_str="";
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		temp_str=sdf.format(dt);
		return temp_str;
	}
	public static String createfile(String path) {
		String result = null;
		File file = new File(path);
		// 如果文件夹不存在则创建
		if (!file.exists() && !file.isDirectory()) {
			log.info("不存在");
			file.mkdir();
			result = "文件夹已创建";
		} else {
			result = "目录存在";
		}
		return result;
	}
	
	public static void testMemberFile(String str) throws IOException {
		String filePath = AppContext.getSystemProperty("ctp.base.folder");
		 createfile(filePath+"/logs");
//		System.out.println(createfile);
		String path = new File(filePath).getCanonicalPath()+"/logs/systemnccTwoLog.log";
		File file = new File(path);
		if(!file.exists()) {
			try {
				file.createNewFile();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		try {
			FileOutputStream out = new FileOutputStream(file,true);
			PrintWriter write = new PrintWriter(out);
			write.write(getNowDateTime()+"  "+str);
			write.flush();
			write.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
