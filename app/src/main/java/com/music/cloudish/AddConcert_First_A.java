package com.music.cloudish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddConcert_First_A extends AppCompatActivity {

    private EditText inputConcertName, inputDescription, inputDuration;
    private TextInputLayout layoutConcertName, layoutDescription, layoutDuration;
    private DatePicker datePicker;
    private Button btnSetTime, btnNext;
    private TextView txtChosenTime, txtMainGenre;
    private TimePickerDialog timePickerDialog;
    private String concertName, concertDescription, concertDate, concertTime, concertDurationStr, phoneNumber, mainGenre;
    private Integer concertDuration;
    private ImageView concertImage;
    private Uri resultUri;
    private DatabaseReference mDatabase;
    private ArrayList<String> arrayList;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_concert__first_);

        //Hook
        layoutConcertName = findViewById(R.id.title_layout);
        layoutDescription = findViewById(R.id.description_layout);
        layoutDuration = findViewById(R.id.duration_layout);
        inputConcertName = findViewById(R.id.title_input);
        inputDescription = findViewById(R.id.description_input);
        inputDuration = findViewById(R.id.duration_input);
        datePicker = findViewById(R.id.date_picker);
        btnSetTime = findViewById(R.id.btnSetTime);
        btnNext = findViewById(R.id.btnNext);
        txtChosenTime = findViewById(R.id.txtChosenTime);
        txtMainGenre = findViewById(R.id.txtMainGenre);
        concertImage = findViewById(R.id.imgConcert);
        mDatabase = FirebaseDatabase.getInstance().getReference();


        arrayList = new ArrayList<>();

        // Memasukkan value ke dalam arrayList

        arrayList.add("EDM");
        arrayList.add("Jazz");
        arrayList.add("Country");
        arrayList.add("Classical");
        arrayList.add("Pop");
        arrayList.add("Indie");
        arrayList.add("Dangdut");

        // OnClick Listener

        txtMainGenre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Initiaize dialog
                dialog = new Dialog(AddConcert_First_A.this);

                // Set customer Dialog
                dialog.setContentView(R.layout.dialog_searchable_spinner);
                dialog.getWindow().setLayout(1000, 1500);

                // transparent
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dialog.show();

                //Initialize and assign variable
                EditText editText = dialog.findViewById(R.id.edit_text);
                ListView listView = dialog.findViewById(R.id.listView);

                // Initialize array adapter
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>
                        (AddConcert_First_A.this, android.R.layout.simple_list_item_1, arrayList){
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent){
                        // Get the Item from ListView
                        View view = super.getView(position, convertView, parent);

                        // Initialize a TextView for ListView each Item
                        TextView tv = (TextView) view.findViewById(android.R.id.text1);

                        // Set the text color of TextView (ListView Item)
                        tv.setTextColor(Color.BLACK);

                        // Generate ListView Item using TextView
                        return view;
                    }
                };


                //set Adapter
                listView.setAdapter(adapter);

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // Filter array List
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // When item selected from list

                        // Set selected item on text View

                        txtMainGenre.setText(adapter.getItem(position));

                        dialog.dismiss();
                    }
                });
            }
        });


        // OnClick Listener for Time
        btnSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog();
            }
        });

        // OnClick Listener for Next Button
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

        // OnClick Listener for Concert Image
        concertImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
    }

    private void validateData() {

        // Hook
        concertName = inputConcertName.getText().toString();
        concertDescription = inputDescription.getText().toString();
        concertDurationStr = inputDuration.getText().toString();
        mainGenre = txtMainGenre.getText().toString();

        Boolean a,b,c,d,e,f;

        b = isGenreValid();
        Log.d("isGenreValid",b.toString());
        a = isConcertNameValid();
        c = isConcertDateValid();
        d = isConcertDescriptionValid();
        e = isDurationValid();
        f = isPictureValid();

        if(!a || !b || !c || !d || !e || !f){
            return;
        }

        saveConcertInformation();
    }

    private Boolean isGenreValid() {
        // Validate concert Main Genre
        if(mainGenre.isEmpty()){
            Toast.makeText(this, "Choose genre first!", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }

    private Boolean isDurationValid() {

        if(concertDurationStr.isEmpty()){
            layoutDuration.setError("field can't be empty");
            return false;
        }

        concertDuration = Integer.parseInt(concertDurationStr);

        if(concertDuration == 0){
            layoutDuration.setError("duration must be more than 0");
            return false;
        }
        else{
            if(concertDuration > 240){
                layoutDuration.setError("concert duration is too long!");
                return false;
            } else{
                layoutDuration.setError(null);
                layoutDuration.setErrorEnabled(false);
            }
        }
        return true;
    }

    private Boolean isPictureValid(){
        if(resultUri == null){
            Toast.makeText(this, "Picture must be chosen!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isConcertDateValid() {
        Date currentdate = Calendar.getInstance().getTime();

        // Hook
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth()+1;
        int year = datePicker.getYear();

        String month2 = String.format("%02d", month);

        concertDate = String.valueOf(day) + "/" + String.valueOf(month2) + "/" + String.valueOf(year);
        concertTime = txtChosenTime.getText().toString().replaceAll(" ", "");

        Log.d("txtChosentime", txtChosenTime.getText().toString());

        if(txtChosenTime.getText().toString().equals("Chosen Time")){

            Log.d("isChosenTimeEmpty", "Yes");
            Toast.makeText(this, "Date or time cannot be empty!", Toast.LENGTH_SHORT).show();
            return false;

        }

        String temp_date = concertDate + " " + concertTime;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");

        Date temp_date1 = null;

        try {
            temp_date1 = simpleDateFormat.parse(temp_date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        Boolean isbefore = temp_date1.before(currentdate);

        Log.d("Bingung", isbefore.toString());

        if(isbefore) {

            Toast.makeText(this, "Date can't be in the past!", Toast.LENGTH_SHORT).show();

            return false;
        }

        return true;

    }

    private boolean isConcertDescriptionValid() {

        // Validate concert Description
        if(concertDescription.isEmpty()){
            layoutDescription.setError("field cannot be emtpy");
            return false;
        }
        else{
            if(concertDescription.length() > 150){
                layoutDescription.setError("description is too long!");
                return false;
            }else if(concertDescription.length() < 10){
                layoutDescription.setError("description is too short!");
                return false;
            }else{
                layoutDescription.setError(null);
                layoutDescription.setErrorEnabled(false);
            }
        }
        return true;
    }

    private boolean isConcertNameValid() {

        // Validate concertName
        if (concertName.isEmpty()) {
            layoutConcertName.setError("field cannot be empty");
            return false;
        } else {

            if (concertName.length() > 20 || concertName.length()<3){
                layoutConcertName.setError("concert name must between 3-20 character");
                return false;
            }
            else{
                layoutConcertName.setError(null);
                layoutConcertName.setErrorEnabled(false);
            }
        }

        return true;

    }

    private void showTimeDialog() {

        Calendar calendar = Calendar.getInstance();

        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                txtChosenTime.setText(String.valueOf(hourOfDay)+" : "+String.valueOf(minute));

            }
        },
                /**
                 * Tampilkan jam saat ini ketika TimePicker pertama kali dibuka
                 */
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),

                /**
                 * Cek apakah format waktu menggunakan 24-hour format
                 */
                DateFormat.is24HourFormat(this));

        timePickerDialog.show();

    }

    private void saveConcertInformation() {

        if(resultUri != null){

            Intent intent = new Intent(AddConcert_First_A.this, AddConcert_Second_A.class);

            intent.putExtra("concertName", concertName);
            intent.putExtra("concertMainGenre", mainGenre);
            intent.putExtra("concertDescription", concertDescription);
            intent.putExtra("concertDuration", concertDurationStr);
            intent.putExtra("concertDate", concertDate);
            intent.putExtra("concertTime", concertTime);
            intent.putExtra("imageUri", resultUri.toString());

            startActivity(intent);


        } else {
            Log.d("resultUri", "uri tidak masuk");
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            concertImage.setImageURI(resultUri);
        } else {
//            Toast.makeText(getActivity(), "in", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }



}