package com.lorytech.WhoIsTheUndercover;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.bigkoo.svprogresshud.SVProgressHUD;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private EditText editText_playerNum;
    private RadioGroup radioGroup_blankCard;
    private Button button_startGame;
    private int playerNum, isBlankCard;
    private SVProgressHUD mSVProgressHUD;
    private WordsDao wordsDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSVProgressHUD = new SVProgressHUD(this);
        editText_playerNum = (EditText) findViewById(R.id.playerNum);
        radioGroup_blankCard = (RadioGroup) findViewById(R.id.radio_blank);
        button_startGame = (Button) findViewById(R.id.btn_startGame);

        button_startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this, StartGameActivity.class);
                Bundle digitalBundle = new Bundle();
                if (!("").equals(editText_playerNum.getText().toString())) {
                    if(isNumber(editText_playerNum.getText().toString())){
                        playerNum = Integer.parseInt(editText_playerNum.getText().toString());
                    } else {
                        mSVProgressHUD.showErrorWithStatus("请输入数字", SVProgressHUD.SVProgressHUDMaskType.Black);
                        return;
                    }
                } else {
                    mSVProgressHUD.showInfoWithStatus("请输入玩家人数", SVProgressHUD.SVProgressHUDMaskType.Black);
                    return;
                }

                if (radioGroup_blankCard.getCheckedRadioButtonId() == R.id.btn_yes) {
                    isBlankCard = 1;
                } else {
                    isBlankCard = 0;
                }

                digitalBundle.putInt("playerNum", playerNum);
                digitalBundle.putInt("isBlankCard", isBlankCard);
                i.putExtras(digitalBundle);

                startActivity(i);
            }
        });

        initDataBase();

    }

    public boolean isNumber(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher match = pattern.matcher(str);
        return match.matches();
    }

    public void initDataBase(){
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(this, "words-db", null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();

        wordsDao = daoSession.getWordsDao();
        String wordsList =
                "神雕侠侣,天龙八部;" +
                "天天向上,非诚勿扰;" +
                "勇往直前,全力以赴;" +
                "鱼香肉丝,四喜丸子;" +
                "麻婆豆腐,皮蛋豆腐;" +
                "语无伦次,词不达意;" +
                "鼠目寸光,井底之蛙;" +
                "近视眼镜,隐形眼镜;" +
                "美人心计,倾世皇妃;";
        String[] group = wordsList.split(";");
        if (wordsDao.count() < 1){
            for (int i=0; i<group.length; i++) {
                String[] item = group[i].split(",");
                Words words = new Words();
                words.setCommon(item[0]);
                words.setUndercover(item[1]);
                wordsDao.save(words);
            }
        }
    }



}