import React from "react";
import {useUser} from "hook/User";
import styled from "styled-components";
import {useForm} from "react-hook-form";
import Idm from "backend/idm";
import {useNavigate} from "react-router-dom";


const StyledDiv = styled.div`
  display: flex;
  flex-direction: column;
`

const StyledH1 = styled.h1`
`

const StyledInput = styled.input`
`

const StyledButton = styled.button`
`

const Login = () => {
    const {
        accessToken, setAccessToken,
        refreshToken, setRefreshToken
    } = useUser();

    const navigate = useNavigate();

    const {register, getValues, handleSubmit} = useForm();

    const submitLogin = () => {
        const email = getValues("email");
        const password = getValues("password");

        const payLoad = {
            email: email,
            password: password.split('')
        }

        Idm.login(payLoad)
            .then(response => (
                setAccessToken(response.data.accessToken),
                setRefreshToken(response.data.refreshToken),
                alert(JSON.stringify(response.data, null, 2)),
                navigate("/")
            ))
            .catch(error => alert(JSON.stringify(error.response.data, null, 2)))
    }

    return (
        <StyledDiv>
            <h1>Login</h1>
            <input {...register("email")} type={"email"}/>
            <input {...register("password")} type={"password"}/>
            <button onClick={handleSubmit(submitLogin)}>Login</button>
        </StyledDiv>
    );
}

export default Login;
