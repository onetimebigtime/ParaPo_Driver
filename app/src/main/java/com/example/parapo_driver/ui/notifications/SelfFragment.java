package com.example.parapo_driver.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.parapo_driver.databinding.FragmentSelfBinding;
import com.example.parapo_driver.ui.signup.UserData;

public class SelfFragment extends Fragment {

    private FragmentSelfBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SelfViewModel selfViewModel =
                new ViewModelProvider(this).get(SelfViewModel.class);

        binding = FragmentSelfBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        TextView fullNameHead = binding.selfFullnameHead;
        TextView fullNameLabel = binding.selfFullnameLabel;
        TextView emailAddressLabel = binding.selfEmailLabel;
        TextView plateNumberLabel = binding.selfPlateNumberLabel;

        selfViewModel.getFullNameTitle().observe(getViewLifecycleOwner(), fullNameHead::setText);
        selfViewModel.getFullName().observe(getViewLifecycleOwner(), fullNameLabel::setText);
        selfViewModel.getEmailAddress().observe(getViewLifecycleOwner(), emailAddressLabel::setText);
        selfViewModel.getPlateNumber().observe(getViewLifecycleOwner(), plateNumberLabel::setText);

        /*final TextView textView = binding.textNotifications;
        selfViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);*/
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}