package com.example.ghkdw.lotterycheck;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ghkdw on 2020-09-05.
 */

public class RegisterTask extends AsyncTask<String, Void, String> {
    public static String ip = "192.168.43.122:8085";
    String sendMsg, recieveMsg;


    @Override
    protected String doInBackground(String... strings) {
        try {
            String str = null;

            URL url = new URL("http://" + ip + "/LotteryCheck/LotteryCheckConnect.jsp");

            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("POST");
            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(), "utf-8");

            for(int i = 0; i < strings.length; i++) {
                if(strings[i].equals("join")) {
                    sendMsg = "type=" + strings[0] + "&ssaid=" + strings[1] + "&id=" + strings[2] + "&pw=" + strings[3];
                }

                if(strings[i].equals("login")) {
                    sendMsg = "type=" + strings[0] + "&id=" + strings[1] + "&pw=" + strings[2] + "&ssaid=" + strings[3];
                    System.out.println(" >> " + sendMsg);
                }

                if(strings[i].equals("logout")) {
                    sendMsg = "type=" + strings[0] + "&id=" + strings[1];
                }

                if(strings[i].equals("auth")) {
                    sendMsg = "type=" + strings[0] + "&ssaid=" + strings[1];
                }

                if(strings[i].equals("drop")) {
                    sendMsg = "type=" + strings[0] + "&id=" + strings[1];
                }

                if(strings[i].equals("testSave")) {
                    StringBuffer tempBuf = new StringBuffer();
                    tempBuf.append("type=" + strings[0] + "&id=" + strings[1]);
                    for(int j = 2; j < strings.length; j++) {
                        tempBuf.append("&lottoNum"+i + "=" + strings[j]);
                    }
                    sendMsg = tempBuf.toString();
                    System.out.println(" >> " + sendMsg);
                }

                if(strings[i].equals("scanSave")) {
                    StringBuffer tempBuf = new StringBuffer();
                    tempBuf.append("type=" + strings[0] + "&id=" + strings[1]);
                    for(int j = 2; j < strings.length -1; j++) {
                        tempBuf.append("&lottoNum"+(j-2) + "=" + strings[j]);
                    }
                    tempBuf.append("&count=" + strings[strings.length -1]);
                    sendMsg = tempBuf.toString();
                }

                if(strings[i].equals("selectList")) {
                    sendMsg = "type=" + strings[0] + "&id=" + strings[1];
                }

                if(strings[i].equals("selectRecord")) {
                    sendMsg = "type=" + strings[0] + "&id=" + strings[1] + "&count=" + strings[2];
                    System.out.println(" >> " + sendMsg);
                }
            }

            osw.write(sendMsg);
            osw.flush();

            if(conn.getResponseCode() == conn.HTTP_OK) {
                InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "utf-8");
                BufferedReader reader = new BufferedReader(tmp);
                StringBuffer buffer = new StringBuffer();

                while((str = reader.readLine()) != null) {
                    if(str.contains("<") || str.contains(">"))
                        continue;
                    else
                        buffer.append(str);
                }
                recieveMsg = buffer.toString();
            } else {
                Log.i(" connect fail", "통신 실패");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        return recieveMsg;
    }
}