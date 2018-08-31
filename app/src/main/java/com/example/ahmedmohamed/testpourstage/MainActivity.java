package com.example.ahmedmohamed.testpourstage;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ahmedmohamed.testpourstage.Adapters.WhatsAppAdapter;
import com.example.ahmedmohamed.testpourstage.Entities.Contact;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLOutput;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //request code relative à lire les contacts
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    //list view des contacts
    ListView contactsListView;
    //adapter pour injecter les contacts
    WhatsAppAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contactsListView = (ListView)findViewById(R.id.list_contacts);
        askForContactPermission();


    }
    //Demander la permission pour voir les contacts(read contacts)
    public void askForContactPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {


                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Contacts access needed");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("please confirm Contacts access");//TODO put real question
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {Manifest.permission.READ_CONTACTS}
                                    , PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    });
                    builder.show();

                } else {



                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            PERMISSIONS_REQUEST_READ_CONTACTS);


                }
            }else{
                getWhatsAppContacts();
            }
        }
        else{
            getWhatsAppContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getWhatsAppContacts();


                } else {

                    Toast.makeText(this, "No permission for contacts",
                            Toast.LENGTH_LONG).show();

                }
                return;
            }

        }
    }







    public  void  getWhatsAppContacts() {

        ContentResolver cr = getApplicationContext().getContentResolver();
        //curseur afin de filtrer les types de comptes(filtrer les contacts what's app)
        Cursor contactCursor = cr.query(
                ContactsContract.RawContacts.CONTENT_URI,
                new String[]{ContactsContract.RawContacts._ID,
                        ContactsContract.RawContacts.CONTACT_ID},
                ContactsContract.RawContacts.ACCOUNT_TYPE + "= ?",
                new String[]{"com.whatsapp"},
                null);
        //ArrayList pour stocker les contacts
        ArrayList<Contact> myWhatsappContacts = new ArrayList<>();

        //parcourir la liste des contacts whats app
        if (contactCursor != null) {
            if (contactCursor.getCount() > 0) {
                if (contactCursor.moveToFirst()) {
                    do {
                        //l'id du contact whatsapp afin de récuperer le reste de ces données aprés
                        String whatsappContactId = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID));

                        if (whatsappContactId != null) {
                            //récuperer le reste des données relative au contactContract à partir de son ID
                            Cursor whatsAppContactCursor = cr.query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    new String[]{whatsappContactId}, null);

                            if (whatsAppContactCursor != null) {
                                whatsAppContactCursor.moveToFirst();
                                String id = whatsAppContactCursor.getString(whatsAppContactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                                String name = whatsAppContactCursor.getString(whatsAppContactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                                String number = whatsAppContactCursor.getString(whatsAppContactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));



                                whatsAppContactCursor.close();

                                //crée un contact et l'ajouter à notre liste
                                Contact contact = new Contact();
                                contact.setNom(name);
                                contact.setNumtel(number);
                                myWhatsappContacts.add(contact);
                                //afficher l'id du contact
                                System.out.println( " WhatsApp id  :  " + id);
                                //afficher le nom du contact
                                System.out.println( " WhatsApp nom :  " + name);
                                //afficher le numéro de téléphone du contact
                                System.out.println( " WhatsApp num :  " + number);
                            }
                        }
                    } while (contactCursor.moveToNext());
                    contactCursor.close();
                }
            }
        }
        //afficher le nombre de contacts
        System.out.println(" nombre de contact WhatsApp  :  " + myWhatsappContacts.size());
        //instantier l'adapter avec la liste des contacts
        adapter = new WhatsAppAdapter(this, myWhatsappContacts);
        contactsListView.setAdapter(adapter);
    }



}
