package com.lorytech.WhoIsTheUndercover;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.blankj.utilcode.util.Utils;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class StartGameActivity extends AppCompatActivity {

    private SVProgressHUD mSVProgressHUD;

    private int isBlankCard, playerNum, clickedNum, ranUndercover, ranBlank, undercoverNum, blankNum, totalNum, ranNextSpeaker;

    private ListView userlist;

    private WordsDao wordsDao;

    private List<Map<String, Object>> userdata;

    private TextView ranNum;

    private LinearLayout linearLayout;

    private Button btn_forgetWord, btn_continueGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startgame);
        Logger.addLogAdapter(new AndroidLogAdapter());
        Bundle extras = getIntent().getExtras();
        userlist = (ListView) findViewById(R.id.userList);
        linearLayout = (LinearLayout) findViewById(R.id.tips);
        ranNum = (TextView) findViewById(R.id.ranNum);
        btn_forgetWord = (Button) findViewById(R.id.btn_forgetWord);
        btn_continueGame = (Button) findViewById(R.id.btn_continueGame);
        isBlankCard = extras.getInt("isBlankCard");
        playerNum = extras.getInt("playerNum");
        clickedNum = 0;
        undercoverNum = 1;
        totalNum = playerNum;
        blankNum = isBlankCard;
        mSVProgressHUD = new SVProgressHUD(this);
        initUI();
        Utils.init(getApplication());
    }

    private void initUI(){

        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(this, "words-db", null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        wordsDao = daoSession.getWordsDao();
        List<Words> wordsList = wordsDao.queryBuilder().where(WordsDao.Properties.Id.notEq(-1))
                                .orderAsc(WordsDao.Properties.Id).limit(99999).build().list();

        Random ran1 = new Random();
        Words words = wordsList.get(ran1.nextInt(wordsList.size()));
        userdata = new ArrayList<>();
        for (int j = 0; j < playerNum; j++) {
            HashMap<String, Object> map = new HashMap<>();

            map.put("userNo", j+1);
            map.put("userWord", words.getCommon());
            userdata.add(map);
        }

        Random ran2 = new Random();
        ranUndercover = ran2.nextInt(playerNum);
        userdata.get(ranUndercover).put("userWord", words.getUndercover());
        ranBlank = -1;
        if (blankNum == 1) {
            ranBlank = (ranUndercover + 1 + ran2.nextInt(playerNum-1)) % playerNum;
            userdata.get(ranBlank).put("userWord", " ");
        }

        UserListAdapter userListAdapter = new UserListAdapter(getApplicationContext(), userdata, mListener);
        userlist.setAdapter(userListAdapter);
        ListAdapterUtil.MeasureHeight(userlist);

        btn_continueGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != userdata.get(0).get("gameover") && userdata.get(0).get("gameover").toString().equals("true")) {
                    Logger.d(userdata);
                    Intent intent = new Intent(StartGameActivity.this, StartGameActivity.class);
                    Bundle bundle = new Bundle();

                    bundle.putInt("playerNum", playerNum);
                    bundle.putInt("isBlankCard", isBlankCard);
                    intent.putExtras(bundle);

                    startActivity(intent);
                } else {
                    Logger.d(userdata);

                    String tips = "游戏还未结束，确定重新开始吗？";
                    new MaterialDialog.Builder(StartGameActivity.this).content(tips).positiveText("Yes")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    Intent intent = new Intent(StartGameActivity.this, StartGameActivity.class);
                                    Bundle bundle = new Bundle();

                                    bundle.putInt("playerNum", playerNum);
                                    bundle.putInt("isBlankCard", isBlankCard);
                                    intent.putExtras(bundle);

                                    startActivity(intent);
                                }
                            }).negativeText("No")
                            .show();
                }
            }
        });

        btn_forgetWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(StartGameActivity.this)
                        .title("查看词语")
                        .content("请输入玩家号码：")
                        .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                        .input("", "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                if (!("").equals(input.toString())){
                                    int forgetNum = Integer.parseInt(input.toString())-1;
                                    if(forgetNum >= 0 && forgetNum <= playerNum-1 ){

                                        String userWord = userdata.get(forgetNum).get("userWord").toString();
                                        mSVProgressHUD.showInfoWithStatus(userWord, SVProgressHUD.SVProgressHUDMaskType.Black);

                                    } else {
                                        mSVProgressHUD.showInfoWithStatus("输入有误！", SVProgressHUD.SVProgressHUDMaskType.Black);
                                        return;
                                    }
                                } else {
                                    mSVProgressHUD.showInfoWithStatus("输入为空！", SVProgressHUD.SVProgressHUDMaskType.Black);
                                    return;
                                }
                            }
                        }).show();
            }
        });
    }

    private UserListAdapter.MyClickListener mListener = new UserListAdapter.MyClickListener() {
        @Override
        public void myOnClick(final int position, View v) {
            clickedNum ++;
            if(clickedNum < playerNum){
                String userWord = userdata.get(position).get("userWord").toString();
                userdata.get(position).put("clicked", "true");
                UserListAdapter userListAdapter = new UserListAdapter(getApplicationContext(), userdata, mListener);
                userlist.setAdapter(userListAdapter);
                ListAdapterUtil.MeasureHeight(userlist);

                mSVProgressHUD.showInfoWithStatus(userWord, SVProgressHUD.SVProgressHUDMaskType.Black);
            } else {
                final Random ran3 = new Random();
                if(clickedNum == playerNum) {
                    userdata.get(position).put("clicked", "true");
                    for (int i = 0; i < playerNum; i++) {
                        userdata.get(i).put("out", "false");
                    }
                    String userWord = userdata.get(position).get("userWord").toString();
                    mSVProgressHUD.showInfoWithStatus(userWord, SVProgressHUD.SVProgressHUDMaskType.Black);
                    UserListAdapter userListAdapter = new UserListAdapter(getApplicationContext(), userdata, mListener);
                    userlist.setAdapter(userListAdapter);
                    userlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                            Logger.d("@@@@");
                            String userWord = userdata.get(position).get("userWord").toString();
                            mSVProgressHUD.showInfoWithStatus(userWord, SVProgressHUD.SVProgressHUDMaskType.Black);

                            return false;
                        }

                    });
                    ListAdapterUtil.MeasureHeight(userlist);
                    do {
                        ranNextSpeaker = ran3.nextInt(playerNum);
                    } while(userdata.get(ranNextSpeaker).get("out").toString().equals("true"));
                    ranNum.setText(String.valueOf(ranNextSpeaker+1));
                    linearLayout.setVisibility(View.VISIBLE);

                } else {

                    String tips = "确定淘汰" + String.valueOf(position + 1) + "号玩家？";
                    new MaterialDialog.Builder(StartGameActivity.this).content(tips).positiveText("Yes")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    if (position == ranUndercover) {
                                        totalNum --;
                                        undercoverNum --;

                                        if (totalNum < 3) {
                                            if (blankNum == 0) {
                                                //平民胜利
                                                userdata.get(position).put("isUndercover", "true");
                                                for (int i = 0; i < playerNum; i++) {
                                                    userdata.get(i).put("gameover", "true");
                                                }
                                                UserListAdapter userListAdapter = new UserListAdapter(getApplicationContext(), userdata, mListener);
                                                userlist.setAdapter(userListAdapter);
                                                ListAdapterUtil.MeasureHeight(userlist);
                                                mSVProgressHUD.showInfoWithStatus("游戏结束，平民胜利！", SVProgressHUD.SVProgressHUDMaskType.Black);
                                                return;
                                            } else {
                                                userdata.get(position).put("isUndercover", "true");
                                                if (blankNum == 1) {
                                                    userdata.get(ranBlank).put("isBlank", "true");
                                                }
                                                for (int i = 0; i < playerNum; i++) {
                                                    userdata.get(i).put("gameover", "true");
                                                }
                                                UserListAdapter userListAdapter = new UserListAdapter(getApplicationContext(), userdata, mListener);
                                                userlist.setAdapter(userListAdapter);
                                                ListAdapterUtil.MeasureHeight(userlist);
                                                mSVProgressHUD.showInfoWithStatus("游戏结束，白板胜利！", SVProgressHUD.SVProgressHUDMaskType.Black);
                                                return;
                                            }
                                        } else {
                                            if (blankNum == 0) {
                                                userdata.get(position).put("isUndercover", "true");
                                                for (int i = 0; i < playerNum; i++) {
                                                    userdata.get(i).put("gameover", "true");
                                                }
                                                UserListAdapter userListAdapter = new UserListAdapter(getApplicationContext(), userdata, mListener);
                                                userlist.setAdapter(userListAdapter);
                                                ListAdapterUtil.MeasureHeight(userlist);
                                                mSVProgressHUD.showInfoWithStatus("游戏结束，平民胜利！", SVProgressHUD.SVProgressHUDMaskType.Black);
                                                return;
                                            } else {
                                                userdata.get(position).put("isUndercover", "true");
                                                userdata.get(position).put("out", "true");
                                                UserListAdapter userListAdapter = new UserListAdapter(getApplicationContext(), userdata, mListener);
                                                userlist.setAdapter(userListAdapter);
                                                ListAdapterUtil.MeasureHeight(userlist);
                                                do {
                                                    ranNextSpeaker = ran3.nextInt(playerNum);
                                                    Logger.d(userdata.get(ranNextSpeaker).get("out").toString());

                                                } while(userdata.get(ranNextSpeaker).get("out").toString().equals("true"));
                                                ranNum.setText(String.valueOf(ranNextSpeaker+1));
                                                linearLayout.setVisibility(View.VISIBLE);
                                            }
                                        }

                                    } else if (position == ranBlank) {
                                        if (blankNum == 1) {
                                            totalNum--;
                                            blankNum--;
                                            if (totalNum < 3) {
                                                if (undercoverNum == 0) {
                                                    //平民胜利
                                                    userdata.get(position).put("isBlank", "true");
                                                    for (int i = 0; i < playerNum; i++) {
                                                        userdata.get(i).put("gameover", "true");
                                                    }
                                                    UserListAdapter userListAdapter = new UserListAdapter(getApplicationContext(), userdata, mListener);
                                                    userlist.setAdapter(userListAdapter);
                                                    ListAdapterUtil.MeasureHeight(userlist);
                                                    mSVProgressHUD.showInfoWithStatus("游戏结束，平民胜利！", SVProgressHUD.SVProgressHUDMaskType.Black);
                                                    return;
                                                } else {
                                                    userdata.get(position).put("isBlank", "true");
                                                    userdata.get(ranUndercover).put("isUndercover", "true");
                                                    for (int i = 0; i < playerNum; i++) {
                                                        userdata.get(i).put("gameover", "true");
                                                    }
                                                    UserListAdapter userListAdapter = new UserListAdapter(getApplicationContext(), userdata, mListener);
                                                    userlist.setAdapter(userListAdapter);
                                                    ListAdapterUtil.MeasureHeight(userlist);
                                                    mSVProgressHUD.showInfoWithStatus("游戏结束，卧底胜利！", SVProgressHUD.SVProgressHUDMaskType.Black);
                                                    return;
                                                }
                                            } else {
                                                userdata.get(position).put("isBlank", "true");
                                                userdata.get(position).put("out", "true");
                                                UserListAdapter userListAdapter = new UserListAdapter(getApplicationContext(), userdata, mListener);
                                                userlist.setAdapter(userListAdapter);
                                                ListAdapterUtil.MeasureHeight(userlist);
                                                do {
                                                    ranNextSpeaker = ran3.nextInt(playerNum);
                                                    Logger.d(userdata.get(ranNextSpeaker).get("out").toString());

                                                } while(userdata.get(ranNextSpeaker).get("out").toString().equals("true"));
                                                ranNum.setText(String.valueOf(ranNextSpeaker+1));
                                                linearLayout.setVisibility(View.VISIBLE);
                                            }
                                        } else {
                                            userdata.get(position).put("out", "true");
                                            do {
                                                ranNextSpeaker = ran3.nextInt(playerNum);
                                                Logger.d(userdata.get(ranNextSpeaker).get("out").toString());

                                            } while(userdata.get(ranNextSpeaker).get("out").toString().equals("true"));
                                            Logger.d("随机之后：" + ranNextSpeaker);
                                            ranNum.setText(String.valueOf(ranNextSpeaker+1));
                                            linearLayout.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        totalNum --;
                                        if (totalNum < 3) {
                                            if (undercoverNum != 0) {
                                                //卧底胜利
                                                for (int i = 0; i < playerNum; i++) {
                                                    userdata.get(i).put("gameover", "true");
                                                }
                                                userdata.get(ranUndercover).put("isUndercover", "true");
                                                if (blankNum == 1) {
                                                    userdata.get(ranBlank).put("isBlank", "true");
                                                }
                                                UserListAdapter userListAdapter = new UserListAdapter(getApplicationContext(), userdata, mListener);
                                                userlist.setAdapter(userListAdapter);
                                                ListAdapterUtil.MeasureHeight(userlist);
                                                mSVProgressHUD.showInfoWithStatus("游戏结束，卧底胜利！", SVProgressHUD.SVProgressHUDMaskType.Black);
                                                return;
                                            }
                                            if (blankNum != 0) {
                                                //白板胜利
                                                for (int i = 0; i < playerNum; i++) {
                                                    userdata.get(i).put("gameover", "true");
                                                }
                                                userdata.get(ranUndercover).put("isUndercover", "true");
                                                if (blankNum == 1) {
                                                    userdata.get(ranBlank).put("isBlank", "true");
                                                }
                                                UserListAdapter userListAdapter = new UserListAdapter(getApplicationContext(), userdata, mListener);
                                                userlist.setAdapter(userListAdapter);
                                                ListAdapterUtil.MeasureHeight(userlist);
                                                mSVProgressHUD.showInfoWithStatus("游戏结束，白板胜利！", SVProgressHUD.SVProgressHUDMaskType.Black);
                                                return;
                                            }
                                        } else {
                                            userdata.get(position).put("out", "true");
                                            do {
                                                ranNextSpeaker = ran3.nextInt(playerNum);
                                                Logger.d(userdata.get(ranNextSpeaker).get("out").toString());
                                            } while(userdata.get(ranNextSpeaker).get("out").toString().equals("true"));
                                            ranNum.setText(String.valueOf(ranNextSpeaker+1));
                                            linearLayout.setVisibility(View.VISIBLE);
                                        }
                                    }
                                    UserListAdapter userListAdapter = new UserListAdapter(getApplicationContext(), userdata, mListener);
                                    userlist.setAdapter(userListAdapter);
                                    ListAdapterUtil.MeasureHeight(userlist);
                                }
                            }).negativeText("No")
                            .show();
                }
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(StartGameActivity.this, MainActivity.class);
        Bundle bundle = new Bundle();

        bundle.putInt("playerNum", playerNum);
        bundle.putInt("isBlankCard", isBlankCard);
        intent.putExtras(bundle);

        startActivity(intent);
    }
}
