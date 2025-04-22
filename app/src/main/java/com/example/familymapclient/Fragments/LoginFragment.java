package com.example.familymapclient.Fragments;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.familymapclient.BackEnd.DataCache;
import com.example.familymapclient.BackEnd.Encoder;
import com.example.familymapclient.BackEnd.ServerProxy;
import com.example.familymapclient.MainActivity;
import com.example.familymapclient.R;
import com.google.gson.Gson;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import model.AuthToken;
import model.Event;
import model.Person;
import model.User;
import request.EventResult;
import request.LoginRequest;
import request.LoginResult;
import request.PersonResult;
import request.RegisterRequest;
import request.RegisterResult;



public class LoginFragment extends Fragment {

    private Listener listener;

    private static LoginRequest loginRequest;
    private static RegisterRequest registerRequest;

    private  EditText serverHost;
    private  EditText serverPort;
    private  EditText username;
    private  EditText password;
    private  EditText firstName;
    private  EditText lastName;
    private  EditText email;
    private RadioButton male;
    private RadioButton female;
    private RadioGroup genderGroup;
    private Button signInButton;
    private Button registerButton;

    private boolean genderIsChecked;


    private static String serverHostString;
    private static String serverPortString;
    private static String usernameString;
    private static String passwordString;
    private static String firstNameString;
    private static String lastNameString;
    private static String emailString;
    private static String genderString;



    private static final String LOGIN_SUCCESS_KEY = "loginSuccessKey";
    private static final String  LOGIN_CONTENTS_KEY = "loginContentsKey";
    private static final String  DATA_SUCCESS_KEY = "dataSuccessKey";
    private static final String AUTHTOKEN_KEY = "authTokenKey";
    private static final String  EVENTS_CONTENT_KEY = "eventsContentKey";
    private static final String  PEOPLE_CONTENT_KEY = "peopleContentKey";
    private static final String REGISTER_SUCCESS_KEY = "registerSuccessKey";





    public interface Listener
    {
        void notifyDone();
    }

