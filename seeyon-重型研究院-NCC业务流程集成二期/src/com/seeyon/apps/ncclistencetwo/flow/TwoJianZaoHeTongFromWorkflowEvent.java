package com.seeyon.apps.ncclistencetwo.flow;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aspose.imaging.internal.I.O;
import com.seeyon.apps.collaboration.event.CollaborationProcessEvent;
import com.seeyon.apps.ncclistencetwo.utils.GetPinyin;
import com.seeyon.apps.ncclistencetwo.utils.JdbcUtil;
import com.seeyon.apps.ncclistencetwo.utils.LogInfo;
import com.seeyon.apps.ncclistencetwo.utils.SendVoucher;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.constants.ApplicationCategoryEnum;
import com.seeyon.ctp.common.ctpenumnew.manager.EnumManager;
import com.seeyon.ctp.common.po.ctpenumnew.CtpEnumItem;
import com.seeyon.ctp.form.service.FormManager;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.util.ObjectUtil;
import com.seeyon.ctp.util.annotation.ListenEvent;
import com.seeyon.ctp.workflow.event.AbstractWorkflowEvent;
import com.seeyon.ctp.workflow.event.WorkflowEventData;
import com.seeyon.ctp.workflow.event.WorkflowEventResult;
import com.seeyon.ctp.workflow.util.WorkflowEventConstants.WorkflowEventType;

import jodd.util.StringUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.songjian.utils.StringUtils;
import www.seeyon.com.utils.json.JSONObj;

