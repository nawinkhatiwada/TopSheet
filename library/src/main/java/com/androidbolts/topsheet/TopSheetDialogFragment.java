package com.androidbolts.topsheet;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;


/**
 * Created by andrea on 23/08/16.
 */
public class TopSheetDialogFragment extends AppCompatDialogFragment {

    private boolean waitingForDismissAllowingStateLoss;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new TopSheetDialog(getContext(), getTheme());
    }

    @Override
    public void dismiss() {
//        if (!tryDismissWithAnimation(false)) {
            super.dismiss();
//        }
    }

    @Override
    public void dismissAllowingStateLoss() {
//        if (!tryDismissWithAnimation(true)) {
            super.dismissAllowingStateLoss();
//        }
    }

    /**
     * Tries to dismiss the dialog fragment with the bottom sheet animation. Returns true if possible,
     * false otherwise.
     */
//    private boolean tryDismissWithAnimation(boolean allowingStateLoss) {
//        Dialog baseDialog = getDialog();
//        if (baseDialog instanceof TopSheetDialog) {
//            TopSheetDialog dialog = (TopSheetDialog) baseDialog;
//            TopSheetBehavior<?> behavior = dialog.getBehavior();
//            if (behavior.isHideable() && dialog.getDismissWithAnimation()) {
//                dismissWithAnimation(behavior, allowingStateLoss);
//                return true;
//            }
//        }
//
//        return false;
//    }

    private void dismissWithAnimation(
            @NonNull TopSheetBehavior<?> behavior, boolean allowingStateLoss) {
        waitingForDismissAllowingStateLoss = allowingStateLoss;

        if (behavior.getState() == TopSheetBehavior.STATE_HIDDEN) {
            dismissAfterAnimation();
        } else {
            /*if (getDialog() instanceof TopSheetDialog) {
                ((BottomSheetDialog) getDialog()).removeDefaultCallback();
            }*/
            behavior.setTopSheetCallback(new TopSheetDialogFragment.TopSheetDismissCallback());
            behavior.setState(TopSheetBehavior.STATE_HIDDEN);
        }
    }

    private void dismissAfterAnimation() {
        if (waitingForDismissAllowingStateLoss) {
            super.dismissAllowingStateLoss();
        } else {
            super.dismiss();
        }
    }

    private class TopSheetDismissCallback extends TopSheetBehavior.TopSheetCallback {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == TopSheetBehavior.STATE_HIDDEN) {
                dismissAfterAnimation();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset, @Nullable Boolean isOpening) {

        }
    }

       /* @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == TopSheetBehavior.STATE_HIDDEN) {
                dismissAfterAnimation();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
    }*/
}
