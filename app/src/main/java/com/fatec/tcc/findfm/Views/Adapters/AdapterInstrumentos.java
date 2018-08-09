package com.fatec.tcc.findfm.Views.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.fatec.tcc.findfm.Model.Business.Instrumento;
import com.fatec.tcc.findfm.Model.Business.NivelHabilidade;
import com.fatec.tcc.findfm.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdapterInstrumentos extends RecyclerView.Adapter {
    //TODO GRAVE: Bug: Quando seleciona a primeira opção do RecyclerView, o ultimo tbm é selecionado
    private Set<Instrumento> instrumentosUsuario;
    private List<Instrumento> instrumentos;
    private String tipoTela;
    private Context context;

    public AdapterInstrumentos(List<Instrumento> instrumentos, String tipoTela, Context context) {
        this.tipoTela = tipoTela;
        this.instrumentos = instrumentos;
        this.context = context;
        this.instrumentosUsuario = new HashSet<>();
    }

    public Set<Instrumento> getInstrumentos(){
        return this.instrumentosUsuario;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate( R.layout.view_instumentos, parent, false);
        ViewHolderInstrumentos viewHolder = new ViewHolderInstrumentos( view );
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolderInstrumentos view = (ViewHolderInstrumentos) holder;
        Instrumento instrumento = instrumentos.get(position);

        switch (tipoTela){
            case "MUSICO":
                view.txtQuantidade.setVisibility(View.INVISIBLE);
                break;
            case "BANDA":
                view.txtQuantidade.setVisibility(View.VISIBLE);
                break;
        }

        view.checkInstrumento.setText(instrumento.getNome());
        view.cb_nivelHabilidade.setAdapter(
                new ArrayAdapter<>(this.context, R.layout.simple_custom_list, NivelHabilidade.values()));
        view.cb_nivelHabilidade.setSelection(instrumento.getNivelHabilidade().getCodigo() -1);

        view.checkInstrumento.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                int id = view.cb_nivelHabilidade.getSelectedItemPosition() + 1;
                instrumento.setNivelHabilidade(NivelHabilidade.from(id));
                instrumentosUsuario.add(instrumento);
            }
            else{
                instrumentosUsuario.remove(instrumento);
            }
        });
        view.cb_nivelHabilidade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Set<Instrumento> toRemove = new HashSet<>();
                Set<Instrumento> toAdd = new HashSet<>();
                for(Instrumento inst : instrumentosUsuario){
                    if(inst.equals(instrumento)){
                        inst.setNivelHabilidade(NivelHabilidade.from(position + 1));
                        toRemove.add(instrumento);
                        toAdd.add(inst);
                    }
                }
                instrumentosUsuario.removeAll(toRemove);
                instrumentosUsuario.addAll(toAdd);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        view.txtQuantidade.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!view.txtQuantidade.getText().toString().isEmpty()) {
                    Integer qtd = Integer.parseInt(view.txtQuantidade.getText().toString());
                    instrumentosUsuario.remove(instrumento);
                    instrumento.setQuantidade(qtd);
                    instrumentosUsuario.add(instrumento);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return instrumentos.size();
    }
}