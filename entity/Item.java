package entity;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
// get a lot of items from response, but we only need a few of them, 
// make them into a class for java processing
public class Item {
	
	private String itemId;
	private String name;
	private double rating;
	private String address;
	private Set<String> categories;
	private String imageUrl;
	private String url;
	private double distance;
	
	//1, private because always use builder.build() to create Item instance
	//, public constructor is redundant
	//2, public constructor means more requirements from users are coming
	private Item(ItemBuilder builder) {
		this.itemId = builder.itemId;
		this.name = builder.name;
		this.rating = builder.rating;
		this.address = builder.address;
		this.categories = builder.categories;
		this.imageUrl = builder.imageUrl;
		this.url = builder.url;
		this.distance = builder.distance;
	}

	
	//1, builder pattern instead of 2^8 constructors
	//2, always used when setters are not allowed
	//3, static because it is used to create an instance
	public static class ItemBuilder {
		private String itemId;
		private String name;
		private double rating;
		private String address;
		private Set<String> categories;
		private String imageUrl;
		private String url;
		private double distance;
		
		public void setItemId(String itemId) {
			this.itemId = itemId;
		}
		public void setName(String name) {
			this.name = name;
		}
		public void setRating(double rating) {
			this.rating = rating;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public void setCategories(Set<String> categories) {
			this.categories = categories;
		}
		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public void setDistance(double distance) {
			this.distance = distance;
		}
		
		public Item build() {
			return new Item(this);
		}
	}
	
	public String getItemId() {
		return itemId;
	}
	public String getName() {
		return name;
	}
	public double getRating() {
		return rating;
	}
	public String getAddress() {
		return address;
	}
	public Set<String> getCategories() {
		return categories;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public String getUrl() {
		return url;
	}
	public double getDistance() {
		return distance;
	}
	
	//Item obj to json for frontend
	public JSONObject toJSONObject() {
			JSONObject obj = new JSONObject();
			try {
				obj.put("item_id", itemId);
				obj.put("name", name);
				obj.put("rating", rating);
				obj.put("address", address);
				obj.put("categories", new JSONArray(categories));
				obj.put("image_url", imageUrl);
				obj.put("url", url);
				obj.put("distance", distance);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return obj;
		}
	
	
}

