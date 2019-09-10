package com.cukraren.kysucka.kysuckacukraren.Activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cukraren.kysucka.kysuckacukraren.Common.Common;
import com.cukraren.kysucka.kysuckacukraren.R;
import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    // Static VAR
    private static final int API_REQUEST_CODE = 7117;

    //
    @BindView(R.id.buttonLogin)
    Button buttonLogin;

    @BindView(R.id.txt_skin)
    TextView textIntro;

    @OnClick(R.id.buttonLogin)
    void LoginButtonUser()
    {
        final Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder confBuilder =
             new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE, AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, confBuilder.build());
        startActivityForResult(intent, API_REQUEST_CODE);
    }

    @OnClick(R.id.txt_skin)
    void skinLoginGoHomeFunction()
    {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(Common.IS_LOGIN, false);
        startActivity(intent);
    }

    // Protected OnActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == API_REQUEST_CODE)
        {
            AccountKitLoginResult accoutKitLoginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if(accoutKitLoginResult.getError() != null)
            {
                Toast.makeText(this, "" + accoutKitLoginResult.getError().getErrorType().getMessage() + "", Toast.LENGTH_SHORT).show();
            }
            else if(accoutKitLoginResult.wasCancelled())
            {
                Toast.makeText(this, "Login cancelled!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Intent intent  = new Intent(this, HomeActivity.class);
                intent.putExtra(Common.IS_LOGIN, true);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);
        // printKeyHash();
        AccessToken accessToken = AccountKit.getCurrentAccessToken();
        if(accessToken != null)
        {
            Intent intent  = new Intent(this, HomeActivity.class);
            intent.putExtra(Common.IS_LOGIN, true);
            startActivity(intent);
            finish();
        }
        else
        {
            setContentView(R.layout.activity_main);
            ButterKnife.bind(MainActivity.this);
        }

    }

    private void printKeyHash()
    {
        try
        {
            PackageInfo packageinfo = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES
            );
            for(Signature signature : packageinfo.signatures)
            {
                MessageDigest messageDig = MessageDigest.getInstance("SHA");
                messageDig.update(signature.toByteArray());
                Log.d("KEYHASH", Base64.encodeToString(messageDig.digest(), Base64.DEFAULT));
            }
        }
        catch(PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
    }
}
