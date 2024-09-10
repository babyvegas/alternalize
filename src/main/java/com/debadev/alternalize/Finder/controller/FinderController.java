package com.debadev.alternalize.Finder.controller;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.debadev.alternalize.Finder.model.MusicAlternative;
import com.debadev.alternalize.Finder.model.SpotifyLinkRequest;
import com.debadev.alternalize.Finder.service.FinderService;

@RestController
@RequestMapping("/finder")
public class FinderController {
	
	private FinderService finderService;
	
	@Autowired
	public FinderController(FinderService finderService) {
		this.finderService = finderService;
	}
	
	@PostMapping("/find-alternatives")
	public ResponseEntity<List<MusicAlternative>> findAlternatives(@RequestBody SpotifyLinkRequest request) {
		String spotifyLink = request.getSpotifyLink();
		if (!isValidSpotifyUri(request.getSpotifyLink())) {
			return ResponseEntity.badRequest().build();
		}
		
	      String trackId = extractTrackId(spotifyLink);
	      List<MusicAlternative> alternatives = finderService.findAlternatives(trackId);
	      return ResponseEntity.ok(alternatives);

	}

	private String extractTrackId(String link) {
		String[] parts = link.split("/");
        String trackIdWithParams = parts[parts.length - 1];
        return trackIdWithParams.split("\\?")[0];
	}

	private boolean isValidSpotifyUri(String link) {
		
		String regex = "https://open\\.spotify\\.com/intl-[a-z]{2}/track/[a-zA-Z0-9]{22}(\\?si=[a-zA-Z0-9]+)?";
        return Pattern.matches(regex, link);
	}
	

}
