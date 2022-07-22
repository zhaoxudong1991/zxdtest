package com.seeyon.apps.ncclistencetwo.flow;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.aspose.imaging.internal.e.D;
import com.seeyon.ctp.util.StringUtil;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seeyon.apps.collaboration.event.CollaborationProcessEvent;
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

public class CaiWuShuJuZhanSWorkFlowEvent extends AbstractWorkflowEvent{
	private static final Log log = LogFactory.getLog(GuDingZiChanFkdFromWorkerFlowEvent.class);
	private static String caiwushujuzhans = AppContext.getSystemProperty("Information.ncctwo.caiwushujuzhans");
	private static String caiwushujuzhansresult = AppContext.getSystemProperty("Information.ncctwo.caiwushujuzhansresult");
    
	private FormManager formManager;

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
        return "caiwushujuzhans";
    }

    @Override
    public String getLabel() {
        // TODO Auto-generated method stub
        return "财务数据展示表";
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

    //发起前事件
    @Override
    public WorkflowEventResult onBeforeStart(WorkflowEventData data) {
        Map<String, Object> d = data.getBusinessData();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = simpleDateFormat.format(new Date());
        String year = format.substring(0, 4);
        String month = format.substring(5, 7);

        String projectName = ObjectUtils.toString(d.get("项目名称"));
        String projectCode = ObjectUtils.toString(d.get("项目编码"));
        String keMuName = ObjectUtils.toString(d.get("科目名称"));
        String keMuCode = ObjectUtils.toString(d.get("科目编码"));
        String keShangCode = ObjectUtils.toString(d.get("客商编码"));


        if (StringUtils.isNotBlank(projectCode) && StringUtils.isBlank(keMuCode)) {
            //调用辅助明细接口
            String result = SendVoucher.getassdetail(year, month, "", "0004", keShangCode, "0010", projectCode, "", "");
            Map parse = (Map) JSON.parse(result);
        }

        if (StringUtils.isNotBlank(keMuCode)&& StringUtils.isBlank(projectCode)) {
            //调用科目余额接口
            String result = SendVoucher.getaccountbalance(year,month,keMuCode);
            Map parse = (Map) JSON.parse(result);
        }

        if (StringUtils.isNotBlank(projectCode) &&  StringUtils.isNotBlank(keMuCode)) {
            //调用辅助明细接口
            String result = SendVoucher.getassdetail(year, month, keMuCode, "0004", keShangCode, "0010", projectCode, "", "");
            Map parse = (Map) JSON.parse(result);
        }
        return new WorkflowEventResult();
    }



    //发起事件
    @Override
    public void onStart(WorkflowEventData data) {
        System.out.println("---发起事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:" + data.getSummaryId() + "_OperationName");
    }


    //处理事件
    @Override
    public void onFinishWorkitem(WorkflowEventData data) {
        System.out.println("---处理事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:" + data.getSummaryId() + "_OperationName:");
    }


    //终止前事件
    @Override
    public WorkflowEventResult onBeforeStop(WorkflowEventData data) {
        System.out.println("---终止前事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:" + data.getSummaryId() + "_OperationName");
        return null;
    }

    //终止事件
    @Override
    public void onStop(WorkflowEventData data) {
        System.out.println("---终止事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:" + data.getSummaryId() + "_OperationName");
    }

    //回退前事件
    @Override
    public WorkflowEventResult onBeforeStepBack(WorkflowEventData data) {
        System.out.println("---回退前事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:" + data.getSummaryId() + "_OperationName");
        return null;
    }

    //回退事件
    @Override
    public void onStepBack(WorkflowEventData data) {
        System.out.println("---回退事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:" + data.getSummaryId() + "_OperationName");
    }

    //撤销前事件
    @Override
    public WorkflowEventResult onBeforeCancel(WorkflowEventData data) {
        System.out.println("---撤销前事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:" + data.getSummaryId() + "_OperationName");
        return null;
    }

    //撤销事件
    @Override
    public void onCancel(WorkflowEventData data) {
        System.out.println("---撤销事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:" + data.getSummaryId() + "_OperationName");
    }

    //取回前事件
    @Override
    public WorkflowEventResult onBeforeTakeBack(WorkflowEventData data) {
        System.out.println("---取回前事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:" + data.getSummaryId() + "_OperationName");
        return null;
    }

    //取回事件
    @Override
    public void onTakeBack(WorkflowEventData data) {
        System.out.println("---取回事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:" + data.getSummaryId() + "_OperationName");
    }

    //结束事件
    @Override
    public void onProcessFinished(WorkflowEventData data) {
    	log.info("---财务数据展示表单---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:"
				+ data.getSummaryId() + "formViewOperation" + data.getFormViewOperation());
    }
    //输出日志
    public void SAVELOG(String content) {
    	try {
			LogInfo.testMemberFile("GuDingZiChanFkdFromWorkerFlowEvent:"+content+";\r\n");
		} catch (IOException e) {
			e.printStackTrace();
			log.error("GuDingZiChanFkdFromWorkerFlowEvent添加日志失败："+e.getMessage());
		}
    }
}