package com.example.simplecopy.ui.fragment.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.simplecopy.R;
import com.example.simplecopy.ui.activity.MainActivity;
import com.example.simplecopy.ui.fragment.BaseFragment;
import com.example.simplecopy.utils.HelperMethods;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.example.simplecopy.utils.HelperMethods.isConnected;
import static com.example.simplecopy.utils.HelperMethods.progressDialog;
import static com.example.simplecopy.utils.HelperMethods.replaceFragment;

public class LoginFragment extends BaseFragment {
    public static final String TAG = "LoginFragment";

    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient mGoogleSignInClient;
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
    @BindView(R.id.login_fragment_btn_google_login)
    SignInButton loginFragmentBtnGoogleLogin;

    //    @BindView(R.id.login_fragment_txt_skip)
//    TextView loginFragmentTxtSkip;
//    @BindView(R.id.login_fragment_txt_email_or_phone)
//    TextInputEditText loginFragmentTxtEmailOrPhone;
//    @BindView(R.id.login_fragment_txt_password)
//    TextInputEditText loginFragmentTxtPassword;
//    @BindView(R.id.login_fragment_txt_forgot_password)
//    TextView loginFragmentTxtForgotPassword;
//    @BindView(R.id.login_fragment_btn_login)
//    Button loginFragmentBtnLogin;
//    @BindView(R.id.login_fragment_txt_sign_up)
//    TextView loginFragmentTxtSignUp;
//    @BindView(R.id.login_fragment_btn_google_login)
//    SignInButton loginFragmentBtnGoogleLogin;
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
        unbinder = ButterKnife.bind (this, view);
        // Inflate the layout for this fragment
        initFragment ( );
        //before mAuth
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder (GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken (getString (R.string.default_web_client_id))
                .requestEmail ( )
                .build ( );
        mGoogleSignInClient = GoogleSignIn.getClient (Objects.requireNonNull (getActivity ( )), gso);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance ( );
        return view;
    }

    @Override
    public void onBack() {
        Objects.requireNonNull (getActivity ( )).finish ( );

    }

    @OnClick({R.id.login_fragment_txt_skip, R.id.login_fragment_txt_forgot_password, R.id.login_fragment_btn_login, R.id.login_fragment_txt_sign_up, R.id.login_fragment_btn_google_login})
    public void onViewClicked(View view) {
        switch (view.getId ( )) {
            case R.id.login_fragment_txt_skip:
                startActivity (new Intent (getActivity ( ), MainActivity.class));
                getActivity ( ).finish ( );
                break;
            case R.id.login_fragment_txt_forgot_password:
                showRecoverPasswordDialog ( );
                break;
            case R.id.login_fragment_btn_login:
                loginData ( );
                break;
            case R.id.login_fragment_txt_sign_up:
                replaceFragment (getActivity ( ).getSupportFragmentManager ( ), R.id.user_container, new SignUpFragment ( ));
                break;
            case R.id.login_fragment_btn_google_login:
                Intent signInIntent = mGoogleSignInClient.getSignInIntent ( );
                startActivityForResult (signInIntent, RC_SIGN_IN);
                break;
        }
    }

