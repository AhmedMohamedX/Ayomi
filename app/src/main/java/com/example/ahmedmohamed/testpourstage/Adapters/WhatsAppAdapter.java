package com.example.ahmedmohamed.testpourstage.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.ahmedmohamed.testpourstage.Entities.Contact;
import com.example.ahmedmohamed.testpourstage.R;

import java.util.List;

/**
 * Created by AHMED Mohamed on 05/06/2018.
 */

public class WhatsAppAdapter extends ArrayAdapter<Contact> {
    Context ctx;

    public static class VieHolder{
        //text view pour afficher le nom du contact
        TextView nom;

        //text view pour afficher le numéro du contact
        TextView num;

    }
    public WhatsAppAdapter(@NonNull Context context, List<Contact> arrayListNames) {
        super(context,0,arrayListNames);
        this.ctx = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Contact contact = getItem(position);
        VieHolder viewhomder;

        if (convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(ctx);
            convertView = inflater.inflate(R.layout.contact_list_item,parent,false);
            viewhomder = new VieHolder();
            viewhomder.nom = convertView.findViewById(R.id.nom);
            viewhomder.num = convertView.findViewById(R.id.num);

            convertView.setTag(viewhomder);

        }else {
            viewhomder = (VieHolder) convertView.getTag();

        }
        viewhomder.nom.setText(contact.getNom());
        viewhomder.num.setText(contact.getNumtel());
        return convertView;
    }
}
