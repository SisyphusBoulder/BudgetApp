package org.beaverbrooks.domain.account;

import java.math.BigDecimal;

public class CustomerAccount implements  IAccount
{

    private BigDecimal AccountBalance;

    public CustomerAccount(){
        AccountBalance = BigDecimal.valueOf(0.00d);
    }

    @Override
    public BigDecimal AddToAccount(BigDecimal amount) {
        this.AccountBalance = AccountBalance.add(amount);
        return GetAccountBalance();
    }

    @Override
    public BigDecimal SubtractFromAccount(BigDecimal amount) {
        this.AccountBalance = AccountBalance.subtract(amount);
        return GetAccountBalance();
    }

    @Override
    public BigDecimal GetAccountBalance() {
        return AccountBalance;
    }
}