public class TwoJianZaoHeTongFromWorkflowEvent extends AbstractWorkflowEvent {
    private static final Log log = LogFactory.getLog(TwoJianZaoHeTongFromWorkflowEvent.class);
    private static String twogoujianhetong = AppContext.getSystemProperty("Information.ncc.twogoujianhetong");
    private static String twogoujianhetongresult = AppContext.getSystemProperty("Information.ncc.twogoujianhetongresult");
    private static EnumManager enumManager = (EnumManager) AppContext.getBean("enumManagerNew");


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
        return "jianzaohetong";
    }

    @Override
    public String getLabel() {
        // TODO Auto-generated method stub
        return "建造合同确认收入表单凭证";
    }

    /**
     * 返回指定的模版编号
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

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String timeFormat = sdf.format(new Date());
        String year = timeFormat.substring(0, 4);
        String month = timeFormat.substring(5, 7);

        Integer arr[] = {54040201, 54040202, 54040203, 54040204, 54040205, 54040206, 54040207, 54040208, 54040301, 54040302, 54040303, 54040304, 54040305, 54040306, 54040307, 54040308, 54040401, 54040402, 54040403, 54040501, 54040502, 54040503, 54040504, 54040505,};
        for (Integer i : arr) {
            //调取接口拿数据 TODO
            String result = SendVoucher.getassbalance(year, month,i.toString() , "0004", d.get("客商编号").toString(), "0001", d.get("部门编码").toString(), "0010", d.get("项目合同号").toString());
            Map parse = (Map) JSON.parse(result);

            List list = (List) parse.get("data");

            for (Object o : list) {
                Map o1 = (Map) o;
                o1.get("");

            }
        }

        return new WorkflowEventResult();
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
         SAVELOG("建造合同确认收入表单的处理前事件---AffairId:" + data.getAffairId() + "_FormApp:" + data.getFormApp() + "_SummaryId:" + data.getSummaryId());

        formManager = (FormManager) AppContext.getBean("formManager");

        WorkflowEventResult workflowEventResult = new WorkflowEventResult();
        String str = useinterface(data);//调用凭证推送接口
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
            LogInfo.testMemberFile("twogoujianhetong:" + content + ";\r\n");
        } catch (IOException e) {
            e.printStackTrace();
            log.error("twogoujianhetong添加日志失败：" + e.getMessage());
        }
    }


    /***
     * 调用凭证的接口
     * @param data
     */
    private String useinterface(WorkflowEventData data) {
        Map<String, Object> d = data.getBusinessData();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String timeFormat = sdf.format(new Date());
        String year = timeFormat.substring(0, 4);
        String month = timeFormat.substring(5, 7);


        String xiangmuname = ObjectUtils.toString(d.get("项目令号"));

        String shouruchengbenname = ObjectUtils.toString(d.get("收入成本类别"));

        String zhaiyao = ObjectUtils.toString(d.get("摘要"));
        if (StringUtils.isBlank(zhaiyao)) {
            SAVELOG("摘要为空:" + zhaiyao);
        }
        String noshuimoney = ObjectUtils.toString(d.get("不含税金额合计小写"));
        if (StringUtils.isBlank(noshuimoney)) {
            SAVELOG("不含税金额合计小写为空:" + noshuimoney);
        }
        String shuimoney = ObjectUtils.toString(d.get("税金合计"));
        if (StringUtils.isBlank(shuimoney)) {
            SAVELOG("税金合计:" + shuimoney);
        }
        String money = ObjectUtils.toString(d.get("含税金额合计"));
        if (StringUtils.isBlank(money)) {
            SAVELOG("含税金额合计为空:" + money);
        }

        String keshangname = ObjectUtils.toString(d.get("客商编号"));
        if (StringUtils.isBlank(keshangname)) {
            SAVELOG("客商编号为空:" + keshangname);
            return "请选择客商编号";
        }

        String hangyename = ObjectUtils.toString(d.get("行业编号"));
        if (StringUtils.isBlank(hangyename)) {
            SAVELOG("行业编号为空:" + hangyename);
            return "请选择行业编号";
        }

        String bumenname = ObjectUtils.toString(d.get("负责部门编码"));
        if (StringUtils.isBlank(bumenname)) {
            SAVELOG("负责部门编码为空:" + bumenname);
            return "请选择负责部门编码";
        }
        String fapiaotype = "";
        String fapiaotypeid = ObjectUtils.toString(d.get("发票类型"));
        if (StringUtils.isBlank(fapiaotypeid)) {
            SAVELOG("发票类型id为空:" + fapiaotypeid);
            return "请选择发票类型";
        } else {
            CtpEnumItem ctpEnumItemSec = enumManager.getEnumItem(Long.valueOf(fapiaotypeid));
            fapiaotype = ctpEnumItemSec.getShowvalue().toString();
            SAVELOG("发票类型:" + fapiaotype);
        }

        String shourutype = "";
        String shourutypeid = ObjectUtils.toString(d.get("收入成本类别"));
        if (StringUtils.isBlank(shourutypeid)) {
            SAVELOG("收入成本类别id为空:" + shourutypeid);
            return "请选择收入成本类别";
        } else {
            CtpEnumItem ctpEnumItemSec = enumManager.getEnumItem(Long.valueOf(shourutypeid));
            shourutype = ctpEnumItemSec.getShowvalue().toString();
            SAVELOG("收入成本类别:" + shourutype);
        }
        String username = ObjectUtils.toString(d.get("凭证生成人"));
        if (StringUtils.isBlank(username)) {
            SAVELOG("凭证生成人为空:" + username);
            return "请选择凭证生成人";
        } else {
            try {
                OrgManager orgManager = (OrgManager) AppContext.getBean("orgManager");
                V3xOrgMember orgMember = orgManager.getMemberById(Long.parseLong(username));
                username = orgMember.getName();
                username = GetPinyin.getPingYin(username);
                //  这个人特殊, 在NCC里重名
                if ("liujie".equals(username)) {
                    username = "liujie01";
                }

            } catch (Exception e1) {
                e1.printStackTrace();
                SAVELOG("获取制单人有误:" + e1.getMessage());
            }
        }

        String returnmsg = null;

        String[] split = xiangmuname.split(",");
        for (String s : split) {
            StringBuffer writexmldoc = new StringBuffer();
            writexmldoc.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            //正式
            writexmldoc.append("<ufinterface account = \"01\" billtype=\"vouchergl\" businessunitcode=\"develop\" filename=\"\" groupcode=\"GJJT\" isexchange=\"Y\" orgcode=\"101005\" receiver=\"101005\" replace=\"Y\" roottag=\"\" sender=\"029ZXJX-OA\">");
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
            writexmldoc.append("<prepareddate>").append(timeFormat).append("</prepareddate>");//制单日期, 非空
            writexmldoc.append("<pk_prepared>").append(username).append("</pk_prepared>"); // 制单人,非空
            writexmldoc.append("<pk_casher>").append("</pk_casher>"); // 出纳
            writexmldoc.append("<signflag>").append("N").append("</signflag>"); //签字标志
            writexmldoc.append("<pk_checked>").append("</pk_checked>"); // 审核人
            writexmldoc.append("<tallydate></tallydate><pk_manager></pk_manager><memo1></memo1><memo2></memo2><reserve1></reserve1><reserve2>N</reserve2><siscardflag />");
            writexmldoc.append("<pk_org>").append("101005").append("</pk_org>"); //所属组织 非空
            writexmldoc.append("<pk_org_v>").append("101005").append("</pk_org_v>"); //所属组织版本. 可空
            writexmldoc.append("<pk_group>").append("GJJT").append("</pk_group>"); //所集团  如果不输集团取当前登陆集团 例如GJJT
            writexmldoc.append("<details>");


            // 借方分录 分录
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
            writexmldoc.append("<pk_accasoa>").append("540801").append("</pk_accasoa>"); //科目  非空
            writexmldoc.append("<pk_unit>").append("</pk_unit>");// 所属二级核算单位
            writexmldoc.append("<pk_unit_v>").append("</pk_unit_v>");  // 所属二级核算单位 版本可空
            // 辅助核算
            writexmldoc.append("<ass>");
            // 客商
            writexmldoc.append("<item>");
            writexmldoc.append("<pk_Checktype>").append("0004").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
            writexmldoc.append("<pk_Checkvalue>").append(keshangname).append("</pk_Checkvalue>");  // 辅助核算值 档案转换
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

            // TODO 合同号
            writexmldoc.append("<item>");
            writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
            writexmldoc.append("<pk_Checkvalue>").append("").append("</pk_Checkvalue>");
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

            //<!-- 贷方分录 1  -->
            writexmldoc.append("<item>");
            writexmldoc.append("<creditquantity>").append("</creditquantity>");//<!-- 贷方数量 可空-->
            writexmldoc.append("<creditamount>").append(noshuimoney).append("</creditamount>");//<!-- 原币贷方金额 非空-->
            writexmldoc.append("<localcreditamount>").append(noshuimoney).append("</localcreditamount>");//<!-- 本币贷方金额 非空-->
            writexmldoc.append("<groupcreditamount>").append(noshuimoney).append("</groupcreditamount>");//<!-- 集团本币贷方金额 非空-->
            writexmldoc.append("<globalcreditamount>").append(noshuimoney).append("</globalcreditamount>");//<!-- 全局本币贷方金额 非空-->
            writexmldoc.append("<detailindex>").append("1").append("</detailindex>");//<!-- 分录号 非空-->
            writexmldoc.append("<explanation>").append(zhaiyao).append("</explanation>");//<!-- 摘要 非空-->
            writexmldoc.append("<verifydate>").append("</verifydate>");//<!-- 业务日期 可空-->
            writexmldoc.append("<price>").append("0.00000000").append("</price>");//<!-- 单价 可空-->
            writexmldoc.append("<excrate2>").append("1").append("</excrate2>");//<!-- 折本汇率 可空-->
            writexmldoc.append("<pk_currtype>").append("CNY").append("</pk_currtype>");//<!-- 币种 非空-->
            writexmldoc.append("<pk_accasoa>").append("6001").append("</pk_accasoa>");//<!-- 科目 非空-->
            writexmldoc.append("<pk_unit>").append("</pk_unit>");////<!-- 所属二级核算单位 可空 （组织） -->
            writexmldoc.append("<pk_unit_v>").append("</pk_unit_v>");//<!-- 所属二级核算单位 版本可空 （组织） -->
            writexmldoc.append("<ass>");

            // 客商
            writexmldoc.append("<item>");
            writexmldoc.append("<pk_Checktype>").append("0004").append("</pk_Checktype>");
            writexmldoc.append("<pk_Checkvalue>").append(keshangname).append("</pk_Checkvalue>");
            writexmldoc.append("</item>");

            // TODO 行业
            writexmldoc.append("<item>");
            writexmldoc.append("<pk_Checktype>").append("").append("</pk_Checktype>");
            writexmldoc.append("<pk_Checkvalue>").append("C3516").append("</pk_Checkvalue>");
            writexmldoc.append("</item>");

            // 部门
            writexmldoc.append("<item>");
            writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
            writexmldoc.append("<pk_Checkvalue>").append(bumenname).append("</pk_Checkvalue>");
            writexmldoc.append("</item>");

            // 项目
            writexmldoc.append("<item>");
            writexmldoc.append("<pk_Checktype>").append("0010").append("</pk_Checktype>");
            writexmldoc.append("<pk_Checkvalue>").append(xiangmuname).append("</pk_Checkvalue>");
            writexmldoc.append("</item>");

            // TODO 收入成本类别
            writexmldoc.append("<item>");
            writexmldoc.append("<pk_Checktype>").append("").append("</pk_Checktype>");
            writexmldoc.append("<pk_Checkvalue>").append("02").append("</pk_Checkvalue>");
            writexmldoc.append("</item>");

            writexmldoc.append("</ass>");
            writexmldoc.append("</item>");

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
            SAVELOG("推送凭证的xml:" + str);

            try {
                returnmsg = SendVoucher.post(str);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                SAVELOG("推送凭证有问题:" + e.getMessage());
            }
        }
        return returnmsg;
    }
    
}

