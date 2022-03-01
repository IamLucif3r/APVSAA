package com.example.pomsa.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.pomsa.R;

public class ExploreFragment extends Fragment {

   // TODO: Rename parameter arguments, choose names that match

   public ExploreFragment() {
       // Required empty public constructor
   }


   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
       // Inflate the layout for this fragment
       return inflater.inflate(R.layout.fragment_explore, container, false);
   }
}