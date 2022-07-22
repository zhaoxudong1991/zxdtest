package com.seeyon.apps.ncclistencetwo.utils;

import org.songjian.utils.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import com.seeyon.ctp.common.AppContext;
import java.io.IOException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import nccloud.open.api.utils.APIUtils;

public class SendVoucher {
	private static String ip = AppContext.getSystemProperty("Information.ncctwo.ip");
	private static final Log log = LogFactory.getLog(SendVoucher.class);

	public static void main(String[] args) throws Exception {
		// System.out.println(post(GetVoucherXml.financeInterface()));
		String getassbalance = getassbalance("2021", "11", "22020103", "0004", "00381316", "0010", "Z2060059.W01",
				"0001", "600");
		System.out.println(getassbalance);
	}

	public SendVoucher() {
	}

	/**
	 * 推送凭证
	 * 
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public static String post(String content) throws Exception {
		String result = "false";
		// HttpPost httpPost = new
		// HttpPost("http://10.1.21.21/service/XChangeServlet?account=01&groupcode=GJJT");
		// http://10.1.1.69:8069/service/XChangeServlet?account=001&groupcode=GJJT
		// http://10.1.1.69:8069/service/XChangeServlet?account=ncc0430&groupcode=GJJT
		HttpPost httpPost = new HttpPost(ip + "/service/XChangeServlet?account=001&groupcode=GJJT");

		StringEntity entity = new StringEntity(content, "UTF-8");
		httpPost.setEntity(entity);

		HttpResponse httpResponse;
		httpResponse = new DefaultHttpClient().execute(httpPost);
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			result = EntityUtils.toString(httpResponse.getEntity(), "GBK");
		} else {
			result = "失败";
		}

		return result;
	}

	/**
	 * 调用查询辅助余额表接口查询余额：endlocamount
	 * 
	 * @return
	 */
	public static String getassbalance(String year, String yue, String kemu, String type1, String typevale1,
			String type2, String typevale2, String type3, String typevale3) {
		String ip = "10.1.1.69";
		String port = "8069";
		String biz_center = "001";
		String client_id = "zggj";
		String client_secret = "0cc5d7a5a63646b9b958";
		String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCLTDPdkFqXjILsRCkgKbW3Gd2sXhIHfI4hjy0D"
				+ "nVVgRfOxnshlyi6hQuvJmGBWF71PwSh113uhOC71UzZgQX1p32g2LsfoYbZwD9Ci5ZuQSlx4S/o+"
				+ "JSTzCp4ZHW+OQzoVmapRmSNHXyGMkduWU3ObF6Ss0CIGikN9aZ1MBXQcuwIDAQAB";
		String url = "nccloud/api/gl/accountrep/assbalance";
		String json = "{" + "\"accbookCode\":[\"101005-0001\"]," + "\"year\":\"" + year + "\"," + "\"endYear\":\""
				+ year + "\"," + "\"period\":\"" + yue + "\"," + "\"endPeriod\":\"" + yue + "\","
				+ "\"inclUntallyed\":true," + "\"currtypeCode\":\"本币\"," + "\"queryObj\":["
				+ "{\"checktype\":{\"checktypecode\":\"会计科目\",\"checktypename\":\"会计科目\"},\"checkvalue\":[{\"checkvaluecode\":\""
				+ kemu + "\"}]}," + "{\"checktype\":{\"checktypecode\":\"" + type1
				+ "\"},\"checkvalue\":[{\"checkvaluecode\":\"" + typevale1 + "\"}]},"
				+ "{\"checktype\":{\"checktypecode\":\"" + type2 + "\"},\"checkvalue\":[{\"checkvaluecode\":\""
				+ typevale2 + "\"}]}," + "{\"checktype\":{\"checktypecode\":\"" + type3
				+ "\"},\"checkvalue\":[{\"checkvaluecode\":\"" + typevale3 + "\"}]}" + "]" + "}";
		APIUtils util = new APIUtils();
		util.init(ip, port, biz_center, client_id, client_secret, pubKey, "sheny", "123qwe");
		String token;
		String result = "";
		try {
			util.setApiUrl(url);
			token = util.getToken();
			result = util.getAPIRetrun(token, json);
			SAVELOG("返回值：" + result);
		} catch (Exception e) {
			e.printStackTrace();
			SAVELOG("查询辅助余额表接口返回值：" + result);
		}

		return result;

	}

