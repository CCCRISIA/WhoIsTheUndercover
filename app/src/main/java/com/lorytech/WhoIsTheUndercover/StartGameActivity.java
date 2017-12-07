package com.lorytech.WhoIsTheUndercover;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class StartGameActivity extends AppCompatActivity {

    private SVProgressHUD mSVProgressHUD;

    private int playerNum, clickedNum, ranUndercover, ranBlank, undercoverNum, blankNum, totalNum, ranNextSpeaker;

    private ListView userlist;

    private WordsDao wordsDao;

    private List<Map<String, Object>> userdata;

    private TextView ranNum;

    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startgame);
        Logger.addLogAdapter(new AndroidLogAdapter());
        Bundle extras = getIntent().getExtras();
        userlist = (ListView) findViewById(R.id.userList);
        linearLayout = (LinearLayout) findViewById(R.id.tips);
        ranNum = (TextView) findViewById(R.id.ranNum);
        blankNum = extras.getInt("isBlankCard");
        playerNum = extras.getInt("playerNum");
        clickedNum = 0;
        undercoverNum = 1;
        totalNum = playerNum;
        mSVProgressHUD = new SVProgressHUD(this);
        initUI();
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

//                Logger.d("clickedNum:" + clickedNum + ",position:" + position);
//                Logger.d(userdata);
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
//                    Logger.d("clickedNum:" + clickedNum + ",position:" + position);
//                    Logger.d(userdata);
                    UserListAdapter userListAdapter = new UserListAdapter(getApplicationContext(), userdata, mListener);
                    userlist.setAdapter(userListAdapter);
                    do {
                        ranNextSpeaker = ran3.nextInt(playerNum);
                        Logger.d(userdata.get(ranNextSpeaker).get("out").toString());

                    } while(userdata.get(ranNextSpeaker).get("out").toString().equals("true"));
                    ranNum.setText(String.valueOf(ranNextSpeaker+1));
                    linearLayout.setVisibility(View.VISIBLE);

                } else {
                    new MaterialDialog.Builder(StartGameActivity.this)
                            .content("确定淘汰该玩家？")
                            .positiveText("Yes")
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
                                                mSVProgressHUD.showInfoWithStatus("游戏结束，平民胜利！", SVProgressHUD.SVProgressHUDMaskType.Black);
                                                return;
                                            } else {
                                                userdata.get(position).put("isUndercover", "true");
                                                userdata.get(position).put("out", "true");
                                                UserListAdapter userListAdapter = new UserListAdapter(getApplicationContext(), userdata, mListener);
                                                userlist.setAdapter(userListAdapter);
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
                                                    mSVProgressHUD.showInfoWithStatus("游戏结束，卧底胜利！", SVProgressHUD.SVProgressHUDMaskType.Black);
                                                    return;
                                                }
                                            } else {
                                                userdata.get(position).put("isBlank", "true");
                                                userdata.get(position).put("out", "true");
                                                UserListAdapter userListAdapter = new UserListAdapter(getApplicationContext(), userdata, mListener);
                                                userlist.setAdapter(userListAdapter);
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

                                }
                            })
                            .negativeText("No")
                            .show();

                }
            }
        }
    };
}
