package com.lorytech.WhoIsTheUndercover;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.Utils;

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
        Utils.init(getApplication());

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

    /*
    * 初始化数据库
    * */
    public void initDataBase(){
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(this, "words-db", null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();

        wordsDao = daoSession.getWordsDao();
        String wordsList =
                "汉堡包,肉夹馍;" +
                "蜘蛛侠,蜘蛛精;" +
                "鱼缸,浴缸;" +
                "汤圆,丸子;" +
                "哈密瓜,西瓜;" +
                "牛奶,豆浆;" +
                "保安,保镖;" +
                "气泡,水泡;" +
                "魔术师,魔法师;" +
                "双胞胎,龙凤胎;" +
                "大白兔,金丝猴;" +
                "镜子,玻璃;";
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityUtils.finishAllActivities();
        ActivityUtils.startHomeActivity();
    }

}