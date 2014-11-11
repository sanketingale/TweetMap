package com;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;

import com.DBHelper;
import com.TweetNode;

public class TweetMapServer extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args){
		DBHelper db = new DBHelper();
		TweetNode node = new TweetNode(2, "vinayak", "sometext", 
				38.898556, -77.037852, new java.util.Date());
		db.insertTweetIntoDB(node);
		for(TweetNode n : db.getAllTweetsFromDB()){
			System.out.println(n);
		}
		db.close();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println("Got the request");
		try {
			JSONArray array = new JSONArray();
			DBHelper db = new DBHelper();
			List<TweetNode> list = db.getAllTweetsFromDB();
			for (TweetNode node : list) {
				array.put(node.getValue());
			}
			JSONObject json = new JSONObject();
			json.put("latlon", array);
			resp.setContentType("text/json");
			resp.getWriter().println(json.toString());
			resp.flushBuffer();
			db.close();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		super.doGet(req, resp);
	}
}