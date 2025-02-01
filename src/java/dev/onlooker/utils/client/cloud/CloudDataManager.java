package dev.onlooker.utils.client.cloud;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.onlooker.Client;
import dev.onlooker.gui.notifications.NotificationManager;
import dev.onlooker.gui.notifications.NotificationType;
import dev.onlooker.gui.clickguis.sidegui.utils.CloudDataUtils;
import dev.onlooker.utils.client.ReleaseType;
import dev.onlooker.utils.client.cloud.data.CloudConfig;
import dev.onlooker.utils.client.cloud.data.CloudScript;
import dev.onlooker.utils.client.cloud.data.Votes;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class CloudDataManager {

    private final List<CloudConfig> cloudConfigs = new ArrayList<>();
    private final List<CloudScript> cloudScripts = new ArrayList<>();
    private boolean refreshing = false;

    public void refreshData() {
        refreshing = true;
        Client.INSTANCE.getSideGui().getTooltips().clear();
        cloudConfigs.clear();
        cloudScripts.clear();

        refreshVotes();

        JsonArray dataArray = CloudUtils.listAllData();

        if (dataArray == null || dataArray.size() == 0) {
            System.err.println("Null or no data found in cloud");
            NotificationManager.post(NotificationType.DISABLE, "Cloud Configs", "Failed to retrieve config data");
            return;
        }

        for (JsonElement element : dataArray) {
            JsonObject data = element.getAsJsonObject();

            String name = data.get("name").getAsString();
            String description = data.get("description").getAsString();
            String shareCode = data.get("share_code").getAsString();
            String author = data.get("author_username").getAsString();
            // Format is script:true:version:5.0
            String[] meta = data.get("meta").getAsString().split(":");
            String lastUpdated = data.get("edit_epoch").getAsString();
            String server = data.get("server").getAsString();
            boolean ownership = data.get("mine").getAsBoolean() || Client.RELEASE.equals(ReleaseType.PUBLIC);

            boolean script = Boolean.parseBoolean(meta[1]);
            String version = meta[3];

            if (script) {
                cloudScripts.add(new CloudScript(name, description, shareCode, author, version, lastUpdated, ownership));
            } else {
                cloudConfigs.add(new CloudConfig(name, description, shareCode, author, version, lastUpdated, server, ownership));
            }
        }

        applyVotes();

        //Refresh cloud config and cloud script gui
        CloudDataUtils.refreshCloud();

        refreshing = false;
    }

    private final Map<String, Votes> voteMap = new HashMap<>();
    private final Map<String, Boolean> userVoteMap = new HashMap<>();
    private boolean refreshedVotes = false;

    public void refreshVotes() {
        voteMap.clear();
        userVoteMap.clear();
        refreshedVotes = true;
    }

    public void applyVotes() {
        if (refreshedVotes) {
            cloudConfigs.forEach(c -> {
                String shareCode = c.getShareCode();
                Votes v = voteMap.get(shareCode);
                if (v != null) {
                    c.setVotes(v);
                    if (userVoteMap.containsKey(shareCode)) {
                        c.forceSet(userVoteMap.get(shareCode));
                    }
                } else {
                    c.setVotes(new Votes(0, 0));
                }
            });

            cloudScripts.forEach(c -> {
                String shareCode = c.getShareCode();
                Votes v = voteMap.get(shareCode);
                if (v != null) {
                    c.setVotes(v);
                    if (userVoteMap.containsKey(shareCode)) {
                        c.forceSet(userVoteMap.get(shareCode));
                    }
                } else {
                    c.setVotes(new Votes(0, 0));
                }
            });


            refreshedVotes = false;
        }
    }

}
