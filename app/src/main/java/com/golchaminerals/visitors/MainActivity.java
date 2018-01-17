package com.golchaminerals.visitors;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.R.layout.simple_dropdown_item_1line;
import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {
    Spinner visitPurpose;
    ImageView takeImage;
    EditText firstName, lastName, mobileNumber, inTime, remarks, noOfPeople, fromLocation;
    AutoCompleteTextView whomToVisit;
    Button submit;
    private static final int CAMERA_REQUEST = 1888;
    private String TAG = MainActivity.class.getSimpleName();
    private String timeOfVisit, dateOfVisit;
    String firstNameS, lastNameS, mobileNumberS, whomToVisitS, inTimeS, remarksS, visitPurposeS, profileImageS, noOfPeopleS, fromLocationS;
    ProgressDialog progressDialog;
    Bitmap photoBitMap;
    boolean spinnerDefaultSelection = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitors);
        if (!isLockState()) {
            startLockTask();
        }
        firstName = (EditText) findViewById(R.id.first_name);
        lastName = (EditText) findViewById(R.id.last_name);
        mobileNumber = (EditText) findViewById(R.id.mobile_number);
        whomToVisit = (AutoCompleteTextView) findViewById(R.id.whom_to_meet);
        inTime = (EditText) findViewById(R.id.in_time);
        remarks = (EditText) findViewById(R.id.remarks);
        fromLocation =(EditText) findViewById(R.id.from);
        noOfPeople = (EditText) findViewById(R.id.total_person);
        submit = (Button) findViewById(R.id.submit);
        visitPurpose = (Spinner) findViewById(R.id.visit_purpose);
        ImageView image = (ImageView) findViewById(R.id.logout);


//        firstName.setHint(getMandatoryTag("First Name"));
//        lastName.setHint(getMandatoryTag("Last Name"));
//        mobileNumber.setHint(getMandatoryTag("Mobile Number"));
//        whomToVisit.setHint(getMandatoryTag("Whom to Visit"));


        image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    stopLockTask();     // unpin the screen
                }catch (SecurityException e){
                    Log.d(TAG, "securityException: " + e.getLocalizedMessage());
                }
                Log.i(TAG, "Button Clicked");
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putBoolean("LoggedIn", false);
                editor.commit();
                Intent i = new Intent(MainActivity.this, Login.class);
                startActivity(i);
                finish();
            }

        });

        takeImage = (ImageView) findViewById(R.id.take_image);
        takeImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                cameraIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
//                cameraIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
//                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                Intent intent = new Intent(MainActivity.this, com.golchaminerals.visitors.Camera.class);
                startActivity(intent);
                finish();

            }

        });

        if (getIntent().hasExtra("imageFile")) {
//            Bitmap _bitmap = BitmapFactory.decodeByteArray(
//                    getIntent().getByteArrayExtra("byteArray"),0,getIntent().getByteArrayExtra("byteArray").length);
//            takeImage.setImageBitmap(_bitmap);
            File file = (File) getIntent().getExtras().get("imageFile");
            Intent mediaStoreUpdateIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaStoreUpdateIntent.setData(Uri.fromFile(file));
            sendBroadcast(mediaStoreUpdateIntent);
            Bitmap _bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            photoBitMap = _bitmap;
            takeImage.setImageBitmap(_bitmap);


            photoBitMap = Bitmap.createScaledBitmap(photoBitMap, 100, 100, false);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            photoBitMap.compress(Bitmap.CompressFormat.JPEG, 80, bytes);

//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            boolean compress = photoBitMap.compress(Bitmap.CompressFormat.PNG, 50, stream);
            profileImageS = Base64.encodeToString(bytes.toByteArray(), Base64.DEFAULT);

            Log.i(TAG, profileImageS);
        }

        whomToVisit.setFocusableInTouchMode(true);
        whomToVisit.requestFocus();
        whomToVisit.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    visitPurpose.setFocusableInTouchMode(true);
                    visitPurpose.requestFocus();
                    visitPurpose.performClick();
                    return true;
                }
                return false;
            }
        });





        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.whomToVisit));
        whomToVisit.setThreshold(1);
        whomToVisit.setAdapter(adapter);



        firstName.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                if(profileImageS == null)
                {
                    Intent intent = new Intent(MainActivity.this, com.golchaminerals.visitors.Camera.class);
                    startActivity(intent);
                    finish();
                }

            }
        });


        whomToVisit.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
                timeOfVisit = df.format(cal.getTime());
                SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
                dateOfVisit = df2.format(cal.getTime());
                SimpleDateFormat df3 = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd");

                if (whomToVisit.getText() != null)

                    if (s.length() != 0)
                        inTime.setText(df3.format(cal.getTime()));
                    else
                        inTime.setText("In Time");
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                if (!isConnected)   // no internet connectivity
                {
                    Toast.makeText(MainActivity.this, "No Internet, Please check your internet connectivity.", Toast.LENGTH_SHORT).show();
                } else {


                    if (photoBitMap != null) {

                        if (firstName.getText().toString().trim().equals("") | lastName.getText().toString().trim().equals("") | mobileNumber.getText().toString().trim().equals("") | whomToVisit.getText().toString().trim().equals("") | noOfPeople.getText().toString().trim().equals("") | fromLocation.getText().toString().trim().equals("")) {
                            Toast.makeText(MainActivity.this, "Please fields are mandatory except Remarks. Please complete all the fields.", Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog = new ProgressDialog(MainActivity.this);
//            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            progressDialog.setMessage("Please wait your data is being uploaded");
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            firstNameS = firstName.getText().toString();
                            lastNameS = lastName.getText().toString();
                            mobileNumberS = mobileNumber.getText().toString();
                            whomToVisitS = whomToVisit.getText().toString();
                            inTimeS = timeOfVisit;
                            visitPurposeS = visitPurpose.getSelectedItem().toString();
                            remarksS = remarks.getText().toString();
                            noOfPeopleS = noOfPeople.getText().toString();
                            fromLocationS = fromLocation.getText().toString();
                            new uploadDataToServer().execute();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Please capture your profile image.", Toast.LENGTH_SHORT).show();
                    }


                }

            }
        });


        visitPurpose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                    remarks.setFocusableInTouchMode(true);       // commented as its not a mandatory field
