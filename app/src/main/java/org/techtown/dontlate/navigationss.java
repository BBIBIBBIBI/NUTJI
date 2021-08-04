package org.techtown.dontlate;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;


import org.techtown.dontlate.model.CoordRegionInfo;
import org.techtown.dontlate.model.SearchingAddress;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class navigationss extends Fragment {


    private View view;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    private RetrofitClient retrofitClient;
    private RetrofitInterface retrofitInterface;

    private String API_KEY = "KakaoAK 8ae57c6ab583cbe890979e12b4c0315a";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.navigations, container, false);

        MapView mapView = new MapView(getActivity());

        ViewGroup mapViewContainer = (ViewGroup) v.findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);


        callNaviItems();
        callAddressItems();


        // 중심점 변경 - 예제 좌표는 서울 남산
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.54892296550104, 126.99089033876304), true);

        // 줌 레벨 변경
        mapView.setZoomLevel(4, true);

        //마커 찍기
        MapPoint MARKER_POINT = MapPoint.mapPointWithGeoCoord(37.54892296550104, 126.99089033876304);//
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName("요기");
        marker.setTag(0);
        marker.setMapPoint(MARKER_POINT);
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.


        mapView.addPOIItem(marker);


        return v;
    }

    public void callNaviItems() {
        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();
        retrofitInterface.RegionInfo(API_KEY, "127.10459896729914","37.40269721785548" ).enqueue(new Callback<CoordRegionInfo>() {
            @Override
            public void onResponse(Call<CoordRegionInfo> call, Response<CoordRegionInfo> response) {
                CoordRegionInfo coordRegionInfo = response.body();
                Log.d("test", coordRegionInfo.getCoordRegionInfoItems().get(0).getAddressName());
            }

            @Override
            public void onFailure(Call<CoordRegionInfo> call, Throwable t) {
                Log.d("test", t.toString());
            }
        });
    }

    public void callAddressItems() {
        retrofitClient = RetrofitClient.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitInterface();
        retrofitInterface.AddressInfo(API_KEY, "서울특별시 강동구 풍성로 128").enqueue(new Callback<SearchingAddress>() {
            @Override
            public void onResponse(Call<SearchingAddress> call, Response<SearchingAddress> response) {
                SearchingAddress searchingAddress = response.body();
                Log.d("testAddress", searchingAddress.getSearchingAddressItems().get(0).getAddressName());
            }

            @Override
            public void onFailure(Call<SearchingAddress> call, Throwable t) {
                Log.d("testAddress", t.toString());
            }
        });
    }
}
