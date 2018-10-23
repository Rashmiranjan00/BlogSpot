package com.rashmi.rrp.blogspot;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {

    public List<Comments> commentsList;
    public Context context;
    public FirebaseFirestore firebaseFirestore;

    public CommentsRecyclerAdapter(List<Comments> commentsList) {

        this.commentsList = commentsList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        return new CommentsRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        String commentMessage = commentsList.get(position).getMessage();
        holder.setCommentMessage(commentMessage);

        String userId = commentsList.get(position).getUserId();

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


    }

    @Override
    public int getItemCount() {

        if (commentsList != null) {

            return commentsList.size();

        } else {

            return 0;

        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView commentMessage;
        private CircleImageView commentProfileImage;
        private TextView commentUsername;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setCommentMessage(String message) {

            commentMessage = mView.findViewById(R.id.commentMessage);
            commentMessage.setText(message);

        }

        public void setUserData(String userName, String userImage) {

            commentProfileImage = mView.findViewById(R.id.commentImage);
            commentUsername = mView.findViewById(R.id.commentUsername);

            commentUsername.setText(userName);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.avatar);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(userImage).into(commentProfileImage);

        }
    }
}
