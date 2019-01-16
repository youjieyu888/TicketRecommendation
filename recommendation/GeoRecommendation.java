package recommendation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONException;

import db.DBConnectionFactory;
import db.DBconnection;
import db.mysql.MySQLConnection;
import entity.Item;

//recommendation with java input and return
public class GeoRecommendation {

	//connect to db, get user's favorite items and their categories, recommend the all nearby favorite categories
	public List<Item> recommendItems(String userId, double lat, double lon) throws JSONException {
		List<Item> recommendationItemsIds = new ArrayList<>();
		
		DBconnection connection =  DBConnectionFactory.getConnection();
		Set<String> favoritedItems = connection.getFavoriteItemIds(userId);
		
		Map<String, Integer> allCategories = new HashMap<>();
		for(String itemId : favoritedItems) {
			// each item has many categories
			Set<String> categories = connection.getCategories(itemId);
			for(String category : categories) {
				allCategories.put(category, allCategories.getOrDefault(category, 0)+1);
			}
		}
		
		//sort hashmap
		List<Entry<String, Integer>> categoryList = new ArrayList<>(allCategories.entrySet());
		Collections.sort(categoryList, (Entry<String, Integer> e1, Entry<String, Integer> e2)-> {
			return Integer.compare(e2.getValue(),e1.getValue());
		});
		
		//find all nearby items in those categories
		Set<String> visitedItemIds = new HashSet<>();
		for(Entry<String, Integer> category: categoryList) {
			List<Item> items = connection.searchItems(lat, lon, category.getKey());
			for(Item item:items) {
				// just recommend non-favorite with no dup
				if(!favoritedItems.contains(item.getItemId())&& !visitedItemIds.contains(item.getItemId())) {
					recommendationItemsIds.add(item);
					visitedItemIds.add(item.getItemId());
				}
			}
		}
		connection.close();
		return recommendationItemsIds;
	}
}
