package com.seeyon.apps.ncclistencetwo.flow;

import com.alibaba.fastjson.JSON;
import com.seeyon.apps.ncclistencetwo.utils.LogInfo;
import com.seeyon.apps.ncclistencetwo.utils.SendVoucher;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.constants.ApplicationCategoryEnum;
import com.seeyon.ctp.common.ctpenumnew.manager.EnumManager;
import com.seeyon.ctp.common.po.ctpenumnew.CtpEnumItem;
import com.seeyon.ctp.form.service.FormManager;
import com.seeyon.ctp.workflow.event.AbstractWorkflowEvent;
import com.seeyon.ctp.workflow.event.WorkflowEventData;
import com.seeyon.ctp.workflow.event.WorkflowEventResult;
import com.seeyon.ctp.workflow.util.WorkflowEventConstants.WorkflowEventType;

import mjson.Json;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.songjian.utils.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @author 徐凤年
 */
public class YuSuanFashengeWorkFlowEvent extends AbstractWorkflowEvent {

	private static final Log log = LogFactory.getLog(YuSuanFashengeWorkFlowEvent.class);
	private FormManager formManager;

	public FormManager getFormManager() {
		return formManager;
	}

	@Override
	public ApplicationCategoryEnum getAppName() {
		return ApplicationCategoryEnum.form;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return "yuefashengbiao";
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return "预算发生额表单";
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
		Map<String, Object> d = data.getBusinessData();
		WorkflowEventResult workflowEventResult = new WorkflowEventResult();
		String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		String year = format.substring(0, 4);
		String month = format.substring(5, 7);

		String kemubianma = ObjectUtils.toString(d.get("科目编码"));

		String result = SendVoucher.getaccountbalance(year, month, kemubianma);

		// TODO
		Map parse = (Map) JSON.parse(result);
		for (Object key : parse.keySet()) {
			System.out.println("Key=" + key + "Value= " + parse.get(key));
		}
		return workflowEventResult;
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

	// 结束事件
	public void onProcessFinished(WorkflowEventData data) {
		System.out.println("---结束事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:"
				+ data.getSummaryId() + "_OperationName");

	}

}