import Billing from "../backend/billing";
import {useUser} from "../hook/User";
import React, {useEffect, useState} from "react";
import {loadStripe} from "@stripe/stripe-js";
import {Elements} from "@stripe/react-stripe-js";

import CheckoutForm from "./CheckoutForm";
import "../css/CheckoutForm.css";

const stripePromise = loadStripe("pk_test_51KxoQFDJMEV0pdsvkXRlqljWA7hlI1c1WzjIHqIXlaF0SARvX1vCVDnu1v3IsmDqiZopjO5AYj50dcu27XBv8sWQ00lIaFuZFd");

const Checkout = () => {

    const {accessToken} = useUser();
    const [clientSecret, setClientSecret] = useState("");
    const [paymentIntentId, setPaymentIntentId] = useState("");

    useEffect(() => {
        Billing.getPaymentIntent(accessToken)
            .then(response => (
                setClientSecret(response.data.clientSecret)
            ))
    }, [])

    const appearance = {
        theme: 'stripe',
    };

    const options = {
        clientSecret,
        appearance,
    };

    return (
        <div>
            {clientSecret && (
                <Elements options={options} stripe={stripePromise}>
                    <CheckoutForm/>
                </Elements>
            )}
        </div>
    )

}

export default Checkout;
