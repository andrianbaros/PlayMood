package com.example.playmood.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.playmood.R;
import com.example.playmood.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import android.widget.TextView;

public class UserListFragment extends Fragment {

    private LinearLayout userListContainer;
    private DatabaseReference usersRef;
    private String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        userListContainer = view.findViewById(R.id.userListContainer);

        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        loadUsers();

        return view;
    }

    private void loadUsers() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userListContainer.removeAllViews();

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    UserModel user = userSnapshot.getValue(UserModel.class);
                    String uid = userSnapshot.getKey();

                    if (uid != null && !uid.equals(currentUserId) && user != null) {
                        View userItem = LayoutInflater.from(getContext()).inflate(R.layout.item_user, userListContainer, false);

                        TextView textUsername = userItem.findViewById(R.id.textUsername);
                        TextView textEmail = userItem.findViewById(R.id.textEmail);
                        CircleImageView imageProfile = userItem.findViewById(R.id.imageProfile);

                        textUsername.setText(user.getUsername());
                        textEmail.setText(user.getEmail());

                        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                            Glide.with(requireContext())
                                    .load(user.getProfileImageUrl())
                                    .placeholder(R.drawable.person)
                                    .into(imageProfile);
                        }

                        // (Optional) Tambahkan klik listener di sini jika ingin fitur follow langsung
                        userListContainer.addView(userItem);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}