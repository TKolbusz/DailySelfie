package com.example.tomasz.dailyselfie;

import android.net.Uri;

import java.util.Calendar;

public class PhotoData
{
    private Calendar timeTaken;
    private String photoName;
    private Uri imageUri;

    public PhotoData(Calendar timeTaken, String photoName, Uri imageUri)
    {
        this.timeTaken = timeTaken;
        this.photoName = photoName;
        this.imageUri = imageUri;
    }

    public Calendar getTimeTaken()
    {
        return timeTaken;
    }

    public void setTimeTaken(Calendar timeTaken)
    {
        this.timeTaken = timeTaken;
    }

    public String getPhotoName()
    {
        return photoName;
    }

    public void setPhotoName(String photoName)
    {
        this.photoName = photoName;
    }

    public Uri getImageUri()
    {
        return imageUri;
    }

    public void setImageUri(Uri imageUri)
    {
        this.imageUri = imageUri;
    }
}
