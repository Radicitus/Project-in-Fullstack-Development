import React, {useEffect} from "react";
import styled from "styled-components";
import Billing from "../backend/billing";
import {useUser} from "../hook/User";
import {useNavigate, useSearchParams} from "react-router-dom";


const StyledDiv = styled.div`
  display: flex;
  flex-direction: column;
  margin: 10px;
`


const OrderComplete = () => {
    const {accessToken} = useUser();
    const [searchParams, setSearchParams] = useSearchParams();
    const navigate = useNavigate();

    useEffect(() => {
            Billing.completeOrder(searchParams.get("payment_intent"), accessToken)
                .catch(error => alert(JSON.stringify(error.response.data, null, 2)))
        }
    )

    return (
        <StyledDiv>
            <h1>Order Complete</h1>
            <h3>Id: {searchParams.get("payment_intent")}</h3>
        </StyledDiv>
    );
}

export default OrderComplete;
