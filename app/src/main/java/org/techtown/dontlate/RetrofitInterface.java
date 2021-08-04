package org.techtown.dontlate;

import org.techtown.dontlate.model.CoordRegionInfo;
import org.techtown.dontlate.model.SearchingAddress;
import org.techtown.dontlate.model.TranslateAddress;
import org.techtown.dontlate.model.TranslateCoord;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface RetrofitInterface {

    @GET("v2/local/search/address.json")
    Call<SearchingAddress> AddressInfo(@Header("Authorization") String authorization, @Query("query") String query);

    @GET("v2/local/geo/coord2regioncode.json")
    Call<CoordRegionInfo> RegionInfo(@Header("Authorization") String authorization, @Query("x") String x, @Query("y") String y);

    @GET("v2/local/geo/coord2address.json")
    Call<TranslateAddress> CoordInfo(@Header("Authorization") String authorization, @Query("x") String x, @Query("y") String y);

    @GET("v2/local/geo/transcoord.json")
    Call<TranslateCoord> Coordxyinfo(@Header("Authorization") String authorization, @Query("x") Double x, @Query("y") Double y,@Query("input_coord") String input_coord, @Query("output_coord") String output_coord);


}
