package my.norxiva.myrrha.channel.unionpay.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@XStreamAlias("BODY")
public class BodyMapper {
    @XStreamAlias("RET_DETAILS")
    private List<DetailMapper> details = new ArrayList<>();

}
