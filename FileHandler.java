package mealplanner;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class FileHandler {
    public static void saveShoppingList(List<Meal> plan, String fileName) {
        try (FileWriter fileWriter = new FileWriter(fileName);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {

            List<String> allIngredients = plan.stream().map(Meal::ingredients).flatMap(Stream::of).toList();
            Set<String> ingredients = new HashSet<>(allIngredients);
            ingredients.forEach(ingredient -> {
                long count = allIngredients.stream().filter(i -> i.equals(ingredient)).count();
                if (count == 1) {
                    printWriter.println(ingredient);
                } else {
                    printWriter.printf("%s x%d\n", ingredient, count);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
