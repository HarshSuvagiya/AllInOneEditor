package com.scorpion.allinoneeditor;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.infideap.drawerbehavior.Advance3DDrawerLayout;
import com.scorpion.allinoneeditor.audioeditor.Activity.AudioEditorMainActivity;
import com.scorpion.allinoneeditor.audioeditor.fragment.AudioEditorFragment;
import com.scorpion.allinoneeditor.videoeditor.activity.SelectVideoActivity;
import com.scorpion.allinoneeditor.videoeditor.activity.VideoEditorMainActivity;
import com.scorpion.allinoneeditor.videoeditor.activity.VideoToMp3;
import com.scorpion.allinoneeditor.videoeditor.fragment.VideoEditorFragment;
import com.scorpion.allinoneeditor.videoeditor.utils.Helper;
import com.scorpion.allinoneeditor.videoeditor.utils.Utility;
import com.scorpion.allinoneeditor.videoeditor.utils.Utils;

import java.io.File;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Advance3DDrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (Helper.check_permission(MainActivity.this)) {
            Helper.request_permission(MainActivity.this, 24);
        }
        AdHelper.LoadInter(MainActivity.this);
        try {
            if (getIntent().getExtras() != null) {
                Object value = getIntent().getExtras().get("link");
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse((String) value));
                startActivity(browserIntent);
            }
        } catch (Exception e) {
        }

        drawer = (Advance3DDrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawer.setViewScale(GravityCompat.START, 0.96f);
        drawer.setRadius(GravityCompat.START, 20);
        drawer.setViewElevation(GravityCompat.START, 8);
        drawer.setViewRotation(GravityCompat.START, 15);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, new MainFragment());
//        transaction.addToBackStack(null);
        transaction.commit();


    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            startActivity(new Intent(getApplicationContext(), ExitActivity.class));
            finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                break;
            case R.id.videoEditor:
                startActivity(new Intent(MainActivity.this, VideoEditorMainActivity.class));
                break;
            case R.id.audioEditor:
                startActivity(new Intent(MainActivity.this, AudioEditorMainActivity.class));
                break;
            case R.id.videotomp3:
                Utils.position = 8;
                callActivity();
                break;
//            case R.id.privacyPolicy:
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/video-editor-trim-reverse/home"));
//                startActivity(browserIntent);
//                break;
//            case R.id.more:
//                startActivity(new Intent(
//                        Intent.ACTION_VIEW,
//                        Uri.parse("https://play.google.com/store/apps/developer?id="+getString(R.string.moreapp))));
//                break;
            case R.id.rate:
//                try {
//                    startActivity(new Intent("android.intent.action.VIEW",
//                            Uri.parse("market://details?id=" + getPackageName())));
//                } catch (Exception e) {
//                    startActivity(new Intent("android.intent.action.VIEW",
//                            Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
//                }
//                break;
//            case R.id.share:
//                try {
//                    Intent intent = new Intent("android.intent.action.SEND");
//                    intent.setType("text/plain");
//                    intent.putExtra("android.intent.extra.SUBJECT", "Have a look at " +
//                            getString(R.string.app_name) + " app ");
//                    intent.putExtra("android.intent.extra.TEXT",
//                            "Hey Check out this new " +
//                                    getString(R.string.app_name) +
//                                    " App " + "- " + getString(R.string.app_name)
//                                    + " \nAvailable on Google play " + "store,"
//                                    + "You can also download it from \"https://play.google" + ".com/store/apps/details?id="
//                                    + getPackageName() + "\"");
//                    startActivity(Intent.createChooser(intent, "Share via"));
//                } catch (Exception e) {
//                    Toast.makeText(getApplicationContext(), "Something went wrong while sharing our app!.Sorry " +
//                            "for the inconvenience .Please try again", Toast.LENGTH_SHORT).show();
//                }
//                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void callActivity(){
        startActivity(new Intent(getApplicationContext(), SelectVideoActivity.class));
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Helper.makeFolder(MainActivity.this);
        } else {
            Toast.makeText(this, "Permission denied!!", Toast.LENGTH_SHORT).show();
        }
        return;
    }

}
