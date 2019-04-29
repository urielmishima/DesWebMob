package br.usjt.ciclodevidagpsemapas;

import com.google.gson.annotations.SerializedName;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class Weather {

    @SerializedName("dt")
    private String dayOfWeek;

    @SerializedName("temp_min")
    private String minTemp;

    @SerializedName("temp_max")
    private String maxTemp;

    @SerializedName("humidity")
    private String humidity;

    @SerializedName("description")
    private String description;

    @SerializedName("icon")
    private String iconURL;

    private final NumberFormat numberFormat;

    public Weather (long timeStamp, double minTemp, double maxTemp, double humidity, String
            description, String iconName){
        numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);
        this.setDayOfWeek(timeStamp);
        this.setMinTemp(minTemp);
        this.setMaxTemp(maxTemp);
        this.setHumidity(humidity);
        this.setDescription(description);
        this.setIconURL("http://openweathermap.org/img/w/" + iconName + ".png");
    }

    public Weather(){
        numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);
    }

    private static String convertTimeStampToDay (long timeStamp){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp * 1000);
        TimeZone tz = TimeZone.getDefault();
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        return new SimpleDateFormat("EEEE").format(calendar.getTime());
    }


    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(long timestamp) {
        this.dayOfWeek = convertTimeStampToDay (timestamp);
    }

    public String getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(double minTemp) {
        this.minTemp = numberFormat.format(minTemp) + "\u00B0C";
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(double maxTemp) {
        this.maxTemp = numberFormat.format(maxTemp) + "\u00B0C";
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = NumberFormat.getPercentInstance().format(humidity / 100.0);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }
}
