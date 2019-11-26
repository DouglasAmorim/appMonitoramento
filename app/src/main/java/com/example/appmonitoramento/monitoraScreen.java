package com.example.appmonitoramento;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.*;

public class monitoraScreen extends AppCompatActivity {
    RespostaServidor resposta = new RespostaServidor();
    ProgressDialog progress;

    public void clique(View view) throws JSONException {
        // Pegar o valor atual do gás de cozinha
        //chama o retrofit para fazer a requisição no webservice
        retrofitConsultar(1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void setaValores(){
        TextView consumoGas = (TextView) findViewById(R.id.textViewvalorGas);
        consumoGas.setText(resposta.getValor());

        TextView idBotijao = (TextView) findViewById(R.id.textViewidBotijao);
        idBotijao.setText(resposta.getIdBotijao());

        TextView data = (TextView) findViewById(R.id.textViewDataMonitoramento);
        data.setText(resposta.getData());

    }

    public void retrofitConsultar(int idUsuario) throws JSONException {

        RetrofitService service = ServiceGenerator.createService(RetrofitService.class);

        final String json  =  "{\"idUsuario\": \"1\"}";
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);

        //Call<User> userCall = apiInterface.getUser(paramObject.toString());

        Call<RespostaServidor> call = service.consultarConsumo(body);

        call.enqueue(new Callback<RespostaServidor>() {
            @Override
            public void onResponse(Call<RespostaServidor> call, Response<RespostaServidor> response) {

                if (response.isSuccessful()) {

                    RespostaServidor respostaServidor = response.body();

                    //verifica aqui se o corpo da resposta não é nulo
                    if (respostaServidor != null) {
                        resposta.setData(respostaServidor.getData());
                        resposta.setIdBotijao(respostaServidor.getIdBotijao());
                        resposta.setIdMonitoramento(respostaServidor.getIdMonitoramento());
                        resposta.setIdSensor(respostaServidor.getIdSensor());
                        resposta.setIdUsuario(respostaServidor.getIdUsuario());
                        resposta.setValor(respostaServidor.getValor());

                        setaValores();
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
            public void onFailure(Call<RespostaServidor> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Erro na chamada ao servidor", Toast.LENGTH_SHORT).show();
            }
        });

    }
}

