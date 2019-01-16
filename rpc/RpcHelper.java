package rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.json.JSONObject;


public class RpcHelper {
	// Writes a JSONArray to http response.
	public static void writeJsonArray(HttpServletResponse response, JSONArray array) throws IOException{
		response.setContentType("application/json");//encode type to client
		response.setHeader("Access-Control-Allow-Origin", "*");//no restriction *
		PrintWriter out = response.getWriter();//write to user
		out.print(array);
		out.close(); 
	}

    // Writes a JSONObject to http response.
	public static void writeJsonObject(HttpServletResponse response, JSONObject obj) throws IOException {		
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");//no restriction *
		PrintWriter out = response.getWriter();
		out.print(obj);
		out.close(); 
	}
	
	//read from request form frontend, string->json
	public static JSONObject readJSONObject(HttpServletRequest request) {
		StringBuilder sBuilder = new StringBuilder();
		//save request body into bufferreader
		try (BufferedReader reader = request.getReader()){
			String line = null;
			while((line=reader.readLine())!=null) {
				sBuilder.append(line);
			}
			return new JSONObject(sBuilder.toString());
		}catch(Exception e) {
			e.printStackTrace();
		}
		return new JSONObject();
	}

}
