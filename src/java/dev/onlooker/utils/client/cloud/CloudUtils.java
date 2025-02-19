package dev.onlooker.utils.client.cloud;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.onlooker.Client;
import dev.onlooker.gui.notifications.NotificationManager;
import dev.onlooker.gui.notifications.NotificationType;
import dev.onlooker.utils.client.cloud.data.CloudData;
import dev.onlooker.utils.client.cloud.data.VoteType;
import dev.onlooker.utils.misc.FileUtils;
import dev.onlooker.utils.misc.NetworkingUtils;
import lombok.NonNull;
import lombok.SneakyThrows;

import javax.net.ssl.HttpsURLConnection;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class CloudUtils {

    public static String postOnlineConfig(String name, String description, String server, String data) {
        JsonObject response = Cloud.begin(RequestType.UPLOAD)
                .modifyGetMap((map) -> {
                    map.put("name", name);
                    map.put("meta", "script:false:version:" + Client.VERSION);
                    map.put("server", server);
                    //Char limit 240 for description
                    map.put("description", description);
                    map.put("published", "1");
                }).modifyPostMap((map) -> map.put("body", data)).post();

        if (response != null && response.has("configuration")) {
            JsonObject config = response.get("configuration").getAsJsonObject();
            return config.get("share_code").getAsString();
        }

        return null;
    }

    public static String postOnlineScript(String name, String description, String data) {
        JsonObject response = Cloud.begin(RequestType.UPLOAD)
                .modifyGetMap((map) -> {
                    map.put("name", name);
                    map.put("meta", "script:true:version:" + Client.VERSION);
                    //Char limit 240 for description
                    map.put("description", description);
                    map.put("published", "1");
                })
                .modifyPostMap((map) -> map.put("body", data))
                .post();

        if (response != null && response.has("configuration")) {
            System.out.println(response);
            JsonObject script = response.get("configuration").getAsJsonObject();
            return script.get("share_code").getAsString();
        }

        return null;
    }

    public static boolean updateData(String shareCode, String description, String data, boolean script) {
        JsonObject response = Cloud.begin(RequestType.UPDATE)
                .modifyGetMap((map) -> {
                    map.put("share_code", shareCode);
                    map.put("meta", "script:" + script + ":version:" + Client.VERSION);
                    //Char limit 240 for description
                    map.put("description", description);
                })
                .modifyPostMap((map) -> map.put("body", data))
                .post();

        if (response != null && response.has("configuration")) {
            return response.has("configuration");
        }

        return false;
    }

    public static JsonObject getData(String shareCode) {
        JsonObject response = Cloud.begin(RequestType.RETRIEVE)
                .modifyGetMap((map) -> map.put("share_code", shareCode))
                .post();


        if (response != null && response.has("configuration")) {
            return response.get("configuration").getAsJsonObject();
        }

        return null;
    }

    @SneakyThrows
    public static boolean deleteData(String shareCode) {
        JsonObject response = Cloud.begin(RequestType.DELETE)
                .modifyGetMap((map) -> map.put("share_code", shareCode))
                .post();

        if (response != null) {
            if (response.get("response").isJsonObject()) {
                String errorResponse = response.get("response").getAsJsonObject().get("error").getAsString();
                System.err.println(errorResponse);
                return false;
            } else {
                System.out.println(response);
                // Delete votes from voting API as well
                NetworkingUtils.bypassSSL();
                HttpsURLConnection connection = (HttpsURLConnection) new URL("https://senoe.win/tenacity/configs").openConnection();
                connection.setRequestProperty("User-Agent", "tenacity/" + Client.VERSION);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                
                JsonObject obj = new JsonObject();
                obj.addProperty("action", "delete");
                obj.addProperty("share_code", shareCode);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = new Gson().toJson(obj).getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                connection.connect();
                return true;
            }
        }
        return false;
    }

    public static JsonArray listAllData() {
        JsonObject response = Cloud.begin(RequestType.LIST).post();

        int pagesLeft = response.get("total_pages").getAsInt() - 1;
        JsonArray jsonArray = new JsonArray();
        if (response.has("configurations")) {
            jsonArray = response.get("configurations").getAsJsonArray();
        }

        if (pagesLeft > 0) {
            for (int i = 0; i < pagesLeft; i++) {
                int finalI = i + 2;
                JsonObject pageResponse = Cloud.begin(RequestType.LIST)
                        .modifyGetMap((map) -> map.put("page", String.valueOf(finalI)))
                        .post();
                jsonArray.addAll(pageResponse.get("configurations").getAsJsonArray());
            }
        }

        return jsonArray;
    }
    
    public static void vote(VoteType type, CloudData config) {
        try {
            if ((type == VoteType.UP && config.isUpvoted()) || (type == VoteType.DOWN && config.isDownvoted())) {
                type = VoteType.UNVOTE;
            }

            NetworkingUtils.bypassSSL();
            HttpsURLConnection connection = (HttpsURLConnection) new URL("https://senoe.win/tenacity/configs").openConnection();
            connection.setRequestProperty("User-Agent", "tenacity/" + Client.VERSION);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            JsonObject obj = new JsonObject();
            obj.addProperty("action", type.getActionName());
            obj.addProperty("share_code", config.getShareCode());

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = new Gson().toJson(obj).getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            connection.connect();
            NetworkingUtils.HttpResponse r = new NetworkingUtils.HttpResponse(FileUtils.readInputStream(
                    connection.getResponseCode() >= 400 ? connection.getErrorStream() : connection.getInputStream()), connection.getResponseCode());
            if (r.getResponse() == 200) {
                if (type == VoteType.UNVOTE) {
                    config.unvote();
                } else if (type == VoteType.UP) {
                    config.upvote();
                } else {
                    config.downvote();
                }
                Client.INSTANCE.getCloudDataManager().refreshVotes();
                //   NotificationManager.post(NotificationType.SUCCESS, "Cloud Configs",
                //         "Successfully " + type.getActionName() + "d \"" + config.getName() + "\"!", 1);
            } else {
                NotificationManager.post(NotificationType.DISABLE, "Cloud Configs",
                        "Failed to " + type.getActionName() + " \"" + config.getName() + "\".", 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    private static String md5(@NonNull String input) {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(input.getBytes());
        BigInteger hash = new BigInteger(1, md.digest());
        StringBuilder result = new StringBuilder(hash.toString(16));
        while (result.length() < 32) {
            result.insert(0, "0");
        }
        return result.toString();
    }

}
