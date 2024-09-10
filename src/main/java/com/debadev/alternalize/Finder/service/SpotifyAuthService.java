package com.debadev.alternalize.Finder.service;

import java.io.IOException;
import io.github.cdimascio.dotenv.Dotenv;
import java.text.ParseException;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

@Service
public class SpotifyAuthService {
	
	private final String clientId;
	private final String clientSecret;
	
	
		
	public SpotifyAuthService(String clientId, String clientSecret, SpotifyApi spotifyApi) {
		super();
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.spotifyApi = spotifyApi;
	}
	
	

	public SpotifyAuthService() {
		super();
		this.clientId = System.getenv("SPOTIFY_CLIENT_ID");
		this.clientSecret = System.getenv("SPOTIFY_CLIENT_SECRET");

	}



	private SpotifyApi spotifyApi;
    
    @PostConstruct
    public void init() {
        this.spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .build();
    }
    
    public String getAccessToken() {
        ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());
            return clientCredentials.getAccessToken();
        } catch (IOException | SpotifyWebApiException | org.apache.hc.core5.http.ParseException e) {
            throw new RuntimeException("Error obtaining Spotify access token", e);
        }
    }
    
    public CompletableFuture<String> getAccessTokenAsync() {
        ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();
        return clientCredentialsRequest.executeAsync()
                .thenApply(clientCredentials -> {
                    spotifyApi.setAccessToken(clientCredentials.getAccessToken());
                    return clientCredentials.getAccessToken();
                });
    }
}
