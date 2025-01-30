package com.solver.callbutton.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.solver.callbutton.Model.Company;

import java.util.List;
import com.solver.callbutton.R;

public class CompanyListAdapter extends RecyclerView.Adapter<CompanyListAdapter.MyViewHolder>{

    private List<Company> dataSet;
    private Context mContext;
    private int selectedPos=1;
    private VehiculeTypeItemActionListener vehiculeTypeItemActionListener;

    public CompanyListAdapter(Context context, List<Company> listItems, VehiculeTypeItemActionListener vehiculeTypeItemActionListener) {
        this.dataSet = listItems;
        this.mContext = context;
        this.vehiculeTypeItemActionListener=vehiculeTypeItemActionListener;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewName;
        public CardView card_view;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewName=(TextView) itemView.findViewById(R.id.title_products);
            this.card_view = (CardView) itemView.findViewById(R.id.cardCategory);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_company, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final Company listItem = dataSet.get(position);
        holder.textViewName.setText(listItem.getCompany_name());

        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                vehiculeTypeItemActionListener.onVehiculeTypeItemClick(view,String.valueOf(listItem.getId()));

            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface VehiculeTypeItemActionListener{
        void onVehiculeTypeItemClick(View view, String idTypeVehicule);
    }
}
