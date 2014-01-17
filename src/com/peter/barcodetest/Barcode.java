package com.peter.barcodetest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class Barcode extends Activity implements OnClickListener {
	
	private Button scanBtn;
	private TextView formatTxt, contentTxt, upcTxt;
	private String apiKey = "d9c6149a623b7b5cf8c32152f1f78076";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_barcode);
		scanBtn = (Button)findViewById(R.id.scan_button);
		formatTxt = (TextView)findViewById(R.id.scan_format);
		contentTxt = (TextView)findViewById(R.id.scan_content);
		upcTxt = (TextView)findViewById(R.id.upc);
		scanBtn.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.barcode, menu);
		return true;
	}
	
	public void onClick(View v){
		//respond to clicks
		if(v.getId()==R.id.scan_button){
			//scan
			IntentIntegrator scanIntegrator = new IntentIntegrator(this);
			scanIntegrator.initiateScan();
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		//retrieve scan result
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanningResult != null) {
			//we have a result
			String scanResult = scanningResult.getContents();
			String scanFormat = scanningResult.getFormatName();
			formatTxt.setText("FORMAT: " + scanFormat);
			contentTxt.setText("CONTENT: " + scanResult);
			getUPCCode(scanResult);
		} else {
			Toast toast = Toast.makeText(getApplicationContext(), "No scan data received!", Toast.LENGTH_LONG);
			toast.show();
		}
	}
	
	private void getUPCCode(String resultCode) {
		String apiURL = "http://www.upcdatabase.org/api/json/" + apiKey + "/" + resultCode;
		JSONObject json = getJson(apiURL);
		// PUT IT INTO THE UPC TEXT VIEW
		// Get this to work
		/*String itemName = json.getString("itemname");
		upcTxt.setText("Item name: " + itemName);*/
	}

	public JSONObject getJson(String url){
		InputStream is = null;
		String result = "";
		JSONObject jsonObject = null;
		// HTTP
		try {	
			HttpClient httpclient = new DefaultHttpClient(); // for port 80 requests!
			HttpPost httppost = new HttpPost(url);
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		} catch(Exception e) {
			return null;
		}
		// Read response to string
		try {	
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();	
		} catch(Exception e) {
			return null;
		}
		 
		// Convert string to object
		try {
			jsonObject = new JSONObject(result);
		} catch(JSONException e) {
			return null;
		}
		return jsonObject;
	}

}
