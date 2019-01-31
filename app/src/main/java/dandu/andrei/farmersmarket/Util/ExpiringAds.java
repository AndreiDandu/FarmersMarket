package dandu.andrei.farmersmarket.Util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import dandu.andrei.farmersmarket.Ad.Ad;

public class ExpiringAds {
    private static ArrayList<Ad> adsWithTwoDays = new ArrayList<>();
    private static ArrayList<Ad> adsWithOneDay = new ArrayList<>();
    private static ArrayList<Ad> adsExpired = new ArrayList<>();


    public static  int getAdExpiringDate(Ad ad) {
        Date parse = null;
        Date dateNow = new Date();
        long diff = 0;

            String timestamp = ad.getTimestamp();
            if (timestamp != null && !timestamp.equals("")) {
                DateFormat dateInstance = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM);
                try {
                    parse = dateInstance.parse(timestamp);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (parse.before(dateNow)) {
                    long abs = Math.abs(dateNow.getTime() - parse.getTime());
                    diff = TimeUnit.DAYS.convert(abs, TimeUnit.MILLISECONDS);
                } else {
                    return -1;
                }
                if (diff == 2) {
                    //mai doua zile
                    return 2;
                }
                if (diff == 1) {
                    // mai o zi
                    return 1;
                }
                if(diff == 0){
                    //expira azi
                    return 0;
                }
            }

        return 4;
    }
    //first to call doar cele expirate ca sa fie dat remove din view
    public static void getExpiringAds(ArrayList<Ad> adList){
        for (Ad ad:adList) {
            int days = getAdExpiringDate(ad);
            if (days == 2){
                //low priority
                adsWithTwoDays.add(ad);
            }
            if(days == 1){
                //high priority
                adsWithOneDay.add(ad);
            }
            if(days == -1){
                adsExpired.add(ad);
            }
        }
    }
    private static ArrayList<Ad> getPriority(){
        if(adsWithOneDay.isEmpty()){
            return adsWithTwoDays;
        }
        return adsWithOneDay;
    }
    public static void getAlertDialog(Context context) {
        ArrayList<Ad> priority = getPriority();
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle("Atentie!")
                .setMessage("Ai anunturi care expira \n Vrei sa le actualizezi?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
