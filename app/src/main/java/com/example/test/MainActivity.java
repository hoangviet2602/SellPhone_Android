package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.test.pk_HelperClasses.adapterphone;
import com.example.test.pk_HelperClasses.phonehelper;
import com.example.test.pk_Item.Item;
import com.example.test.pk_Item.adapterItem;
import com.example.test.pk_img_viewflper.Photo;
import com.example.test.pk_img_viewflper.PhotoAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;


public class MainActivity extends AppCompatActivity implements adapterphone.ListItemClickListener, adapterItem.ListItemClickListener {
    BottomNavigationView bottomNavigationView;
    RecyclerView itemRecycler;
    RecyclerView phoneRecycler2;
    RecyclerView phoneRecycler3;
    RecyclerView phoneRecycler4;
    CircleIndicator circleIndicator;
    private ViewPager viewPager;
    private PhotoAdapter photoAdapter;
    private List<Photo> mlistphoto;
    private Timer mTimer;
    RecyclerView.Adapter adapter;
    Toolbar toolbar;
    public static String user = null;
    public static String pass = null;

    TextView smsCountTxt;
    public static int pendingSMSCount = 0;
    private static  final String BASE_URL = "http://192.168.1.62/androidwebservice/danhmuc.php";
    private static  final String BASE_URL_SP = "http://192.168.1.62/androidwebservice/sanpham.php";
    ArrayList<Item> itemDMs = new ArrayList<Item>();
    ArrayList<phonehelper> Phones = new ArrayList<phonehelper>();
    ArrayList<phonehelper> Phones2 = new ArrayList<phonehelper>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.profile:
                        if(user!=null){
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        overridePendingTransition(0,0);
                        }else{
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            overridePendingTransition(0,0);
                        }
                        return true;
                    case R.id.home:
                        return true;
                    case R.id.item:
                        startActivity(new Intent(getApplicationContext(), CategoryActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.action_notification:
                        if(user!=null){
                            startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
                            overridePendingTransition(0,0);
                        }else{
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            overridePendingTransition(0,0);
                        }
                        return true;
                }
                return false;
            }
        });


        //Hooks
        itemRecycler = findViewById(R.id.my_recycler);
        getDanhMuc();



        phoneRecycler2 = findViewById(R.id.my_recycler1);
        getSanPham();
        phoneRecycler3 = findViewById(R.id.my_recycler2);
        getSanPham2();
        phoneRecycler4 = findViewById(R.id.my_recycler3);
        //phoneRecycler4();

        mlistphoto = getListPhoto();
        viewPager = findViewById(R.id.viewpager);
        circleIndicator = findViewById(R.id.circle_indicator);

        photoAdapter = new PhotoAdapter(this,mlistphoto );
        viewPager.setAdapter(photoAdapter);
        autoSlideImages();
        circleIndicator.setViewPager(viewPager);
        photoAdapter.registerDataSetObserver(circleIndicator.getDataSetObserver());

        toolbar = findViewById(R.id.toolbarmain);
        setSupportActionBar(toolbar);

    }
    private  void getDanhMuc(){
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, BASE_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for(int i = 0 ; i <=response.length();i++){
                            try{
                                JSONObject object = response.getJSONObject(i);
                                    Item item = new Item();
                                    item.setId(object.getInt("idDM"));
                                    item.setTitle(object.getString("TenDM"));
                                    item.setImage(object.getString("HinhAnh"));
                                    itemDMs.add(item);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        itemRecycler.setLayoutManager(new GridLayoutManager(getApplicationContext(),2,GridLayoutManager.HORIZONTAL,false));
                        //itemRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                        adapter = new adapterItem(getApplicationContext(),itemDMs,MainActivity.this);
                        itemRecycler.setAdapter(adapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText( null, "lỗi", Toast.LENGTH_SHORT).show();
                Log.d("tag","onErrorRespone: " +error.getMessage());
            }
        });
        queue.add(jsonArrayRequest);
    }
    private void getSanPham(){
        NumberFormat formatter = new DecimalFormat("###,###,###");
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, BASE_URL_SP, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for(int i = 0 ; i <=response.length();i++){
                            try{
                                JSONObject object = response.getJSONObject(i);
                                phonehelper phone = new phonehelper();
                                phone.setIdDM(object.getInt("idDM"));
                                phone.setIdSP(object.getInt("idSP"));
                                phone.setTitle(object.getString("Tittle"));
                                phone.setImage(object.getString("HinhAnh"));
                                phone.setNote(object.getString("UuDai"));
                                phone.setPrice(formatter.format(object.getInt("Gia"))+" VNĐ");
                                phone.setRate(object.getInt("SoDanhGia")+" đánh giá");
                                phone.setSizemanhinh(object.getString("size"));
                                phone.setLoaimanhinh(object.getString("loai"));
                                phone.setRam(object.getString("ram"));
                                phone.setRom(object.getString("rom"));
                                phone.setPin(object.getString("pin"));
                                phone.setStar(R.drawable.star);
                                phone.setStar2(R.drawable.star);
                                phone.setStar3(R.drawable.star);
                                phone.setStar4(R.drawable.star);
                                Phones.add(phone);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        phoneRecycler2.setLayoutManager(new GridLayoutManager(getApplicationContext(),2,GridLayoutManager.HORIZONTAL,false));
                        //itemRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                        adapter = new adapterphone(getApplicationContext(),Phones,MainActivity.this);
                        phoneRecycler2.setAdapter(adapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText( null, "lỗi", Toast.LENGTH_SHORT).show();
                Log.d("tag","onErrorRespone: " +error.getMessage());
            }
        });
        queue.add(jsonArrayRequest);
    }
    private void getSanPham2(){
        NumberFormat formatter = new DecimalFormat("###,###,###");
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, BASE_URL_SP, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for(int i = 0 ; i <=response.length();i++){
                            try{
                                JSONObject object = response.getJSONObject(i);
                                phonehelper phone = new phonehelper();
                                phone.setIdDM(object.getInt("idDM"));
                                phone.setIdSP(object.getInt("idSP"));
                                phone.setTitle(object.getString("Tittle"));
                                phone.setImage(object.getString("HinhAnh"));
                                phone.setNote(object.getString("UuDai"));
                                phone.setPrice(formatter.format(object.getInt("Gia"))+" VNĐ");
                                phone.setRate(object.getInt("SoDanhGia")+" đánh giá");
                                phone.setSizemanhinh(object.getString("size"));
                                phone.setLoaimanhinh(object.getString("loai"));
                                phone.setRam(object.getString("ram"));
                                phone.setRom(object.getString("rom"));
                                phone.setPin(object.getString("pin"));
                                phone.setStar(R.drawable.star);
                                phone.setStar2(R.drawable.star);
                                phone.setStar3(R.drawable.star);
                                phone.setStar4(R.drawable.star);
                                Phones2.add(phone);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        phoneRecycler3.setLayoutManager(new GridLayoutManager(getApplicationContext(),1,GridLayoutManager.HORIZONTAL,false));
                        //itemRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                        adapter = new adapterphone(getApplicationContext(),Phones,MainActivity.this);
                        phoneRecycler3.setAdapter(adapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText( null, "lỗi", Toast.LENGTH_SHORT).show();
                Log.d("tag","onErrorRespone: " +error.getMessage());
            }
        });
        queue.add(jsonArrayRequest);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2,menu);
         MenuItem menuItem = menu.findItem(R.id.actioncart);
         smsCountTxt = findViewById(R.id.cart_badge);
        if(pendingSMSCount==0){
            menuItem.setActionView(null);
        }else{
            menuItem.setActionView(R.layout.custom_cart_layout);
            View view = menuItem.getActionView();
            smsCountTxt = view.findViewById(R.id.cart_badge);
            smsCountTxt.setText(String.valueOf((pendingSMSCount)));
            setupBadge();
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onOptionsItemSelected(menuItem);
                }
            });
        }

        return true;
    }
    private void setupBadge() {
        if (smsCountTxt != null) {
            if (pendingSMSCount == 0) {
                if (smsCountTxt.getVisibility() != View.GONE) {
                    smsCountTxt.setVisibility(View.GONE);
                }
            } else {
                smsCountTxt.setText(String.valueOf(Math.min(pendingSMSCount, 99)));
                if (smsCountTxt.getVisibility() != View.VISIBLE) {
                    smsCountTxt.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case  R.id.actioncart:
                if(user != null ){
                    Intent intent = new Intent(MainActivity.this, CartActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
        return true;
    }

    private List<Photo> getListPhoto(){
        List<Photo> list = new ArrayList<>();
        list.add(new Photo(R.drawable.view1));
        list.add(new Photo(R.drawable.view2));
        list.add(new Photo(R.drawable.view3));
        list.add(new Photo(R.drawable.view1));
        list.add(new Photo(R.drawable.view2));
        list.add(new Photo(R.drawable.view3));
        return list;
    }
    private void autoSlideImages(){
        if(mlistphoto==null || mlistphoto.isEmpty() || viewPager == null){
            return;
        }
        if(mTimer == null){
            mTimer = new Timer();
        }
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        int currentItem = viewPager.getCurrentItem();
                        int totalItem =  mlistphoto.size() -1 ;
                        if(currentItem < totalItem){
                            currentItem++;
                            viewPager.setCurrentItem(currentItem);
                        }else{
                            viewPager.setCurrentItem(0);
                        }
                    }
                });
            }
        },300,3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mTimer != null){
            mTimer.cancel();
            mTimer = null;
        }
    }


    @Override
    public void onphoneListClick(int clickedItemIndex) {

    }
    @Override
    public void onitemListClick(int clickedItemIndex) {

    }
}