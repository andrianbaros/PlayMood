package com.example.playmood.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.playmood.MusicPlayerActivity;
import com.example.playmood.R;
import com.example.playmood.model.AlbumModel;
import com.example.playmood.model.FavoriteModel;
import com.example.playmood.model.UserModel;
import com.example.playmood.network.FavoritesService;
import com.example.playmood.network.RetrofitClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private ImageView imageViewProfile;
    private TextView textEmail, textUsername;
    private LinearLayout playlistContainerProfile;

    private DatabaseReference usersRef;
    private FirebaseUser currentUser;
    private FavoritesService favoritesService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imageViewProfile = view.findViewById(R.id.imageViewProfile);
        textEmail = view.findViewById(R.id.textEmail);
        textUsername = view.findViewById(R.id.textUsername);
        playlistContainerProfile = view.findViewById(R.id.playlistContainerProfile );

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        // Gunakan RetrofitClient yang sudah ada
        favoritesService = RetrofitClient.getSupabaseInstance().create(FavoritesService.class);

        if (currentUser != null) {
            loadUserData();
            loadFavoritePlaylistsFromSupabase();
        }

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

                    if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                        Glide.with(requireContext())
                                .load(user.getProfileImageUrl())
                                .placeholder(R.drawable.person)
                                .error(R.drawable.person)
                                .into(imageViewProfile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to read user data: " + error.getMessage());
            }
        });
    }

    private void loadFavoritePlaylistsFromSupabase() {
        String userId = currentUser.getUid();
        Log.d("SUPABASE", "Memuat data favorit untuk userId: " + userId);

        favoritesService.getFavoritesByUser("eq." + userId, "*").enqueue(new Callback<List<FavoriteModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<FavoriteModel>> call, @NonNull Response<List<FavoriteModel>> response) {
                Log.d("SUPABASE", "Kode HTTP: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    List<FavoriteModel> favorites = response.body();
                    Log.d("SUPABASE", "Jumlah lagu favorit: " + favorites.size());

                    // Ganti nama variabel ke yang sesuai di XML
                    playlistContainerProfile.removeAllViews();

                    for (FavoriteModel fav : favorites) {
                        AlbumModel album = new AlbumModel(
                                fav.getTitle(),
                                java.util.Collections.singletonList(fav.getArtist()),
                                fav.getCover_url(),
                                fav.getSong_url(),
                                null
                        );

                        // Inflate item
                        View itemView = LayoutInflater.from(getContext())
                                .inflate(R.layout.item_playlist, playlistContainerProfile, false);

                        ImageView imageView = itemView.findViewById(R.id.imageView);
                        TextView title = itemView.findViewById(R.id.textViewTitle);
                        TextView artist = itemView.findViewById(R.id.textViewSubtitle);

                        title.setText(album.getTitle());
                        artist.setText(album.getArtist());

                        Glide.with(requireContext())
                                .load(album.getCoverUrl())
                                .placeholder(R.drawable.rounded_image_background)
                                .error(R.drawable.rounded_image_background)
                                .into(imageView);

                        itemView.setOnClickListener(v -> {
                            Intent intent = new Intent(requireContext(), MusicPlayerActivity.class);
                            intent.putExtra("TITLE", album.getTitle());
                            intent.putExtra("ARTIST", album.getArtist());
                            intent.putExtra("COVER", album.getCoverUrl());
                            intent.putExtra("SONGURL", album.getSongUrl());
                            startActivity(intent);
                        });

                        playlistContainerProfile.addView(itemView);
                    }
                } else {
                    Log.e("SUPABASE", "Gagal memuat data favorit. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<FavoriteModel>> call, @NonNull Throwable t) {
                Log.e("SUPABASE", "Request gagal: " + t.getMessage(), t);
            }
        });
    }
}
