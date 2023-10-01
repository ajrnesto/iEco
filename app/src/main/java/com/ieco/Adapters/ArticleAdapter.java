package com.ieco.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ieco.Objects.Article;
import com.ieco.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.articleViewHolder> {

    private final FirebaseFirestore DB = FirebaseFirestore.getInstance();
    private final FirebaseAuth AUTH = FirebaseAuth.getInstance();
    private final FirebaseUser USER = AUTH.getCurrentUser();
    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    Context context;
    private Activity activity;
    ArrayList<Article> arrArticle;
    private OnClick mOnClick;

    public ArticleAdapter(Context context, Activity activity, ArrayList<Article> arrArticle, OnClick onArticleListener) {
        this.context = context;
        this.activity = activity;
        this.arrArticle = arrArticle;
        this.mOnClick = onArticleListener;
    }

    @NonNull
    @Override
    public articleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        boolean USER_IS_ADMIN = USER != null;
        if (USER_IS_ADMIN) {
            view = LayoutInflater.from(context).inflate(R.layout.cardview_article, parent, false);
        }
        else {
            view = LayoutInflater.from(context).inflate(R.layout.cardview_article_grid, parent, false);
        }

        return new articleViewHolder(view, mOnClick);
    }

    @Override
    public void onBindViewHolder(@NonNull articleViewHolder holder, int position) {
        Article article = arrArticle.get(position);

        String image = article.getImageFileName();
        String articleTitle = article.getArticleTitle();
        String content = article.getContent();

        boolean USER_IS_ADMIN = USER != null;
        storageRef.child("images/"+image).getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    if (USER_IS_ADMIN) {
                        Picasso.get().load(uri).resize(120, 120).centerCrop().into(holder.imgArticle);
                    }
                    else {
                        Picasso.get().load(uri).resize(150, 100).centerCrop().into(holder.imgArticle);
                    }
                })
                .addOnFailureListener(e -> {
                    if (USER_IS_ADMIN) {
                        Picasso.get().load("https://place-hold.it/120x120/fffffc/bbbba1?text=No%20Image").fit().into(holder.imgArticle);
                    }
                    else {
                        Picasso.get().load("https://place-hold.it/120x80/fffffc/bbbba1?text=No%20Image").fit().into(holder.imgArticle);
                    }
                });

        holder.tvArticleTitle.setText(articleTitle);
        holder.tvContent.setText(content);
    }

    @Override
    public int getItemCount() {
        return arrArticle.size();
    }

    public class articleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        OnClick onArticleListener;
        RoundedImageView imgArticle;
        TextView tvArticleTitle, tvContent;

        public articleViewHolder(@NonNull View itemView, OnClick onArticleListener) {
            super(itemView);

            imgArticle = itemView.findViewById(R.id.imgArticle);
            tvArticleTitle = itemView.findViewById(R.id.tvArticleTitle);
            tvContent = itemView.findViewById(R.id.tvContent);

            this.onArticleListener = onArticleListener;
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            onArticleListener.onClick(getLayoutPosition());
        }
    }

    public interface OnClick{
        void onClick(int position);
    }
}
