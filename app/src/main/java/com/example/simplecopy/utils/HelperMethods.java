package com.example.simplecopy.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.simplecopy.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

public class HelperMethods {
    public static ProgressDialog progressDialog;
    public static AlertDialog alertDialog;
    static ConnectivityManager cm;

    public static void replaceFragment(FragmentManager getChildFragmentManager, int id, Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager.beginTransaction ( );
        transaction.replace (id, fragment);
        transaction.addToBackStack (null);
        transaction.commit ( );
    }

    public static void replaceFragment(FragmentManager getChildFragmentManager, int id, Fragment fragment, Bundle bundle) {
        FragmentTransaction transaction = getChildFragmentManager.beginTransaction ( );
        fragment.setArguments (bundle);
        transaction.replace (id, fragment);
        transaction.addToBackStack (null);
        transaction.commit ( );
    }

//    public static void showCalender(Context context, String title, final TextView text_view_data, final DateTxt data1) {
//        DatePickerDialog mDatePicker = new DatePickerDialog (context, new DatePickerDialog.OnDateSetListener ( ) {
//            public void onDateSet(DatePicker datepicker, int selectedYear, int selectedMonth, int selectedDay) {
//                DecimalFormatSymbols symbols = new DecimalFormatSymbols (Locale.US);
//                DecimalFormat mFormat = new DecimalFormat ("00", symbols);
//                String data = selectedYear + "-" + mFormat.format (Double.valueOf ((selectedMonth + 1))) + "-" + mFormat.format (Double.valueOf (selectedDay));
//                data1.setDate_txt (data);
//                data1.setDay (mFormat.format (Double.valueOf (selectedDay)));
//                data1.setMonth (mFormat.format (Double.valueOf (selectedMonth + 1)));
//                data1.setYear (String.valueOf (selectedYear));
//                text_view_data.setText (data);
//            }
//        }, Integer.parseInt (data1.getYear ( )), Integer.parseInt (data1.getMonth ( )) - 1, Integer.parseInt (data1.getDay ( )));
//        mDatePicker.setTitle (title);
//        mDatePicker.show ( );
//    }
//
//    public static void onLoadImageFromUrl(ImageView imageView, String URl, Context context) {
//        Glide.with (context)
//                .load (URl)
//                .into (imageView);
//    }

    /**
     * Making notification bar no transparent
     */
    public static void setSystemBarColor(Activity act) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = act.getWindow ( );
            window.addFlags (WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor (act.getResources ( ).getColor (R.color.colorPrimaryDark));
        }
    }

    public static void setSystemBarColor(Activity act, @ColorRes int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = act.getWindow ( );
            window.addFlags (WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor (act.getResources ( ).getColor (color));
        }
    }

    /**
     * Making notification bar transparent
     */
    public static void setSystemBarTransparent(Activity act) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = act.getWindow ( );
            window.addFlags (WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor (Color.TRANSPARENT);
        }
    }

    public static void nestedScrollTo(final NestedScrollView nested, final View targetView) {
        nested.post (new Runnable ( ) {
            @Override
            public void run() {
                nested.scrollTo (500, targetView.getBottom ( ));
            }
        });
    }

    public static void showProgressDialog(Activity activity, String title) {
        try {

            progressDialog = new ProgressDialog (activity);
            progressDialog.setMessage (title);
            progressDialog.setIndeterminate (false);
            progressDialog.setCancelable (false);

            progressDialog.show ( );

        } catch (Exception e) {

        }
    }

    public static void dismissProgressDialog() {
        try {

            progressDialog.dismiss ( );

        } catch (Exception e) {

        }
    }

    public static Snackbar getSnackBarWithAction(View v, String text, String actionText, int actionTextColor) {
        return Snackbar.make (v, text, Snackbar.LENGTH_LONG)
                .setAction (actionText, new View.OnClickListener ( ) {
                    @Override
                    public void onClick(View v) {
                        //ACTION

                    }
                }).setActionTextColor (actionTextColor);
    }

    public static Snackbar getSnackBar(View v, String text) {
        return Snackbar.make (v, text, Snackbar.LENGTH_LONG);
    }

    public static void showSnackBar(View v, String text) {
        Snackbar snackbar = Snackbar.make (v, text, Snackbar.LENGTH_LONG);
        snackbar.show ( );
    }

    public static void showSnackBarWithBottomNavigation(View v, String text, BottomNavigationView bottomNavigation) {
        Snackbar snackbar = Snackbar.make (v, text, Snackbar.LENGTH_LONG);
        snackbar.setAnchorView (bottomNavigation);
        snackbar.show ( );
    }

    public static Snackbar getSnackBarWithBottomNavigation(View v, String text, BottomNavigationView bottomNavigation) {
        Snackbar snackbar = Snackbar.make (v, text, Snackbar.LENGTH_LONG);
        snackbar.setAnchorView (bottomNavigation);
        return snackbar;
    }

    public static void showSnackBarMargin(Snackbar snackbar, int side, int bottom) {
        final View snackBarView = snackbar.getView ( );
        final ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) snackBarView.getLayoutParams ( );
        params.setMargins (params.leftMargin + side,
                params.topMargin,
                params.rightMargin + side,
                params.bottomMargin + bottom);
        snackBarView.setLayoutParams (params);
        snackbar.show ( );
    }


    public static void disappearKeypad(Activity activity, View v) {
        try {
            if (v != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService (Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow (v.getWindowToken ( ), 0);
            }
        } catch (Exception e) {

        }
    }

    public static void changeLang(Context context, String lang) {
        Resources res = context.getResources ( );
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics ( );
        android.content.res.Configuration conf = res.getConfiguration ( );
        conf.setLocale (new Locale (lang)); // API 17+ only.
        // Use conf.locale = new Locale(...) if targeting lower versions
        res.updateConfiguration (conf, dm);
    }

    public static void htmlReader(TextView textView, String s) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText (Html.fromHtml (s, Html.FROM_HTML_MODE_COMPACT));
        } else {
            textView.setText (Html.fromHtml (s));
        }
    }
