package com.seeyon.apps.ncclistencetwo.utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.songjian.utils.StringUtils;

import com.seeyon.apps.collaboration.manager.ColManager;
import com.seeyon.apps.collaboration.po.ColSummary;
import com.seeyon.apps.ncclistencetwo.flow.BaoxiaodanFormWorkflowEvent;
import com.seeyon.apps.ncclistencetwo.utils.GetPinyin;
import com.seeyon.apps.ncclistencetwo.utils.JdbcUtil;
import com.seeyon.apps.ncclistencetwo.utils.LogInfo;
import com.seeyon.apps.ncclistencetwo.utils.SendVoucher;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.workflow.event.WorkflowEventData;

import jodd.util.StringUtil;

public class BxUtils {
	private static final Log log = LogFactory.getLog(BaoxiaodanFormWorkflowEvent.class);
	private static String teshuxiangnu = AppContext.getSystemProperty("Information.ncc.teshuxiangnu");
	private static String teshuxiangnucolumn = AppContext.getSystemProperty("Information.ncc.teshuxiangnucolumn");  
	private static ColManager colManager;
	//输出日志
    public static void SAVELOG(String content) {
    	try {
			LogInfo.testMemberFile("BxdUtil:"+content+";\r\n");
		} catch (IOException e) {
			e.printStackTrace();
			log.error("添加日志失败："+e.getMessage());
		}
    }
    /***
     * 服务费调用凭证的接口
     * @param data
     */
    public static String fwfjtuseinterface(WorkflowEventData data) {
        Map<String, Object> d = data.getBusinessData();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String timeFormat = sdf.format(new Date());
        String fulltime = timeFormat.toString();
        String year = fulltime.substring(0, 4);
        String month = fulltime.substring(5, 7);

        String zhaiyao = ObjectUtils.toString(d.get("摘要"));
        if(StringUtils.isBlank(zhaiyao)){
        	SAVELOG("摘要为空:"+zhaiyao);
        }
        String money = ObjectUtils.toString(d.get("付款金额"));
        if(StringUtils.isBlank(money)){
        	SAVELOG("付款金额为空:"+money);
        	
        }
        String bxmoney = ObjectUtils.toString(d.get("小写借款金额"));
        if(StringUtils.isBlank(bxmoney)){
        	SAVELOG("小写借款金额:"+bxmoney);
        	
        }
        String shoumoneyuser =  ObjectUtils.toString(d.get("收款人账号"));
		if(StringUtils.isBlank(shoumoneyuser)){
			SAVELOG("收款人账号为空:"+shoumoneyuser);
		}
		String zengzhikemu="";
		String zengzhishui = ObjectUtils.toString(d.get("增值税"));
		if(StringUtils.isNoBlank(zengzhishui)&&!zengzhishui.equals("0")){
			zengzhikemu="2221010101";
		}
		String cjkmoney =  ObjectUtils.toString(d.get("冲借款金额"));
			if(StringUtils.isBlank(cjkmoney)){
				SAVELOG("冲借款金额:"+cjkmoney);
		}
		
    	String deptname="";
    	String bumenname = ObjectUtils.toString(d.get("部门编号"));
    	if(StringUtils.isBlank(bumenname)){
        	SAVELOG("部门编号为空:"+bumenname);
        	return "请选择部门编号";
        }else{
        	List<String> list=new ArrayList<String>();
        	list.add(bumenname);
        	deptname=JdbcUtil.jdbcselectone("select name from org_unit where code=?",list,"name");
        }
    	
		String jiefangkemu="";
		String xiangmuname = ObjectUtils.toString(d.get("项目编号"));
		if(StringUtils.isNoBlank(xiangmuname)){
			jiefangkemu = jiefangkemu(xiangmuname, "54040306", "52010306","52010306");
			if(StringUtils.isBlank(jiefangkemu)){
        		return "借方科目为空";
        	}
		}else{
			SAVELOG("项目编号:"+xiangmuname);
			if("科技工作部".equals(deptname) || "市场开发部".equals(deptname) || "用户办".equals(deptname) ){
				jiefangkemu="660124";
			}else if("资产财务部".equals(deptname) || "党委办公室、综合管理部".equals(deptname) || "党委组织部、人力资源部部".equals(deptname) 
					|| "法律审计部".equals(deptname)  || "信息中心".equals(deptname)  ){
				jiefangkemu="660224";
			}else{
				return deptname+"-该部门不能生成凭证";
			}
		}
		String username = ObjectUtils.toString(d.get("凭证生成人")) ;
		String createusercode = "";
        if(StringUtils.isBlank(username)){
        	SAVELOG("凭证生成人为空:"+username);
        	return "请选择凭证生成人";
        }	
		try {
			OrgManager orgManager = (OrgManager) AppContext.getBean("orgManager");
			V3xOrgMember orgMember = orgManager.getMemberById(Long.parseLong(username));
			username = orgMember.getName();
	        username = GetPinyin.getPingYin(username);
	        //  这个人特殊, 在NCC里重名
	        if("liujie".equals(username)){
	            username ="liujie01";
	        }
	        //发起人
	        ColSummary colSummary = colManager.getSummaryById(data.getSummaryId());
	        Long startMemberId = colSummary.getStartMemberId();
	        V3xOrgMember faqiorgMember = orgManager.getMemberById(startMemberId);
	        createusercode= faqiorgMember.getCode();
	        username="030ZXJX-OA";
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			SAVELOG("获取制单人有误:"+e1.getMessage());
		}
		StringBuffer writexmldoc = new StringBuffer();
        writexmldoc.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        //正式
        writexmldoc.append("<ufinterface account = \"ncc0430\" billtype=\"vouchergl\" businessunitcode=\"develop\" filename=\"\" groupcode=\"GJJT\" isexchange=\"Y\" orgcode=\"101005\" receiver=\"101005\" replace=\"Y\" roottag=\"\" sender=\"030ZXJX-OA\">");
        writexmldoc.append("<voucher id= \"\">");
        writexmldoc.append("<voucher_head>");
        writexmldoc.append("<pk_voucher>").append("").append("</pk_voucher>");
        writexmldoc.append("<pk_vouchertype>").append("01").append("</pk_vouchertype>");// 凭证主键, 没有就是新增, 有就是修改   可以为空
        writexmldoc.append("<year>").append(year).append("</year>");//  会计年度 如2012
        writexmldoc.append("<pk_system>").append("GL").append("</pk_system>");// 来源系统 如GL
        writexmldoc.append("<voucherkind>").append("0").append("</voucherkind>"); //<!--凭证类型值 0：正常凭证 3：数量调整凭证 不可空-->
        writexmldoc.append("<pk_accountingbook>").append("101005-0001").append("</pk_accountingbook>");//<!--核算账簿 非空 （账簿_财务核算账簿）-->
        writexmldoc.append("<discardflag>").append("N").append("</discardflag>"); // 作废标志; 可空 N
        writexmldoc.append("<period>").append(month).append("</period>");// 会计期间, 非空
        writexmldoc.append("<no>").append("</no>");//凭证号为空自动分配 非空：按凭证号处理
        writexmldoc.append("<attachment>").append("0").append("</attachment>");//附单数据 可空
        writexmldoc.append("<prepareddate>").append(fulltime).append("</prepareddate>");//制单日期, 非空
        writexmldoc.append("<pk_prepared>").append(username).append("</pk_prepared>"); // 制单人,非空
        writexmldoc.append("<pk_casher>").append("</pk_casher>"); // 出纳
        writexmldoc.append("<signflag>").append("N").append("</signflag>"); //签字标志
        writexmldoc.append("<pk_checked>").append("</pk_checked>"); // 审核人
        writexmldoc.append("<tallydate></tallydate><pk_manager></pk_manager><memo1></memo1><memo2></memo2><reserve1></reserve1><reserve2>N</reserve2><siscardflag />");

        writexmldoc.append("<pk_org>").append("101005").append("</pk_org>"); //所属组织 非空
        writexmldoc.append("<pk_org_v>").append("101005").append("</pk_org_v>"); //所属组织版本. 可空
        writexmldoc.append("<pk_group>").append("GJJT").append("</pk_group>"); //所集团  如果不输集团取当前登陆集团 例如GJJT
        writexmldoc.append("<details>");

        // 借方分录 分录1
        writexmldoc.append("<item>");
        writexmldoc.append("<detailindex>").append("1").append("</detailindex>"); // 分录号
        writexmldoc.append("<explanation>").append(zhaiyao).append("</explanation>"); //   摘要 非空
        writexmldoc.append("<verifydate>").append("").append("</verifydate>");  // 业务日期, 可空
        writexmldoc.append("<price>").append("0").append("</price>");//单价 可空
        writexmldoc.append("<excrate2>").append("").append("</excrate2>"); // 折本汇率 可空
        writexmldoc.append("<debitquantity>").append("").append("</debitquantity>"); // 借方数量 可空
        writexmldoc.append("<debitamount>").append(bxmoney).append("</debitamount>"); //原币借方金额 非空
        writexmldoc.append("<localdebitamount>").append(bxmoney).append("</localdebitamount>"); //本币借方金额 非空
        writexmldoc.append("<groupdebitamount>").append(bxmoney).append("</groupdebitamount>"); //集团本币借方金额 非空
        writexmldoc.append("<globaldebitamount>").append(bxmoney).append("</globaldebitamount>"); //全局本币借方金额 非空
        writexmldoc.append("<pk_currtype>").append("CNY").append("</pk_currtype>"); //币种  非空
        writexmldoc.append("<pk_accasoa>").append(jiefangkemu).append("</pk_accasoa>"); //科目  非空
        writexmldoc.append("<pk_unit>").append("</pk_unit>");// 所属二级核算单位
        writexmldoc.append("<pk_unit_v>").append("</pk_unit_v>");  // 所属二级核算单位 版本可空
        if(StringUtils.isNoBlank(xiangmuname)){
        	if("54040306".equals(jiefangkemu)){
        	 // 辅助核算  
            writexmldoc.append("<ass>");
            // 项目
            writexmldoc.append("<item>");
            writexmldoc.append("<pk_Checktype>").append("0010").append("</pk_Checktype>");
            writexmldoc.append("<pk_Checkvalue>").append(xiangmuname).append("</pk_Checkvalue>");
            writexmldoc.append("</item>");
                // 部门
                writexmldoc.append("<item>");
                writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
                writexmldoc.append("<pk_Checkvalue>").append(bumenname).append("</pk_Checkvalue>");
                writexmldoc.append("</item>");
                //  客商
            	writexmldoc.append("<item>");
                writexmldoc.append("<pk_Checktype>").append("0004").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
                writexmldoc.append("<pk_Checkvalue>").append("00111538").append("</pk_Checkvalue>");  // 辅助核算值 档案转换
                writexmldoc.append("</item>");
                //  合同号
                writexmldoc.append("<item>");
                writexmldoc.append("<pk_Checktype>").append("QYZY0020").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
                writexmldoc.append("<pk_Checkvalue>").append("").append("</pk_Checkvalue>");  // 辅助核算值 档案转换
                writexmldoc.append("</item>");
            writexmldoc.append("</ass>");
        	}else if("52010306".equals(jiefangkemu)||"52010306".equals(jiefangkemu)){ 
        	 // 辅助核算  
            writexmldoc.append("<ass>");
        	 // 项目
            writexmldoc.append("<item>");
            writexmldoc.append("<pk_Checktype>").append("0010").append("</pk_Checktype>");
            writexmldoc.append("<pk_Checkvalue>").append(xiangmuname).append("</pk_Checkvalue>");
            writexmldoc.append("</item>");
        	// 部门
            writexmldoc.append("<item>");
            writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
            writexmldoc.append("<pk_Checkvalue>").append(bumenname).append("</pk_Checkvalue>");
            writexmldoc.append("</item>");
            writexmldoc.append("</ass>");
        	}
        }else{
           	 // 辅助核算  
               writexmldoc.append("<ass>");
             
                   // 部门
                   writexmldoc.append("<item>");
                   writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
                   writexmldoc.append("<pk_Checkvalue>").append(bumenname).append("</pk_Checkvalue>");
                   writexmldoc.append("</item>");
                   //  客商
               	writexmldoc.append("<item>");
                   writexmldoc.append("<pk_Checktype>").append("0004").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
                   writexmldoc.append("<pk_Checkvalue>").append("").append("</pk_Checkvalue>");  // 辅助核算值 档案转换
                   writexmldoc.append("</item>");
                   // 专项费用
                   writexmldoc.append("<item>");
                   writexmldoc.append("<pk_Checktype>").append("QYZY0020").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
                   writexmldoc.append("<pk_Checkvalue>").append("01").append("</pk_Checkvalue>");  // 辅助核算值 档案转换
                   writexmldoc.append("</item>");
               writexmldoc.append("</ass>");
        }
        
        
      //借方分录 2
    	if(StringUtil.isNotBlank(zengzhikemu)){
    		String zengzhi = zengzhi("2", zhaiyao, zengzhishui, zengzhikemu, xiangmuname, bumenname);
    		writexmldoc.append(zengzhi);
    	}
        
        //<!-- 欧盟vat导入 -->
        writexmldoc.append("<vatdetail><businesscode></businesscode>");  //交易代码 最大长度为64,类型为:String
        writexmldoc.append("<pk_receivecountry>").append("</pk_receivecountry>");  // 收货国家,最大长度为64,类型为:String
        writexmldoc.append("<pk_suppliervatcode>").append("</pk_suppliervatcode>");  //供应商VAT码,最大长度为64  String
        writexmldoc.append("<pk_taxcode>").append("</pk_taxcode>"); //  税码,最大长度为64,类型为:String-
        writexmldoc.append("<pk_clientvatcode>").append("</pk_clientvatcode>"); // 客户VAT码,最大长度为64,类型为:String-
        writexmldoc.append("<direction>").append("</direction>");   // 方向,最大长度为64,类型为:String-->
        writexmldoc.append("<moneyamount>").append("</moneyamount>");    // 税额,最大长度为64,类型为:Double
        writexmldoc.append("<pk_vatcountry>").append("</pk_vatcountry>");    //  报税国家,最大长度为64,类型为:String
        writexmldoc.append("<taxamount>").append("</taxamount>");   //   税额,最大长度为64,类型为:Double
        writexmldoc.append("</vatdetail>");
        // 流
        writexmldoc.append("<cashFlow><item>");

        writexmldoc.append("<m_pk_currtype></m_pk_currtype>");  //币种,最大长度为64,类型为:String
        writexmldoc.append("<money></money>");  //  <!--原币,最大长度为64,类型为:Double-->
        writexmldoc.append("<moneyglobal></moneyglobal>"); // <!--全局本币,最大长度为64,类型为:Double-->
        writexmldoc.append("<moneygroup></moneygroup>");  // <!--集团本币,最大长度为64,类型为:Double-->
        writexmldoc.append("<moneymain></moneymain>");     // <!--本币,最大长度为64,类型为:Double-->
        writexmldoc.append("<pk_cashflow></pk_cashflow>");  // <!--现金主键,最大长度为64,类型为:String-->
        writexmldoc.append("<pk_innercorp></pk_innercorp>");  //  <!--内部单位主键,最大长度为64,类型为:String-->
        writexmldoc.append("</item></cashFlow></item>"); 
      //<!-- 贷方分录1   -->
        String cjkdaifangfenlu =null;
        if(StringUtils.isNoBlank(cjkmoney)&&!cjkmoney.equals("0")){
        	BigDecimal A = new BigDecimal(bxmoney);
     		BigDecimal B = new BigDecimal(cjkmoney);
     		BigDecimal C = A.subtract(B);
     		 cjkdaifangfenlu = cjkdaifangfenlu(A,B,C,zhaiyao,bumenname,createusercode,shoumoneyuser);
     		
        }else if(StringUtils.isBlank(cjkmoney)||cjkmoney.equals("0")){
        	 cjkdaifangfenlu = cjkdaifangfenlu1(bxmoney,zhaiyao,bumenname,createusercode);
        }
        writexmldoc.append(cjkdaifangfenlu);
      
        //贷方分录 2
        if(StringUtil.isNotBlank(zengzhikemu)){
    		String zengzhi = daifangfenlu("2", zengzhishui, zhaiyao);
    		writexmldoc.append(zengzhi);
        }
        writexmldoc.append("<cashFlow><item>");
        writexmldoc.append("<m_pk_currtype>").append("</m_pk_currtype>");  //<!--币种,最大长度为64,类型为:String-->
        writexmldoc.append("<money>").append("</money>");   // <!--原币,最大长度为64,类型为:Double-->
        writexmldoc.append("<moneyglobal>").append("</moneyglobal>");  // <!--全局本币,最大长度为64,类型为:Double-->
        writexmldoc.append("<moneygroup>").append("</moneygroup>");   //  <!--集团本币,最大长度为64,类型为:Double-->
        writexmldoc.append("<moneymain>").append("</moneymain>");    //  <!--本币,最大长度为64,类型为:Double-->
        writexmldoc.append("<pk_cashflow>").append("</pk_cashflow>");    //  <!--现金主键,最大长度为64,类型为:String-->
        writexmldoc.append("<pk_innercorp>").append("</pk_innercorp>");   //  <!--内部单位主键,最大长度为64,类型为:String-->
        writexmldoc.append("</item></cashFlow></item></details></voucher_head></voucher></ufinterface>");
        String str = writexmldoc.toString();
        SAVELOG("推送凭证的xml:"+str);
        String returnmsg=null;
		try {
			returnmsg = SendVoucher.post(str);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			SAVELOG("推送凭证有问题:"+e.getMessage());
		}
        return returnmsg;
	}
    
    
    /***
     * 固定资产购置调用凭证的接口
     * @param data
     */
    public static String gdzcjtuseinterface(WorkflowEventData data) {
        Map<String, Object> d = data.getBusinessData();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String timeFormat = sdf.format(new Date());
        String fulltime = timeFormat.toString();
        String year = fulltime.substring(0, 4);
        String month = fulltime.substring(5, 7);

        String zhaiyao = ObjectUtils.toString(d.get("摘要"));
        if(StringUtils.isBlank(zhaiyao)){
        	SAVELOG("摘要为空:"+zhaiyao);
        }
        String money = ObjectUtils.toString(d.get("付款金额"));
        if(StringUtils.isBlank(money)){
        	SAVELOG("付款金额为空:"+money);
        	
        }
        
        String bxmoney = ObjectUtils.toString(d.get("小写借款金额"));
        if(StringUtils.isBlank(bxmoney)){
        	SAVELOG("小写借款金额:"+bxmoney);
        	
        }
        String zengzhikemu="";
		String zengzhishui = ObjectUtils.toString(d.get("增值税"));
		if(StringUtils.isNoBlank(zengzhishui)&&!zengzhishui.equals("0")){
			zengzhikemu="2221010101";
		}
        String jiefangkemu = ObjectUtils.toString(d.get("借方科目"));
        if(StringUtils.isBlank(jiefangkemu)){
			SAVELOG("借方科目为空:"+jiefangkemu);
		}
    	
        String xiangmuname = ObjectUtils.toString(d.get("项目编号"));
		if(StringUtils.isBlank(xiangmuname)){
			SAVELOG("项目编号:"+xiangmuname);
		}

        
    	String bumenname = ObjectUtils.toString(d.get("部门编号"));
    	if(StringUtils.isBlank(bumenname)){
        	SAVELOG("部门编号:"+bumenname);
        	return "请选择收部门编号";
        }
    	String username = ObjectUtils.toString(d.get("凭证生成人")) ;
        if(StringUtils.isBlank(username)){
        	SAVELOG("凭证生成人为空:"+username);
        	return "请选择凭证生成人";
        }	
		try {
			OrgManager orgManager = (OrgManager) AppContext.getBean("orgManager");
			V3xOrgMember orgMember = orgManager.getMemberById(Long.parseLong(username));
			username = orgMember.getName();
	        username = GetPinyin.getPingYin(username);
	        //  这个人特殊, 在NCC里重名
	        if("liujie".equals(username)){
	            username ="liujie01";
	        }
	        username="030ZXJX-OA";
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			SAVELOG("获取制单人有误:"+e1.getMessage());
		}
		StringBuffer writexmldoc = new StringBuffer();
        writexmldoc.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        //正式
        writexmldoc.append("<ufinterface account = \"ncc0430\" billtype=\"vouchergl\" businessunitcode=\"develop\" filename=\"\" groupcode=\"GJJT\" isexchange=\"Y\" orgcode=\"101005\" receiver=\"101005\" replace=\"Y\" roottag=\"\" sender=\"030ZXJX-OA\">");
        writexmldoc.append("<voucher id= \"\">");
        writexmldoc.append("<voucher_head>");
        writexmldoc.append("<pk_voucher>").append("").append("</pk_voucher>");
        writexmldoc.append("<pk_vouchertype>").append("01").append("</pk_vouchertype>");// 凭证主键, 没有就是新增, 有就是修改   可以为空
        writexmldoc.append("<year>").append(year).append("</year>");//  会计年度 如2012
        writexmldoc.append("<pk_system>").append("GL").append("</pk_system>");// 来源系统 如GL
        writexmldoc.append("<voucherkind>").append("0").append("</voucherkind>"); //<!--凭证类型值 0：正常凭证 3：数量调整凭证 不可空-->
        writexmldoc.append("<pk_accountingbook>").append("101005-0001").append("</pk_accountingbook>");//<!--核算账簿 非空 （账簿_财务核算账簿）-->
        writexmldoc.append("<discardflag>").append("N").append("</discardflag>"); // 作废标志; 可空 N
        writexmldoc.append("<period>").append(month).append("</period>");// 会计期间, 非空
        writexmldoc.append("<no>").append("</no>");//凭证号为空自动分配 非空：按凭证号处理
        writexmldoc.append("<attachment>").append("0").append("</attachment>");//附单数据 可空
        writexmldoc.append("<prepareddate>").append(fulltime).append("</prepareddate>");//制单日期, 非空
        writexmldoc.append("<pk_prepared>").append(username).append("</pk_prepared>"); // 制单人,非空
        writexmldoc.append("<pk_casher>").append("</pk_casher>"); // 出纳
        writexmldoc.append("<signflag>").append("N").append("</signflag>"); //签字标志
        writexmldoc.append("<pk_checked>").append("</pk_checked>"); // 审核人
        writexmldoc.append("<tallydate></tallydate><pk_manager></pk_manager><memo1></memo1><memo2></memo2><reserve1></reserve1><reserve2>N</reserve2><siscardflag />");

        writexmldoc.append("<pk_org>").append("101005").append("</pk_org>"); //所属组织 非空
        writexmldoc.append("<pk_org_v>").append("101005").append("</pk_org_v>"); //所属组织版本. 可空
        writexmldoc.append("<pk_group>").append("GJJT").append("</pk_group>"); //所集团  如果不输集团取当前登陆集团 例如GJJT
        writexmldoc.append("<details>");

        // 借方分录 分录1
        writexmldoc.append("<item>");
        writexmldoc.append("<detailindex>").append("1").append("</detailindex>"); // 分录号
        writexmldoc.append("<explanation>").append(zhaiyao).append("</explanation>"); //   摘要 非空
        writexmldoc.append("<verifydate>").append("").append("</verifydate>");  // 业务日期, 可空
        writexmldoc.append("<price>").append("0").append("</price>");//单价 可空
        writexmldoc.append("<excrate2>").append("").append("</excrate2>"); // 折本汇率 可空
        writexmldoc.append("<debitquantity>").append("").append("</debitquantity>"); // 借方数量 可空
        writexmldoc.append("<debitamount>").append(bxmoney).append("</debitamount>"); //原币借方金额 非空
        writexmldoc.append("<localdebitamount>").append(bxmoney).append("</localdebitamount>"); //本币借方金额 非空
        writexmldoc.append("<groupdebitamount>").append(bxmoney).append("</groupdebitamount>"); //集团本币借方金额 非空
        writexmldoc.append("<globaldebitamount>").append(bxmoney).append("</globaldebitamount>"); //全局本币借方金额 非空
        writexmldoc.append("<pk_currtype>").append("CNY").append("</pk_currtype>"); //币种  非空
        writexmldoc.append("<pk_accasoa>").append(jiefangkemu).append("</pk_accasoa>"); //科目  非空
        writexmldoc.append("<pk_unit>").append("</pk_unit>");// 所属二级核算单位
        writexmldoc.append("<pk_unit_v>").append("</pk_unit_v>");  // 所属二级核算单位 版本可空
        // 辅助核算  
        writexmldoc.append("<ass>");
            // 部门
            writexmldoc.append("<item>");
            writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
            writexmldoc.append("<pk_Checkvalue>").append(bumenname).append("</pk_Checkvalue>");
            writexmldoc.append("</item>");
        writexmldoc.append("</ass>"); 
        
        //借方分录 2
    	if(StringUtil.isNotBlank(zengzhikemu)){
    		String zengzhi = zengzhi("2", zhaiyao, zengzhishui, zengzhikemu, xiangmuname, bumenname);
    		writexmldoc.append(zengzhi);
    	}
        //<!-- 欧盟vat导入 -->
        writexmldoc.append("<vatdetail><businesscode></businesscode>");  //交易代码 最大长度为64,类型为:String
        writexmldoc.append("<pk_receivecountry>").append("</pk_receivecountry>");  // 收货国家,最大长度为64,类型为:String
        writexmldoc.append("<pk_suppliervatcode>").append("</pk_suppliervatcode>");  //供应商VAT码,最大长度为64  String
        writexmldoc.append("<pk_taxcode>").append("</pk_taxcode>"); //  税码,最大长度为64,类型为:String-
        writexmldoc.append("<pk_clientvatcode>").append("</pk_clientvatcode>"); // 客户VAT码,最大长度为64,类型为:String-
        writexmldoc.append("<direction>").append("</direction>");   // 方向,最大长度为64,类型为:String-->
        writexmldoc.append("<moneyamount>").append("</moneyamount>");    // 税额,最大长度为64,类型为:Double
        writexmldoc.append("<pk_vatcountry>").append("</pk_vatcountry>");    //  报税国家,最大长度为64,类型为:String
        writexmldoc.append("<taxamount>").append("</taxamount>");   //   税额,最大长度为64,类型为:Double
        writexmldoc.append("</vatdetail>");
        // 流
        writexmldoc.append("<cashFlow><item>");

        writexmldoc.append("<m_pk_currtype></m_pk_currtype>");  //币种,最大长度为64,类型为:String
        writexmldoc.append("<money></money>");  //  <!--原币,最大长度为64,类型为:Double-->
        writexmldoc.append("<moneyglobal></moneyglobal>"); // <!--全局本币,最大长度为64,类型为:Double-->
        writexmldoc.append("<moneygroup></moneygroup>");  // <!--集团本币,最大长度为64,类型为:Double-->
        writexmldoc.append("<moneymain></moneymain>");     // <!--本币,最大长度为64,类型为:Double-->
        writexmldoc.append("<pk_cashflow></pk_cashflow>");  // <!--现金主键,最大长度为64,类型为:String-->
        writexmldoc.append("<pk_innercorp></pk_innercorp>");  //  <!--内部单位主键,最大长度为64,类型为:String-->
        writexmldoc.append("</item></cashFlow></item>");
        //<!-- 贷方分录   -->
        writexmldoc.append("<item>");
        writexmldoc.append("<creditquantity>").append("</creditquantity>");//<!-- 贷方数量 可空-->
        writexmldoc.append("<creditamount>").append(bxmoney).append("</creditamount>");//<!-- 原币贷方金额 非空-->
        writexmldoc.append("<localcreditamount>").append(bxmoney).append("</localcreditamount>");//<!-- 本币贷方金额 非空-->
        writexmldoc.append("<groupcreditamount>").append(bxmoney).append("</groupcreditamount>");//<!-- 集团本币贷方金额 非空-->
        writexmldoc.append("<globalcreditamount>").append(bxmoney).append("</globalcreditamount>");//<!-- 全局本币贷方金额 非空-->
        writexmldoc.append("<detailindex>").append("1").append("</detailindex>");//<!-- 分录号 非空-->
        writexmldoc.append("<explanation>").append(zhaiyao).append("</explanation>");//<!-- 摘要 非空-->
        writexmldoc.append("<verifydate>").append("</verifydate>");//<!-- 业务日期 可空-->
        writexmldoc.append("<price>").append("0.00000000").append("</price>");//<!-- 单价 可空-->
        writexmldoc.append("<excrate2>").append("1").append("</excrate2>");//<!-- 折本汇率 可空-->
        writexmldoc.append("<pk_currtype>").append("CNY").append("</pk_currtype>");//<!-- 币种 非空-->
        writexmldoc.append("<pk_accasoa>").append("112303").append("</pk_accasoa>");//<!-- 科目 非空-->
        writexmldoc.append("<pk_unit>").append("</pk_unit>");////<!-- 所属二级核算单位 可空 （组织） -->
        writexmldoc.append("<pk_unit_v>").append("</pk_unit_v>");//<!-- 所属二级核算单位 版本可空 （组织） -->
        	//辅助核算
            writexmldoc.append("<ass>");
            // 部门
            writexmldoc.append("<item>");
            writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
            writexmldoc.append("<pk_Checkvalue>").append(bumenname).append("</pk_Checkvalue>");
            writexmldoc.append("</item>");
            writexmldoc.append("</ass>");
          //贷方分录 2
            if(StringUtil.isNotBlank(zengzhikemu)){
        		String zengzhi = daifangfenlu("2", zengzhishui, zhaiyao);
        		writexmldoc.append(zengzhi);
            }
        writexmldoc.append("<cashFlow><item>");
        writexmldoc.append("<m_pk_currtype>").append("</m_pk_currtype>");  //<!--币种,最大长度为64,类型为:String-->
        writexmldoc.append("<money>").append("</money>");   // <!--原币,最大长度为64,类型为:Double-->
        writexmldoc.append("<moneyglobal>").append("</moneyglobal>");  // <!--全局本币,最大长度为64,类型为:Double-->
        writexmldoc.append("<moneygroup>").append("</moneygroup>");   //  <!--集团本币,最大长度为64,类型为:Double-->
        writexmldoc.append("<moneymain>").append("</moneymain>");    //  <!--本币,最大长度为64,类型为:Double-->
        writexmldoc.append("<pk_cashflow>").append("</pk_cashflow>");    //  <!--现金主键,最大长度为64,类型为:String-->
        writexmldoc.append("<pk_innercorp>").append("</pk_innercorp>");   //  <!--内部单位主键,最大长度为64,类型为:String-->
        writexmldoc.append("</item></cashFlow></item></details></voucher_head></voucher></ufinterface>");
        String str = writexmldoc.toString();
        SAVELOG("推送凭证的xml:"+str);
        String returnmsg=null;
		try {
			returnmsg = SendVoucher.post(str);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			SAVELOG("推送凭证有问题:"+e.getMessage());
		}
        return returnmsg;
	}
    
    
    /***
     * 固定资产购置费调用凭证的接口
     * @param data
     */
    public static String ggxcjtuseinterface(WorkflowEventData data) {
        Map<String, Object> d = data.getBusinessData();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String timeFormat = sdf.format(new Date());
        String fulltime = timeFormat.toString();
        String year = fulltime.substring(0, 4);
        String month = fulltime.substring(5, 7);

        String zhaiyao = ObjectUtils.toString(d.get("摘要"));
        if(StringUtils.isBlank(zhaiyao)){
        	SAVELOG("摘要为空:"+zhaiyao);
        }
        String money = ObjectUtils.toString(d.get("付款金额"));
        if(StringUtils.isBlank(money)){
        	SAVELOG("付款金额为空:"+money);
        	
        }
        String bxmoney = ObjectUtils.toString(d.get("小写借款金额"));
        if(StringUtils.isBlank(bxmoney)){
        	SAVELOG("小写借款金额:"+bxmoney);
        	
        }
        
        String shoumoneyuser =  ObjectUtils.toString(d.get("收款人账号"));
		if(StringUtils.isBlank(shoumoneyuser)){
			SAVELOG("收款人账号为空:"+shoumoneyuser);
		}
		String zengzhikemu="";
		String zengzhishui = ObjectUtils.toString(d.get("增值税"));
		if(StringUtils.isNoBlank(zengzhishui)&&!zengzhishui.equals("0")){
			zengzhikemu="2221010101";
		}
		String cjkmoney =  ObjectUtils.toString(d.get("冲借款金额"));
			if(StringUtils.isBlank(cjkmoney)){
				SAVELOG("冲借款金额:"+cjkmoney);
		}
		
    	String deptname="";
    	String bumenname = ObjectUtils.toString(d.get("部门编号"));
    	if(StringUtils.isBlank(bumenname)){
        	SAVELOG("部门编号为空:"+bumenname);
        	return "请选择部门编号";
        }else{
        	List<String> list=new ArrayList<String>();
        	list.add(bumenname);
        	deptname=JdbcUtil.jdbcselectone("select name from org_unit where code=?",list,"name");
        }
    	
		String jiefangkemu=  ObjectUtils.toString(d.get("借方科目"));
		 if(StringUtils.isBlank(jiefangkemu)){
				SAVELOG("借方科目为空:"+jiefangkemu);
			}
		
		String xiangmuname = ObjectUtils.toString(d.get("项目编号"));
		if(StringUtils.isBlank(xiangmuname)){
			SAVELOG("项目编号:"+xiangmuname);
		}
		
		String username = ObjectUtils.toString(d.get("凭证生成人")) ;
		String createusercode = "";
        if(StringUtils.isBlank(username)){
        	SAVELOG("凭证生成人为空:"+username);
        	return "请选择凭证生成人";
        }	
		try {
			OrgManager orgManager = (OrgManager) AppContext.getBean("orgManager");
			V3xOrgMember orgMember = orgManager.getMemberById(Long.parseLong(username));
			username = orgMember.getName();
	        username = GetPinyin.getPingYin(username);
	        //  这个人特殊, 在NCC里重名
	        if("liujie".equals(username)){
	            username ="liujie01";
	        }
	        //发起人
	        ColSummary colSummary = colManager.getSummaryById(data.getSummaryId());
	        Long startMemberId = colSummary.getStartMemberId();
	        V3xOrgMember faqiorgMember = orgManager.getMemberById(startMemberId);
	        createusercode= faqiorgMember.getCode();
	        username="030ZXJX-OA";
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			SAVELOG("获取制单人有误:"+e1.getMessage());
		}
		StringBuffer writexmldoc = new StringBuffer();
        writexmldoc.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        //正式
        writexmldoc.append("<ufinterface account = \"ncc0430\" billtype=\"vouchergl\" businessunitcode=\"develop\" filename=\"\" groupcode=\"GJJT\" isexchange=\"Y\" orgcode=\"101005\" receiver=\"101005\" replace=\"Y\" roottag=\"\" sender=\"030ZXJX-OA\">");
        writexmldoc.append("<voucher id= \"\">");
        writexmldoc.append("<voucher_head>");
        writexmldoc.append("<pk_voucher>").append("").append("</pk_voucher>");
        writexmldoc.append("<pk_vouchertype>").append("01").append("</pk_vouchertype>");// 凭证主键, 没有就是新增, 有就是修改   可以为空
        writexmldoc.append("<year>").append(year).append("</year>");//  会计年度 如2012
        writexmldoc.append("<pk_system>").append("GL").append("</pk_system>");// 来源系统 如GL
        writexmldoc.append("<voucherkind>").append("0").append("</voucherkind>"); //<!--凭证类型值 0：正常凭证 3：数量调整凭证 不可空-->
        writexmldoc.append("<pk_accountingbook>").append("101005-0001").append("</pk_accountingbook>");//<!--核算账簿 非空 （账簿_财务核算账簿）-->
        writexmldoc.append("<discardflag>").append("N").append("</discardflag>"); // 作废标志; 可空 N
        writexmldoc.append("<period>").append(month).append("</period>");// 会计期间, 非空
        writexmldoc.append("<no>").append("</no>");//凭证号为空自动分配 非空：按凭证号处理
        writexmldoc.append("<attachment>").append("0").append("</attachment>");//附单数据 可空
        writexmldoc.append("<prepareddate>").append(fulltime).append("</prepareddate>");//制单日期, 非空
        writexmldoc.append("<pk_prepared>").append(username).append("</pk_prepared>"); // 制单人,非空
        writexmldoc.append("<pk_casher>").append("</pk_casher>"); // 出纳
        writexmldoc.append("<signflag>").append("N").append("</signflag>"); //签字标志
        writexmldoc.append("<pk_checked>").append("</pk_checked>"); // 审核人
        writexmldoc.append("<tallydate></tallydate><pk_manager></pk_manager><memo1></memo1><memo2></memo2><reserve1></reserve1><reserve2>N</reserve2><siscardflag />");

        writexmldoc.append("<pk_org>").append("101005").append("</pk_org>"); //所属组织 非空
        writexmldoc.append("<pk_org_v>").append("101005").append("</pk_org_v>"); //所属组织版本. 可空
        writexmldoc.append("<pk_group>").append("GJJT").append("</pk_group>"); //所集团  如果不输集团取当前登陆集团 例如GJJT
        writexmldoc.append("<details>");

        // 借方分录 分录1
        writexmldoc.append("<item>");
        writexmldoc.append("<detailindex>").append("1").append("</detailindex>"); // 分录号
        writexmldoc.append("<explanation>").append(zhaiyao).append("</explanation>"); //   摘要 非空
        writexmldoc.append("<verifydate>").append("").append("</verifydate>");  // 业务日期, 可空
        writexmldoc.append("<price>").append("0").append("</price>");//单价 可空
        writexmldoc.append("<excrate2>").append("").append("</excrate2>"); // 折本汇率 可空
        writexmldoc.append("<debitquantity>").append("").append("</debitquantity>"); // 借方数量 可空
        writexmldoc.append("<debitamount>").append(bxmoney).append("</debitamount>"); //原币借方金额 非空
        writexmldoc.append("<localdebitamount>").append(bxmoney).append("</localdebitamount>"); //本币借方金额 非空
        writexmldoc.append("<groupdebitamount>").append(bxmoney).append("</groupdebitamount>"); //集团本币借方金额 非空
        writexmldoc.append("<globaldebitamount>").append(bxmoney).append("</globaldebitamount>"); //全局本币借方金额 非空
        writexmldoc.append("<pk_currtype>").append("CNY").append("</pk_currtype>"); //币种  非空
        writexmldoc.append("<pk_accasoa>").append(jiefangkemu).append("</pk_accasoa>"); //科目  非空
        writexmldoc.append("<pk_unit>").append("</pk_unit>");// 所属二级核算单位
        writexmldoc.append("<pk_unit_v>").append("</pk_unit_v>");  // 所属二级核算单位 版本可空
       // 辅助核算  
        writexmldoc.append("<ass>");
                // 部门
                writexmldoc.append("<item>");
                writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
                writexmldoc.append("<pk_Checkvalue>").append(bumenname).append("</pk_Checkvalue>");
                writexmldoc.append("</item>");
                //  客商
            	writexmldoc.append("<item>");
                writexmldoc.append("<pk_Checktype>").append("0004").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
                writexmldoc.append("<pk_Checkvalue>").append("00111538").append("</pk_Checkvalue>");  // 辅助核算值 档案转换
                writexmldoc.append("</item>");
          writexmldoc.append("</ass>");
      //借方分录 2
    	if(StringUtil.isNotBlank(zengzhikemu)){
    		String zengzhi = zengzhi("2", zhaiyao, zengzhishui, zengzhikemu, xiangmuname, deptname);
    		writexmldoc.append(zengzhi);
    	}
        
        //<!-- 欧盟vat导入 -->
        writexmldoc.append("<vatdetail><businesscode></businesscode>");  //交易代码 最大长度为64,类型为:String
        writexmldoc.append("<pk_receivecountry>").append("</pk_receivecountry>");  // 收货国家,最大长度为64,类型为:String
        writexmldoc.append("<pk_suppliervatcode>").append("</pk_suppliervatcode>");  //供应商VAT码,最大长度为64  String
        writexmldoc.append("<pk_taxcode>").append("</pk_taxcode>"); //  税码,最大长度为64,类型为:String-
        writexmldoc.append("<pk_clientvatcode>").append("</pk_clientvatcode>"); // 客户VAT码,最大长度为64,类型为:String-
        writexmldoc.append("<direction>").append("</direction>");   // 方向,最大长度为64,类型为:String-->
        writexmldoc.append("<moneyamount>").append("</moneyamount>");    // 税额,最大长度为64,类型为:Double
        writexmldoc.append("<pk_vatcountry>").append("</pk_vatcountry>");    //  报税国家,最大长度为64,类型为:String
        writexmldoc.append("<taxamount>").append("</taxamount>");   //   税额,最大长度为64,类型为:Double
        writexmldoc.append("</vatdetail>");
        // 流
        writexmldoc.append("<cashFlow><item>");

        writexmldoc.append("<m_pk_currtype></m_pk_currtype>");  //币种,最大长度为64,类型为:String
        writexmldoc.append("<money></money>");  //  <!--原币,最大长度为64,类型为:Double-->
        writexmldoc.append("<moneyglobal></moneyglobal>"); // <!--全局本币,最大长度为64,类型为:Double-->
        writexmldoc.append("<moneygroup></moneygroup>");  // <!--集团本币,最大长度为64,类型为:Double-->
        writexmldoc.append("<moneymain></moneymain>");     // <!--本币,最大长度为64,类型为:Double-->
        writexmldoc.append("<pk_cashflow></pk_cashflow>");  // <!--现金主键,最大长度为64,类型为:String-->
        writexmldoc.append("<pk_innercorp></pk_innercorp>");  //  <!--内部单位主键,最大长度为64,类型为:String-->
        writexmldoc.append("</item></cashFlow></item>"); 
       
        //<!-- 贷方分录1   -->
        String cjkdaifangfenlu =null;
        if(StringUtils.isNoBlank(cjkmoney)&&!cjkmoney.equals("0")){
        	BigDecimal A = new BigDecimal(bxmoney);
     		BigDecimal B = new BigDecimal(cjkmoney);
     		BigDecimal C = A.subtract(B);
     		 cjkdaifangfenlu = cjkdaifangfenlu(A,B,C,zhaiyao,bumenname,createusercode,shoumoneyuser);
     		
        }else if(StringUtils.isBlank(cjkmoney)||cjkmoney.equals("0")){
        	 cjkdaifangfenlu = cjkdaifangfenlu1(bxmoney,zhaiyao,bumenname,createusercode);
        }
        writexmldoc.append(cjkdaifangfenlu);
        //贷方分录 2
        if(StringUtil.isNotBlank(zengzhikemu)){
    		String zengzhi = daifangfenlu("2", zengzhishui, zhaiyao);
    		writexmldoc.append(zengzhi);
        }
        writexmldoc.append("<cashFlow><item>");
        writexmldoc.append("<m_pk_currtype>").append("</m_pk_currtype>");  //<!--币种,最大长度为64,类型为:String-->
        writexmldoc.append("<money>").append("</money>");   // <!--原币,最大长度为64,类型为:Double-->
        writexmldoc.append("<moneyglobal>").append("</moneyglobal>");  // <!--全局本币,最大长度为64,类型为:Double-->
        writexmldoc.append("<moneygroup>").append("</moneygroup>");   //  <!--集团本币,最大长度为64,类型为:Double-->
        writexmldoc.append("<moneymain>").append("</moneymain>");    //  <!--本币,最大长度为64,类型为:Double-->
        writexmldoc.append("<pk_cashflow>").append("</pk_cashflow>");    //  <!--现金主键,最大长度为64,类型为:String-->
        writexmldoc.append("<pk_innercorp>").append("</pk_innercorp>");   //  <!--内部单位主键,最大长度为64,类型为:String-->
        writexmldoc.append("</item></cashFlow></item></details></voucher_head></voucher></ufinterface>");
        String str = writexmldoc.toString();
        SAVELOG("推送凭证的xml:"+str);
        String returnmsg=null;
		try {
			returnmsg = SendVoucher.post(str);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			SAVELOG("推送凭证有问题:"+e.getMessage());
		}
        return returnmsg;
	}
    
    
    /***
     * 其它费用调用凭证的接口
     * @param data
     */
    public static String qitatuseinterface(WorkflowEventData data) {
        Map<String, Object> d = data.getBusinessData();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String timeFormat = sdf.format(new Date());
        String fulltime = timeFormat.toString();
        String year = fulltime.substring(0, 4);
        String month = fulltime.substring(5, 7);

        String zhaiyao = ObjectUtils.toString(d.get("摘要"));
        if(StringUtils.isBlank(zhaiyao)){
        	SAVELOG("摘要为空:"+zhaiyao);
        }
        String money = ObjectUtils.toString(d.get("付款金额"));
        if(StringUtils.isBlank(money)){
        	SAVELOG("付款金额为空:"+money);
        	
        }
        String bxmoney = ObjectUtils.toString(d.get("小写借款金额"));
        if(StringUtils.isBlank(bxmoney)){
        	SAVELOG("小写借款金额:"+bxmoney);
        	
        }
        String shoumoneyuser =  ObjectUtils.toString(d.get("收款人账号"));
		if(StringUtils.isBlank(shoumoneyuser)){
			SAVELOG("收款人账号为空:"+shoumoneyuser);
		}
		
		
		String zengzhikemu="";
		String zengzhishui = ObjectUtils.toString(d.get("增值税"));
		if(StringUtils.isNoBlank(zengzhishui)&&!zengzhishui.equals("0")){
			zengzhikemu="2221010101";
		}
		String cjkmoney =  ObjectUtils.toString(d.get("冲借款金额"));
			if(StringUtils.isBlank(cjkmoney)){
				SAVELOG("冲借款金额:"+cjkmoney);
		}
		
    	String bumenname = ObjectUtils.toString(d.get("部门编号"));
    	if(StringUtils.isBlank(bumenname)){
        	SAVELOG("部门编号为空:"+bumenname);
        	return "请选择部门编号";
        }
    	
		String jiefangkemu=  ObjectUtils.toString(d.get("借方科目"));
		 if(StringUtils.isBlank(jiefangkemu)){
				SAVELOG("借方科目为空:"+jiefangkemu);
			}
		String xiangmuname = ObjectUtils.toString(d.get("项目编号"));
		if(StringUtils.isBlank(xiangmuname)){
			SAVELOG("项目编号:"+xiangmuname);
		}
		
		String username = ObjectUtils.toString(d.get("凭证生成人")) ;
		String createusercode = "";
        if(StringUtils.isBlank(username)){
        	SAVELOG("凭证生成人为空:"+username);
        	return "请选择凭证生成人";
        }	
		try {
			OrgManager orgManager = (OrgManager) AppContext.getBean("orgManager");
			V3xOrgMember orgMember = orgManager.getMemberById(Long.parseLong(username));
			username = orgMember.getName();
	        username = GetPinyin.getPingYin(username);
	        //  这个人特殊, 在NCC里重名
	        if("liujie".equals(username)){
	            username ="liujie01";
	        }
	        //发起人
	        ColSummary colSummary = colManager.getSummaryById(data.getSummaryId());
	        Long startMemberId = colSummary.getStartMemberId();
	        V3xOrgMember faqiorgMember = orgManager.getMemberById(startMemberId);
	        createusercode= faqiorgMember.getCode();
	        username="030ZXJX-OA";
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			SAVELOG("获取制单人有误:"+e1.getMessage());
		}
		StringBuffer writexmldoc = new StringBuffer();
        writexmldoc.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        //正式
        writexmldoc.append("<ufinterface account = \"ncc0430\" billtype=\"vouchergl\" businessunitcode=\"develop\" filename=\"\" groupcode=\"GJJT\" isexchange=\"Y\" orgcode=\"101005\" receiver=\"101005\" replace=\"Y\" roottag=\"\" sender=\"030ZXJX-OA\">");
        writexmldoc.append("<voucher id= \"\">");
        writexmldoc.append("<voucher_head>");
        writexmldoc.append("<pk_voucher>").append("").append("</pk_voucher>");
        writexmldoc.append("<pk_vouchertype>").append("01").append("</pk_vouchertype>");// 凭证主键, 没有就是新增, 有就是修改   可以为空
        writexmldoc.append("<year>").append(year).append("</year>");//  会计年度 如2012
        writexmldoc.append("<pk_system>").append("GL").append("</pk_system>");// 来源系统 如GL
        writexmldoc.append("<voucherkind>").append("0").append("</voucherkind>"); //<!--凭证类型值 0：正常凭证 3：数量调整凭证 不可空-->
        writexmldoc.append("<pk_accountingbook>").append("101005-0001").append("</pk_accountingbook>");//<!--核算账簿 非空 （账簿_财务核算账簿）-->
        writexmldoc.append("<discardflag>").append("N").append("</discardflag>"); // 作废标志; 可空 N
        writexmldoc.append("<period>").append(month).append("</period>");// 会计期间, 非空
        writexmldoc.append("<no>").append("</no>");//凭证号为空自动分配 非空：按凭证号处理
        writexmldoc.append("<attachment>").append("0").append("</attachment>");//附单数据 可空
        writexmldoc.append("<prepareddate>").append(fulltime).append("</prepareddate>");//制单日期, 非空
        writexmldoc.append("<pk_prepared>").append(username).append("</pk_prepared>"); // 制单人,非空
        writexmldoc.append("<pk_casher>").append("</pk_casher>"); // 出纳
        writexmldoc.append("<signflag>").append("N").append("</signflag>"); //签字标志
        writexmldoc.append("<pk_checked>").append("</pk_checked>"); // 审核人
        writexmldoc.append("<tallydate></tallydate><pk_manager></pk_manager><memo1></memo1><memo2></memo2><reserve1></reserve1><reserve2>N</reserve2><siscardflag />");

        writexmldoc.append("<pk_org>").append("101005").append("</pk_org>"); //所属组织 非空
        writexmldoc.append("<pk_org_v>").append("101005").append("</pk_org_v>"); //所属组织版本. 可空
        writexmldoc.append("<pk_group>").append("GJJT").append("</pk_group>"); //所集团  如果不输集团取当前登陆集团 例如GJJT
        writexmldoc.append("<details>");

        // 借方分录 分录1
        writexmldoc.append("<item>");
        writexmldoc.append("<detailindex>").append("1").append("</detailindex>"); // 分录号
        writexmldoc.append("<explanation>").append(zhaiyao).append("</explanation>"); //   摘要 非空
        writexmldoc.append("<verifydate>").append("").append("</verifydate>");  // 业务日期, 可空
        writexmldoc.append("<price>").append("0").append("</price>");//单价 可空
        writexmldoc.append("<excrate2>").append("").append("</excrate2>"); // 折本汇率 可空
        writexmldoc.append("<debitquantity>").append("").append("</debitquantity>"); // 借方数量 可空
        writexmldoc.append("<debitamount>").append(bxmoney).append("</debitamount>"); //原币借方金额 非空
        writexmldoc.append("<localdebitamount>").append(bxmoney).append("</localdebitamount>"); //本币借方金额 非空
        writexmldoc.append("<groupdebitamount>").append(bxmoney).append("</groupdebitamount>"); //集团本币借方金额 非空
        writexmldoc.append("<globaldebitamount>").append(bxmoney).append("</globaldebitamount>"); //全局本币借方金额 非空
        writexmldoc.append("<pk_currtype>").append("CNY").append("</pk_currtype>"); //币种  非空
        writexmldoc.append("<pk_accasoa>").append(jiefangkemu).append("</pk_accasoa>"); //科目  非空
        writexmldoc.append("<pk_unit>").append("</pk_unit>");// 所属二级核算单位
        writexmldoc.append("<pk_unit_v>").append("</pk_unit_v>");  // 所属二级核算单位 版本可空
        
        if(StringUtil.isNotBlank(xiangmuname)){
        	String str  = jiefangkemu.substring(0,4);
        	if(str.equals("5404")){
        		 // 辅助核算  
                writexmldoc.append("<ass>");
                // 项目
                writexmldoc.append("<item>");
                writexmldoc.append("<pk_Checktype>").append("0010").append("</pk_Checktype>");
                writexmldoc.append("<pk_Checkvalue>").append(xiangmuname).append("</pk_Checkvalue>");
                writexmldoc.append("</item>");
                    // 部门
                    writexmldoc.append("<item>");
                    writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
                    writexmldoc.append("<pk_Checkvalue>").append(bumenname).append("</pk_Checkvalue>");
                    writexmldoc.append("</item>");
                    //  客商
                	writexmldoc.append("<item>");
                    writexmldoc.append("<pk_Checktype>").append("0004").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
                    writexmldoc.append("<pk_Checkvalue>").append("00111538").append("</pk_Checkvalue>");  // 辅助核算值 档案转换
                    writexmldoc.append("</item>");
                    //  合同号
                    writexmldoc.append("<item>");
                    writexmldoc.append("<pk_Checktype>").append("QYZY0020").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
                    writexmldoc.append("<pk_Checkvalue>").append("").append("</pk_Checkvalue>");  // 辅助核算值 档案转换
                    writexmldoc.append("</item>");
                  writexmldoc.append("</ass>");
        	}else if(str.equals("5201") || str.equals("5101")){
        		// 辅助核算  
        		writexmldoc.append("<ass>");
        		// 项目
                 writexmldoc.append("<item>");
                 writexmldoc.append("<pk_Checktype>").append("0010").append("</pk_Checktype>");
                 writexmldoc.append("<pk_Checkvalue>").append(xiangmuname).append("</pk_Checkvalue>");
                 writexmldoc.append("</item>");
                 // 部门
                 writexmldoc.append("<item>");
                 writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
                 writexmldoc.append("<pk_Checkvalue>").append(bumenname).append("</pk_Checkvalue>");
                 writexmldoc.append("</item>");
           writexmldoc.append("</ass>");
        	}
        }else{
        	 // 辅助核算  
            writexmldoc.append("<ass>");
                    // 部门
                    writexmldoc.append("<item>");
                    writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
                    writexmldoc.append("<pk_Checkvalue>").append(bumenname).append("</pk_Checkvalue>");
                    writexmldoc.append("</item>");
                    //  客商
                	writexmldoc.append("<item>");
                    writexmldoc.append("<pk_Checktype>").append("0004").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
                    writexmldoc.append("<pk_Checkvalue>").append("").append("</pk_Checkvalue>");  // 辅助核算值 档案转换
                    writexmldoc.append("</item>");
              writexmldoc.append("</ass>");
        }
      
      
       //借方分录 2
    	if(StringUtil.isNotBlank(zengzhikemu)){
    		String zengzhi = zengzhi("2", zhaiyao, zengzhishui, zengzhikemu, xiangmuname, bumenname);
    		writexmldoc.append(zengzhi);
    	}
        
        //<!-- 欧盟vat导入 -->
        writexmldoc.append("<vatdetail><businesscode></businesscode>");  //交易代码 最大长度为64,类型为:String
        writexmldoc.append("<pk_receivecountry>").append("</pk_receivecountry>");  // 收货国家,最大长度为64,类型为:String
        writexmldoc.append("<pk_suppliervatcode>").append("</pk_suppliervatcode>");  //供应商VAT码,最大长度为64  String
        writexmldoc.append("<pk_taxcode>").append("</pk_taxcode>"); //  税码,最大长度为64,类型为:String-
        writexmldoc.append("<pk_clientvatcode>").append("</pk_clientvatcode>"); // 客户VAT码,最大长度为64,类型为:String-
        writexmldoc.append("<direction>").append("</direction>");   // 方向,最大长度为64,类型为:String-->
        writexmldoc.append("<moneyamount>").append("</moneyamount>");    // 税额,最大长度为64,类型为:Double
        writexmldoc.append("<pk_vatcountry>").append("</pk_vatcountry>");    //  报税国家,最大长度为64,类型为:String
        writexmldoc.append("<taxamount>").append("</taxamount>");   //   税额,最大长度为64,类型为:Double
        writexmldoc.append("</vatdetail>");
        // 流
        writexmldoc.append("<cashFlow><item>");

        writexmldoc.append("<m_pk_currtype></m_pk_currtype>");  //币种,最大长度为64,类型为:String
        writexmldoc.append("<money></money>");  //  <!--原币,最大长度为64,类型为:Double-->
        writexmldoc.append("<moneyglobal></moneyglobal>"); // <!--全局本币,最大长度为64,类型为:Double-->
        writexmldoc.append("<moneygroup></moneygroup>");  // <!--集团本币,最大长度为64,类型为:Double-->
        writexmldoc.append("<moneymain></moneymain>");     // <!--本币,最大长度为64,类型为:Double-->
        writexmldoc.append("<pk_cashflow></pk_cashflow>");  // <!--现金主键,最大长度为64,类型为:String-->
        writexmldoc.append("<pk_innercorp></pk_innercorp>");  //  <!--内部单位主键,最大长度为64,类型为:String-->
        writexmldoc.append("</item></cashFlow></item>"); 
        //<!-- 贷方分录1   -->
        String cjkdaifangfenlu =null;
        if(StringUtils.isNoBlank(cjkmoney)&&!cjkmoney.equals("0")){
        	BigDecimal A = new BigDecimal(bxmoney);
     		BigDecimal B = new BigDecimal(cjkmoney);
     		BigDecimal C = A.subtract(B);
     		 cjkdaifangfenlu = cjkdaifangfenlu(A,B,C,zhaiyao,bumenname,createusercode,shoumoneyuser);
     		
        }else if(StringUtils.isBlank(cjkmoney)||cjkmoney.equals("0")){
        	 cjkdaifangfenlu = cjkdaifangfenlu1(bxmoney,zhaiyao,bumenname,createusercode);
        }
        writexmldoc.append(cjkdaifangfenlu);
     
        //贷方分录 2
        if(StringUtil.isNotBlank(zengzhikemu)){
    		String zengzhi = daifangfenlu("2", zengzhishui, zhaiyao);
    		writexmldoc.append(zengzhi);
        }
        writexmldoc.append("<cashFlow><item>");
        writexmldoc.append("<m_pk_currtype>").append("</m_pk_currtype>");  //<!--币种,最大长度为64,类型为:String-->
        writexmldoc.append("<money>").append("</money>");   // <!--原币,最大长度为64,类型为:Double-->
        writexmldoc.append("<moneyglobal>").append("</moneyglobal>");  // <!--全局本币,最大长度为64,类型为:Double-->
        writexmldoc.append("<moneygroup>").append("</moneygroup>");   //  <!--集团本币,最大长度为64,类型为:Double-->
        writexmldoc.append("<moneymain>").append("</moneymain>");    //  <!--本币,最大长度为64,类型为:Double-->
        writexmldoc.append("<pk_cashflow>").append("</pk_cashflow>");    //  <!--现金主键,最大长度为64,类型为:String-->
        writexmldoc.append("<pk_innercorp>").append("</pk_innercorp>");   //  <!--内部单位主键,最大长度为64,类型为:String-->
        writexmldoc.append("</item></cashFlow></item></details></voucher_head></voucher></ufinterface>");
        String str = writexmldoc.toString();
        SAVELOG("推送凭证的xml:"+str);
        String returnmsg=null;
		try {
			returnmsg = SendVoucher.post(str);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			SAVELOG("推送凭证有问题:"+e.getMessage());
		}
        return returnmsg;
	}
    
