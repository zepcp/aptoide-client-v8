/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 17/06/2016.
 */

package cm.aptoide.accountmanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.design.ShowMessage;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

/**
 * Created by trinkes on 4/29/16.
 */
public class SignUpActivity extends BaseActivity implements AptoideAccountManager.IRegisterUser {

  private Button signUpButton;
  private Toolbar mToolbar;
  private EditText password_box;
  private EditText emailBox;
  private Button hidePasswordButton;
  private View content;

  private String SIGNUP = "signup";
  private TwitterLoginButton loginButton;

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    // Make sure that the loginButton hears the result from any
    // Activity that it triggered.
    loginButton.onActivityResult(requestCode, resultCode, data);
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getLayoutId());
    bindViews();
    setupToolbar();
    setupListeners();

    loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
    loginButton.setCallback(new Callback<TwitterSession>() {
      @Override public void success(Result<TwitterSession> result) {
        // The TwitterSession is also available through:
        // Twitter.getInstance().core.getSessionManager().getActiveSession()
        TwitterSession session = result.data;
        // with your app's user model
        String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        Logger.v("TwitterKit", msg);
        TwitterAuthToken authToken = session.getAuthToken();
        String token = authToken.token;
        String secret = authToken.secret;
        Logger.v("TwitterKit", token + " " + secret);

        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setMessage(msg + "\n token scret: " + token + " " + secret);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialogInterface, int i) {

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, msg + "\n token scret: " + token + " " + secret);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
          }
        });
        builder.create().show();
      }

      @Override public void failure(TwitterException exception) {
        Logger.d("TwitterKit", "Login with Twitter failure", exception);
      }
    });
  }

  @Override protected String getActivityTitle() {
    return getString(R.string.register);
  }

  @Override int getLayoutId() {
    return R.layout.sign_up_activity_layout;
  }

  private void bindViews() {
    content = findViewById(android.R.id.content);
    signUpButton = (Button) findViewById(R.id.submitCreateUser);
    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    emailBox = (EditText) findViewById(R.id.username);
    password_box = (EditText) findViewById(R.id.password);
    hidePasswordButton = (Button) findViewById(R.id.btn_show_hide_pass);
  }

  private void setupToolbar() {
    if (mToolbar != null) {
      setSupportActionBar(mToolbar);
      getSupportActionBar().setHomeButtonEnabled(true);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setDisplayShowTitleEnabled(true);
      getSupportActionBar().setTitle(getActivityTitle());
    }
  }

  private void setupListeners() {
    setupShowHidePassButton();
    AptoideAccountManager.setupRegisterUser(this, signUpButton);
  }

  private void setupShowHidePassButton() {
    hidePasswordButton.setOnClickListener(v -> {
      final int cursorPosition = password_box.getSelectionStart();
      final boolean passwordShown = password_box.getTransformationMethod() == null;
      v.setBackgroundResource(
          passwordShown ? R.drawable.icon_closed_eye : R.drawable.icon_open_eye);
      password_box.setTransformationMethod(
          passwordShown ? new PasswordTransformationMethod() : null);
      password_box.setSelection(cursorPosition);
    });
  }

  @Override public void onRegisterSuccess(Bundle data) {
    ShowMessage.asSnack(content, R.string.user_created);
    data.putString(AptoideLoginUtils.APTOIDE_LOGIN_FROM, SIGNUP);
    setResult(RESULT_OK, new Intent().putExtras(data));
    Analytics analytics = AptoideAccountManager.getAnalytics();
    if (analytics != null) {
      analytics.signUp();
    }
    AptoideAccountManager.sendLoginBroadcast();
    finish();
  }

  @Override public void onRegisterFail(@StringRes int reason) {
    ShowMessage.asSnack(content, reason);
  }

  @Override public String getUserPassword() {
    return password_box == null ? "" : password_box.getText().toString();
  }

  @Override public String getUserEmail() {
    return emailBox == null ? "" : emailBox.getText().toString().toLowerCase();
  }
}
