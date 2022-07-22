package com.seeyon.apps.ncclistencetwo.flow;

import com.seeyon.apps.collaboration.event.CollaborationProcessEvent;
import com.seeyon.apps.ncclistencetwo.utils.GetPinyin;
import com.seeyon.apps.ncclistencetwo.utils.JdbcUtil;
import com.seeyon.apps.ncclistencetwo.utils.LogInfo;
import com.seeyon.apps.ncclistencetwo.utils.SendVoucher;
import com.seeyon.apps.ncclistencetwo.utils.BxUtils;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.constants.ApplicationCategoryEnum;
import com.seeyon.ctp.common.ctpenumnew.manager.EnumManager;
import com.seeyon.ctp.common.po.ctpenumnew.CtpEnumItem;
import com.seeyon.ctp.form.service.FormManager;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.util.annotation.ListenEvent;
import com.seeyon.ctp.workflow.event.AbstractWorkflowEvent;
import com.seeyon.ctp.workflow.event.WorkflowEventData;
import com.seeyon.ctp.workflow.event.WorkflowEventResult;
import com.seeyon.ctp.workflow.util.WorkflowEventConstants.WorkflowEventType;

import jodd.util.StringUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.songjian.utils.StringUtils;

public class BaoxiaodanFormWorkflowEvent extends AbstractWorkflowEvent {
	private static final Log log = LogFactory.getLog(BaoxiaodanFormWorkflowEvent.class);
	private static EnumManager enumManager = (EnumManager) AppContext.getBean("enumManagerNew");
	private static String baoxiaotwo = AppContext.getSystemProperty("Information.ncctwo.baoxiaotwo");
	private static String baoxiaotworesult = AppContext.getSystemProperty("Information.ncctwo.baoxiaotworesult");
	
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
        return "baoxiaotwo";
    }

    @Override
    public String getLabel() {
        // TODO Auto-generated method stub
        return "报销单凭证-2022";
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
        WorkflowEventResult result = new WorkflowEventResult();
        result.setAlertMessage("事件处理完成");
        System.out.println("---发起前事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:" + data.getSummaryId() + "_OperationName" + "====" + data.getBusinessData().get(""));


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

    //发起事件
    @Override
    public void onStart(WorkflowEventData data) {
        System.out.println("---发起事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:" + data.getSummaryId() + "_OperationName");
    }

    //处理前事件

    @Override
    @ListenEvent(event = CollaborationProcessEvent.class, async = true)
    public WorkflowEventResult onBeforeFinishWorkitem(WorkflowEventData data) {
       
    	SAVELOG("报销单的处理前事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:" + data.getSummaryId());

        formManager = (FormManager) AppContext.getBean("formManager");
        WorkflowEventResult workflowEventResult = new WorkflowEventResult();
       
        Map<String, Object> d = data.getBusinessData();
        String baoxiaotype = "";
		String baoxiaotypeid =ObjectUtils.toString(d.get("报销类型"));
    	if(StringUtils.isBlank(baoxiaotypeid)){
        	SAVELOG("报销类型为空id:"+baoxiaotypeid);
        }else{
        	CtpEnumItem ctpEnumItemSec = enumManager.getEnumItem(Long.valueOf(baoxiaotypeid));
        	baoxiaotype= ctpEnumItemSec.getShowvalue().toString();
        }
    	String bumenname = ObjectUtils.toString(d.get("部门编号"));
    	if(StringUtils.isBlank(bumenname)){
    		SAVELOG("部门编号为空:"+bumenname);
    		workflowEventResult.setAlertMessage("请选择部门编号");
        	return workflowEventResult;
        }
    	String newresult = null;
    	String str=null;
    	if("服务费".equals(baoxiaotype)){
        	//计提凭证返回值
    		String fwfresult="";
        	String fwfjtuseinterface = BxUtils.fwfjtuseinterface(data);
            SAVELOG("服务费凭证-调用凭证推送接口返回值："+fwfjtuseinterface);
           
            String xmlmsg = fwfjtuseinterface;
            if (fwfjtuseinterface.contains("处理完毕")) {
            	fwfresult = "生成服务费凭证";
            } else {
            	fwfresult = "服务费未凭证生成";
                workflowEventResult.setAlertMessage(fwfresult);
            	return workflowEventResult;
            }
            
           
            if (xmlmsg.contains("异常信息:") || "服务费凭证未生成".equals(fwfresult) || xmlmsg.contains("Connection timed out")) {
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
            str=fwfresult;
    	}else if("固定资产购置".equals(baoxiaotype)){
    		String gdzcresult="";
    		//固定资产购置凭证返回值
        	String gdzcjtuseinterface = BxUtils.gdzcjtuseinterface(data);
            SAVELOG("固定资产购置-调用凭证推送接口返回值："+gdzcjtuseinterface);
            String xmlmsg = gdzcjtuseinterface;
            if (gdzcjtuseinterface.contains("处理完毕")) {
            	gdzcresult = "生成固定资产购置凭证";
            } else {
            	gdzcresult = "固定资产购置凭证未生成";
                workflowEventResult.setAlertMessage(gdzcresult);
            	return workflowEventResult;
            }
            
           
            if (xmlmsg.contains("异常信息:") || "固定资产购置凭证未生成".equals(gdzcresult) || xmlmsg.contains("Connection timed out")) {
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
        	
            str=gdzcresult;
    	}else if("其它费用".equals(baoxiaotype)){
    		String qitaresult="";
		//广告宣传费购置凭证返回值
    	String qitatuseinterface = BxUtils.qitatuseinterface(data);
        SAVELOG("其它费用-调用凭证推送接口返回值："+qitatuseinterface);
        String xmlmsg = qitatuseinterface;
        if (qitatuseinterface.contains("处理完毕")) {
        	qitaresult = "生成其它费用凭证";
        } else {
        	qitaresult = "其它费用凭证未生成";
            workflowEventResult.setAlertMessage(qitaresult);
        	return workflowEventResult;
        }
        
       
        if (xmlmsg.contains("异常信息:") || "其它费用凭证未生成".equals(qitaresult) || xmlmsg.contains("Connection timed out")) {
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
    	
        str=qitaresult;
        }else if("广告宣传费".equals(baoxiaotype)){
    		String ggxcresult="";
		//广告宣传费购置凭证返回值
    	String ggxcjtuseinterface = BxUtils.ggxcjtuseinterface(data);
        SAVELOG("广告宣传费-调用凭证推送接口返回值："+ggxcjtuseinterface);
        String xmlmsg = ggxcjtuseinterface;
        if (ggxcjtuseinterface.contains("处理完毕")) {
        	ggxcresult = "生成广告宣传费凭证";
        } else {
        	ggxcresult = "广告宣传费凭证未生成";
            workflowEventResult.setAlertMessage(ggxcresult);
        	return workflowEventResult;
        }
        
       
        if (xmlmsg.contains("异常信息:") || "广告宣传费凭证未生成".equals(ggxcresult) || xmlmsg.contains("Connection timed out")) {
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
    	
        str=ggxcresult;
        }
    		
    	SAVELOG("凭证推送结果str："+str);
    	//修改表单数据
        Integer result = changeform(data, str);
        SAVELOG("凭证推送结果回填表单："+result);
        if(result<1){
			SAVELOG("回填凭证结果字段修改失败:"+result);
			workflowEventResult.setAlertMessage("回填凭证结果字段修改失败:"+result);
		}

        return workflowEventResult;
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
        System.out.println("---结束事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:" + data.getSummaryId() + "_OperationName");
    }
    //输出日志
    public void SAVELOG(String content) {
    	try {
			LogInfo.testMemberFile("BaoxiaodanFormWorkflowEvent:"+content+";\r\n");
		} catch (IOException e) {
			e.printStackTrace();
			log.error("BaoxiaodanFormWorkflowEvent添加日志失败："+e.getMessage());
		}
    }
    /**
     * 回填表单凭证结果字段
     * @param data
     * @param auto9
     * @param agreeAndNosend
     * @param content
     * @return
     */
	public Integer changeform(WorkflowEventData data,  String content) {
        
		Integer result = 0;
        String FORM_RECORDID = "";
        long summaryId = data.getSummaryId();

        //修改表单上的信息
        try {
            // 查询  form_recordid
            List<String> params = new ArrayList<String>();
            params.add(summaryId+"");
            FORM_RECORDID = JdbcUtil.jdbcselectone("select form_recordid,id from col_summary where id = ?"
            		, params, "form_recordid");
            if(!StringUtils.isNoBlank(FORM_RECORDID)){
            	SAVELOG("查询  form_recordid失败:"+FORM_RECORDID);
            }
            
            //  回填凭证结果字段
            List<String> params2 = new ArrayList<String>();
            params2.add(content);
            params2.add(FORM_RECORDID);
            String updatesql = "update "+baoxiaotwo+" set "+baoxiaotworesult+" = ? where id = ?";
            result = JdbcUtil.aud(updatesql, params2);
            if(result<1){
				SAVELOG("回填凭证结果字段修改失败:"+result);
			}

        } catch (Exception e) {
            e.printStackTrace();
            SAVELOG("修改表单上的信息:回填凭证结果字段修改失败:"+e.getMessage());
        }
        return result;
    }
   
}
