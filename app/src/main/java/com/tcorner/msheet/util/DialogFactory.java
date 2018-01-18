package com.tcorner.msheet.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;

import com.tcorner.msheet.R;


public final class DialogFactory {

    public static Dialog createSimpleOkErrorDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_ok, null);
        return alertDialog.create();
    }

    public static Dialog createSimpleOkErrorDialog(Context context,
                                                   @StringRes int titleResource,
                                                   @StringRes int messageResource) {

        return createSimpleOkErrorDialog(context,
                context.getString(titleResource),
                context.getString(messageResource));
    }

    public static Dialog createGenericErrorDialog(Context context, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.dialog_error_title))
                .setMessage(message)
                .setPositiveButton(R.string.dialog_ok, null);
        return alertDialog.create();
    }

    public static Dialog createGenericErrorDialog(Context context, @StringRes int messageResource) {
        return createGenericErrorDialog(context, context.getString(messageResource));
    }

    public static ProgressDialog createProgressDialog(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);

        return progressDialog;
    }

    public static ProgressDialog createProgressDialog(Context context,
                                                      @StringRes int messageResource) {
        return createProgressDialog(context, context.getString(messageResource));
    }

    public static AlertDialog createConfirmDialog(Context context, String title, String msg,
                                                  DialogInterface.OnClickListener yesListener) {
        return new AlertDialog.Builder(context).setTitle(title)
                .setMessage(msg)
                .setPositiveButton("Yes", yesListener)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).create();
    }

    public static Dialog createMessageDialog(Context context,
                                             @StringRes int titleResource,
                                             @StringRes int messageResource) {

        return createMessageDialog(context,
                context.getString(titleResource),
                context.getString(messageResource));
    }

    public static AlertDialog createMessageDialog(Context context, String title, String msg) {
        return new AlertDialog.Builder(context).setTitle(title)
                .setMessage(msg)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();
    }

    public static AlertDialog createSingleOptionDialog(Context context, String title, String[] items,
                                                       DialogInterface.OnClickListener onClickListener) {
        return new AlertDialog.Builder(context).setTitle(title)
                .setItems(items, onClickListener).create();
    }

    public static AlertDialog createInputDialog(final Context context, String title, String message, String defaultText, String hint, int inputType, final OnInputDialogListener onInputDialogListener) {
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_input, null);
        final AppCompatTextView tvMessage = v.findViewById(R.id.tv_message);
        final TextInputLayout textInputInput = v.findViewById(R.id.textinput_input);

        tvMessage.setText(message);
        textInputInput.getEditText().setText(defaultText);
        textInputInput.getEditText().setHint(hint);
        textInputInput.getEditText().setInputType(inputType);

        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(v)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onInputDialogListener.onSubmit(textInputInput.getEditText().getText().toString());
                    }
                })
                .create();
    }

    public interface OnInputDialogListener {
        void onSubmit(String message);
    }
}
