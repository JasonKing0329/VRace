package com.king.app.vrace.model;

import android.content.res.Resources;

import com.king.app.vrace.base.RaceApplication;
import com.king.app.vrace.conf.AppConfig;
import com.king.app.vrace.conf.AppConstants;
import com.king.app.vrace.model.entity.MapBean;
import com.king.app.vrace.model.entity.MapCountry;
import com.king.app.vrace.model.entity.MapPath;
import com.king.app.vrace.view.widget.map.MapItem;
import com.king.app.vrace.view.widget.map.PathParser;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/7/23 17:08
 */
public class MapModel {

    public Observable<List<MapItem>> loadMapItems(MapBean map) {
        return Observable.create(e -> {
            List<MapItem> list = new ArrayList<>();
            for (MapPath mapPath:map.getPathItems()) {
                MapItem item = new MapItem();
                item.setBean(mapPath);
                if (mapPath.getType() == AppConstants.MAP_ITEM_BG) {
                    item.setBg(true);
                }
                item.setPath(PathParser.createPathFromPathData(mapPath.getPathValues()));
                list.add(item);
            }
            e.onNext(list);
        });
    }

    public Observable<MapBean> loadWorldMap() {
        return Observable.create(e -> {
            MapBean map = RaceApplication.getInstance().getDaoSession().getMapBeanDao().load(AppConstants.MAP_ID_WORLD);
            if (map == null) {
                map = new MapBean();
            }
            e.onNext(map);
        });
    }

    public Observable<MapBean> createMap(Resources resources, int res, long mapId, String name) {
        return Observable.create(e -> {
            MapBean map = new MapBean();
            map.setId(mapId);
            map.setName(name);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputStream is = resources.openRawResource(res);
            Document doc = db.parse(is);
            //根节点
            Element element = doc.getDocumentElement();
            String strWidth = element.getAttribute("android:viewportWidth");
            String strHeight = element.getAttribute("android:viewportHeight");
            int width = getDimensionSize(strWidth);
            int height = getDimensionSize(strHeight);
            map.setWidth(width);
            map.setHeight(height);
            RaceApplication.getInstance().getDaoSession().getMapBeanDao().insertOrReplace(map);

            List<MapPath> pathList = new ArrayList<>();
            //获取path元素节点集合
            NodeList paths = doc.getElementsByTagName("path");
            for (int i = 0; i < paths.getLength(); i++) {
                // 取出每一个元素
                Element node = (Element) paths.item(i);
                String pathValue = node.getAttribute("android:pathData");
                String colorValue = node.getAttribute("android:fillColor");
                String nameValue=node.getAttribute("android:name");
                MapPath mapPath = new MapPath();
                mapPath.setMapId(mapId);
                mapPath.setPlace(nameValue);
                mapPath.setPathValues(pathValue);
                mapPath.setType("Background".equals(nameValue) ? AppConstants.MAP_ITEM_BG : AppConstants.MAP_ITEM_PATH);

                pathList.add(mapPath);
            }
            RaceApplication.getInstance().getDaoSession().getMapPathDao().insertInTx(pathList);
            e.onNext(map);
        });
    }

    public Observable<Object> createMapCountry(MapBean map) {
        return Observable.create(observer -> {
            org.jsoup.nodes.Document document = Jsoup.parse(new File(AppConfig.HTML_BASE + "/" + AppConfig.HTML_COUNTRY_ENG_CHN), "UTF-8");
            org.jsoup.nodes.Element tableCell = document.select("table").get(0);
            Elements rows = tableCell.select("tr");
            List<MapCountry> list = new ArrayList<>();
            for (int i = 1; i < rows.size(); i++) {
                MapCountry country = new MapCountry();
                country.setMapId(map.getId());

                org.jsoup.nodes.Element tr = rows.get(i);
                Elements cols = tr.select("td");

                try {
                    org.jsoup.nodes.Element eng = cols.get(0).select("b").get(0);
                    country.setNameInMap(eng.text());
                } catch (Exception e) {}
                try {
                    org.jsoup.nodes.Element shortName = cols.get(1).select("b").get(0);
                    country.setNameShort(shortName.text());
                } catch (Exception e) {}
                try {
                    org.jsoup.nodes.Element chn = cols.get(2).select("b").get(0);
                    country.setNameChn(chn.text());
                } catch (Exception e) {}

                list.add(country);
            }

            RaceApplication.getInstance().getDaoSession().getMapCountryDao().insertInTx(list);
            observer.onNext(new Object());
        });
    }

    private int getDimensionSize(String size) {
        try {
            if (size.endsWith("dp")) {
                size = size.split("dp")[0];
            }
            else if (size.endsWith("px")) {
                size = size.split("px")[0];
            }
            return (int) Float.parseFloat(size);
        } catch (Exception e) {}
        return 0;
    }
}
