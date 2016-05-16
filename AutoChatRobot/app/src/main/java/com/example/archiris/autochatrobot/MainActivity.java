package com.example.archiris.autochatrobot;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import turing.os.http.core.ErrorMessage;
import turing.os.http.core.HttpConnectionListener;
import turing.os.http.core.RequestResult;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.turing.androidsdk.InitListener;
import com.turing.androidsdk.SDKInit;
import com.turing.androidsdk.SDKInitBuilder;
import com.turing.androidsdk.TuringApiManager;
import com.turing.androidsdk.asr.VoiceRecognizeManager;
import com.turing.androidsdk.asr.VoiceRecognizeListener;
import com.turing.androidsdk.tts.TTSListener;
import com.turing.androidsdk.tts.TTSManager;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();
    private TTSManager ttsManager;
    private VoiceRecognizeManager recognizerManager;
    private TuringApiManager mTuringApiManager;
    private TextView mStatus;

    // user input his chat content
    public final int CHAT_START = 0;

    // robot reply
    public final int CHAT_RESULT = 1;
    /**
     * 申请的turing的apikey
     **/
    private final String TURING_APIKEY = "d975f8141aa550cea27b7f48dd50c48d";
    /**
     * 申请的secret
     **/
    private final String TURING_SECRET = "4145a1cb5f92901b";
    /**
     * 填写一个任意的标示，没有具体要求，，但一定要写，
     **/
    private final String UNIQUEID = "131313131";

    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHAT_RESULT:
                    // add textview
                    break;
                case CHAT_START:
                    mStatus.setText((String) msg.obj);
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mStatus = (TextView) findViewById(R.id.tvstatus);

        init();
    }

    private void init() {
        // turingSDK初始化
        SDKInitBuilder builder = new SDKInitBuilder(this)
                .setSecret(TURING_SECRET).setTuringKey(TURING_APIKEY).setUniqueId(UNIQUEID);
        SDKInit.init(builder, new InitListener() {
            @Override
            public void onFail(String error) {
                Log.d(TAG, error);
            }

            @Override
            public void onComplete() {
                // 获取userid成功后，才可以请求Turing服务器，需要请求必须在此回调成功，才可正确请求
                mTuringApiManager = new TuringApiManager(MainActivity.this);
                mTuringApiManager.setHttpListener(myHttpConnectionListener);
            }
        });
    }

    HttpConnectionListener myHttpConnectionListener = new HttpConnectionListener() {
        @Override
        public void onError(ErrorMessage errorMessage) {
            Log.d(TAG, errorMessage.getMessage());
        }

        @Override
        public void onSuccess(RequestResult result) {
            if (result != null) {
                try {
                    Log.d(TAG, result.getContent().toString());
                    JSONObject result_obj = new JSONObject(result.getContent()
                            .toString());
                    if (result_obj.has("text")) {
                        Log.d(TAG, result_obj.get("text").toString());
                        myHandler.obtainMessage(CHAT_RESULT,
                                result_obj.get("text")).sendToTarget();
                    }
                } catch (JSONException e) {
                    Log.d(TAG, "JSONException:" + e.getMessage());
                }
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
