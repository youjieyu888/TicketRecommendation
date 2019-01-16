package rpc;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import db.DBConnectionFactory;
import db.DBconnection;
import db.mysql.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import external.TicketMasterAPI;

/**
 * Servlet implementation class SearchItem
 */
@WebServlet("/search")
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */ 
    // find the items with keyword from ticketmaster, but update with fav attribute from db
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		// TODO Auto-generated method stub
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		
		String term = request.getParameter("term");
		String userId = request.getParameter("user_id");

		DBconnection connection = DBConnectionFactory.getConnection();
		
		try {
			List<Item> items = connection.searchItems(lat, lon, term);
			Set<String> favoritedItemIds = connection.getFavoriteItemIds(userId);
			
			JSONArray array = new JSONArray();
			for (Item item : items) {
				//every time user is back to recommend page, reload query and 
				// send the favorite data in db to frontend
				JSONObject obj = item.toJSONObject();
				obj.put("favorite", favoritedItemIds.contains(item.getItemId()));
				array.put(obj);
			}
			RpcHelper.writeJsonArray(response, array);
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(connection != null) {
				connection.close();
				}
			
		}
			
			
		
//		TicketMasterAPI tmAPI = new TicketMasterAPI();
//		List<Item> items;
//		try {
//			items = tmAPI.search(lat, lon, null);
//			JSONArray array = new JSONArray();
//			for (Item item : items) {
//				array.put(item.toJSONObject());
//			}
//			RpcHelper.writeJsonArray(response, array);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
