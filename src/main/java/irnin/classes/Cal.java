package irnin.classes;

import java.net.InterfaceAddress;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Date;

public class Cal {
    private int month;
    private int year;
    private int day;

    public Cal()
    {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        month = Integer.parseInt(new SimpleDateFormat("M").format(cal.getTime()));
        year = Integer.parseInt(new SimpleDateFormat("yyyy").format(cal.getTime()));
    }

    public void subtractMonth() {
        month -= 1;

        if(month == 0) {
            month = 12;
            year -= 1;
        }
    }

    public void addMonth() {
        month += 1;

        if(month == 13) {
            month = 1;
            year += 1;
        }
    }
    public String getMonth()
    {
        String name = "";

        switch(month) {
            case 1:
                name = "Styczeń";
                break;
            case 2:
                name = "Luty";
                break;
            case 3:
                name = "Marzec";
                break;
            case 4:
                name = "Kwiecień";
                break;
            case 5:
                name = "Maj";
                break;
            case 6:
                name = "Czerwiec";
                break;
            case 7:
                name = "Lipiec";
                break;
            case 8:
                name = "Sierpień";
                break;
            case 9:
                name = "Wrzesień";
                break;
            case 10:
                name = "Październik";
                break;
            case 11:
                name = "Listopad";
                break;
            case 12:
                name = "Grudzień";
                break;
        }

        return name;
    }

    public String getYear() {
        return Integer.toString(year);
    }

    public int getLengthOfMonth() {
        YearMonth yearMonthObject = YearMonth.of(year, month);
        return yearMonthObject.lengthOfMonth();
    }

    public int getFirstDayNumber() throws ParseException {
        String[] days = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

        String dt = String.format("%d-%02d-01", year, month);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(sdf.parse(dt));

        int day = c.get(Calendar.DAY_OF_WEEK) - 1;

        if(day == 0) {
            day = 7;
        }

        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getDay() {
        return day;
    }

    public void clearDay() {
        this.day = -1;
    }
}
