import dbms.commands.Command;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner kbd = new Scanner(System.in);
        while(true) {
            System.out.print("Sql> ");
            String command = kbd.nextLine();
            try {
                System.out.println(Command.parse(command).execute());
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}