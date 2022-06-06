package com.github.klefstad_teaching.cs122b.billing.model.data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;

public class Sale {

    private Integer saleId;
    private BigDecimal total;
    private Instant orderDate;


    public Integer getSaleId() {
        return saleId;
    }

    public Sale setSaleId(Integer saleId) {
        this.saleId = saleId;
        return this;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public Sale setTotal(BigDecimal total) {
        this.total = total;
        return this;
    }

    public Instant getOrderDate() {
        return orderDate;
    }

    public Sale setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate.toInstant();
        return this;
    }

}
