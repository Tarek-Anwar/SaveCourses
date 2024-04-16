package com.dhay.myapplication;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddCoursesActivity extends AppCompatActivity {

    public static final SimpleDateFormat dayFormat =new SimpleDateFormat("dd/MM/yyyy" , new Locale("en"));

    TextView dateStartCourseTV, dateEndCourseTV;
    EditText nameCourse, descriptionCourse;
    Button saveCourse;
    String dateStartCourse , dateEndCourse;
    private final String CHANEL_ID = "contact_manager";
    private final int NOTIFICATION_ID =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_courses);

        initUI();

        DatabaseHelper dbh = new DatabaseHelper(getApplicationContext());

        saveCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!nameCourse.getText().toString().isEmpty()
                        && !descriptionCourse.getText().toString().isEmpty()
                        && dateStartCourse != null
                        && dateEndCourse != null
                ) {
                    CoursesModel courses = new CoursesModel(nameCourse.getText().toString(),
                            descriptionCourse.getText().toString(),
                            dateStartCourse , dateEndCourse );
                    if (dbh.InsertCourse(courses)) {
                        createNotification();
                        addNotification("Record inserted successfully");
                        Toast.makeText(getApplicationContext(), "Record inserted successfully", Toast.LENGTH_LONG).show();

                    } else
                        Toast.makeText(getApplicationContext(), "Record not inserted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Make sure the data is correct ", Toast.LENGTH_LONG).show();

                }
            }
        });

        dateStartCourseTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddCoursesActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                ++month;
                                dateStartCourse = dayOfMonth+"/"+month+"/"+year;
                                dateStartCourseTV.setText(dateStartCourse);
                            }
                        }, 2024, 0, 1);
                datePickerDialog.show();
            }
        });

        dateEndCourseTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddCoursesActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                ++month;
                                dateEndCourse = dayOfMonth+"/"+month+"/"+year;
                                dateEndCourseTV.setText(dateEndCourse);
                            }
                        }, 2024, 1, 1);
                datePickerDialog.show();
            }
        });

    }

    private static final String TAG = "AddCoursesActivity";
    private void initUI() {
        dateStartCourseTV = findViewById(R.id.date_start_courses_tv);
        dateEndCourseTV = findViewById(R.id.date_end_courses_tv);
        nameCourse = findViewById(R.id.name_course_et);
        descriptionCourse = findViewById(R.id.description_course_et);
        saveCourse = findViewById(R.id.save_btn);
    }


    public static long getDateLong(String dateString){
        Date date = null;
        try {
            date = dayFormat.parse(dateString);
        } catch (ParseException e) { e.printStackTrace(); }
        return  date.getTime();
    }

    public static String parseAllDataFormat(long dateLong){
        Date date = new Date(dateLong);
        String dateString = dayFormat.format(date);
        return dateString;
    }


    private void createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence appName = "Contact Manager";

            NotificationChannel notificationChannel = new NotificationChannel(CHANEL_ID, appName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("This is Contact Manager Notification");

            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);

        }
    }

    @SuppressLint("MissingPermission")
    private void addNotification(String massage) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANEL_ID);
        builder.setSmallIcon(R.drawable.logo);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo));
        builder.setContentTitle("Contact Manager");
        builder.setContentText(massage);
        builder.setAutoCancel(true);
        builder.setTicker("New Massage");
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Add as notification
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());

    }
}

