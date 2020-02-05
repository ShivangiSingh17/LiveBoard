package com.example.livewhiteboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CopyOnWriteArrayList;

import io.socket.client.Socket;

public class CanvasView extends View {
    public static final String TAG = "Hey";

    public int width;
    public int height;
    private Bitmap bitmap;
    private Canvas canvas;
    private Path path;
    private CopyOnWriteArrayList<Stroke> allStrokes = new CopyOnWriteArrayList<>();
    Context context;
    private Paint paint;
    private Socket socket;
    private float mX = 0, mY = 0;
    private static final float TOLERANCE = 5;
    private String emitTo;
    private String sessionId;

    public CanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.setDrawingCacheEnabled(true);
        this.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        path = new Path();
        socket = ((SocketHandler) ((Activity) context).getApplication()).getSocket();

        createPaintObject(Color.BLACK, 4f);

    }

    public class Stroke {
        private Path path;

        private Paint paint;

        public Stroke(Path path, Paint paint) {

            this.path = path;
            this.paint = paint;
        }

        public Path getPath() {
            return path;
        }

        public Paint getPaint() {
            return paint;
        }
    }

    public void changeColor(int color) {
        paint.setColor(color);
        paint.setStrokeWidth(4f);
    }

    public void setEraser() {
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//        invalidate();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(50f);
    }

    public Bitmap getBitmap() {
        this.destroyDrawingCache();
        this.buildDrawingCache();
        return this.getDrawingCache();
    }

    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void drawFromBitmap(Bitmap bitmap) {
        Log.d(TAG, "updateCanvas: " + bitmap);
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        this.bitmap = mutableBitmap;
//        canvas.drawBitmap(mutableBitmap,0,0,paint);
//        canvas = new Canvas(mutableBitmap);
//        Log.d(TAG, "updateCanvas: ");
        Log.d(TAG, "drawFromBitmap: " + this.bitmap);
//        super.onDraw(canvas);
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged: " + this.bitmap);
        if (this.bitmap == null)
            this.bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(this.bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
//        if (this.bitmap != null) {
        Log.d(TAG, "onDraw: " + bitmap);
        canvas.drawBitmap(this.bitmap, 0, 0, paint);

        Log.d(TAG, "onDraw: drawn" + bitmap);
//            this.bitmap = null;
//            Log.d(TAG, "onDraw: " + bitmap);
//        }


        for (Stroke s : allStrokes) {
            canvas.drawPath(s.getPath(), s.getPaint());
        }
        canvas.drawPath(path, paint);


    }

    public void clearCanvas() {
        path.reset();
        allStrokes.clear();
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        invalidate();
    }

    // when ACTION_DOWN start touch according to the x,y values
    private void startTouch(float x, float y) {
//        try {
//            socket.emit("drawing", getJsonObject(x,y));
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        path.moveTo(x, y);
        mX = x;
        mY = y;
        emitToServer(x, y);

    }

    // when ACTION_MOVE move touch according to the x,y values
    private void moveTouch(float x, float y) {
//        try {
//            socket.emit("drawing", getJsonObject(x,y));
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOLERANCE || dy >= TOLERANCE) {
            emitToServer(x, y);
            path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    // when ACTION_UP stop touch
    private void upTouch(float x, float y) {
        Log.d("UPTOUCH", "upTouch: " + x + " " + y);

//        try {
//            socket.emit("drawing", getJsonObject(x,y));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        emitToServer(x, y);
        path.lineTo(mX, mY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                moveTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                upTouch(x, y);
                allStrokes.add(new Stroke(path, paint));
                path = new Path();
                createPaintObject(paint.getColor(), paint.getStrokeWidth());
                invalidate();
                break;
        }
        return true;
    }

    private void createPaintObject(int color, float strokeWidth) {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(strokeWidth);
    }

    private JSONObject getJsonObject(float x, float y) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("x0", mX / getWidth());
        jsonObject.put("y0", mY / getHeight());
        jsonObject.put("x1", x / getWidth());
        jsonObject.put("y1", y / getHeight());
        String color = "";
        switch (paint.getColor()) {
            case Color.BLACK:
                color = "black";
                break;
            case Color.WHITE:
                color = "white";
                break;
            case Color.RED:
                color = "red";
                break;
            case Color.BLUE:
                color = "blue";
                break;
            case Color.GREEN:
                color = "green";
                break;
        }
        jsonObject.put("color", color);
        jsonObject.put("strokeWidth", paint.getStrokeWidth());
        return jsonObject;
    }

    public void setEmitTo(String emitTo) {
        this.emitTo = emitTo;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }


    private void emitToServer(float x, float y) {
        Log.d(TAG, "emitToServer: Trying");

        JSONObject jsonObject = null;
        try {
            jsonObject = getJsonObject(x, y);
            if (this.emitTo == "drawingInSession") {
                Log.d(TAG, "emitToServer: " + this.emitTo);
                jsonObject.put("sessionId", this.sessionId);
                socket.emit(this.emitTo, jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void drawFromServer(JSONObject jsonObject) {
        try {
            Log.d(TAG, "drawFromServer: Trying");
            float x0 = (float) jsonObject.getDouble("x0");
            float y0 = (float) jsonObject.getDouble("y0");
            float x1 = (float) jsonObject.getDouble("x1");
            float y1 = (float) jsonObject.getDouble("y1");
            path.moveTo(x0 * getWidth(), y0 * getHeight());
            path.lineTo(x1 * getWidth(), y1 * getHeight());

            int color = 0;
            switch (jsonObject.getString("color")) {
                case "black":
                    color = Color.BLACK;
                    break;
                case "white":
                    color = Color.WHITE;
                    break;
                case "red":
                    color = Color.RED;
                    break;
                case "blue":
                    color = Color.BLUE;
                    break;
                case "green":
                    color = Color.GREEN;
                    break;
                case "yellow":
                    color = Color.YELLOW;
                    break;
            }
            paint.setColor(color);
            paint.setStrokeWidth((float) jsonObject.getDouble("strokeWidth"));
            allStrokes.add(new Stroke(path, paint));
            path = new Path();
            createPaintObject(paint.getColor(), paint.getStrokeWidth());
            invalidate();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
