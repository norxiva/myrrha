package my.norxiva.myrrha;

import lombok.Getter;

public enum TransactionType {
    WITHHOLD("代收");

    @Getter
    private String name;

    TransactionType(String name) {
        this.name = name;
    }
}
