package servlets;

public class ArtistInfo{
	
	private String name, bio, image;
	private int listeners, playcount;
	
	public ArtistInfo(String name, String bio, String image, int listeners, int playcount) {
		this.name = name;
		this.bio = bio;
		this.image = image;
		this.listeners = listeners;
		this.playcount = playcount;
	}
	
	public String getName() {
		return name;
	}
	
	public String getImage() {
		return image;
	}
	
	public String getBio() {
		return bio;
	}
	
	public int getListeners() {
		return listeners;
	}
	
	public int getPlaycount() {
		return playcount;
	}
	

}
