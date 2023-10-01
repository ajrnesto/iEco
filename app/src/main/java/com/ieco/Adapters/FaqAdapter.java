package com.ieco.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ieco.Objects.Faq;
import com.ieco.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.faqViewHolder> {

    private final FirebaseFirestore DB = FirebaseFirestore.getInstance();
    private final FirebaseAuth AUTH = FirebaseAuth.getInstance();
    private final FirebaseUser USER = AUTH.getCurrentUser();

    Context context;
    private Activity activity;
    ArrayList<Faq> arrFaq;
    private OnClick mOnClick;

    public FaqAdapter(Context context, Activity activity, ArrayList<Faq> arrFaq, OnClick onFaqListener) {
        this.context = context;
        this.activity = activity;
        this.arrFaq = arrFaq;
        this.mOnClick = onFaqListener;
    }

    @NonNull
    @Override
    public faqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_faq, parent, false);
        return new faqViewHolder(view, mOnClick);
    }

    @Override
    public void onBindViewHolder(@NonNull faqViewHolder holder, int position) {
        Faq faq = arrFaq.get(position);

        String question = faq.getQuestion();
        String answer = faq.getAnswer();

        boolean USER_IS_ADMIN = USER != null;

        if (USER_IS_ADMIN) {
            holder.btnIcon.setIcon(holder.btnIcon.getResources().getDrawable(R.drawable.pencil_24));
            holder.tvAnswer.setVisibility(View.VISIBLE);
        }
        else {
            holder.btnIcon.setIcon(holder.btnIcon.getResources().getDrawable(R.drawable.angle_down_24));
            holder.tvAnswer.setVisibility(View.GONE);
        }

        holder.tvQuestion.setText("Q: " + question);
        holder.tvAnswer.setText("A: " + answer);
    }

    @Override
    public int getItemCount() {
        return arrFaq.size();
    }

    public class faqViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        OnClick onFaqListener;
        TextView tvQuestion, tvAnswer;
        MaterialButton btnIcon;

        public faqViewHolder(@NonNull View itemView, OnClick onFaqListener) {
            super(itemView);

            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvAnswer = itemView.findViewById(R.id.tvAnswer);
            btnIcon = itemView.findViewById(R.id.btnIcon);

            this.onFaqListener = onFaqListener;
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            onFaqListener.onClick(getLayoutPosition());

            boolean USER_IS_ADMIN = USER != null;
            if (!USER_IS_ADMIN) {
                boolean ANSWER_IS_VISIBLE = tvAnswer.getVisibility() == View.VISIBLE;
                if (ANSWER_IS_VISIBLE) {
                    tvAnswer.setVisibility(View.GONE);
                }
                else {
                    tvAnswer.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public interface OnClick{
        void onClick(int position);
    }
}
