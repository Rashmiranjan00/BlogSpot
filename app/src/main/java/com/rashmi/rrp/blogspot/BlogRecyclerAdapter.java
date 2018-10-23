package com.rashmi.rrp.blogspot;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    public List<BlogPost> blogList;
    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public BlogRecyclerAdapter(List<BlogPost> blogList) {

        this.blogList = blogList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        final String blogPostId = blogList.get(position).BlogPostId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

        String descData = blogList.get(position).getDesc();
        holder.setDescText(descData);

        String imageUrl = blogList.get(position).getImageUrl();
        String thumbUri = blogList.get(position).getThumbUrl();
        holder.setBlogImage(imageUrl, thumbUri);

        String userId = blogList.get(position).getUserId();
        firebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    String userName = task.getResult().getString("name");
                    String userImage = task.getResult().getString("image");

                    holder.setUserData(userName, userImage);

                } else {
                    task.getException();
                }

            }
        });

        try {

            long millisecond = blogList.get(position).getTimestamp().getTime();
            String date = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
            holder.setDate(date);

        } catch (Exception e) {

            Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }

        //Likes Count
        firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (!queryDocumentSnapshots.isEmpty()) {

                    int count = queryDocumentSnapshots.size();
                    holder.updateLikesCount(count);

                } else {
                    holder.updateLikesCount(0);
                }

            }
        });

        //Get Likes
        firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if (documentSnapshot.exists()) {


                    holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.like));

                } else {
                    holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.like_gray));
                }

            }
        });

        //Likes Features
        holder.blogLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(!task.getResult().exists()) {

                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).set(likesMap);

                        } else {

                            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).delete();

                        }

                    }
                });


            }
        });

        holder.blogCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent commentIntent = new Intent(context, CommentActivity.class);
                commentIntent.putExtra("blogPostId", blogPostId);
                context.startActivity(commentIntent);

            }
        });

        firebaseFirestore.collection("Posts/" + blogPostId + "/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (!queryDocumentSnapshots.isEmpty()) {

                    int count = queryDocumentSnapshots.size();
                    holder.updateCommentsCount(count);

                } else {
                    holder.updateCommentsCount(0);
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView descView;
        private ImageView blogImageView;
        private TextView blogUsername;
        private CircleImageView blogProfileImage;
        private TextView blogDate;
        private ImageView blogLikeBtn;
        private TextView blogLikeCount;
        private ImageView blogCommentBtn;
        private TextView blogCommentCount;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            blogLikeBtn = mView.findViewById(R.id.blogLikeBtn);
            blogCommentBtn = mView.findViewById(R.id.blogCommentBtn);

        }

        public void setDescText(String descText) {

            descView = mView.findViewById(R.id.blogDesc);
            descView.setText(descText);

        }

        public void setBlogImage(String downloadUri, String thumbUri) {

            blogImageView = mView.findViewById(R.id.blogImage);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.avatar);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(downloadUri).thumbnail(
                    Glide.with(context).load(thumbUri)
            ).into(blogImageView);

        }

        public void setUserData(String name, String image){

            blogProfileImage = mView.findViewById(R.id.blogUserImage);
            blogUsername = mView.findViewById(R.id.blogUserName);

            blogUsername.setText(name);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.avatar);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(blogProfileImage);

        }

        public void setDate(String date) {

            blogDate = mView.findViewById(R.id.blogDate);
            blogDate.setText(date);

        }

        public void updateLikesCount(int count) {

            blogLikeCount = mView.findViewById(R.id.blogLikeCount);
            blogLikeCount.setText(count + " likes");

        }

        public void updateCommentsCount(int count) {

            blogCommentCount = mView.findViewById(R.id.blogCommentCount);
            blogCommentCount.setText(count + " comments");

        }
    }

}
