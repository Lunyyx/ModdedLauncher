package fr.lunyx.moddedlauncher;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class MojangLogin {
    public static void login() throws IOException {
        if(LauncherPanel.accInfos.isFile()) {
            Scanner myReader = new Scanner(LauncherPanel.accInfos);
            String accInfosJSONStr = null;
            while (myReader.hasNextLine()) {
                accInfosJSONStr = myReader.nextLine();
            }
            String accInfosArrStr = "[" + accInfosJSONStr + "]";
            System.out.println(accInfosArrStr);

            JSONArray accInfosArr = new JSONArray(accInfosArrStr);
            for(int i=0;i<accInfosArr.length();i++) {
                JSONObject jsonObject = accInfosArr.getJSONObject(i);
                String aToken = jsonObject.getString("aToken");
                String username = jsonObject.getString("username");
                String uuid = jsonObject.getString("uuid");
                LauncherPanel.status.setText("Connecté au serveur Mojang. Le jeu va se mettre à jour.");
                System.out.println("Connecté au serveur Mojang. Le jeu va se mettre à jour.");
                try {
                    Minecraft.update();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Minecraft.start(username, aToken, uuid);
            }
        } else {
            LauncherPanel.status.setText("Obtention des informations de votre compte...");
            System.out.println("Obtention des informations de votre compte...");
            URL authmojang = new URL("https://authserver.mojang.com/authenticate");
            HttpURLConnection req = (HttpURLConnection) authmojang.openConnection();
            req.setRequestMethod("POST");
            req.setRequestProperty("Content-Type", "application/json; utf-8");
            req.setRequestProperty("Accept", "application/json");
            req.setDoOutput(true);
            String inputString = "{\"agent\": {\"name\": \"Minecraft\", \"version\": 1}, \"username\": \"" + LauncherPanel.mailField.getText() + "\", \"password\": \"" + LauncherPanel.passwordField.getText() + "\"}";
            try(OutputStream os = req.getOutputStream()) {
                byte[] input = inputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(req.getInputStream(), StandardCharsets.UTF_8))) {
                String responseLine;
                LauncherPanel.status.setText("Informations reçues.");
                while ((responseLine = br.readLine()) != null)
                        getToken(responseLine);
                }
        }
    }

    public static void getToken(String json) {
        System.out.println("Informations reçues.");
        LauncherPanel.status.setText("Validation du token...");
        System.out.println("Validation du token...");
        String array = "[" + json + "]";
        JSONArray infos = new JSONArray(array);
        for(int i=0;i<infos.length();i++) {
            JSONObject jsonObject1 = infos.getJSONObject(i);
            String aToken = jsonObject1.getString("accessToken");
            String cToken = jsonObject1.getString("clientToken");
            JSONArray profile = jsonObject1.getJSONArray("availableProfiles");
            JSONObject jsonObject2 = profile.getJSONObject(0);
            String username = jsonObject2.getString("name");
            String uuid = jsonObject2.getString("id");
            try {
                URL authmojangval = new URL("https://authserver.mojang.com/validate");
                HttpURLConnection req = (HttpURLConnection) authmojangval.openConnection();
                req.setRequestMethod("POST");
                req.setRequestProperty("Content-Type", "application/json; utf-8");
                req.setRequestProperty("Accept", "application/json");
                req.setDoOutput(true);
                String inputString = "{\"accessToken\": \"" + aToken + "\", \"clientToken\": \"" + cToken + "\"}";
                try(OutputStream os = req.getOutputStream()) {
                    byte[] input = inputString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(req.getInputStream(), StandardCharsets.UTF_8))) {
                    String responseLine;
                    while ((responseLine = br.readLine()) != null);
                    if(req.getResponseCode() == 204 && responseLine == null) {
                        if(LauncherPanel.saveAcc.isSelected()) {
                            if(LauncherPanel.accInfos.createNewFile()) {
                                System.out.println("Fichier accInfos.json créé.");
                            }
                            FileWriter fw = new FileWriter(System.getenv("APPDATA") + "/.ModdedLauncher/accInfos.json", true); //the true will append the new data
                            PrintWriter writer = new PrintWriter(System.getenv("APPDATA") + "/.ModdedLauncher/accInfos.json");
                            writer.print("");
                            writer.close();
                            fw.write("{\"username\": \"" + username + "\", \"aToken\": \"" + aToken + "\", \"uuid\": \"" + uuid + "\"}");
                            fw.close();
                        }
                        LauncherPanel.status.setText("Token validé. Le jeu va se mettre à jour.");
                        System.out.println("Token validé. Le jeu va se mettre à jour.");
                        Minecraft.update();
                        Minecraft.start(username, aToken, uuid);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
