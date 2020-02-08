package com.example.simplecopy.ui.fragment.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.example.simplecopy.data.local.prefs.SharedPreferencesManger.REMEMBER;
import static com.example.simplecopy.data.local.prefs.SharedPreferencesManger.REMEMBER_ME;
import static com.example.simplecopy.data.local.prefs.SharedPreferencesManger.SaveData;
import static com.example.simplecopy.data.local.prefs.SharedPreferencesManger.USER_ID;
import static com.example.simplecopy.utils.HelperMethods.isConnected;
import static com.example.simplecopy.utils.HelperMethods.progressDialog;
import static com.example.simplecopy.utils.HelperMethods.validationConfirmPassword;

public class SignUpFragment extends BaseFragment {


    //    @BindView(R.id.sign_up_fragment_txt_phone)
//    TextInputEditText signUpFragmentTxtPhone;
    @BindView(R.id.sign_up_fragment_txt_email)
    TextInputEditText signUpFragmentTxtEmail;
    @BindView(R.id.sign_up_fragment_txt_password)
    TextInputEditText signUpFragmentTxtPassword;
    @BindView(R.id.sign_up_fragment_txt_confirm_password)
    TextInputEditText signUpFragmentTxtConfirmPassword;
    @BindView(R.id.sign_up_fragment_btn_sign_up)
    Button signUpFragmentBtnSignUp;
    private Unbinder unbinder = null;

    //Declare an instance of FirebaseAuth
    private FirebaseAuth mAuth;


    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate (R.layout.fragment_signup, container, false);
        unbinder = ButterKnife.bind (this, view);
        // Inflate the layout for this fragment
        initFragment ( );

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance ( );

        return view;
    }

    //
//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//    }
    @Override
    public void onBack() {
        super.onBack ( );
    }


    @OnClick(R.id.sign_up_fragment_btn_sign_up)
    public void onViewClicked() {
        registerData ();
    }

    private void registerData() {
        //if inputs empty nothing do...
        if (signUpFragmentTxtEmail.getText ( ) == null || signUpFragmentTxtPassword.getText ( ).length ( ) == 0) {
            return;
        }
        //input email, password
        String email = signUpFragmentTxtEmail.getText ( ).toString ( ).trim ( );
        String password = signUpFragmentTxtPassword.getText ( ).toString ( ).trim ( );
        //validate
        if (!Patterns.EMAIL_ADDRESS.matcher (email).matches ( )) {
            //set error and focus to email editText
            signUpFragmentTxtEmail.setError (getResources ( ).getString (R.string.invalid_email));
            signUpFragmentTxtEmail.setFocusable (true);
        } else if (password.length ( ) < 6) {
            signUpFragmentTxtPassword.setError (getResources ( ).getString (R.string.Password_length));
            signUpFragmentTxtPassword.setFocusable (true);
        } else if (!validationConfirmPassword(getActivity(), signUpFragmentTxtPassword, signUpFragmentTxtConfirmPassword)) {
            return;
        }else {
            registerUser (email, password);
        }
    }

    private void registerUser(String email, String password) {
        if (isConnected (getActivity ( ))) {
            if (progressDialog == null) {
                HelperMethods.showProgressDialog (getActivity ( ), getResources ( ).getString (R.string.wait));
            } else {
                if (!progressDialog.isShowing ( )) {
                    HelperMethods.showProgressDialog (getActivity ( ), getResources ( ).getString (R.string.wait));
                }
            }
            mAuth.createUserWithEmailAndPassword (email, password)
                    .addOnCompleteListener (getActivity ( ), new OnCompleteListener<AuthResult> ( ) {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful ( )) {
                                // Sign in success, dismiss dialog and start register activity
                                HelperMethods.dismissProgressDialog();
                                FirebaseUser user = mAuth.getCurrentUser ( );
//                                assert user != null;
//                                SaveData(getActivity (), USER_ID, user.getUid ());
                                Toast.makeText (getActivity (), getResources ().getString (R.string.registered)+ "\n"+ user.getEmail (), Toast.LENGTH_SHORT).show ();
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
