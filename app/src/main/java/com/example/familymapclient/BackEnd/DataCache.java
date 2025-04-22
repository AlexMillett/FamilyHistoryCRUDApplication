package com.example.familymapclient.BackEnd;

import android.media.metrics.Event;

import java.util.List;
import java.util.Map;
import java.util.Set;

import model.AuthToken;
import model.Person;

public class DataCache {

    private static DataCache instance;

    private Map<String, model.Person> usersPeople;
    private Map<String, model.Event> usersEvents;
    private Map<String, List<Event>> personEvents;
    private Set<String> paternalAncestors;
    private Set<String> maternalAncestors;

    private AuthToken userAuthToken;

    private String authTokenString;

    public String getAuthTokenString() {
        return authTokenString;
    }

    public void setAuthTokenString(String authTokenString) {
        this.authTokenString = authTokenString;
    }

    public AuthToken getUserAuthToken() {
        return userAuthToken;
    }

    public void setUserAuthToken(AuthToken userAuthToken) {
        this.userAuthToken = userAuthToken;
    }

    public static DataCache getInstance() {
        if(instance == null);
        {
            instance = new DataCache();
        }
        return instance;
    }

    public Map<String, Person> getUsersPeople() {
        return usersPeople;
    }

    public void setUsersPeople(Map<String, Person> usersPeople) {
        this.usersPeople = usersPeople;
    }

    public Map<String, model.Event> getUsersEvents() {
        return usersEvents;
    }

    public void setUsersEvents(Map<String, model.Event> usersEvents) {
        this.usersEvents = usersEvents;
    }

    public Map<String, List<Event>> getPersonEvents() {
        return personEvents;
    }

    public void setPersonEvents(Map<String, List<Event>> personEvents) {
        this.personEvents = personEvents;
    }

    public Set<String> getPaternalAncestors() {
        return paternalAncestors;
    }

    public void setPaternalAncestors(Set<String> paternalAncestors) {
        this.paternalAncestors = paternalAncestors;
    }

    public Set<String> getMaternalAncestors() {
        return maternalAncestors;
    }

    public void setMaternalAncestors(Set<String> maternalAncestors) {
        this.maternalAncestors = maternalAncestors;
    }

    private DataCache(){
    }





}
