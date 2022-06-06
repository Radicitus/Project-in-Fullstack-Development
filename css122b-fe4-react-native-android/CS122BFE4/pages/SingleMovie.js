import {FlatList, Image, StyleSheet, Text, View} from 'react-native';
import React, {useState, useEffect} from 'react';

const SingleMovieScreen = ({route, navigation}) => {
  const {movieId, accessToken} = route.params;
  const [movieDetails, setMovieDetails] = useState([]);
  const [genreDetails, setGenreDetails] = useState([]);
  const [personDetails, setPersonDetails] = useState([]);

  const searchMovieById = async () => {
    return await fetch('http://10.0.2.2:8082/movie/' + movieId, {
      method: 'GET',
      headers: {
        Authorization: 'Bearer ' + accessToken,
      },
    })
      .then(response => response.json())
      .then(json => {
        console.log(json);
        return json;
      })
      .catch(error => {
        console.error(error);
      });
  };

  useEffect(() => {
    searchMovieById().then(result => {
      if (result.result.code === 2010) {
        console.log('USE EFFECT');
        console.log(result);
        console.log(result.movie);
        setMovieDetails(result.movie);
        setGenreDetails(result.genres);
        setPersonDetails(result.persons);
      } else {
        alert('Movie not found.');
      }
    });
  }, []);

  return (
    <View>
      {movieDetails && (
        <View>
          <Image
            style={styles.poster}
            source={{
              uri:
                'https://image.tmdb.org/t/p/original' + movieDetails.posterPath,
            }}
          />
          <Text>{movieDetails.title}</Text>
          <Text>{movieDetails.year}</Text>
          <Text>Director: {movieDetails.director}</Text>
          <Text>Rating: {movieDetails.rating}</Text>
          <Text>Votes: {movieDetails.numVotes}</Text>
          <Text>Overview: {movieDetails.overview}</Text>

          <Text>Genres: </Text>
          <FlatList
            data={genreDetails}
            keyExtractor={item => item.id}
            renderItem={({item}) => (
              <View style={styles.container}>
                <Text>{item.name}</Text>
              </View>
            )}
          />

          <Text>People: </Text>
          <FlatList
            data={personDetails}
            keyExtractor={item => item.id}
            renderItem={({item}) => (
              <View style={styles.container}>
                <Text>{item.name}</Text>
              </View>
            )}
          />
        </View>
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  view: {
    margin: 10,
  },
  container: {
    flex: 1,
    justifyContent: 'center',
  },
  subContainer: {
    flex: 1,
  },
  buttonContainer: {
    margin: 20,
  },
  input: {
    height: 40,
    margin: 12,
    borderWidth: 1,
    padding: 10,
  },
  baseText: {
    fontFamily: 'Cochin',
  },
  titleText: {
    fontSize: 20,
    fontWeight: 'bold',
  },
  poster: {
    width: 100,
    height: 200,
    margin: 10,
  },
});

export default SingleMovieScreen;