    private void showRecoverPasswordDialog() {
        //Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder (getActivity ( ));
        builder.setTitle (getResources ( ).getString (R.string.recover_password));

        //set layout linear layout
        LinearLayout linearLayout = new LinearLayout (getActivity ( ));
        //views to set in dialog
        EditText emailEt = new EditText (getActivity ( ));
        emailEt.setHint (getResources ( ).getString (R.string.email));
        emailEt.setInputType (InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailEt.setMinEms (16);

        linearLayout.addView (emailEt);
        linearLayout.setPadding (10, 10, 10, 10);

        builder.setView (linearLayout);

        //Buttons recover
        builder.setPositiveButton (getResources ( ).getString (R.string.reset), new DialogInterface.OnClickListener ( ) {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input email
                String email = emailEt.getText ( ).toString ( ).trim ( );
                beginReset (email);
            }
        });

        //Buttons cancel
        builder.setNegativeButton (getResources ( ).getString (R.string.cancel), new DialogInterface.OnClickListener ( ) {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss ( );
            }
        });
        //show dialog
        builder.create ( ).show ( );
    }

    private void beginReset(String email) {


        if (isConnected (getActivity ( ))) {
            if (progressDialog == null) {
                HelperMethods.showProgressDialog (getActivity ( ), getResources ( ).getString (R.string.sending));
            } else {
                if (!progressDialog.isShowing ( )) {
                    HelperMethods.showProgressDialog (getActivity ( ), getResources ( ).getString (R.string.sending));
                }
            }
            mAuth.sendPasswordResetEmail (email)
                    .addOnCompleteListener (new OnCompleteListener<Void> ( ) {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            HelperMethods.dismissProgressDialog ( );
                            if (task.isSuccessful ( )) {
                                Toast.makeText (getActivity ( ), getResources ( ).getString (R.string.email_sent), Toast.LENGTH_SHORT).show ( );
                            } else {
                                Toast.makeText (getActivity ( ), getResources ( ).getString (R.string.failed), Toast.LENGTH_SHORT).show ( );
                            }
                        }
                    }).addOnFailureListener (new OnFailureListener ( ) {
                @Override
                public void onFailure(@NonNull Exception e) {
                    HelperMethods.dismissProgressDialog ( );
                    Toast.makeText (getActivity ( ), e.getMessage ( ), Toast.LENGTH_SHORT).show ( );
                }
            });
        } else {
            try {
                HelperMethods.onCreateErrorToast (getActivity ( ), getResources ( ).getString (R.string.error_inter_net));
            } catch (Exception e) {

            }

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
            mAuth.signInWithEmailAndPassword (email, password)
                    .addOnCompleteListener (getActivity ( ), new OnCompleteListener<AuthResult> ( ) {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            HelperMethods.dismissProgressDialog ( );
                            if (task.isSuccessful ( )) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser ( );
//                            assert user != null;
//                            SaveData(getActivity (), USER_ID, user.getUid ());
                                startActivity (new Intent (getActivity ( ), MainActivity.class));
                                getActivity ( ).finish ( );
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText (getActivity ( ), getString (R.string.authentication_failed), Toast.LENGTH_SHORT).show ( );
                            }
                        }
                    }).addOnFailureListener (new OnFailureListener ( ) {
                @Override
                public void onFailure(@NonNull Exception e) {
                    HelperMethods.dismissProgressDialog ( );
                    Toast.makeText (getActivity ( ), e.getMessage ( ), Toast.LENGTH_SHORT).show ( );
                }
            });
        } else {
            try {
                HelperMethods.onCreateErrorToast (getActivity ( ), getResources ( ).getString (R.string.error_inter_net));
            } catch (Exception e) {

            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult (requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent (data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult (ApiException.class);
                firebaseAuthWithGoogle (account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
//                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText (getActivity ( ), e.getMessage ( ), Toast.LENGTH_SHORT).show ( );
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        if (progressDialog == null) {
            HelperMethods.showProgressDialog (getActivity ( ), getResources ( ).getString (R.string.wait));
        } else {
            if (!progressDialog.isShowing ( )) {
                HelperMethods.showProgressDialog (getActivity ( ), getResources ( ).getString (R.string.wait));
            }
        }
        AuthCredential credential = GoogleAuthProvider.getCredential (acct.getIdToken ( ), null);
        mAuth.signInWithCredential (credential)
                .addOnCompleteListener (Objects.requireNonNull (getActivity ( )), new OnCompleteListener<AuthResult> ( ) {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        HelperMethods.dismissProgressDialog ( );
                        if (task.isSuccessful ( )) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser ( );
                            startActivity (new Intent (getActivity ( ), MainActivity.class));
                            getActivity ( ).finish ( );
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w (TAG, "signInWithCredential:failure", task.getException ( ));
                            Toast.makeText (getActivity ( ), getResources ( ).getString (R.string.login_failed), Toast.LENGTH_SHORT).show ( );
//                            updateUI(null);
                        }

                    }
                }).addOnFailureListener (new OnFailureListener ( ) {
            @Override
            public void onFailure(@NonNull Exception e) {
                HelperMethods.dismissProgressDialog ( );
                // get and show error message
                Toast.makeText (getActivity ( ), "signInWithCredential:failure ..." + e.getMessage ( ), Toast.LENGTH_SHORT).show ( );
            }
        });
    }
//    @OnClick(R.id.login_fragment_btn_google_login)
//    public void onViewClicked() {
//    }
}