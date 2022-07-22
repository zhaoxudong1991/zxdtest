package com.seeyon.apps.ncclistencetwo.flow;

import com.seeyon.apps.ncclistencetwo.utils.GetPinyin;
import com.seeyon.apps.ncclistencetwo.utils.LogInfo;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.constants.ApplicationCategoryEnum;
import com.seeyon.ctp.common.ctpenumnew.manager.EnumManager;
import com.seeyon.ctp.common.po.ctpenumnew.CtpEnumItem;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.workflow.event.AbstractWorkflowEvent;
import com.seeyon.ctp.workflow.event.WorkflowEventData;
import com.seeyon.ctp.workflow.event.WorkflowEventResult;
import com.seeyon.ctp.workflow.util.WorkflowEventConstants;

import jodd.util.StringUtil;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.songjian.utils.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @program: workspace
 * @author: ldd
 * @Date: 2022-06-24 19:43
 * @description:
 */
public class BaoXiaoPingZhengWorkFlowEvent extends AbstractWorkflowEvent {

	private static final Log log = LogFactory.getLog(BaoXiaoPingZhengWorkFlowEvent.class);

	@Override
	public String getId() {
		return "baoXiaoPingZhengForm";
	}

	@Override
	public String getLabel() {
		return "表单-报销凭证表单";
	}

	@Override
	public WorkflowEventConstants.WorkflowEventType getType() {
		return super.getType();
	}

	@Override
	public ApplicationCategoryEnum getAppName() {
		return ApplicationCategoryEnum.form;
	}

	/**
	 * 返回指定的模版编号
	 * 
	 * @return
	 */
	public String getTemplateCode() {
		return "";
	};

	// 发起前事件
	public WorkflowEventResult onBeforeStart(WorkflowEventData data) {
		WorkflowEventResult result = new WorkflowEventResult();
		result.setAlertMessage("事件处理完成");
		System.out.println("=====" + result.getAlertMessage());
		System.out.println("---发起前事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:"
				+ data.getSummaryId() + "_OperationName" + "====" + data.getBusinessData().get(""));
		return result;
	}

	// 发起事件
	public void onStart(WorkflowEventData data) {
		System.out.println("---发起事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:"
				+ data.getSummaryId() + "_OperationName");
	}

	// 处理前事件
	public WorkflowEventResult onBeforeFinishWorkitem(WorkflowEventData data) {
		System.out.println("---处理前事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:"
				+ data.getSummaryId() + "_OperationName:");
		return null;
	}

	// 处理事件
	public void onFinishWorkitem(WorkflowEventData data) {
		System.out.println("---处理事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:"
				+ data.getSummaryId() + "_OperationName:");
	}

	// 终止前事件
	public WorkflowEventResult onBeforeStop(WorkflowEventData data) {
		System.out.println("---终止前事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:"
				+ data.getSummaryId() + "_OperationName");
		return null;
	}

	// 终止事件
	public void onStop(WorkflowEventData data) {
		System.out.println("---终止事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:"
				+ data.getSummaryId() + "_OperationName");
	}

	// 回退前事件
	public WorkflowEventResult onBeforeStepBack(WorkflowEventData data) {
		System.out.println("---回退前事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:"
				+ data.getSummaryId() + "_OperationName");
		return null;
	}

	// 回退事件
	public void onStepBack(WorkflowEventData data) {
		System.out.println("---回退事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:"
				+ data.getSummaryId() + "_OperationName");
	}

	// 撤销前事件
	public WorkflowEventResult onBeforeCancel(WorkflowEventData data) {
		System.out.println("---撤销前事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:"
				+ data.getSummaryId() + "_OperationName");
		return null;
	}

	// 撤销事件
	public void onCancel(WorkflowEventData data) {
		System.out.println("---撤销事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:"
				+ data.getSummaryId() + "_OperationName");
	}

	// 输出日志
	public static void SAVELOG(String content) {
		try {
			LogInfo.testMemberFile("xxxx:" + content + ";\r\n");
		} catch (IOException e) {
			e.printStackTrace();
			log.error("BaoxiaodanFormWorkflowEvent添加日志失败：" + e.getMessage());
		}
	}

	// 取回前事件
	public WorkflowEventResult onBeforeTakeBack(WorkflowEventData data) {
		System.out.println("---取回前事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:"
				+ data.getSummaryId() + "_OperationName");
		return null;
	}

	// 取回事件
	public void onTakeBack(WorkflowEventData data) {
		System.out.println("---取回事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:"
				+ data.getSummaryId() + "_OperationName");
	}

	private static EnumManager enumManager = (EnumManager) AppContext.getBean("enumManagerNew");

