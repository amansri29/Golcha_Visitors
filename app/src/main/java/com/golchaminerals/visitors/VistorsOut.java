package com.golchaminerals.visitors;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class VistorsOut extends AppCompatActivity {
    private static final String TAG = "VistorsOut";
    ArrayList<String> name =new ArrayList<String>();
    ArrayList<String> vehicleNO =new ArrayList<String>();
    ArrayList<String> inTime =new ArrayList<String>();
    ArrayList<String> id =new ArrayList<String>();
    private ListView visitorsListView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vistors_out);

        visitorsListView = (ListView) findViewById(R.id.visitors_list);

        visitorsListView.setEmptyView(findViewById(R.id.empty_list_item));

        TextView textView = new TextView(this);
        textView.setText("Visitor List for Exit");
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        textView.setTextSize(22);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        visitorsListView.addHeaderView(textView);


    }


    @Override
    protected void onResume() {
        super.onResume();
        new GetVisitorData().execute();
    }

    public class GetVisitorData extends AsyncTask<Void, Void, Void> {
        public Connection connection;
        ResultSet resultSet;
        String inputLocation;

        @Override
        protected void onPreExecute() {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            inputLocation = sharedPrefs.getString("inputLocation", "No Location");
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String userName = sharedPrefs.getString("UserName", "nu");
                String passWord2 = sharedPrefs.getString("Password", "np");
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                connection = DriverManager.getConnection("jdbc:jtds:sqlserver://45.114.141.43:1433/VisitorsList;user=" + userName + ";password=" + passWord2);
                Log.i(TAG, " Connection Open Now");

                Statement stmt = connection.createStatement();

                resultSet = stmt.executeQuery("Select Id,Name, VehicleNo, InTime, InDate from [dbo].[VisitorsData2]  where EntryFromLocation = '" + inputLocation + "' and OutTime = '' ");


                while (resultSet.next()) {
                        Log.i(TAG, "onPostExecute: " + resultSet.getString("Name") + " " + resultSet.getString("VehicleNo")
                                + " " + resultSet.getString("InTime") + " " + resultSet.getString("InDate"));
                        name.add(resultSet.getString("Name"));
                        vehicleNO.add(resultSet.getString("VehicleNo"));
                        id.add(resultSet.getString("Id"));
                        inTime.add(resultSet.getString("InTime") + " " + resultSet.getString("InDate"));
                }

            } catch (Exception e) {
                Log.w("Error connection", "" + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {


                // get data from the table by the ListAdapter
                VisitorsListAdaptor customAdapter = new VisitorsListAdaptor(VistorsOut.this, R.layout.visitors_gate_out_list, name, vehicleNO, inTime, id);
                visitorsListView.setAdapter(customAdapter);


//            } catch (SQLException e) {
//                e.printStackTrace();
//            }

        }


    }
}
