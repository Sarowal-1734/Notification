package com.sarowal.notification.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sarowal.notification.R;

import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {

    Button buttonLogin, buttonCreateAccount, buttonLogout;
    EditText nameET, emailET, passwordET;
    //String name, email, password;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        buttonLogin = view.findViewById(R.id.button_login);
        buttonCreateAccount = view.findViewById(R.id.button_create_account);
        buttonLogout = view.findViewById(R.id.button_logout);
        nameET = (EditText) view.findViewById(R.id.editTextName);
        emailET = (EditText) view.findViewById(R.id.editTextEmail);
        passwordET = (EditText) view.findViewById(R.id.editTextPassword);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(emailET.getText().toString(), passwordET.getText().toString());
            }
        });

        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(emailET.getText().toString(), passwordET.getText().toString());
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() != null) {
                    mAuth.signOut();
                    Toast.makeText(getActivity(), "LOGOUT SUCCESS", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "NO USER LOGGED IN", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        storeDataToFireStore();
                    }
                });
    }

    private void storeDataToFireStore() {
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("Username", nameET.getText().toString());
        user.put("email", emailET.getText().toString());
        user.put("password", passwordET.getText().toString());
        // Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getActivity(), "Account Successfully Created!", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new HomeFragment(), "Home")
                                .commit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to create account!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(getActivity(), "Login Success!", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new HomeFragment(), "Home")
                                .commit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Login Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}