package com.lorytech.WhoIsTheUndercover;

/**
 * Created by ZhangChen on 2017/12/5 15:34
 */

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Entity mapped to table "NOTE".
 */
@Entity(indexes = {
        @Index(value = "common", unique = true)
})
public class Words {

    @Id
    private Long id;

    @NotNull
    private String common;

    private String undercover;

    @Generated(hash = 796553661)
    public Words() {
    }

    public Words(Long id) {
        this.id = id;
    }

    @Generated(hash = 650230802)
    public Words(Long id, @NotNull String common, String undercover) {
        this.id = id;
        this.common = common;
        this.undercover = undercover;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public String getCommon() {
        return common;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setCommon(@NotNull String common) {
        this.common = common;
    }

    public String getUndercover() {
        return undercover;
    }

    public void setUndercover(String undercover) {
        this.undercover = undercover;
    }


}