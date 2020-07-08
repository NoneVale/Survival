package net.nighthawkempires.survival.user;

import com.google.common.collect.Maps;
import net.nighthawkempires.core.datasection.DataSection;
import net.nighthawkempires.core.datasection.Model;
import net.nighthawkempires.survival.SurvivalPlugin;

import java.util.Map;
import java.util.UUID;

public class UserModel implements Model {

    private String key;

    private int kills;
    private int deaths;

    public UserModel(UUID uuid) {
        this.key = uuid.toString();

        this.kills = 0;
        this.deaths = 0;
    }

    public UserModel(String key, DataSection data) {
        this.key = key;

        this.kills = data.getInt("kills");
        this.deaths = data.getInt("deaths");
    }

    public int getKills() {
        return this.kills;
    }

    public void setKills(int kills){
        this.kills = kills;
        SurvivalPlugin.getUserRegistry().register(this);
    }

    public void addKill() {
        setKills(getKills() + 1);
    }

    public int getDeaths() {
        return this.deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
        SurvivalPlugin.getUserRegistry().register(this);
    }

    public void addDeath() {
        setDeaths(getDeaths() + 1);
    }

    public double getRatio() {
        if (kills == 0 || deaths == 0) return  -1;
        return ((double) kills) / ((double) deaths);
    }

    public String getKey() {
        return this.key;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();

        map.put("kills", kills);
        map.put("deaths", deaths);

        return map;
    }
}