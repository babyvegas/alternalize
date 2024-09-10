package com.debadev.alternalize.Finder.model;

public class MusicAlternative {
	private String platform;
	private String title;
	private String artist;
	private String link;
	
	
	public MusicAlternative() {
		super();
	}


	public MusicAlternative(String platform, String title, String artist, String link) {
		super();
		this.platform = platform;
		this.title = title;
		this.artist = artist;
		this.link = link;
	}


	public String getPlatform() {
		return platform;
	}


	public void setPlatform(String platform) {
		this.platform = platform;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getArtist() {
		return artist;
	}


	public void setArtist(String artist) {
		this.artist = artist;
	}


	public String getLink() {
		return link;
	}


	public void setLink(String link) {
		this.link = link;
	}
	
	
	

}
