package rpc;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnectionFactory;
import db.DBconnection;
import entity.Item;

/**
 * Servlet implementation class ItemHistory
 */
@WebServlet("/history")
public class ItemHistory extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ItemHistory() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
    
    // get: get items in myfav page, write to front end
    // request: read from tomcat
    // response: write to tomcat, set status and write output
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String userId = request.getParameter("user_id");
		JSONArray array = new JSONArray();
		
		DBconnection conn = DBConnectionFactory.getConnection();
		try {
			// get all fav items from this user
			// but what we get from DB is java obj, need to convert to json to frontend
			Set<Item> items = conn.getFavoriteItems(userId);
			for (Item item : items) {
				JSONObject obj = item.toJSONObject();
				obj.append("favorite", true);
				array.put(obj);
			}
			
			RpcHelper.writeJsonArray(response, array);
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}

	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	//post: user sets fav item, we need to write to db
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		DBconnection connection = DBConnectionFactory.getConnection();
		//string-> json obj, we need readJSONObject now cuz client send json info explicitly
		JSONObject input = RpcHelper.readJSONObject( request);
		String userId;
		try {
			//parse json to get what to write to db
			userId = input.getString("user_id");
			JSONArray array = input.getJSONArray("favorite");
			List<String> itemIds = new ArrayList<>();
			for(int i=0; i<array.length();i++) {
				itemIds.add(array.getString(i));
			}
			connection.setFavoriteItems(userId, itemIds);
			RpcHelper.writeJsonObject(response,  new JSONObject().put("result", "SUCCESS"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(connection !=null) connection.close();
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	//delete: user unset fav item, we need to delete from db
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBconnection connection = DBConnectionFactory.getConnection();
		JSONObject input = RpcHelper.readJSONObject(request);
		String userId;
		try {
			userId = input.getString("user_id");
			JSONArray array = input.getJSONArray("favorite");
			List<String> itemIds = new ArrayList<>();
			for(int i=0; i<array.length();i++) {
				itemIds.add(array.getString(i));
			}
			connection.unsetFavoriteItems(userId, itemIds);
			RpcHelper.writeJsonObject(response,  new JSONObject().put("result", "SUCCESS"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(connection !=null) connection.close();
		}

		
	}

}
