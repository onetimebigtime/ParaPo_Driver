package com.example.parapo_driver;

import android.app.AlertDialog;
import android.content.Context;

public class PopUpAlert {
    Context context;
    public PopUpAlert(String title, String message, Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Ok", (dialog, which) -> dialog.cancel());
        AlertDialog popAlertDialog = alertDialog.create();
        popAlertDialog.show();
    }
}
