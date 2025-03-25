package com.recipes.FlavourFoundry.model;

public class WithdrawRequest {
    private String selectedBank;
    private String bankAccount;
    private Integer credits;

    public WithdrawRequest() {}

    public WithdrawRequest(String selectedBank, String bankAccount, Integer credits) {
        this.selectedBank = selectedBank;
        this.bankAccount = bankAccount;
        this.credits = credits;
    }
    public String getSelectedBank() {
        return selectedBank;
    }
    public void setSelectedBank(String selectedBank) {
        this.selectedBank = selectedBank;
    }
    public String getBankAccount() {
        return bankAccount;
    }
    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }
    public Integer getCredits() {
        return credits;
    }
    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    @Override
    public String toString() {
        return "WithdrawRequest [selectedBank=" + selectedBank + ", bankAccount=" + bankAccount + ", credits=" + credits
                + "]";
    }

    
}