    /**
     * 项目号结尾是否包含J00。C00
     * @param xiangmucode
     * @return
     */
    //有项目号，项目号结尾是J00的科目编码是54040306，项目号以非S开头且结尾是C00的科目编码是52010306，项目号以S开头且结尾是C00为51010306。
	//54040306的辅助核算为项目、部门、客商（一次性/00111538）、合同号（空值）。52010306和51010306的辅助核算为项目、部门，根据项目号提取。
    //（项目号为Y1905500.C00的科目是51010204开头）。
    public static String jiefangkemu(String xiangmuname,String joo,String coo,String soo){
    	String jiefangkemu="";
    	if(StringUtils.isNoBlank(xiangmuname)){
    		if(xiangmuname.equals("Y1905500.C00")){
	        	jiefangkemu ="51010204";
	        }
    		int xingmulength = xiangmuname.length();
	        if(xingmulength>=3){
	        	String str=xiangmuname.substring(xingmulength-3,xingmulength);
	        	String str1=xiangmuname.substring(0,1);
	        	if("J00".equals(str)){
	        		jiefangkemu=joo;
	        	}else if(!"S".equals(str1)&&"C00".equals(str)){
	        		/*List<String> params3 = new ArrayList<String>();
					params3.add(xiangmuname);
					String result = JdbcUtil.jdbcselectone("select "+teshuxiangnucolumn+" from "+teshuxiangnu+" where "+teshuxiangnucolumn+" = ?", params3,teshuxiangnucolumn);
					if(StringUtil.isNotBlank(result)){
						jiefangkemu=joo;
					}else{
						jiefangkemu=coo;
					}*/
	        		jiefangkemu=coo;
	        	}else if("S".equals(str1)&&"C00".equals(str)){
	        		jiefangkemu=soo;
	        	}else{
	        		SAVELOG("项目号不包含J00和C00:"+xiangmuname);
	        	}
	        }else {
	        	SAVELOG("项目号长度不够:"+xiangmuname);
	        }
		}
		return jiefangkemu;
    }
    /**
     * 增值税借方分录 
     * @param detailindex
     * @param zhaiyao
     * @param zengzhishui
     * @param zengzhikemu
     * @param xiangmuname
     * @param bumenname
     * @return
     */
    public static String zengzhi(String detailindex,String zhaiyao,String zengzhishui,String zengzhikemu,String xiangmuname,String bumenname){
    	 StringBuffer writexmldoc = new StringBuffer();
    	 writexmldoc.append("</item>");
     	// 借方分录 
         writexmldoc.append("<item>");
         writexmldoc.append("<detailindex>").append(detailindex).append("</detailindex>"); // 分录号
         writexmldoc.append("<explanation>").append(zhaiyao).append("</explanation>"); //   摘要 非空
         writexmldoc.append("<verifydate>").append("").append("</verifydate>");  // 业务日期, 可空
         writexmldoc.append("<price>").append("0").append("</price>");//单价 可空
         writexmldoc.append("<excrate2>").append("").append("</excrate2>"); // 折本汇率 可空
         writexmldoc.append("<debitquantity>").append("").append("</debitquantity>"); // 借方数量 可空
         writexmldoc.append("<debitamount>").append(zengzhishui).append("</debitamount>"); //原币借方金额 非空
         writexmldoc.append("<localdebitamount>").append(zengzhishui).append("</localdebitamount>"); //本币借方金额 非空
         writexmldoc.append("<groupdebitamount>").append(zengzhishui).append("</groupdebitamount>"); //集团本币借方金额 非空
         writexmldoc.append("<globaldebitamount>").append(zengzhishui).append("</globaldebitamount>"); //全局本币借方金额 非空
         writexmldoc.append("<pk_currtype>").append("CNY").append("</pk_currtype>"); //币种  非空
         writexmldoc.append("<pk_accasoa>").append(zengzhikemu).append("</pk_accasoa>"); //科目  非空
         writexmldoc.append("<pk_unit>").append("</pk_unit>");// 所属二级核算单位
         writexmldoc.append("<pk_unit_v>").append("</pk_unit_v>");  // 所属二级核算单位 版本可空
         // 辅助核算  
         writexmldoc.append("<ass>");
        if(StringUtils.isNoBlank(xiangmuname)){
        	
        	//  客商
            writexmldoc.append("<item>");
            writexmldoc.append("<pk_Checktype>").append("0004").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
            writexmldoc.append("<pk_Checkvalue>").append("00111538").append("</pk_Checkvalue>");  // 辅助核算值 档案转换
            writexmldoc.append("</item>");

            // 项目
            writexmldoc.append("<item>");
            writexmldoc.append("<pk_Checktype>").append("0010").append("</pk_Checktype>");
            writexmldoc.append("<pk_Checkvalue>").append(xiangmuname).append("</pk_Checkvalue>");
            writexmldoc.append("</item>");
            // 部门
            writexmldoc.append("<item>");
            writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
            writexmldoc.append("<pk_Checkvalue>").append(bumenname).append("</pk_Checkvalue>");
            writexmldoc.append("</item>");
        }else{
        	//  客商
            writexmldoc.append("<item>");
            writexmldoc.append("<pk_Checktype>").append("0004").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
            writexmldoc.append("<pk_Checkvalue>").append("00111538").append("</pk_Checkvalue>");  // 辅助核算值 档案转换
            writexmldoc.append("</item>");

            // 项目
            writexmldoc.append("<item>");
            writexmldoc.append("<pk_Checktype>").append("0010").append("</pk_Checktype>");
            writexmldoc.append("<pk_Checkvalue>").append("S2205700.C00").append("</pk_Checkvalue>");
            writexmldoc.append("</item>");
            // 部门
            writexmldoc.append("<item>");
            writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
            writexmldoc.append("<pk_Checkvalue>").append("057").append("</pk_Checkvalue>");
            writexmldoc.append("</item>");
        }
        
        writexmldoc.append("</ass>");
    	
		return writexmldoc.toString();
    	
    }
    
