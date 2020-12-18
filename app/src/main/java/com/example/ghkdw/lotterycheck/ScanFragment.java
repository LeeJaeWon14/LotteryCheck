package com.example.ghkdw.lotterycheck;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ghkdw on 2020-10-14.
 */

public class ScanFragment extends Fragment {
    private String session;
    View view;
    ImageView imageView;
    Context context;
    TextView label;



    public static ScanFragment newInstance(int page, String title, String session) {
        ScanFragment fragment = new ScanFragment();
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
        imageView.setImageResource(R.drawable.noun_camera_3564682);

        label = (TextView)view.findViewById(R.id.slideLabel);
        label.setText("스캔");

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
                Bundle bundle = new Bundle();
                bundle.putString("session", session);
                Intent intent = new Intent(context, ScanActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
