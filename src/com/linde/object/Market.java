package com.linde.object;

import com.linde.RuleEnum;

import java.math.BigDecimal;

public class Market {
    private String salesOrg;
    private BigDecimal totalNumber;
    private BigDecimal acturalCount;
    private BigDecimal percentage;
    private Double month;
    private String salesGrp;

    private RuleEnum rule;

    public Market() {
    }

    public Market(final String name, final BigDecimal totalNumber, final BigDecimal percentage) {
        this.salesOrg = name;
        this.totalNumber = totalNumber;
        this.percentage = percentage;
    }

    public String getSalesOrg() {
        return salesOrg;
    }

    public void setSalesOrg(final String salesOrg) {
        this.salesOrg = salesOrg;
    }

    public BigDecimal getTotalNumber() {
        if (totalNumber != null)
            return totalNumber;
        else
            return new BigDecimal(100);
    }

    public void setTotalNumber(final BigDecimal totalNumber) {
        this.totalNumber = totalNumber;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(final BigDecimal percentage) {
        this.percentage = percentage;
    }

    public RuleEnum getRule() {
        return rule;
    }

    public void setRule(RuleEnum rule) {
        this.rule = rule;
    }

    public Double getMonth() {
        return month;
    }

    public void setMonth(Double month) {
        this.month = month;
    }

    public String getSalesGrp() {
        return salesGrp;
    }

    public void setSalesGrp(String salesGrp) {
        this.salesGrp = salesGrp;
    }

    public BigDecimal getActuralCount() {
        return acturalCount;
    }

    public void setActuralCount(BigDecimal acturalCount) {
        this.acturalCount = acturalCount;
    }

    @Override
    public String toString() {
        return String.format("[month=%s][salesOrg=%s][salesGrp=%s][totalNumber=%s][percentage=%s][rule=%s]", month, salesOrg, salesGrp, totalNumber, percentage, rule);
    }


}