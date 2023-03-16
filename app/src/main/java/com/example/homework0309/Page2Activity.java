package com.example.homework0309;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import application.SettingPreference;

public class Page2Activity extends AppCompatActivity {
    String TAG = "Patty:P2";
    private RecyclerView m_recyclerView;
    private RecyclerView.LayoutManager m_LayoutManager;
    private List<DataModel> m_data = new ArrayList<>();
    private MyAdapter m_Adapter;
    private String m_json;
    TextView m_getSample;
    private List<Integer> m_deleteIndex = new ArrayList<>();;
    String m_deleteIndexString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page2);

        //用setting preference 接收
        m_json = SettingPreference.getInstance().getSample();
        Gson gson = new Gson();
        Type type = new TypeToken<List<DataModel>>(){}.getType();
        m_data = new ArrayList<>(gson.fromJson(m_json,type));

        // 1.獲取 RecyclerView 的引用
        m_recyclerView = findViewById(R.id.recyclerView);
        // 2.Set LinearLayout Manager
        m_LayoutManager = new LinearLayoutManager(this);
        m_recyclerView.setLayoutManager(m_LayoutManager);

        // 4.關鍵性的工作：要讓recyclerView把調變器整合上來
        m_Adapter = new MyAdapter();
        m_recyclerView.setAdapter(m_Adapter);
    }

    // 3.Set Adapter-維護性
    // 參數記得傳回值
    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
        class MyViewHolder extends RecyclerView.ViewHolder{
            public View itemView;
            public TextView name, age, deleteButton;

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
            //產生的view，介紹給Viewholder作為呈現
            View itemView = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item,parent,false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
            //資料的重點放在這邊(資料的細節)
            holder.name.setText(m_data.get(position).getName());
            holder.age.setText(m_data.get(position).getAge()+"");
            //刪除資料
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("Patty:Page2","Click: " +position);
                    //找到刪除的資料的index，存到deleteIndex這個Array中
                    m_deleteIndex.add(m_data.get(position).getIndex());
                    Log.d("Patty:Page2","deleteIndex: " + m_deleteIndex);
                    //刪除資料
                    m_data.remove(position);
                    m_Adapter.notifyDataSetChanged();

                    //把list轉成字串
                    ////轉成字串
                    StringBuilder sb = new StringBuilder();
                    for (Integer i : m_deleteIndex) {
                        sb.append(i).append(", ");
                    }
                    Log.d("Patty", "deleteIndexString: " + sb);
                    ////把最後的","刪掉
                    m_deleteIndexString = sb.toString();
                    if (m_deleteIndexString.length() > 0) {
                        m_deleteIndexString = m_deleteIndexString.substring(0, m_deleteIndexString.length() - 2);
                    }
                    Log.d("Patty:Page2", "deleteIndexString: " + m_deleteIndexString);
                }
            });
        }

        @Override
        public int getItemCount() {
            //總共有幾筆資料
            return m_data.size();
        }
    }

    @Override
    public void onBackPressed() {
        //deleteIndex 要傳回第一頁
        Intent resultIntent = new Intent();
        resultIntent.putExtra("deleteIndexString", m_deleteIndexString);
        Log.d("Patty:Page2", "deleteIndexString是否可以傳: " + m_deleteIndexString);
        setResult(Activity.RESULT_OK, resultIntent);
        super.onBackPressed();
    }
}