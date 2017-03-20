package org.taitasciore.android.fmtest;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

/**
 * Created by roberto on 19/03/17.
 */

/**
 * A fragment is used to display the progress dialog because it will survive
 * through orientation changes
 */
public class ProgressDialogFragment extends DialogFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog progressDialog = ProgressDialog.show(
                getActivity(), "", "Getting subway data. Please wait...", true, false);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }
}