	/**
	 * 调用查询辅助余额表接口查询余额：endlocamount
	 * 
	 * @return
	 */
	public static String getassbalance(String year, String yue, String kemu, String type1, String typevale1,
			String type2, String typevale2, String type3, String typevale3, String type4, String typevale4) {
		String ip = "10.1.1.69";
		String port = "8069";
		String biz_center = "001";
		String client_id = "zggj";
		String client_secret = "0cc5d7a5a63646b9b958";
		String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCLTDPdkFqXjILsRCkgKbW3Gd2sXhIHfI4hjy0D"
				+ "nVVgRfOxnshlyi6hQuvJmGBWF71PwSh113uhOC71UzZgQX1p32g2LsfoYbZwD9Ci5ZuQSlx4S/o+"
				+ "JSTzCp4ZHW+OQzoVmapRmSNHXyGMkduWU3ObF6Ss0CIGikN9aZ1MBXQcuwIDAQAB";
		String url = "nccloud/api/gl/accountrep/assbalance";
		String json = "{" + "\"accbookCode\":[\"101005-0001\"]," + "\"year\":\"" + year + "\"," + "\"endYear\":\""
				+ year + "\"," + "\"period\":\"" + yue + "\"," + "\"endPeriod\":\"" + yue + "\","
				+ "\"inclUntallyed\":true," + "\"currtypeCode\":\"本币\"," + "\"queryObj\":["
				+ "{\"checktype\":{\"checktypecode\":\"会计科目\",\"checktypename\":\"会计科目\"},\"checkvalue\":[{\"checkvaluecode\":\""
				+ kemu + "\"}]}," + "{\"checktype\":{\"checktypecode\":\"" + type1
				+ "\"},\"checkvalue\":[{\"checkvaluecode\":\"" + typevale1 + "\"}]},"
				+ "{\"checktype\":{\"checktypecode\":\"" + type2 + "\"},\"checkvalue\":[{\"checkvaluecode\":\""
				+ typevale2 + "\"}]}," + "{\"checktype\":{\"checktypecode\":\"" + type3
				+ "\"},\"checkvalue\":[{\"checkvaluecode\":\"" + typevale3 + "\"}]}" + "]" + "}";
		// 100033-0001
		APIUtils util = new APIUtils();
		util.init(ip, port, biz_center, client_id, client_secret, pubKey, "sheny", "123qwe");
		String token;
		String result = "";
		try {
			util.setApiUrl(url);
			token = util.getToken();
			result = util.getAPIRetrun(token, json);
			SAVELOG("返回值：" + result);

		} catch (Exception e) {
			e.printStackTrace();
			SAVELOG("查询辅助余额表接口返回值：" + result);
		}

		return result;

	}

