package com.lawrence254.autosign.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.lawrence254.autosign.R;
import com.lawrence254.autosign.model.Attendance;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FirebaseAttendanceAdapter extends RecyclerView.Adapter<FirebaseAttendanceAdapter.AttendanceViewHolder> {

    private ArrayList<Attendance>mAttendance = new ArrayList<>();
    private Context mContext;

    public FirebaseAttendanceAdapter (Context context,ArrayList<Attendance> attend){
        mAttendance = attend;
        mContext = context;
    }

    @NonNull
    @Override
    public FirebaseAttendanceAdapter.AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_style, parent, false);
        FirebaseAttendanceAdapter.AttendanceViewHolder viewHolder = new FirebaseAttendanceAdapter.AttendanceViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FirebaseAttendanceAdapter.AttendanceViewHolder holder, int position) {
        holder.bindAttendance(mAttendance.get(position));
    }

    @Override
    public int getItemCount() {

        return mAttendance.size();
    }

    public class AttendanceViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.email)TextView mAEmail;
        @BindView(R.id.time)TextView mATime;
        @BindView(R.id.day)TextView mADay;

        private Context mContext;

        public AttendanceViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this,itemView);
            mContext = itemView.getContext();
        }

        public void bindAttendance(Attendance attendance){
            mAEmail.setText(attendance.getStudent());
            mADay.setText(attendance.getDate());
            mATime.setText(attendance.getTime());
        }
    }
}