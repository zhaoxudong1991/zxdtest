package com.seeyon.apps.ncclistencetwo.flow;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.songjian.utils.StringUtils;

import com.seeyon.apps.collaboration.event.CollaborationProcessEvent;
import com.seeyon.apps.collaboration.manager.ColManager;
import com.seeyon.apps.ncclistencetwo.utils.GetPinyin;
import com.seeyon.apps.ncclistencetwo.utils.JdbcUtil;
import com.seeyon.apps.ncclistencetwo.utils.LogInfo;
import com.seeyon.apps.ncclistencetwo.utils.SendVoucher;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.constants.ApplicationCategoryEnum;
import com.seeyon.ctp.form.service.FormManager;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.util.annotation.ListenEvent;
import com.seeyon.ctp.workflow.event.AbstractWorkflowEvent;
import com.seeyon.ctp.workflow.event.WorkflowEventData;
import com.seeyon.ctp.workflow.event.WorkflowEventResult;
import com.seeyon.ctp.workflow.util.WorkflowEventConstants.WorkflowEventType;

public class HengXiagHeTongFromWorkFlowEvent extends AbstractWorkflowEvent {
	private static final Log log = LogFactory.getLog(HengXiagHeTongFromWorkFlowEvent.class);
	private static String hengxiaghetong = AppContext.getSystemProperty("Information.ncctwo.hengxiaghetong");
	private static String hengxiaghetongresult = AppContext.getSystemProperty("Information.ncctwo.hengxiaghetongresult");
	private FormManager formManager;
	private ColManager colManager;

	public ColManager getColManager() {
		return colManager;
	}

	public void setColManager(ColManager colManager) {
		this.colManager = colManager;
	}

	public FormManager getFormManager() {
		return formManager;
	}

	public void setFormManager(FormManager formManager) {
		this.formManager = formManager;
	}

	@Override
	public ApplicationCategoryEnum getAppName() {
		return ApplicationCategoryEnum.form;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return "hengXiangHeTongForm";
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return "表单-横向合同内部拨款单";
	}

	/**
	 * 返回指定的模版编号
	 *
	 * @return
	 */
	public String getTemplateCode() {
		return "";
	}

	@Override
	public WorkflowEventType getType() {
		// TODO Auto-generated method stub
		return super.getType();
	}

	// 发起前事件
	@Override
	public WorkflowEventResult onBeforeStart(WorkflowEventData data) {
		WorkflowEventResult result = new WorkflowEventResult();
		result.setAlertMessage("事件处理完成");
		System.out.println("---发起前事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:"
				+ data.getSummaryId() + "_OperationName" + "====" + data.getBusinessData().get(""));

		return result;
	}

	public String getTotalMidValue(String source, String priStr, String suxStr) {
		if (source == null) {
			return null;
		}
		int iFirst = source.indexOf(priStr);
		int iLast = source.lastIndexOf(suxStr);
		if (iFirst < 0 || iLast < 0) {
			return null;
		}
		int beginIndex = iFirst + priStr.length();
		return source.substring(beginIndex, iLast);
	}

	// 发起事件
	@Override
	public void onStart(WorkflowEventData data) {
		System.out.println("---发起事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:"
				+ data.getSummaryId() + "_OperationName");
	}

	// 处理前事件

	@Override
	@ListenEvent(event = CollaborationProcessEvent.class, async = true)
	public WorkflowEventResult onBeforeFinishWorkitem(WorkflowEventData data) {

		SAVELOG("横向合同内部拨款单的处理前事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:"
				+ data.getSummaryId());

		formManager = (FormManager) AppContext.getBean("formManager");
		WorkflowEventResult workflowEventResult = new WorkflowEventResult();
		String str = useinterface(data);
		SAVELOG("调用凭证推送接口返回值：" + str);
		String xmlmsg = str;
		if (str.contains("处理完毕")) {
			str = "生成凭证";
		} else {
			str = "凭证未生成";
			workflowEventResult.setAlertMessage(str);
			return workflowEventResult;
		}

		String newresult = null;
		if (xmlmsg.contains("异常信息:") || "凭证未生成".equals(str) || xmlmsg.contains("Connection timed out")) {
			if (xmlmsg.contains("异常信息")) {
				newresult = getTotalMidValue(xmlmsg, "异常信息:", "</resultdescription>");
			}

			if (xmlmsg.contains("Connection timed out")) {
				newresult = "凭证未生成, 请检查vpn";
			}

			if (xmlmsg.contains("Exception")) {
				newresult = getTotalMidValue(xmlmsg, "开始处理...", "</resultdescription>");
			}
			newresult = xmlmsg;
			workflowEventResult.setAlertMessage(newresult);
			return workflowEventResult;
		}
		Integer result = changeform(data, str);
		SAVELOG("凭证推送结果回填表单：" + result);
		if (result < 1) {
			workflowEventResult.setAlertMessage("回填凭证结果字段修改失败:" + result);
		}
		return workflowEventResult;
	}

