package com.example.ghkdw.lotterycheck;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by ghkdw on 2020-10-14.
 */

public class TestFragmnet extends Fragment {
    private String session;
    View view;
    Context context;
    ImageView imageView;
    LotteryThread thread;
    TextView label;
    LinearLayout refLayout;

    public static TestFragmnet newInstance(int page, String title, String session) {
        TestFragmnet fragment = new TestFragmnet();
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
        imageView.setImageResource(R.drawable.noun_lottery_1501474);

        label = (TextView)view.findViewById(R.id.slideLabel);
        label.setText("모의추첨");

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final View dialogViewForTest = (View)View.inflate(context, R.layout.test_layout, null);
                AlertDialog.Builder builderForTest = new AlertDialog.Builder(context);
                final AlertDialog dlgForTest = builderForTest.create();

                dlgForTest.setView(dialogViewForTest);
                dlgForTest.getWindow().setBackgroundDrawableResource(R.drawable.block);

                /*Window window = dlgForTest.getWindow();
                window.setBackgroundDrawableResource(R.drawable.block);
                //dlgForTest.getWindow().setBackgroundDrawableResource(R.drawable.block);
                WindowManager.LayoutParams params = window.getAttributes();
                params.windowAnimations = R.style.AnimationPopupStyle;
                window.setAttributes(params);*/



                final TextView testNum1 = (TextView)dialogViewForTest.findViewById(R.id.test_number_1st);
                TextView testNum2 = (TextView)dialogViewForTest.findViewById(R.id.test_number_2nd);
                TextView testNum3 = (TextView)dialogViewForTest.findViewById(R.id.test_number_3rd);
                TextView testNum4 = (TextView)dialogViewForTest.findViewById(R.id.test_number_4th);
                TextView testNum5 = (TextView)dialogViewForTest.findViewById(R.id.test_number_5th);
                TextView testNum6 = (TextView)dialogViewForTest.findViewById(R.id.test_number_6th);

                final TextView[] viewArray = {testNum1, testNum2, testNum3, testNum4, testNum5, testNum6};

                final Button btnRun = (Button)dialogViewForTest.findViewById(R.id.btnRun);
                final Button btnReset = (Button)dialogViewForTest.findViewById(R.id.btnCancelTest);
                final Button btnTestSave = (Button)dialogViewForTest.findViewById(R.id.btnTestSave);

                final LinearLayout basedLayout = (LinearLayout)dialogViewForTest.findViewById(R.id.layoutTestSave);
                final LinearLayout testLine = (LinearLayout)dialogViewForTest.findViewById(R.id.testLine);

                btnRun.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(btnRun.getText().toString().equals("추첨")) {
                            thread = new LotteryThread();
                            thread.set(viewArray);
                            thread.start();
                            btnRun.setText("멈춤");
                        }
                        else if(btnRun.getText().toString().equals("멈춤")) {
                            thread.exit();
                            btnRun.setText("추첨");
                        }
                    }
                });

                btnReset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(thread != null)
                            thread.exit();

                        dlgForTest.dismiss();
                    }
                });

                btnTestSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(testNum1.getText().toString().equals("1번")) {
                            Toast.makeText(context, "추첨 실행 후 저장해주세요!", Toast.LENGTH_SHORT).show();
                            return ;
                        }
                        else {
                            if(basedLayout.getChildCount() == 5) {
                                ArrayList<String> lottNumList = new ArrayList<String>();
                                for(int i = 0; i < basedLayout.getChildCount(); i++) {
                                    LinearLayout parentLayout = (LinearLayout)basedLayout.getChildAt(i);
                                    for(int j = 0; j < parentLayout.getChildCount(); j++) {
                                        lottNumList.add(((TextView)parentLayout.getChildAt(j)).getText().toString());
                                    }
                                }

                                new Thread() {
                                    @Override
                                    public void run() {
                                        basedLayout.removeAllViewsInLayout();
                                        btnTestSave.setText("기록");
                                    }
                                }.run();
                            }
                            else {
                                testLine.setVisibility(View.VISIBLE);
                                ArrayList<Integer> tempList = new ArrayList<Integer>();
                                for(int i = 0; i < viewArray.length; i++) {
                                    if(viewArray[i].getText().toString().contains("번")) {
                                        Toast.makeText(context, "잠시후 시도해주세요.", Toast.LENGTH_SHORT).show();
                                        return ;
                                    }
                                    tempList.add(Integer.parseInt(viewArray[i].getText().toString()));
                                }
                                Collections.sort(tempList);
                                LinearLayout createLayout = makeLayout(basedLayout);
                                for(int i = 0; i < viewArray.length; i++) {
                                    TextView addText = makeText(tempList.get(i).toString(), viewArray[i]);
                                    createLayout.addView(addText);
                                }
                                if(basedLayout.getChildCount() == 5)
                                    btnTestSave.setText("리셋");
                            }
                        }
                    }
                });

                dlgForTest.setCancelable(false);
                dlgForTest.show();
            }
        });
    }

    protected LinearLayout makeLayout(final LinearLayout basedLayout) {
        final LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(basedLayout.getLayoutParams());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER);
        basedLayout.addView(layout);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i < basedLayout.getChildCount(); i++) {
                    ((LinearLayout)basedLayout.getChildAt(i)).setBackgroundColor(Color.WHITE);
                }
                layout.setBackgroundColor(getResources().getColor(R.color.logo_color));
            }
        });

        layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ColorDrawable cd = (ColorDrawable)v.getBackground();
                try {
                    if (cd.getColor() != Color.WHITE) {
                        final View dialogView = (View) View.inflate(context, R.layout.default_dialog_layout, null);
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        final AlertDialog dlg = builder.create();

                        dlg.setView(dialogView);
                        dlg.getWindow().setBackgroundDrawableResource(R.drawable.block);

                        refLayout = (LinearLayout) v;

                        ((TextView) dialogView.findViewById(R.id.txtDefault)).setVisibility(View.INVISIBLE);

                        final LinearLayout basedLayout = (LinearLayout) dialogView.findViewById(R.id.layoutTestModify);

                        ArrayList<String> textList = new ArrayList<String>();
                        for (int i = 0; i < refLayout.getChildCount(); i++) {
                            TextView txt = (TextView) refLayout.getChildAt(i);
                            textList.add(txt.getText().toString());
                        }

                        LinearLayout createLayout = makeLayout(basedLayout);
                        createLayout.setOnClickListener(null);
                        createLayout.setOnLongClickListener(null);
                        for (int i = 0; i < 6; i++) {
                            TextView addTextView = makeText(textList.get(i), (TextView) refLayout.getChildAt(i));
                            addTextView.setTextSize(23);
                            addTextView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final View dialogView = (View)View.inflate(context, R.layout.modify_layout, null);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    final AlertDialog dlg = builder.create();

                                    dlg.setView(dialogView);
                                    dlg.getWindow().setBackgroundDrawableResource(R.drawable.block);

                                    final TextView refView = (TextView)v;

                                    final EditText modifyEdit = (EditText)dialogView.findViewById(R.id.mdofiy_edit);
                                    final String value = refView.getText().toString();
                                    modifyEdit.setHint(value);



                                    final InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                                    ((Button)dialogView.findViewById(R.id.btnOkModify)).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if(modifyEdit.getText().toString().equals("")) {
                                                Toast.makeText(context, "수정할 값을 입력해주세요", Toast.LENGTH_SHORT).show();
                                                return ;
                                            }
                                            refView.setText(modifyEdit.getText().toString());
                                            imm.hideSoftInputFromWindow(modifyEdit.getWindowToken(), 0);

                                            for(int i = 0; i < refLayout.getChildCount(); i++) {
                                                TextView tempText = (TextView)refLayout.getChildAt(i);
                                                if(tempText.getText().equals(value)) {
                                                    tempText.setText(refView.getText().toString());
                                                }
                                            }

                                            dlg.dismiss();
                                        }
                                    });

                                    dlg.setCanceledOnTouchOutside(false);
                                    dlg.show();
                                }
                            });
                            createLayout.addView(addTextView);
                        }

                        ((Button) dialogView.findViewById(R.id.btnOk)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dlg.dismiss();
                            }
                        });

                        final TextView[] refText = new TextView[6];
                        for (int i = 0; i < refText.length; i++) {
                            refText[i] = (TextView) refLayout.getChildAt(i);
                        }

                        final Button removeBtn = (Button) dialogView.findViewById(R.id.btnCancel);
                        removeBtn.setText("삭제");
                        removeBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LinearLayout refParentLayout = (LinearLayout) refLayout.getParent();
                                refParentLayout.removeView(refLayout);
                                dlg.dismiss();
                            }
                        });

                        dlg.setCanceledOnTouchOutside(false);
                        dlg.show();
                    }
                } catch(NullPointerException e) {
                    Toast.makeText(context, "선택해주세요", Toast.LENGTH_SHORT).show();
                    return false;
                }

                return false;
            }
        });

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

    class LotteryThread extends Thread {
        TextView[] views;
        boolean flag = true;
        Handler handler = new Handler();
        int i, j, k;
        ArrayList<Integer> tempIntegerList = new ArrayList<Integer>();


        public LotteryThread () {

        }

        public void set(TextView[] views) {
            this.views = views;
        }
        public void exit() {
            flag = false;
        }

        @Override
        public void run() {
            while(true) {
                if(!flag) {
                    break;
                }

                //final ArrayList<Integer> tempList = new ArrayList<Integer>();
                for(i = 0; i < views.length -1; i++) {
                    while(true) {
                        final int number = new Random().nextInt(99);
                        if(number > 0 && number < 46) {
                            boolean point = false;
                            for(j = 0; j < i; j++) {
                                if(i == 0) {
                                    break;
                                }

                                if(views[i].getText().toString().contains("번"))
                                    break;

                                if(Integer.parseInt(views[i].getText().toString()) == number) {
                                    point = true;
                                    break;
                                }
                            }
                            if(!point) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        views[i].setText(String.valueOf(number));
                                    }
                                });

                                break;
                            }
                        }
                    }
                }

                try {
                    sleep(10);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void captureView(View View) {
        View.buildDrawingCache();
        Bitmap captureView = View.getDrawingCache();
        FileOutputStream fos;

        String strFolderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/lottery_check";
        File folder = new File(strFolderPath);
        if(!folder.exists()) {
            folder.mkdirs();
        }

        String strFilePath = strFolderPath + "/" + System.currentTimeMillis() + ".png";
        File fileCacheItem = new File(strFilePath);

        try {
            fos = new FileOutputStream(fileCacheItem);
            captureView.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}