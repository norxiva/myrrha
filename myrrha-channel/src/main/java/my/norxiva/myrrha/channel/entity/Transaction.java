package my.norxiva.myrrha.channel.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Transaction {
    private String serialNo;
    private String bankCode;
    private String bankAccountNo;
    private String bankAccountName;
    private String bankReservedPhone;
    private String idNo;
    private String idType;
    private String branchName;
    private String branchProvince;
    private String branchCityCode;

    private BigDecimal amount;

    private String code;
    private String message;
}
