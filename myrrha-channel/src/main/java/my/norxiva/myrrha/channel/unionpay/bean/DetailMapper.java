package my.norxiva.myrrha.channel.unionpay.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@XStreamAlias("RET_DETAIL")
public class DetailMapper {

    @XStreamAlias("SN")
    private String serialNo;

    @XStreamAlias("RET_CODE")
    private String returnCode;

    @XStreamAlias("ERR_MSG")
    private String errorMsg;

    @XStreamAlias("ACCOUNT_NO")
    private String bankAccountNo;

    @XStreamAlias("ACCOUNT")
    private String accountNo;

    @XStreamAlias("ACCOUNT_NAME")
    private String bankAccountName;

    @XStreamAlias("AMOUNT")
    private BigDecimal amount;

    @XStreamAlias("CUST_USERID")
    private String userId;

    @XStreamAlias("REMARK")
    private String remark;

    @XStreamAlias("RESERVE1")
    private String reserve1;

    @XStreamAlias("RESERVE2")
    private String reserve2;

    @XStreamAlias("COMPLETE_TIME")
    private String completedTime;

    @XStreamAlias("SETT_DATE")
    private String settlementDate;

}