	// 处理事件
	@Override
	public void onFinishWorkitem(WorkflowEventData data) {
		System.out.println("---处理事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:"
				+ data.getSummaryId() + "_OperationName:");

	}

	// 终止前事件
	@Override
	public WorkflowEventResult onBeforeStop(WorkflowEventData data) {
		System.out.println("---终止前事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:"
				+ data.getSummaryId() + "_OperationName");
		return null;
	}

	// 终止事件
	@Override
	public void onStop(WorkflowEventData data) {
		System.out.println("---终止事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:"
				+ data.getSummaryId() + "_OperationName");
	}

	// 回退前事件
	@Override
	public WorkflowEventResult onBeforeStepBack(WorkflowEventData data) {
		System.out.println("---回退前事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:"
				+ data.getSummaryId() + "_OperationName");
		return null;
	}

	// 回退事件
	@Override
	public void onStepBack(WorkflowEventData data) {
		System.out.println("---回退事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:"
				+ data.getSummaryId() + "_OperationName");
	}

	// 撤销前事件
	@Override
	public WorkflowEventResult onBeforeCancel(WorkflowEventData data) {
		System.out.println("---撤销前事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:"
				+ data.getSummaryId() + "_OperationName");
		return null;
	}

	// 撤销事件
	@Override
	public void onCancel(WorkflowEventData data) {
		System.out.println("---撤销事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:"
				+ data.getSummaryId() + "_OperationName");
	}

	// 取回前事件
	@Override
	public WorkflowEventResult onBeforeTakeBack(WorkflowEventData data) {
		System.out.println("---取回前事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:"
				+ data.getSummaryId() + "_OperationName");
		return null;
	}

	// 取回事件
	@Override
	public void onTakeBack(WorkflowEventData data) {
		System.out.println("---取回事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:"
				+ data.getSummaryId() + "_OperationName");
	}

	// 结束事件
	@Override
	public void onProcessFinished(WorkflowEventData data) {
		System.out.println("---结束事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:"
				+ data.getSummaryId() + "_OperationName");
	}

	// 输出日志
	public void SAVELOG(String content) {
		try {
			LogInfo.testMemberFile("HengXiagHeTongFromWorkFlowEvent:" + content + ";\r\n");
		} catch (IOException e) {
			e.printStackTrace();
			log.error("HengXiagHeTongFromWorkFlowEvent添加日志失败：" + e.getMessage());
		}
	}

	/**
	 * 回填表单凭证结果字段
	 * 
	 * @param data
	 * @param auto9
	 * @param agreeAndNosend
	 * @param content
	 * @return
	 */
	public Integer changeform(WorkflowEventData data, String content) {

		Integer result = 0;
		String FORM_RECORDID = "";
		long summaryId = data.getSummaryId();

		// 修改表单上的信息
		try {
			// 查询 form_recordid
			List<String> params = new ArrayList<String>();
			params.add(summaryId + "");
			FORM_RECORDID = JdbcUtil.jdbcselectone("select form_recordid,id from col_summary where id = ?", params,
					"form_recordid");
			if (!StringUtils.isNoBlank(FORM_RECORDID)) {
				SAVELOG("查询  form_recordid失败:" + FORM_RECORDID);
			}

			// 回填凭证结果字段
			List<String> params2 = new ArrayList<String>();
			params2.add(content);
			params2.add(FORM_RECORDID);
			String updatesql = "update " + hengxiaghetong + " set " + hengxiaghetongresult + " = ? where id = ?";
			result = JdbcUtil.aud(updatesql, params2);
			if (result < 1) {
				SAVELOG("回填凭证结果字段修改失败:" + result);
			}
		} catch (Exception e) {
			e.printStackTrace();
			SAVELOG("修改表单上的信息:回填凭证结果字段修改失败:" + e.getMessage());
		}
		return result;
	}

