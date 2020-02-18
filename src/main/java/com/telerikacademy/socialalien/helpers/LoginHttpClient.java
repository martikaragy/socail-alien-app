package com.telerikacademy.socialalien.helpers;

import com.telerikacademy.socialalien.exceptions.EntityNotFoundException;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginHttpClient {

    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    public LoginHttpClient(){}

    public void close() throws IOException {
        httpClient.close();
    }

    public void sendPost(String username, String password) throws Exception {
        System.out.println(username);
        System.out.println(password);
        HttpPost requestUrl = new HttpPost("http://localhost:8080/authenticate");

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("username",username));
        urlParameters.add(new BasicNameValuePair("password", password));

        requestUrl.setEntity(new UrlEncodedFormEntity(urlParameters));

        CloseableHttpResponse response = httpClient.execute(requestUrl);


        System.out.println(response.getStatusLine().getStatusCode());
        String redirectLocation = response.getAllHeaders()[7].toString();
        if(!redirectLocation.equals("Location: http://localhost:8080/")){
            throw new EntityNotFoundException("Login failed");
        }

        httpClient.close();

    }







}
