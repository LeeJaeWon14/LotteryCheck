package com.example.ghkdw.lotterycheck;

import android.app.AlertDialog;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class JoinActivity extends AppCompatActivity {
    EditText email, password;
    Button join, back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        email = (EditText)findViewById(R.id.edtEmail);
        password = (EditText)findViewById(R.id.edtPassword);
        join = (Button)findViewById(R.id.btnJoin);
        back = (Button)findViewById(R.id.btnBack);

        final String ssaid = Settings.Secure.getString(JoinActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    RegisterTask task = new RegisterTask();
                    String result = task.execute("join", ssaid, email.getText().toString(), password.getText().toString()).get();
                    if(result.contains("success")) {
                        View dialogView = (View)View.inflate(JoinActivity.this, R.layout.default_dialog_layout, null);
                        AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                        final AlertDialog dlg = builder.create();

                        dlg.setView(dialogView);
                        dlg.getWindow().setBackgroundDrawableResource(R.drawable.block);

                        TextView msg = (TextView)dialogView.findViewById(R.id.txtDefault);
                        msg.setText("회원가입을 환영합니다!");

                        ((Button)dialogView.findViewById(R.id.btnCancel)).setVisibility(View.INVISIBLE);

                        ((Button)dialogView.findViewById(R.id.btnOk)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(JoinActivity.this, MainActivity.class);
                                intent.putExtra("session", email.getText().toString());
                                startActivity(intent);
                                finish();
                            }
                        });

                        dlg.setCanceledOnTouchOutside(false);
                        dlg.show();
                    }
                    else if(result.contains("exists")) {
                        Toast.makeText(JoinActivity.this, "이미 존재하는 회원정보입니다.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(JoinActivity.this, "실패했습니다.\r\n 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
