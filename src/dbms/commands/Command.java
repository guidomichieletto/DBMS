package dbms.commands;

public abstract class Command {
    protected Command() {}

    public static Command parse(String command) throws Exception {
        String[] tokens = command.toUpperCase().split(" ");

        if(tokens[0].equals("SELECT")) return new Select(command);
        if((tokens[0] + tokens[1]).equals("INSERTINTO")) return new Insert(command);

        throw new Exception("Unknown command: " + tokens[0]);
    }

    public String execute() { return "NO RESULT DEFINED"; }
}
