package com.dhay.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private DatabaseHelper dbHandler;
    ArrayList<CoursesModel> listCourses;
    private CoursesRVAdapter coursesRVAdapter;

    private RecyclerView coursesRV;
    TextView noCoursesTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHandler = new DatabaseHelper(MainActivity.this);

        coursesRV = findViewById(R.id.courses_rv);
        noCoursesTV = findViewById(R.id.no_courses_tv);

        listCourses = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
        coursesRV.setLayoutManager(linearLayoutManager);
        coursesRVAdapter = new CoursesRVAdapter(MainActivity.this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        listCourses = dbHandler.getAllData();

        if (listCourses.isEmpty()) {
            coursesRV.setVisibility(View.GONE);
            noCoursesTV.setVisibility(View.VISIBLE);

        } else {
            noCoursesTV.setVisibility(View.GONE);
            coursesRV.setVisibility(View.VISIBLE);
            coursesRVAdapter.setCoursesModelArrayList(listCourses);
            coursesRV.setAdapter(coursesRVAdapter);
            coursesRVAdapter.notifyDataSetChanged();

            coursesRVAdapter.setOnItemLongClickListener((pos , id) -> {
                dbHandler.deleteData(id);
                listCourses.remove(pos);
                coursesRVAdapter.notifyItemRemoved(pos);
                if(listCourses.isEmpty()){
                    noCoursesTV.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mai_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            Intent intent = new Intent(this, AddCoursesActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}