package com.fatec.tcc.findfm.Views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.fatec.tcc.findfm.Controller.Login.LoginViewModel;
import com.fatec.tcc.findfm.Infrastructure.Request.ImageRequest;
import com.fatec.tcc.findfm.Infrastructure.Request.Volley.JsonTypedRequest;
import com.fatec.tcc.findfm.Infrastructure.Request.Volley.SharedRequestQueue;
import com.fatec.tcc.findfm.Model.Business.Contratante;
import com.fatec.tcc.findfm.Model.Business.Estados;
import com.fatec.tcc.findfm.Model.Business.Musico;
import com.fatec.tcc.findfm.Model.Business.TiposUsuario;
import com.fatec.tcc.findfm.Model.Business.Usuario;
import com.fatec.tcc.findfm.Model.Http.Response.ErrorResponse;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseBody;
import com.fatec.tcc.findfm.Model.Http.Response.ResponseCode;
import com.fatec.tcc.findfm.Model.Http.Response.User;
import com.fatec.tcc.findfm.R;
import com.fatec.tcc.findfm.Utils.AlertDialogUtils;
import com.fatec.tcc.findfm.Utils.FindFM;
import com.fatec.tcc.findfm.Utils.HttpUtils;
import com.fatec.tcc.findfm.Utils.ImagemUtils;
import com.fatec.tcc.findfm.Utils.JsonUtils;
import com.fatec.tcc.findfm.Utils.Util;
import com.fatec.tcc.findfm.databinding.ActivityLoginBinding;

import java.nio.charset.Charset;
import java.util.Map;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setViewModel(new LoginViewModel(this));
        binding.executePendingBindings();
        binding.getViewModel().init();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Aguarde...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);

        if(FindFM.isLogado(this)) {
            getUser(this);
        }
        else{
            ImageView imageView = findViewById(R.id.circularImageView2);
            byte[] image = FindFM.getFotoPrefBytes(this);
            if(image != null && image.length != 0) {
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
            }
            else{
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.capaplaceholder_photo, getTheme()));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialog.dismiss();
        binding.getViewModel().dismissDialog();
    }

    public void btnEntrar_Click(View v) {
        binding.getViewModel().btnEntrar_Click(v);
    }

    public void lb_recuperarSenha_Click(View v){
        Util.open_form(getApplicationContext(), RecuperarSenha.class);
    }

    public void lb_registrar_Click(View v){
        Util.open_form(getApplicationContext(), Registrar.class);
    }

    private void getUser(Activity activity) {
        JsonTypedRequest<Usuario, ResponseBody, ErrorResponse> registrarRequest = new JsonTypedRequest<>
                (       activity,
                        Request.Method.GET,
                        Usuario.class,
                        ResponseBody.class,
                        ErrorResponse.class,
                        HttpUtils.buildUrl(activity.getResources(),"account", "me"),
                        null,
                        (ResponseBody response) ->
                        {
                            if(ResponseCode.from(response.getCode()).equals(ResponseCode.GenericSuccess)) {
                                User user= JsonUtils.jsonConvert(((Map<String, Object>) response.getData()).get("usuario"), User.class);
                                Usuario usuario = new Usuario();
                                usuario.setId(user.getId());
                                usuario.setFotoID(user.getAvatar().get_id());
                                usuario.setTipoUsuario(TiposUsuario.fromKind(user.getKind()));
                                usuario.setNomeCompleto(user.getFullName());
                                usuario.setEmail(user.getEmail());
                                usuario.setTelefone(user.getTelefone().getStateCode() + user.getTelefone().getNumber());

                                FindFM.setUsuario(usuario);

                                switch (usuario.getTipoUsuario()){
                                    case CONTRATANTE:
                                        Contratante contratante = new Contratante(usuario);
                                        contratante.setInauguracao(user.getDate());
                                        contratante.setCidade(user.getEndereco().getCidade());
                                        contratante.setUf(
                                                Estados.fromNome( user.getEndereco().getEstado() ).getSigla()
                                        );
                                        FindFM.setContratante(contratante);
                                        break;
                                    case MUSICO:
                                        Musico musico = new Musico(usuario);
                                        musico.setNascimento(user.getDate());
                                        musico.setCidade(user.getEndereco().getCidade());
                                        musico.setUf(
                                                Estados.fromNome( user.getEndereco().getEstado() ).getSigla()
                                        );
                                        musico.setInstrumentos(user.getIntrumentos());
                                        FindFM.setMusico(musico);
                                        break;
                                }

                                if(usuario.getFotoID() != null)
                                    this.getImagemFromEndPoint(this, usuario.getFotoID());
                                else
                                    Util.open_form(getApplicationContext(), TelaPrincipal.class);
                            }
                        },
                        (ErrorResponse errorResponse) ->
                        {
                            dialog.dismiss();
                            AlertDialogUtils.newSimpleDialog__OneButton(activity,
                                    "Ops!", R.drawable.ic_error,
                                    errorResponse.getMessage(),"OK",
                                    (dialog, id) -> { }).create().show();
                        },
                        (VolleyError error) ->
                        {
                            dialog.dismiss();
                            error.printStackTrace();
                            AlertDialogUtils.newSimpleDialog__OneButton(activity,
                                    "Ops!", R.drawable.ic_error,
                                    "Ocorreu um erro ao tentar conectar com nossos servidores." +
                                            "\nVerifique sua conexão com a Internet e tente novamente","OK",
                                    (dialog, id) -> { }).create().show();
                        }
                );
        SharedRequestQueue.addToRequestQueue(activity.getApplicationContext(), registrarRequest);
        this.dialog.show();
    }

    public void getImagemFromEndPoint(Context context, String id_imagem) {

        ImageRequest imagemRequest = new ImageRequest(context, id_imagem, 0, 0, ImageView.ScaleType.CENTER_CROP,
                response -> {
                    dialog.dismiss();
                    String base64 = new String(Base64.encode(ImagemUtils.getByteArrayFromBitmap(response), Base64.DEFAULT), Charset.forName("UTF-8"));
                    FindFM.getUsuario().setFoto(base64);
                    FindFM.setFotoPref(this, base64);
                    Util.open_form(getApplicationContext(), TelaPrincipal.class);
                },
                error -> {
                    dialog.dismiss();
                    AlertDialogUtils.newSimpleDialog__OneButton(context, "Ops!", R.drawable.ic_error, "Não foi possível carregar sua foto", "OK",
                            (dialog, id) -> {
                                Util.open_form(getApplicationContext(), TelaPrincipal.class);
                            }).create().show();
                }
        );
        imagemRequest.execute();

    }
}
