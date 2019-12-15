package com.example.appmonitoramento;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import org.json.JSONException;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class confirmacaoTAG extends AppCompatActivity {
    RespostaServidorCadastrarBotijao resposta = new RespostaServidorCadastrarBotijao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacao_tag);

        //Pega a intent de outra activity
        final Intent confirmacaoTAG = getIntent();

        //Recuperei a string da outra activity
        final String peso = confirmacaoTAG.getStringExtra("peso");
        final String tag = confirmacaoTAG.getStringExtra("tag");

        TextView peso_t = (TextView) findViewById(R.id.textViewPesoRecebido);
        peso_t.setText(peso);

        Button btTara = (Button) findViewById(R.id.buttonTara);
        btTara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tTara = (TextView) findViewById(R.id.editTextTara);
                TextView tPlace = (TextView) findViewById(R.id.editTextLugar);

                String tara = tTara.getText().toString();
                String place = tPlace.getText().toString();
                RespostaServidorLogin teste = new RespostaServidorLogin();
                SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(confirmacaoTAG.this);
                String user = myPreferences.getString("USUARIO", "unknown");
                String senha = myPreferences.getString("SENHA", "unknown");
                String token = myPreferences.getString("TOKEN", "unknown");
                try {
                    retrofitEnviarTara(tara,tag,place,peso,user,senha);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent monitoraScreen = new Intent(confirmacaoTAG.this, monitoraScreen.class);
                monitoraScreen.putExtra("token",teste.getToken());
                startActivity(monitoraScreen);
            }
        });
    }

    public void retrofitEnviarTara(String tara, String tag, String lugar, String peso, String usuario, String senha) throws JSONException {

        RetrofitService service = ServiceGenerator.createService(RetrofitService.class,usuario,senha);

        final String json  =  "{\"tareweight\": \""+tara+"\",\"tag\":\""+tag+"\",\"place\":\""+lugar+"\",\"weight\":\""+peso+"\"}";
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);
        //System.out.println(token);
        //Call<User> userCall = apiInterface.getUser(paramObject.toString());

        Call<RespostaServidorCadastrarBotijao> call = service.cadastrarBotijao(body);

        call.enqueue(new Callback<RespostaServidorCadastrarBotijao>() {
            @Override
            public void onResponse(Call<RespostaServidorCadastrarBotijao> call, Response<RespostaServidorCadastrarBotijao> response) {

                if (response.isSuccessful()) {

                    RespostaServidorCadastrarBotijao respostaServidor = response.body();

                    //verifica aqui se o corpo da resposta não é nulo
                    if (respostaServidor != null) {
                        resposta.setTag(respostaServidor.getTag());
                        resposta.setWeight(respostaServidor.getWeight());
                        resposta.setTareweight(respostaServidor.getTareweight());
                        resposta.setPlace(respostaServidor.getPlace());
                        alert(resposta.getPlace());
                        //setaValores();
                        //progress.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(),"Resposta nula do servidor", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"Resposta não foi sucesso", Toast.LENGTH_SHORT).show();
                    // segura os erros de requisição
                    ResponseBody errorBody = response.errorBody();
                }
                //progress.dismiss();
            }

            @Override
            public void onFailure(Call<RespostaServidorCadastrarBotijao> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Erro na chamada ao servidor", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void alert(String s){
        Toast.makeText(this,s, Toast.LENGTH_LONG).show();
    }
}
