package external;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;

// query with TicketMaster with java input and java return
public class TicketMasterAPI {
	private static final String URL = "https://app.ticketmaster.com/discovery/v2/events.json";
	private static final String DEFAULT_KEYWORD = ""; // no restriction
	private static final String API_KEY = "x4K89koGXpflAgKzXuoN309FTwxtp3OP";
	
	//input search keyword, long and lat
	public List<Item> search(double lat, double lon, String keyword) throws JSONException {
		if(keyword==null) {
			keyword=DEFAULT_KEYWORD;
		}
		
		try {
			//these vars need to be put in url, so space->%20
			keyword = URLEncoder.encode(keyword, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String geoHash = GeoHash.encodeGeohash(lat, lon, 8);

		// apikey=abcde&geoPoint=xyz123&keyword=&radius=50, i.e., translate to ticketmaster query
		String query = String.format("apikey=%s&geoPoint=%s&keyword=%s&radius=%s", API_KEY, geoHash, keyword, 50);

		String url = URL + "?"+query;
		
		try {
			//connect to url with method
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("GET");
			
			//check reponse code
			int responseCode = connection.getResponseCode();
			System.out.println("sending request to url:" +url);
			System.out.println("Response code:"+responseCode);
			
			if(responseCode!=200) {//300 multiple choice etc sometimes is also OKAY
				return new ArrayList<>();
			}
			 
			// I am the client, request is out, response is input, so use getInputStream
			BufferedReader reader= new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			// need to read line by line
			String line;
			StringBuilder response = new StringBuilder();
			
			while((line=reader.readLine())!=null) {
				response.append(line);
			}
			reader.close();
			
			//what we get is json in string, parse: string ->List<Items>
			JSONObject obj = new JSONObject(response.toString());
			
			//find what we need according to response structure in doc
			// here it is {_links:, _embedded:[events], page:}
			if(!obj.isNull("_embedded")){
				JSONObject embedded= obj.getJSONObject("_embedded");
				//events is still json, we need to convert each in it to item java obj
				return getItemList(embedded.getJSONArray("events"));
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return new ArrayList<>();
		
	}
	
	//helper methods to convert jsonobj to java obj
	
	// convert events array to list
	private List<Item> getItemList(JSONArray events) throws JSONException {
		List<Item> itemList = new ArrayList<>();
		
		// events is like [{},{},{}..]
		for(int i =0;i<events.length();++i) {
			JSONObject event = events.getJSONObject(i);
			ItemBuilder builder = new ItemBuilder();
			if (!event.isNull("id")) {
				builder.setItemId(event.getString("id"));
			}
			if (!event.isNull("name")) {
				builder.setName(event.getString("name"));
			}
			if (!event.isNull("url")) {
				builder.setUrl(event.getString("url"));
			}
			if (!event.isNull("distance")) {
				builder.setDistance(event.getDouble("distance"));
			}
			// in fact, there is no rating from tiketmastser
			if (!event.isNull("rating")) {
				builder.setRating(event.getDouble("rating"));
			}
			//setAddress() checked null itself
			builder.setAddress(getAddress(event));
			builder.setCategories(getCategories(event));
			builder.setImageUrl(getImageUrl(event));
			
			itemList.add(builder.build());
		}
		return itemList;
	}
	
	
	
	private Set<String> getCategories(JSONObject event) throws JSONException {
		Set<String> categories = new HashSet<>();
		if (!event.isNull("classifications")) {
			JSONArray classifications = event.getJSONArray("classifications");
			for (int i = 0; i < classifications.length(); ++i) {
				JSONObject classification = classifications.getJSONObject(i);
				if (!classification.isNull("segment")) {
					JSONObject segment = classification.getJSONObject("segment");
					if (!segment.isNull("name")) {
						categories.add(segment.getString("name"));
					}
				}
			}
		}
		return categories;
	}
	
	private String getImageUrl(JSONObject event) throws JSONException {
		if (!event.isNull("images")) {
			JSONArray array = event.getJSONArray("images");
			for (int i = 0; i < array.length(); ++i) {
				JSONObject image = array.getJSONObject(i);
				if (!image.isNull("url")) {
					return image.getString("url");
				}
			}
		}
		return "";
	}


	//find the final info from response structure
	private String getAddress(JSONObject event) throws JSONException {
		if(!event.isNull("_embedded")) {
			JSONObject embedded = event.getJSONObject("_embedded");
			if(!embedded.isNull("venues")) {
				JSONArray venues = embedded.getJSONArray("venues");
				for(int i=0;i<venues.length();++i) {
					JSONObject venue = venues.getJSONObject(i);
					StringBuilder addressBuilder = new StringBuilder();
					if(!venue.isNull("address")) {
						JSONObject address = venue.getJSONObject("address");
						if(!address.isNull("line1")) {
							addressBuilder.append(address.getString("line1"));
						}
						if(!address.isNull("line2")) {
							addressBuilder.append(","); //, or \n is for js
							addressBuilder.append(address.getString("line2"));
						}
						if(!address.isNull("line3")) {
							addressBuilder.append(","); 
							addressBuilder.append(address.getString("line3"));
						}
					}
					if(!venue.isNull("city")) {
						JSONObject city = venue.getJSONObject("city");
						if(!city.isNull("name")) {
							addressBuilder.append(","); 
							addressBuilder.append(city.getString("name"));
						}
					}
					String addressStr = addressBuilder.toString();
					if (!addressStr.equals("")) {
						return addressStr;
					}
				}
			}
		}
		return "";
	}

	
	//for test
	private void queryAPI(double lat, double lon) throws JSONException {
		List<Item> events = search(lat, lon, null);

		for (Item event : events) {
			System.out.println(event.toJSONObject());
		}
	}


	//for test only
	public static void main(String[] args) throws JSONException {
		TicketMasterAPI tmApi = new TicketMasterAPI();
		// Mountain View, CA
		// tmApi.queryAPI(37.38, -122.08);
		// London, UK
		// tmApi.queryAPI(51.503364, -0.12);
		// Houston, TX
		tmApi.queryAPI(29.682684, -95.295410);}

}
