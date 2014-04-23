package wikipediaClassification;

import java.util.Set;

public class LinkedArticle {
	private String title;
	private Set<String> previous;
	private Set<String> following;

	public LinkedArticle(String title) {
		super();
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public Set<String> getPrevious() {
		return previous;
	}

	public Set<String> getFollowing() {
		return following;
	}
	
	
}
