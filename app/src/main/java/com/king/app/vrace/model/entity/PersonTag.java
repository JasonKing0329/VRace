package com.king.app.vrace.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/20 20:11
 */
@Entity(nameInDb = "person_tag")
public class PersonTag {

    @Id(autoincrement = true)
    private Long id;

    private String tag;

    @Generated(hash = 680959)
    public PersonTag(Long id, String tag) {
        this.id = id;
        this.tag = tag;
    }

    @Generated(hash = 1388219677)
    public PersonTag() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

}
