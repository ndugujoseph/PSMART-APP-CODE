package com.example.psmart;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.psmart.ui.login.Account;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DisplayActivity extends AppCompatActivity {

    //this is the JSON Data URL
    //make sure you are using the correct ip else it will not work
    private static final String URL_ACCOUNT = "https://solfixafrica.com/psmart/accountApi.php?apicall=";

    //a list to store all the products
    List<Account> accountList;

    //the recyclerview
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        //getting the recyclerview from xml
        recyclerView = findViewById(R.id.recylcerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //initializing the productlist
        accountList = new ArrayList<>();

        //this method will fetch and parse json
        //to display it in recyclerview
        loadAccount();
    }

    private void loadAccount() {


        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_ACCOUNT,
                response -> {
                    try {
                        //converting the string to json array object
                        JSONArray array = new JSONArray(response);

                        //traversing through all the object
                        for (int i = 0; i < array.length(); i++) {

                            //getting product object from json array
                            JSONObject account = array.getJSONObject(i);

                            //adding the product to product list
                            accountList.add(new Account(
                                    account.getInt("id"),
                                    account.getString("name"),
                                    account.getString("gender"),
                                    account.getString("email"),
                                    account.getString("contact"),
                                    account.getString("image")
                            ));
                        }

                        //creating adapter object and setting it to recyclerview
                      // AccountAdapter adapter = new AccountAdapter(DisplayActivity.this, accountList);
                      //  recyclerView.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {

                });

        //adding our stringRequest to queue
        Volley.newRequestQueue(this).add(stringRequest);
    }
}
