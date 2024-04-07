package ru.otus.bank.entity;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private Long id;
    private BigDecimal amount;
    private Integer type;
    private String number;
    private Long agreementId;

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", amount=" + amount +
                ", type=" + type +
                ", number='" + number + '\'' +
                ", agreementId=" + agreementId +
                "}\n";
    }
}