    /**
     * 贷方分录
     * @param detailindex
     * @param money
     * @param zhaiyao
     * @param kemu
     * @return
     */
    public static String daifangfenlu(String detailindex,String money,String zhaiyao){
    	StringBuffer writexmldoc = new StringBuffer();
    	writexmldoc.append("</item>");
    	//<!-- 贷方分录 1  -->
        writexmldoc.append("<item>");
        writexmldoc.append("<creditquantity>").append("</creditquantity>");//<!-- 贷方数量 可空-->
        writexmldoc.append("<creditamount>").append(money).append("</creditamount>");//<!-- 原币贷方金额 非空-->
        writexmldoc.append("<localcreditamount>").append(money).append("</localcreditamount>");//<!-- 本币贷方金额 非空-->
        writexmldoc.append("<groupcreditamount>").append(money).append("</groupcreditamount>");//<!-- 集团本币贷方金额 非空-->
        writexmldoc.append("<globalcreditamount>").append(money).append("</globalcreditamount>");//<!-- 全局本币贷方金额 非空-->
        writexmldoc.append("<detailindex>").append(detailindex).append("</detailindex>");//<!-- 分录号 非空-->
        writexmldoc.append("<explanation>").append(zhaiyao).append("</explanation>");//<!-- 摘要 非空-->
        writexmldoc.append("<verifydate>").append("</verifydate>");//<!-- 业务日期 可空-->
        writexmldoc.append("<price>").append("0.00000000").append("</price>");//<!-- 单价 可空-->
        writexmldoc.append("<excrate2>").append("1").append("</excrate2>");//<!-- 折本汇率 可空-->
        writexmldoc.append("<pk_currtype>").append("CNY").append("</pk_currtype>");//<!-- 币种 非空-->
        writexmldoc.append("<pk_accasoa>").append("10120401").append("</pk_accasoa>");//<!-- 科目 非空-->
        writexmldoc.append("<pk_unit>").append("</pk_unit>");////<!-- 所属二级核算单位 可空 （组织） -->
        writexmldoc.append("<pk_unit_v>").append("</pk_unit_v>");//<!-- 所属二级核算单位 版本可空 （组织） -->

        writexmldoc.append("<ass>");
       
         
        // 银行账户
        writexmldoc.append("<item>");
        writexmldoc.append("<pk_Checktype>").append("0011").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
        writexmldoc.append("<pk_Checkvalue>").append("023").append("</pk_Checkvalue>");  // 辅助核算值 档案转换
        writexmldoc.append("</item>");

        // 现金流量项目
        writexmldoc.append("<item>");
        writexmldoc.append("<pk_Checktype>").append("0007").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
        writexmldoc.append("<pk_Checkvalue>").append("CF100499").append("</pk_Checkvalue>");  // 辅助核算值 档案转换
        writexmldoc.append("</item>");

        //   客商
        writexmldoc.append("<item>");
        writexmldoc.append("<pk_Checktype>").append("0004").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
        writexmldoc.append("<pk_Checkvalue>").append("").append("</pk_Checkvalue>");  // 辅助核算值 档案转换
        writexmldoc.append("</item>");
        
        writexmldoc.append("</ass>");
		return writexmldoc.toString();
    }
    
    
    /**
     * 冲借款贷方分录
     * @param detailindex
     * @param money
     * @param zhaiyao
     * @param kemu
     * @return
     */
    public static String cjkdaifangfenlu(BigDecimal A,BigDecimal B,BigDecimal C,String zhaiyao,String bumenname,String createusercode,String shoumoneyuser){
    	StringBuffer writexmldoc = new StringBuffer();
    if(A.compareTo(B) == 1){ 
     	if (B.compareTo(BigDecimal.ZERO) ==  0) {
     		//<!-- 贷方分录  1 -->
             writexmldoc.append("<item>");
             writexmldoc.append("<creditquantity>").append("</creditquantity>");//<!-- 贷方数量 可空-->
             writexmldoc.append("<creditamount>").append(A).append("</creditamount>");//<!-- 原币贷方金额 非空-->
             writexmldoc.append("<localcreditamount>").append(A).append("</localcreditamount>");//<!-- 本币贷方金额 非空-->
             writexmldoc.append("<groupcreditamount>").append(A).append("</groupcreditamount>");//<!-- 集团本币贷方金额 非空-->
             writexmldoc.append("<globalcreditamount>").append(A).append("</globalcreditamount>");//<!-- 全局本币贷方金额 非空-->
             writexmldoc.append("<detailindex>").append("1").append("</detailindex>");//<!-- 分录号 非空-->
             writexmldoc.append("<explanation>").append(zhaiyao).append("</explanation>");//<!-- 摘要 非空-->
             writexmldoc.append("<verifydate>").append("</verifydate>");//<!-- 业务日期 可空-->
             writexmldoc.append("<price>").append("0.00000000").append("</price>");//<!-- 单价 可空-->
             writexmldoc.append("<excrate2>").append("1").append("</excrate2>");//<!-- 折本汇率 可空-->
             writexmldoc.append("<pk_currtype>").append("CNY").append("</pk_currtype>");//<!-- 币种 非空-->
             writexmldoc.append("<pk_accasoa>").append("12210501").append("</pk_accasoa>");//<!-- 科目 非空-->
             writexmldoc.append("<pk_unit>").append("</pk_unit>");////<!-- 所属二级核算单位 可空 （组织） -->
             writexmldoc.append("<pk_unit_v>").append("</pk_unit_v>");//<!-- 所属二级核算单位 版本可空 （组织） -->
                 // 辅助核算  
   	          writexmldoc.append("<ass>");
   	          //  人员档案
   	          writexmldoc.append("<item>");
   	          writexmldoc.append("<pk_Checktype>").append("0002").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
   	          writexmldoc.append("<pk_Checkvalue>").append(createusercode).append("</pk_Checkvalue>");  // 辅助核算值 档案转换
   	          writexmldoc.append("</item>");
   	           // 部门
   	          writexmldoc.append("<item>");
   	          writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
   	          writexmldoc.append("<pk_Checkvalue>").append(bumenname).append("</pk_Checkvalue>");
   	          writexmldoc.append("</item>");
   	          writexmldoc.append("</ass>");
     	}else{
     	// 贷方分录 分录1
	    	  writexmldoc.append("<item>");
	          writexmldoc.append("<creditquantity>").append("</creditquantity>");//<!-- 贷方数量 可空-->
	          writexmldoc.append("<creditamount>").append(B).append("</creditamount>");//<!-- 原币贷方金额 非空-->
	          writexmldoc.append("<localcreditamount>").append(B).append("</localcreditamount>");//<!-- 本币贷方金额 非空-->
	          writexmldoc.append("<groupcreditamount>").append(B).append("</groupcreditamount>");//<!-- 集团本币贷方金额 非空-->
	          writexmldoc.append("<globalcreditamount>").append(B).append("</globalcreditamount>");//<!-- 全局本币贷方金额 非空-->
	          writexmldoc.append("<detailindex>").append("1").append("</detailindex>");//<!-- 分录号 非空-->
	          writexmldoc.append("<explanation>").append(zhaiyao).append("</explanation>");//<!-- 摘要 非空-->
	          writexmldoc.append("<verifydate>").append("</verifydate>");//<!-- 业务日期 可空-->
	          writexmldoc.append("<price>").append("0.00000000").append("</price>");//<!-- 单价 可空-->
	          writexmldoc.append("<excrate2>").append("1").append("</excrate2>");//<!-- 折本汇率 可空-->
	          writexmldoc.append("<pk_currtype>").append("CNY").append("</pk_currtype>");//<!-- 币种 非空-->
	          writexmldoc.append("<pk_accasoa>").append("12210501").append("</pk_accasoa>"); //科目  非空
	          writexmldoc.append("<pk_unit>").append("</pk_unit>");// 所属二级核算单位
	          writexmldoc.append("<pk_unit_v>").append("</pk_unit_v>");  // 所属二级核算单位 版本可空
	          // 辅助核算  
	          writexmldoc.append("<ass>");
	          //  人员档案
	          writexmldoc.append("<item>");
	          writexmldoc.append("<pk_Checktype>").append("0002").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
	          writexmldoc.append("<pk_Checkvalue>").append(createusercode).append("</pk_Checkvalue>");  // 辅助核算值 档案转换
	          writexmldoc.append("</item>");
	           // 部门
	          writexmldoc.append("<item>");
	          writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
	          writexmldoc.append("<pk_Checkvalue>").append(bumenname).append("</pk_Checkvalue>");
	          writexmldoc.append("</item>");
	          writexmldoc.append("</ass>");
	          writexmldoc.append("</item>");
	          // 借方分录 分录2
	    	  writexmldoc.append("<item>");
	          writexmldoc.append("<creditquantity>").append("</creditquantity>");//<!-- 贷方数量 可空-->
	          writexmldoc.append("<creditamount>").append(C).append("</creditamount>");//<!-- 原币贷方金额 非空-->
	          writexmldoc.append("<localcreditamount>").append(C).append("</localcreditamount>");//<!-- 本币贷方金额 非空-->
	          writexmldoc.append("<groupcreditamount>").append(C).append("</groupcreditamount>");//<!-- 集团本币贷方金额 非空-->
	          writexmldoc.append("<globalcreditamount>").append(C).append("</globalcreditamount>");//<!-- 全局本币贷方金额 非空-->
	          writexmldoc.append("<detailindex>").append("2").append("</detailindex>");//<!-- 分录号 非空-->
	          writexmldoc.append("<explanation>").append(zhaiyao).append("</explanation>");//<!-- 摘要 非空-->
	          writexmldoc.append("<verifydate>").append("</verifydate>");//<!-- 业务日期 可空-->
	          writexmldoc.append("<price>").append("0.00000000").append("</price>");//<!-- 单价 可空-->
	          writexmldoc.append("<excrate2>").append("1").append("</excrate2>");//<!-- 折本汇率 可空-->
	          writexmldoc.append("<pk_currtype>").append("CNY").append("</pk_currtype>");//<!-- 币种 非空-->
	          if(StringUtils.isNoBlank(shoumoneyuser)){
		            writexmldoc.append("<pk_accasoa>").append("1001").append("</pk_accasoa>");//<!-- 科目 非空-->
		            writexmldoc.append("<pk_unit>").append("</pk_unit>");////<!-- 所属二级核算单位 可空 （组织） -->
		            writexmldoc.append("<pk_unit_v>").append("</pk_unit_v>");//<!-- 所属二级核算单位 版本可空 （组织） -->

		            writexmldoc.append("<ass>");
		            // 银行账户
		            writexmldoc.append("<item>");
		            writexmldoc.append("<pk_Checktype>").append("0011").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
		            writexmldoc.append("<pk_Checkvalue>").append("012").append("</pk_Checkvalue>");  // 辅助核算值 档案转换
		            writexmldoc.append("</item>");

		            // 现金流量项目
		            writexmldoc.append("<item>");
		            writexmldoc.append("<pk_Checktype>").append("0007").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
		            writexmldoc.append("<pk_Checkvalue>").append("CF100499").append("</pk_Checkvalue>");  // 辅助核算值 档案转换
		            writexmldoc.append("</item>");
		            //  客商
	             	writexmldoc.append("<item>");
	                 writexmldoc.append("<pk_Checktype>").append("0004").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
	                 writexmldoc.append("<pk_Checkvalue>").append("").append("</pk_Checkvalue>");  // 辅助核算值 档案转换
	                 writexmldoc.append("</item>");
	         }else{
		            writexmldoc.append("<pk_accasoa>").append("10120401").append("</pk_accasoa>");//<!-- 科目 非空-->
		            writexmldoc.append("<pk_unit>").append("</pk_unit>");////<!-- 所属二级核算单位 可空 （组织） -->
		            writexmldoc.append("<pk_unit_v>").append("</pk_unit_v>");//<!-- 所属二级核算单位 版本可空 （组织） -->

		            writexmldoc.append("<ass>");
		            // 银行账户
		            writexmldoc.append("<item>");
		            writexmldoc.append("<pk_Checktype>").append("0011").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
		            writexmldoc.append("<pk_Checkvalue>").append("023").append("</pk_Checkvalue>");  // 辅助核算值 档案转换
		            writexmldoc.append("</item>");

		            // 现金流量项目
		            writexmldoc.append("<item>");
		            writexmldoc.append("<pk_Checktype>").append("0007").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
		            writexmldoc.append("<pk_Checkvalue>").append("CF100499").append("</pk_Checkvalue>");  // 辅助核算值 档案转换
		            writexmldoc.append("</item>");
		            //  客商
	             	writexmldoc.append("<item>");
	                 writexmldoc.append("<pk_Checktype>").append("0004").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
	                 writexmldoc.append("<pk_Checkvalue>").append("").append("</pk_Checkvalue>");  // 辅助核算值 档案转换
	                 writexmldoc.append("</item>");
	         }
	          writexmldoc.append("</ass>");
     	}
     	
     }else{
    	// 贷方分录 分录1
    	 writexmldoc.append("<item>");
         writexmldoc.append("<creditquantity>").append("</creditquantity>");//<!-- 贷方数量 可空-->
         writexmldoc.append("<creditamount>").append(A).append("</creditamount>");//<!-- 原币贷方金额 非空-->
         writexmldoc.append("<localcreditamount>").append(A).append("</localcreditamount>");//<!-- 本币贷方金额 非空-->
         writexmldoc.append("<groupcreditamount>").append(A).append("</groupcreditamount>");//<!-- 集团本币贷方金额 非空-->
         writexmldoc.append("<globalcreditamount>").append(A).append("</globalcreditamount>");//<!-- 全局本币贷方金额 非空-->
         writexmldoc.append("<detailindex>").append("1").append("</detailindex>");//<!-- 分录号 非空-->
         writexmldoc.append("<explanation>").append(zhaiyao).append("</explanation>");//<!-- 摘要 非空-->
         writexmldoc.append("<verifydate>").append("</verifydate>");//<!-- 业务日期 可空-->
         writexmldoc.append("<price>").append("0.00000000").append("</price>");//<!-- 单价 可空-->
         writexmldoc.append("<excrate2>").append("1").append("</excrate2>");//<!-- 折本汇率 可空-->
         writexmldoc.append("<pk_currtype>").append("CNY").append("</pk_currtype>");//<!-- 币种 非空-->
         writexmldoc.append("<pk_accasoa>").append("12210501").append("</pk_accasoa>");//<!-- 科目 非空-->
         writexmldoc.append("<pk_unit>").append("</pk_unit>");////<!-- 所属二级核算单位 可空 （组织） -->
         writexmldoc.append("<pk_unit_v>").append("</pk_unit_v>");//<!-- 所属二级核算单位 版本可空 （组织） -->
          // 辅助核算  
	          writexmldoc.append("<ass>");
	          //  人员档案
	          writexmldoc.append("<item>");
	          writexmldoc.append("<pk_Checktype>").append("0002").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
	          writexmldoc.append("<pk_Checkvalue>").append(createusercode).append("</pk_Checkvalue>");  // 辅助核算值 档案转换
	          writexmldoc.append("</item>");
	           // 部门
	          writexmldoc.append("<item>");
	          writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
	          writexmldoc.append("<pk_Checkvalue>").append(bumenname).append("</pk_Checkvalue>");
	          writexmldoc.append("</item>");
	          writexmldoc.append("</ass>");
     }
    	return writexmldoc.toString();
    }
    
