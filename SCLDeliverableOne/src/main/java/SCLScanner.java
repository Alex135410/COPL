import java.io.*;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class SCLScanner {


    private static final String[] KEYWORDS = {
            "import", "global", "constants", "function", "type", "byte", "define", "is", "of", "call",
            "integer", "float", "set", "exit", "declarations", "endfun", "begin", "if", "endif", "else",
            "display", "double", "input", "return", "long", "short", "pointer", "NodeType", "destroy",
            "structures", "variables", "while", "struct", "endstruct", "endwhile"
    };

    private static final String[] OPERATORS = {
            "+", "-", "*", "/", ">", "<", "=", "(", ")", "band", "bor", "bxor"
    };


    private enum TokenType {
        KEYWORD, IDENTIFIER, OPERATOR, CONSTANT, SPECIAL_CHAR
    }


    private static class Token {
        String value;
        TokenType type;

        Token(String value, TokenType type) {
            this.value = value;
            this.type = type;
        }

        @Override
        public String toString() {
            return "{ value: " + value + ", type: " + type + " }";
        }
    }


    public static List<Token> scan(String input) {
        List<Token> tokens = new ArrayList<>();
        String[] words = input.split("\\s+|(?<=[(){}\\[\\]=+\\-*/<>,;])|(?=[(){}\\[\\]=+\\-*/<>,;])");

        for (String word : words) {
            if (word.isEmpty()) continue;

            if (Arrays.asList(KEYWORDS).contains(word)) {
                tokens.add(new Token(word, TokenType.KEYWORD));
            } else if (Arrays.asList(OPERATORS).contains(word)) {
                tokens.add(new Token(word, TokenType.OPERATOR));
            } else if (word.matches("\\d+(\\.\\d+)?")) { // Check for constants (numbers)
                tokens.add(new Token(word, TokenType.CONSTANT));
            } else if (word.matches("[a-zA-Z_][a-zA-Z0-9_]*")) { // Check for identifiers
                tokens.add(new Token(word, TokenType.IDENTIFIER));
            } else {
                tokens.add(new Token(word, TokenType.SPECIAL_CHAR)); // Special characters
            }
        }

        return tokens;
    }


    public static String tokensToJson(List<Token> tokens) {
        JSONArray jsonArray = new JSONArray();
        for (Token token : tokens) {
            JSONObject jsonToken = new JSONObject();
            jsonToken.put("value", token.value);
            jsonToken.put("type", token.type.toString());
            jsonArray.put(jsonToken);
        }
        return jsonArray.toString(2);
    }


    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java SCLScanner <input_file>");
            return;
        }

        String inputFile = args[0];
        String outputFile = "tokens.json";

        try {

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            StringBuilder sourceCode = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sourceCode.append(line).append("\n");
            }
            reader.close();


            List<Token> tokens = scan(sourceCode.toString());


            System.out.println("Tokens:");
            for (Token token : tokens) {
                System.out.println(token);
            }


            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write(tokensToJson(tokens));
            writer.close();

            System.out.println("Tokens written to " + outputFile);

        } catch (IOException e) {
            System.err.println("Error reading or writing file: " + e.getMessage());
        }
    }
}

