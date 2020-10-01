package com.golchaminerals.visitors;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.golchaminerals.visitors.Retrofit.Retrourl;
import com.golchaminerals.visitors.Retrofit.ScanModelClass;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IdcardResultActivity extends AppCompatActivity {
    private TextView name , age , workingdep , workinglocation , validtill , workingauthorization ,labourtype;
    private ImageView imageView;
    private Button attendence_submit_btn;
    private String key ,user;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idcard_result);
        Intent intent = getIntent();
        imageView =(ImageView)findViewById(R.id.Scanned_imageView);
        name = (TextView)findViewById(R.id.scanned_name);
        workingdep = (TextView)findViewById(R.id.workingDepartment);
        workinglocation = (TextView)findViewById(R.id.workLocation);
        validtill = (TextView)findViewById(R.id.valid_till);
        workingauthorization = (TextView)findViewById(R.id.workAuthorization);
        labourtype = (TextView)findViewById(R.id.labourtype);
        attendence_submit_btn = (Button)findViewById(R.id.attendence_submit_btn);
        attendence_submit_btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Call<ScanModelClass> call = Retrourl.retrofitApiInterface().MakeAttendence(key ,user);
                        CallServer(call , "attendence");
                    }
                }
        );
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        user = sharedPrefs.getString("UserName","");

        progressDialog = new ProgressDialog(this);
//            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setMessage("Please wait while We are authenticating You.");
        progressDialog.setCancelable(false);


        key = intent.getStringExtra("scanresult");
        Call<ScanModelClass> call = Retrourl.retrofitApiInterface().callUserDetail(key );
        CallServer(call,"details");
        progressDialog.show();


    }

    private void CallServer(Call<ScanModelClass> call ,String callType) {
        call.enqueue(
                new Callback<ScanModelClass>() {
                    @Override
                    public void onResponse(Call<ScanModelClass> call, Response<ScanModelClass> response) {
                        //Log.i("vipuk", response.body().getmyName() +response.body().getPhoto());
                      if(!response.isSuccessful()){
                          try {
                              Log.i("earror: ",response.errorBody().string() );
                              Toast.makeText(getApplicationContext(),response.errorBody().string(),Toast.LENGTH_SHORT).show();
                          } catch (IOException e) {
                              e.printStackTrace();
                          }
                      }
                      else{
                          ScanModelClass scanModelClass =response.body();
                          if (callType.equals("details")) {
                              try {
                                  name.setText(scanModelClass.getmyName());
                                  workingdep.setText("Department"+" - "+scanModelClass.getWork_department());
                                  workinglocation.setText("Location"+" - "+scanModelClass.getWork_location());
                                  validtill.setText("Valid to"+" - "+scanModelClass.getValid_till());
                                  workingauthorization.setText("Authorization"+" - "+scanModelClass.getWork_authorization());
                                  labourtype.setText("Labour"+" - "+scanModelClass.getLabour_type());
                                  progressDialog.dismiss();
                              } catch (Exception e) {
                                  e.printStackTrace();
                                  progressDialog.dismiss();
                              }
                              try {
                                  new ImageLoad(scanModelClass.getPhoto(),imageView).execute();
                              } catch (Exception e) {
                                  e.printStackTrace();
                              }
                          } else {
                              try {
                                  Toast.makeText(getApplicationContext(),scanModelClass.getMessage(),Toast.LENGTH_SHORT).show();
                                  if(response.code()==200){
                                      Intent intent = new Intent(IdcardResultActivity.this,MainActivity.class);
                                      startActivity(intent);
                                      finish();
                                  }
                              } catch (Exception e) {
                                  e.printStackTrace();
                              }

                          }

                      }

                    }

                    @Override
                    public void onFailure(Call<ScanModelClass> call, Throwable t) {
                        Log.i( "onFailure: ", t.toString());

                    }
                }
        );

    }
}
class ImageLoad extends AsyncTask<Void , Void , Bitmap>{
    private String url;
    private ImageView imageView;
    public ImageLoad(String url, ImageView imageView) {
        this.url = url;
        this.imageView = imageView;
    }
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
        super.onPostExecute(bitmap);
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        try {
            URL urlConnection = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlConnection
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