//                    remarks.requestFocus();
//                    remarks.performClick();
//

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });

        takeImage.setFocusableInTouchMode(true);
        takeImage.requestFocus();


    }


//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
//            photoBitMap = (Bitmap) data.getExtras().get("data");
//            takeImage.setImageBitmap(photoBitMap);
//        }
//    }


    public class uploadDataToServer extends AsyncTask<Void, Void, Void> {
        public Connection connection;
        boolean uploadStatus = false;


        @Override
        protected void onPreExecute() {
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            boolean compress = photoBitMap.compress(Bitmap.CompressFormat.PNG, 50, stream);
//            profileImageS = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String userName = sharedPrefs.getString("UserName", "nu");
                String passWord2 = sharedPrefs.getString("Password", "np");
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                connection = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.36.42:1433/VisitorsList;user=" + userName + ";password=" + passWord2);
                Log.i(TAG, " Connection Open Now");
                String commands = "INSERT INTO dbo.VisitorsData\n" +
                        "VALUES ('" + firstNameS + "','" + lastNameS + "','" + mobileNumberS + "','" + whomToVisitS + "','" + inTimeS + "','"  + dateOfVisit + "','" + visitPurposeS + "','" + remarksS + "','" + profileImageS + "','"  + fromLocationS + "','"  + noOfPeopleS + "')";
                PreparedStatement preStmt = connection.prepareStatement(commands);
                preStmt.executeUpdate();
                Log.i(TAG, "Uploaded Successfully");
                uploadStatus = true;
            } catch (Exception e) {
                Log.w("Error connection", "" + e.getMessage());
                uploadStatus = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            if (uploadStatus) {
                Toast.makeText(MainActivity.this, "Thanks for Visiting Golcha Group.", Toast.LENGTH_SHORT).show();
                uploadStatus = false;
                progressDialog.dismiss();
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(MainActivity.this, "Error, Please try again", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }


    }


    Spannable getMandatoryTag(String tagName)
    {
        Spannable WordtoSpan = new SpannableString("* " + tagName);
        WordtoSpan.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return  WordtoSpan;
    }


    @TargetApi(Build.VERSION_CODES.M)
    public boolean isLockState() {
        boolean isLocked = false;
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        try {

            if (am.getLockTaskModeState() == ActivityManager.LOCK_TASK_MODE_NONE) {
                Log.d(TAG, "Lock task mode is not active.");
            } else {
                Log.d(TAG, "Lock task mode is active.");
                isLocked = true;
            }
        } catch (Exception e) {
            Log.d(TAG, "exception: ",e);
        }
        return isLocked;
    }

}
