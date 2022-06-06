import Config from "backend/config.json";
import Axios from "axios";


/**
 * We use axios to create REST calls to our backend
 *
 * We have provided the login rest call for your
 * reference to build other rest calls with.
 *
 * This is an async function. Which means calling this function requires that
 * you "chain" it with a .then() function call.
 * <br>
 * What this means is when the function is called it will essentially do it "in
 * another thread" and when the action is done being executed it will do
 * whatever the logic in your ".then()" function you chained to it
 * @example
 * login(request)
 * .then(response => alert(JSON.stringify(response.data, null, 2)));
 */

async function orderHistory(accessToken) {

    const options = {
        method: "GET",
        baseURL: Config.baseUrl,
        url: Config.billing.order_list,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}

async function shoppingCart(accessToken) {

    const options = {
        method: "GET",
        baseURL: Config.baseUrl,
        url: Config.billing.cart_retrieve,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}

async function updateShoppingCart(cartUpdateRequest, accessToken) {

    const requestBody = {
        movieId : cartUpdateRequest.movieId,
        quantity : cartUpdateRequest.quantity
    }

    const options = {
        method: "POST",
        baseURL: Config.baseUrl,
        url: Config.billing.cart_update,
        data: requestBody,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}

async function removeFromShoppingCart(movieId, accessToken) {

    const options = {
        method: "DELETE",
        baseURL: Config.baseUrl,
        url: Config.billing.cart_delete + "/" + movieId,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}

async function insertIntoShoppingCart(insertIntoCartRequest, accessToken) {

    const options = {
        method: "POST",
        baseURL: Config.baseUrl,
        url: Config.billing.cart_insert,
        data: insertIntoCartRequest,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}

async function getPaymentIntent(accessToken) {

    const options = {
        method: "GET",
        baseURL: Config.baseUrl,
        url: Config.billing.order_pay_intent,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}

async function completeOrder(paymentIntentId, accessToken) {

    const requestBody = {
        paymentIntentId: paymentIntentId
    }

    const options = {
        method: "POST",
        baseURL: Config.baseUrl,
        url: Config.billing.order_complete,
        data: requestBody,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}

export default {
    orderHistory, shoppingCart, updateShoppingCart, removeFromShoppingCart, insertIntoShoppingCart,
    getPaymentIntent, completeOrder
}
