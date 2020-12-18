package com.example.ghkdw.lotterycheck;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ghkdw on 2020-10-14.
 */

public class RecordFragment extends Fragment {
    private String session;
    View view;
    Context context;
    ImageView imageView;
    JsonObject jsonObject;
    TextView label;
    LinearLayout parentLayout;

    public static int count = 0;
    public ArrayList<Integer> colorCount = new ArrayList<Integer>();

    public static RecordFragment newInstance(int page, String title, String session) {
        RecordFragment fragment = new RecordFragment();
        Bundle args = new Bundle();
        args.putInt("page", page);
        args.putString("title", title);
        args.putString("session", session);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = getArguments().getString("session");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.slider, container, false);
        context = container.getContext();

        imageView = (ImageView)view.findViewById(R.id.slideImage);
        imageView.setImageResource(R.drawable.noun_records_1992836);

        label = (TextView)view.findViewById(R.id.slideLabel);
        label.setText("기록조회");

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(session.equals("no")) {
                    Toast.makeText(context, "회원가입 후 이용해주세요", Toast.LENGTH_SHORT).show();
                    return ;
                }
                if(session.equals("disconnected")) {
                    Toast.makeText(context, "인터넷 연결 후 이용해주세요", Toast.LENGTH_SHORT).show();
                    return ;
                }

                final View dialogView = (View)View.inflate(context, R.layout.record_layout, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                final AlertDialog dlg = builder.create();

                dlg.setView(dialogView);
                dlg.getWindow().setBackgroundDrawableResource(R.drawable.block);

                String result = null;
                try {
                    result = new RegisterTask().execute("selectList", session).get();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                System.out.println(result);
                String[] test = result.split("/");
                Arrays.sort(test);
                Spinner spinner = (Spinner)dialogView.findViewById(R.id.dateSpinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_layout, test);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setSelection(0, false);

                final TextView[] refNum = {(TextView)dialogView.findViewById(R.id.refNum_1), (TextView)dialogView.findViewById(R.id.refNum_2),
                        (TextView)dialogView.findViewById(R.id.refNum_3), (TextView)dialogView.findViewById(R.id.refNum_4),
                        (TextView)dialogView.findViewById(R.id.refNum_5), (TextView)dialogView.findViewById(R.id.refNum_6)};

                final TextView resultPrice = (TextView)dialogView.findViewById(R.id.lottoResult);

                parentLayout = (LinearLayout)dialogView.findViewById(R.id.parentLayout);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(position == 0) return ;
                        final String lottoNum = parent.getItemAtPosition(position).toString();

                        //서버 점검 예외처리 만들기
                        try {
                            String url = "https://www.dhlottery.co.kr/common.do?method=getLottoNumber&drwNo=" + lottoNum;

                            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    jsonObject = (JsonObject) JsonParser.parseString(response);

                                    for(int i = 0; i < refNum.length; i++) {
                                        refNum[i].setText(jsonObject.get("drwtNo" + (i+1)).getAsString());
                                        fillColor(refNum[i]);
                                    }

                                    String result = null;
                                    try {
                                        result = new RegisterTask().execute("selectRecord", session, lottoNum).get();
                                    } catch(Exception e) {
                                        e.printStackTrace();
                                    }
                                    System.out.println(result);
                                    final String[] resultArray = result.split("/");

                                    try {

                                        new Thread() {
                                            @Override
                                            public void run() {
                                                parentLayout.removeAllViewsInLayout();
                                                for(int i = 0; i < resultArray.length; i += 6) {
                                                    count = 0;
                                                    LinearLayout layout = makeLayout(parentLayout);
                                                    for(int j = i; j < i + 6; j++) {
                                                        TextView txtNums = makeText(resultArray[j], refNum[0]);
                                                        layout.addView(txtNums);
                                                        for(int k = 0; k < refNum.length; k++) {
                                                            if(txtNums.getText().toString().equals(refNum[k].getText().toString())) {
                                                                count ++;
                                                                fillColor(txtNums);
                                                            }
                                                        }
                                                    }
                                                    colorCount.add(count);
                                                }
                                            }
                                        }.run();
                                    } catch(Exception e) {
                                        e.printStackTrace();
                                    }

                                    String rank = null;
                                    for(int i = 0; i < colorCount.size(); i++) {
                                        switch(colorCount.get(i)) {
                                            case 3:
                                                rank = "5등";
                                                break;
                                            case 4:
                                                rank = "4등";
                                                break;
                                            case 5:
                                                rank = "3등";
                                                break;
                                            case 6:
                                                rank = "1등";
                                                break;
                                            default:
                                                rank = "낙첨";
                                                break;
                                        }
                                    }

                                    resultPrice.setText("1등 : " + jsonObject.get("firstWinamnt") + "원" + "\r\n" +
                                            "본인의 등수 : " + rank);
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            });

                            if(AppHelper.requestQueue == null) {
                                AppHelper.requestQueue = Volley.newRequestQueue(context);
                            }

                            request.setShouldCache(false);
                            AppHelper.requestQueue.add(request);
                        } catch(Exception e) {
                            Toast.makeText(context, "현재 로또 서버가 점검중입니다.", Toast.LENGTH_SHORT).show();
                            return ;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                Button btnBack = (Button)dialogView.findViewById(R.id.btnBackRecord);
                btnBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dlg.dismiss();
                    }
                });

                dlg.setCancelable(false);
                dlg.show();
            }
        });
    }

    protected void fillColor(TextView txt) {
        int drwNoInt = Integer.parseInt(txt.getText().toString());
        if(drwNoInt < 11) {
            txt.setBackgroundColor(Color.YELLOW);
        }
        else if(10 < drwNoInt && drwNoInt < 21) {
            txt.setBackgroundColor(Color.BLUE);
        }
        else if(20 < drwNoInt && drwNoInt < 31) {
            txt.setBackgroundColor(Color.RED);
        }
        else if(30 < drwNoInt && drwNoInt < 41) {
            txt.setBackgroundColor(Color.GRAY);
        }
        else if(40 < drwNoInt) {
            txt.setBackgroundColor(Color.LTGRAY);
        }
    }

    protected LinearLayout makeLayout(LinearLayout basedLayout) {
        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(basedLayout.getLayoutParams());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER);
        basedLayout.addView(layout);

        return layout;
    }

    protected TextView makeText(String msg, TextView basedTextView) {
        TextView txt = new TextView(context);
        txt.setGravity(Gravity.CENTER);
        txt.setTextSize(15);
        txt.setTypeface(basedTextView.getTypeface());
        txt.setTextColor(Color.BLACK);
        txt.setLayoutParams(basedTextView.getLayoutParams());
        txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        txt.setText(msg);

        return txt;
    }
}
