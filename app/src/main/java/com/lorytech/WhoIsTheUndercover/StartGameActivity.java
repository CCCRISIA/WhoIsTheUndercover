package com.lorytech.WhoIsTheUndercover;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.bigkoo.svprogresshud.SVProgressHUD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class StartGameActivity extends AppCompatActivity {

    private SVProgressHUD mSVProgressHUD;

    private int playerNum, isBlank;

    private ListView userlist;

    private WordsDao wordsDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startgame);
        mSVProgressHUD = new SVProgressHUD(this);
        Bundle  extras=getIntent().getExtras();
        playerNum = extras.getInt("playerNum");
        isBlank = extras.getInt("isBlankCard");
        userlist = (ListView) findViewById(R.id.userList);
        initUI();
    }

    private void initUI(){

        // get the wordsDAO
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(this, "words-db", null);

        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        wordsDao = daoSession.getWordsDao();
        List<Words> wordsList = wordsDao.queryBuilder()
        .where(WordsDao.Properties.Id.notEq(-1))
        .orderAsc(WordsDao.Properties.Id)
        .limit(999)
        .build().list();
        for (int j=0; j< wordsList.size(); j++){
            Log.d("wordsList", "平民：" + wordsList.get(j).getCommon() + "; 卧底：" + wordsList.get(j).getUndercover());
        }

        Random ran1 = new Random();
        Words words = wordsList.get(ran1.nextInt(wordsList.size()));
        List<Map<String, Object>> prodata = new ArrayList<Map<String, Object>>();
        for (int j = 0; j < playerNum; j++) {
            Map<String, Object> map = new HashMap<String, Object>();

            map.put("userNo", j+1);
            map.put("userWord", words.getCommon());
            prodata.add(map);

        }

        Random ran2 = new Random();

        prodata.get(ran2.nextInt(playerNum)).put("userWord", words.getUndercover());

        UserListAdapter userListAdapter = new UserListAdapter(getApplicationContext(), prodata);
        userlist.setAdapter(userListAdapter);

    }

}
