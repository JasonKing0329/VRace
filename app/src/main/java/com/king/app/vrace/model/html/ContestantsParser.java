package com.king.app.vrace.model.html;

import android.util.SparseBooleanArray;

import com.king.app.vrace.base.RaceApplication;
import com.king.app.vrace.conf.AppConfig;
import com.king.app.vrace.model.entity.Player;
import com.king.app.vrace.model.entity.PlayerDao;
import com.king.app.vrace.model.entity.Season;
import com.king.app.vrace.model.entity.SeasonDao;
import com.king.app.vrace.utils.DebugLog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/7/18 11:16
 */
public class ContestantsParser extends AbsParser {

    public Observable<List<Player>> parse(File file) {
        return Observable.create(e -> {

            List<Player> list = new ArrayList<>();
            SeasonDao seasonDao = RaceApplication.getInstance().getDaoSession().getSeasonDao();
            PlayerDao playerDao = RaceApplication.getInstance().getDaoSession().getPlayerDao();
            Map<Integer, Boolean> seasonAddedMap = new HashMap<>();
            Map<Integer, Long> seasonIdMap = new HashMap<>();

            Document document = Jsoup.parse(file, "UTF-8");
            // class有空格这个不管用
//            Elements tableCell = document.select("table.sortable wikitable");
            Element tableCell = document.select("table").get(3);
            Elements rows = tableCell.select("tr");
            for (int i = 1; i < rows.size(); i ++) {

                StringBuffer logBuffer = new StringBuffer(i + "  ");
                Player player = new Player();
                player.setOccupy("");
                player.setDescription("");
                Element tr = rows.get(i);
                Elements cols = tr.select("td");

                // 先检查season过滤不需要添加的内容
                Element eleSeason = cols.get(3).selectFirst("a");
                if (eleSeason == null) {
                    eleSeason = cols.get(3).selectFirst("span");
                }
                if (eleSeason == null) {
                    eleSeason = cols.get(3);
                }
                String season = eleSeason.text().substring(7);
                logBuffer.append("season ").append(season);
                int index = Integer.parseInt(season);
                // 缓存seasonId
                if (seasonIdMap.get(index) == null) {
                    Season seasonBean = seasonDao.queryBuilder()
                            .where(SeasonDao.Properties.Index.eq(index))
                            .build().unique();
                    if (seasonBean == null) {
                        continue;
                    }
                    seasonIdMap.put(index, seasonBean.getId());
                }
                // 已添加过的不再添加
                if (seasonAddedMap.get(index) == null) {
                    long count = playerDao.queryBuilder()
                            .where(PlayerDao.Properties.DebutSeasonId.eq(seasonIdMap.get(index)))
                            .buildCount().count();
                    if (count > 0) {
                        seasonAddedMap.put(index, true);
                        continue;
                    }
                    else {
                        seasonAddedMap.put(index, false);
                    }
                }
                else if (seasonAddedMap.get(index)) {
                    continue;
                }
                player.setDebutSeasonId(seasonIdMap.get(index));

                Element eleName = cols.get(0);
                String name = eleName.text();
                name = name.split(" ")[0].trim();
                player.setName(name);
                logBuffer.append(", ").append(name);

                Element eleAge = cols.get(1);
                String age = eleAge.text();
                player.setAge(Integer.parseInt(age));
                logBuffer.append(", ").append(age);

                Element elePlace = cols.get(2).selectFirst("a");
                String place = elePlace.attr("title");
                if (place.contains(", ")) {
                    String[] arrays = place.split(",");
                    player.setCity(arrays[0]);
                    player.setProvince(arrays[1].trim());
                }
                else {// New York City
                    if (place.equals("New York City")) {
                        player.setCity("New York City");
                        player.setProvince("New York");
                    }
                    else {
                        player.setCity(place);
                        player.setProvince(place);
                    }
                }
                logBuffer.append(", ").append(place);

                DebugLog.e(logBuffer.toString());

                list.add(player);
            }
            e.onNext(list);
        });
    }

    public Observable<File> getFile() {
        return Observable.create(e -> e.onNext(new File(AppConfig.FILE_HTML_CONTESTANTS)));
    }

    public Observable<File> saveFile(final ResponseBody responseBody, final String path) {
        return Observable.create(e -> e.onNext(saveFile(responseBody.byteStream(), path)));
    }

    private File saveFile(InputStream inputStream, String path) {
        File file = new File(path);

        OutputStream outputStream = null;

        try {
            byte[] fileReader = new byte[4096];
            outputStream = new FileOutputStream(file);
            while (true) {
                int read = inputStream.read(fileReader);
                if (read == -1) {
                    break;
                }
                outputStream.write(fileReader, 0, read);
            }
            outputStream.flush();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
