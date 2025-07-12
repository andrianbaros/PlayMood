package com.example.playmood.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.playmood.R;
import com.example.playmood.presenter.HomePresenter;
import com.example.playmood.presenter.contract.HomeContract;

public class HomeFragment extends Fragment implements HomeContract.View {

    private HomeContract.Presenter presenter;

    private TextView tabNews, tabVideo, tabArtists, tabPodcast;

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        presenter = new HomePresenter(this);
        presenter.onHomeLoaded();

        tabNews = view.findViewById(R.id.tab_news);
        tabVideo = view.findViewById(R.id.tab_video);
        tabArtists = view.findViewById(R.id.tab_artists);
        tabPodcast = view.findViewById(R.id.tab_podcast);

        // Default tampilkan News dan aktifkan tab-nya
        loadFragment(new NewsFragment());
        setActiveTab(tabNews);

        // Event tab diklik
        tabNews.setOnClickListener(v -> {
            loadFragment(new NewsFragment());
            setActiveTab(tabNews);
        });

        tabVideo.setOnClickListener(v -> {
            loadFragment(new VideoFragment());
            setActiveTab(tabVideo);
        });

        tabArtists.setOnClickListener(v -> {
            loadFragment(new ArtistsFragment());
            setActiveTab(tabArtists);
        });

        tabPodcast.setOnClickListener(v -> {
            loadFragment(new PodcastFragment());
            setActiveTab(tabPodcast);
        });

        return view;
    }

    private void loadFragment(Fragment fragment) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.tab_content_container, fragment)
                .commit();
    }

    private void setActiveTab(TextView activeTab) {
        int hitam = ContextCompat.getColor(requireContext(), R.color.black);
        int abu = ContextCompat.getColor(requireContext(), R.color.abu);

        TextView[] allTabs = {tabNews, tabVideo, tabArtists, tabPodcast};
        for (TextView tab : allTabs) {
            tab.setTextColor(tab == activeTab ? hitam : abu);
        }
    }

    @Override
    public void showWelcomeMessage(String message) {

    }
}
