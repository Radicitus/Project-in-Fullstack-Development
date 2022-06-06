const searchMovie = async (payLoad, accessToken) => {
  return await fetch(
    'http://localhost:8082/movie/search?' +
      new URLSearchParams({
        title: payLoad.title,
        year: payLoad.year,
        director: payLoad.director,
        genre: payLoad.genre,
        limit: payLoad.limit,
        page: payLoad.page,
        orderBy: payLoad.orderBy,
        direction: payLoad.direction,
      }),
    {
      method: 'GET',
      headers: {
        Authorization: 'Bearer ' + accessToken,
      },
    },
  )
    .then(response => response.json())
    .then(json => {
      console.log(json);
      alert(json);
      return json;
    })
    .catch(error => {
      console.error(error);
    });
};

export default {
  searchMovie,
};
