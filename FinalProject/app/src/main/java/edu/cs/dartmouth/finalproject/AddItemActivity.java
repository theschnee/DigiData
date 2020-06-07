package edu.cs.dartmouth.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AddItemActivity extends AppCompatActivity {

    private EditText mName, msecond, mthird, mfourth;
    private EditText mContent, mfifth, msixth, mseventh;
    private EditText meigth, mnineth, mtenth, meleventh;

    private Button mButton;
    private ProgressBar progressBar;
    private String receivedWord;
    private ArrayList<String> received_sentences;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        progressBar = findViewById(R.id.progressBar);
        mButton = findViewById(R.id.button);
        mName = findViewById(R.id.Name);
        mContent = findViewById(R.id.Content);
        msecond =findViewById(R.id.second_section);
        mthird = findViewById(R.id.third_section);
        mfourth = findViewById(R.id.fourth_section);
        mfifth = findViewById(R.id.fifth_section);
        msixth = findViewById(R.id.sixth_section);
        mseventh = findViewById(R.id.seventh_section);
        meigth = findViewById(R.id.eighth_section);
        mnineth = findViewById(R.id.nineth_section);
        mtenth = findViewById(R.id.tenth_section);

        //receivedWord = getIntent().getStringExtra("word");
        received_sentences = getIntent().getStringArrayListExtra("sentences");
        for(int i = 0; i<received_sentences.size(); i++){
            if(received_sentences.get(i).contains("Gross Profit"))
                mName.setText("Gross Profit");
      //      }else {
                String[] split_recieved = received_sentences.get(i).trim().split("\\s+");

                for(int j=0; j<split_recieved.length; j++){

               //    if(Integer.parseInt(split_recieved[0]) == java.lang.NumberFormatException)




                    if(split_recieved[j].contains("$") || split_recieved[j].contains("S")) {
                        if (split_recieved[j].contains("Co") || split_recieved[j].contains("So")){
                            break;
                        }

                        if(split_recieved[j].contains("S")) {
                            split_recieved[j].replace("S","$");
                        }

                        switch (counter){
                            case 0:
                                mContent.setText(split_recieved[j]);
                                counter = counter + 1;
                                break;
                            case 1:
                                msecond.setText(split_recieved[j]);
                                counter = counter + 1;
                                break;
                            case 2:
                                mthird.setText(split_recieved[j]);
                                counter = counter + 1;
                                break;
                            case 3:
                                mfourth.setText(split_recieved[j]);
                                counter = counter + 1;
                                break;
                            case 4:
                                mfifth.setText(split_recieved[j]);
                                counter = counter + 1;
                                break;
                            case 5:
                                msixth.setText(split_recieved[j]);
                                counter = counter + 1;
                                break;
                            case 6:
                                mseventh.setText(split_recieved[j]);
                                counter = counter + 1;
                                break;
                            case 7:
                                meigth.setText(split_recieved[j]);
                                counter = counter + 1;
                                break;
                            case 8:
                                mnineth.setText(split_recieved[j]);
                                counter = counter + 1;
                                break;
                        }


                    //    mContent.setText(split_recieved[j]);
                    }


                }


                System.out.println("SIZE OF SPLIT ARRAY" + split_recieved.length);
                System.out.println("CONTENTS OF SPLIT ARRAYYY" + Arrays.toString(split_recieved));


               // if(!split_recieved[0].contains(" ")) {
//                for(int j = 0; j < split_recieved.length; j++){
//                    if(split_recieved[j].contains("$")) {
//                        System.out.println("SPLIT SENTENCE " + split_recieved + "");
//                        System.out.print("SIZEEE" + split_recieved.length);
//                       // mContent.setText(split_recieved[i]);
//                    }
//                }
               // }


                //if (split_recieved[0].contains("Gross") || split_recieved[1].contains("Profit")) {
//                    mName.setText(split_recieved[0] + " " + split_recieved[1]);

//                    mContent.setText(split_recieved[2]);
//                    msecond.setText(split_recieved[3]);
//                    mthird.setText(split_recieved[4]);
//                    mfourth.setText(split_recieved[5]);
//                    mfifth.setText(split_recieved[6]);
//                    msixth.setText(split_recieved[7]);
//                    mseventh.setText(split_recieved[8]);
//                    meigth.setText(split_recieved[9]);
//                    mnineth.setText(split_recieved[10]);

                //}
   //         }
       //     }

            if(received_sentences.get(i).contains("May")){
               // mContent.setText(received_sentences.get(i));
            }

        }

      //  mName.setText( receivedWord);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToSheet();

            }
        });

    }

    private void addItemToSheet() {
        progressBar.setVisibility(View.INVISIBLE);

        final String Name = mName.getText().toString();
        final String content = mContent.getText().toString();
        final String second = msecond.getText().toString();
        final String third = mthird.getText().toString();
        final String fourth = mfourth.getText().toString();
        final String fifth = mfifth.getText().toString();
        final String sixth = msixth.getText().toString();
        final String seventh = mseventh.getText().toString();
        final String eigth = meigth.getText().toString();

        final String nineth = mnineth.getText().toString();
        final String tenth = mtenth.getText().toString();




        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbwjT689_TgvwSWY5vrekuwLMMZnWK6Q_mFlIu24UXaC4YHyKVk/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressBar.setVisibility(View.VISIBLE);
                    //    Toast.makeText(AddItemActivity.this, response, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddItemActivity.this, MainActivity.class);
                        startActivity(intent);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }

                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parameters = new HashMap<>();

                //here we pass params
                parameters.put("action", "addItem");
                parameters.put("Name", Name);

                parameters.put("First", content);

                parameters.put("Second", second);
                parameters.put("Third", third);
                parameters.put("Fourth", fourth);
                parameters.put("Fifth", fifth);
                parameters.put("Sixth", sixth);
                parameters.put("Seventh", seventh);
                parameters.put("Eigth", eigth);





                return parameters;
            }
        };


        RetryPolicy retryPolicy = new DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue requestQueue = Volley.newRequestQueue(AddItemActivity.this);
        requestQueue.add(stringRequest);


    }



}
