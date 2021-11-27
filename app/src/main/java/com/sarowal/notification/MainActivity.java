package com.sarowal.notification;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.sarowal.notification.fragment.HomeFragment;
import com.sarowal.notification.fragment.LoginFragment;
import com.sarowal.notification.fragment.NotificationFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoginFragment loginFragment = new LoginFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, loginFragment, "Login")
                .commit();

        findViewById(R.id.nav_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new LoginFragment(), "Login")
                        .commit();
            }
        });

        findViewById(R.id.nav_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment(), "Home")
                        .commit();
            }
        });

        findViewById(R.id.nav_notifications).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new NotificationFragment(), "Notification")
                        .commit();
            }
        });

    }
}