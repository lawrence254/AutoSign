package com.lawrence254.studentsignin;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, View.OnClickListener {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @BindView(R.id.signclass)Button mSclass;
    @BindView(R.id.timedisp)TextView mTime;
    @BindView(R.id.serv)TextView ser;
    String SERVICE_ID="";

    GoogleApiClient mGoogleApiClient;
    private String mEndpoint;

    private PayloadCallback mPayloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(String endpoint, Payload payload) {
            Log.e("Moringa: ", new String(payload.asBytes()));

            addText(new String(payload.asBytes()));

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat mdformat = new SimpleDateFormat("yyyy / MM / dd ");
            SimpleDateFormat tmformar = new SimpleDateFormat("HH:mm");

            String time = tmformar.format(calendar.getTime());
            String date = mdformat.format(calendar.getTime());

            Map<String, String> map = new HashMap<>();
            map.put("time",time);
            map.put("date",date);
            map.put("student",user.getEmail());
            map.put("module",SERVICE_ID);

//            reference.child("attendance").child(user.getUid()).push().setValue(map);
            reference.child("attendance").push().setValue(map);
        }

        @Override
        public void onPayloadTransferUpdate(String endpoint, PayloadTransferUpdate payloadTransferUpdate) {}
    };

    private final ConnectionLifecycleCallback mConnectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                    Nearby.Connections.acceptConnection(mGoogleApiClient, endpointId, mPayloadCallback);
                    mEndpoint = endpointId;
                    Nearby.Connections.stopDiscovery(mGoogleApiClient);
                    Nearby.Connections.acceptConnection(mGoogleApiClient, endpointId, mPayloadCallback)
                            .setResultCallback(new ResultCallback<Status>() {
                                @Override
                                public void onResult(@NonNull com.google.android.gms.common.api.Status status) {
                                    if( status.isSuccess() ) {
                                        //Connection accepted
                                    }
                                }
                            });

                    Nearby.Connections.stopAdvertising(mGoogleApiClient);
                }

                @Override
                public void onConnectionResult(String endpointId, ConnectionResolution result) {}

                @Override
                public void onDisconnected(String endpointId) {}
            };

    private final EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(String endpointId, DiscoveredEndpointInfo discoveredEndpointInfo) {
            mEndpoint = endpointId;
            if (discoveredEndpointInfo.getServiceId().equals(SERVICE_ID)){
                Nearby.Connections.requestConnection(
                        mGoogleApiClient,
                        "STUDENT",
                        endpointId,
                        mConnectionLifecycleCallback);
                Toast.makeText(MainActivity.this, "Connected to: "+mEndpoint, Toast.LENGTH_SHORT).show();
                    mSclass.setEnabled(true);
                mSclass.setBackgroundResource(R.color.colorPrimaryDark);
                    ser.setText("Connected To TM");
            }

        }

        @Override
        public void onEndpointLost(String s) {
//            addtext("Connection Lost");
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this, this, this)
                .addApi(Nearby.CONNECTIONS_API)
                .enableAutoManage(this, this)
                .build();
        mSclass.setOnClickListener(this);
        ser.setText("Searching for TM Device");
        mSclass.setEnabled(false);
        mSclass.setBackgroundResource(R.color.colorDivider);
        mTime.setText("");
    }

    public void startDiscovery(){
        Query query = reference.child("students")
                .child(user.getUid())
                .child("class");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SERVICE_ID = String.valueOf(dataSnapshot.getValue());
                Nearby.Connections.startDiscovery(
                mGoogleApiClient,
                        SERVICE_ID,
                endpointDiscoveryCallback,
                new DiscoveryOptions(Strategy.P2P_STAR));
//                ser.setText(SERVICE_ID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
    });

    }

    private void addText(String text) {
        mTime.setText(text);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startDiscovery();
        Toast.makeText(this, "API Connected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Nearby.getConnectionsClient(getApplicationContext()).disconnectFromEndpoint(mEndpoint);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onClick(View v) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy / MM / dd ");
        SimpleDateFormat tmformar = new SimpleDateFormat("HH:mm");

        String time = tmformar.format(calendar.getTime());
        String date = mdformat.format(calendar.getTime());
        mTime.setText(time);

//        Map<String, String> map = new HashMap<>();
//        map.put("time",time);
//        map.put("date",date);
//        map.put("student",user.getEmail());
//        map.put("module",reference.child("students").child(user.getUid()).child("class").toString());
//
//        reference.child("attendance").child(user.getUid()).push().setValue(map);

        Nearby.Connections.sendPayload(mGoogleApiClient, mEndpoint, Payload.fromBytes(time.getBytes()));
//        Toast.makeText(this, "Sent Payload: "+new String(String.valueOf(Payload.fromBytes(time.getBytes()))), Toast.LENGTH_SHORT).show();
    }
}
