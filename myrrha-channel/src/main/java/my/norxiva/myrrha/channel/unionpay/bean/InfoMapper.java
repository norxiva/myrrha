package my.norxiva.myrrha.channel.unionpay.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XStreamAlias("INFO")
public class InfoMapper {

    @XStreamAlias("TRX_CODE")
    private String transactionCode;

    @XStreamAlias("VERSION")
    private String version;

    @XStreamAlias("DATA_TYPE")
    private String dataType;

    @XStreamAlias("REQ_SN")
    private String requestSerialNo;

    @XStreamAlias("RET_CODE")
    private String returnCode;

    @XStreamAlias("ERR_MSG")
    private String errorMsg;

    @XStreamAlias("SIGNED_MSG")
    private String signedMsg;


}
