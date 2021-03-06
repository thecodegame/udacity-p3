package barqsoft.footballscores.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Amrendra Kumar on 28/01/16.
 */
public class LeagueLink {

    @SerializedName("href")
    String league;

    public int getLeagueId() {
        String leagueId = league.substring(league.lastIndexOf("/") + 1);
        return Integer.parseInt(leagueId);
    }

    public String getLeagueLink() {
        return league;
    }


    @Override
    public String toString() {
        return "LeagueLink[" + league + "]\n";
    }
}
