package mealplanner;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Repository {
    static final String DB_URL = "jdbc:postgresql://localhost:5432/meals_db";
    static final String USER = "postgres";
    static final String PASS = "1111";

    public static void init() {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            connection.setAutoCommit(true);

            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS meals (" +
                    "meal_id  INTEGER PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY," +
                    "category VARCHAR NOT NULL," +
                    "meal VARCHAR NOT NULL" +
                    ")");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS ingredients (" +
                    "ingredient_id  INTEGER PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY," +
                    "ingredient VARCHAR NOT NULL," +
                    "meal_id INTEGER NOT NULL" +
                    ")");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS plan (" +
                    "plan_id  INTEGER PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY," +
                    "meal VARCHAR NOT NULL," +
                    "category VARCHAR NOT NULL," +
                    "meal_id INTEGER NOT NULL" +
                    ")");

            statement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveMeal(Meal meal) {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            connection.setAutoCommit(true);

            PreparedStatement preparedStatementMeals = connection.prepareStatement("INSERT INTO meals (category, meal) VALUES (?, ?) RETURNING meal_id");
            preparedStatementMeals.setString(1, meal.category().name);
            preparedStatementMeals.setString(2, meal.name());
            ResultSet resultSet = preparedStatementMeals.executeQuery();

            if (resultSet.next()) {
                long mealId = resultSet.getLong("meal_id");
                PreparedStatement preparedStatementIngredients = connection.prepareStatement("INSERT INTO ingredients (ingredient, meal_id) values (?, ?)");
                for (String ingredient : meal.ingredients()) {
                    preparedStatementIngredients.setString(1, ingredient);
                    preparedStatementIngredients.setLong(2, mealId);
                    preparedStatementIngredients.executeUpdate();
                }
                preparedStatementIngredients.close();
            }

            preparedStatementMeals.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Meal> findAllMeal() {
        List<Meal> meals;
        try {
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            connection.setAutoCommit(true);

            Statement statement = connection.createStatement();
            ResultSet resultMeals = statement.executeQuery("SELECT * FROM meals");

            meals = getMeals(connection, resultMeals);
            resultMeals.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return meals;
    }

    public static List<Meal> findAllMealByCategory(Category category) {
        List<Meal> meals = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            connection.setAutoCommit(true);

            PreparedStatement preparedStatementMeal = connection.prepareStatement("SELECT * FROM meals WHERE category = ? ORDER BY meal_id");
            preparedStatementMeal.setString(1, category.name);

            ResultSet resultMeals = preparedStatementMeal.executeQuery();

            if (resultMeals == null) return meals;

            meals = getMeals(connection, resultMeals);
            resultMeals.close();
            preparedStatementMeal.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return meals;
    }

    private static List<Meal> getMeals(Connection connection, ResultSet resultMeals) throws SQLException {
        List<Meal> meals = new ArrayList<>();
        while (resultMeals.next()) {
            long currentMealId = resultMeals.getLong("meal_id");
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT DISTINCT * FROM ingredients WHERE meal_id = ?");
            preparedStatement.setLong(1, currentMealId);
            ResultSet resultIngredients = preparedStatement.executeQuery();

            meals.add(new Meal(
                    currentMealId,
                    Category.getCategory(resultMeals.getString("category")),
                    resultMeals.getString("meal"),
                    getIngredientsByMealId(resultIngredients, resultMeals.getLong("meal_id"))));
            resultIngredients.close();
            preparedStatement.close();
        }
        return meals;
    }

    private static String[] getIngredientsByMealId(ResultSet resultIngredients, long mealId) {
        List<String> ingredients = new ArrayList<>();
        try {
            while (resultIngredients.next()) {
                if (resultIngredients.getLong("meal_id") == mealId)
                    ingredients.add(resultIngredients.getString("ingredient"));
            }
            return ingredients.toArray(new String[0]);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static void deletePlane() {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            connection.setAutoCommit(true);

            Statement statement = connection.createStatement();
            statement.executeUpdate("TRUNCATE TABLE plan");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void savePlan(Meal meal) {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            connection.setAutoCommit(true);

            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO plan (category, meal, meal_id) VALUES (?, ?, ?)");
            preparedStatement.setString(1, meal.category().name);
            preparedStatement.setString(2, meal.name());
            preparedStatement.setLong(3, (meal.mealId()));
            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Meal> getPlan() {
        List<Meal> meals;
        try {
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            connection.setAutoCommit(true);

            Statement statement = connection.createStatement();
            ResultSet resultMeals = statement.executeQuery("SELECT * FROM plan");

            meals = getMeals(connection, resultMeals);
            resultMeals.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return meals;
    }
}
