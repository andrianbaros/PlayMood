package com.example.playmood.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.playmood.R;
import com.example.playmood.model.AlbumModel;
import com.example.playmood.model.SupabaseService;
import com.example.playmood.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileFragment extends Fragment {

    private ImageView imageViewProfile;
    private ImageView btnAddProfileImage;
    private TextView textEmail, textUsername, textFollowersCount, textFolloweesCount;
    private LinearLayout playlistContainer;

    private DatabaseReference usersRef;
    private FirebaseUser currentUser;

    private final ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) openGallery();
            }
    );

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null && currentUser != null) {
                        uploadProfileImageToSupabase(imageUri, currentUser.getUid());
                        Glide.with(requireContext()).load(imageUri).into(imageViewProfile);
                    }
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imageViewProfile = view.findViewById(R.id.imageViewProfile);
        btnAddProfileImage = view.findViewById(R.id.btnAddProfileImage);
        textEmail = view.findViewById(R.id.textEmail);
        textUsername = view.findViewById(R.id.textUsername);
        textFollowersCount = view.findViewById(R.id.textFollowersCount);
        textFolloweesCount = view.findViewById(R.id.textFolloweesCount);
        playlistContainer = view.findViewById(R.id.playlistContainer);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        if (currentUser != null) {
            loadUserData();
            loadPlaylistFromSupabase();
        }

        btnAddProfileImage.setOnClickListener(v -> checkPermissionAndOpenGallery());

        return view;
    }

    private void loadUserData() {
        String uid = currentUser.getUid();

        usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                if (user != null) {
                    textEmail.setText(user.getEmail());
                    textUsername.setText(user.getUsername());
                    textFollowersCount.setText(String.valueOf(user.getFollowers().size()));
                    textFolloweesCount.setText(String.valueOf(user.getFollowing().size()));

                    if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                        Glide.with(requireContext())
                                .load(user.getProfileImageUrl())
                                .placeholder(R.drawable.person)
                                .into(imageViewProfile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadPlaylistFromSupabase() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ebtorprhvaefmogaxhyq.supabase.co/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SupabaseService supabaseService = retrofit.create(SupabaseService.class);

        supabaseService.getAlbums().enqueue(new retrofit2.Callback<List<AlbumModel>>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<List<AlbumModel>> call, @NonNull retrofit2.Response<List<AlbumModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    playlistContainer.removeAllViews();
                    for (AlbumModel album : response.body()) {
                        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_playlist, playlistContainer, false);

                        ImageView imageView = itemView.findViewById(R.id.imageView);
                        TextView title = itemView.findViewById(R.id.textViewTitle);
                        TextView artist = itemView.findViewById(R.id.textViewSubtitle);

                        title.setText(album.getTitle());
                        artist.setText(album.getArtist());

                        String imageUrl = "https://ebtorprhvaefmogaxhyq.supabase.co/storage/v1/object/public/playmood/cover/" + album.getCoverUrl();
                        Glide.with(requireContext()).load(imageUrl).into(imageView);

                        playlistContainer.addView(itemView);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<List<AlbumModel>> call, @NonNull Throwable t) {}
        });
    }

    private void checkPermissionAndOpenGallery() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        } else {
            openGallery();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void uploadProfileImageToSupabase(Uri imageUri, String userId) {
        try {
            InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
            byte[] imageBytes = new byte[inputStream.available()];
            inputStream.read(imageBytes);
            inputStream.close();

            String fileName = userId + ".jpg";
            String url = "https://ebtorprhvaefmogaxhyq.supabase.co/storage/v1/object/public/playmood/profile/" + fileName;
            String token = "Bearer sb_secret_pPtrqnOgYBpKT-erW4dK0w_HgY1sLuD";

            RequestBody requestBody = RequestBody.create(imageBytes, MediaType.parse("image/jpeg"));

            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization", token)
                    .put(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String imageUrl = "https://ebtorprhvaefmogaxhyq.supabase.co/storage/v1/object/public/playmood/profile/" + fileName;
                        saveImageUrlToFirebase(imageUrl);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveImageUrlToFirebase(String imageUrl) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        usersRef.child("profileImageUrl").setValue(imageUrl);
    }
}