//
//    public static void customToast(Activity activity, String ToastTitle, boolean failed) {
//
//        LayoutInflater inflater = activity.getLayoutInflater();
//
//        int layout_id;
//
//        if (failed) {
//            layout_id = R.layout.toast;
//        } else {
//            layout_id = R.layout.success_toast;
//        }
//
//        View layout = inflater.inflate(layout_id,
//                (ViewGroup) activity.findViewById(R.id.toast_layout_root));
//
//        TextView text = layout.findViewById(R.id.text);
//        text.setText(ToastTitle);
//
//        Toast toast = new Toast(activity);
//        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 100);
//        toast.setDuration(Toast.LENGTH_LONG);
//        toast.setView(layout);
//        toast.show();
//    }

    public static void onPermission(Activity activity) {
        String[] perms = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CALL_PHONE};

        ActivityCompat.requestPermissions (activity,
                perms,
                100);

    }

    public static void expand(final View v) {
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec (((View) v.getParent ( )).getWidth ( ), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec (0, View.MeasureSpec.UNSPECIFIED);
        v.measure (matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = v.getMeasuredHeight ( );

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams ( ).height = 1;
        v.setVisibility (View.VISIBLE);
        Animation a = new Animation ( ) {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams ( ).height = interpolatedTime == 1
                        ? LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout ( );
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Expansion speed of 1dp/ms
        a.setDuration ((int) (targetHeight / v.getContext ( ).getResources ( ).getDisplayMetrics ( ).density));
        v.startAnimation (a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight ( );

        Animation a = new Animation ( ) {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility (View.GONE);
                } else {
                    v.getLayoutParams ( ).height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout ( );
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Collapse speed of 1dp/ms
        a.setDuration ((int) (initialHeight / v.getContext ( ).getResources ( ).getDisplayMetrics ( ).density));
        v.startAnimation (a);
    }


    static public boolean isConnected(Context context) {
        try {
            cm = (ConnectivityManager) context.getSystemService (Context.CONNECTIVITY_SERVICE);
        } catch (NullPointerException e) {

        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo ( );
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting ( );
        return isConnected;
    }

    public static void onCreateErrorToast(Activity activity, String toastTitle) {
        LayoutInflater inflater = activity.getLayoutInflater ( );

        int layout_id = R.layout.toast_error;

        View layout = inflater.inflate (layout_id,
                (ViewGroup) activity.findViewById (R.id.toast_layout_root));

        TextView text = layout.findViewById (R.id.toast_tv_text);
        text.setText (toastTitle);

        Toast toast = new Toast (activity);
        toast.setGravity (Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 100);
        toast.setDuration (Toast.LENGTH_LONG);
        toast.setView (layout);
        toast.show ( );
    }

    public static boolean validationConfirmPassword(Activity activity, TextInputEditText password, TextInputEditText confirmPassword) {

        if (password.getText ( ).toString ( ).equals (confirmPassword.getText ( ).toString ( ))) {
            return true;
        } else {
            confirmPassword.setError (activity.getString (R.string.invalid_confirm_password));
            confirmPassword.setFocusable (true);
            return false;
        }
    }

    public static void vibration(Context context) {
        Vibrator v = (Vibrator) context.getSystemService (Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            assert v != null;
            v.vibrate (VibrationEffect.createOneShot (100, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            assert v != null;
            v.vibrate (100);
        }
    }

    public static void vibrate(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.performHapticFeedback (HapticFeedbackConstants.KEYBOARD_TAP,
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);  // Ignore device's setting. Otherwise, you can use FLAG_IGNORE_VIEW_SETTING to ignore view's setting.
        }
    }

}
