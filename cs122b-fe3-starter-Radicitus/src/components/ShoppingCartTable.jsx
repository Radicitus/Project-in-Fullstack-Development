import React from "react";
import styled from "styled-components";
import Billing from "../backend/billing";
import {useUser} from "../hook/User";


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
    text-align: center;
`

const StyledButton = styled.button`
    margin: 5px;
`

const ShoppingCartTable = ({cart, total}) => {

    const {accessToken} = useUser();


    function updateQuantity(movieId, quantity) {

        const element = document.getElementById("q-" + movieId);
        element.innerHTML = (parseInt(element.innerHTML) + quantity).toString();
        quantity = parseInt(element.innerHTML);

        const payLoad = {
            movieId: movieId,
            quantity: quantity
        }

        Billing.updateShoppingCart(payLoad, accessToken)
            .catch(error => alert(JSON.stringify(error.response.data, null, 2)));
    }

    function removeCartItem(movieId) {
        let row = document.getElementById("row-" + movieId);
        row.parentNode.removeChild(row);

        Billing.removeFromShoppingCart(movieId, accessToken)
            .catch(error => alert(JSON.stringify(error.response.data, null, 2)));
    }

    if (cart.length === 0) {
        return (
            <div></div>
        )
    } else {
        return (
            <div>
                <h2>Total Price: {total}</h2>
                {/*TODO: Add updating total price with updating quantity*/}

                <StyledTable>
                    <thead>
                    <tr>
                        <StyledTH>Unit Price</StyledTH>
                        <StyledTH>Quantity</StyledTH>
                        <StyledTH>Movie Title</StyledTH>
                        <StyledTH>BD</StyledTH>
                        <StyledTH>Poster</StyledTH>
                    </tr>
                    </thead>
                    <tbody>
                    {cart.map(
                        cartItem => (
                            <tr key={cartItem.movieId} id={"row-" + cartItem.movieId}>
                                {/*TODO: Add total price of each movie and updating with quantity*/}
                                <StyledTD>{cartItem.unitPrice}</StyledTD>
                                <StyledTD>
                                    <StyledButton style={{width: "3rem"}}
                                                  onClick={() => updateQuantity(cartItem.movieId, -1)
                                                  }>-</StyledButton>

                                    <span id={"q-" + cartItem.movieId}>{cartItem.quantity}</span>

                                    <StyledButton style={{width: "3rem"}}
                                                  onClick={() => updateQuantity(cartItem.movieId, 1)
                                                  }>+</StyledButton>

                                    <StyledButton style={{width: "7rem"}}
                                                  onClick={() => removeCartItem(cartItem.movieId)}>Remove</StyledButton>
                                </StyledTD>
                                <StyledTD>{cartItem.movieTitle}</StyledTD>
                                <StyledTD><img src={"https://image.tmdb.org/t/p/w300" + cartItem.backdropPath} alt=""/></StyledTD>
                                <StyledTD><img src={"https://image.tmdb.org/t/p/w92" + cartItem.posterPath} alt=""/></StyledTD>
                            </tr>
                        )
                    )}
                    </tbody>
                </StyledTable>
            </div>
        )
    }
}

export default ShoppingCartTable;