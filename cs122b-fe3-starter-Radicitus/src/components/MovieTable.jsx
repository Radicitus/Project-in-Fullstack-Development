import React, {useEffect, useState} from "react";
import styled from "styled-components";
import {useNavigate} from "react-router-dom";


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

const MovieTable = ({movies}) => {

    const navigate = useNavigate();

    if (movies.length === 0) {
        return (<div></div>)
    } else {
        return (
            <StyledTable>
                <thead>
                <tr>
                    <StyledTH>ID</StyledTH>
                    <StyledTH>Title</StyledTH>
                    <StyledTH>Year</StyledTH>
                    <StyledTH>Director</StyledTH>
                    <StyledTH>Rating</StyledTH>
                    <StyledTH>Backdrop Path</StyledTH>
                    <StyledTH>Poster Path</StyledTH>
                </tr>
                </thead>
                <tbody>
                {movies.map(
                    movie => (
                        <tr key={movie.id}>
                            <StyledTD>{movie.id}</StyledTD>
                            <StyledTD>{movie.title}</StyledTD>
                            <StyledTD>{movie.year}</StyledTD>
                            <StyledTD>{movie.director}</StyledTD>
                            <StyledTD>{movie.rating}</StyledTD>
                            <StyledTD>{movie.backdropPath}</StyledTD>
                            <StyledTD>{movie.posterPath}</StyledTD>
                            <StyledTD><button onClick={() => navigate("/movieDetail/" + movie.id)}>Details</button></StyledTD>
                        </tr>
                    )
                )}
                </tbody>
            </StyledTable>
        )
    }
}

export default MovieTable;