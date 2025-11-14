package org.beaverbrooks.repository;

import org.beaverbrooks.domain.user.*;

import java.util.ArrayList;
import java.util.UUID;

public class DummyData {


    public static ArrayList<IUser> DummyUserData() {
        ArrayList<IUser> dummyUserData = new ArrayList<>();

        dummyUserData.add(new Customer("TestCustA", "John", "Test", UUID.fromString("e29b41d4-e89b-12d3-a456-426614174000")));
        dummyUserData.add(new Customer("TestCustB", "Mary", "Testing", UUID.fromString("e29b41d4-e89b-12d3-a456-426614174001")));
        dummyUserData.add(new Business("TestBusiness", UUID.fromString("e29b41d4-e89b-12d3-a456-426614174002")));

        return dummyUserData;
    }
    public static ArrayList<IUserAuth> DummyAuthData()
    {
        ArrayList<IUserAuth> dummyAuthData = new ArrayList<>();
        dummyAuthData.add(new UserAuth(UUID.fromString("e29b41d4-e89b-12d3-a456-426614174000"), "Pa55word!!123$1"));
        dummyAuthData.add(new UserAuth(UUID.fromString("e29b41d4-e89b-12d3-a456-426614174001"), "Pa55word!!123$2"));
        dummyAuthData.add(new UserAuth(UUID.fromString("e29b41d4-e89b-12d3-a456-426614174002"), "Pa55word!!123$3"));

        return dummyAuthData;
    }
}
