package fr.lunyx.moddedlauncher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.json.JSONArray;
import org.json.JSONObject;

public class MojangStatus extends JFrame {
    public static void getStatus() {
        try {
            URL mojang = new URL("https://status.mojang.com/check");
            HttpURLConnection req = (HttpURLConnection) mojang.openConnection();
            req.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(req.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                jsonConvert(inputLine);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void jsonConvert(String json) {
        JSONArray jsonArray = new JSONArray(json);
        StringBuilder finalStatus = new StringBuilder();

        for(int i=0;i<jsonArray.length();i++) {
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            String status = null;
            for(String key : jsonObject1.keySet()){
                if (jsonObject1.get(key).equals("green")) {
                    status = "<span style='color: green;'>En ligne</span>";
                } else if(jsonObject1.get(key).equals("yellow")) {
                    status = "<span style='color: orange;'>Maintenance</span>";
                } else if(jsonObject1.get(key).equals("red")) {
                    status = "<span style='color: red;'>Hors ligne</span>";
                }
                finalStatus.append("<p style='font-size: 10px;'><b>").append(key).append("</b>").append(" = ").append(status).append("</p><br>");
            }
        }
        String msg = "<html>" + finalStatus + "</html>";
        JLabel label = new JLabel(msg);
        JOptionPane.showMessageDialog(new JFrame(), label, "Status des serveurs Mojang", JOptionPane.INFORMATION_MESSAGE);
    }
}
