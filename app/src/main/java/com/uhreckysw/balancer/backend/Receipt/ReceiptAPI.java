package com.uhreckysw.balancer.backend.Receipt;

import com.uhreckysw.balancer.backend.DateCommon;
import com.uhreckysw.balancer.backend.db.Database;
import com.uhreckysw.balancer.backend.db.Payment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class ReceiptAPI {
    private static final String ekasaURL = "https://ekasa.financnasprava.sk/mdu/api/v1/opd/receipt/find";
    private static Database db = null;

    public static String post(String id) throws IOException {
        StringBuilder result = new StringBuilder();
        HttpURLConnection conn = (HttpURLConnection) (new URL(ekasaURL).openConnection());
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setRequestProperty("Accept","application/json");
        conn.setDoOutput(true);
        conn.setDoInput(true);

        DataOutputStream os = new DataOutputStream(conn.getOutputStream());
        os.writeBytes(String.format("{\"receiptId\": \"%s\"}", id));
        os.flush();
        os.close();

        if (conn.getResponseCode() != 200) throw new IOException("Server responded with error " + conn.getResponseCode());
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) result.append(line);
        reader.close();
        return result.toString();
    }

    public static void createPayment(String jsonString) throws JSONException {
        if (db == null) db = Database.getInstance();
        JSONObject json = new JSONObject(jsonString).getJSONObject("receipt");
        String shopName = json.getJSONObject("organization").getString("name").toLowerCase().replaceAll("[^\\w\\s]"," ");
        JSONArray items = json.getJSONArray("items");
        String onlyOneItemName = "";
        if (items.length() == 1) {
            onlyOneItemName = items.getJSONObject(0).getString("name").toLowerCase().replaceAll("[^\\w\\s]"," ");
            onlyOneItemName = " (" + onlyOneItemName.substring(0, Math.min(onlyOneItemName.length(), 10)) + ")";
        }
        StringBuilder description = new StringBuilder(shopName).append("\n").append(json.getString("receiptId"));
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            description.append("\n").append(String.format(Locale.ENGLISH, "%s | %.02f | %d",
                    item.getString("name").toLowerCase(), item.getDouble("price"), item.getInt("quantity")));
        }

        db.ensureCategory(".qr");
        db.createPayment(new Payment()
                .setItem(shopName.substring(0, Math.min(shopName.length(), 10)) + onlyOneItemName)
                .setDate_of_buy(DateCommon.parseDateGUI((json.getString("issueDate").split(" "))[0]))
                .setPrice(((float) Math.round(json.getDouble("totalPrice") * 100)) / 100)
                .setDescription(description.toString())
                .setCategory(".qr"));
    }
}
