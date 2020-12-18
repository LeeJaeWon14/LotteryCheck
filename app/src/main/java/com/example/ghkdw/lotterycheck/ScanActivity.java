package com.example.ghkdw.lotterycheck;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

/**
 * Created by ghkdw on 2020-10-19.
 */

public class ScanActivity extends AppCompatActivity {
    static String session = null;
    Bundle sessionBundle;

    Button secondCamera, btnQr, btnSave, btnBack;
    LinearLayout afterLayout, drwLayout, refDrwLayout;
    TextView refNum, drwNoText, drwDateText;

    protected String[] nums = null;

    JsonObject jsonObject;

    public static int count = 0;
    public ArrayList<Integer> colorCount = new ArrayList<Integer>();
    String[] drwNum = new String[6];

    private IntentIntegrator qrScan;
    String value;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_layout);

        sessionBundle = getIntent().getExtras();
        session = sessionBundle.getString("session");

        secondCamera = (Button)findViewById(R.id.btn2ndCamera);
        btnSave = (Button)findViewById(R.id.btnSave);

        afterLayout = (LinearLayout)findViewById(R.id.afterLayout);
        drwLayout = (LinearLayout)findViewById(R.id.drwLayout);
        refDrwLayout = (LinearLayout)findViewById(R.id.refDrwLayout);

        refNum = (TextView)findViewById(R.id.myLottoRefNum);
        btnBack = (Button)findViewById(R.id.btnBackAtScan);

        drwNoText = (TextView)findViewById(R.id.drwNoText);
        drwDateText = (TextView)findViewById(R.id.drwDateText);


        //Load on Tess-two
        //Tesseract();

        //Run QR scan
        qrClick();

        secondCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(ScanActivity.this, ScanActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("session", session);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("save enter >> " + session);
                try {
                    String result = new RegisterTask().execute("scanSave", session, nums[0], nums[1], nums[2], nums[3], nums[4], nums[5],
                            nums[6], nums[7], nums[8], nums[9], nums[10], nums[11], nums[12], nums[13], nums[14], nums[15], nums[16], nums[17],
                            nums[18], nums[19], nums[20], nums[21], nums[22], nums[23], nums[24], nums[25], nums[26], nums[27], nums[28],
                            nums[29], drwNoText.getText().toString().split("회")[0]).get();
                    System.out.println("Save Result :: " + result);
                } catch(Exception e) {
                    e.printStackTrace();
                }

                Toast.makeText(ScanActivity.this, "저장되었습니다!", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    String firstPrice = null;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result != null) {
            if(result.getContents() == null) {
                Log.i("QR", "no data");
            }
            else {
                try {
                    value = result.getContents();

                    if(!value.contains("lott")) {
                        Toast.makeText(this, "로또 용지의 QR코드를 스캔해주세요", Toast.LENGTH_SHORT).show();
                        return ;
                    }

                    String drwCount = new CrollingTask().execute("getDate", value).get();

                    drwNoText.setText(drwCount + "회차");

                    String url = "https://www.dhlottery.co.kr/common.do?method=getLottoNumber&drwNo=" + drwCount;

                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            jsonObject = (JsonObject) JsonParser.parseString(response);

                            drwDateText.setText(jsonObject.get("drwNoDate").getAsString());
                            for(int i = 1; i < 7; i++) {
                                drwNum[i-1] = jsonObject.get("drwtNo" + i).getAsString();
                            }

                            LinearLayout layout = makeLayout(refDrwLayout);
                            for(int i = 0; i < 6; i++) {
                                TextView drwTextView = makeText(drwNum[i], refNum);
                                layout.addView(drwTextView);
                                fillColor(drwTextView);
                            }
                            refDrwLayout.addView(makeLine());

                            firstPrice = jsonObject.get("firstWinamnt").getAsString();

                            try {
                                String sResult = new CrollingTask().execute("scan", value).get();
                                nums = sResult.split("/");

                                new Thread() {
                                    @Override
                                    public void run() {
                                        for(int i = 0; i < nums.length; i += 6) {
                                            count = 0;
                                            LinearLayout layout = makeLayout(afterLayout);
                                            for(int j = i; j < i + 6; j++) {
                                                TextView txtNums = makeText(nums[j], refNum);
                                                layout.addView(txtNums);
                                                for(int k = 0; k < drwNum.length; k++) {
                                                    if(txtNums.getText().toString().equals(drwNum[k])) {
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

                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    TextView drwResult = makeText("당첨 몇등", refNum);
                                    drwLayout.addView(drwResult);
                                    for(int i = 0; i < colorCount.size(); i++) {
                                        if(colorCount.get(i) < 3) {
                                            drwResult.setText("아쉽게도, 낙첨되셨습니다.");
                                        }
                                        else if(colorCount.get(i) == 3) {
                                            drwResult.setText("축하합니다!" + "\r\n" + "5등에 당첨되셨습니다.");
                                        }
                                        else if(colorCount.get(i) == 4) {
                                            drwResult.setText("축하합니다!" + "\r\n" + "4등에 당첨되셨습니다.");
                                        }
                                        else if(colorCount.get(i) == 5) {
                                            drwResult.setText("축하합니다!" + "\r\n" + "3등에 당첨되셨습니다.");
                                        }
                                        else if(colorCount.get(i) == 6) {
                                            drwResult.setText("축하합니다!" + "\r\n" + "1등에 당첨되셨습니다." + "\r\n" + "당첨금액은 " + firstPrice + "원입니다.");
                                        }
                                    }
                                }
                            });

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                    if(AppHelper.requestQueue == null) {
                        AppHelper.requestQueue = Volley.newRequestQueue(ScanActivity.this);
                    }

                    request.setShouldCache(false);
                    AppHelper.requestQueue.add(request);

                } catch(Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    protected void qrClick() {
        qrScan = new IntentIntegrator(ScanActivity.this);
        qrScan.setPrompt("QR코드를 맞춰주세요.");
        qrScan.setCaptureActivity(EmptyActivity.class);
        qrScan.initiateScan();
    }

    protected LinearLayout makeLayout(LinearLayout basedLayout) {
        LinearLayout layout = new LinearLayout(ScanActivity.this);
        layout.setLayoutParams(basedLayout.getLayoutParams());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER);
        basedLayout.addView(layout);

        return layout;
    }

    protected TextView makeText(String msg, TextView basedTextView) {
        TextView txt = new TextView(ScanActivity.this);
        txt.setGravity(Gravity.CENTER);
        txt.setTextSize(15);
        txt.setTypeface(basedTextView.getTypeface());
        txt.setTextColor(Color.BLACK);
        txt.setLayoutParams(basedTextView.getLayoutParams());
        txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        txt.setText(msg);

        return txt;
    }

    protected LinearLayout makeLine() {
        LinearLayout layout = new LinearLayout(ScanActivity.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, 3);
        layout.setBackgroundColor(getResources().getColor(R.color.logo_color));

        return layout;
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
}