package my.norxiva.myrrha;

import lombok.Getter;

public enum  ChannelType {
    UNIONPAY("银联支付");

    @Getter
    private String name;

    ChannelType(String name) {
        this.name = name;
    }
}
