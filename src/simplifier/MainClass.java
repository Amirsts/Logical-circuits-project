package simplifier;

import java.util.*;

// Entry point of the program. Handles user input, runs the simplifier, and prints the final simplified logic expression.
public class MainClass {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

// Welcome message
        System.out.println("Welcome to the logic circuit simplification program!");
        System.out.println("Enter the minterms with a space:");

// Read input line and split by whitespace
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            System.out.println("The input is empty!");
            return;
        }

        String[] tokens = input.split("\\s+");
        List<Integer> minterms = new ArrayList<>();

// Parse and validate input tokens
        try {
            for (String token : tokens) {
                int m = Integer.parseInt(token);
                if (m < 0) throw new NumberFormatException();
                minterms.add(m);
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter only positive integers!");
            return;
        }

// Instantiate the simplifier (QM)
        Simplifier simplifier = new QuineMcCluskeySimplifier();
        List<Term> simplified = simplifier.simplify(minterms);

// Convert simplified terms to string expression
        String expression = ExpressionBuilder.buildSOP(simplified);

// Display result
        System.out.println("Simplified expression:");
        System.out.println(expression);
    }
}
