import React, {useEffect, useState} from "react";
import styled from "styled-components";


const StyledTable = styled.table`
    border: black solid 1px;
    padding: 0;
    max-height: 30px;
`
const StyledTH = styled.th`
    border: black solid 1px;
    max-height: 30px;
`
const StyledTD = styled.td`
    border: black solid 1px;
    max-height: 20px;
`

const OrderHistoryTable = ({sales}) => {
    if (sales.length === 0) {
        return (
            <div></div>
        )
    } else {
        return (
            <StyledTable>
                <thead>
                <tr>
                    <StyledTH>Sale ID</StyledTH>
                    <StyledTH>Total</StyledTH>
                    <StyledTH>Order Date</StyledTH>
                </tr>
                </thead>
                <tbody>
                {sales.map(
                    sale => (
                        <tr key={sale.saleId}>
                            <StyledTD>{sale.saleId}</StyledTD>
                            <StyledTD>{sale.total}</StyledTD>
                            <StyledTD>{sale.orderDate}</StyledTD>
                        </tr>
                    )
                )}
                </tbody>
            </StyledTable>
        )
    }
}

export default OrderHistoryTable;