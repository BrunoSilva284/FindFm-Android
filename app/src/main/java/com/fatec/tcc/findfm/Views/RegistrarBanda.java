package com.fatec.tcc.findfm.Views;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.fatec.tcc.findfm.Views.Adapters.AdapterInstrumentos;
import com.fatec.tcc.findfm.Model.Business.Banda;
import com.fatec.tcc.findfm.Model.Business.Instrumento;
import com.fatec.tcc.findfm.Model.Business.NivelHabilidade;
import com.fatec.tcc.findfm.Model.Http.Response.ErrorResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseBody;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseCode;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Infrastructure.Request.HttpTypedRequest;
import com.fatec.tcc.findfm.Utils.AlertDialogUtils;
import com.fatec.tcc.findfm.Utils.HttpUtils;
import com.fatec.tcc.findfm.Utils.Util;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RegistrarBanda extends AppCompatActivity {

    public static final int PICK_IMAGE = 1;
    private ImageView imageView;
    private ImageButton btnRemoverImagem;
    private Bundle param = new Bundle();
    private EditText txtFormacao;
    private RecyclerView rc;
    private HttpTypedRequest<Banda, ResponseBody, ErrorResponse> registrarRequest;
    private Date formacao;
    private ProgressDialog dialog;
    private Spinner cb_uf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_banda);
        init();
        updateList();
    }

    @Override
    protected void onDestroy() {
        this.dialog.dismiss();
        super.onDestroy();
    }

    private void init(){
        initRequests();
        this.rc = findViewById(R.id.listaInstrumentos);
        this.imageView = findViewById(R.id.circularImageView);
        this.btnRemoverImagem = findViewById(R.id.btnRemoverImagem);
        this.txtFormacao = findViewById(R.id.txtFormacao);
        this.cb_uf = findViewById(R.id.cb_uf);

        this.txtFormacao.setShowSoftInputOnFocus(false);
        this.txtFormacao.setInputType(InputType.TYPE_NULL);
        this.param = getIntent().getBundleExtra("com.fatec.tcc.findfm.Views.Registrar");
        this.dialog = new ProgressDialog(this);
        dialog.setMessage("Carregando...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);

        this.cb_uf.setAdapter(
                new ArrayAdapter<>(this, R.layout.simple_custom_list, getResources().getStringArray(R.array.lista_uf)));

        byte[] image = this.param.getByteArray("foto");

        if(image != null && image.length != 0) {
            this.imageView.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));

            this.btnRemoverImagem.setVisibility(View.VISIBLE);
        }
        else{
            this.btnRemoverImagem.setVisibility(View.INVISIBLE);
        }
    }

    private void initRequests() {
        registrarRequest = new HttpTypedRequest<>
                (
                        Request.Method.POST,
                        Banda.class,
                        ResponseBody.class,
                        ErrorResponse.class,
                        (ResponseBody response) ->
                        {
                            this.dialog.hide();
                            if(ResponseCode.from(response.getResponseCode()).equals(ResponseCode.GenericSuccess)) {
                                // Compartilhado com toda a aplicação, acessado pela Key abaixo \/
                                SharedPreferences.Editor editor = getSharedPreferences("FindFM_param", MODE_PRIVATE).edit();
                                editor.putBoolean("isLogado", true);
                                editor.putString("tipoUsuario", "BANDA");
                                editor.putString("nomeUsuario", param.getString("nomeCompleto"));
                                // As chaves precisam ser persistidas
                                editor.apply();
                                dialog.dismiss();
                                Util.open_form__no_return(this, HomePage.class);
                            }
                        },
                        (ErrorResponse errorResponse) ->
                        {
                            dialog.hide();
                        },
                        (Exception error) ->
                        {
                            dialog.hide();
                            error.printStackTrace();
                            AlertDialogUtils.newSimpleDialog__OneButton(this,
                                    "Ops!", R.drawable.ic_error,
                                    "Ocorreu um erro ao tentar conectar com nossos servidores." +
                                            "\nVerifique sua conexão com a Internet e tente novamente","OK",
                                    (dialog, id) -> { }).create().show();
                        }
                );
        registrarRequest.setFullUrl(HttpUtils.buildUrl(getResources(),"metro_api/login/registrar"));
    }

    private void updateList() {
        //TODO: server retornar uma lista mais completa
        List<Instrumento> instrumentos = Arrays.asList(
                new Instrumento("Guitarra", NivelHabilidade.INICIANTE),
                new Instrumento("Bateria", NivelHabilidade.INICIANTE),
                new Instrumento("Baixo", NivelHabilidade.INICIANTE),
                new Instrumento("Vocal", NivelHabilidade.INICIANTE),
                new Instrumento("Saxofone", NivelHabilidade.INICIANTE),
                new Instrumento("Flauta", NivelHabilidade.INICIANTE),
                new Instrumento("Piano", NivelHabilidade.INICIANTE),
                new Instrumento("Percussão", NivelHabilidade.INICIANTE),
                new Instrumento("Trombone", NivelHabilidade.INICIANTE));

        RecyclerView view = findViewById(R.id.listaInstrumentos);
        view.setAdapter( new AdapterInstrumentos(instrumentos,"BANDA",this) );
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        view.setLayoutManager( layout );

    }

    public void txtFormacao_Click (View v) {
        Util.hideSoftKeyboard(this);
        Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            this.formacao = myCalendar.getTime();
            this.txtFormacao.setText(sdf.format(myCalendar.getTime()));
        };
        new DatePickerDialog(this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void btnFoto_Click(View v){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Escolha uma foto"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            try {
                this.imageView.setImageBitmap(BitmapFactory.decodeStream(getApplicationContext()
                        .getContentResolver().openInputStream(data.getData())));
                this.btnRemoverImagem.setVisibility(View.VISIBLE);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void btnRemoverImagem_Click(View v){
        this.imageView.setImageDrawable(getResources().getDrawable(R.drawable.capaplaceholder, getTheme()));
        this.btnRemoverImagem.setVisibility(View.INVISIBLE);
    }

    public void btnRegistrar_Click (View v) {
        TextView txtNomeBanda = findViewById(R.id.txtNomeBanda);
        TextView txtFormacao = findViewById(R.id.txtFormacao);
        TextView txtNumeroIntegrantes = findViewById(R.id.txtNumeroIntegrantes);

        AdapterInstrumentos adapter = (AdapterInstrumentos) rc.getAdapter();
        List<Instrumento> instrumentos = new ArrayList<>();
        instrumentos.addAll(adapter.getInstrumentos());
        boolean isQtdInstrumentoVazio = false;

        if(!instrumentos.isEmpty()){
            for(Instrumento instrumento : instrumentos){
                if(instrumento.getQuantidade() == 0 ){
                    isQtdInstrumentoVazio = true;
                }
            }
        }

        if( txtNomeBanda.getText().toString().isEmpty()||
                txtFormacao.getText().toString().isEmpty() ||
                txtNumeroIntegrantes.getText().toString().isEmpty() ||
                instrumentos.isEmpty() || isQtdInstrumentoVazio)
            Toast.makeText(getApplicationContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
        else {
            this.dialog.show();

            Bitmap bitmap = ((BitmapDrawable) this.imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            this.param.putByteArray("foto", baos.toByteArray());
            this.param.putString("nomeCompleto", txtNomeBanda.getText().toString());
            int numeroParticipantes = Integer.parseInt(txtNumeroIntegrantes.getText().toString());

            Banda banda = new Banda(
                    param.getString("nomeUsuario"),
                    param.getString("senha"),
                    param.getString("email"),
                    param.getString("telefone"),
                    param.getByteArray("foto"),
                    false,
                    false,
                    param.getString("nomeCompleto"),
                    formacao,
                    instrumentos,
                    numeroParticipantes
            );

            registrarRequest.setRequestObject(banda);
            registrarRequest.execute(getApplicationContext());
        }
    }
}