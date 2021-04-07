package org.interview.paysend.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.math.BigDecimal;

public class BalanceResponse extends BaseResponse {
    @JacksonXmlProperty(localName = "extra")
    private Extra balance;

    public BalanceResponse(BigDecimal balance) {
        super(0);
        this.balance = new Extra();
        this.balance.setName("balance");
        this.balance.setValue(balance.toString());
    }

    public Extra getBalance() {
        return balance;
    }

    public void setBalance(Extra balance) {
        this.balance = balance;
    }
}
