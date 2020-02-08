package com.example.simplecopy.ui.fragment.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.simplecopy.R;
import com.example.simplecopy.ui.activity.MainActivity;
import com.example.simplecopy.ui.fragment.BaseFragment;
import com.example.simplecopy.utils.HelperMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.example.simplecopy.data.local.prefs.SharedPreferencesManger.SaveData;
import static com.example.simplecopy.data.local.prefs.SharedPreferencesManger.USER_ID;
import static com.example.simplecopy.utils.HelperMethods.isConnected;
import static com.example.simplecopy.utils.HelperMethods.progressDialog;
import static com.example.simplecopy.utils.HelperMethods.replaceFragment;
import static com.example.simplecopy.utils.HelperMethods.validationConfirmPassword;

public class LoginFragment extends BaseFragment {

    @BindView(R.id.login_fragment_txt_skip)
    TextView loginFragmentTxtSkip;
    @BindView(R.id.login_fragment_txt_email_or_phone)
    TextInputEditText loginFragmentTxtEmailOrPhone;
    @BindView(R.id.login_fragment_txt_password)
    TextInputEditText loginFragmentTxtPassword;
    @BindView(R.id.login_fragment_txt_forgot_password)
    TextView loginFragmentTxtForgotPassword;
    @BindView(R.id.login_fragment_btn_login)
    Button loginFragmentBtnLogin;
    @BindView(R.id.login_fragment_txt_sign_up)
    TextView loginFragmentTxtSignUp;
    private Unbinder unbinder = null;

    //Declare an instance of FirebaseAuth
    private FirebaseAuth mAuth;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate (R.layout.fragment_login, container, false);
        unbinder = ButterKnife.bind(this, view);
        // Inflate the layout for this fragment
        initFragment ( );
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        return view;
    }

    @Override
    public void onBack() {
        Objects.requireNonNull (getActivity ( )).finish ();

    }

    @OnClick({R.id.login_fragment_txt_skip, R.id.login_fragment_txt_forgot_password, R.id.login_fragment_btn_login, R.id.login_fragment_txt_sign_up})
    public void onViewClicked(View view) {
        switch (view.getId ( )) {
            case R.id.login_fragment_txt_skip:
                startActivity (new Intent (getActivity (), MainActivity.class));
                getActivity ().finish ();
                break;
            case R.id.login_fragment_txt_forgot_password:
                break;
            case R.id.login_fragment_btn_login:
                loginData ();
                break;
            case R.id.login_fragment_txt_sign_up:
                replaceFragment (getActivity ().getSupportFragmentManager(), R.id.user_container, new SignUpFragment ());
                break;
        }
    }

    private void loginData() {
        //if inputs empty nothing do...
        if (loginFragmentTxtEmailOrPhone.getText ( ) == null || loginFragmentTxtPassword.getText ( ).length ( ) == 0) {
            return;
        }
        //input email, password
        String email = loginFragmentTxtEmailOrPhone.getText ( ).toString ( ).trim ( );
        String password = loginFragmentTxtPassword.getText ( ).toString ( ).trim ( );
        //validate
        if (!Patterns.EMAIL_ADDRESS.matcher (email).matches ( )) {
            //set error and focus to email editText
            loginFragmentTxtEmailOrPhone.setError (getResources ( ).getString (R.string.invalid_email));
            loginFragmentTxtEmailOrPhone.setFocusable (true);
        } else if (password.length ( ) < 6) {
            loginFragmentTxtPassword.setError (getResources ( ).getString (R.string.Password_length));
            loginFragmentTxtPassword.setFocusable (true);
        } else {
            loginUser (email, password);
        }
    }

    private void loginUser(String email, String password) {
        if (isConnected (getActivity ( ))) {
            if (progressDialog == null) {
                HelperMethods.showProgressDialog (getActivity ( ), getResources ( ).getString (R.string.wait));
            } else {
                if (!progressDialog.isShowing ( )) {
                    HelperMethods.showProgressDialog (getActivity ( ), getResources ( ).getString (R.string.wait));
                }
            }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener (getActivity ( ), new OnCompleteListener<AuthResult> ( ) {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            HelperMethods.dismissProgressDialog();
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
//                            assert user != null;
//                            SaveData(getActivity (), USER_ID, user.getUid ());
                            startActivity (new Intent (getActivity (), MainActivity.class));
                            getActivity ().finish ();
                        } else {
                            HelperMethods.dismissProgressDialog();
                            // If sign in fails, display a message to the user.
                            Toast.makeText (getActivity ( ), getString (R.string.authentication_failed), Toast.LENGTH_SHORT).show ( );
                        }
                    }
                }).addOnFailureListener (new OnFailureListener ( ) {
            @Override
            public void onFailure(@NonNull Exception e) {
                HelperMethods.dismissProgressDialog();
                Toast.makeText (getActivity ( ), e.getMessage (), Toast.LENGTH_SHORT).show ( );
            }
        });
        } else {
            try {
                HelperMethods.onCreateErrorToast (getActivity ( ), getResources ( ).getString (R.string.error_inter_net));
            } catch (Exception e) {

            }

        }
    }
}