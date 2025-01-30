package com.solver.callbutton;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.solver.callbutton.Model.ApiResponse;
import com.solver.callbutton.Model.Company;
import com.solver.callbutton.api.CompanyService;
import com.solver.callbutton.api.Retrofit2Client;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompanyListActivity extends AppCompatActivity {

    // Create an instance of Retrofit2Client
    private Retrofit2Client retrofit2Client;
    private CompanyService companyService;

    private RelativeLayout relativeLayoutLoading;
    private RelativeLayout relativeLayoutNetword_error_layout;
    private RelativeLayout no_data_found_layout;
    private Button btn_try_again;
    private TextView tv_no_result;

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView mRecyclerView;
    private ListAdapter listAdapter;
    private EditText editTextSearch;
    private ImageButton btn_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_company_list);

        retrofit2Client = new Retrofit2Client(getApplicationContext());
        companyService = retrofit2Client.createService(CompanyService.class);

        //GENERAL
        relativeLayoutLoading = (RelativeLayout) findViewById(R.id.rlLoading);
        relativeLayoutNetword_error_layout = (RelativeLayout) findViewById(R.id.network_error_layout);
        no_data_found_layout = (RelativeLayout) findViewById(R.id.no_result_found_layout);
        tv_no_result = (TextView) findViewById(R.id.tv_no_result);
        btn_try_again = (Button) findViewById(R.id.btn_try_again);
        btn_back = (ImageButton) findViewById(R.id.btn_back);

        mRecyclerView = findViewById(R.id.rv_item);
        layoutManager = new GridLayoutManager(getApplicationContext(), 1);
        mRecyclerView.setLayoutManager(layoutManager);
        editTextSearch=(EditText) findViewById(R.id.eTRoutesSearch);

        findAll();

        //Code pour filtrage
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        btn_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> implements Filterable {

        private List<Company> dataSet;
        private List<Company> dataSetFull;
        private Context context;

        public ListAdapter(List<Company> listItems, Context context) {
            this.dataSet = listItems;
            dataSetFull = new ArrayList<>(listItems);
            this.context = context;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            public TextView textViewName;
            public CardView card_view;

            public MyViewHolder(View itemView) {
                super(itemView);
                this.textViewName = (TextView) itemView.findViewById(R.id.title_products);
                this.card_view = (CardView) itemView.findViewById(R.id.cardCategory);
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_company, parent, false);
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
                    Intent returnIntent = new Intent(view.getContext(), RegisterActivity.class);
                    returnIntent.putExtra("ID_COMPANY", String.valueOf(listItem.getId()));
                    returnIntent.putExtra("NOM_COMPANY", listItem.getCompany_name());
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            });
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }

        @Override
        public Filter getFilter() {
            return exampleFilter;
        }

        private Filter exampleFilter=new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Company> filteredList=new ArrayList<>();
                if (constraint == null || constraint.length() ==0){

                    filteredList.addAll(dataSetFull);
                }
                else {

                    String filterPattern=constraint.toString().toLowerCase().trim();
                    for (Company item : dataSetFull){

                        if (item.getCompany_name().toLowerCase().contains(filterPattern)){

                            filteredList.add(item);

                        }

                    }
                }
                FilterResults results= new FilterResults();
                results.values=filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                dataSet.clear();
                dataSet.addAll((List) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public void findAll() {
        Call<ApiResponse> call = companyService.findAll();
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {


                if (response.isSuccessful()) {
                    ApiResponse apiResponse = response.body();

                    if (apiResponse != null && apiResponse.isSuccess()) {
                        List<Company> companies = apiResponse.getData();



                        if (companies.isEmpty()) {
                            relativeLayoutNetword_error_layout.setVisibility(View.GONE);
                            no_data_found_layout.setVisibility(View.VISIBLE);
                            relativeLayoutLoading.setVisibility(View.GONE);
                        } else {
                            relativeLayoutNetword_error_layout.setVisibility(View.GONE);
                            no_data_found_layout.setVisibility(View.GONE);
                            relativeLayoutLoading.setVisibility(View.GONE);
                        }

                        // Set the adapter with the response data
                        listAdapter = new ListAdapter(companies, getApplicationContext());
                        mRecyclerView.setAdapter(listAdapter);  // Set the correct adapter here
                    } else {
                        Toast.makeText(CompanyListActivity.this, "No companies found", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(CompanyListActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Handle failure
                relativeLayoutLoading.setVisibility(View.GONE);
                relativeLayoutNetword_error_layout.setVisibility(View.VISIBLE);
                Toast.makeText(CompanyListActivity.this, "Request failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //////CODE POUR BAR DE RECHERCHE DANS TOOLBAR/////////////

    private void filter(String text) {
        listAdapter.getFilter().filter(text);
    }
    ///////FIN CODE POUR BAR DE RECHERCHE DANS TOOLBAR/////////////

}
