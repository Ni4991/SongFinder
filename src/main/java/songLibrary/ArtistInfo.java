package songLibrary;

/**
 * a class to store info for a single artist.
 * @author nluo
 *
 */
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
	
	/**
	 * getter for artist's name.
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * getter for artist's image(small).
	 * @return
	 */
	public String getImage() {
		return image;
	}
	
	/**
	 * getter for artist's bio.
	 * @return
	 */
	public String getBio() {
		return bio;
	}
	
	/**
	 * getter for artist's number of listeners.
	 * @return
	 */
	public int getListeners() {
		return listeners;
	}
	
	/**
	 * getter for artist's play count.
	 * @return
	 */
	public int getPlaycount() {
		return playcount;
	}
}
