import health.database.DAO.BaseDAO;
import health.input.jsonmodels.JsonDataImport;
import health.input.jsonmodels.JsonDataPoints;
import health.input.jsonmodels.JsonDataValues;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import util.DateUtil;

import com.google.gson.Gson;

public class TestSendDatapoints {
	public static void main(String args[]) {
		try {
			String subjectID = "644";
			String dataStreamID = "75abb188-9c15-4333-b3bf-f80d5e8095ad";
			URL url = new URL(
					"http://146.169.35.28:55555/healthbook/v1/subjects/"
							+ subjectID + "/datastreams/" + dataStreamID
							+ "/datapoints");
			URLConnection connection = url.openConnection();
			connection.setDoOutput(true);

			connection.setDoInput(true);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Cache-Control", "no-cache");
			OutputStreamWriter out = new OutputStreamWriter(
					connection.getOutputStream());
			Gson gson = new Gson();
			BaseDAO dao = new BaseDAO();
			Hashtable<Long, String> tempList = new Hashtable<Long, String>();
			ArrayList<JsonDataPoints> datapoint_List = new ArrayList<JsonDataPoints>();
			
			Iterator<Entry<Long, String>> itrTemp = tempList.entrySet().iterator();
			while (itrTemp.hasNext()) {
				Entry<Long, String> entryV = itrTemp.next();
				// Date date=new Date();
				// date.setTime(entryV.getKey());
				JsonDataPoints point = new JsonDataPoints();
				point.setAt(Long.toString(entryV.getKey()));
				ArrayList<JsonDataValues> value_list = new ArrayList<JsonDataValues>();
				JsonDataValues value1 = new JsonDataValues();
				value1.setUnit_id("48aaed97-b78b-4817-96ad-71038483b57e");
				value1.setVal(entryV.getValue());

				value_list.add(value1);

				point.setValue_list(value_list);
				datapoint_List.add(point);
			}
			JsonDataImport importData = new JsonDataImport();
			importData.setData_points(datapoint_List);
			// importData.setBlock_id("8ad4c077-d30d-4a14-b882-0cbfcfb3e4ea");
			System.out.println(gson.toJson(importData));
			out.write(gson.toJson(importData));
			out.close();
			java.io.BufferedReader br = new java.io.BufferedReader(
					new java.io.InputStreamReader(connection.getInputStream()));
			java.lang.StringBuffer sb = new java.lang.StringBuffer();
			java.lang.String str = br.readLine();
			while (str != null) {
				sb.append(str);
				str = br.readLine();
			}
			br.close();
			java.lang.String responseString = sb.toString();
			HttpURLConnection httpConnection = (HttpURLConnection) connection;
			int code = httpConnection.getResponseCode();
			System.out.println("returncode:" + code);
			System.out.println(responseString);
			//
			// if(Bytes.toBytes(now.getTime())Bytes.toBytes(now2.getTime()))
			// {
			// System.out.println("smaller");
			// }
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
