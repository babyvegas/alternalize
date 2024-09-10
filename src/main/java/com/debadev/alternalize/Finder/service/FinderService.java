package com.debadev.alternalize.Finder.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.debadev.alternalize.Finder.model.MusicAlternative;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.Track;


@Service
public class FinderService {
	
	private final SpotifyAuthService spotifyAuthService;
	private final SpotifyApi spotifyApi;
	
	@Autowired
	public FinderService(SpotifyAuthService spotifyAuthService, SpotifyApi spotifyApi
			 ) {
		super();
		this.spotifyAuthService = spotifyAuthService;
		this.spotifyApi = spotifyApi;

	}

	public List<MusicAlternative> findAlternatives(String trackId) {
        Map<String, String> companyUrls = new HashMap<>();
        companyUrls.put("Spotify", "spotify.com");
        companyUrls.put("Apple Music", "music.apple.com");
        companyUrls.put("deezer", "deezer.com");
        companyUrls.put("amazon", "music.amazon");
        companyUrls.put("musixmatch", "musixmatch.com");
        companyUrls.put("soundcloud", "soundcloud.com");
        
		
		try {
			String accessToken = spotifyAuthService.getAccessToken();
			spotifyApi.setAccessToken(accessToken);
            Track track = spotifyApi.getTrack(trackId).build().execute();

            String trackName = track.getName();
            String artistName = track.getArtists()[0].getName();
            String track_query = trackName + " - " + artistName;
            List<Map<String, String>> results = googleSearch(track_query, companyUrls);

            
            List<MusicAlternative> alternatives = new ArrayList<>();
            for (Map<String, String> result : results) {
            	for (Map.Entry<String, String> entry : result.entrySet()) {
                    String source = entry.getKey();  // Ej. "youtube", "Spotify"
                    String url = entry.getValue();   // La URL asociada
                    // Crear un objeto MusicAlternative con esta información
                    alternatives.add(new MusicAlternative(source, trackName, artistName, url));
                }
            }
            
            return alternatives;
		} catch (Exception e) {
			throw new RuntimeException("Error al buscar alternativas para la cancion de Spotify", e);
		}
		

	}
	
	public List<Map<String, String>> googleSearch(String query, Map<String, String> companyUrls) {
		List<Map<String, String>> urls = new ArrayList<>();

        try {
            // Codifica la query para URL
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            String googleSearchUrl = "https://www.google.com/search?q=" + encodedQuery;

            // Realiza la solicitud HTTP
            URL url = new URL(googleSearchUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            connection.disconnect();

            // Parsear la respuesta HTML usando Jsoup
            Document doc = Jsoup.parse(content.toString());

            // Obtener todas las etiquetas <a> con atributos href y data-ved
            Elements anchorTags = doc.select("a[href][data-ved]");

            // Procesar los enlaces obtenidos
            List<String> linksList = new ArrayList<>();
            for (Element tag : anchorTags) {
                String href = tag.attr("href");
                if (href.startsWith("/url?q=")) {
                    // Eliminar '/url?q=' y '&sa=U&ved=' de la URL
                    String cleanLink = href.replace("/url?q=", "").split("&sa=U&ved=")[0];
                    linksList.add(java.net.URLDecoder.decode(cleanLink, "UTF-8"));
                }
            }

            // Filtrar enlaces de imágenes (imgres?imgurl=)
            linksList.removeIf(link -> link.contains("imgres?imgurl="));

            // Filtrar y guardar el primer enlace de YouTube
            List<String> youtubeLinks = new ArrayList<>();
            for (String link : linksList) {
                if (link.contains("youtube.com")) {
                    youtubeLinks.add(link);
                }
            }

            String youtubeLink = youtubeLinks.isEmpty() ? null : youtubeLinks.get(0);
            Map<String, String> youtubeMap = new HashMap<>();
            youtubeMap.put("youtube", youtubeLink);
            urls.add(youtubeMap);

            // Eliminar todos los enlaces de YouTube del resto de la lista
            linksList.removeIf(link -> link.contains("youtube.com"));

            // Filtrar los enlaces según las URLs de las compañías
            Set<String> seenLinks = new HashSet<>();
            for (String link : linksList) {
                for (Map.Entry<String, String> entry : companyUrls.entrySet()) {
                    String company = entry.getKey();
                    String companyUrl = entry.getValue();
                    if (link.contains(companyUrl) && !seenLinks.contains(link)) {
                        Map<String, String> companyMap = new HashMap<>();
                        companyMap.put(company, link);
                        urls.add(companyMap);
                        seenLinks.add(link);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return urls;
	}
	





}
