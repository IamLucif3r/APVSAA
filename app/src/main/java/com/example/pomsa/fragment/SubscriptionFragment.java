package com.example.pomsa.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.pomsa.R;

public class SubscriptionFragment extends Fragment {

   // TODO: Rename parameter arguments, choose names that match

   public SubscriptionFragment() {
       // Required empty public constructor
   }


   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
       // Inflate the layout for this fragment
       return inflater.inflate(R.layout.fragment_subscription, container, false);
   }
}