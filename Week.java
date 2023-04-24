package mealplanner;

public enum Week {
    MONDAY("Monday"),
    TUESDAY("Tuesday"),
    WEDNESDAY("Wednesday"),
    THURSDAY("Thursday"),
    FRIDAY("Friday"),
    SATURDAY("Saturday"),
    SUNDAY("Sunday");

    final String name;

    Week(String name) {
        this.name = name;
    }

    public static Week getDayOfWeek(String input) {
        for (Week dayOfWeek : Week.values()) {
            if (dayOfWeek.name.equals(input)) return dayOfWeek;
        }
        return null;
    }
}
