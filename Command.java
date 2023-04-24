package mealplanner;

public enum Command {
    ADD("add"),
    SHOW("show"),
    PLAN("plan"),
    SAVE("save"),
    EXIT("exit"),
    UNKNOWN("");

    final String name;

    Command(String name) {
        this.name = name;
    }

    public static Command getCommand(String input) {
        for (Command command : Command.values()) {
            if (command.name.equals(input)) return command;
        }
        return UNKNOWN;
    }
}
