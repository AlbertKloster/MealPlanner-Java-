package mealplanner;

public enum Category {
    BREAKFAST("breakfast"),
    LUNCH("lunch"),
    DINNER("dinner");

    final String name;

    Category(String name) {
        this.name = name;
    }

    public static Category getCategory(String input) {
        for (Category category : Category.values()) {
            if (category.name.equals(input)) return category;
        }
        return null;
    }
}
