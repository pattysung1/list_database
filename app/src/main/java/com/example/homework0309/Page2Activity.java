package com.example.homework0309;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private List<Person> m_data = new ArrayList<>();
    private MyAdapter m_adapter;
    private String m_json;
    private List<Person> m_deletePerson = new ArrayList<>();
    List<Person> m_searchList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page2);

        //用setting preference 接收
        m_json = SettingPreference.getInstance().getSample();
        Gson gson = new Gson();
        Type type = new TypeToken<List<Person>>(){}.getType();
        m_data = new ArrayList<>(gson.fromJson(m_json,type));

        //用intent接收query
        Intent intent = getIntent();
        String queryReceive = intent.getStringExtra("searchList");
        Log.d( "Patty", "intent: " + queryReceive);

        //在m_data中，挑選searchView的query
        //取得符合條件的 title List
        for ( int i = 0; i < m_data.size(); i++ )
        {
            String title = m_data.get( i ).getName();
            int age = m_data.get( i ).getAge();
            int index = m_data.get( i ).getIndex();
            if ( title.contains( queryReceive ) )
            {
                m_searchList.add( new Person( index, title, age ) );
            }
        }

        // 1.獲取 RecyclerView 的引用
        m_recyclerView = findViewById(R.id.recyclerView);
        // 2.Set LinearLayout Manager
        m_LayoutManager = new LinearLayoutManager(this);
        m_recyclerView.setLayoutManager(m_LayoutManager);

        // 4.關鍵性的工作：要讓recyclerView把調變器整合上來
        m_adapter = new MyAdapter();
        m_recyclerView.setAdapter( m_adapter );
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
            //產生的view，介紹給ViewHolder作為呈現
            View itemView = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item,parent,false);
            return new MyViewHolder(itemView);
        }
        @Override
        public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
            //資料的重點放在這邊(資料的細節)
            holder.name.setText(m_searchList.get(position).getName());
            holder.age.setText(m_searchList.get(position).getAge()+"");
            //刪除資料
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("Patty:Page2","Click: " +holder.getAdapterPosition());
                    //找到刪除的資料的，存到Array中
                    m_deletePerson.add(m_searchList.get(position));

                    //刪除資料m_searchList
                    m_searchList.remove(holder.getAdapterPosition());
                    m_adapter.notifyDataSetChanged();
                    for ( int i = 0; i < m_deletePerson.size(); i++ )
                    {
                        Person person = m_deletePerson.get( i ); //所刪掉的person
                        for ( int j = 0; j < m_data.size(); j++ )
                        {
                            Person personInData = m_data.get( j );
                            if (person.getName().equals( personInData.getName()) && person.getAge() == personInData.getAge() )
                            {
                                m_data.remove( j );
                                break;
                            }
                        }
                    }
                    //存檔至setting preference
                    Gson gson = new Gson();
                    String json = gson.toJson( m_data );
                    //0316 SettingPreferences
                    SettingPreference.getInstance().setSample( json );
                }
            });
        }
        @Override
        public int getItemCount() {
            //總共有幾筆資料
            return m_searchList.size();
        }
    }
}
