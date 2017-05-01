package com.teamtreehouse.ribbit.models;

import com.teamtreehouse.ribbit.mockdata.MockRelations;
import com.teamtreehouse.ribbit.mockdata.MockUsers;
import com.teamtreehouse.ribbit.models.callbacks.LogInCallback;
import com.teamtreehouse.ribbit.models.callbacks.SaveCallback;
import com.teamtreehouse.ribbit.models.callbacks.SignUpCallback;

import java.util.UUID;

public class User implements Comparable<User> {

    // Field names
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_FRIENDS_RELATION = "friendsRelation";

    private static User currentUser;

    private UUID id;
    private String username;
    private String password;
    private String email;

    public User() {
        id = UUID.randomUUID();
    }

    public User(String username, String password, String email) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getObjectId() {
        return id.toString();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Relation getRelation(String key) {
        if (key.equals(KEY_FRIENDS_RELATION)) {
            return MockRelations.getUserRelations(User.getCurrentUser().getObjectId());
        }

        return null;
    }

    public void signUpInBackground(SignUpCallback callback) {
        boolean isNewUser = true;
        for (User user : MockUsers.testUsers) {
            if (user.getUsername().equalsIgnoreCase(this.username)) {
                isNewUser = false;
                callback.done(new Exception("Username already in use."));
            }
            else if (user.getEmail().equalsIgnoreCase(this.email)) {
                isNewUser = false;
                callback.done(new Exception("Email address already in use."));
            }
        }

        if (isNewUser) {
            MockUsers.add(this);
            currentUser = this;
            callback.done(null);
        }
    }

    public void saveInBackground(SaveCallback callback) {

    }

    public static void logOut() {
        currentUser = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static Query<User> getQuery() {
        Query<User> query = new Query<User>(User.class.getSimpleName());
        query.setDataSet(MockUsers.testUsers);
        return query;
    }

    public static void logInInBackground(String username, String password, LogInCallback callback) {
        boolean usernameExists = false;

        for (User user : MockUsers.testUsers) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                usernameExists = true;
                // check the password
                if (user.getPassword().equals(password)) {
                    currentUser = user;
                    callback.done(user, null);
                    return;
                }
                else {
                    callback.done(null, new Exception("The username or password you entered is incorrect."));
                }
            }
        }

        // No match
        callback.done(null, new Exception());
    }

    @Override
    public int compareTo(User another) {
        String compareUserName = ((User)another).getUsername();
        return this.getUsername().toLowerCase().compareTo(compareUserName.toLowerCase());
    }
}
