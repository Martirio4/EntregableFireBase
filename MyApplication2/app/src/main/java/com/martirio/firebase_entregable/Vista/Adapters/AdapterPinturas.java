package com.martirio.firebase_entregable.Vista.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.martirio.firebase_entregable.Modelo.Paint;
import com.martirio.firebase_entregable.R;
import com.martirio.firebase_entregable.Vista.Activities.activityFullScreen;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by elmar on 11/7/2017.
 */

public class AdapterPinturas extends RecyclerView.Adapter{
    private Context context;
    private List<Paint> listaPinturasOriginales;
   

    public void setContext(Context context) {
        this.context = context;
    }

    public void setlistaPinturasOriginales(List<Paint> listaPinturas) {
        this.listaPinturasOriginales = listaPinturas;
    }
    public void addlistaPinturasOriginales(List<Paint> listaPinturasOriginales) {
        this.listaPinturasOriginales .addAll(listaPinturasOriginales);
    }


    public List<Paint> getlistaPinturasOriginales(){
        return listaPinturasOriginales;
    }

    //crear vista y viewholder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View viewCelda;
        FragmentActivity unaActivity= (FragmentActivity) context;
        viewCelda = layoutInflater.inflate(R.layout.celda_recycler, parent, false);
        PinturasViewHolder peliculasViewHolder = new PinturasViewHolder(viewCelda);


        return peliculasViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Paint unaPintura = listaPinturasOriginales.get(position);
        PinturasViewHolder PinturasViewHolder = (PinturasViewHolder) holder;
        PinturasViewHolder.cargarPintura(unaPintura);

        PinturasViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent=new Intent(v.getContext(), activityFullScreen.class);
                Bundle bundle=new Bundle();
                bundle.putString(activityFullScreen.IMAGEN, unaPintura.getImage());
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaPinturasOriginales.size();
    }





    //creo el viewholder que mantiene las referencias
    //de los elementos de la celda

    private static class PinturasViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textViewTitulo;


        public PinturasViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imagenRecycler);
            textViewTitulo=(TextView)itemView.findViewById(R.id.textoRecycler);

        }

        public void cargarPintura(Paint unaPintura) {
            if (unaPintura.getName().length()>24){

            }
            else {
                textViewTitulo.setText(unaPintura.getName());
            }

            Picasso.with(imageView.getContext())
                    .load(unaPintura.getImage())
                    .placeholder(R.drawable.loading)
                    .into(imageView);
        }


    }

    public void agregarPintura(Paint unaPintura){
        listaPinturasOriginales.add(unaPintura);
        notifyDataSetChanged();
    }


}
