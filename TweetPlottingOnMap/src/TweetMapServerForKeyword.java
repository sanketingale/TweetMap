import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;


public class TweetMapServerForKeyword extends HttpServlet {

	private static final long serialVersionUID = 10283173239L;

	public TweetMapServerForKeyword(){
		super();
	}
	
	public static void main(String[] args) throws ServletException, IOException{
		new TweetMapServerForKeyword().doPost(null, null);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			JSONObject json = new JSONObject();
			DBHelper db = DBHelper.getDBInstance();;
			JSONArray keywords = new JSONArray();
			HashSet<String> set = new HashSet<String>();
			for (String str : db.getListOfKeywords()) {
				if(!set.contains(str)){
					set.add(str);
					keywords.put(str);
				}
			}
			json.put("keywords", keywords);
			resp.setContentType("text/json");
			PrintWriter out = resp.getWriter();
			out.println(json.toString());
			out.flush();
			out.close();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}