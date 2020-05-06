package com.androidbolts.topsheet;


import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatDialog;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

/** Base class for {@link android.app.Dialog}s styled as a bottom sheet. */
public class TopSheetDialog extends AppCompatDialog {

  private TopSheetBehavior<FrameLayout> behavior;

  private FrameLayout container;

  boolean dismissWithAnimation;

  boolean cancelable = true;
  private boolean canceledOnTouchOutside = true;
  private boolean canceledOnTouchOutsideSet;

  public TopSheetDialog(@NonNull Context context) {
    this(context, 0);
  }

  public TopSheetDialog(@NonNull Context context, @StyleRes int theme) {
    super(context, getThemeResId(context, theme));
    // We hide the title bar for any style configuration. Otherwise, there will be a gap
    // above the bottom sheet when it is expanded.
    supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
  }

  protected TopSheetDialog(
      @NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
    super(context, cancelable, cancelListener);
    supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    this.cancelable = cancelable;
  }

  @Override
  public void setContentView(@LayoutRes int layoutResId) {
    super.setContentView(wrapInTopSheet(layoutResId, null, null));
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Window window = getWindow();
    if (window != null) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      }
      window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
  }

  @Override
  public void setContentView(View view) {
    super.setContentView(wrapInTopSheet(0, view, null));
  }

  @Override
  public void setContentView(View view, ViewGroup.LayoutParams params) {
    super.setContentView(wrapInTopSheet(0, view, params));
  }

  @Override
  public void setCancelable(boolean cancelable) {
    super.setCancelable(cancelable);
    if (this.cancelable != cancelable) {
      this.cancelable = cancelable;
      if (behavior != null) {
        behavior.setHideable(cancelable);
      }
    }
  }

  @Override
  protected void onStart() {
    super.onStart();
    if (behavior != null && behavior.getState() == TopSheetBehavior.STATE_HIDDEN) {
      behavior.setState(TopSheetBehavior.STATE_COLLAPSED);
    }
  }

  /**
   * This function can be called from a few different use cases, including Swiping the dialog down
   * or calling `dismiss()` from a `BottomSheetDialogFragment`, tapping outside a dialog, etc...
   *
   * <p>The default animation to dismiss this dialog is a fade-out transition through a
   * windowAnimation. Call {@link #setDismissWithAnimation(true)} if you want to utilize the
   * BottomSheet animation instead.
   *
   * <p>If this function is called from a swipe down interaction, or dismissWithAnimation is false,
   * then keep the default behavior.
   *
   * <p>Else, since this is a terminal event which will finish this dialog, we override the attached
   * {@link TopSheetBehavior.TopSheetCallback} to call this function, after {@link
   * TopSheetBehavior#STATE_HIDDEN} is set. This will enforce the swipe down animation before
   * canceling this dialog.
   */
  @Override
  public void cancel() {
//    TopSheetBehavior<FrameLayout> behavior = getBehavior();
//
//    if (!dismissWithAnimation || behavior.getState() == TopSheetBehavior.STATE_HIDDEN) {
     super.cancel();
    /*} else {
      behavior.setState(TopSheetBehavior.STATE_HIDDEN);
    }*/
  }

  @Override
  public void setCanceledOnTouchOutside(boolean cancel) {
    super.setCanceledOnTouchOutside(cancel);
    if (cancel && !cancelable) {
      cancelable = true;
    }
    canceledOnTouchOutside = cancel;
    canceledOnTouchOutsideSet = true;
  }

  /*@NonNull
  public TopSheetBehavior<FrameLayout> getBehavior() {
    if (behavior == null) {
      // The content hasn't been set, so the behavior doesn't exist yet. Let's create it.
      ensureContainerAndBehavior();
    }
    return behavior;
  }*/

  /**
   * Set to perform the swipe down animation when dismissing instead of the window animation for the
   * dialog.
   *
   * @param dismissWithAnimation True if swipe down animation should be used when dismissing.
   */
  public void setDismissWithAnimation(boolean dismissWithAnimation) {
    this.dismissWithAnimation = dismissWithAnimation;
  }

  /**
   * Returns if dismissing will perform the swipe down animation on the bottom sheet, rather than
   * the window animation for the dialog.
   */
  public boolean getDismissWithAnimation() {
    return dismissWithAnimation;
  }

  /** Creates the container layout which must exist to find the behavior */
  private FrameLayout ensureContainerAndBehavior() {
    if (container == null) {
      container =
              (FrameLayout) View.inflate(getContext(), R.layout.top_sheet_dialog, null);

      FrameLayout bottomSheet = (FrameLayout) container.findViewById(R.id.design_top_sheet);
      behavior = TopSheetBehavior.from(bottomSheet);
//      behavior.addBottomSheetCallback(bottomSheetCallback);
      behavior.setHideable(cancelable);
    }
    return container;
  }

  private View wrapInTopSheet(
      int layoutResId, @Nullable View view, @Nullable ViewGroup.LayoutParams params) {
//    ensureContainerAndBehavior();
    final CoordinatorLayout coordinator = (CoordinatorLayout) View.inflate(getContext(),
            R.layout.top_sheet_dialog, null);
    if (layoutResId != 0 && view == null) {
      view = getLayoutInflater().inflate(layoutResId, coordinator, false);
    }
    FrameLayout topSheet = (FrameLayout) coordinator.findViewById(R.id.design_top_sheet);
    behavior = TopSheetBehavior.from(topSheet);
    behavior.setTopSheetCallback(mTopSheetCallback);
    if (params == null) {
      topSheet.addView(view);
    } else {
      topSheet.addView(view, params);
    }
    // We treat the CoordinatorLayout as outside the dialog though it is technically inside
    if (shouldWindowCloseOnTouchOutside()) {
      coordinator.findViewById(R.id.top_sheet_touch_outside).setOnClickListener(
              new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  if (isShowing()) {
                    cancel();
                  }
                }
              });
    }
    return coordinator;
  }

  boolean shouldWindowCloseOnTouchOutside() {
    if (!canceledOnTouchOutsideSet) {
      TypedArray a =
          getContext()
              .obtainStyledAttributes(new int[] {android.R.attr.windowCloseOnTouchOutside});
      canceledOnTouchOutside = a.getBoolean(0, true);
      a.recycle();
      canceledOnTouchOutsideSet = true;
    }
    return canceledOnTouchOutside;
  }

  private static int getThemeResId(@NonNull Context context, int themeId) {
    if (themeId == 0) {
      // If the provided theme is 0, then retrieve the dialogTheme from our theme
      TypedValue outValue = new TypedValue();
      if (context.getTheme().resolveAttribute(
              R.attr.bottomSheetDialogTheme, outValue, true)) {
        themeId = outValue.resourceId;
      } else {
        // bottomSheetDialogTheme is not provided; we default to our light theme
        themeId = R.style.Theme_Design_TopSheetDialog;
      }
    }
    return themeId;
  }

//  void removeDefaultCallback() {
//    behavior.removeBottomSheetCallback(bottomSheetCallback);
//  }

  private TopSheetBehavior.TopSheetCallback mTopSheetCallback
          = new TopSheetBehavior.TopSheetCallback() {
    @Override
    public void onStateChanged(@NonNull View topSheet,
                               @TopSheetBehavior.State int newState) {
      if (newState == TopSheetBehavior.STATE_HIDDEN) {
        dismiss();
      }
    }

    @Override
    public void onSlide(@NonNull View topSheet, float slideOffset, @Nullable Boolean isOpening) {
    }
  };
}