package com.bijak.techno.oganlopian.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Constants {

    //Chat nodes
    public static final String NODE_TEXT = "text";
    public static final String NODE_TIMESTAMP = "timestamp";
    public static final String NODE_RECEIVER_ID = "receiverid";
    public static final String NODE_RECEIVER_NAME = "receivername";
    public static final String NODE_RECEIVER_PHOTO = "receiverphoto";
    public static final String NODE_SENDER_ID = "senderid";
    public static final String NODE_SENDER_NAME = "sendername";
    public static final String NODE_SENDER_PHOTO = "senderphoto";
    public static final String NODE_IS_READ = "isread";
    public static String MESSAGE_TEXT="text";
    //User nodes
    public static final String NODE_ID = "id";
    public static final String NODE_NAME = "name";
    public static final String NODE_PHOTO = "photo";
    public static final String NODE_ROLE = "roles";

    public static final String LOG_TAG="OganLopian";
    //https://samkes-af658.firebaseio.com/
    public static final String MESSAGE_CHILD = "messages";

    public static final String PREF_MY_ID = "myid";
    public static final String PREF_MY_NAME = "myname";
    public static final String PREF_MY_DP = "mydp";
    public static final String PREF_MY_ROL = "myrol";
    public static final String PREF_MY_UID = "myuid";

    public  static final String IMG_URL ="https://www.shareicon.net/data/48x48/2016/09/15/829466_man_512x512.png";

    public static final int LOCATION_INTERVAL = 5;
    public static final int FASTEST_LOCATION_INTERVAL = 30000;
    public static final double defaultLat= -6.541527513207555;
    public static final double defaultLng= 107.4436604976654;
    public static final int MAX_DISTANCE=20;
    public static final double LAMA_ONLINE = 5; //in hour

    public static String dateFormat(String inputdate, String fromated) throws ParseException {
        // Parse the input date
        String format_tanggal="dd-MM-yyyy";
        if(fromated==null){ fromated=format_tanggal;}
        SimpleDateFormat fmt = new SimpleDateFormat(fromated);
        Date inputDate = fmt.parse(inputdate);

        // Create the MySQL datetime string
        fmt = new SimpleDateFormat(fromated);
        String dateString = fmt.format(inputDate);
        return dateString;
    }
    public static String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while (itr.hasNext()) {

            String key = itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }

        return result.toString();
    }
    public static void AutoCompleteFill(Context context, AppCompatAutoCompleteTextView txt, String[] list, int threshold){
        ArrayAdapter<String> adapter =null;
        adapter = new ArrayAdapter<String>(context,android.R.layout.select_dialog_item,list);
        txt.setThreshold(threshold);
        txt.setAdapter(adapter);
    }
    public static Bitmap String2Bitmap(String decodeBitmap){
        try {
            final String encodedString = decodeBitmap;
            final String pureBase64Encoded = encodedString.substring(encodedString.indexOf(",") + 1);
            final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            return decodedBitmap;
        } finally {
            Log.d("Bitmap",decodeBitmap);
        }
    }
    public static String bitmapToBase64String(Bitmap bmp, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte[] bytes = baos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
    public static String LamaWaktu(String tanggal){
        String result="";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        //format.setTimeZone(TimeZone.getTimeZone("UTC"));
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format.parse(tanggal);
            d2 = format.parse(currentTime);

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            /*System.out.print(diffDays + " days, ");
            System.out.print(diffHours + " hours, ");
            System.out.print(diffMinutes + " minutes, ");
            System.out.print(diffSeconds + " seconds.");*/
            if(diffDays >1 || diffHours >12) {
                result = tanggal.toString();
            }else if(diffDays==0 && diffHours==0){
                result = diffMinutes +" menit yang lalu";
            }else{
                result= diffHours +" Jam yang lalu";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean isValidEmail(EditText argEditText) {

        try {
            Pattern pattern = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
            Matcher matcher = pattern.matcher(argEditText.getText());
            return matcher.matches();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static String getWhiteSpace(int size) {
        StringBuilder builder = new StringBuilder(size);
        for(int i = 0; i <size ; i++) {
            builder.append(' ');
        }
        return builder.toString();
    }

    /**
     * Convert Text to Bitmap Image
     * @param text
     * @param textSize
     * @param textColor
     * @return
     */
    public static Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.0f); // round
        int height = (int) (baseline + paint.descent() + 0.0f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

}
