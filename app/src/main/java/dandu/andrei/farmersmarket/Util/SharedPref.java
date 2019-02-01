package dandu.andrei.farmersmarket.Util;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SharedPref  {
    public static final String MY_PREFS_NAME ="MyPrefFile";
    public static final String DATE_KEY ="date_key";
    public SharedPreferences preference;

    public SharedPref(SharedPreferences pref) {
        this.preference = pref;
    }


    public  String getDateNow() {
        DateFormat simpleDateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM);
        return simpleDateFormat.format(new Date());
    }
    public String getDatePreference() {
//        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        return preference.getString(DATE_KEY, null);
    }

    public void setDatePreference(String datePreference) {
        final SharedPreferences.Editor editor = preference.edit();
        editor.putString(DATE_KEY, datePreference);
        editor.apply();
    }
    public  boolean showMessageOnceDay() {
        String datePreference = getDatePreference();
        if (datePreference == null) {
            setDatePreference(getDateNow());
            return true;
        } else {
            if (!datePreference.equals(getDateNow())) {
                setDatePreference(getDateNow());
                return true;
            }
        }
        return false;
    }
}
