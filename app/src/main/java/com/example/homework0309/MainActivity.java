package com.example.homework0309;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import application.SettingPreference;

public class MainActivity extends AppCompatActivity
{
    //宣告
    EditText m_nameType;
    EditText m_ageType;
    SearchView m_searchView;
    Button m_addButton;
    Button m_searchButton;
    private RecyclerView m_recyclerView;
    private RecyclerView.LayoutManager m_LayoutManager;
    String m_nameTypeInput = "";
    String m_ageTypeInput = "";
    private List<Person> m_personList = new ArrayList<>();
    private MyAdapter m_Adapter;
    List<Person> m_searchList = new ArrayList<>();
    int m_count = 0;
    private String m_json;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        m_addButton = findViewById( R.id.addButton );
        m_addButton.setEnabled( false ); //一開始不能按
        m_nameType = findViewById( R.id.edit_name );
        m_ageType = findViewById( R.id.edit_age );
        m_recyclerView = findViewById( R.id.recyclerView );
        m_searchView = findViewById( R.id.searchView );
        m_searchButton = findViewById( R.id.searchButton );

        //把recyclerView 交給 myAdapter
        m_Adapter = new MyAdapter();
        m_recyclerView.setAdapter( m_Adapter );
        //RecyclerView
        m_recyclerView.setHasFixedSize( true );
        m_LayoutManager = new LinearLayoutManager( this, LinearLayoutManager.VERTICAL, false );//直的且不反轉
        m_recyclerView.setLayoutManager( m_LayoutManager );

        TextWatcher textWatcher = new TextWatcher()
        {
            @Override
            public void beforeTextChanged( CharSequence charSequence, int i, int i1, int i2 )
            {

            }

            @Override
            public void onTextChanged( CharSequence charSequence, int i, int i1, int i2 )
            {

            }

            @Override
            public void afterTextChanged( Editable editable )
            {

                Log.d( "Patty", "name:" + editable );
                if ( (m_nameType.length() > 0) && (m_ageType.length() > 0) )
                {
                    m_addButton.setEnabled( true );
                }
                else
                {
                    m_addButton.setEnabled( false );
                }
            }
        };

        //姓名欄位
        m_nameType.addTextChangedListener( textWatcher );
        //年齡欄位
        m_ageType.addTextChangedListener( textWatcher );

        //ADD按鈕
        m_addButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View view )
            {
                m_nameTypeInput = m_nameType.getText().toString();
                m_ageTypeInput = m_ageType.getText().toString();
                int age = Integer.parseInt( m_ageTypeInput ); //轉成int
                //檢查有沒有重複，有的話不給輸入
                for (Person p : m_personList ){
                    if ( (p.getAge() == age) && (p.getName().equals(m_nameTypeInput) ) ){
                        Toast toast = Toast.makeText(getApplicationContext(), "此筆資料已重複", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }
                }

                Person person= new Person( m_count, m_nameTypeInput, age );
                m_personList.add(person);
                m_count++;
                Log.d( "Patty", "dataName:" + m_personList.get( 0 ).getName() );

                m_Adapter.notifyItemInserted( m_personList.size() - 1);
                m_ageType.setText( "" );

                // 將m_data轉成Json存至SettingPreferences
                Gson gson = new Gson();
                String json = gson.toJson( m_personList );
                //0313 SettingPreferences
                SettingPreference.getInstance().setSample( json );
            }
        } );



        //搜尋器
        m_searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit( String query )
            {
                return false;

            }

            @Override
            public boolean onQueryTextChange( String newText )
            {
                return false;
            }
        } );

        //搜尋按鈕
        m_searchButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                m_searchList.clear();
                // 獲取用戶輸入的內容
                String query = m_searchView.getQuery().toString();

                // 把query字串傳到第二頁
                Intent intent = new Intent( MainActivity.this, Page2Activity.class );
                intent.putExtra( "searchList", query );
                startActivity(intent);
            }
        } );
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>
    {
        class MyViewHolder extends RecyclerView.ViewHolder
        {
            private View itemView;
            private TextView name, age, deleteButton;

            public MyViewHolder( View view )
            {
                super( view );
                itemView = view;

                name = itemView.findViewById( R.id.item_name );
                age = itemView.findViewById( R.id.item_age );
                deleteButton = itemView.findViewById( R.id.deleteButton );
            }
        }

        @NonNull
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType )
        {
            //產生介面or浮現版面
            View itemView = LayoutInflater
                    .from( parent.getContext() )
                    .inflate( R.layout.item, parent, false );
            return new MyViewHolder( itemView );
        }

        @Override
        public void onBindViewHolder( @NonNull MyAdapter.MyViewHolder holder, int position )
        {
            //處理資料細節
            holder.name.setText( m_personList.get( position ).getName() );
            holder.age.setText( m_personList.get( position ).getAge() + "" );
            //刪除資料
            holder.deleteButton.setOnClickListener( new View.OnClickListener()
            {
                @Override
                public void onClick( View view )
                {
                    Log.v( "Patty", "Click: " + holder.getAdapterPosition( ));
                    m_personList.remove( holder.getAdapterPosition() );
                    m_Adapter.notifyDataSetChanged();

                    // 將m_data轉成Json存至SettingPreferences
                    Gson gson = new Gson();
                    String json = gson.toJson( m_personList );
                    //0313 SettingPreferences
                    SettingPreference.getInstance().setSample( json );
                }
            } );
        }

        @Override
        public int getItemCount()
        {
            //幾筆資料
            return m_personList.size();
        }
    }

    @Override
    protected void onResume()
    {
        //用setting preference 接收給recyclerView
        m_json = SettingPreference.getInstance().getSample();
        Gson gson = new Gson();
        Type type = new TypeToken<List<Person>>()
        {
        }.getType();
        m_personList = new ArrayList<>( gson.fromJson( m_json, type ) );
        m_Adapter.notifyDataSetChanged();
        super.onResume();
    }
}