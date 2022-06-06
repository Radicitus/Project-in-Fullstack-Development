import React, {Component} from 'react';
import {Button, TextInput, StyleSheet, View} from 'react-native';

const registerPost = async (email, password) => {
  // alert("Email: " + email + ", password: " + password);
  return await fetch('http://10.0.2.2:8083/register', {
    method: 'POST',
    headers: {
      Accept: 'application/json',
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      email: email,
      password: password,
    }),
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

const RegisterInput = ({navigation}) => {
  const [email, onChangeEmail] = React.useState(null);
  const [password, onChangePassword] = React.useState(null);
  const [passwordRe, onChangePasswordRe] = React.useState(null);

  return (
    <View>
      <TextInput
        style={styles.input}
        onChangeText={onChangeEmail}
        placeholder="Email"
        value={email}
      />
      <TextInput
        style={styles.input}
        onChangeText={onChangePassword}
        value={password}
        placeholder="Password"
      />
      <TextInput
        style={styles.input}
        onChangeText={onChangePasswordRe}
        value={passwordRe}
        placeholder="Password again"
      />
      <View style={styles.buttonContainer}>
        <Button
          onPress={
            //   () => {
            //     //   alert("password: " + password);
            //     //   if (password !== passwordRe) {
            //     //       alert("Two passwords should be identical!");
            //     //   }
            //     // navigation.goBack(null);
            //   }
            async () => {
              if (password !== passwordRe) {
                alert('Two passwords should be identical!');
              } else {
                const registerResult = await registerPost(email, password);
                //   alert("result code: " + registerResult.result.code);
                if (registerResult.result.code === 1010) {
                  alert('Registration succeeded!');
                } else {
                  alert(
                    'Registration failed! \n' + registerResult.result.message,
                  );
                }
              }
            }
          }
          title="SIGN UP"
          color="#841584"
        />
      </View>
    </View>
  );
};

class RegisterScreen extends Component {
  render() {
    return (
      <View style={styles.container}>
        <RegisterInput />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
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
});

export default RegisterScreen;
