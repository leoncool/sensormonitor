import health.database.DAO.BaseDAO;
import health.input.jsonmodels.JsonDataImport;
import health.input.jsonmodels.JsonDataPoints;
import health.input.jsonmodels.JsonDataValues;
import health.input.jsonmodels.singleunitstream.JsonSingleDataPoints;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;

import util.DateUtil;

import com.google.gson.Gson;

public class TestSendDatapoints {
	public synchronized void PostData(String datastreamID_,LinkedList<DataWrap> table) throws IOException
	{
				String subjectID = "644";
		String dataStreamID = datastreamID_;
		URL url = new URL(
				"http://146.169.35.28:55555/healthbook/v1/defaultsubject/datastreams/"
						+ dataStreamID + "/datapoints");
		URLConnection connection = url.openConnection();
		connection.setDoOutput(true);

		connection.setDoInput(true);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("Cache-Control", "no-cache");
		OutputStreamWriter out = new OutputStreamWriter(
				connection.getOutputStream());
		Gson gson = new Gson();
		BaseDAO dao = new BaseDAO();

		ArrayList<JsonSingleDataPoints> value_list = new ArrayList<JsonSingleDataPoints>();
		for(int i=0;i<table.size();i++)
		{
			DataWrap wrap=table.pollFirst();
			if(wrap!=null)
			{
				JsonSingleDataPoints value = new JsonSingleDataPoints();
				value.setVal(wrap.value);
				value.setAt(Long.toString(wrap.date.getTime()));
				value_list.add(value);
			}
		}
		JsonDataImport importData = new JsonDataImport();
		importData.setData_points_single_list(value_list);
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
	}
	public static void main(String args[]) {
		try {
			String subjectID = "644";
			String dataStreamID = "feb6f678-a518-4d57-b0c3-f8ae34e22354";
			URL url = new URL(
					"http://146.169.35.28:55555/healthbook/v1/defaultsubject/datastreams/"
							+ dataStreamID + "/datapoints");
			URLConnection connection = url.openConnection();
			connection.setDoOutput(true);

			connection.setDoInput(true);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Cache-Control", "no-cache");
			OutputStreamWriter out = new OutputStreamWriter(
					connection.getOutputStream());
			Gson gson = new Gson();
			BaseDAO dao = new BaseDAO();

			ArrayList<JsonSingleDataPoints> value_list = new ArrayList<JsonSingleDataPoints>();
			JsonSingleDataPoints value1 = new JsonSingleDataPoints();

			value1.setVal("25");
			value1.setAt(Long.toString(new Date().getTime()));
			value_list.add(value1);
			JsonDataImport importData = new JsonDataImport();
			importData.setData_points_single_list(value_list);
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
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
