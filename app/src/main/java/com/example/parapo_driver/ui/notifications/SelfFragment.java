package com.example.parapo_driver.ui.notifications;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.parapo_driver.R;
import com.example.parapo_driver.SignInActivity;
import com.example.parapo_driver.databinding.FragmentSelfBinding;
import com.example.parapo_driver.ui.signup.UserData;
import com.google.firebase.auth.FirebaseAuth;

public class SelfFragment extends Fragment {

    private FragmentSelfBinding binding;
    private String message;
    private boolean isFinished, isThere;

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
        Button logoutButton = root.findViewById(R.id.logout_button);
        ProgressBar progressBar = root.findViewById(R.id.self_progressbar);
        message = selfViewModel.getMessage();
        isThere = selfViewModel.isThere();
        isFinished = selfViewModel.isFinished();
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOutUser();
            }
        });


        if (isThere){
            selfViewModel.getFullNameTitle().observe(getViewLifecycleOwner(), fullNameHead::setText);
            selfViewModel.getFullName().observe(getViewLifecycleOwner(), fullNameLabel::setText);
            selfViewModel.getEmailAddress().observe(getViewLifecycleOwner(), emailAddressLabel::setText);
            selfViewModel.getPlateNumber().observe(getViewLifecycleOwner(), plateNumberLabel::setText);
        } else if (!isFinished) {
            progressBar.setVisibility(View.VISIBLE);
        } else if (isFinished) {
            progressBar.setVisibility(View.GONE);
        } else {
            Toast.makeText(this.requireActivity(), message, Toast.LENGTH_SHORT).show();
        }
        /*final TextView textView = binding.textNotifications;
        selfViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);*/
        return root;
    }

    private void signOutUser() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        getActivity().finish();
        startActivity(new Intent(this.requireActivity(), SignInActivity.class));
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}