package com.debadev.alternalize.Finder.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import se.michaelthelin.spotify.SpotifyApi;

@Configuration
public class SpotifyConfig {
	

    private String clientId;

  
    private String clientSecret;

    @Bean
    public SpotifyApi spotifyApi() {
        return new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .build();
    }

}
