package com.king.app.vrace.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/20 20:16
 */
@Entity(nameInDb = "leg_place")
public class LegPlaces {

    @Id(autoincrement = true)
    private Long id;

    private long legId;

    private int seq;

    private String continent;

    private String country;

    private String city;

    private String countryEng;

    private String cityEng;

    @Generated(hash = 810388672)
    public LegPlaces(Long id, long legId, int seq, String continent, String country,
            String city, String countryEng, String cityEng) {
        this.id = id;
        this.legId = legId;
        this.seq = seq;
        this.continent = continent;
        this.country = country;
        this.city = city;
        this.countryEng = countryEng;
        this.cityEng = cityEng;
    }

    @Generated(hash = 1678271984)
    public LegPlaces() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getLegId() {
        return this.legId;
    }

    public void setLegId(long legId) {
        this.legId = legId;
    }

    public String getContinent() {
        return this.continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountryEng() {
        return this.countryEng;
    }

    public void setCountryEng(String countryEng) {
        this.countryEng = countryEng;
    }

    public String getCityEng() {
        return this.cityEng;
    }

    public void setCityEng(String cityEng) {
        this.cityEng = cityEng;
    }

    public int getSeq() {
        return this.seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

}
