package com.seeyon.apps.ncclistencetwo.utils;

public class GetVoucherXml {


    public static String financeInterface() {
        StringBuffer writexmldoc = new StringBuffer();
        writexmldoc.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writexmldoc.append("<ufinterface account = \"develop\" billtype=\"vouchergl\" businessunitcode=\"develop\" filename=\"\" groupcode=\"GJJT\" isexchange=\"Y\" orgcode=\"101005\" receiver=\"101005\" replace=\"Y\" roottag=\"\" sender=\"022ZXJX-OA\">");

        writexmldoc.append("<voucher id= \"\">");
        writexmldoc.append("<voucher_head>");
        writexmldoc.append("<pk_voucher>").append("10111210000000005DP").append("</pk_voucher>");
        writexmldoc.append("<pk_vouchertype>").append("01").append("</pk_vouchertype>");// 凭证主键, 没有就是新增, 有就是修改   可以为空
        writexmldoc.append("<year>").append("2020").append("</year>");//  会计年度 如2012
        writexmldoc.append("<pk_system>").append("GL").append("</pk_system>");// 来源系统 如GL
        writexmldoc.append("<voucherkind>").append("0").append("</voucherkind>"); //<!--凭证类型值 0：正常凭证 3：数量调整凭证 不可空-->
        writexmldoc.append("<pk_accountingbook>").append("101005-0001").append("</pk_accountingbook>");//<!--核算账簿 非空 （账簿_财务核算账簿）-->
        writexmldoc.append("<discardflag>").append("N").append("</discardflag>"); // 作废标志; 可空 N
        writexmldoc.append("<period>").append("12").append("</period>");// 会计期间, 非空
        writexmldoc.append("<no>").append("</no>");//凭证号为空自动分配 非空：按凭证号处理
        writexmldoc.append("<attachment>").append("0").append("</attachment>");//附单数据 可空
        writexmldoc.append("<prepareddate>").append("2020-12-31 13:31:55").append("</prepareddate>");//制单日期, 非空
        writexmldoc.append("<pk_prepared>").append("wanganqi").append("</pk_prepared>"); // 制单人,非空
        writexmldoc.append("<pk_casher>").append("</pk_casher>"); // 出纳,非空
        writexmldoc.append("<signflag>").append("N").append("</signflag>"); //签字标志
        writexmldoc.append("<pk_checked>").append("</pk_checked>"); // 审核人
        writexmldoc.append("<tallydate></tallydate><pk_manager></pk_manager><memo1></memo1><memo2></memo2><reserve1></reserve1><reserve2>N</reserve2><siscardflag />");

        writexmldoc.append("<pk_org>").append("101005").append("</pk_org>"); //所属组织 非空
        writexmldoc.append("<pk_org_v>").append("101005").append("</pk_org_v>"); //所属组织版本. 可空
        writexmldoc.append("<pk_group>").append("GJJT").append("</pk_group>"); //所集团  如果不输集团取当前登陆集团 例如GJJT
        writexmldoc.append("<details>");
        // 借方分录
        writexmldoc.append("<item>");
        writexmldoc.append("<detailindex>").append("1").append("</detailindex>"); // 分录号
        writexmldoc.append("<explanation>").append("冶金所测试付款").append("</explanation>"); //   摘要 非空
        writexmldoc.append("<verifydate>").append("2020-12-31 13:31:55").append("</verifydate>");  // 业务日期, 可空
        writexmldoc.append("<price>").append("0").append("</price>");//单价 可空
        writexmldoc.append("<excrate2>").append("1").append("</excrate2>"); // 折本汇率 可空
        writexmldoc.append("<debitquantity>").append("32000").append("</debitquantity>"); // 借方数量 可空
        writexmldoc.append("<debitamount>").append("8000").append("</debitamount>"); //原币借方金额 非空
        writexmldoc.append("<localdebitamount>").append("8000").append("</localdebitamount>"); //本币借方金额 非空
        writexmldoc.append("<groupdebitamount>").append("0").append("</groupdebitamount>"); //集团本币借方金额 非空
        writexmldoc.append("<globaldebitamount>").append("0").append("</globaldebitamount>"); //全局本币借方金额 非空
        writexmldoc.append("<pk_currtype>").append("CNY").append("</pk_currtype>"); //币种  非空
        writexmldoc.append("<pk_accasoa>").append("112303").append("</pk_accasoa>"); //科目  非空
        writexmldoc.append("<pk_unit>").append("</pk_unit>");// 所属二级核算单位
        writexmldoc.append("<pk_unit_v>").append("</pk_unit_v>");  // 所属二级核算单位 版本可空
        // 辅助核算
        writexmldoc.append("<ass>").append("<item>");
        writexmldoc.append("<pk_Checktype>").append("0004").append("</pk_Checktype>"); // 辅助核算类型 (会计科目辅助核算)
        writexmldoc.append("<pk_Checkvalue>").append("00015398").append("</pk_Checkvalue>");  // 辅助核算值 档案转换
        writexmldoc.append("</item>");

        writexmldoc.append("<item>");
        writexmldoc.append("<pk_Checktype>").append("0010").append("</pk_Checktype>");
        writexmldoc.append("<pk_Checkvalue>").append("Y1600313.W03").append("</pk_Checkvalue>");
        writexmldoc.append("</item>");

        writexmldoc.append("<item>");
        writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
        writexmldoc.append("<pk_Checkvalue>").append("002").append("</pk_Checkvalue>");
        writexmldoc.append("</item>").append("</ass>");


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
        writexmldoc.append("<creditamount>").append("8000").append("</creditamount>");//<!-- 原币贷方金额 非空-->
        writexmldoc.append("<localcreditamount>").append("8000").append("</localcreditamount>");//<!-- 本币贷方金额 非空-->
        writexmldoc.append("<groupcreditamount>").append("19.00000000").append("</groupcreditamount>");//<!-- 集团本币贷方金额 非空-->
        writexmldoc.append("<globalcreditamount>").append("19.00000000").append("</globalcreditamount>");//<!-- 全局本币贷方金额 非空-->
        writexmldoc.append("<detailindex>").append("2").append("</detailindex>");//<!-- 分录号 非空-->
        writexmldoc.append("<explanation>").append("取备用金").append("</explanation>");//<!-- 摘要 非空-->
        writexmldoc.append("<verifydate>").append("</verifydate>");//<!-- 业务日期 可空-->
        writexmldoc.append("<price>").append("0.00000000").append("</price>");//<!-- 单价 可空-->
        writexmldoc.append("<excrate2>").append("1").append("</excrate2>");//<!-- 折本汇率 可空-->
        writexmldoc.append("<pk_currtype>").append("CNY").append("</pk_currtype>");//<!-- 币种 非空-->
        writexmldoc.append("<pk_accasoa>").append("112303").append("</pk_accasoa>");//<!-- 科目 非空-->
        writexmldoc.append("<pk_unit>").append("</pk_unit>");////<!-- 所属二级核算单位 可空 （组织） -->
        writexmldoc.append("<pk_unit_v>").append("</pk_unit_v>");//<!-- 所属二级核算单位 版本可空 （组织） -->

        writexmldoc.append("<ass><item>");
        writexmldoc.append("<pk_Checktype>").append("0004").append("</pk_Checktype>");
        writexmldoc.append("<pk_Checkvalue>").append("00015398").append("</pk_Checkvalue>");
        writexmldoc.append("</item>");
        writexmldoc.append("<item>");
        writexmldoc.append("<pk_Checktype>").append("0010").append("</pk_Checktype>");
        writexmldoc.append("<pk_Checkvalue>").append("Y1600313.W03").append("</pk_Checkvalue>");
        writexmldoc.append("</item>");
        writexmldoc.append("<item>");
        writexmldoc.append("<pk_Checktype>").append("0001").append("</pk_Checktype>");
        writexmldoc.append("<pk_Checkvalue>").append("002").append("</pk_Checkvalue>");


        writexmldoc.append("</item></ass>");

        writexmldoc.append("<cashFlow><item>");
        writexmldoc.append("<m_pk_currtype>").append("</m_pk_currtype>");  //<!--币种,最大长度为64,类型为:String-->
        writexmldoc.append("<money>").append("</money>");   // <!--原币,最大长度为64,类型为:Double-->
        writexmldoc.append("<moneyglobal>").append("</moneyglobal>");  // <!--全局本币,最大长度为64,类型为:Double-->
        writexmldoc.append("<moneygroup>").append("</moneygroup>");   //  <!--集团本币,最大长度为64,类型为:Double-->
        writexmldoc.append("<moneymain>").append("</moneymain>");    //  <!--本币,最大长度为64,类型为:Double-->
        writexmldoc.append("<pk_cashflow>").append("</pk_cashflow>");    //  <!--现金主键,最大长度为64,类型为:String-->
        writexmldoc.append("<pk_innercorp>").append("</pk_innercorp>");   //  <!--内部单位主键,最大长度为64,类型为:String-->
        writexmldoc.append("</item></cashFlow></item></details></voucher_head></voucher></ufinterface>");




        return writexmldoc.toString();
    }

}
