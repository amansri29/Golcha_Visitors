package com.golchaminerals.visitors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Aman.Srivastav on 05-10-2018.
 */


public class VisitorsListAdaptor extends ArrayAdapter<String> {
    Context context;

    private  ArrayList<String> name =new ArrayList<String>();
    private  ArrayList<String> vehicleNO =new ArrayList<String>();
    private  ArrayList<String> inTime =new ArrayList<String>();
    private  ArrayList<String> id =new ArrayList<String>();
    String timeOfVisitOut;
    String dateOfVisitOut;
    String id1;

    public VisitorsListAdaptor(@NonNull Context context, int resource,  ArrayList<String> name,
                               ArrayList<String> vehicleNO, ArrayList<String> inTime, ArrayList<String> id) {
        super(context, resource, name);
        this.name = name;
        this.vehicleNO = vehicleNO;
        this.inTime = inTime;
        this.context = context;
        this.id = id;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.visitors_gate_out_list, null);
        }

        TextView visitorName = (TextView) v.findViewById(R.id.name);
        TextView vehicle = (TextView) v.findViewById(R.id.vehicle_no);
        TextView inTimeDate = (TextView) v.findViewById(R.id.in_time);
        Button getOutButton = (Button) v.findViewById(R.id.get_out_button);

        visitorName.setText(name.get(position));
        vehicle.setText(vehicleNO.get(position));
        inTimeDate.setText(inTime.get(position));

        getOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
                timeOfVisitOut = df.format(cal.getTime());
                SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
                dateOfVisitOut = df2.format(cal.getTime());
                id1 = id.get(position);
                new UpdateOutTime().execute();
            }
        });

        return v;
    }



    public class UpdateOutTime extends AsyncTask<Void, Void, Void> {
        private static final String TAG = "UpdateOutTime";
        public Connection connection;
        boolean uploadStatus = false;

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
                String commands = "Update dbo.VisitorsData2 set " +
                        "OutTime = '" +  timeOfVisitOut + "', OutDate = '" + dateOfVisitOut +  "' where Id = "+ id1  ;
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
            Toast.makeText(context, "Successfully Noted ", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, VistorsOut.class);
            context.startActivity(intent);
            ((Activity)context).finish();
        } else {
            Toast.makeText(context, "Error, Please try again", Toast.LENGTH_SHORT).show();
        }

        }
    }
}
