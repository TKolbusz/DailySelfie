package com.example.tomasz.dailyselfie;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.icu.text.SelectFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
{
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";
    private static final int REQUEST_CODE_PHOTO = 0xA;
    private static final int REQUEST_CODE_ALARM = 0x1;
    private static final int REQUEST_CODE_ACTIVITY = 0x3;
    private static final int REQUEST_CODE_PERMISSION = 0x5;
    private final int ALARM_INTERVAL = 30 * 1000; // 30 seconds
    private PhotoArrayAdapter photoArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupAlarmsAndNotifications();
        setupListView();
    }

    private void setupAlarmsAndNotifications()
    {
        // setup MainActivityIntent to start activity when user clicks notification
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(
                this,
                REQUEST_CODE_ACTIVITY,
                mainActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // setup Notification
        Notification.Builder builder = new Notification.Builder(this);
        Notification notification = builder.setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_description))
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setAutoCancel(true)
                .setContentIntent(mainActivityPendingIntent)
                .build();

        // setup Alarm intents
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent notificationReceiverIntent = new Intent(this, NotificationPublisher.class);
        notificationReceiverIntent.putExtra(NOTIFICATION_ID, REQUEST_CODE_ALARM);
        notificationReceiverIntent.putExtra(NOTIFICATION, notification);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_ALARM, notificationReceiverIntent, PendingIntent
                .FLAG_UPDATE_CURRENT);

        // set up repeating alarm to repeat
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + ALARM_INTERVAL, ALARM_INTERVAL, alarmIntent);
    }

    private void setupListView()
    {
        //get saved photos
        File photoDir = new File(Environment.getExternalStorageDirectory(), getString(R.string.folder_name));
        File[] photos = photoDir.listFiles();
        if (photos != null)
        {
            PhotoData[] photoData = new PhotoData[photos.length];
            String[] photoNames = new String[photos.length];

            for (int i = 0; i < photos.length; i++)
            {
                //remove extension
                String photoName = photos[i].getName();
                photoNames[i] = photoName;
                String fileNameWithOutExt = photoName.replaceFirst("[.][^.]+$", "");
                // prone to exceptions if someone renames a file
                long timeInMillis = Long.parseLong(fileNameWithOutExt);
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(timeInMillis);

                photoData[i] = new PhotoData(cal, photoName, Uri.fromFile(photos[i]));
            }

            ListView listView = (ListView) findViewById(R.id.list_view);
            photoArrayAdapter = new PhotoArrayAdapter(this, photoData, photoNames);
            listView.setAdapter(photoArrayAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    Intent photoViewIntent = new Intent(Intent.ACTION_VIEW);
                    photoViewIntent.setDataAndType(photoArrayAdapter.getImageUriFromPosition(position), "image/*");
                    //check if there is photo viewer
                    if (photoViewIntent.resolveActivity(getPackageManager()) != null)
                        startActivity(photoViewIntent);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_camera)
        {
            //permission check required in newer android versions
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED)
            {
                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePhotoIntent.resolveActivity(getPackageManager()) != null)// check if there is camera installed
                {

                    //folder stuff
                    File imagesFolder = new File(Environment.getExternalStorageDirectory(), getString(R.string.folder_name));

                    // make folder if non existent
                    imagesFolder.mkdirs();

                    // specify to which file save an image
                    File image = new File(imagesFolder, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    Uri uriSavedImage = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", image);

                    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                    takePhotoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    takePhotoIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    startActivityForResult(takePhotoIntent, REQUEST_CODE_PHOTO);
                }
            }
            else
                {
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE_PERMISSION);
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PHOTO && resultCode == Activity.RESULT_OK)
        {
            Toast.makeText(MainActivity.this, getString(R.string.congratulations_message), Toast.LENGTH_SHORT).show();
            // reinstantiate listview after new photo had been taken
            setupListView();
        }
    }
}
