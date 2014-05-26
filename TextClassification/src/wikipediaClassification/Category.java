package wikipediaClassification;

/*
 * This class holds one category of Wikipedia
 */
public class Category {
	private int id; 		// internal id used by Wikipedia
	private int pages; 		// number of pages/subcategories below this category
	private String title; 	// category-title in Wikipedia
	private String parentCategoryTitle;	// title of the parent category
	public Category(int id, String title, String parentCategoryTitle, int pages) {
		super();
		this.title = title;
		this.id = id;
		this.pages = pages;
		this.parentCategoryTitle = parentCategoryTitle;
	}
	public String getParentCategoryTitle() {
		return parentCategoryTitle;
	}
	public void setParentCategoryTitle(String parentCategoryTitle) {
		this.parentCategoryTitle = parentCategoryTitle;
	}
	public Category(String title) {
		this.title = title;
	}
	public int getId() {
		return id;
	}
	public int getPages() {
		return pages;
	}
	public String getTitle() {
		return title;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setPages(int pages) {
		this.pages = pages;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@Override
	public String toString() {
		return "Category [id=" + id + ", pages=" + pages + ", title=" + title
				+ ", parentCategoryTitle=" + parentCategoryTitle + "]";
	}
}
