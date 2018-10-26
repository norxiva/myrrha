package my.norxiva.myrrha.channel.unionpay.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XStreamAlias("GZELINK")
public class BatchTransactionMapper {

    @XStreamAlias("INFO")
    private InfoMapper info;

    @XStreamAlias("BODY")
    private BodyMapper body;
}
