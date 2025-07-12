package com.example.playmood.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.playmood.R;

public class LoveFragment extends Fragment {

    public LoveFragment() {
        // Konstruktor kosong
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_love, container, false);

        TextView textView = view.findViewById(R.id.text_love);
        textView.setText("Ini adalah halaman Favorit (Love)");

        return view;
    }
}
