import dbms.commands.Command;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner kbd = new Scanner(System.in);
        while(true) {
            System.out.print("Sql> ");
            String command = kbd.nextLine();
            try {
                Command cmd = Command.parse(command);
                System.out.println(cmd.execute());
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}