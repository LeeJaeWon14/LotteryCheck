package com.example.ghkdw.lotterycheck;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    String session = null;
    String ssaid = null;

    private DrawerLayout drawerLayout;

    ActionBarDrawerToggle drawerToggle;

    PagerAdapter adapter;
    ViewPager viewPager;
    Toolbar toolbar;
    NavigationView navigationView;

    LinearLayout logoImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check Permission
        permissionCheck();

        session = getIntent().getStringExtra("session");
        System.out.println("now session >> " + session);
        ssaid = getIntent().getStringExtra("ssaid");

        if(session.equals("no") || session.equals("disconnected")) {
            checkOnline(session);
        }

        viewPager = (ViewPager)findViewById(R.id.viewPager);

        adapter = new MyPagerAdapter(getSupportFragmentManager(), session);
        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(true, new DepthPageTransformer());

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            if(session.equals("disconnected"))
                getSupportActionBar().setTitle("네트워크에 연결해주십시오");
            else {
                getSupportActionBar().setTitle("현재 " + new CrollingTask().execute("recent").get() + "회차");
            }
        } catch(Exception e) {}
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.add);

        navigationView = (NavigationView)findViewById(R.id.navView);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);

        drawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                switch(item.getItemId()) {
                    case R.id.menu_join:
                        intent = new Intent(MainActivity.this, JoinActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.menu_login:
                        final View dialogView = (View)View.inflate(MainActivity.this, R.layout.login_layout, null);
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        AlertDialog dlg = builder.create();

                        dlg.setView(dialogView);
                        dlg.getWindow().setBackgroundDrawableResource(R.drawable.block);

                        final EditText edtEmail = (EditText)dialogView.findViewById(R.id.edtEmail_Login);
                        final EditText edtPassword = (EditText)dialogView.findViewById(R.id.edtPassword_Login);

                        ((Button)dialogView.findViewById(R.id.btnLogin)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String result = null;
                                try {
                                    result = new RegisterTask().execute("login", edtEmail.getText().toString(), edtPassword.getText().toString(), ssaid).get();
                                } catch(Exception e) {
                                    e.printStackTrace();
                                }

                                if(result.contains("success")) {
                                    Intent sIntent = new Intent(MainActivity.this, SplashActivity.class);
                                    startActivity(sIntent);
                                    Toast.makeText(MainActivity.this, "로그인 되었습니다", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(MainActivity.this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        dlg.show();

                        break;
                    case R.id.menu_logout:
                        String result = null;
                        try {
                            result = new RegisterTask().execute("logout", session).get();
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                        if(result.contains("success")) {
                            ActivityCompat.finishAffinity(MainActivity.this);
                            intent = new Intent(MainActivity.this, SplashActivity.class);
                            startActivity(intent);
                            Toast.makeText(MainActivity.this, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "로그아웃에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.menu_drop:
                        final View dialogViewForConfirm = (View)View.inflate(MainActivity.this, R.layout.default_dialog_layout, null);
                        AlertDialog.Builder builderForConfirm = new AlertDialog.Builder(MainActivity.this);
                        final AlertDialog dlgForConfirm = builderForConfirm.create();

                        dlgForConfirm.setView(dialogViewForConfirm);
                        dlgForConfirm.getWindow().setBackgroundDrawableResource(R.drawable.block);

                        ((TextView)dialogViewForConfirm.findViewById(R.id.txtDefault)).setText("정말 탈퇴하시겠습니까?");

                        ((Button)dialogViewForConfirm.findViewById(R.id.btnOk)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    RegisterTask task = new RegisterTask();
                                    String result = task.execute("drop", session).get();
                                    if(result.contains("success")) {
                                        Toast.makeText(MainActivity.this, "탈퇴되었습니다.", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(MainActivity.this, SplashActivity.class));
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(MainActivity.this, "실패하였습니다", Toast.LENGTH_SHORT).show();
                                    }
                                } catch(Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        ((Button)dialogViewForConfirm.findViewById(R.id.btnCancel)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dlgForConfirm.dismiss();
                            }
                        });

                        dlgForConfirm.show();
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        checkMember();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case IntentCode.PERMISSION_CODE:
                Toast.makeText(this, "권한이 허용되었습니다.", Toast.LENGTH_SHORT).show();
                break;

            case IntentCode.REQUEST_JOIN:
                session = data.getStringExtra("session");
                Toast.makeText(this, "회원가입 되었습니다.", Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
    }

    //권한 요청
    public void permissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                //권한 없음
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        IntentCode.PERMISSION_CODE);
            } else { /*권한 있음 */ }
        }
    }

    //Fragment 전환 애니메이션
    public class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0f);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1f);
                view.setTranslationX(0f);
                view.setScaleX(1f);
                view.setScaleY(1f);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0f);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void checkOnline(String session) {
        View dialogView = (View)View.inflate(MainActivity.this, R.layout.default_dialog_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final AlertDialog dlg = builder.create();

        dlg.setView(dialogView);
        dlg.getWindow().setBackgroundDrawableResource(R.drawable.block);

        TextView msg = (TextView)dialogView.findViewById(R.id.txtDefault);
        if(session.equals("no"))
            msg.setText("비회원은 스캔, 기록조회 등의 기능을 사용하실 수 없습니다.");
        else if(session.equals("disconnected"))
            msg.setText("네트워크에 연결되어있지 않으면 스캔, 기록조회 등의 기능을 사용하실 수 없습니다.");

        ((Button)dialogView.findViewById(R.id.btnCancel)).setVisibility(View.INVISIBLE);

        ((Button)dialogView.findViewById(R.id.btnOk)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });

        dlg.show();
    }

    public void checkMember() {
        View view = (View)View.inflate(MainActivity.this, R.layout.drawer_layout, null);

        TextView temp = (TextView)((NavigationView)drawerLayout.getChildAt(1)).getHeaderView(0).findViewById(R.id.memberName);
        logoImage = (LinearLayout)((NavigationView)drawerLayout.getChildAt(1)).getHeaderView(0).findViewById(R.id.logoImage);

        if(session.equals("no")) {
            temp.setText("비회원님");
            navigationView.inflateMenu(R.menu.non_login_drawer);
        }
        else if(session.equals("disconnected")) {
            temp.setText("오프라인");
            //navigationView.inflateMenu(R.menu.non_login_drawer);
        }
        else {
            temp.setText(session + "님");
            navigationView.inflateMenu(R.menu.drawer);
        }

        logoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View dialogView = (View)View.inflate(MainActivity.this, R.layout.default_dialog_layout, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final AlertDialog dlg  = builder.create();

                dlg.setView(dialogView);
                dlg.getWindow().setBackgroundDrawableResource(R.drawable.block);

                ((Button)dialogView.findViewById(R.id.btnCancel)).setVisibility(View.INVISIBLE);

                ((Button)dialogView.findViewById(R.id.btnOk)).setVisibility(View.INVISIBLE);

                TextView txt = (TextView)dialogView.findViewById(R.id.txtDefault);
                txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                txt.setText("Team 나로" + "\r\n" + "\r\n" + "\r\n" + "이재원" + "\r\n" + "\r\n" + "이종열" + "\r\n" + "\r\n" + "김대훈");

                dlg.show();
            }
        });
    }

    private long time= 0;
    @Override
    public void onBackPressed(){
        if(System.currentTimeMillis()-time>=2000){
            time=System.currentTimeMillis();
            Toast.makeText(getApplicationContext(),"뒤로 버튼을 한번 더 누르면 종료합니다.",Toast.LENGTH_SHORT).show();
        }else if(System.currentTimeMillis()-time<2000){
            this.finishAffinity();
        }
    }
}