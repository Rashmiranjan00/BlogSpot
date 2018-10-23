package com.rashmi.rrp.blogspot;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class CommentActivity extends AppCompatActivity {

    private Toolbar commentToolbar;

    private EditText commentField;
    private ImageView commentPostBtn;

    private RecyclerView commentList;
    private CommentsRecyclerAdapter commentsRecyclerAdapter;
    private List<Comments> commentsList;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    private String blogPostId;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        commentToolbar = findViewById(R.id.commentToolbar);
        setSupportActionBar(commentToolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        currentUserId = firebaseAuth.getCurrentUser().getUid();
        blogPostId = getIntent().getStringExtra("blogPostId");

        commentField = findViewById(R.id.commentField);
        commentPostBtn = findViewById(R.id.commentPostBtn);
        commentList = findViewById(R.id.commentList);

        //RecyclerView Firebase List
        commentsList = new ArrayList<>();
        commentsRecyclerAdapter = new CommentsRecyclerAdapter(commentsList);
        commentList.setHasFixedSize(true);
        commentList.setLayoutManager(new LinearLayoutManager(this));
        commentList.setAdapter(commentsRecyclerAdapter);


        firebaseFirestore.collection("Posts/" + blogPostId + "/Comments")
                .addSnapshotListener(CommentActivity.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                        if (!documentSnapshots.isEmpty()) {

                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                if (doc.getType() == DocumentChange.Type.ADDED) {

                                    String commentId = doc.getDocument().getId();
                                    Comments comments = doc.getDocument().toObject(Comments.class);
                                    commentsList.add(comments);
                                    commentsRecyclerAdapter.notifyDataSetChanged();


                                }
                            }

                        }

                    }
                });

        commentPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String commentMessage = commentField.getText().toString();

                if (!commentMessage.isEmpty()) {

                    Map<String, Object> commentsMap = new HashMap<>();
                    commentsMap.put("message", commentMessage);
                    commentsMap.put("userId", currentUserId);
                    commentsMap.put("timestamp", FieldValue.serverTimestamp());

                    firebaseFirestore.collection("Posts/" + blogPostId + "/Comments").add(commentsMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {

                            if (!task.isSuccessful()) {

                                Toast.makeText(CommentActivity.this, "Error is posting comment: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();

                            } else {

                                commentField.setText("");

                            }

                        }
                    });

                }

            }
        });

    }
}
