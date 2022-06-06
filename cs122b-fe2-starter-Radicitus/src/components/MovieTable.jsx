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

const MovieTable = ({movies}) => {
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
                        <tr>
                            <StyledTD>{movie.id}</StyledTD>
                            <StyledTD>{movie.title}</StyledTD>
                            <StyledTD>{movie.year}</StyledTD>
                            <StyledTD>{movie.director}</StyledTD>
                            <StyledTD>{movie.rating}</StyledTD>
                            <StyledTD>{movie.backdropPath}</StyledTD>
                            <StyledTD>{movie.posterPath}</StyledTD>
                        </tr>
                    )
                )}
            </tbody>
        </StyledTable>
    )

}

export default MovieTable;