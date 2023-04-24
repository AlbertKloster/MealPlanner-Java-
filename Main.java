package mealplanner;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner SCANNER = new Scanner(System.in);


    public static void main(String[] args) {
        Repository.init();

        boolean exit = false;
        while (!exit) {
            System.out.println("What would you like to do (add, show, plan, save, exit)?");
            switch (Command.getCommand(SCANNER.nextLine())) {
                case ADD -> add();
                case SHOW -> show();
                case PLAN -> plan();
                case SAVE -> save();
                case EXIT -> exit = true;
                case UNKNOWN -> {}
            }
        }
        System.out.println("Bye!");
    }

    private static void add() {
        System.out.println("Which meal do you want to add (breakfast, lunch, dinner)?");
        Category category = null;
        while (category == null) {
            category = Category.getCategory(SCANNER.nextLine());
            if (category == null)
                System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
        }
        System.out.println("Input the meal's name:");
        String name = "";
        while (isWrongFormat(name)) {
            name = SCANNER.nextLine();
            if (isWrongFormat(name))
                System.out.println("Wrong format. Use letters only!");
        }
        System.out.println("Input the ingredients:");
        String input = "";
        while (isInvalidIngredients(input)) {
            input = SCANNER.nextLine();
            if (isInvalidIngredients(input))
                System.out.println("Wrong format. Use letters only!");
        }

        String[] ingredients = input.split("\\s*,\\s*");
        Repository.saveMeal(new Meal(0, category, name, ingredients));
        System.out.println("The meal has been added!");
    }

    private static boolean isInvalidIngredients(String input) {
        return !input.matches("([a-zA-Z][a-zA-Z ]*,\\s*)*[a-zA-Z][a-zA-Z ]+");
    }

    private static boolean isWrongFormat(String name) {
        if (name.isBlank()) return true;
        return !name.matches("[a-zA-Z]+\\s*[a-zA-Z]*");
    }

    private static void show() {
        List<Meal> meals = Repository.findAllMeal();
        if (meals.isEmpty()) {
            System.out.println("No meals saved. Add a meal first.");
            return;
        }

        System.out.println("Which category do you want to print (breakfast, lunch, dinner)?");
        Category category = null;
        while (category == null) {
            category = Category.getCategory(SCANNER.nextLine());
            if (category == null)
                System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
        }
        meals = Repository.findAllMealByCategory(category);

        if (meals.isEmpty()) {
            System.out.println("No meals found.");
            return;
        }

        System.out.printf("Category: %s\n", category.name);
        meals.forEach(System.out::println);
        System.out.println();
    }

    private static void plan() {
        Repository.deletePlane();
        for (Week dayOfWeek : Week.values()) {
            System.out.println(dayOfWeek.name);
            List<Meal> meals;
            for (Category category : Category.values()) {
                meals = Repository.findAllMealByCategory(category);
                meals.stream().map(Meal::name).sorted().forEach(System.out::println);
                System.out.printf("Choose the %s for %s from the list above:\n", category.name, dayOfWeek.name);
                Meal meal = null;
                while (meal == null) {
                    String name = SCANNER.nextLine();
                    meal = getMealByName(meals, name);
                    if (meal == null)
                        System.out.println("This meal doesn’t exist. Choose a meal from the list above.");
                }
                Repository.savePlan(meal);
            }
            System.out.printf("Yeah! We planned the meals for %s.\n\n", dayOfWeek.name);
        }
        printPlan();
    }

    private static void save() {
        List<Meal> plan = Repository.getPlan();
        if (plan.isEmpty()) {
            System.out.println("Unable to save. Plan your meals first.");
            return;
        }

        System.out.println("Input a filename:");
        String fileName = SCANNER.nextLine();
        FileHandler.saveShoppingList(plan, fileName);
        System.out.println("Saved!");
    }

    private static Meal getMealByName(List<Meal> meals, String name) {
        return meals.stream().filter(meal -> meal.name().equals(name)).findAny().orElse(null);
    }

    private static void printPlan() {
        List<Meal> plan = Repository.getPlan();
        for (int i = 0; i < plan.size(); i += 3) {
            System.out.println(Week.values()[i / 3].name);
            System.out.printf("Breakfast: %s\n", plan.get(i).name());
            System.out.printf("Lunch: %s\n", plan.get(i + 1).name());
            System.out.printf("Dinner: %s\n\n", plan.get(i + 2).name());
        }
    }
}