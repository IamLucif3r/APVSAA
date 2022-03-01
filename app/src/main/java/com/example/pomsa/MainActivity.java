package com.example.pomsa;



import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.pomsa.fragment.ExploreFragment;
import com.example.pomsa.fragment.HomeFragment;
import com.example.pomsa.fragment.LibraryFragement;
import com.example.pomsa.fragment.SubscriptionFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    FrameLayout frameLayout;
    Fragment fragment;
    ImageView userprofile_image;
    FirebaseAuth auth;
    FirebaseUser user;
    GoogleSignInClient mGoogleSignInClient;
    AppBarLayout appBarLayout;
    private static final int RC_SIGN_IN = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("");

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        frameLayout = findViewById(R.id.frame_layout);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        appBarLayout = findViewById(R.id.appBar);


        userprofile_image= findViewById(R.id.user_profile_image);
        GoogleSignInOptions gsc = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gsc);
        showFragment();
        userprofile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user != null){
                   startActivity(new Intent(MainActivity.this,AccountActivity.class));
                    getProfileImage();
                }
                else
                    showDialouge();
                userprofile_image.setImageResource(R.drawable.ic_baseline_account_circle_24);
            }

            private void showDialouge() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(true);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View view = LayoutInflater.from(getApplicationContext()).inflate((R.layout.item_signin_dialogue),viewGroup,false);
                builder.setView(view);

                TextView txt_google_signin = view.findViewById(R.id.txt_google_signin);
                txt_google_signin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        signIn();
                    }
                });
                builder.create().show();
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        HomeFragment homeFragment = new HomeFragment();
                        selectedFragment(homeFragment);
                        break;
                    case R.id.explore:
                        ExploreFragment exploreFragment = new ExploreFragment();
                        selectedFragment(exploreFragment);
                        break;
                    case R.id.publish:
                        Toast.makeText(MainActivity.this, "Video Uploading Feature", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.subscription:
                        SubscriptionFragment subscriptionFragment = new SubscriptionFragment();
                        selectedFragment(subscriptionFragment);
                        break;
                    case R.id.library:
                        LibraryFragement libraryFragement = new LibraryFragement();
                        selectedFragment(libraryFragement);
                        break;
                }
            return false;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.home);
}

    private void getProfileImage() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String p = snapshot.child("profile").getValue().toString();
                    Picasso.get().load(p).placeholder(R.drawable.ic_baseline_account_circle_24).into(userprofile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signIn() {
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent,RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);

            auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        //Store in firebase
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("username",account.getDisplayName());
                        map.put("email",account.getEmail());
                        map.put("profile",String.valueOf(account.getPhotoUrl()));
                        map.put("uid", firebaseUser.getUid());
                        map.put("search",account.getDisplayName().toLowerCase());

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
                        reference.child(firebaseUser.getUid()).setValue(map);

                    }
                    else{
                        Toast.makeText(MainActivity.this, task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }catch (Exception e){
            Toast.makeText(this,e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }}

    private void selectedFragment(Fragment fragment) {
        appBarLayout.setVisibility(View.VISIBLE);
        setStatusBarColor("#FFFFFF");
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.notification:
                Toast.makeText(this, "Notification",Toast.LENGTH_SHORT).show();
                break;
            case R.id.search:
                Toast.makeText(this, "Search",Toast.LENGTH_SHORT).show();
                break;
            case R.id.account:
                Toast.makeText(this,"Account",Toast.LENGTH_SHORT).show();
                break;

            default:
                return super.onOptionsItemSelected(item);

        }return false;
    }

    private void showFragment(){
        String type = getIntent().getStringExtra("type");
        if(type!=null)
        {
            switch (type){
                case "channel":
                    setStatusBarColor("#99FF0000");
                    appBarLayout.setVisibility(View.GONE);
                    fragment = ChannelDashboardFragment.newInstance();
                    break;
            }
            if(fragment!=null){
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_layout,fragment).commit();
            }else{
                Toast.makeText(this,"Something Went Wrong...",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setStatusBarColor(String color){
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor(color));
    }
}








