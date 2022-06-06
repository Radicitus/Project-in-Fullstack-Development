import React, {useState, useEffect} from "react";
import styled from "styled-components";
import Billing from "../backend/billing";
import {useUser} from "../hook/User";
import {useForm} from "react-hook-form";
import OrderHistoryTable from "../components/OrderHistoryTable";


const StyledDiv = styled.div`
  display: flex;
  flex-direction: column;
  margin: 10px;
`

const StyledH1 = styled.h1`
`

const StyledInput = styled.input`
`

const StyledButton = styled.button`
`

const OrderHistory = () => {
    const {accessToken} = useUser();
    const {handleSubmit} = useForm();

    const [orderHistoryResults, setOrderHistoryResults] = useState([]);

    React.useEffect(() => {
        Billing.orderHistory(accessToken)
            .then(response => (
                setOrderHistoryResults(response.data.sales)
                // , alert(JSON.stringify(response.data, null, 2))
            ))
            .catch(error => alert(JSON.stringify(error.response.data, null, 2)))
    }, [])

    return (
        <StyledDiv>
            <h1>Order History</h1>
            {orderHistoryResults && <OrderHistoryTable sales={orderHistoryResults}/>}
        </StyledDiv>
    );
}

export default OrderHistory;
