package com.example.tomasz.dailyselfie;

import android.widget.ImageView;
import android.widget.TextView;

public class ViewHolder
{
    private TextView titleText;
    private ImageView photo;
    private TextView description;

    public TextView getTitleText()
    {
        return titleText;
    }

    public void setTitleText(TextView titleText)
    {
        this.titleText = titleText;
    }

    public ImageView getPhoto()
    {
        return photo;
    }

    public void setPhoto(ImageView photo)
    {
        this.photo = photo;
    }

    public TextView getDescription()
    {
        return description;
    }

    public void setDescription(TextView description)
    {
        this.description = description;
    }
}
