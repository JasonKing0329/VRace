package com.king.app.vrace.model;

import android.content.res.Resources;
import android.graphics.Path;
import android.graphics.Point;

import com.king.app.vrace.base.RaceApplication;
import com.king.app.vrace.conf.AppConfig;
import com.king.app.vrace.conf.AppConstants;
import com.king.app.vrace.model.entity.MapBean;
import com.king.app.vrace.model.entity.MapCountry;
import com.king.app.vrace.model.entity.MapCountryDao;
import com.king.app.vrace.model.entity.MapPath;
import com.king.app.vrace.model.entity.MapPathDao;
import com.king.app.vrace.utils.DebugLog;
import com.king.app.vrace.view.widget.map.MapItem;
import com.king.app.vrace.view.widget.map.PathParser;
import com.king.app.vrace.viewmodel.bean.LegMapItem;
import com.king.app.vrace.viewmodel.bean.MapData;

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

    public MapItem getMapItemByName(String name) {
        MapItem item = new MapItem();
        MapPath mp = RaceApplication.getInstance().getDaoSession().getMapPathDao().queryBuilder()
                .where(MapPathDao.Properties.Place.eq(name))
                .build().unique();
        if (mp == null) {
            return null;
        }
        else {
            item.setBean(mp);
            if (mp.getType() == AppConstants.MAP_ITEM_BG) {
                item.setBg(true);
            }
            item.setPath(PathParser.createPathFromPathData(mp.getPathValues()));
        }
        return item;
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

    /**
     * 创建飞行航程（只考虑起点国与终点国，不考虑中转）
     * 最左与最右区域相互飞行连线需要穿过边界而不是直接连线
     * @param mapData
     * @param lastPoint
     * @param point
     * @param lastItem
     * @param item
     * @return
     */
    public Path createFlightPath(MapData mapData, Point lastPoint, Point point, LegMapItem lastItem, LegMapItem item) {

        DebugLog.e("from " + lastItem.getCountryChn() + "  " + lastPoint.x + ", " + lastPoint.y);
        DebugLog.e("to " + item.getCountryChn() + "  " + point.x + ", " + point.y);

        int mapWidth = mapData.getMap().getWidth();
        // 以800为例，南美洲为最左，巴西的矩形中心位置为279，取300为最左边界
        int edgeLeft = mapWidth * 3 / 8;
        // 以800为例，中国的矩形中心位置为632，取600为最右边界
        int edgeRight = mapWidth * 6 / 8;

        Path path = new Path();
        // 从 北美洲/南美洲 飞往 亚洲东部/大洋洲
        if (lastPoint.x < edgeLeft && point.x > edgeRight) {
            // 以两张地图拼接的思路，目标点在拼接图的对应位置，做lastPoint到目标point的直线连接
            Point targetPoint = new Point(point.x - mapWidth, point.y);
            // 得出连线抵达地图最左边界的y值
            Point mostLeftPoint = new Point();
            mostLeftPoint.x = 0;
            mostLeftPoint.y = (-lastPoint.x) * (targetPoint.y - lastPoint.y) / (targetPoint.x - lastPoint.x) + lastPoint.y;
            path.moveTo(lastPoint.x, lastPoint.y);
            path.lineTo(mostLeftPoint.x, mostLeftPoint.y);
            DebugLog.e("separate from " + lastPoint.x + ", " + lastPoint.y);
            DebugLog.e("separate to " + mostLeftPoint.x + ", " + mostLeftPoint.y);
            // 连线穿过地图边界后到达目的地的连线
            Point mostRightPoint = new Point(mapWidth, mostLeftPoint.y);
            path.moveTo(mostRightPoint.x, mostRightPoint.y);
            path.lineTo(point.x, point.y);
            DebugLog.e("separate from " + mostRightPoint.x + ", " + mostRightPoint.y);
            DebugLog.e("separate to " + point.x + ", " + point.y);
            makePathArrow(mostRightPoint.x, mostRightPoint.y, point.x, point.y, 4, 3, path);
        }
        // 从 亚洲东部/大洋洲 飞往 北美洲/南美洲
        else if (lastPoint.x > edgeRight && point.x < edgeLeft) {
            // 以两张地图拼接的思路，目标点在拼接图的对应位置，做lastPoint到目标point的直线连接
            Point targetPoint = new Point(point.x + mapWidth, point.y);
            // 得出连线抵达地图最右边界的y值
            Point mostRightPoint = new Point();
            mostRightPoint.x = mapWidth;
            mostRightPoint.y = (mapWidth - lastPoint.x) * (targetPoint.y - lastPoint.y) / (targetPoint.x - lastPoint.x) + lastPoint.y;
            path.moveTo(lastPoint.x, lastPoint.y);
            path.lineTo(mostRightPoint.x, mostRightPoint.y);
            DebugLog.e("separate from " + lastPoint.x + ", " + lastPoint.y);
            DebugLog.e("separate to " + mostRightPoint.x + ", " + mostRightPoint.y);
            // 连线穿过地图边界后到达目的地的连线
            Point mostLeftPoint = new Point(0, mostRightPoint.y);
            path.moveTo(mostLeftPoint.x, mostLeftPoint.y);
            path.lineTo(point.x, point.y);
            DebugLog.e("separate from " + mostLeftPoint.x + ", " + mostLeftPoint.y);
            DebugLog.e("separate to " + point.x + ", " + point.y);
            makePathArrow(mostLeftPoint.x, mostLeftPoint.y, point.x, point.y, 4, 3, path);
        }
        // 其他情况直接连线
        else {
            path.moveTo(lastPoint.x, lastPoint.y);
            path.lineTo(point.x, point.y);
            makePathArrow(lastPoint.x, lastPoint.y, point.x, point.y, 4, 3, path);
        }
        return path;
    }

    /**
     *
     * @param fromX start x of line
     * @param fromY start y of line
     * @param toX end x of line
     * @param toY end y of line
     * @param height the length of arrow
     * @param bottom the widest distance between to arrow line
     * @param path
     */
    private void makePathArrow(float fromX, float fromY, float toX, float toY,
                                 int height, int bottom, Path path) {

        float juli = (float) Math.sqrt((toX - fromX) * (toX - fromX)
                + (toY - fromY) * (toY - fromY));// 获取线段距离
        float juliX = toX - fromX;// 有正负，不要取绝对值
        float juliY = toY - fromY;// 有正负，不要取绝对值
        float dianX = toX - (height / juli * juliX);
        float dianY = toY - (height / juli * juliY);
        path.moveTo(toX, toY);// 此点为三边形的起点
        path.lineTo(dianX + (bottom / juli * juliY), dianY
                - (bottom / juli * juliX));
        path.moveTo(toX, toY);// 此点为三边形的起点
        path.lineTo(dianX - (bottom / juli * juliY), dianY
                + (bottom / juli * juliX));
//        path.close(); // 使这些点构成封闭的三边形
    }

}
