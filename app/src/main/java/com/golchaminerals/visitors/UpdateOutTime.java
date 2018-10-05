package com.golchaminerals.visitors;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 * Created by Aman.Srivastav on 05-10-2018.
 */

public class UpdateOutTime extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "UpdateOutTime";
    public Connection connection;
    boolean uploadStatus = false;
    String inputLocation;
    String timeOfVisitOut;
    String dateOfVisitOut;
    String id;
    Context context;

    public UpdateOutTime(Context context, String timeOfVisitOut, String dateOfVisitOut, String id) {
      this.timeOfVisitOut = timeOfVisitOut;
      this.dateOfVisitOut = dateOfVisitOut;
      this.id = id;
      this.context = context;
    }

    @Override
    protected void onPreExecute() {
        
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
            String userName = sharedPrefs.getString("UserName", "nu");
            String passWord2 = sharedPrefs.getString("Password", "np");
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection("jdbc:jtds:sqlserver://45.114.141.43:1433/VisitorsList;user=" + userName + ";password=" + passWord2);
            Log.i(TAG, " Connection Open Now");
            String commands = "INSERT INTO dbo.VisitorsData2 (OutTime, OutDate) " +
                    "VALUES ('" +  timeOfVisitOut + "','" + dateOfVisitOut +  "') where Id = " + id ;
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
//        if (uploadStatus) {
//            Toast.makeText(MainActivity.this, "Thanks for Visiting Golcha Group.", Toast.LENGTH_SHORT).show();
//            uploadStatus = false;
//            progressDialog.dismiss();
//            Intent intent = new Intent(MainActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//
//        } else {
//            Toast.makeText(MainActivity.this, "Error, Please try again", Toast.LENGTH_SHORT).show();
//            progressDialog.dismiss();
//        }

    }
}
