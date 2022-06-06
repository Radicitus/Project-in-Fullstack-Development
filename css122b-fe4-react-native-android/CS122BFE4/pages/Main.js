import React, {useState, useEffect} from 'react';
import {Picker} from '@react-native-picker/picker';
import {
  Text,
  FlatList,
  StyleSheet,
  View,
  TouchableHighlight,
  Image,
  TextInput,
  Button,
} from 'react-native';

var movieList = [];

const addMovie = (newKey, newValue, new_thumbnail_url) => {
  movieList.push({
    key: newKey,
    value: newValue,
    thumbnail_url: new_thumbnail_url,
  });
};

const movieDivider = () => {
  return (
    <View
      style={{
        height: 1,
        width: '100%',
        backgroundColor: '#607D8B',
      }}
    />
  );
};

const MainScreen = ({route, navigation}) => {
  const {accessToken, refreshToken} = route.params;

  const [movieResults, setMovieResults] = useState([]);

  const [title, setTitle] = useState(null);
  const [year, setYear] = useState(null);
  const [director, setDirector] = useState(null);
  const [genre, setGenre] = useState(null);
  const [limit, setLimit] = useState(10);
  const [page, setPage] = useState(1);
  const [orderBy, setOrderBy] = useState('title');
  const [direction, setDirection] = useState('asc');

  const updateMovieList = () => {
    movieList.length = 0;

    for (const movie in movieResults) {
      addMovie(
        movieResults[movie].id,
        movieResults[movie].title,
        'https://image.tmdb.org/t/p/original' + movieResults[movie].posterPath,
      );
    }
  };

  const searchMovie = async () => {
    return await fetch(
      'http://10.0.2.2:8082/movie/search?' +
        new URLSearchParams({
          title: title,
          year: year,
          director: director,
          genre: genre,
          limit: limit,
          page: page,
          orderBy: orderBy,
          direction: direction,
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
        return json;
      })
      .catch(error => {
        console.error(error);
      });
  };

  // addMovie("accessToken", accessToken, "https://reactnative.dev/img/tiny_logo.png");
  // addMovie("refreshToken", refreshToken, "https://reactnative.dev/img/tiny_logo.png");

  return (
    <View style={styles.container}>
      <View>
        <Text>SEARCH</Text>
        <TextInput
          placeholder={'Title'}
          value={title}
          onChangeText={text => setTitle(text)}
        />
        <TextInput
          placeholder={'Year'}
          value={year}
          onChangeText={text => setYear(text)}
        />
        <TextInput
          placeholder={'Director'}
          value={director}
          onChangeText={text => setDirector(text)}
        />
        <TextInput
          placeholder={'Genre'}
          value={genre}
          onChangeText={text => setGenre(text)}
        />
        <Text>Limit</Text>
        <Picker
          selectedValue={limit.toString()}
          onValueChange={currentLim => setLimit(parseInt(currentLim))}>
          <Picker.Item label={'10'} value={'10'} />
          <Picker.Item label={'25'} value={'25'} />
          <Picker.Item label={'50'} value={'50'} />
          <Picker.Item label={'100'} value={'100'} />
        </Picker>
        <Text>Page</Text>
        <TextInput
          keyboardType={'numeric'}
          value={page.toString()}
          onChangeText={num => setPage(parseInt(num))}
        />
        <Text>Order By</Text>
        <Picker
          selectedValue={orderBy}
          onValueChange={currentOrderBy => setOrderBy(currentOrderBy)}>
          <Picker.Item label={'Title'} value={'title'} />
          <Picker.Item label={'Rating'} value={'rating'} />
          <Picker.Item label={'Year'} value={'year'} />
        </Picker>
        <Text>Direction</Text>
        <Picker
          selectedValue={direction}
          onValueChange={currentDirection => setDirection(currentDirection)}>
          <Picker.Item label={'Ascending'} value={'asc'} />
          <option label={'Descending'} value={'desc'} />
        </Picker>
        <Button
          title={'Submit'}
          onPress={async () => {
            const result = await searchMovie();
            if (result.result.code === 2020) {
              setMovieResults(result.movies);
              updateMovieList();
              console.log('MovieList updated');
            } else {
              alert('Movies not found. \n' + result.result.message);
            }
          }}
        />
      </View>

      <FlatList
        data={movieList}
        keyExtractor={item => item.key}
        renderItem={({item}) => (
          <View style={styles.container}>
            <TouchableHighlight
              onPress={() => {
                // alert('Key: ' + item.key + '\nValue: ' + item.value);
                navigation.navigate('SingleMovie', {
                  movieId: item.key,
                  accessToken: accessToken,
                });
              }}
              underlayColor="white">
              <View style={styles.subContainer} flexDirection="row">
                <Image
                  style={styles.thumbnail}
                  source={{
                    uri: item.thumbnail_url,
                  }}
                />
                <Text style={{fontSize: 22, margin: 10}}>
                  {item.key} - {item.value}
                </Text>
              </View>
            </TouchableHighlight>
          </View>
        )}
        ItemSeparatorComponent={movieDivider}
      />
      {/* <Text
                style={styles.titleText}
            >
                "accessToken: {accessToken}" {"\n"}
                "refreshToken: {refreshToken}"
            </Text> */}
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
  thumbnail: {
    width: 50,
    height: 50,
    margin: 10,
  },
});

export default MainScreen;
