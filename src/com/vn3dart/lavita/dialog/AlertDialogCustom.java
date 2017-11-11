package com.vn3dart.lavita.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.vn3dart.lavita.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Annv on 11/10/17.
 */

public class AlertDialogCustom extends Dialog {

    public boolean isDismiss = false;

    public AlertDialogCustom(Context context) {
        super(context, R.style.Theme_Dialog_Guide);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(R.layout.custom_progress_dialog);
        setCancelable(false);
        ButterKnife.bind(this);
    }


    @OnClick({R.id.btn_cancel, R.id.btn_ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                isDismiss = false;
                break;
            case R.id.btn_ok:
                isDismiss = true;
                break;
        }
        dismiss();
    }
}
