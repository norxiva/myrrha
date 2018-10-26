<#-- @ftlvariable name="request" type="my.norxiva.myrrha.channel.bean.BatchTransactionRequest" -->
<#-- @ftlvariable name="sum" type="java.math.BigDecimal" -->
<#-- @ftlvariable name="count" type="java.lang.Long" -->
<#setting number_format="######0"/>
<#import "UNIONPAY_Macro.ftl" as macro>
<@macro.compress_single_line>
<?xml version="1.0" encoding="gbk"?>
<GZELINK>
    <INFO>
        <TRX_CODE>100001</TRX_CODE>
        <VERSION>05</VERSION>
        <USER_NAME>${request.username}</USER_NAME>
        <USER_PASS>${request.password}</USER_PASS>
        <REQ_SN>${request.orderNo}</REQ_SN>
        <SIGNED_MSG></SIGNED_MSG>
    </INFO>
    <BODY>
    <TRANS_SUM>
        <BUSINESS_CODE>14900</BUSINESS_CODE>
        <MERCHANT_ID>${request.merchantNo}</MERCHANT_ID>
        <TOTAL_ITEM>${count}</TOTAL_ITEM>
        <TOTAL_SUM>${sum * 100}</TOTAL_SUM>
    </TRANS_SUM>
    <TRANS_DETAILS>
        <#list request.transactions as transaction>
            <TRANS_DETAIL>
                <SN>${transaction.serialNo}</SN>
                <BANK_CODE>${transaction.bankCode}</BANK_CODE>
                <ACCOUNT_TYPE>00</ACCOUNT_TYPE>
                <ACCOUNT_NO>${transaction.bankAccountNo}</ACCOUNT_NO>
                <ACCOUNT_NAME>${transaction.bankAccountName}</ACCOUNT_NAME>
                <PROVINCE>${transaction.branchProvince!}</PROVINCE>
                <CITY>${transaction.branchCityCode!}</CITY>
                <BANK_NAME>${transaction.branchName!}</BANK_NAME>
                <AMOUNT>${transaction.amount * 100}</AMOUNT>
                <ID>${transaction.idNo!}</ID>
                <TEL>${transaction.bankReservedPhone!}</TEL>
            </TRANS_DETAIL>
        </#list>

    </TRANS_DETAILS>
    </BODY>
</GZELINK>

</@macro.compress_single_line>