	// 结束事件
	public void onProcessFinished(WorkflowEventData data) {
		log.info("---付款申请单据---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:"
				+ data.getSummaryId() + "formViewOperation" + data.getFormViewOperation());
		Long affairId = data.getAffairId();
		System.out.println("==affairId=====" + affairId);
//    	try {
		Map<String, Object> map = data.getBusinessData();
		String baoxiaotype = null;
		String baoxiaotypeid = ObjectUtils.toString(map.get("报销类型"));
		if (StringUtils.isBlank(baoxiaotypeid)) {
			SAVELOG("报销类型为空id:" + baoxiaotypeid);
		} else {
			CtpEnumItem ctpEnumItemSec = enumManager.getEnumItem(Long.valueOf(baoxiaotypeid));
			baoxiaotype = ctpEnumItemSec.getShowvalue().toString();
		}
		/*
		 * 11、”报销凭证表单”的报销类型是固定资产购置时，给该表单绑定流程处理事件，在处理事件中按规则生成凭证，调用NC的凭证推送接口推送至NC系统；
		 * 凭证信息规则： 1) 借方科目: 借方科目以表单时上填写的借方科目为准，辅助核算，部门
		 * 若有增值税专用发票，则借方多一分录：科目编码为2221010101，若有项目号，辅助核算为客商（00111538/一次性）、项目、部门，根据项目号提取；
		 * 若无项目号，辅助核算为客商（00111538/一次性）、项目（财务部零号S2205700.C00）、部门（资产财务部057），均为固定值。
		 * 若无专票则忽略此分录。 2)贷方科目： 112303，辅助核算客商，部门，项目
		 * 
		 * 生成凭证规则：
		 * 借方科目：16010101/16010201/16010301/16010401/16010501/16010601/16010701/16010801/
		 * 16010901，辅助核算部门 贷方科目：112303，辅助核算客商，部门，项目 若有增值税专用发票，则借方多一分录：科目编码为
		 * 2221010101，若有项目号，辅助核算为客商（00111538/一次性）、项目、部门，根据项目号提取；
		 * 若无项目号，辅助核算为客商（00111538/一次性）、项目（财务部零号 S2205700.C00）、部门（资产财务部
		 * 057），均为固定值。若无专票则忽略此分 录
		 */

		String str = null;
		System.out.println("=======" + baoxiaotype);
		str = useinterface(data, baoxiaotype);
		SAVELOG("调用凭证推送接口返回值：" + str);
		// com.seeyon.cap4.form.bean.FormDataMasterBean master =
		// (com.seeyon.cap4.form.bean.FormDataMasterBean)map.get("formDataBean");

//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

	/***
	 * 调用凭证的接口
	 * 
	 * @param data
	 */
	private String useinterface(WorkflowEventData data, String baoxiaotype) {
		Map<String, Object> d = data.getBusinessData();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String timeFormat = sdf.format(new Date());
		String fulltime = timeFormat.toString();
		String year = fulltime.substring(0, 4);
		String month = fulltime.substring(5, 7);

		String zhaiyao = ObjectUtils.toString(d.get("摘要"));
		if (StringUtils.isBlank(zhaiyao)) {
			SAVELOG("摘要为空:" + zhaiyao);
		}
		String fukuanjine = ObjectUtils.toString(d.get("付款金额"));
		if (StringUtils.isBlank(fukuanjine)) {
			SAVELOG("付款金额为空:" + fukuanjine);
		}

		String username = ObjectUtils.toString(d.get("凭证生成人"));
		if (StringUtils.isBlank(username)) {
			SAVELOG("凭证生成人为空:" + username);
			return "请选择凭证生成人";
		}

		String keshangname = ObjectUtils.toString(d.get("客商名称"));
		if (StringUtils.isBlank(keshangname)) {
			SAVELOG("客商名称为空:" + keshangname);
			return "请选择客商名称";
		}
		String zengzhishui = ObjectUtils.toString(d.get("增值税"));

		String bumengname = ObjectUtils.toString(d.get("部门编码"));
		if (StringUtils.isBlank(bumengname)) {
			SAVELOG("部门编码为空:" + bumengname);
			return "请选择部门";
		}
		String xiangmubianhao = ObjectUtils.toString(d.get("项目编号"));
		if (StringUtils.isBlank(xiangmubianhao)) {
			SAVELOG("项目编号为空:" + xiangmubianhao);
			return "请完善项目编号";
		}
		String shoukuanrenzhuanghao=ObjectUtils.toString(d.get("收款人账号"));  

		try {
			OrgManager orgManager = (OrgManager) AppContext.getBean("orgManager");
			V3xOrgMember orgMember = orgManager.getMemberById(Long.parseLong(username));
			username = orgMember.getName();
			username = GetPinyin.getPingYin(username);

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			SAVELOG("获取制单人有误:" + e1.getMessage());
		}

		String daifangflag = "false";
		StringBuffer writexmldoc = new StringBuffer();
		writexmldoc.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		// 正式
		writexmldoc.append(
				"<ufinterface account = \"01\" billtype=\"vouchergl\" businessunitcode=\"develop\" filename=\"\" groupcode=\"GJJT\" isexchange=\"Y\" orgcode=\"101005\" receiver=\"101005\" replace=\"Y\" roottag=\"\" sender=\"029ZXJX-OA\">");
		writexmldoc.append("<voucher id= \"\">");
		writexmldoc.append("<voucher_head>");
		writexmldoc.append("<pk_voucher>").append("").append("</pk_voucher>");
		writexmldoc.append("<pk_vouchertype>").append("01").append("</pk_vouchertype>");// 凭证主键, 没有就是新增, 有就是修改 可以为空
		writexmldoc.append("<year>").append(year).append("</year>");// 会计年度 如2012
		writexmldoc.append("<pk_system>").append("GL").append("</pk_system>");// 来源系统 如GL
		writexmldoc.append("<voucherkind>").append("0").append("</voucherkind>"); // <!--凭证类型值 0：正常凭证 3：数量调整凭证 不可空-->
		writexmldoc.append("<pk_accountingbook>").append("101005-0001").append("</pk_accountingbook>");// <!--核算账簿 非空
																										// （账簿_财务核算账簿）-->
		writexmldoc.append("<discardflag>").append("N").append("</discardflag>"); // 作废标志; 可空 N
		writexmldoc.append("<period>").append(month).append("</period>");// 会计期间, 非空
		writexmldoc.append("<no>").append("</no>");// 凭证号为空自动分配 非空：按凭证号处理
		writexmldoc.append("<attachment>").append("0").append("</attachment>");// 附单数据 可空
		writexmldoc.append("<prepareddate>").append(fulltime).append("</prepareddate>");// 制单日期, 非空
		writexmldoc.append("<pk_prepared>").append(username).append("</pk_prepared>"); // 制单人,非空
		writexmldoc.append("<pk_casher>").append("</pk_casher>"); // 出纳
		writexmldoc.append("<signflag>").append("N").append("</signflag>"); // 签字标志
		writexmldoc.append("<pk_checked>").append("</pk_checked>"); // 审核人
		writexmldoc.append(
				"<tallydate></tallydate><pk_manager></pk_manager><memo1></memo1><memo2></memo2><reserve1></reserve1><reserve2>N</reserve2><siscardflag />");

		writexmldoc.append("<pk_org>").append("101005").append("</pk_org>"); // 所属组织 非空
		writexmldoc.append("<pk_org_v>").append("101005").append("</pk_org_v>"); // 所属组织版本. 可空
		writexmldoc.append("<pk_group>").append("GJJT").append("</pk_group>"); // 所集团 如果不输集团取当前登陆集团 例如GJJT
		writexmldoc.append("<details>");

		// 借固定资产 贷银行

		if ("固定资产购置".equals(baoxiaotype)) {
			daifangflag = "true";
			String jiefangkemu = " ";
			if (StringUtils.isBlank(jiefangkemu)) {
				return "借方科目为空";
			}
			// 借方分录 分录1
			writexmldoc.append("<item>");
			writexmldoc.append("<detailindex>").append("1").append("</detailindex>"); // 分录号
			writexmldoc.append("<explanation>").append(zhaiyao).append("</explanation>"); // 摘要 非空
			writexmldoc.append("<verifydate>").append("").append("</verifydate>"); // 业务日期, 可空
			writexmldoc.append("<price>").append("0").append("</price>");// 单价 可空
			writexmldoc.append("<excrate2>").append("").append("</excrate2>"); // 折本汇率 可空
			writexmldoc.append("<debitquantity>").append("").append("</debitquantity>"); // 借方数量 可空
			writexmldoc.append("<debitamount>").append(fukuanjine).append("</debitamount>"); // 原币借方金额 非空
			writexmldoc.append("<localdebitamount>").append(fukuanjine).append("</localdebitamount>"); // 本币借方金额 非空
			writexmldoc.append("<groupdebitamount>").append(fukuanjine).append("</groupdebitamount>"); // 集团本币借方金额 非空
			writexmldoc.append("<globaldebitamount>").append(fukuanjine).append("</globaldebitamount>"); // 全局本币借方金额 非空
			writexmldoc.append("<pk_currtype>").append("CNY").append("</pk_currtype>"); // 币种 非空
			writexmldoc.append("<pk_accasoa>").append(jiefangkemu).append("</pk_accasoa>"); // 科目 非空
			writexmldoc.append("<pk_unit>").append("</pk_unit>");// 所属二级核算单位
			writexmldoc.append("<pk_unit_v>").append("</pk_unit_v>"); // 所属二级核算单位 版本可空
			// 辅助核算
			writexmldoc.append("<ass>");

			// 部门
			writexmldoc.append("<item>");
			writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
			writexmldoc.append("<pk_Checkvalue>").append(bumengname).append("</pk_Checkvalue>");
			writexmldoc.append("</item>");

			writexmldoc.append("</ass>");
			if (StringUtils.isBlank(zengzhishui)) {
				SAVELOG("增值税为空:" + zengzhishui);
			}

			if (StringUtils.isNoBlank(zengzhishui)) {
				writexmldoc.append("</item>");
				// 借方分录 分录2
				writexmldoc.append("<item>");
				writexmldoc.append("<detailindex>").append("2").append("</detailindex>"); // 分录号
				writexmldoc.append("<explanation>").append(zhaiyao).append("</explanation>"); // 摘要 非空
				writexmldoc.append("<verifydate>").append("").append("</verifydate>"); // 业务日期, 可空
				writexmldoc.append("<price>").append("0").append("</price>");// 单价 可空
				writexmldoc.append("<excrate2>").append("").append("</excrate2>"); // 折本汇率 可空
				writexmldoc.append("<debitquantity>").append("").append("</debitquantity>"); // 借方数量 可空
				writexmldoc.append("<debitamount>").append(zengzhishui).append("</debitamount>"); // 原币借方金额 非空
				writexmldoc.append("<localdebitamount>").append(zengzhishui).append("</localdebitamount>"); // 本币借方金额 非空
				writexmldoc.append("<groupdebitamount>").append(zengzhishui).append("</groupdebitamount>"); // 集团本币借方金额
																											// 非空
				writexmldoc.append("<globaldebitamount>").append(zengzhishui).append("</globaldebitamount>"); // 全局本币借方金额
																												// 非空
				writexmldoc.append("<pk_currtype>").append("CNY").append("</pk_currtype>"); // 币种 非空
				writexmldoc.append("<pk_accasoa>").append("2221010101").append("</pk_accasoa>"); // 科目 非空
				writexmldoc.append("<pk_unit>").append("</pk_unit>");// 所属二级核算单位
				writexmldoc.append("<pk_unit_v>").append("</pk_unit_v>"); // 所属二级核算单位 版本可空
				// 辅助核算
				writexmldoc.append("<ass>");

				if (StringUtils.isNoBlank(xiangmubianhao)) {
					// 客商
					writexmldoc.append("<item>");
					writexmldoc.append("<pk_Checktype>").append("0004").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
					writexmldoc.append("<pk_Checkvalue>").append("00111538").append("</pk_Checkvalue>"); // 辅助核算值 档案转换
					writexmldoc.append("</item>");
					// 项目
					writexmldoc.append("<item>");
					writexmldoc.append("<pk_Checktype>").append("0010").append("</pk_Checktype>");
					writexmldoc.append("<pk_Checkvalue>").append(xiangmubianhao).append("</pk_Checkvalue>");
					writexmldoc.append("</item>");

					// 部门
					writexmldoc.append("<item>");
					writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
					writexmldoc.append("<pk_Checkvalue>").append(bumengname).append("</pk_Checkvalue>");
					writexmldoc.append("</item>");
				} else {

					// 客商
					writexmldoc.append("<item>");
					writexmldoc.append("<pk_Checktype>").append("0004").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
					writexmldoc.append("<pk_Checkvalue>").append("00111538").append("</pk_Checkvalue>"); // 辅助核算值 档案转换
					writexmldoc.append("</item>");
					// 项目
					writexmldoc.append("<item>");
					writexmldoc.append("<pk_Checktype>").append("0010").append("</pk_Checktype>");
					writexmldoc.append("<pk_Checkvalue>").append("S2105700.C00").append("</pk_Checkvalue>");
					writexmldoc.append("</item>");
					// 部门
					writexmldoc.append("<item>");
					writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
					writexmldoc.append("<pk_Checkvalue>").append("057").append("</pk_Checkvalue>");
					writexmldoc.append("</item>");
				}
				writexmldoc.append("</ass>");

			}

			// <!-- 欧盟vat导入 -->
			writexmldoc.append("<vatdetail><businesscode></businesscode>"); // 交易代码 最大长度为64,类型为:String
			writexmldoc.append("<pk_receivecountry>").append("</pk_receivecountry>"); // 收货国家,最大长度为64,类型为:String
			writexmldoc.append("<pk_suppliervatcode>").append("</pk_suppliervatcode>"); // 供应商VAT码,最大长度为64 String
			writexmldoc.append("<pk_taxcode>").append("</pk_taxcode>"); // 税码,最大长度为64,类型为:String-
			writexmldoc.append("<pk_clientvatcode>").append("</pk_clientvatcode>"); // 客户VAT码,最大长度为64,类型为:String-
			writexmldoc.append("<direction>").append("</direction>"); // 方向,最大长度为64,类型为:String-->
			writexmldoc.append("<moneyamount>").append("</moneyamount>"); // 税额,最大长度为64,类型为:Double
			writexmldoc.append("<pk_vatcountry>").append("</pk_vatcountry>"); // 报税国家,最大长度为64,类型为:String
			writexmldoc.append("<taxamount>").append("</taxamount>"); // 税额,最大长度为64,类型为:Double
			writexmldoc.append("</vatdetail>");

			// 流

			writexmldoc.append("<cashFlow><item>");

			writexmldoc.append("<m_pk_currtype></m_pk_currtype>"); // 币种,最大长度为64,类型为:String
			writexmldoc.append("<money></money>"); // <!--原币,最大长度为64,类型为:Double-->
			writexmldoc.append("<moneyglobal></moneyglobal>"); // <!--全局本币,最大长度为64,类型为:Double-->
			writexmldoc.append("<moneygroup></moneygroup>"); // <!--集团本币,最大长度为64,类型为:Double-->
			writexmldoc.append("<moneymain></moneymain>"); // <!--本币,最大长度为64,类型为:Double-->
			writexmldoc.append("<pk_cashflow></pk_cashflow>"); // <!--现金主键,最大长度为64,类型为:String-->
			writexmldoc.append("<pk_innercorp></pk_innercorp>"); // <!--内部单位主键,最大长度为64,类型为:String-->
			writexmldoc.append("</item></cashFlow></item>");

			// <!-- 贷方分录 -->
			if ("true".equals(daifangflag)) {
				// <!-- 贷方分录1 -->
				writexmldoc.append("<item>");
				writexmldoc.append("<creditquantity>").append("</creditquantity>");// <!-- 贷方数量 可空-->
				writexmldoc.append("<creditamount>").append(fukuanjine).append("</creditamount>");// <!-- 原币贷方金额 非空-->
				writexmldoc.append("<localcreditamount>").append(fukuanjine).append("</localcreditamount>");// <!--
																											// 本币贷方金额
																											// 非空-->
				writexmldoc.append("<groupcreditamount>").append(fukuanjine).append("</groupcreditamount>");// <!--
																											// 集团本币贷方金额
																											// 非空-->
				writexmldoc.append("<globalcreditamount>").append(fukuanjine).append("</globalcreditamount>");// <!--
																												// 全局本币贷方金额
																												// 非空-->
				writexmldoc.append("<detailindex>").append("1").append("</detailindex>");// <!-- 分录号 非空-->
				writexmldoc.append("<explanation>").append(zhaiyao).append("</explanation>");// <!-- 摘要 非空-->
				writexmldoc.append("<verifydate>").append("</verifydate>");// <!-- 业务日期 可空-->
				writexmldoc.append("<price>").append("0.00000000").append("</price>");// <!-- 单价 可空-->
				writexmldoc.append("<excrate2>").append("1").append("</excrate2>");// <!-- 折本汇率 可空-->
				writexmldoc.append("<pk_currtype>").append("CNY").append("</pk_currtype>");// <!-- 币种 非空-->
				writexmldoc.append("<pk_accasoa>").append("112303").append("</pk_accasoa>");// <!-- 科目 非空-->
				writexmldoc.append("<pk_unit>").append("</pk_unit>");//// <!-- 所属二级核算单位 可空 （组织） -->
				writexmldoc.append("<pk_unit_v>").append("</pk_unit_v>");// <!-- 所属二级核算单位 版本可空 （组织） -->

				writexmldoc.append("<ass>");

				// 银行账户
				writexmldoc.append("<item>");
				writexmldoc.append("<pk_Checktype>").append("0011").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
				writexmldoc.append("<pk_Checkvalue>").append("023").append("</pk_Checkvalue>"); // 辅助核算值 档案转换
				writexmldoc.append("</item>");

				// 现金流量项目
				writexmldoc.append("<item>");
				writexmldoc.append("<pk_Checktype>").append("0007").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
				writexmldoc.append("<pk_Checkvalue>").append("CF100499").append("</pk_Checkvalue>"); // 辅助核算值 档案转换
				writexmldoc.append("</item>");

				// 客商
				writexmldoc.append("<item>");
				writexmldoc.append("<pk_Checktype>").append("0004").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
				writexmldoc.append("<pk_Checkvalue>").append("").append("</pk_Checkvalue>"); // 辅助核算值 档案转换
				writexmldoc.append("</item>");

				// 项目
				writexmldoc.append("<item>");
				writexmldoc.append("<pk_Checktype>").append("0010").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
				writexmldoc.append("<pk_Checkvalue>").append("").append("</pk_Checkvalue>"); // 辅助核算值 档案转换
				writexmldoc.append("</item>");

				writexmldoc.append("</ass>");
			}

			writexmldoc.append("<cashFlow><item>");
			writexmldoc.append("<m_pk_currtype>").append("</m_pk_currtype>"); // <!--币种,最大长度为64,类型为:String-->
			writexmldoc.append("<money>").append("</money>"); // <!--原币,最大长度为64,类型为:Double-->
			writexmldoc.append("<moneyglobal>").append("</moneyglobal>"); // <!--全局本币,最大长度为64,类型为:Double-->
			writexmldoc.append("<moneygroup>").append("</moneygroup>"); // <!--集团本币,最大长度为64,类型为:Double-->
			writexmldoc.append("<moneymain>").append("</moneymain>"); // <!--本币,最大长度为64,类型为:Double-->
			writexmldoc.append("<pk_cashflow>").append("</pk_cashflow>"); // <!--现金主键,最大长度为64,类型为:String-->
			writexmldoc.append("<pk_innercorp>").append("</pk_innercorp>"); // <!--内部单位主键,最大长度为64,类型为:String-->
			writexmldoc.append("</item></cashFlow></item></details></voucher_head></voucher></ufinterface>");
			String str = writexmldoc.toString();
			SAVELOG("推送凭证的xml:" + str);

			return str;

		}

		if ("广告宣传费".equals(baoxiaotype)) {
			String chongjiekuanjine = ObjectUtils.toString(d.get("冲借款金额"));
			if (StringUtils.isBlank(chongjiekuanjine)) {
				daifangflag = "false";
				SAVELOG("付款金额为空:" + fukuanjine);
			}else {
				daifangflag = "true";
			}
			
			String jiefangkemu = "";
			jiefangkemu = guanggaojiefangkemu(xiangmubianhao, bumengname, jiefangkemu, jiefangkemu);
			if (StringUtils.isBlank(jiefangkemu)) {
				return "借方科目为空";
			}
			// 借方分录 分录1
			writexmldoc.append("<item>");
			writexmldoc.append("<detailindex>").append("1").append("</detailindex>"); // 分录号
			writexmldoc.append("<explanation>").append(zhaiyao).append("</explanation>"); // 摘要 非空
			writexmldoc.append("<verifydate>").append("").append("</verifydate>"); // 业务日期, 可空
			writexmldoc.append("<price>").append("0").append("</price>");// 单价 可空
			writexmldoc.append("<excrate2>").append("").append("</excrate2>"); // 折本汇率 可空
			writexmldoc.append("<debitquantity>").append("").append("</debitquantity>"); // 借方数量 可空
			writexmldoc.append("<debitamount>").append(fukuanjine).append("</debitamount>"); // 原币借方金额 非空
			writexmldoc.append("<localdebitamount>").append(fukuanjine).append("</localdebitamount>"); // 本币借方金额 非空
			writexmldoc.append("<groupdebitamount>").append(fukuanjine).append("</groupdebitamount>"); // 集团本币借方金额 非空
			writexmldoc.append("<globaldebitamount>").append(fukuanjine).append("</globaldebitamount>"); // 全局本币借方金额 非空
			writexmldoc.append("<pk_currtype>").append("CNY").append("</pk_currtype>"); // 币种 非空
			writexmldoc.append("<pk_accasoa>").append(jiefangkemu).append("</pk_accasoa>"); // 科目 非空
			writexmldoc.append("<pk_unit>").append("</pk_unit>");// 所属二级核算单位
			writexmldoc.append("<pk_unit_v>").append("</pk_unit_v>"); // 所属二级核算单位 版本可空
			// 辅助核算
			writexmldoc.append("<ass>");
			// 客商
			writexmldoc.append("<item>");
			writexmldoc.append("<pk_Checktype>").append("0004").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
			writexmldoc.append("<pk_Checkvalue>").append("").append("</pk_Checkvalue>"); // 辅助核算值 档案转换
			writexmldoc.append("</item>");

			// 部门
			writexmldoc.append("<item>");
			writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
			writexmldoc.append("<pk_Checkvalue>").append(bumengname).append("</pk_Checkvalue>");
			writexmldoc.append("</item>");

			writexmldoc.append("</ass>");
			if (StringUtils.isBlank(zengzhishui)) {
				SAVELOG("增值税为空:" + zengzhishui);
			}

			if (StringUtils.isNoBlank(zengzhishui)) {
				writexmldoc.append("</item>");
				// 借方分录 分录2
				writexmldoc.append("<item>");
				writexmldoc.append("<detailindex>").append("2").append("</detailindex>"); // 分录号
				writexmldoc.append("<explanation>").append(zhaiyao).append("</explanation>"); // 摘要 非空
				writexmldoc.append("<verifydate>").append("").append("</verifydate>"); // 业务日期, 可空
				writexmldoc.append("<price>").append("0").append("</price>");// 单价 可空
				writexmldoc.append("<excrate2>").append("").append("</excrate2>"); // 折本汇率 可空
				writexmldoc.append("<debitquantity>").append("").append("</debitquantity>"); // 借方数量 可空
				writexmldoc.append("<debitamount>").append(zengzhishui).append("</debitamount>"); // 原币借方金额 非空
				writexmldoc.append("<localdebitamount>").append(zengzhishui).append("</localdebitamount>"); // 本币借方金额 非空
				writexmldoc.append("<groupdebitamount>").append(zengzhishui).append("</groupdebitamount>"); // 集团本币借方金额
																											// 非空
				writexmldoc.append("<globaldebitamount>").append(zengzhishui).append("</globaldebitamount>"); // 全局本币借方金额
																												// 非空
				writexmldoc.append("<pk_currtype>").append("CNY").append("</pk_currtype>"); // 币种 非空
				writexmldoc.append("<pk_accasoa>").append("2221010101").append("</pk_accasoa>"); // 科目 非空
				writexmldoc.append("<pk_unit>").append("</pk_unit>");// 所属二级核算单位
				writexmldoc.append("<pk_unit_v>").append("</pk_unit_v>"); // 所属二级核算单位 版本可空
				
			
				// 辅助核算
				writexmldoc.append("<ass>");

				if (StringUtils.isNoBlank(xiangmubianhao)) {
					// 客商
					writexmldoc.append("<item>");
					writexmldoc.append("<pk_Checktype>").append("0004").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
					writexmldoc.append("<pk_Checkvalue>").append("00111538").append("</pk_Checkvalue>"); // 辅助核算值 档案转换
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

			}

			// <!-- 欧盟vat导入 -->
			writexmldoc.append("<vatdetail><businesscode></businesscode>"); // 交易代码 最大长度为64,类型为:String
			writexmldoc.append("<pk_receivecountry>").append("</pk_receivecountry>"); // 收货国家,最大长度为64,类型为:String
			writexmldoc.append("<pk_suppliervatcode>").append("</pk_suppliervatcode>"); // 供应商VAT码,最大长度为64 String
			writexmldoc.append("<pk_taxcode>").append("</pk_taxcode>"); // 税码,最大长度为64,类型为:String-
			writexmldoc.append("<pk_clientvatcode>").append("</pk_clientvatcode>"); // 客户VAT码,最大长度为64,类型为:String-
			writexmldoc.append("<direction>").append("</direction>"); // 方向,最大长度为64,类型为:String-->
			writexmldoc.append("<moneyamount>").append("</moneyamount>"); // 税额,最大长度为64,类型为:Double
			writexmldoc.append("<pk_vatcountry>").append("</pk_vatcountry>"); // 报税国家,最大长度为64,类型为:String
			writexmldoc.append("<taxamount>").append("</taxamount>"); // 税额,最大长度为64,类型为:Double
			writexmldoc.append("</vatdetail>");

			// 流

			writexmldoc.append("<cashFlow><item>");

			writexmldoc.append("<m_pk_currtype></m_pk_currtype>"); // 币种,最大长度为64,类型为:String
			writexmldoc.append("<money></money>"); // <!--原币,最大长度为64,类型为:Double-->
			writexmldoc.append("<moneyglobal></moneyglobal>"); // <!--全局本币,最大长度为64,类型为:Double-->
			writexmldoc.append("<moneygroup></moneygroup>"); // <!--集团本币,最大长度为64,类型为:Double-->
			writexmldoc.append("<moneymain></moneymain>"); // <!--本币,最大长度为64,类型为:Double-->
			writexmldoc.append("<pk_cashflow></pk_cashflow>"); // <!--现金主键,最大长度为64,类型为:String-->
			writexmldoc.append("<pk_innercorp></pk_innercorp>"); // <!--内部单位主键,最大长度为64,类型为:String-->
			writexmldoc.append("</item></cashFlow></item>");

			// <!-- 贷方分录 ------------------------------------------------------------------------------------------------   -->
			if ("true".equals(daifangflag)) {
				BigDecimal A =BigDecimal.ZERO;
				BigDecimal B =BigDecimal.ZERO;
				if(StringUtils.isNoBlank(chongjiekuanjine)){
		        	 A = new BigDecimal(fukuanjine);
		     		 B = new BigDecimal(chongjiekuanjine);
		     		
		     		
		        }
				
				if (chongjiekuanjine.equals("")||B.equals(BigDecimal.ZERO) && B.compareTo(A)==1) {
					// <!-- 贷方分录1 -->
					writexmldoc.append("<item>");
					writexmldoc.append("<creditquantity>").append("</creditquantity>");// <!-- 贷方数量 可空-->
					writexmldoc.append("<creditamount>").append(chongjiekuanjine).append("</creditamount>");// <!-- 原币贷方金额 非空-->
					writexmldoc.append("<localcreditamount>").append(chongjiekuanjine).append("</localcreditamount>");// <!--
																												// 本币贷方金额
																												// 非空-->
					writexmldoc.append("<groupcreditamount>").append(chongjiekuanjine).append("</groupcreditamount>");// <!--
																												// 集团本币贷方金额
																												// 非空-->
					writexmldoc.append("<globalcreditamount>").append(chongjiekuanjine).append("</globalcreditamount>");// <!--
																													// 全局本币贷方金额
																													// 非空-->
					writexmldoc.append("<detailindex>").append("1").append("</detailindex>");// <!-- 分录号 非空-->
					writexmldoc.append("<explanation>").append(zhaiyao).append("</explanation>");// <!-- 摘要 非空-->
					writexmldoc.append("<verifydate>").append("</verifydate>");// <!-- 业务日期 可空-->
					writexmldoc.append("<price>").append("0.00000000").append("</price>");// <!-- 单价 可空-->
					writexmldoc.append("<excrate2>").append("1").append("</excrate2>");// <!-- 折本汇率 可空-->
					writexmldoc.append("<pk_currtype>").append("CNY").append("</pk_currtype>");// <!-- 币种 非空-->
					writexmldoc.append("<pk_accasoa>").append("12210501").append("</pk_accasoa>");// <!-- 科目 非空-->
					writexmldoc.append("<pk_unit>").append("</pk_unit>");//// <!-- 所属二级核算单位 可空 （组织） -->
					writexmldoc.append("<pk_unit_v>").append("</pk_unit_v>");// <!-- 所属二级核算单位 版本可空 （组织） -->

					writexmldoc.append("<ass>");

					// 银行账户
					writexmldoc.append("<item>");
					writexmldoc.append("<pk_Checktype>").append("0011").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
					writexmldoc.append("<pk_Checkvalue>").append("023").append("</pk_Checkvalue>"); // 辅助核算值 档案转换
					writexmldoc.append("</item>");

					// 现金流量项目
					writexmldoc.append("<item>");
					writexmldoc.append("<pk_Checktype>").append("0007").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
					writexmldoc.append("<pk_Checkvalue>").append("CF100499").append("</pk_Checkvalue>"); // 辅助核算值 档案转换
					writexmldoc.append("</item>");

					// 部门
		   	          writexmldoc.append("<item>");
		   	          writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
		   	          writexmldoc.append("<pk_Checkvalue>").append(bumengname).append("</pk_Checkvalue>");
		   	          writexmldoc.append("</item>");

				//  人员档案
		   	          writexmldoc.append("<item>");
		   	          writexmldoc.append("<pk_Checktype>").append("0002").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
		   	          writexmldoc.append("<pk_Checkvalue>").append("").append("</pk_Checkvalue>");  // 辅助核算值 档案转换
		   	          writexmldoc.append("</item>");

					writexmldoc.append("</ass>");
				}else if (B.compareTo(A)==-1) {
					BigDecimal C = A.subtract(B);
					String km="";
					String yhzh="";
					if (StringUtils.isNoBlank(shoukuanrenzhuanghao)) {
						km="10120401";
						yhzh="023";
					}else {
						km="100201";
						yhzh="012";
					} 
					// <!-- 贷方分录2 -->
					writexmldoc.append("<item>");
					writexmldoc.append("<creditquantity>").append("</creditquantity>");// <!-- 贷方数量 可空-->
					writexmldoc.append("<creditamount>").append(C).append("</creditamount>");// <!-- 原币贷方金额 非空-->
					writexmldoc.append("<localcreditamount>").append(C).append("</localcreditamount>");// <!--
																												// 本币贷方金额
																												// 非空-->
					writexmldoc.append("<groupcreditamount>").append(C).append("</groupcreditamount>");// <!--
																												// 集团本币贷方金额
																												// 非空-->
					writexmldoc.append("<globalcreditamount>").append(C).append("</globalcreditamount>");// <!--
																													// 全局本币贷方金额
																													// 非空-->
					writexmldoc.append("<detailindex>").append("2").append("</detailindex>");// <!-- 分录号 非空-->
					writexmldoc.append("<explanation>").append(zhaiyao).append("</explanation>");// <!-- 摘要 非空-->
					writexmldoc.append("<verifydate>").append("</verifydate>");// <!-- 业务日期 可空-->
					writexmldoc.append("<price>").append("0.00000000").append("</price>");// <!-- 单价 可空-->
					writexmldoc.append("<excrate2>").append("1").append("</excrate2>");// <!-- 折本汇率 可空-->
					writexmldoc.append("<pk_currtype>").append("CNY").append("</pk_currtype>");// <!-- 币种 非空-->
					writexmldoc.append("<pk_accasoa>").append(km).append("</pk_accasoa>");// <!-- 科目 非空-->
					writexmldoc.append("<pk_unit>").append("</pk_unit>");//// <!-- 所属二级核算单位 可空 （组织） -->
					writexmldoc.append("<pk_unit_v>").append("</pk_unit_v>");// <!-- 所属二级核算单位 版本可空 （组织） -->

					writexmldoc.append("<ass>");

					// 银行账户
					writexmldoc.append("<item>");
					writexmldoc.append("<pk_Checktype>").append("0011").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
					writexmldoc.append("<pk_Checkvalue>").append(yhzh).append("</pk_Checkvalue>"); // 辅助核算值 档案转换
					writexmldoc.append("</item>");

					// 现金流量项目
					writexmldoc.append("<item>");
					writexmldoc.append("<pk_Checktype>").append("0007").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
					writexmldoc.append("<pk_Checkvalue>").append("CF100499").append("</pk_Checkvalue>"); // 辅助核算值 档案转换
					writexmldoc.append("</item>");

					// 客商
					writexmldoc.append("<item>");
					writexmldoc.append("<pk_Checktype>").append("0004").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
					writexmldoc.append("<pk_Checkvalue>").append("").append("</pk_Checkvalue>"); // 辅助核算值 档案转换
					writexmldoc.append("</item>");

					writexmldoc.append("</ass>");
				} {
					
				}
				
			}

			writexmldoc.append("<cashFlow><item>");
			writexmldoc.append("<m_pk_currtype>").append("</m_pk_currtype>"); // <!--币种,最大长度为64,类型为:String-->
			writexmldoc.append("<money>").append("</money>"); // <!--原币,最大长度为64,类型为:Double-->
			writexmldoc.append("<moneyglobal>").append("</moneyglobal>"); // <!--全局本币,最大长度为64,类型为:Double-->
			writexmldoc.append("<moneygroup>").append("</moneygroup>"); // <!--集团本币,最大长度为64,类型为:Double-->
			writexmldoc.append("<moneymain>").append("</moneymain>"); // <!--本币,最大长度为64,类型为:Double-->
			writexmldoc.append("<pk_cashflow>").append("</pk_cashflow>"); // <!--现金主键,最大长度为64,类型为:String-->
			writexmldoc.append("<pk_innercorp>").append("</pk_innercorp>"); // <!--内部单位主键,最大长度为64,类型为:String-->
			writexmldoc.append("</item></cashFlow></item></details></voucher_head></voucher></ufinterface>");
			String str = writexmldoc.toString();
			SAVELOG("推送凭证的xml:" + str);

			return str;

		}
		
		if ("其他费用".equals(baoxiaotype)) {
			
			
			
			String chongjiekuanjine = ObjectUtils.toString(d.get("冲借款金额"));
			if (StringUtils.isBlank(chongjiekuanjine)) {
				daifangflag = "false";
				SAVELOG("付款金额为空:" + fukuanjine);
			}else {
				daifangflag = "true";
			}
			
			String jiefangkemu = "";
		    jiefangkemu = jiefangkemu(xiangmubianhao, "54040306", "52010306","52010306",bumengname);
		    
			if (StringUtils.isBlank(jiefangkemu)) {
				return "借方科目为空";
			}
			// 借方分录 分录1
			writexmldoc.append("<item>");
			writexmldoc.append("<detailindex>").append("1").append("</detailindex>"); // 分录号
			writexmldoc.append("<explanation>").append(zhaiyao).append("</explanation>"); // 摘要 非空
			writexmldoc.append("<verifydate>").append("").append("</verifydate>"); // 业务日期, 可空
			writexmldoc.append("<price>").append("0").append("</price>");// 单价 可空
			writexmldoc.append("<excrate2>").append("").append("</excrate2>"); // 折本汇率 可空
			writexmldoc.append("<debitquantity>").append("").append("</debitquantity>"); // 借方数量 可空
			writexmldoc.append("<debitamount>").append(fukuanjine).append("</debitamount>"); // 原币借方金额 非空
			writexmldoc.append("<localdebitamount>").append(fukuanjine).append("</localdebitamount>"); // 本币借方金额 非空
			writexmldoc.append("<groupdebitamount>").append(fukuanjine).append("</groupdebitamount>"); // 集团本币借方金额 非空
			writexmldoc.append("<globaldebitamount>").append(fukuanjine).append("</globaldebitamount>"); // 全局本币借方金额 非空
			writexmldoc.append("<pk_currtype>").append("CNY").append("</pk_currtype>"); // 币种 非空
			writexmldoc.append("<pk_accasoa>").append(jiefangkemu).append("</pk_accasoa>"); // 科目 非空
			writexmldoc.append("<pk_unit>").append("</pk_unit>");// 所属二级核算单位
			writexmldoc.append("<pk_unit_v>").append("</pk_unit_v>"); // 所属二级核算单位 版本可空
			if(StringUtils.isNoBlank(xiangmubianhao)){
				String str  = jiefangkemu.substring(0,4);
				if(str.equals("5404")){
	        	 // 辅助核算  
	            writexmldoc.append("<ass>");
	            // 项目
	            writexmldoc.append("<item>");
	            writexmldoc.append("<pk_Checktype>").append("0010").append("</pk_Checktype>");
	            writexmldoc.append("<pk_Checkvalue>").append(xiangmubianhao).append("</pk_Checkvalue>");
	            writexmldoc.append("</item>");
	                // 部门
	                writexmldoc.append("<item>");
	                writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
	                writexmldoc.append("<pk_Checkvalue>").append(bumengname).append("</pk_Checkvalue>");
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
	            writexmldoc.append("<pk_Checkvalue>").append(xiangmubianhao).append("</pk_Checkvalue>");
	            writexmldoc.append("</item>");
	        	// 部门
	            writexmldoc.append("<item>");
	            writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
	            writexmldoc.append("<pk_Checkvalue>").append(bumengname).append("</pk_Checkvalue>");
	            writexmldoc.append("</item>");
	            writexmldoc.append("</ass>");
	        	}
	        }else{
	           	 // 辅助核算  
	               writexmldoc.append("<ass>");
	             
	                   // 部门
	                   writexmldoc.append("<item>");
	                   writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
	                   writexmldoc.append("<pk_Checkvalue>").append(bumengname).append("</pk_Checkvalue>");
	                   writexmldoc.append("</item>");
	                   //  客商
	               	writexmldoc.append("<item>");
	                   writexmldoc.append("<pk_Checktype>").append("0004").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
	                   writexmldoc.append("<pk_Checkvalue>").append("").append("</pk_Checkvalue>");  // 辅助核算值 档案转换
	                   writexmldoc.append("</item>");
	                  
	               writexmldoc.append("</ass>");
	        }
			if (StringUtils.isBlank(zengzhishui)) {
				SAVELOG("增值税为空:" + zengzhishui);
			}

			if (StringUtils.isNoBlank(zengzhishui)) {
				writexmldoc.append("</item>");
				// 借方分录 分录2
				writexmldoc.append("<item>");
				writexmldoc.append("<detailindex>").append("2").append("</detailindex>"); // 分录号
				writexmldoc.append("<explanation>").append(zhaiyao).append("</explanation>"); // 摘要 非空
				writexmldoc.append("<verifydate>").append("").append("</verifydate>"); // 业务日期, 可空
				writexmldoc.append("<price>").append("0").append("</price>");// 单价 可空
				writexmldoc.append("<excrate2>").append("").append("</excrate2>"); // 折本汇率 可空
				writexmldoc.append("<debitquantity>").append("").append("</debitquantity>"); // 借方数量 可空
				writexmldoc.append("<debitamount>").append(zengzhishui).append("</debitamount>"); // 原币借方金额 非空
				writexmldoc.append("<localdebitamount>").append(zengzhishui).append("</localdebitamount>"); // 本币借方金额 非空
				writexmldoc.append("<groupdebitamount>").append(zengzhishui).append("</groupdebitamount>"); // 集团本币借方金额
																											// 非空
				writexmldoc.append("<globaldebitamount>").append(zengzhishui).append("</globaldebitamount>"); // 全局本币借方金额
																												// 非空
				writexmldoc.append("<pk_currtype>").append("CNY").append("</pk_currtype>"); // 币种 非空
				writexmldoc.append("<pk_accasoa>").append("2221010101").append("</pk_accasoa>"); // 科目 非空
				writexmldoc.append("<pk_unit>").append("</pk_unit>");// 所属二级核算单位
				writexmldoc.append("<pk_unit_v>").append("</pk_unit_v>"); // 所属二级核算单位 版本可空
				
			
				// 辅助核算
				writexmldoc.append("<ass>");

				if (StringUtils.isNoBlank(xiangmubianhao)) {
					// 客商
					writexmldoc.append("<item>");
					writexmldoc.append("<pk_Checktype>").append("0004").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
					writexmldoc.append("<pk_Checkvalue>").append("00111538").append("</pk_Checkvalue>"); // 辅助核算值 档案转换
					writexmldoc.append("</item>");
					// 项目
					writexmldoc.append("<item>");
					writexmldoc.append("<pk_Checktype>").append("0010").append("</pk_Checktype>");
					writexmldoc.append("<pk_Checkvalue>").append(xiangmubianhao).append("</pk_Checkvalue>");
					writexmldoc.append("</item>");

					// 部门
					writexmldoc.append("<item>");
					writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
					writexmldoc.append("<pk_Checkvalue>").append(bumengname).append("</pk_Checkvalue>");
					writexmldoc.append("</item>");
				}else {
					// 客商
					writexmldoc.append("<item>");
					writexmldoc.append("<pk_Checktype>").append("0004").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
					writexmldoc.append("<pk_Checkvalue>").append("00111538").append("</pk_Checkvalue>"); // 辅助核算值 档案转换
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

			}

			// <!-- 欧盟vat导入 -->
			writexmldoc.append("<vatdetail><businesscode></businesscode>"); // 交易代码 最大长度为64,类型为:String
			writexmldoc.append("<pk_receivecountry>").append("</pk_receivecountry>"); // 收货国家,最大长度为64,类型为:String
			writexmldoc.append("<pk_suppliervatcode>").append("</pk_suppliervatcode>"); // 供应商VAT码,最大长度为64 String
			writexmldoc.append("<pk_taxcode>").append("</pk_taxcode>"); // 税码,最大长度为64,类型为:String-
			writexmldoc.append("<pk_clientvatcode>").append("</pk_clientvatcode>"); // 客户VAT码,最大长度为64,类型为:String-
			writexmldoc.append("<direction>").append("</direction>"); // 方向,最大长度为64,类型为:String-->
			writexmldoc.append("<moneyamount>").append("</moneyamount>"); // 税额,最大长度为64,类型为:Double
			writexmldoc.append("<pk_vatcountry>").append("</pk_vatcountry>"); // 报税国家,最大长度为64,类型为:String
			writexmldoc.append("<taxamount>").append("</taxamount>"); // 税额,最大长度为64,类型为:Double
			writexmldoc.append("</vatdetail>");

			// 流

			writexmldoc.append("<cashFlow><item>");

			writexmldoc.append("<m_pk_currtype></m_pk_currtype>"); // 币种,最大长度为64,类型为:String
			writexmldoc.append("<money></money>"); // <!--原币,最大长度为64,类型为:Double-->
			writexmldoc.append("<moneyglobal></moneyglobal>"); // <!--全局本币,最大长度为64,类型为:Double-->
			writexmldoc.append("<moneygroup></moneygroup>"); // <!--集团本币,最大长度为64,类型为:Double-->
			writexmldoc.append("<moneymain></moneymain>"); // <!--本币,最大长度为64,类型为:Double-->
			writexmldoc.append("<pk_cashflow></pk_cashflow>"); // <!--现金主键,最大长度为64,类型为:String-->
			writexmldoc.append("<pk_innercorp></pk_innercorp>"); // <!--内部单位主键,最大长度为64,类型为:String-->
			writexmldoc.append("</item></cashFlow></item>");

			// <!-- 贷方分录 ------------------------------------------------------------------------------------------------   -->
			if ("true".equals(daifangflag)) {
				BigDecimal A =BigDecimal.ZERO;
				BigDecimal B =BigDecimal.ZERO;
				if(StringUtils.isNoBlank(chongjiekuanjine)){
		        	 A = new BigDecimal(fukuanjine);
		     		 B = new BigDecimal(chongjiekuanjine);
		     		
		     		
		        }
				
				if (chongjiekuanjine.equals("")||B.equals(BigDecimal.ZERO) && B.compareTo(A)==1) {
					// <!-- 贷方分录1 -->
					writexmldoc.append("<item>");
					writexmldoc.append("<creditquantity>").append("</creditquantity>");// <!-- 贷方数量 可空-->
					writexmldoc.append("<creditamount>").append(chongjiekuanjine).append("</creditamount>");// <!-- 原币贷方金额 非空-->
					writexmldoc.append("<localcreditamount>").append(chongjiekuanjine).append("</localcreditamount>");// <!--
																												// 本币贷方金额
																												// 非空-->
					writexmldoc.append("<groupcreditamount>").append(chongjiekuanjine).append("</groupcreditamount>");// <!--
																												// 集团本币贷方金额
																												// 非空-->
					writexmldoc.append("<globalcreditamount>").append(chongjiekuanjine).append("</globalcreditamount>");// <!--
																													// 全局本币贷方金额
																													// 非空-->
					writexmldoc.append("<detailindex>").append("1").append("</detailindex>");// <!-- 分录号 非空-->
					writexmldoc.append("<explanation>").append(zhaiyao).append("</explanation>");// <!-- 摘要 非空-->
					writexmldoc.append("<verifydate>").append("</verifydate>");// <!-- 业务日期 可空-->
					writexmldoc.append("<price>").append("0.00000000").append("</price>");// <!-- 单价 可空-->
					writexmldoc.append("<excrate2>").append("1").append("</excrate2>");// <!-- 折本汇率 可空-->
					writexmldoc.append("<pk_currtype>").append("CNY").append("</pk_currtype>");// <!-- 币种 非空-->
					writexmldoc.append("<pk_accasoa>").append("12210501").append("</pk_accasoa>");// <!-- 科目 非空-->
					writexmldoc.append("<pk_unit>").append("</pk_unit>");//// <!-- 所属二级核算单位 可空 （组织） -->
					writexmldoc.append("<pk_unit_v>").append("</pk_unit_v>");// <!-- 所属二级核算单位 版本可空 （组织） -->

					writexmldoc.append("<ass>");

					// 银行账户
					writexmldoc.append("<item>");
					writexmldoc.append("<pk_Checktype>").append("0011").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
					writexmldoc.append("<pk_Checkvalue>").append("023").append("</pk_Checkvalue>"); // 辅助核算值 档案转换
					writexmldoc.append("</item>");

					// 现金流量项目
					writexmldoc.append("<item>");
					writexmldoc.append("<pk_Checktype>").append("0007").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
					writexmldoc.append("<pk_Checkvalue>").append("CF100499").append("</pk_Checkvalue>"); // 辅助核算值 档案转换
					writexmldoc.append("</item>");

					// 部门
		   	          writexmldoc.append("<item>");
		   	          writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
		   	          writexmldoc.append("<pk_Checkvalue>").append(bumengname).append("</pk_Checkvalue>");
		   	          writexmldoc.append("</item>");

				//  人员档案
		   	          writexmldoc.append("<item>");
		   	          writexmldoc.append("<pk_Checktype>").append("0002").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
		   	          writexmldoc.append("<pk_Checkvalue>").append("").append("</pk_Checkvalue>");  // 辅助核算值 档案转换
		   	          writexmldoc.append("</item>");

					writexmldoc.append("</ass>");
				}else if (B.compareTo(A)==-1) {
					BigDecimal C = A.subtract(B);
					String km="";
					String yhzh="";
					if (StringUtils.isNoBlank(shoukuanrenzhuanghao)) {
						km="10120401";
						yhzh="023";
					}else {
						km="100201";
						yhzh="012";
					} 
					// <!-- 贷方分录2 -->
					writexmldoc.append("<item>");
					writexmldoc.append("<creditquantity>").append("</creditquantity>");// <!-- 贷方数量 可空-->
					writexmldoc.append("<creditamount>").append(C).append("</creditamount>");// <!-- 原币贷方金额 非空-->
					writexmldoc.append("<localcreditamount>").append(C).append("</localcreditamount>");// <!--
																												// 本币贷方金额
																												// 非空-->
					writexmldoc.append("<groupcreditamount>").append(C).append("</groupcreditamount>");// <!--
																												// 集团本币贷方金额
																												// 非空-->
					writexmldoc.append("<globalcreditamount>").append(C).append("</globalcreditamount>");// <!--
																													// 全局本币贷方金额
																													// 非空-->
					writexmldoc.append("<detailindex>").append("2").append("</detailindex>");// <!-- 分录号 非空-->
					writexmldoc.append("<explanation>").append(zhaiyao).append("</explanation>");// <!-- 摘要 非空-->
					writexmldoc.append("<verifydate>").append("</verifydate>");// <!-- 业务日期 可空-->
					writexmldoc.append("<price>").append("0.00000000").append("</price>");// <!-- 单价 可空-->
					writexmldoc.append("<excrate2>").append("1").append("</excrate2>");// <!-- 折本汇率 可空-->
					writexmldoc.append("<pk_currtype>").append("CNY").append("</pk_currtype>");// <!-- 币种 非空-->
					writexmldoc.append("<pk_accasoa>").append(km).append("</pk_accasoa>");// <!-- 科目 非空-->
					writexmldoc.append("<pk_unit>").append("</pk_unit>");//// <!-- 所属二级核算单位 可空 （组织） -->
					writexmldoc.append("<pk_unit_v>").append("</pk_unit_v>");// <!-- 所属二级核算单位 版本可空 （组织） -->

					writexmldoc.append("<ass>");

					// 银行账户
					writexmldoc.append("<item>");
					writexmldoc.append("<pk_Checktype>").append("0011").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
					writexmldoc.append("<pk_Checkvalue>").append(yhzh).append("</pk_Checkvalue>"); // 辅助核算值 档案转换
					writexmldoc.append("</item>");

					// 现金流量项目
					writexmldoc.append("<item>");
					writexmldoc.append("<pk_Checktype>").append("0007").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
					writexmldoc.append("<pk_Checkvalue>").append("CF100499").append("</pk_Checkvalue>"); // 辅助核算值 档案转换
					writexmldoc.append("</item>");

					// 客商
					writexmldoc.append("<item>");
					writexmldoc.append("<pk_Checktype>").append("0004").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
					writexmldoc.append("<pk_Checkvalue>").append("").append("</pk_Checkvalue>"); // 辅助核算值 档案转换
					writexmldoc.append("</item>");

					writexmldoc.append("</ass>");
				} {
					
				}
				
			}

			writexmldoc.append("<cashFlow><item>");
			writexmldoc.append("<m_pk_currtype>").append("</m_pk_currtype>"); // <!--币种,最大长度为64,类型为:String-->
			writexmldoc.append("<money>").append("</money>"); // <!--原币,最大长度为64,类型为:Double-->
			writexmldoc.append("<moneyglobal>").append("</moneyglobal>"); // <!--全局本币,最大长度为64,类型为:Double-->
			writexmldoc.append("<moneygroup>").append("</moneygroup>"); // <!--集团本币,最大长度为64,类型为:Double-->
			writexmldoc.append("<moneymain>").append("</moneymain>"); // <!--本币,最大长度为64,类型为:Double-->
			writexmldoc.append("<pk_cashflow>").append("</pk_cashflow>"); // <!--现金主键,最大长度为64,类型为:String-->
			writexmldoc.append("<pk_innercorp>").append("</pk_innercorp>"); // <!--内部单位主键,最大长度为64,类型为:String-->
			writexmldoc.append("</item></cashFlow></item></details></voucher_head></voucher></ufinterface>");
			String str = writexmldoc.toString();
			SAVELOG("推送凭证的xml:" + str);

			return str;

		}
		
if ("服务费".equals(baoxiaotype)) {
			
			
			
			String chongjiekuanjine = ObjectUtils.toString(d.get("冲借款金额"));
			if (StringUtils.isBlank(chongjiekuanjine)) {
				daifangflag = "false";
				SAVELOG("付款金额为空:" + fukuanjine);
			}else {
				daifangflag = "true";
			}
			
			String jiefangkemu = "";
		    jiefangkemu = jiefangkemu(xiangmubianhao, "54040306", "52010306","52010306",bumengname);
			if (StringUtils.isBlank(jiefangkemu)) {
				return "借方科目为空";
			}
			// 借方分录 分录1
			writexmldoc.append("<item>");
			writexmldoc.append("<detailindex>").append("1").append("</detailindex>"); // 分录号
			writexmldoc.append("<explanation>").append(zhaiyao).append("</explanation>"); // 摘要 非空
			writexmldoc.append("<verifydate>").append("").append("</verifydate>"); // 业务日期, 可空
			writexmldoc.append("<price>").append("0").append("</price>");// 单价 可空
			writexmldoc.append("<excrate2>").append("").append("</excrate2>"); // 折本汇率 可空
			writexmldoc.append("<debitquantity>").append("").append("</debitquantity>"); // 借方数量 可空
			writexmldoc.append("<debitamount>").append(fukuanjine).append("</debitamount>"); // 原币借方金额 非空
			writexmldoc.append("<localdebitamount>").append(fukuanjine).append("</localdebitamount>"); // 本币借方金额 非空
			writexmldoc.append("<groupdebitamount>").append(fukuanjine).append("</groupdebitamount>"); // 集团本币借方金额 非空
			writexmldoc.append("<globaldebitamount>").append(fukuanjine).append("</globaldebitamount>"); // 全局本币借方金额 非空
			writexmldoc.append("<pk_currtype>").append("CNY").append("</pk_currtype>"); // 币种 非空
			writexmldoc.append("<pk_accasoa>").append(jiefangkemu).append("</pk_accasoa>"); // 科目 非空
			writexmldoc.append("<pk_unit>").append("</pk_unit>");// 所属二级核算单位
			writexmldoc.append("<pk_unit_v>").append("</pk_unit_v>"); // 所属二级核算单位 版本可空
			if(StringUtils.isNoBlank(xiangmubianhao)){
	        	if("54040306".equals(jiefangkemu)){
	        	 // 辅助核算  
	            writexmldoc.append("<ass>");
	            // 项目
	            writexmldoc.append("<item>");
	            writexmldoc.append("<pk_Checktype>").append("0010").append("</pk_Checktype>");
	            writexmldoc.append("<pk_Checkvalue>").append(xiangmubianhao).append("</pk_Checkvalue>");
	            writexmldoc.append("</item>");
	                // 部门
	                writexmldoc.append("<item>");
	                writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
	                writexmldoc.append("<pk_Checkvalue>").append(bumengname).append("</pk_Checkvalue>");
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
	            writexmldoc.append("<pk_Checkvalue>").append(xiangmubianhao).append("</pk_Checkvalue>");
	            writexmldoc.append("</item>");
	        	// 部门
	            writexmldoc.append("<item>");
	            writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
	            writexmldoc.append("<pk_Checkvalue>").append(bumengname).append("</pk_Checkvalue>");
	            writexmldoc.append("</item>");
	            writexmldoc.append("</ass>");
	        	}
	        }else{
	           	 // 辅助核算  
	               writexmldoc.append("<ass>");
	             
	                   // 部门
	                   writexmldoc.append("<item>");
	                   writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
	                   writexmldoc.append("<pk_Checkvalue>").append(bumengname).append("</pk_Checkvalue>");
	                   writexmldoc.append("</item>");
	                   //  客商
	               	writexmldoc.append("<item>");
	                   writexmldoc.append("<pk_Checktype>").append("0004").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
	                   writexmldoc.append("<pk_Checkvalue>").append("").append("</pk_Checkvalue>");  // 辅助核算值 档案转换
	                   writexmldoc.append("</item>");
	                  
	               writexmldoc.append("</ass>");
	        }
			if (StringUtils.isBlank(zengzhishui)) {
				SAVELOG("增值税为空:" + zengzhishui);
			}

			if (StringUtils.isNoBlank(zengzhishui)) {
				writexmldoc.append("</item>");
				// 借方分录 分录2
				writexmldoc.append("<item>");
				writexmldoc.append("<detailindex>").append("2").append("</detailindex>"); // 分录号
				writexmldoc.append("<explanation>").append(zhaiyao).append("</explanation>"); // 摘要 非空
				writexmldoc.append("<verifydate>").append("").append("</verifydate>"); // 业务日期, 可空
				writexmldoc.append("<price>").append("0").append("</price>");// 单价 可空
				writexmldoc.append("<excrate2>").append("").append("</excrate2>"); // 折本汇率 可空
				writexmldoc.append("<debitquantity>").append("").append("</debitquantity>"); // 借方数量 可空
				writexmldoc.append("<debitamount>").append(zengzhishui).append("</debitamount>"); // 原币借方金额 非空
				writexmldoc.append("<localdebitamount>").append(zengzhishui).append("</localdebitamount>"); // 本币借方金额 非空
				writexmldoc.append("<groupdebitamount>").append(zengzhishui).append("</groupdebitamount>"); // 集团本币借方金额
																											// 非空
				writexmldoc.append("<globaldebitamount>").append(zengzhishui).append("</globaldebitamount>"); // 全局本币借方金额
																												// 非空
				writexmldoc.append("<pk_currtype>").append("CNY").append("</pk_currtype>"); // 币种 非空
				writexmldoc.append("<pk_accasoa>").append("2221010101").append("</pk_accasoa>"); // 科目 非空
				writexmldoc.append("<pk_unit>").append("</pk_unit>");// 所属二级核算单位
				writexmldoc.append("<pk_unit_v>").append("</pk_unit_v>"); // 所属二级核算单位 版本可空
				
			
				// 辅助核算
				writexmldoc.append("<ass>");

				if (StringUtils.isNoBlank(xiangmubianhao)) {
					// 客商
					writexmldoc.append("<item>");
					writexmldoc.append("<pk_Checktype>").append("0004").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
					writexmldoc.append("<pk_Checkvalue>").append("00111538").append("</pk_Checkvalue>"); // 辅助核算值 档案转换
					writexmldoc.append("</item>");
					// 项目
					writexmldoc.append("<item>");
					writexmldoc.append("<pk_Checktype>").append("0010").append("</pk_Checktype>");
					writexmldoc.append("<pk_Checkvalue>").append(xiangmubianhao).append("</pk_Checkvalue>");
					writexmldoc.append("</item>");

					// 部门
					writexmldoc.append("<item>");
					writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
					writexmldoc.append("<pk_Checkvalue>").append(bumengname).append("</pk_Checkvalue>");
					writexmldoc.append("</item>");
				}else {
					// 客商
					writexmldoc.append("<item>");
					writexmldoc.append("<pk_Checktype>").append("0004").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
					writexmldoc.append("<pk_Checkvalue>").append("00111538").append("</pk_Checkvalue>"); // 辅助核算值 档案转换
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

			}

			// <!-- 欧盟vat导入 -->
			writexmldoc.append("<vatdetail><businesscode></businesscode>"); // 交易代码 最大长度为64,类型为:String
			writexmldoc.append("<pk_receivecountry>").append("</pk_receivecountry>"); // 收货国家,最大长度为64,类型为:String
			writexmldoc.append("<pk_suppliervatcode>").append("</pk_suppliervatcode>"); // 供应商VAT码,最大长度为64 String
			writexmldoc.append("<pk_taxcode>").append("</pk_taxcode>"); // 税码,最大长度为64,类型为:String-
			writexmldoc.append("<pk_clientvatcode>").append("</pk_clientvatcode>"); // 客户VAT码,最大长度为64,类型为:String-
			writexmldoc.append("<direction>").append("</direction>"); // 方向,最大长度为64,类型为:String-->
			writexmldoc.append("<moneyamount>").append("</moneyamount>"); // 税额,最大长度为64,类型为:Double
			writexmldoc.append("<pk_vatcountry>").append("</pk_vatcountry>"); // 报税国家,最大长度为64,类型为:String
			writexmldoc.append("<taxamount>").append("</taxamount>"); // 税额,最大长度为64,类型为:Double
			writexmldoc.append("</vatdetail>");

			// 流

			writexmldoc.append("<cashFlow><item>");

			writexmldoc.append("<m_pk_currtype></m_pk_currtype>"); // 币种,最大长度为64,类型为:String
			writexmldoc.append("<money></money>"); // <!--原币,最大长度为64,类型为:Double-->
			writexmldoc.append("<moneyglobal></moneyglobal>"); // <!--全局本币,最大长度为64,类型为:Double-->
			writexmldoc.append("<moneygroup></moneygroup>"); // <!--集团本币,最大长度为64,类型为:Double-->
			writexmldoc.append("<moneymain></moneymain>"); // <!--本币,最大长度为64,类型为:Double-->
			writexmldoc.append("<pk_cashflow></pk_cashflow>"); // <!--现金主键,最大长度为64,类型为:String-->
			writexmldoc.append("<pk_innercorp></pk_innercorp>"); // <!--内部单位主键,最大长度为64,类型为:String-->
			writexmldoc.append("</item></cashFlow></item>");

			// <!-- 贷方分录 ------------------------------------------------------------------------------------------------   -->
			if ("true".equals(daifangflag)) {
				BigDecimal A =BigDecimal.ZERO;
				BigDecimal B =BigDecimal.ZERO;
				if(StringUtils.isNoBlank(chongjiekuanjine)){
		        	 A = new BigDecimal(fukuanjine);
		     		 B = new BigDecimal(chongjiekuanjine);
		     		
		     		
		        }
				
				if (chongjiekuanjine.equals("")||B.equals(BigDecimal.ZERO) && B.compareTo(A)==1) {
					// <!-- 贷方分录1 -->
					writexmldoc.append("<item>");
					writexmldoc.append("<creditquantity>").append("</creditquantity>");// <!-- 贷方数量 可空-->
					writexmldoc.append("<creditamount>").append(chongjiekuanjine).append("</creditamount>");// <!-- 原币贷方金额 非空-->
					writexmldoc.append("<localcreditamount>").append(chongjiekuanjine).append("</localcreditamount>");// <!--
																												// 本币贷方金额
																												// 非空-->
					writexmldoc.append("<groupcreditamount>").append(chongjiekuanjine).append("</groupcreditamount>");// <!--
																												// 集团本币贷方金额
																												// 非空-->
					writexmldoc.append("<globalcreditamount>").append(chongjiekuanjine).append("</globalcreditamount>");// <!--
																													// 全局本币贷方金额
																													// 非空-->
					writexmldoc.append("<detailindex>").append("1").append("</detailindex>");// <!-- 分录号 非空-->
					writexmldoc.append("<explanation>").append(zhaiyao).append("</explanation>");// <!-- 摘要 非空-->
					writexmldoc.append("<verifydate>").append("</verifydate>");// <!-- 业务日期 可空-->
					writexmldoc.append("<price>").append("0.00000000").append("</price>");// <!-- 单价 可空-->
					writexmldoc.append("<excrate2>").append("1").append("</excrate2>");// <!-- 折本汇率 可空-->
					writexmldoc.append("<pk_currtype>").append("CNY").append("</pk_currtype>");// <!-- 币种 非空-->
					writexmldoc.append("<pk_accasoa>").append("12210501").append("</pk_accasoa>");// <!-- 科目 非空-->
					writexmldoc.append("<pk_unit>").append("</pk_unit>");//// <!-- 所属二级核算单位 可空 （组织） -->
					writexmldoc.append("<pk_unit_v>").append("</pk_unit_v>");// <!-- 所属二级核算单位 版本可空 （组织） -->

					writexmldoc.append("<ass>");

					// 银行账户
					writexmldoc.append("<item>");
					writexmldoc.append("<pk_Checktype>").append("0011").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
					writexmldoc.append("<pk_Checkvalue>").append("023").append("</pk_Checkvalue>"); // 辅助核算值 档案转换
					writexmldoc.append("</item>");

					// 现金流量项目
					writexmldoc.append("<item>");
					writexmldoc.append("<pk_Checktype>").append("0007").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
					writexmldoc.append("<pk_Checkvalue>").append("CF100499").append("</pk_Checkvalue>"); // 辅助核算值 档案转换
					writexmldoc.append("</item>");

					// 部门
		   	          writexmldoc.append("<item>");
		   	          writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
		   	          writexmldoc.append("<pk_Checkvalue>").append(bumengname).append("</pk_Checkvalue>");
		   	          writexmldoc.append("</item>");

				//  人员档案
		   	          writexmldoc.append("<item>");
		   	          writexmldoc.append("<pk_Checktype>").append("0002").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
		   	          writexmldoc.append("<pk_Checkvalue>").append("").append("</pk_Checkvalue>");  // 辅助核算值 档案转换
		   	          writexmldoc.append("</item>");

					writexmldoc.append("</ass>");
				}else if (B.compareTo(A)==-1) {
					BigDecimal C = A.subtract(B);
					String km="";
					String yhzh="";
					if (StringUtils.isNoBlank(shoukuanrenzhuanghao)) {
						km="10120401";
						yhzh="023";
					}else {
						km="100201";
						yhzh="012";
					} 
					// <!-- 贷方分录2 -->
					writexmldoc.append("<item>");
					writexmldoc.append("<creditquantity>").append("</creditquantity>");// <!-- 贷方数量 可空-->
					writexmldoc.append("<creditamount>").append(C).append("</creditamount>");// <!-- 原币贷方金额 非空-->
					writexmldoc.append("<localcreditamount>").append(C).append("</localcreditamount>");// <!--
																												// 本币贷方金额
																												// 非空-->
					writexmldoc.append("<groupcreditamount>").append(C).append("</groupcreditamount>");// <!--
																												// 集团本币贷方金额
																												// 非空-->
					writexmldoc.append("<globalcreditamount>").append(C).append("</globalcreditamount>");// <!--
																													// 全局本币贷方金额
																													// 非空-->
					writexmldoc.append("<detailindex>").append("2").append("</detailindex>");// <!-- 分录号 非空-->
					writexmldoc.append("<explanation>").append(zhaiyao).append("</explanation>");// <!-- 摘要 非空-->
					writexmldoc.append("<verifydate>").append("</verifydate>");// <!-- 业务日期 可空-->
					writexmldoc.append("<price>").append("0.00000000").append("</price>");// <!-- 单价 可空-->
					writexmldoc.append("<excrate2>").append("1").append("</excrate2>");// <!-- 折本汇率 可空-->
					writexmldoc.append("<pk_currtype>").append("CNY").append("</pk_currtype>");// <!-- 币种 非空-->
					writexmldoc.append("<pk_accasoa>").append(km).append("</pk_accasoa>");// <!-- 科目 非空-->
					writexmldoc.append("<pk_unit>").append("</pk_unit>");//// <!-- 所属二级核算单位 可空 （组织） -->
					writexmldoc.append("<pk_unit_v>").append("</pk_unit_v>");// <!-- 所属二级核算单位 版本可空 （组织） -->

					writexmldoc.append("<ass>");

					// 银行账户
					writexmldoc.append("<item>");
					writexmldoc.append("<pk_Checktype>").append("0011").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
					writexmldoc.append("<pk_Checkvalue>").append(yhzh).append("</pk_Checkvalue>"); // 辅助核算值 档案转换
					writexmldoc.append("</item>");

					// 现金流量项目
					writexmldoc.append("<item>");
					writexmldoc.append("<pk_Checktype>").append("0007").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
					writexmldoc.append("<pk_Checkvalue>").append("CF100499").append("</pk_Checkvalue>"); // 辅助核算值 档案转换
					writexmldoc.append("</item>");

					// 客商
					writexmldoc.append("<item>");
					writexmldoc.append("<pk_Checktype>").append("0004").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
					writexmldoc.append("<pk_Checkvalue>").append("").append("</pk_Checkvalue>"); // 辅助核算值 档案转换
					writexmldoc.append("</item>");

					writexmldoc.append("</ass>");
				} {
					
				}
				
			}

			writexmldoc.append("<cashFlow><item>");
			writexmldoc.append("<m_pk_currtype>").append("</m_pk_currtype>"); // <!--币种,最大长度为64,类型为:String-->
			writexmldoc.append("<money>").append("</money>"); // <!--原币,最大长度为64,类型为:Double-->
			writexmldoc.append("<moneyglobal>").append("</moneyglobal>"); // <!--全局本币,最大长度为64,类型为:Double-->
			writexmldoc.append("<moneygroup>").append("</moneygroup>"); // <!--集团本币,最大长度为64,类型为:Double-->
			writexmldoc.append("<moneymain>").append("</moneymain>"); // <!--本币,最大长度为64,类型为:Double-->
			writexmldoc.append("<pk_cashflow>").append("</pk_cashflow>"); // <!--现金主键,最大长度为64,类型为:String-->
			writexmldoc.append("<pk_innercorp>").append("</pk_innercorp>"); // <!--内部单位主键,最大长度为64,类型为:String-->
			writexmldoc.append("</item></cashFlow></item></details></voucher_head></voucher></ufinterface>");
			String str = writexmldoc.toString();
			SAVELOG("推送凭证的xml:" + str);

			return str;

		}
		return "";

		

	}
	 /**
	  * 
	  * 服务费，
     * 项目号结尾是否包含J00。C00
     * @param xiangmucode
     * @return
     */
	/*
	 * 有项目号，项目号结尾是J00的，根据发票种类财务人员选择具体科目， 项目号以非S开头且结尾是C00的，根据发票种类财务人员选择具体科目，
	 * 项目号以S开头且结尾是C00为5101开头的科目。
	 * 5404开头的科目的辅助核算为项目、部门、客商（一次性/00111538）、合同号（空值），5201和5101开头的科目辅助核算为项目、部门，
	 * 根据项目号提取。
	 */

    public static String jiefangkemu(String xiangmuname,String joo,String coo,String soo,String deptname){
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
    	else {
    		if (StringUtils.isBlank(xiangmuname)) {
    			// '科技部','市场部','用户办',
    			// '财务部','综合部','人力党群部','法律审计部','信息中心'
    			if ("科技工作部".equals(deptname) || "市场开发部".equals(deptname)|| "用户办".equals(deptname)) {
    				jiefangkemu ="660124";
    			} else if ("资产财务部".equals(deptname) || "党委办公室、综合管理部".equals(deptname) || "党委组织部、人力资源部部".equals(deptname)
    					|| "法律审计部".equals(deptname) || "信息中心".equals(deptname)) {
    				jiefangkemu = "660224";

    			} else {
    				SAVELOG(deptname + "-该部门不能生成凭证");
    			}

    		}
		}
		return jiefangkemu;
    }

	/**
	 * 借方科目
	 * 
	 * @param xiangmucode
	 * @return
	 */
	public String guanggaojiefangkemu(String xiangmuname, String deptname, String bmkemu1, String bmkemu2) {
		String jiefangkemu = "";
		if (StringUtils.isBlank(xiangmuname)) {
			// '科技部','市场部','用户办',
			// '财务部','综合部','人力党群部','法律审计部','信息中心'
			if ("科技工作部".equals(deptname) || "市场开发部".equals(deptname)|| "用户办".equals(deptname)) {
				jiefangkemu = bmkemu1;
			} else if ("资产财务部".equals(deptname) || "党委办公室、综合管理部".equals(deptname) || "党委组织部、人力资源部部".equals(deptname)
					|| "法律审计部".equals(deptname) || "信息中心".equals(deptname)) {
				jiefangkemu = bmkemu2;

			} else {
				SAVELOG(deptname + "-该部门不能生成凭证");
			}

		}
		return jiefangkemu;
	}
}