    /**
     * 冲借款贷方分录
     * @param detailindex
     * @param money
     * @param zhaiyao
     * @param kemu
     * @return
     */
    public static String cjkdaifangfenlu1(String bxmoney,String zhaiyao,String bumenname,String createusercode){
    	StringBuffer writexmldoc = new StringBuffer();
     		//<!-- 贷方分录  1 -->
             writexmldoc.append("<item>");
             writexmldoc.append("<creditquantity>").append("</creditquantity>");//<!-- 贷方数量 可空-->
             writexmldoc.append("<creditamount>").append(bxmoney).append("</creditamount>");//<!-- 原币贷方金额 非空-->
             writexmldoc.append("<localcreditamount>").append(bxmoney).append("</localcreditamount>");//<!-- 本币贷方金额 非空-->
             writexmldoc.append("<groupcreditamount>").append(bxmoney).append("</groupcreditamount>");//<!-- 集团本币贷方金额 非空-->
             writexmldoc.append("<globalcreditamount>").append(bxmoney).append("</globalcreditamount>");//<!-- 全局本币贷方金额 非空-->
             writexmldoc.append("<detailindex>").append("1").append("</detailindex>");//<!-- 分录号 非空-->
             writexmldoc.append("<explanation>").append(zhaiyao).append("</explanation>");//<!-- 摘要 非空-->
             writexmldoc.append("<verifydate>").append("</verifydate>");//<!-- 业务日期 可空-->
             writexmldoc.append("<price>").append("0.00000000").append("</price>");//<!-- 单价 可空-->
             writexmldoc.append("<excrate2>").append("1").append("</excrate2>");//<!-- 折本汇率 可空-->
             writexmldoc.append("<pk_currtype>").append("CNY").append("</pk_currtype>");//<!-- 币种 非空-->
             writexmldoc.append("<pk_accasoa>").append("12210501").append("</pk_accasoa>");//<!-- 科目 非空-->
             writexmldoc.append("<pk_unit>").append("</pk_unit>");////<!-- 所属二级核算单位 可空 （组织） -->
             writexmldoc.append("<pk_unit_v>").append("</pk_unit_v>");//<!-- 所属二级核算单位 版本可空 （组织） -->
                 // 辅助核算  
   	          writexmldoc.append("<ass>");
   	          //  人员档案
   	          writexmldoc.append("<item>");
   	          writexmldoc.append("<pk_Checktype>").append("0002").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
   	          writexmldoc.append("<pk_Checkvalue>").append(createusercode).append("</pk_Checkvalue>");  // 辅助核算值 档案转换
   	          writexmldoc.append("</item>");
   	           // 部门
   	          writexmldoc.append("<item>");
   	          writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
   	          writexmldoc.append("<pk_Checkvalue>").append(bumenname).append("</pk_Checkvalue>");
   	          writexmldoc.append("</item>");
   	          writexmldoc.append("</ass>");
     	
    	return writexmldoc.toString();
    }
}
