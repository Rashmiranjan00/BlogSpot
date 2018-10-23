package com.rashmi.rrp.blogspot;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private Toolbar mainToolbar;
    private FloatingActionButton addPostBtn;
    private BottomNavigationView mainBottomNav;

    private HomeFragment homeFragment;
    private NotificationFragment notificationFragment;
    private ProfileFragment profileFragment;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        mainToolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Blog spot");

        if(mAuth.getCurrentUser() != null) {

            mainBottomNav = findViewById(R.id.mainBottomNav);

            //FRAGMENTS
            homeFragment = new HomeFragment();
            notificationFragment = new NotificationFragment();
            profileFragment = new ProfileFragment();

            initializeFragment();

            mainBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.mainContainer);

                    switch (item.getItemId()) {

                        case R.id.bottomActionHome:
                            replaceFragment(homeFragment, currentFragment);
                            return true;

                        case R.id.bottomActionNotification:
                            replaceFragment(notificationFragment, currentFragment);
                            return true;

                        case R.id.bottomActionProfile:
                            replaceFragment(profileFragment, currentFragment);
                            return true;

                        default:
                            return false;


                    }
                }
            });

            addPostBtn = findViewById(R.id.addPostBtn);
            addPostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent newPostIntent = new Intent(MainActivity.this, newPostActivity.class);
                    startActivity(newPostIntent);

                }
            });
        }

    }

    private void initializeFragment() {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mainContainer, homeFragment);
        fragmentTransaction.add(R.id.mainContainer, notificationFragment);
        fragmentTransaction.add(R.id.mainContainer, profileFragment);

        fragmentTransaction.hide(notificationFragment);
        fragmentTransaction.hide(profileFragment);

        fragmentTransaction.commit();

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null) {
            sendToLogin();
        } else {

            currentUserId = mAuth.getCurrentUser().getUid();

            firebaseFirestore.collection("Users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful()) {

                        if (!task.getResult().exists()) {
                            Intent setupIntent = new Intent(MainActivity.this, setupActivity.class);
                            startActivity(setupIntent);
                            finish();
                        }

                    } else {

                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(MainActivity.this, "Error : " +errorMessage, Toast.LENGTH_LONG).show();

                    }

                }
            });

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.actionLogoutBtn:
                logout();
                return true;

            case R.id.actionSettingsBtn:
                Intent settingsIntent = new Intent(MainActivity.this, setupActivity.class);
                startActivity(settingsIntent);
                return true;

            default:
                return false;

        }

    }

    private void logout() {

        mAuth.signOut();
        sendToLogin();
    }

    private void sendToLogin() {

        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();

    }

    private void replaceFragment(Fragment fragment, Fragment currentFragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (fragment == homeFragment) {

            fragmentTransaction.hide(notificationFragment);
            fragmentTransaction.hide(profileFragment);

        }

        if (fragment == notificationFragment) {

            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(profileFragment);

        }

        if (fragment == profileFragment) {

            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(notificationFragment);

        }

        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();

    }

}
