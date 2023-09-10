package common;

import com.google.gson.annotations.SerializedName;


    public class User {
        @SerializedName("email")
        private String email;
        @SerializedName("password")
        private String password;
        @SerializedName("token")
        private String token;
        @SerializedName("accessToken")
        private String accessToken;
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {return password;}

        public void setPassword(String password) {
            this.password = password;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getAccessToken() {return accessToken;}

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String toString() {
            return "User{" +
                    "email='" + email + '\'' +
                    ", password='" + password + '\'' +
                    ", token=" + token +
                    ", accessToken=" + accessToken +
                    '}';
        }
    }




