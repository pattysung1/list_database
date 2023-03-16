package com.example.homework0309;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.gson.Gson;


import java.util.ArrayList;
import java.util.List;

import application.SettingPreference;

public class MainActivity extends AppCompatActivity {
    //宣告
    EditText m_nameType;
    EditText m_ageType;
    SearchView m_searchView;
    Button m_addButton;
    Button m_searchButton;
    private RecyclerView m_recyclerView;
    private RecyclerView.LayoutManager m_LayoutManager;
    String m_nameTypeInput ="" ;
    String m_ageTypeInput ="";
    private List<DataModel> m_data = new ArrayList<>();
    private MyAdapter m_Adapter;
    List<DataModel> m_searchList = new ArrayList<>();
    int m_count =0;
    String m_deleteIndexPage1 = "";
    final static int m_REQUEST_CODE_FOR_PAGE2 = 123;
    private boolean m_isFirstTime = true;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_isFirstTime = true;
        m_addButton = findViewById(R.id.addButton);
        m_addButton.setEnabled(false); //一開始不能按
        m_nameType = findViewById(R.id.edit_name);
        m_ageType = findViewById(R.id.edit_age);
        m_recyclerView = findViewById(R.id.recyclerView);
        m_searchView = findViewById(R.id.searchView);
        m_searchButton = findViewById(R.id.searchButton);

//        doData();

        //把recyclerView 交給 myAdapter
        m_Adapter = new MyAdapter();
        m_recyclerView.setAdapter(m_Adapter);


        //姓名欄位
        m_nameType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {

                Log.d("Patty", "name:"+ editable);
                if((m_nameType.length()>0) && (m_ageType.length()>0)){
                    m_addButton.setEnabled(true);
                } else{
                    m_addButton.setEnabled(false);
                }
            }
        });
        //年齡欄位
        m_ageType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {

                Log.d("Patty", "age:"+ editable);
                if((m_nameType.length()>0) && (m_ageType.length()>0)){
                    m_addButton.setEnabled(true);
                } else{
                    m_addButton.setEnabled(false);
                }
            }
        });
//        m_nameEditText.addTextChangedListener(new TextWatcher())//研究一下

        //ADD按鈕
        m_addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_nameTypeInput = m_nameType.getText().toString();
                m_ageTypeInput = m_ageType.getText().toString();
                int age = Integer.parseInt(m_ageTypeInput); //轉成int
                Log.d("addButton","name:"+ m_nameTypeInput +"age:"+ m_ageTypeInput);

                m_data.add(new DataModel(m_count, m_nameTypeInput, age));
                m_count++;
                Log.d("Patty","dataName:" + m_data.get(0).getName());
                m_Adapter.notifyDataSetChanged();//搞清楚和itemchanged差別
//                m_Adapter.notifyItemInserted();

                m_ageType.setText("");

            }
        });

        //RecyclerView
        m_recyclerView.setHasFixedSize(true);
        m_LayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);//直的且不反轉
        m_recyclerView.setLayoutManager(m_LayoutManager);

        //搜尋器
        m_searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;

            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        //搜尋按鈕
        m_searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_searchList.clear();
                // 獲取用戶輸入的內容
                String query = m_searchView.getQuery().toString();

                // 在這裡執行搜索操作，例如發送網絡請求或搜索本地數據庫
                // 在 RecyclerView 中查找符合搜索詞的項目
                // 取得符合條件的 title List
                for (int i = 0; i < m_data.size(); i++) {
                    String title = m_data.get(i).getName();
                    int age = m_data.get(i).getAge();
                    int index = m_data.get(i).getIndex();
                    if (title.contains(query)) { // 如果標題包含搜索詞
                        m_recyclerView.smoothScrollToPosition(i); // 滾動到該項目的位置
                        m_searchList.add(new DataModel(index,title, age));
                    }
                }
                // 將list轉成Json
                Gson gson = new Gson();
                String json = gson.toJson(m_searchList);

                //0313 SettingPreferences
                SettingPreference.getInstance().setSample(json);

                // 跳轉到下一頁，例如搜索結果頁面
                Intent intent = new Intent(MainActivity.this, Page2Activity.class);
                intent.putExtra("searchList", json);
                Log.d("Patty","json:" + json);

                // ***啟動第二頁的 Activity
                startActivityForResult(intent, m_REQUEST_CODE_FOR_PAGE2);

            }
        });
    }
    //新增資料