    public void registerListener(Listener listener)
    {
        this.listener = listener;
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        serverHost = view.findViewById(R.id.serverHostField);
        serverPort = view.findViewById(R.id.serverPortField);
        username = view.findViewById(R.id.userNameField);
        password = view.findViewById(R.id.passwordField);
        firstName = view.findViewById(R.id.firstNameField);
        lastName = view.findViewById(R.id.lastNameField);
        email = view.findViewById(R.id.emailAddressField);
        male = view.findViewById(R.id.maleRadioField);
        female = view.findViewById(R.id.femaleRadioField);
        genderGroup = view.findViewById(R.id.genderRadioGroupField);
        signInButton = view.findViewById(R.id.signInButton);
        registerButton = view.findViewById(R.id.registerButton);

        signInButton.setEnabled(false);
        registerButton.setEnabled(false);


        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i)
            {
                genderIsChecked = true;
            }
        });

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkFields();
            }
        };
        serverHost.addTextChangedListener(watcher);
        serverPort.addTextChangedListener(watcher);
        username.addTextChangedListener(watcher);
        password.addTextChangedListener(watcher);
        firstName.addTextChangedListener(watcher);
        lastName.addTextChangedListener(watcher);
        email.addTextChangedListener(watcher);





        signInButton.setOnClickListener((new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                   getLoginRequest(view);

                    Handler signInMessageHandler = new Handler(Looper.getMainLooper())
                    {
                        @Override
                        public void handleMessage(Message message)
                        {
                            Bundle bundle = message.getData();
                            Boolean loginBoolean = bundle.getBoolean(LOGIN_SUCCESS_KEY);
                            String loginMessage;

                            if(loginBoolean)
                            {
                                Boolean dataBoolean = bundle.getBoolean(DATA_SUCCESS_KEY);
                                if(dataBoolean)
                                {
                                    loginMessage = "User Logged In";
                                    Toast.makeText(getActivity(),loginMessage,Toast.LENGTH_LONG).show();
                                    listener.notifyDone();

                                }
                            }
                            else
                            {
                                loginMessage = "Login Failed";
                                Toast t = Toast.makeText(getActivity(),loginMessage,Toast.LENGTH_LONG);
                                t.show();
//                                t.cancel();
                            }

                        }
                    };

                    LoginTask task = new LoginTask(signInMessageHandler,loginRequest,serverHost.getText().toString(),serverPort.getText().toString());
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.submit(task);


            }
        }) );

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                getRegisterRequest(view);

                Handler registerMessageHandler = new Handler(Looper.getMainLooper())
                {
                    @Override
                    public void handleMessage(@NonNull Message message) {
                        Bundle bundle = message.getData();
                        Boolean registerBoolean = bundle.getBoolean(REGISTER_SUCCESS_KEY);
                        String registerMessage;
                    }
                };




                if(listener != null)
                {

                    listener.notifyDone();
                }
            }

        });

        return view;
    }


    private static class LoginTask implements Runnable {
        private final Handler messageHandler;
        private final LoginRequest request;
        private String serverHostString;
        private String serverPortString;

        public LoginTask(Handler messageHandler, LoginRequest loginRequest, String serverHost, String serverPort) {
            this.messageHandler = messageHandler;
            this.request = loginRequest;
            this.serverHostString = serverHost;
            this.serverPortString = serverPort;
        }

        @Override
        public void run() {
            ServerProxy proxy = new ServerProxy();
            LoginResult result = proxy.login(this.request, this.serverHostString, this.serverPortString);
            sendMessage(result);
        }

        private void sendMessage(LoginResult result) {
            Message message = Message.obtain();

            Gson gson = new Gson();
            Bundle messageBundle = new Bundle();

            if (!result.getSuccess())
            {
                messageBundle.putBoolean(LOGIN_SUCCESS_KEY, false);
            }
            else
            {
                messageBundle.putBoolean(LOGIN_SUCCESS_KEY, true);
                messageBundle.putString(LOGIN_CONTENTS_KEY, result.getAuthtoken());
            }
            message.setData(messageBundle);
            messageHandler.sendMessage(message);

            if(result.getSuccess())
            {
                DataTask dataTask = new DataTask(this.messageHandler,result.getPersonID(),result.getAuthtoken(),this.serverHostString,this.serverPortString);
                dataTask.run();
            }


        }


    }


    private static class DataTask implements Runnable{

        private final Handler messageHandler;
        private String serverHost;
        private String serverPort;
        private String personIDofUser;
        private String authToken;

        public DataTask(Handler messageHandler, String personID, String authToken, String host, String port )
        {
            this.messageHandler = messageHandler;
            this.serverHost = serverHost;
            this.serverPort = serverPort;
            this.personIDofUser = personID;
            this.authToken = authToken;

        }

        @Override
        public void run()
        {
            ServerProxy proxy = new ServerProxy();
            PersonResult peopleResult =  proxy.getPeople(authToken,serverHost,serverPort);
            EventResult eventResult = proxy.getEvents(authToken,serverHost,serverPort);
            sendMessage(peopleResult,eventResult);
        }

        private void sendMessage(PersonResult personResult, EventResult eventResult) {
            Message message = Message.obtain();

            Gson gson = new Gson();
            Bundle messageBundle = new Bundle();

            if (personResult.getSuccess() && eventResult.getSuccess())
            {
                messageBundle.putBoolean(DATA_SUCCESS_KEY, true);
                messageBundle.putString(PEOPLE_CONTENT_KEY, gson.toJson(personResult));
                messageBundle.putString(EVENTS_CONTENT_KEY, gson.toJson(eventResult));
                messageBundle.putString(AUTHTOKEN_KEY, authToken);
                initializeDataCache(personResult,eventResult,personIDofUser,authToken);
            }
            else
            {
                messageBundle.putBoolean(DATA_SUCCESS_KEY, false);
            }
            message.setData(messageBundle);
            messageHandler.sendMessage(message);
        }

    }

    private static void initializeDataCache(PersonResult personResult, EventResult eventResult, String personIDofUser, String authTokenString)
    {
        DataCache dataCache = DataCache.getInstance();
        List<Person> people = Arrays.asList(personResult.getData());
        List<Event> events = Arrays.asList(eventResult.getData());


        initializeUserData(people,events,dataCache,authTokenString);
    }

    private static void initializeUserData(List<Person> people, List<Event> events,DataCache cache, String authTokenString)
    {
        Map<String,Person> userPeople = new HashMap<>();
        Map<String, Event> userEvents = new HashMap<>();

        for(Person person : people)
        {
            userPeople.put(person.getPersonID(),person);
        }

        for(Event event : events)
        {
            userEvents.put(event.getEventID(),event);
        }

        AuthToken theAuthToken = new AuthToken(authTokenString,usernameString);

        cache.setUsersPeople(userPeople);
        cache.setUsersEvents(userEvents);
        cache.setUserAuthToken(theAuthToken);
        cache.setAuthTokenString(theAuthToken.getAuthToken());
    }

        private void getLoginRequest(View view) {
            LoginRequest loginRequest = new LoginRequest(username.getText().toString(), password.getText().toString());
            this.loginRequest = loginRequest;
        }

        private void getRegisterRequest(View view) {

            RegisterRequest request;

                request = new RegisterRequest(username.toString(), password.toString(), email.toString(),
                        firstName.toString(), lastName.toString(), genderString);

            this.registerRequest = request;
        }

        private void checkFields()
        {
            serverHostString = serverHost.toString();
            serverPortString = serverPort.toString();
            usernameString = username.toString();
            passwordString = password.toString();
            firstNameString = firstName.toString();
            lastNameString = lastName.toString();
            emailString = email.toString();

            if(genderIsChecked)
            {
                if(male.isChecked())
                {
                    genderString = "m";
                }
                else
                {
                    genderString = "f";
                }
            }

            signInButton.setEnabled(!serverHost.toString().isEmpty() && !serverPort.toString().isEmpty() && !username.toString().isEmpty() && !password.toString().isEmpty());

            registerButton.setEnabled(!serverHost.toString().isEmpty() && !serverPort.toString().isEmpty() && !username.toString().isEmpty() && !password.toString().isEmpty() &&
                    !firstName.toString().isEmpty() && !lastName.toString().isEmpty() && !email.toString().isEmpty() && genderIsChecked);

        }


    }
