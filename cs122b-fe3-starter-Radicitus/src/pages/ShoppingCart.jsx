import React, {useEffect, useState} from "react";
import styled from "styled-components";
import Billing from "../backend/billing";
import {useUser} from "../hook/User";
import ShoppingCartTable from "../components/ShoppingCartTable";
import {useNavigate} from "react-router-dom";


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

const ShoppingCart = () => {
    const {accessToken} = useUser();
    const navigate = useNavigate();

    const [cart, setCart] = useState([]);
    const [total, setTotal] = useState([]);

    useEffect(() => {
        Billing.shoppingCart(accessToken)
            .then(response => (
                setCart(response.data.items),
                    setTotal(response.data.total)
                // , alert(JSON.stringify(response.data, null, 2))
            ))
            .catch(error => alert(JSON.stringify(error.response.data, null, 2)))
    }, [])

    return (
        <StyledDiv>
            <h1>Shopping Cart</h1>

            {cart &&
                <div>
                    <ShoppingCartTable cart={cart} total={total}/>

                    <button onClick={() => navigate("/checkout")}>Checkout</button>
                </div>}
        </StyledDiv>
    );
}

export default ShoppingCart;
