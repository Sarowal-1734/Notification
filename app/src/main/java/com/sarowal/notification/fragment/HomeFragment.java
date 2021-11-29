package com.sarowal.notification.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sarowal.notification.R;
import com.sarowal.notification.adapter.UserAdapter;
import com.sarowal.notification.databinding.FragmentHomeBinding;
import com.sarowal.notification.model.UserModel;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Show current userName
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            binding.textViewUsername.setText("Logged In: " + documentSnapshot.get("userName"));
                        }
                    });
        }

        // Display User List
        ArrayList<UserModel> userList = new ArrayList<>();
        db.collection("users")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            UserModel userModel = snapshot.toObject(UserModel.class);
                            if (!userModel.getUserId().equals(currentUser.getUid())) {
                                userList.add(userModel);
                            }
                        }
                    }
                });
        binding.recyclerViewHome.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerViewHome.setHasFixedSize(true);
        binding.recyclerViewHome.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        UserAdapter adapter = new UserAdapter(getActivity(), userList);
        binding.recyclerViewHome.setAdapter(adapter);

        // On User Clicked
        adapter.setOnItemClickListener(new UserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(UserModel userModel) {
                Bundle bundle = new Bundle();
                bundle.putString("ReceiverUserId", userModel.getUserId());
                ChatFragment fragment = new ChatFragment();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.fragment_container, fragment).commit();
            }
        });

        return binding.getRoot();
    }
}