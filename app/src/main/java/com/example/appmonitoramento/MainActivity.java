package com.example.appmonitoramento;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//0import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    private BroadcastReceiver broadcastReceiver;
    RespostaServidorLogin respostaLogin = new RespostaServidorLogin();
    RespostaServidorEnvioToken respostaEnvioToken = new RespostaServidorEnvioToken();
    String tokenApp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Ver no banco se o usuario já não está salvo
        final SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String user_salvo = myPreferences.getString("USUARIO", "unknown");
        String senha_salvo = myPreferences.getString("SENHA", "unknown");
        String token = myPreferences.getString("TOKEN", "unknown");

        try {
            retrofitFazerLogin(user_salvo,senha_salvo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(respostaLogin.getToken()!= null){
            alert("Login Realizado com Sucesso");
            try {
                retrofitEnviarToken(tokenApp, user_salvo, senha_salvo);
                alert("TOKEN" + tokenApp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //alert(respostaLogin.getToken());
            SharedPreferences.Editor myEditor = myPreferences.edit();
            myEditor.putString("USUARIO", user_salvo);
            myEditor.putString("SENHA", senha_salvo);
            myEditor.putString("TOKEN", respostaLogin.getToken());
            Intent monitoraScreen = new Intent(MainActivity.this, monitoraScreen.class);
            monitoraScreen.putExtra("token",respostaLogin.getToken());
            startActivity(monitoraScreen);
        }else {
            alert("LOGIN OU SENHA INCORRETOS");
        };

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        if(verificarPlayServices()) {
            Intent intent = new Intent(this, MyFirebaseInstanceService.class);
            startService(intent);
        }


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        tokenApp = task.getResult().getToken();
                        System.out.println(tokenApp);
                        // Log and toast
                        //String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, tokenApp);
                        Toast.makeText(MainActivity.this, tokenApp, Toast.LENGTH_SHORT).show();
                    }
                });



        Button btLogin = (Button) findViewById(R.id.btLogin);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tLogin = (TextView) findViewById(R.id.tLogin);
                TextView tSenha = (TextView) findViewById(R.id.tSenha);

                String login = tLogin.getText().toString();
                String senha = tSenha.getText().toString();

                try {
                    retrofitFazerLogin(login,senha);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(respostaLogin.getToken()!= null){
                    alert("Login Realizado com Sucesso");
                    try {
                        retrofitEnviarToken(tokenApp, login, senha);
                        alert("TOKEN" + tokenApp);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    /*SQLiteDatabase db = mDbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(PostContract.PostEntry.COLUMN_NAME_USUARIO, login);
                    values.put(PostContract.PostEntry.COLUMN_NAME_TOKEN, respostaLogin.getToken());
                    long newRowId = db.insert(PostContract.PostEntry.TABLE_NAME, null, values);*/

                    //alert(respostaLogin.getToken());
                    SharedPreferences.Editor myEditor = myPreferences.edit();
                    myEditor.putString("USUARIO", login);
                    myEditor.putString("SENHA", senha);
                    myEditor.putString("TOKEN", respostaLogin.getToken());
                    myEditor.commit();

                    Intent monitoraScreen = new Intent(MainActivity.this, monitoraScreen.class);
                    monitoraScreen.putExtra("token",respostaLogin.getToken());
                    startActivity(monitoraScreen);
                }else {
                    alert("LOGIN OU SENHA INCORRETOS");
                };

            }
        });

    }
    private void alert(String s){
        Toast.makeText(this,s, Toast.LENGTH_LONG).show();
    }
    private boolean verificarPlayServices() {
        int codigo = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (codigo != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(codigo)) {
                GooglePlayServicesUtil.getErrorDialog(codigo, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
                Log.i(TAG, "BANANA.");
            } else {
                Log.i(TAG, "Este dispositivo não permite usar o recurso.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter("registrationComplete"));
    }
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    public void retrofitFazerLogin(String usuario, String Senha) throws JSONException {
        if (respostaLogin.getToken() != null){
            alert("Usuario já está logado");
            RetrofitService service = ServiceGenerator.createService(RetrofitService.class, respostaLogin.getToken());

            //final String json  =  "{\"idUsuario\": \"1\"}";
            //RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);

            //Call<RespostaServidor> call = service.consultarConsumo(body);

        }else {
            RetrofitService service = ServiceGenerator.createService(RetrofitService.class, usuario, Senha);

            Call<RespostaServidorLogin> call = service.consultarLogin();

            call.enqueue(new Callback<RespostaServidorLogin>() {
                @Override
                public void onResponse(Call<RespostaServidorLogin> call, Response<RespostaServidorLogin> response) {

                    if (response.isSuccessful()) {

                        RespostaServidorLogin respostaServidorLogin = response.body();

                        //verifica aqui se o corpo da resposta não é nulo
                        if (respostaServidorLogin != null) {
                            respostaLogin.setToken(respostaServidorLogin.getToken());
                            //setaValores();
                            //progress.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(), "Resposta nula do servidor", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Resposta não foi sucesso", Toast.LENGTH_SHORT).show();
                        // segura os erros de requisição
                        ResponseBody errorBody = response.errorBody();
                    }
                    //progress.dismiss();
                }


                @Override
                public void onFailure(Call<RespostaServidorLogin> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Erro na chamada ao servidor" + t, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void retrofitEnviarToken(String token, String login, String senha) throws JSONException {
        RetrofitService service = ServiceGenerator.createService(RetrofitService.class, login,senha);

        final String json  =  "{\"app_token\": \"" + token+"\"}";
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);

        Call<RespostaServidorEnvioToken> call = service.enviarToken(body);


        call.enqueue(new Callback<RespostaServidorEnvioToken>() {
            @Override
            public void onResponse(Call<RespostaServidorEnvioToken> call, Response<RespostaServidorEnvioToken> response) {

                if (response.isSuccessful()) {

                    RespostaServidorEnvioToken respostaServidorEnvioToken = response.body();

                    //verifica aqui se o corpo da resposta não é nulo
                    if (respostaServidorEnvioToken != null) {
                        respostaServidorEnvioToken.setStatus(respostaServidorEnvioToken.getStatus());


                        //setaValores();
                        //progress.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "Resposta nula do servidor", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Resposta não foi sucesso", Toast.LENGTH_SHORT).show();
                    // segura os erros de requisição
                    ResponseBody errorBody = response.errorBody();
                }
                //progress.dismiss();
            }


            @Override
            public void onFailure(Call<RespostaServidorEnvioToken> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Erro na chamada ao servidor" + t, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
