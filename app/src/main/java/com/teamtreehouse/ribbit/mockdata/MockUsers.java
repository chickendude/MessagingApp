package com.teamtreehouse.ribbit.mockdata;

import com.teamtreehouse.ribbit.models.User;

import java.util.ArrayList;

/**
 * Created by benjakuben on 10/12/16.
 */

public class MockUsers {

    public static final String TAG = MockUsers.class.getSimpleName();

    public static ArrayList<User> testUsers;

    public static void initialize() {
        testUsers = new ArrayList<User>();
        testUsers.add(new User("Ben", "test", "ben@teamtreehouse.com"));
        testUsers.add(new User("Sam", "test", "test@teamtreehouse.com"));
        testUsers.add(new User("Rachel", "test", "test@teamtreehouse.com"));
        testUsers.add(new User("Mike", "test", "test@teamtreehouse.com"));
        testUsers.add(new User("Pasan", "test", "pasan@teamtreehouse.com"));
        testUsers.add(new User("Jamele", "test", "test@teamtreehouse.com"));
        testUsers.add(new User("Muhammed", "test", "test@teamtreehouse.com"));
        testUsers.add(new User("Lisa", "test", "test@teamtreehouse.com"));
        testUsers.add(new User("Sun", "test", "test@teamtreehouse.com"));
        testUsers.add(new User("Maria", "test", "test@teamtreehouse.com"));
        testUsers.add(new User("bdeitch", "test", "bdeitch@teamtreehouse.com"));
    }

    public static void add(User user) {
        testUsers.add(user);
    }
}
