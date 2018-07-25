package com.king.app.vrace.view.widget.map;

import android.graphics.Path;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Desc: the flight trace in WorldMapView
 *
 * @author：Jing Yang
 * @date: 2018/7/24 17:46
 */
public class LinePoint {

    /**
     * all points to be drawn
     */
    private List<Point> pointList;

    /**
     * lines between point and point
     */
    private List<Path> linePathList;

    public LinePoint() {
        pointList = new ArrayList<>();
        linePathList = new ArrayList<>();
    }

    public List<Point> getPointList() {
        return pointList;
    }

    public List<Path> getLinePathList() {
        return linePathList;
    }

    public void addPoint(Point point) {
        pointList.add(point);
    }

    public void addLinePath(Path path) {
        linePathList.add(path);
    }
}