//    public void doData() {
//        data = new ArrayList<>();
//        Log.d("Patty","Dodata:"+data);
//
//    }
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
        class MyViewHolder extends RecyclerView.ViewHolder{
            private View itemView;
            private TextView name,age,deleteButton;

            public MyViewHolder(View view){
                super(view);
                itemView = view;

                name = itemView.findViewById(R.id.item_name);
                age = itemView.findViewById(R.id.item_age);
                deleteButton = itemView.findViewById(R.id.deleteButton);
            }
        }

        @NonNull
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //產生介面or浮現版面
            View itemView = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item,parent,false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        //處理資料細節
            holder.name.setText(m_data.get(position).getName());
            holder.age.setText(m_data.get(position).getAge()+"");
            //刪除資料
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v("Patty","Click: " +position);
                    m_data.remove(position);
                    m_Adapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            //幾筆資料
            return m_data.size();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case m_REQUEST_CODE_FOR_PAGE2:
                if(resultCode == Activity.RESULT_OK){
                    m_deleteIndexPage1 = data.getStringExtra("deleteIndexString");
                    Log.d("Patty", "onActivityResult接收回傳:  " + m_deleteIndexPage1);
                } else if (resultCode == Activity.RESULT_CANCELED){

                }
                break;
        }
    }

    @Override
    protected void onResume() {
        //看刪除前data有幾筆
        if (m_isFirstTime) {
            // 第一次啟動 Activity，執行相應的操作
            // 將 isFirstTime 設置為 false
            m_isFirstTime = false;
        } else {
            // 從第二個頁面返回到第一個頁面，執行其他的操作
            Log.d("Patty", "onResume_data.size(): "+ m_data.size());

            if(m_deleteIndexPage1!= null){
                // 從"deleteIndexPage1"中讀取索引(以逗號分隔的字串)
                String[] indexes = m_deleteIndexPage1.split(","); // 將索引字串以逗號分隔為一個字串陣列
                Log.d("Patty", "indexes: "+ indexes.length);
                int[] indexesToInt = new int[indexes.length];
                //把indexes字串變成數字
                for (int i = 0; i < indexesToInt.length; i++) {
                    indexesToInt[i] = Integer.parseInt(indexes[i].trim());
                    Log.d("Patty", "indexesToInt[i]: " + indexesToInt[i]);
                }

                //印出資料看看
                for(int i = 0; i < m_data.size(); i++){
                    Log.d("Patty", "data.get(i).getIndex(): " + m_data.get(i).getIndex());
                }
                //幾筆資料(給跑迴圈用)
                int dataSize = m_data.size();

                //**如果indexesToInt裡面的index 和data裡面的Index相同，就刪除那筆data的整筆資料
                for (int i = 0; i < indexesToInt.length; i++) {
                    int index = indexesToInt[i]; //所要刪掉的index值
                    Log.d("Patty", "index: " + index);
                    for (int j = 0; j < dataSize; j++) {
                        int indexInData = m_data.get(j).getIndex();
                        Log.d("Patty", "indexInData: " + indexInData);
                        if (indexInData == index) {
                            Log.d("Patty", "indexInData2: " + indexInData);
                            Log.d("Patty", "indexInData2: " + m_data.get(j).getIndex());
                            m_data.remove(j);

                            m_Adapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
//            //更新data至shared preference
//            // 將list轉成Json
//            Gson gson = new Gson();
//            String json = gson.toJson(m_searchList);
//
//            //0313 SettingPreferences
//            SettingPreference.getInstance().setSample(json);
        }
        super.onResume();
    }
}