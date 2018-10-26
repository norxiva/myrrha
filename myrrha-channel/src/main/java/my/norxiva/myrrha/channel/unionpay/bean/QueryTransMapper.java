package my.norxiva.myrrha.channel.unionpay.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("QUERY_TRANS")
public class QueryTransMapper {

    @XStreamAlias("QUERY_SN")
    private String querySerialNo;

    @XStreamAlias("QUERY_REMARK")
    private String queryRemark;
}
