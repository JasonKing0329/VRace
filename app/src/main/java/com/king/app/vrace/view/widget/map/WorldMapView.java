package com.king.app.vrace.view.widget.map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.king.app.vrace.utils.DebugLog;

import java.util.List;

/**
 * Desc:显示世界地图以及访问过的国家
 * 采用svg图片转换为xml读取path并绘制于canvas上
 *
 * @author：Jing Yang
 * @date: 2018/7/23 15:28
 */
public class WorldMapView extends View {

    private Paint paintMap;
    private Paint paintBorder;
    private Paint paintPoint;
    private Paint paintLine;

    private float scale = 1.0f;

    private int mWidth;

    private int mHeight;

    private AbsMapAdapter adapter;

    private int mBgColor = Color.WHITE;

    private int mPlaceColor = Color.parseColor("#999999");

    private int mPlaceFocusColor = Color.parseColor("#3F51B5");

    private GestureDetector mDetector;

    private boolean mDisableTogglePlace;

    public WorldMapView(Context context) {
        super(context);
        init();
    }

    public WorldMapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        setLayerType(LAYER_TYPE_SOFTWARE, null);
        paintMap = new Paint();
        paintMap.setAntiAlias(true);
        paintMap.setStyle(Paint.Style.FILL);
        paintMap.setStrokeWidth(5);

        paintBorder = new Paint();
        paintBorder.setAntiAlias(true);
        paintBorder.setColor(Color.WHITE);
        paintBorder.setStyle(Paint.Style.STROKE);
        paintBorder.setStrokeWidth(1);

        paintPoint = new Paint();
        paintPoint.setAntiAlias(true);
        paintPoint.setColor(Color.parseColor("#FF0000"));
        paintPoint.setStyle(Paint.Style.FILL);
        paintPoint.setStrokeWidth(3);
        paintPoint.setStrokeCap(Paint.Cap.ROUND);

        paintLine = new Paint();
        paintLine.setAntiAlias(true);
        paintLine.setColor(Color.parseColor("#FF4081"));
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeWidth(1);

        mDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
        mDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
                DebugLog.e(motionEvent.getX() + ", " + motionEvent.getY());
                float x = motionEvent.getX() / scale;
                float y = motionEvent.getY() / scale;
                if (!mDisableTogglePlace) {
                    togglePlace(x, y);
                }
                invalidate();
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return false;
            }
        });

    }

    /**
     * 选中/取消选择地点
     * @param x
     * @param y
     */
    private void togglePlace(float x, float y) {
        if (adapter != null && adapter.getMapItems() != null) {

            int size = adapter.getMapItems().size();
            for (int i = 0; i < size; i ++) {
                MapItem item = adapter.getMapItems().get(i);
                if (item.isBg()) {
                    continue;
                }
                if (isInRegion((int) x, (int) y, item.getPath())) {
                    boolean target = !item.isSelected();
                    adapter.onSelectPlace(i, target);
                    item.setSelected(target);
                } else {
                    item.setSelected(false);
                }

            }
        }
    }

    public void setAdapter(AbsMapAdapter adapter) {
        this.adapter = adapter;
        if (adapter != null) {
            mWidth = adapter.getMapWidth();
            mHeight = adapter.getMapHeight();
        }
        invalidate();
        requestLayout();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event); //把手势相关操作返回给 手势操控类
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int minimumWidth = getSuggestedMinimumWidth();
        int minimumHeight = getSuggestedMinimumHeight();
        int width = measureWidth(minimumWidth, widthMeasureSpec);
        int height = measureHeight(minimumHeight, heightMeasureSpec);
        DebugLog.e("width=" + width);
        DebugLog.e("height=" + height);
        setMeasuredDimension(width, height);
    }

    /**
     * specMode判断控件设置的layout_width
     * 1. 本view layout_width指定为固定值，specMode=固定值
     * 2. 本view嵌套在HorizontalScrollView中，HorizontalScrollView作用于横向滚动
     *      --> 无论本view layout_width设置的是match_parent还是wrap_content，specMode=UNSPECIFIED
     *          为支持嵌入HorizontalScrollView滚动视图，在UNSPECIFIED里计算本view应该有的宽度
     * 3. 本view嵌套在其他没有横向滚动功能的ViewGroup中
     *      --> ViewGroup宽度已知（指定过大小，或match_parent，parent已知大小，比如整个屏幕）
     *          --> 无论本view layout_width设置的是match_parent还是wrap_content，specMode=AT_MOST
     *              所以这里选择在AT_MOST也运用本view应该有的宽度，也可以改为运用parent的宽度
     *      --> ViewGroup宽度未知（不是说设置为wrap_content就是未知，而是比如嵌套在HorizontalScrollView中，导致ViewGroup的宽度也未知）
     *          --> 同第2条
     *
     *  measureHeight同理，考虑layout_height与是否嵌入ScrollView
     * @param defaultWidth
     * @param measureSpec
     * @return
     */
    private int measureWidth(int defaultWidth, int measureSpec) {

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.AT_MOST:
                defaultWidth = (int) (mWidth * scale);
                break;
            case MeasureSpec.EXACTLY:
                defaultWidth = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                defaultWidth = (int) (mWidth * scale);
        }
        return defaultWidth;
    }


    private int measureHeight(int defaultHeight, int measureSpec) {

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.AT_MOST:
                defaultHeight = (int) (mHeight * scale);
                break;
            case MeasureSpec.EXACTLY:
                defaultHeight = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                defaultHeight = (int) (mHeight * scale);
                break;
        }
        return defaultHeight;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();

        if (adapter != null) {
            scale = (float) getHeight() / (float) adapter.getMapHeight();
            DebugLog.e("scale=" + scale);
            canvas.scale(scale, scale, 0, 0);
            List<MapItem> mapItems = adapter.getMapItems();
            if (mapItems != null) {
                for (MapItem item:mapItems) {
                    // draw countries
                    drawMapItem(canvas, item);
                    // draw border of countries
                    drawItemBorder(canvas, item);
                }
            }
            // draw flight line
            LinePoint linePoint = adapter.getLinePoint();
            if (linePoint != null) {
                drawLinePoint(canvas, linePoint);
            }
        }

        canvas.restore();
    }

    private void drawLinePoint(Canvas canvas, LinePoint linePoint) {
        drawPoints(canvas, linePoint.getPointList());
        drawLines(canvas, linePoint.getLinePathList());
    }

    private void drawPoints(Canvas canvas, List<Point> pointList) {
        if (pointList != null) {
            for (Point point:pointList) {
                canvas.drawPoint(point.x, point.y, paintPoint);
            }
        }
    }

    private void drawLines(Canvas canvas, List<Path> linePathList) {
        if (linePathList != null) {
            for (Path path:linePathList) {
                canvas.drawPath(path, paintLine);
            }
        }
    }

    private void drawMapItem(Canvas canvas, MapItem item) {
        if (item.isBg()) {
            paintMap.clearShadowLayer();
            paintMap.setColor(mBgColor);
            canvas.drawPath(item.getPath(), paintMap);
        }
        else {
            if (item.isSelected()) {
                // shadow
                paintMap.clearShadowLayer();
                paintMap.setShadowLayer(3, 0, 0, Color.BLACK);
                canvas.drawPath(item.getPath(), paintMap);
                // selected place
                paintMap.clearShadowLayer();
                paintMap.setColor(mPlaceFocusColor);
                canvas.drawPath(item.getPath(), paintMap);
            }
            else {
                paintMap.clearShadowLayer();
                paintMap.setColor(mPlaceColor);
                canvas.drawPath(item.getPath(), paintMap);
            }
        }
    }

    private void drawItemBorder(Canvas canvas, MapItem item) {
        canvas.drawPath(item.getPath(), paintBorder);
    }

    /**
     * 是否touch在该path内部
     * @param x
     * @param y
     * @return
     */
    public boolean isInRegion(int x, int y, Path mPath) {
        Region result = new Region();
        //构造一个区域对象。
        RectF r=new RectF();
        //计算path的边界
        mPath.computeBounds(r, true);
        //设置区域路径和剪辑描述的区域
        result.setPath(mPath, new Region((int)r.left,(int)r.top,(int)r.right,(int)r.bottom));
        return result.contains(x, y);
    }

    public void disableTogglePlace() {
        mDisableTogglePlace = true;
    }

    public void enableTogglePlace() {
        mDisableTogglePlace = false;
    }
}
