package com.seeyon.apps.ncclistencetwo.flow;

import com.alibaba.fastjson.JSON;
import com.seeyon.apps.collaboration.event.CollaborationProcessEvent;
import com.seeyon.apps.ncclistencetwo.utils.SendVoucher;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.constants.ApplicationCategoryEnum;
import com.seeyon.ctp.form.service.FormManager;
import com.seeyon.ctp.util.annotation.ListenEvent;
import com.seeyon.ctp.workflow.event.AbstractWorkflowEvent;
import com.seeyon.ctp.workflow.event.WorkflowEventData;
import com.seeyon.ctp.workflow.event.WorkflowEventResult;
import com.seeyon.ctp.workflow.util.WorkflowEventConstants.WorkflowEventType;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class NeiBuTiChengWorkFlowEvent extends AbstractWorkflowEvent {
	private static final Log log = LogFactory.getLog(GuDingZiChanFkdFromWorkerFlowEvent.class);
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
		return "neibuticheng";
	}

	@Override
	public String getLabel() {
		return "内部提成基础数据表单";
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

		String xiangMuBianMa = ObjectUtils.toString(d.get("项目编码"));
		String keShangCode = ObjectUtils.toString(d.get("客商编码"));
		String buMenCode = ObjectUtils.toString(d.get("负责部门编码"));
		xiangMuBianMa.replace("[", "").replace("]", "");
		String[] xiangMuCodes = xiangMuBianMa.split(",");

		for (String xiangMuCode : xiangMuCodes) {
			System.out.println(xiangMuCode);
			// String result = SendVoucher.getassdetail(year, month,
			// "2203","0004",keShangCode,"0010",xiangMuCode,"0001",buMenCode,"QYZY0025","1");

			// Map parse = (Map) JSON.parse(result);

		}

		// 资料费
		Integer zl[] = { 51010203, 52010203, 54040203 };
		// 会议费
		Integer hy[] = { 51010204, 52010204, 54040204 };
		// 差旅费
		Integer cl[] = { 51010301, 52010301, 54040301 };
		// 出国差旅费
		Integer cgcl[] = { 51010305, 52010305, 54040305 };
		// 经营费
		Integer jy[] = { 51010302, 52010302, 54040302 };
		// 邮电办公费
		Integer ydbg[] = { 51010201, 51010202, 52010201, 54040201, 52010202, 54040202 };
		// 水电费
		Integer sd[] = { 510104, 520104, 540404 };
		// 劳务费
		Integer lw[] = { 51010110, 52010110, 54040110 };
		// 外购材料费
		Integer wgcl[] = { 51010501, 51010504, 52010501, 52010504, 54040501, 54040504 };
		// 内其他
		Integer nqt[] = { 51010206, 51010207, 51010208, 51010303, 51010304, 51010307, 51010308, 52010206, 52010207,
				52010208, 52010303, 52010304, 52010307, 52010308, 54040206, 54040207, 54040208, 54040303, 54040304,
				54040307, 54040308 };
		// 运费
		Integer yf[] = { 51010505, 52010505, 54040505 };
		// 专利投标
		Integer zltb[] = { 51010306, 52010306, 54040306 };
		// 外其他
		Integer wqt[] = { 51010503, 52010503, 54040503 };
		// 外协费
		Integer wxf[] = { 51010502, 52010502, 54040502 };

		ArrayList<Integer[]> list = new ArrayList<>();
		list.add(zl);
		list.add(hy);
		list.add(cl);
		list.add(cgcl);
		list.add(jy);
		list.add(ydbg);
		list.add(sd);
		list.add(lw);
		list.add(wgcl);
		list.add(nqt);
		list.add(yf);
		list.add(zltb);
		list.add(wqt);
		list.add(wxf);

		ArrayList<String> list2 = new ArrayList<>();

		for (Integer[] integers : list) {
			Integer num = 0;
			for (Integer code : integers) {

				String result = SendVoucher.getassbalance(year, month, code.toString(), "0001",
						d.get("部门编码").toString(), "0010", d.get("项目编码").toString(), "", "");
				Map parse = (Map) JSON.parse(result);
				List list1 = (List) parse.get("data");
				for (Object o : list) {
					Map o1 = (Map) o;
					Integer debitaccumamount = (Integer) o1.get("debitaccumamount");
					num += debitaccumamount;
				}
			}
			list2.add(num.toString());
		}

		// TODO
		list2.add(xiangMuBianMa);
		String updatesql = "update formson_2372 set field0006 = ?, field0007 = ?  , field0020 = ? ,field0021 = ? ,field0022 = ? ,field0023 = ? ,"
				+ "field0024 = ? ,field0025 = ? ,field0026 = ? ,field0027 = ? ,field0028 = ? ,field0029 = ? "
				+ " ,field0030 = ? ,field0031 = ? ,field0032 = ? ,field0033 = ? ,field0034 = ? ,field0035 = ? ,field0040 = ? where field0002 = ?";
		// JdbcUtil.aud(updatesql, list2);

		return workflowEventResult;
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
		return new WorkflowEventResult();
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

}
