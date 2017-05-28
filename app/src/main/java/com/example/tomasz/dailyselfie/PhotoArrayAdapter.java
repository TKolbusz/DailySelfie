package com.example.tomasz.dailyselfie;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

class PhotoArrayAdapter extends ArrayAdapter<String>
{
    private final Activity activity;
    private final List<PhotoData> photoData;

    PhotoArrayAdapter(@NonNull Activity activity, List<PhotoData> photoData, List<String> photoNames)
    {
        super(activity, R.layout.listview, photoNames);

        this.activity = activity;
        this.photoData = photoData;
    }

    Uri getImageUriFromPosition(int position)
    {
        return photoData.get(position).getImageUri();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        LayoutInflater inflater = activity.getLayoutInflater();
        ViewHolder holder;
        View rowView = convertView;
        if (rowView == null)
        {
            rowView = inflater.inflate(R.layout.listview, null);

            holder = new ViewHolder();
            holder.setTitleText((TextView) rowView.findViewById(R.id.item));
            holder.setPhoto((ImageView) rowView.findViewById(R.id.photo));
            holder.setDescription((TextView) rowView.findViewById(R.id.description));

            rowView.setTag(holder);
        }
        else
            holder = (ViewHolder) rowView.getTag();

        SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss dd-M-yyyy", Locale.US);
        PhotoData data = photoData.get(position);
        String dateString = sdf.format(data.getTimeTaken().getTime());

        holder.getTitleText().setText(data.getPhotoName());
        holder.getPhoto().setImageURI(data.getImageUri());
        holder.getDescription().setText(String.format(activity.getString(R.string.taken_on),dateString));

        return rowView;
    }


}
