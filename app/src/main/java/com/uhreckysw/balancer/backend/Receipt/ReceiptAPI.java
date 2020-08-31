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
        String receiptId = json.getString("receiptId");
        String shopName = json.getJSONObject("organization").getString("name").toLowerCase().replaceAll("[^\\w\\s]"," ");
        JSONArray items = json.getJSONArray("items");

        Object[] itemsForDb = new Object[items.length() * 3];
        int i = 0;
        for (int j = 0; j < items.length(); j++) {
            JSONObject item = items.getJSONObject(j);
            itemsForDb[i++] = item.getString("name").toLowerCase();
            itemsForDb[i++] = item.getDouble("price");
            itemsForDb[i++] = item.getInt("quantity");
        }

        String onlyOneItemName = "";
        if (itemsForDb.length == 3) {
            onlyOneItemName = ((String) itemsForDb[0]).toLowerCase().replaceAll("[^\\w\\s]"," ");
            onlyOneItemName = " (" + onlyOneItemName.substring(0, Math.min(onlyOneItemName.length(), 10)) + ")";
        }

        if (!db.createReceipt(receiptId, shopName, itemsForDb)) throw new JSONException("invalid receipt");
        db.ensureCategory(".qr");
        db.createPayment(
                shopName.substring(0, Math.min(shopName.length(), 10)) + onlyOneItemName,
                ((float) Math.round(json.getDouble("totalPrice") * 100)) / 100,
                DateCommon.parseDateGUI((json.getString("issueDate").split(" "))[0]),
                ".qr",
                "",
                receiptId
        );
    }
}
