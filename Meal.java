package mealplanner;

public record Meal(long mealId, Category category, String name, String[] ingredients) {

    @Override
    public String toString() {
        return String.format("""
                        
                        Name: %s
                        Ingredients:
                        %s""",
                name,
                String.join("\n", ingredients)
        );
    }
}
