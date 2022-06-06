package com.github.klefstad_teaching.cs122b.billing.model.response;

import com.github.klefstad_teaching.cs122b.billing.model.data.MovieItem;
import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class TotalItemResponse extends ResponseModel<TotalItemResponse> {

    private BigDecimal total;
    private List<MovieItem> items;


    public BigDecimal getTotal() {
        return total;
    }

    public TotalItemResponse setTotal(BigDecimal total) {
        this.total = total.setScale(2, RoundingMode.DOWN);
        return this;
    }

    public List<MovieItem> getItems() {
        return items;
    }

    public TotalItemResponse setItems(List<MovieItem> items) {
        this.items = items;
        return this;
    }
}
