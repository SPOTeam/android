// Generated by view binder compiler. Do not edit!
package com.example.spoteam_android.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.spoteam_android.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityInterestFilterBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final TextView activityfee;

  @NonNull
  public final RangeSlider ageRangeSlider;

  @NonNull
  public final TextView ageRangeText;

  @NonNull
  public final TextView behindEt;

  @NonNull
  public final BottomNavigationView bottomNavigation;

  @NonNull
  public final Chip chip1;

  @NonNull
  public final Chip chip2;

  @NonNull
  public final ChipGroup chipGroup1;

  @NonNull
  public final ChipGroup chipGroup2;

  @NonNull
  public final EditText edittext1;

  @NonNull
  public final FrameLayout fragmentContainer;

  @NonNull
  public final Spinner genderSpinner;

  @NonNull
  public final TextView maxValueText;

  @NonNull
  public final TextView minValueText;

  @NonNull
  public final Chip studyChip1;

  @NonNull
  public final Chip studyChip10;

  @NonNull
  public final Chip studyChip2;

  @NonNull
  public final Chip studyChip3;

  @NonNull
  public final Chip studyChip4;

  @NonNull
  public final Chip studyChip5;

  @NonNull
  public final Chip studyChip6;

  @NonNull
  public final Chip studyChip7;

  @NonNull
  public final Chip studyChip8;

  @NonNull
  public final Chip studyChip9;

  @NonNull
  public final TextView studyThemeLabel;

  @NonNull
  public final TextView wave;

  private ActivityInterestFilterBinding(@NonNull ConstraintLayout rootView,
      @NonNull TextView activityfee, @NonNull RangeSlider ageRangeSlider,
      @NonNull TextView ageRangeText, @NonNull TextView behindEt,
      @NonNull BottomNavigationView bottomNavigation, @NonNull Chip chip1, @NonNull Chip chip2,
      @NonNull ChipGroup chipGroup1, @NonNull ChipGroup chipGroup2, @NonNull EditText edittext1,
      @NonNull FrameLayout fragmentContainer, @NonNull Spinner genderSpinner,
      @NonNull TextView maxValueText, @NonNull TextView minValueText, @NonNull Chip studyChip1,
      @NonNull Chip studyChip10, @NonNull Chip studyChip2, @NonNull Chip studyChip3,
      @NonNull Chip studyChip4, @NonNull Chip studyChip5, @NonNull Chip studyChip6,
      @NonNull Chip studyChip7, @NonNull Chip studyChip8, @NonNull Chip studyChip9,
      @NonNull TextView studyThemeLabel, @NonNull TextView wave) {
    this.rootView = rootView;
    this.activityfee = activityfee;
    this.ageRangeSlider = ageRangeSlider;
    this.ageRangeText = ageRangeText;
    this.behindEt = behindEt;
    this.bottomNavigation = bottomNavigation;
    this.chip1 = chip1;
    this.chip2 = chip2;
    this.chipGroup1 = chipGroup1;
    this.chipGroup2 = chipGroup2;
    this.edittext1 = edittext1;
    this.fragmentContainer = fragmentContainer;
    this.genderSpinner = genderSpinner;
    this.maxValueText = maxValueText;
    this.minValueText = minValueText;
    this.studyChip1 = studyChip1;
    this.studyChip10 = studyChip10;
    this.studyChip2 = studyChip2;
    this.studyChip3 = studyChip3;
    this.studyChip4 = studyChip4;
    this.studyChip5 = studyChip5;
    this.studyChip6 = studyChip6;
    this.studyChip7 = studyChip7;
    this.studyChip8 = studyChip8;
    this.studyChip9 = studyChip9;
    this.studyThemeLabel = studyThemeLabel;
    this.wave = wave;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityInterestFilterBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityInterestFilterBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_interest_filter, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityInterestFilterBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.activityfee;
      TextView activityfee = ViewBindings.findChildViewById(rootView, id);
      if (activityfee == null) {
        break missingId;
      }

      id = R.id.ageRangeSlider;
      RangeSlider ageRangeSlider = ViewBindings.findChildViewById(rootView, id);
      if (ageRangeSlider == null) {
        break missingId;
      }

      id = R.id.ageRangeText;
      TextView ageRangeText = ViewBindings.findChildViewById(rootView, id);
      if (ageRangeText == null) {
        break missingId;
      }

      id = R.id.behind_et;
      TextView behindEt = ViewBindings.findChildViewById(rootView, id);
      if (behindEt == null) {
        break missingId;
      }

      id = R.id.bottom_navigation;
      BottomNavigationView bottomNavigation = ViewBindings.findChildViewById(rootView, id);
      if (bottomNavigation == null) {
        break missingId;
      }

      id = R.id.chip1;
      Chip chip1 = ViewBindings.findChildViewById(rootView, id);
      if (chip1 == null) {
        break missingId;
      }

      id = R.id.chip2;
      Chip chip2 = ViewBindings.findChildViewById(rootView, id);
      if (chip2 == null) {
        break missingId;
      }

      id = R.id.chipGroup1;
      ChipGroup chipGroup1 = ViewBindings.findChildViewById(rootView, id);
      if (chipGroup1 == null) {
        break missingId;
      }

      id = R.id.chipGroup2;
      ChipGroup chipGroup2 = ViewBindings.findChildViewById(rootView, id);
      if (chipGroup2 == null) {
        break missingId;
      }

      id = R.id.edittext1;
      EditText edittext1 = ViewBindings.findChildViewById(rootView, id);
      if (edittext1 == null) {
        break missingId;
      }

      id = R.id.fragment_container;
      FrameLayout fragmentContainer = ViewBindings.findChildViewById(rootView, id);
      if (fragmentContainer == null) {
        break missingId;
      }

      id = R.id.gender_spinner;
      Spinner genderSpinner = ViewBindings.findChildViewById(rootView, id);
      if (genderSpinner == null) {
        break missingId;
      }

      id = R.id.maxValueText;
      TextView maxValueText = ViewBindings.findChildViewById(rootView, id);
      if (maxValueText == null) {
        break missingId;
      }

      id = R.id.minValueText;
      TextView minValueText = ViewBindings.findChildViewById(rootView, id);
      if (minValueText == null) {
        break missingId;
      }

      id = R.id.study_chip1;
      Chip studyChip1 = ViewBindings.findChildViewById(rootView, id);
      if (studyChip1 == null) {
        break missingId;
      }

      id = R.id.study_chip10;
      Chip studyChip10 = ViewBindings.findChildViewById(rootView, id);
      if (studyChip10 == null) {
        break missingId;
      }

      id = R.id.study_chip2;
      Chip studyChip2 = ViewBindings.findChildViewById(rootView, id);
      if (studyChip2 == null) {
        break missingId;
      }

      id = R.id.study_chip3;
      Chip studyChip3 = ViewBindings.findChildViewById(rootView, id);
      if (studyChip3 == null) {
        break missingId;
      }

      id = R.id.study_chip4;
      Chip studyChip4 = ViewBindings.findChildViewById(rootView, id);
      if (studyChip4 == null) {
        break missingId;
      }

      id = R.id.study_chip5;
      Chip studyChip5 = ViewBindings.findChildViewById(rootView, id);
      if (studyChip5 == null) {
        break missingId;
      }

      id = R.id.study_chip6;
      Chip studyChip6 = ViewBindings.findChildViewById(rootView, id);
      if (studyChip6 == null) {
        break missingId;
      }

      id = R.id.study_chip7;
      Chip studyChip7 = ViewBindings.findChildViewById(rootView, id);
      if (studyChip7 == null) {
        break missingId;
      }

      id = R.id.study_chip8;
      Chip studyChip8 = ViewBindings.findChildViewById(rootView, id);
      if (studyChip8 == null) {
        break missingId;
      }

      id = R.id.study_chip9;
      Chip studyChip9 = ViewBindings.findChildViewById(rootView, id);
      if (studyChip9 == null) {
        break missingId;
      }

      id = R.id.studyThemeLabel;
      TextView studyThemeLabel = ViewBindings.findChildViewById(rootView, id);
      if (studyThemeLabel == null) {
        break missingId;
      }

      id = R.id.wave;
      TextView wave = ViewBindings.findChildViewById(rootView, id);
      if (wave == null) {
        break missingId;
      }

      return new ActivityInterestFilterBinding((ConstraintLayout) rootView, activityfee,
          ageRangeSlider, ageRangeText, behindEt, bottomNavigation, chip1, chip2, chipGroup1,
          chipGroup2, edittext1, fragmentContainer, genderSpinner, maxValueText, minValueText,
          studyChip1, studyChip10, studyChip2, studyChip3, studyChip4, studyChip5, studyChip6,
          studyChip7, studyChip8, studyChip9, studyThemeLabel, wave);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