	/***
	 * 调用凭证的接口
	 * 
	 * @param data
	 */
	private String useinterface(WorkflowEventData data) {
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
        String money = ObjectUtils.toString(d.get("金额小写"));
        if(StringUtils.isBlank(money)){
        	SAVELOG("金额为空:"+money);
        	
        }
		
    	String skbumenname = ObjectUtils.toString(d.get("收款单位"));
    	if(StringUtils.isBlank(skbumenname)){
        	SAVELOG("收款单位:"+skbumenname);
        	return "请选择收款单位";
        }
    	String fkbumenname = ObjectUtils.toString(d.get("付款单位"));
    	if(StringUtils.isBlank(fkbumenname)){
        	SAVELOG("付款单位:"+fkbumenname);
        	return "请选择付款单位";
        } 
		String skgongzuoling = ObjectUtils.toString(d.get("收款工作令号"));
		String fkgongzuoling = ObjectUtils.toString(d.get("付款工作令号"));
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
        writexmldoc.append("<debitamount>").append(money).append("</debitamount>"); //原币借方金额 非空
        writexmldoc.append("<localdebitamount>").append(money).append("</localdebitamount>"); //本币借方金额 非空
        writexmldoc.append("<groupdebitamount>").append(money).append("</groupdebitamount>"); //集团本币借方金额 非空
        writexmldoc.append("<globaldebitamount>").append(money).append("</globaldebitamount>"); //全局本币借方金额 非空
        writexmldoc.append("<pk_currtype>").append("CNY").append("</pk_currtype>"); //币种  非空
        writexmldoc.append("<pk_accasoa>").append("2203").append("</pk_accasoa>"); //科目  非空
        writexmldoc.append("<pk_unit>").append("</pk_unit>");// 所属二级核算单位
        writexmldoc.append("<pk_unit_v>").append("</pk_unit_v>");  // 所属二级核算单位 版本可空
        // 辅助核算  
        writexmldoc.append("<ass>");
        //  客商
    	writexmldoc.append("<item>");
        writexmldoc.append("<pk_Checktype>").append("0004").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
        writexmldoc.append("<pk_Checkvalue>").append(skbumenname).append("</pk_Checkvalue>");  // 辅助核算值 档案转换
        writexmldoc.append("</item>");
    	// 项目
        writexmldoc.append("<item>");
        writexmldoc.append("<pk_Checktype>").append("0010").append("</pk_Checktype>");
        writexmldoc.append("<pk_Checkvalue>").append(fkgongzuoling).append("</pk_Checkvalue>");
        writexmldoc.append("</item>");
            // 部门
            writexmldoc.append("<item>");
            writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
            writexmldoc.append("<pk_Checkvalue>").append(fkbumenname).append("</pk_Checkvalue>");
            writexmldoc.append("</item>");
            // 预收款账类型
            writexmldoc.append("<item>");
            writexmldoc.append("<pk_Checktype>").append("QYZY0025").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
            writexmldoc.append("<pk_Checkvalue>").append("3").append("</pk_Checkvalue>");  // 辅助核算值 档案转换
            writexmldoc.append("</item>");
        writexmldoc.append("</ass>"); 
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
        writexmldoc.append("<creditamount>").append(money).append("</creditamount>");//<!-- 原币贷方金额 非空-->
        writexmldoc.append("<localcreditamount>").append(money).append("</localcreditamount>");//<!-- 本币贷方金额 非空-->
        writexmldoc.append("<groupcreditamount>").append(money).append("</groupcreditamount>");//<!-- 集团本币贷方金额 非空-->
        writexmldoc.append("<globalcreditamount>").append(money).append("</globalcreditamount>");//<!-- 全局本币贷方金额 非空-->
        writexmldoc.append("<detailindex>").append("1").append("</detailindex>");//<!-- 分录号 非空-->
        writexmldoc.append("<explanation>").append(zhaiyao).append("</explanation>");//<!-- 摘要 非空-->
        writexmldoc.append("<verifydate>").append("</verifydate>");//<!-- 业务日期 可空-->
        writexmldoc.append("<price>").append("0.00000000").append("</price>");//<!-- 单价 可空-->
        writexmldoc.append("<excrate2>").append("1").append("</excrate2>");//<!-- 折本汇率 可空-->
        writexmldoc.append("<pk_currtype>").append("CNY").append("</pk_currtype>");//<!-- 币种 非空-->
        writexmldoc.append("<pk_accasoa>").append("2203").append("</pk_accasoa>");//<!-- 科目 非空-->
        writexmldoc.append("<pk_unit>").append("</pk_unit>");////<!-- 所属二级核算单位 可空 （组织） -->
        writexmldoc.append("<pk_unit_v>").append("</pk_unit_v>");//<!-- 所属二级核算单位 版本可空 （组织） -->
        	//辅助核算
            writexmldoc.append("<ass>");
            //  客商
        	writexmldoc.append("<item>");
            writexmldoc.append("<pk_Checktype>").append("0004").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
            writexmldoc.append("<pk_Checkvalue>").append(fkbumenname).append("</pk_Checkvalue>");  // 辅助核算值 档案转换
            writexmldoc.append("</item>");
        	// 项目
            writexmldoc.append("<item>");
            writexmldoc.append("<pk_Checktype>").append("0010").append("</pk_Checktype>");
            writexmldoc.append("<pk_Checkvalue>").append(skgongzuoling).append("</pk_Checkvalue>");
            writexmldoc.append("</item>");
                // 部门
                writexmldoc.append("<item>");
                writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
                writexmldoc.append("<pk_Checkvalue>").append(skbumenname).append("</pk_Checkvalue>");
                writexmldoc.append("</item>");
               
                // 预收款账类型
                writexmldoc.append("<item>");
                writexmldoc.append("<pk_Checktype>").append("QYZY0025").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
                writexmldoc.append("<pk_Checkvalue>").append("2").append("</pk_Checkvalue>");  // 辅助核算值 档案转换
                writexmldoc.append("</item>");
                
                writexmldoc.append("</ass>");
        
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
}
