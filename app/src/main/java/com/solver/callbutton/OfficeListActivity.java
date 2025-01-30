package com.solver.callbutton;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.solver.callbutton.Model.ApiOfficeResponse;
import com.solver.callbutton.Model.ApiResponse;
import com.solver.callbutton.Model.Office;
import com.solver.callbutton.api.CompanyService;
import com.solver.callbutton.api.OfficeService;
import com.solver.callbutton.api.Retrofit2Client;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OfficeListActivity extends AppCompatActivity {

    // Create an instance of Retrofit2Client
    private Retrofit2Client retrofit2Client;
    private OfficeService officeService;

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
        setContentView(R.layout.activity_office_list);

        retrofit2Client = new Retrofit2Client(getApplicationContext());
        officeService = retrofit2Client.createService(OfficeService.class);

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

        Long company_id=Long.parseLong(getIntent().getExtras().getString("ID_COMPANY"));

        findCompanyOffices(company_id);

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

        private List<Office> dataSet;
        private List<Office> dataSetFull;
        private Context context;

        public ListAdapter(List<Office> listItems, Context context) {
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
            final Office listItem = dataSet.get(position);
            holder.textViewName.setText(listItem.getOffice_name());

            holder.card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    Intent returnIntent = new Intent(view.getContext(), RegisterActivity.class);
                    returnIntent.putExtra("ID_OFFICE", String.valueOf(listItem.getId()));
                    returnIntent.putExtra("NOM_OFFICE", listItem.getOffice_name());
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
                List<Office> filteredList=new ArrayList<>();
                if (constraint == null || constraint.length() ==0){

                    filteredList.addAll(dataSetFull);
                }
                else {

                    String filterPattern=constraint.toString().toLowerCase().trim();
                    for (Office item : dataSetFull){

                        if (item.getOffice_name().toLowerCase().contains(filterPattern)){

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

    public void findCompanyOffices(Long id) {
        Call<ApiOfficeResponse> call = officeService.getOfficeById(id);
        call.enqueue(new Callback<ApiOfficeResponse>() {
            @Override
            public void onResponse(Call<ApiOfficeResponse> call, Response<ApiOfficeResponse> response) {


                if (response.isSuccessful()) {
                    ApiOfficeResponse apiResponse = response.body();

                    if (apiResponse != null) {
                        List<Office> offices = apiResponse.getData();



                        if (offices.isEmpty()) {
                            relativeLayoutNetword_error_layout.setVisibility(View.GONE);
                            no_data_found_layout.setVisibility(View.VISIBLE);
                            relativeLayoutLoading.setVisibility(View.GONE);
                        } else {
                            relativeLayoutNetword_error_layout.setVisibility(View.GONE);
                            no_data_found_layout.setVisibility(View.GONE);
                            relativeLayoutLoading.setVisibility(View.GONE);
                        }

                        // Set the adapter with the response data
                        listAdapter = new ListAdapter(offices, getApplicationContext());
                        mRecyclerView.setAdapter(listAdapter);  // Set the correct adapter here
                    } else {
                        Toast.makeText(OfficeListActivity.this, "No companies found", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(OfficeListActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiOfficeResponse> call, Throwable t) {
                // Handle failure
                relativeLayoutLoading.setVisibility(View.GONE);
                relativeLayoutNetword_error_layout.setVisibility(View.VISIBLE);
                Toast.makeText(OfficeListActivity.this, "Request failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //////CODE POUR BAR DE RECHERCHE DANS TOOLBAR/////////////

    private void filter(String text) {
        listAdapter.getFilter().filter(text);
    }
    ///////FIN CODE POUR BAR DE RECHERCHE DANS TOOLBAR/////////////

}