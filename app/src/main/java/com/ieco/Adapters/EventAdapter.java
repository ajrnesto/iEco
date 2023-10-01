package com.ieco.Adapters;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ieco.Objects.Event;
import com.ieco.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.eventViewHolder> {

    private final FirebaseFirestore DB = FirebaseFirestore.getInstance();
    private final FirebaseAuth AUTH = FirebaseAuth.getInstance();
    private final FirebaseUser USER = AUTH.getCurrentUser();
    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    Context context;
    private Activity activity;
    ArrayList<Event> arrEvent;
    private OnClick mOnClick;

    public EventAdapter(Context context, Activity activity, ArrayList<Event> arrEvent, OnClick onEventListener) {
        this.context = context;
        this.activity = activity;
        this.arrEvent = arrEvent;
        this.mOnClick = onEventListener;
    }

    @NonNull
    @Override
    public eventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_event, parent, false);
        return new eventViewHolder(view, mOnClick);
    }

    @Override
    public void onBindViewHolder(@NonNull eventViewHolder holder, int position) {
        Event event = arrEvent.get(position);

        String banner = event.getBannerFileName();
        String eventName = event.getEventName();
        long schedule = event.getSchedule();

        storageRef.child("images/"+banner).getDownloadUrl()
                .addOnSuccessListener(uri -> Picasso.get().load(uri).resize(120,120).centerCrop().into(holder.imgEvent))
                .addOnFailureListener(e -> Picasso.get().load("https://place-hold.it/120x120/fffffc/bbbba1?text=No%20Image").fit().into(holder.imgEvent));

        holder.tvEventName.setText(eventName);
        SimpleDateFormat sdfSchedule = new SimpleDateFormat("MMMM dd, yyyy");
        holder.tvSchedule.setText(sdfSchedule.format(schedule));

        boolean CURRENT_USER_IS_ADMIN = USER != null;
        if (CURRENT_USER_IS_ADMIN) {
            holder.btnIcon.setIcon(holder.btnIcon.getResources().getDrawable(R.drawable.pencil_24));
        }
        else {
            holder.btnIcon.setIcon(holder.btnIcon.getResources().getDrawable(R.drawable.angle_right_24));
        }
    }

    @Override
    public int getItemCount() {
        return arrEvent.size();
    }

    public class eventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        OnClick onEventListener;
        RoundedImageView imgEvent;
        TextView tvEventName, tvSchedule;
        MaterialButton btnIcon;

        public eventViewHolder(@NonNull View itemView, OnClick onEventListener) {
            super(itemView);

            imgEvent = itemView.findViewById(R.id.imgEvent);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvSchedule = itemView.findViewById(R.id.tvSchedule);
            btnIcon = itemView.findViewById(R.id.btnIcon);

            this.onEventListener = onEventListener;
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            onEventListener.onClick(getLayoutPosition());
        }
    }

    public interface OnClick{
        void onClick(int position);
    }
}
