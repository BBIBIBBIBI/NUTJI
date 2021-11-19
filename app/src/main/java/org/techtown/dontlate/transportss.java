package org.techtown.dontlate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.internal.InternalTokenProvider;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.poi_item.TMapPOIItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.dontlate.model.NetworkThreadArsId;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class transportss extends Activity {

    Button fnBtn, sOkBtn;
    TextView ttview;
    private NetworkThread thread;
    private NetworkThreadArsId threadArsId;
    private String queryUrl;
    private FirebaseDatabase database;
    ArrayList<Item> list = null;
    ArrayList<SubwayItems> slist = new ArrayList<SubwayItems>();
    ArrayList<String> stCode = new ArrayList<>();
    ArrayList<String> etCode = new ArrayList<>();
    Item bus = null;
    SubwayItems sub = null;
    RecyclerView recyclerView;
    RecyclerView srecyclerView;
    String startPoint, arrivePoint;
    public double stplo, stpla, arplo, arpla;
    public String SX, SY, EX, EY;
    String ssId, esId, globalStartName, globalEndName;
    String glsName, gleName, gltTime, glsCount, adFare, laName, selaName, thlaName, sttName, sesttName, thsttName, stCount, sestCount, thstCount, waName, sewaName, thwaName;


    //seoulCitySubway.json 자체


    //??

    private String getJsonString() {
        String json = "";

        try {
            InputStream is = getAssets().open("seoulCitySubway.json");

            int fileSize = is.available();

            byte[] buffer = new byte[fileSize];
            is.read(buffer);
            is.close();


            json = new String(buffer, "UTF-8");
            JSONArray jsonArray = new JSONArray(json);

            globalStartName = "강동구청";

            globalEndName = "정발산";

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                if (obj.getString("station_nm").equals(globalStartName)) {
                    stCode.add(obj.getString("fr_code"));
                }

                if (obj.getString("station_nm").equals(globalEndName)) {
                    etCode.add(obj.getString("fr_code"));
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transports);

        srecyclerView = (RecyclerView) findViewById(R.id.subwayRecycler);

        Context context = this;


        PublicApi.OnSuccessListener onSuccessListener = new PublicApi.OnSuccessListener() {
            @Override
            public void onRequestSuccess(String result) {
                slist.add(sub);

                LinearLayoutManager slayoutManager = new LinearLayoutManager(getApplicationContext());

                srecyclerView.setLayoutManager(slayoutManager);
                slayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

                srecyclerView.setHasFixedSize(true);
                SubwayAdapter swAdapter = new SubwayAdapter(getApplicationContext(), slist);
                srecyclerView.setAdapter(swAdapter);
                swAdapter.notifyDataSetChanged();

            }
        };


        getJsonString();


        ssId = stCode.get(0);
        esId = etCode.get(0);


        fnBtn = (Button) findViewById(R.id.finish_Button);
        sOkBtn = (Button) findViewById(R.id.search_Ok_Button);




        recyclerView = (RecyclerView) findViewById(R.id.transRecycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        TransAsyncTask transAsyncTask = new TransAsyncTask();
        transAsyncTask.execute();







        database = FirebaseDatabase.getInstance();



        TabHost tabHost = findViewById(R.id.tabhost);
        tabHost.setup();

        TabHost.TabSpec tabSpecBusroad = tabHost.newTabSpec("busroad").setIndicator("버스");
        tabSpecBusroad.setContent(R.id.busRoad);
        tabHost.addTab(tabSpecBusroad);

        TabHost.TabSpec tabSpecSubway = tabHost.newTabSpec("subway").setIndicator("지하철");
        tabSpecSubway.setContent(R.id.subwayRoad);
        tabHost.addTab(tabSpecSubway);


        tabHost.setCurrentTab(0);


        fnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        database.getReference("Nutji").child("User").child("Place").child("집").child("Address").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                startPoint = value;
                ttview.setText(startPoint);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.getReference("Nutji").child("User").child("Place").child("학교").child("Address").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                arrivePoint = value;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> mResultLocation =
                    geocoder.getFromLocationName("강동구청역", 1);
            stpla = mResultLocation.get(0).getLatitude();
            stplo = mResultLocation.get(0).getLongitude();
            SX = String.valueOf(stplo);
            SY = String.valueOf(stpla);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Geocoder mGeocoder = new Geocoder(this);
        try {
            List<Address> mResultLocations =
                    mGeocoder.getFromLocationName("건대입구역", 1);
            arpla = mResultLocations.get(0).getLatitude();
            arplo = mResultLocations.get(0).getLongitude();
            EX = String.valueOf(arplo);
            EY = String.valueOf(arpla);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ODsayService odsayService = ODsayService.init(context, "aA9ke5zmlxhLEwa6zdtrHc1gR4YctbDBTch+0TVOm1g");
        // 서버 연결 제한 시간(단위(초), default : 5초)
        odsayService.setReadTimeout(5000);
        // 데이터 획득 제한 시간(단위(초), default : 5초)
        odsayService.setConnectionTimeout(5000);
        // 콜백 함수 구현
        OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
            // 호출 성공 시 실행
            @Override
            public void onSuccess(ODsayData odsayData, API api) {
                try {
                    slist = new ArrayList<SubwayItems>();
                    // API 호출구문
                    if (api == API.SUBWAY_PATH) {
                        sub = new SubwayItems();
                        //출발역 명
                        glsName = odsayData.getJson().getJSONObject("result").getString("globalStartName");
                        sub.setGlsName(glsName);

                        //도착역 명
                        gleName = odsayData.getJson().getJSONObject("result").getString("globalEndName");
                        sub.setGleName(gleName);

                        //소요시간

                        gltTime = odsayData.getJson().getJSONObject("result").getString("globalTravelTime");
                        sub.setGtTime(gltTime);


                        //정차역 수
                        glsCount = odsayData.getJson().getJSONObject("result").getString("globalStationCount");
                        sub.setGlsCount(glsCount);


                        //카드요금
                        adFare = odsayData.getJson().getJSONObject("result").getString("fare");
                        sub.setAdFare(adFare);


                        //승차역 호선명
                        laName = odsayData.getJson().getJSONObject("result").getJSONObject("driveInfoSet").getJSONArray("driveInfo").getJSONObject(0).getString("laneName");
                        sub.setLnName(laName);
                        try {
                            selaName = odsayData.getJson().getJSONObject("result").getJSONObject("driveInfoSet").getJSONArray("driveInfo").getJSONObject(1).getString("laneName");

                            thlaName = odsayData.getJson().getJSONObject("result").getJSONObject("driveInfoSet").getJSONArray("driveInfo").getJSONObject(2).getString("laneName");

                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            throw e;

                        } finally {
                            //승차역 명
                            sttName = odsayData.getJson().getJSONObject("result").getJSONObject("driveInfoSet").getJSONArray("driveInfo").getJSONObject(0).getString("startName");
                            sub.setSttName(sttName);
                            try {
                                sesttName = odsayData.getJson().getJSONObject("result").getJSONObject("driveInfoSet").getJSONArray("driveInfo").getJSONObject(1).getString("startName");


                                thsttName = odsayData.getJson().getJSONObject("result").getJSONObject("driveInfoSet").getJSONArray("driveInfo").getJSONObject(2).getString("startName");

                            } catch (NullPointerException e) {
                                e.printStackTrace();
                                throw e;
                            } finally {
                                //이동역 수
                                stCount = odsayData.getJson().getJSONObject("result").getJSONObject("driveInfoSet").getJSONArray("driveInfo").getJSONObject(0).getString("stationCount");
                                sub.setSttCount(stCount);
                                try {
                                    sestCount = odsayData.getJson().getJSONObject("result").getJSONObject("driveInfoSet").getJSONArray("driveInfo").getJSONObject(1).getString("stationCount");


                                    thstCount = odsayData.getJson().getJSONObject("result").getJSONObject("driveInfoSet").getJSONArray("driveInfo").getJSONObject(2).getString("stationCount");

                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                    throw e;
                                } finally {
                                    //방면 명
                                    waName = odsayData.getJson().getJSONObject("result").getJSONObject("driveInfoSet").getJSONArray("driveInfo").getJSONObject(0).getString("wayName");
                                    sub.setWaName(waName);
                                    try {
                                        sewaName = odsayData.getJson().getJSONObject("result").getJSONObject("driveInfoSet").getJSONArray("driveInfo").getJSONObject(1).getString("wayName");


                                        thwaName = odsayData.getJson().getJSONObject("result").getJSONObject("driveInfoSet").getJSONArray("driveInfo").getJSONObject(2).getString("wayName");

                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                        throw e;
                                    } finally {
                                        //출발역 명 호출
                                        onSuccessListener.onRequestSuccess(glsName);

                                        //도착역 명 호출
                                        onSuccessListener.onRequestSuccess(gleName);

                                        //소요시간 호출
                                        onSuccessListener.onRequestSuccess(gltTime);

                                        //정차역 수
                                        onSuccessListener.onRequestSuccess(glsCount);

                                        //카드요금 호출
                                        onSuccessListener.onRequestSuccess(adFare);

                                        //승차역 호선명 호출
                                        onSuccessListener.onRequestSuccess(laName);
                                        onSuccessListener.onRequestSuccess(selaName);
                                        onSuccessListener.onRequestSuccess(thlaName);

                                        //승차역명 호출
                                        onSuccessListener.onRequestSuccess(sttName);
                                        onSuccessListener.onRequestSuccess(sesttName);
                                        onSuccessListener.onRequestSuccess(thsttName);

                                        //이동역수 호출
                                        onSuccessListener.onRequestSuccess(stCount);
                                        onSuccessListener.onRequestSuccess(sestCount);
                                        onSuccessListener.onRequestSuccess(thstCount);

                                        //방면명 호출
                                        onSuccessListener.onRequestSuccess(waName);
                                        onSuccessListener.onRequestSuccess(sewaName);
                                        onSuccessListener.onRequestSuccess(thwaName);
                                    }
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // 호출 실패 시 실행
            @Override
            public void onError(int i, String s, API api) {
                if (api == API.SUBWAY_PATH) {
                }
            }
        };
        //API 호출
        odsayService.requestSubwayPath("1000", ssId, esId, "1", onResultCallbackListener);



    }



    public class TransAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            queryUrl = "http://ws.bus.go.kr/api/rest/stationinfo/getLowStationByUid?"
                    + "ServiceKey=de8Q96jmb%2FJj%2BopbZdsPv5k4%2F2XDiyfTluNAwrhznOJROomUFPdf7D4M%2Bzw%2BbXjCIY%2B1VqXP%2BTmJaY7wOShFIA%3D%3D&arsId=48066";

            try {
                boolean b_stnNm = false;
                boolean b_rtNm = false;
                boolean b_stationNm1 = false;
                boolean b_arrmsg1 = false;

                URL url = new URL(queryUrl);
                InputStream is = url.openStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(new InputStreamReader(is, "UTF-8"));

                String tag;
                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:
                            list = new ArrayList<Item>();
                            break;

                        case XmlPullParser.END_DOCUMENT:
                            break;

                        case XmlPullParser.END_TAG:
                            if (xpp.getName().equals("itemList") && bus != null) {
                                list.add(bus);
                            }
                            break;

                        case XmlPullParser.START_TAG:
                            if (xpp.getName().equals("itemList")) {
                                bus = new Item();
                            }
                            if (xpp.getName().equals("stnNm"))
                                b_stnNm = true;

                            if (xpp.getName().equals("rtNm"))
                                b_rtNm = true;

                            if (xpp.getName().equals("stationNm1"))
                                b_stationNm1 = true;

                            if (xpp.getName().equals("arrmsg1"))
                                b_arrmsg1 = true;
                            break;

                        case XmlPullParser.TEXT:
                            if (b_stnNm) {
                                bus.setStnNm(xpp.getText());
                                b_stnNm = false;
                            } else if (b_rtNm) {
                                bus.setRtNm(xpp.getText());
                                b_rtNm = false;
                            } else if (b_stationNm1) {
                                bus.setStationNm1(xpp.getText());
                                b_stationNm1 = false;
                            } else if (b_arrmsg1) {
                                bus.setArrmsg1(xpp.getText());
                                b_arrmsg1 = false;
                            }
                            break;
                    }
                    eventType = xpp.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            TransAdapter adapter = new TransAdapter(getApplicationContext(), list);
            recyclerView.setAdapter(adapter);
        }
    }
}



