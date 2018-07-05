package com.lawrence254.autosign;

import android.bluetooth.le.AdvertisingSetCallback;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @BindView(R.id.ser)TextView mSer;


    public static final String CLIENT_NAME="TEACHER";
    String SERVICE_ID ="";

    GoogleApiClient mGoogleApiClient;
    private String mEndpoint;

    private PayloadCallback mPayloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(String endpoint, Payload payload) {
            Log.e("Moringa: ", new String(payload.asBytes()));
            sendMessage("Arrived At: " + new String(payload.asBytes()));
        }

        @Override
        public void onPayloadTransferUpdate(String endpoint, PayloadTransferUpdate payloadTransferUpdate) {}
    };

    private final ConnectionLifecycleCallback mConnectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                    mEndpoint = endpointId;

//                    PayloadCallback mPayloadCallback = null;
                    Nearby.Connections.acceptConnection(mGoogleApiClient, endpointId, mPayloadCallback)
                            .setResultCallback(new ResultCallback<com.google.android.gms.common.api.Status>() {
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

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startAdvertising();
    }
    private void startAdvertising() {
        Query query = reference.child("teacher")
                .child(user.getUid())
                .child("class");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SERVICE_ID = String.valueOf(dataSnapshot.getValue());

                Nearby.Connections.startAdvertising(
                        mGoogleApiClient,
                        CLIENT_NAME,
                        SERVICE_ID,
                        mConnectionLifecycleCallback,
                        new AdvertisingOptions(com.google.android.gms.nearby.connection.Strategy.P2P_STAR));
                mSer.setText(SERVICE_ID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void sendMessage(String message) {
        Nearby.Connections.sendPayload(mGoogleApiClient, mEndpoint, Payload.fromBytes(message.getBytes()));

    }



    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
 }
