package com.king.app.vrace.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/20 19:57
 */
@Entity
public class Player {

    @Id(autoincrement = true)
    private Long id;

    private int gender;

    private int age;

    private String birthday;

    private String province;

    private String city;

    private String occupy;

    private String description;

    @Generated(hash = 938989079)
    public Player(Long id, int gender, int age, String birthday, String province,
            String city, String occupy, String description) {
        this.id = id;
        this.gender = gender;
        this.age = age;
        this.birthday = birthday;
        this.province = province;
        this.city = city;
        this.occupy = occupy;
        this.description = description;
    }

    @Generated(hash = 30709322)
    public Player() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getGender() {
        return this.gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getOccupy() {
        return this.occupy;
    }

    public void setOccupy(String occupy) {
        this.occupy = occupy;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAge() {
        return this.age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBirthday() {
        return this.birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
