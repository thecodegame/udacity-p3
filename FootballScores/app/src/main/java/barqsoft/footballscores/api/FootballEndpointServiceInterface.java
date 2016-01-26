package barqsoft.footballscores.api;

import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;

/**
 * Created by Amrendra Kumar on 27/01/16.
 */
public interface FootballEndpointServiceInterface {
    @GET("/alpha/teams/{team_id}")
    void getTeamInfo(
            @Header("X-Auth-Token") String apiKey,
            @Path("team_id") String teamId
    );

    @GET("/alpha/teams/{team_id}/fixtures")
    void getTeamFixtures(
            @Header("X-Auth-Token") String apiKey,
            @Path("team_id") String teamId
    );

    @GET("/alpha/teams/{team_id}/players")
    void getTeamPlayers(
            @Header("X-Auth-Token") String apiKey,
            @Path("team_id") String teamId
    );
}
