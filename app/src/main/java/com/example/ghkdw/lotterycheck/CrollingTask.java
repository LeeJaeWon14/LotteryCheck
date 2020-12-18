package com.example.ghkdw.lotterycheck;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by ghkdw on 2020-10-25.
 */

public class CrollingTask extends AsyncTask<String, Void, String> {
    Document doc;
    Elements contents;
    @Override
    protected String doInBackground(String... strings) {
        if(strings[0].equals("recent")) {
            try {
                System.out.println(" >> Recent Croll Start");
                doc = Jsoup.connect("https://dhlottery.co.kr/common.do?method=main").get();
                contents = doc.select("#lottoDrwNo");
                System.out.println(" >> Contents : " + contents.text());
            } catch(Exception e) {
                e.printStackTrace();
            }

            return contents.text();
        }
        else if(strings[0].equals("scan")) {
            try {
                System.out.println(" >> Crolling Start");
                String[] tempArr = strings[1].split("v");
                String url = "https://m.dhlottery.co.kr/qr.do?method=winQr&v" + tempArr[1];
                System.out.println(" >> Url : " + url);
                doc = Jsoup.connect(url).get();
                contents = doc.select(".clr");

                String[] nums = contents.text().split(" ");
                StringBuffer sBuffer = new StringBuffer();
                for(int i = 7; i < nums.length; i++) {
                    if(i == 7) {
                        sBuffer.append(nums[i]);
                        continue;
                    }
                    sBuffer.append("/" + nums[i]);
                }

                return sBuffer.toString();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        else if(strings[0].equals("getDate")) {
            try {
                String[] tempArr = strings[1].split("v");
                String url = "https://m.dhlottery.co.kr/qr.do?method=winQr&v" + tempArr[1];
                doc = Jsoup.connect(url).get();
                System.out.println(" >> Url : " + url);
                contents = doc.select(".key_clr1");

                System.out.println("key_clr1 >> " + contents.toString());

                String[] split_1 = contents.toString().split("제");
                String[] split_2 = String.valueOf(split_1[1]).split("회");

                return split_2[0];
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