	/**
	 * 调用查询科目余额表接口查询余额：endlocamount
	 * 
	 * @return
	 */
	public static String getaccountbalance(String year, String yue, String kemu) {
		String ip = "10.1.1.69";
		String port = "8069";
		String biz_center = "001";
		String client_id = "zggj";
		String client_secret = "0cc5d7a5a63646b9b958";
		String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCLTDPdkFqXjILsRCkgKbW3Gd2sXhIHfI4hjy0D"
				+ "nVVgRfOxnshlyi6hQuvJmGBWF71PwSh113uhOC71UzZgQX1p32g2LsfoYbZwD9Ci5ZuQSlx4S/o+"
				+ "JSTzCp4ZHW+OQzoVmapRmSNHXyGMkduWU3ObF6Ss0CIGikN9aZ1MBXQcuwIDAQAB";
		String url ="nccloud/api/gl/accountrep/accountbalance";
		
		String json="{"+"\"period\":\""+yue+"\","
				+ "\"startSubjLevel\":\"1\","
				+ "\"inclUntallyed\":\"true\","
				+ "\"endPeriod\":\""+yue+"\","
				+ "\"year\":\""+yue+"\","
				+ "\"currtypeCode\":\"本币\","
				+ "\"endSubjLevel\":\"1\","
				+ "\"accountCode\":\""+kemu+"\","
				+ "\"accbookCode\":[\"101005-0001\"],"
				+ "\"endYear\":\""+yue+"\""
				+ "}";
		APIUtils util = new APIUtils();
		util.init(ip, port, biz_center, client_id, client_secret, pubKey, "sheny", "123qwe");
		String token;
		String result = "";
		try {
			util.setApiUrl(url);
			token = util.getToken();
			result = util.getAPIRetrun(token, json);
			SAVELOG("返回值：" + result);

		} catch (Exception e) {
			e.printStackTrace();
			SAVELOG("查询科目余额表接口返回值：" + result);
		}
		return result;

	}
	/**
	 * 调用查询辅助明细接口查询余额：endlocamount
	 * 
	 * @return
	 */
	public static String getassdetail(String year, String yue, String kemu, String type1, String typevale1,
			String type2, String typevale2, String type3, String typevale3) {
		String ip = "10.1.1.69";
		String port = "8069";
		String biz_center = "001";
		String client_id = "zggj";
		String client_secret = "0cc5d7a5a63646b9b958";
		String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCLTDPdkFqXjILsRCkgKbW3Gd2sXhIHfI4hjy0D"
				+ "nVVgRfOxnshlyi6hQuvJmGBWF71PwSh113uhOC71UzZgQX1p32g2LsfoYbZwD9Ci5ZuQSlx4S/o+"
				+ "JSTzCp4ZHW+OQzoVmapRmSNHXyGMkduWU3ObF6Ss0CIGikN9aZ1MBXQcuwIDAQAB";
		String url ="nccloud/api/gl/accountrep/assdetail";
		
		String json = "{" + "\"accbookCode\":[\"101005-0001\"]," + "\"year\":\"" + year + "\"," + "\"endYear\":\""
				+ year + "\"," + "\"period\":\"" + yue + "\"," + "\"endPeriod\":\"" + yue + "\","
				+ "\"inclUntallyed\":true," + "\"currtypeCode\":\"本币\"," + "\"queryObj\":["
				+ "{\"checktype\":{\"checktypecode\":\"会计科目\",\"checktypename\":\"会计科目\"},\"checkvalue\":[{\"checkvaluecode\":\""
				+ kemu + "\"}]}," + "{\"checktype\":{\"checktypecode\":\"" + type1
				+ "\"},\"checkvalue\":[{\"checkvaluecode\":\"" + typevale1 + "\"}]},"
				+ "{\"checktype\":{\"checktypecode\":\"" + type2 + "\"},\"checkvalue\":[{\"checkvaluecode\":\""
				+ typevale2 + "\"}]}," + "{\"checktype\":{\"checktypecode\":\"" + type3
				+ "\"},\"checkvalue\":[{\"checkvaluecode\":\"" + typevale3 + "\"}]}" + "]" + "}";
		APIUtils util = new APIUtils();
		util.init(ip, port, biz_center, client_id, client_secret, pubKey, "sheny", "123qwe");
		String token;
		String result = "";
		try {
			util.setApiUrl(url);
			token = util.getToken();
			result = util.getAPIRetrun(token, json);
			SAVELOG("返回值：" + result);

		} catch (Exception e) {
			e.printStackTrace();
			SAVELOG("查询科目余额表接口返回值：" + result);
		}
		return result;

	}
	
	
	// 输出日志
	public static void SAVELOG(String content) {
		try {
			LogInfo.testMemberFile("SendVoucher:" + content + ";\r\n");
		} catch (IOException e) {
			e.printStackTrace();
			log.error("SendVoucher添加日志失败：" + e.getMessage());
		}
	}

}
