package com.lorytech.WhoIsTheUndercover;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.bigkoo.svprogresshud.SVProgressHUD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class StartGameActivity extends AppCompatActivity {

    private SVProgressHUD mSVProgressHUD;

    private int playerNum, clickedNum, ranUndercover, ranBlank, undercoverNum, blankNum, totalNum;

    private ListView userlist;

    private WordsDao wordsDao;

    private List<Map<String, Object>> userdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startgame);
        Bundle extras = getIntent().getExtras();
        totalNum = playerNum;
        userlist = (ListView) findViewById(R.id.userList);
        blankNum = extras.getInt("isBlankCard");
        playerNum = extras.getInt("playerNum");
        clickedNum = 0;
        undercoverNum = 1;
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
//        for (int j=0; j< wordsList.size(); j++){
//            Log.d("wordsList", "平民：" + wordsList.get(j).getCommon() + "; 卧底：" + wordsList.get(j).getUndercover());
//        }

        Random ran1 = new Random();
        Words words = wordsList.get(ran1.nextInt(wordsList.size()));
        userdata = new ArrayList<Map<String, Object>>();
        for (int j = 0; j < playerNum; j++) {
            Map<String, Object> map = new HashMap<String, Object>();

            map.put("userNo", j+1);
            map.put("userWord", words.getCommon());
            userdata.add(map);
        }

        Random ran2 = new Random();
        ranUndercover = ran2.nextInt(playerNum);
        userdata.get(ranUndercover).put("userWord", words.getUndercover());

        if (blankNum == 1) {
            ranBlank = (ranUndercover + 1 + ran2.nextInt(playerNum-1)) % playerNum;
            userdata.get(ranBlank).put("userWord", " ");
        }

        UserListAdapter userListAdapter = new UserListAdapter(getApplicationContext(), userdata, mListener);
        userlist.setAdapter(userListAdapter);

    }

    private UserListAdapter.MyClickListener mListener = new UserListAdapter.MyClickListener() {
        @Override
        public void myOnClick(int position, View v) {
            clickedNum ++;
            if(clickedNum < playerNum){
                String userWord = userdata.get(position).get("userWord").toString();
                userdata.get(position).put("clicked", "true");
                UserListAdapter userListAdapter = new UserListAdapter(getApplicationContext(), userdata, mListener);
                userlist.setAdapter(userListAdapter);
                Log.d("userdata", userdata.toString());
                mSVProgressHUD.showInfoWithStatus(userWord, SVProgressHUD.SVProgressHUDMaskType.Black);
            } else {
                if(clickedNum == playerNum) {
                    userdata.get(position).put("clicked", "true");
                    for (int i = 0; i < playerNum; i++) {
                        userdata.get(i).put("out", "false");
                    }
                    Log.d("userdata", userdata.toString());
                    UserListAdapter userListAdapter = new UserListAdapter(getApplicationContext(), userdata, mListener);
                    userlist.setAdapter(userListAdapter);
                    String userWord = userdata.get(position).get("userWord").toString();
                    mSVProgressHUD.showInfoWithStatus(userWord, SVProgressHUD.SVProgressHUDMaskType.Black);

                } else {
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
                                mSVProgressHUD.showInfoWithStatus("平民胜利", SVProgressHUD.SVProgressHUDMaskType.Black);
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
                                mSVProgressHUD.showInfoWithStatus("白板胜利", SVProgressHUD.SVProgressHUDMaskType.Black);
                                return;
                            }
                        } else {
                            userdata.get(position).put("isUndercover", "true");
                            UserListAdapter userListAdapter = new UserListAdapter(getApplicationContext(), userdata, mListener);
                            userlist.setAdapter(userListAdapter);
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
                                    mSVProgressHUD.showInfoWithStatus("平民胜利", SVProgressHUD.SVProgressHUDMaskType.Black);
                                    return;
                                } else {
                                    userdata.get(position).put("isBlank", "true");
                                    userdata.get(ranUndercover).put("isUndercover", "true");
                                    for (int i = 0; i < playerNum; i++) {
                                        userdata.get(i).put("gameover", "true");
                                    }
                                    UserListAdapter userListAdapter = new UserListAdapter(getApplicationContext(), userdata, mListener);
                                    userlist.setAdapter(userListAdapter);
                                    mSVProgressHUD.showInfoWithStatus("卧底胜利", SVProgressHUD.SVProgressHUDMaskType.Black);
                                    return;
                                }
                            } else {
                                userdata.get(position).put("isBlank", "true");
                                UserListAdapter userListAdapter = new UserListAdapter(getApplicationContext(), userdata, mListener);
                                userlist.setAdapter(userListAdapter);
                            }
                        } else {
                            userdata.get(position).put("out", "true");
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
                                mSVProgressHUD.showInfoWithStatus("卧底胜利", SVProgressHUD.SVProgressHUDMaskType.Black);
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
                                mSVProgressHUD.showInfoWithStatus("白板胜利", SVProgressHUD.SVProgressHUDMaskType.Black);
                                return;
                            }
                        } else {
                            userdata.get(position).put("out", "true");
                        }
                    }
                    UserListAdapter userListAdapter = new UserListAdapter(getApplicationContext(), userdata, mListener);
                    userlist.setAdapter(userListAdapter);
                }
            }
        }
    };
}
