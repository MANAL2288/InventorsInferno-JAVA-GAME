import java.util.Arrays;
import java.util.List; 

public class CommandParser {

    private List<String> moveWords = List.of("go", "move", "walk"); 
    private List<String> takeWords = List.of("take", "grab", "pick");
    private List<String> inspectWords = List.of("look", "check", "inspect");
    private List<String> useWords = Arrays.asList("use", "apply");

    /**
     * Parses an input string into a Command.
     * @param input User input
     * @return Parsed Command
     */
    public Command parse(String input) {
        if (input == null || input.isBlank())
            {
                return new Command(CmdType.UNKNOWN, null); 
        }

        String[] words = input.toLowerCase().trim().split("\\s+"); //!

        CmdType type = identifyType(words[0]);
        String target = extractTarget(words); 

        return new Command(type, target); 
    }

    /**
     * Identifies the command type from a word.
     * @param word First word of input
     * @return Command type
     */
    public  CmdType identifyType(String word)
    {
        if (moveWords.contains(word)) {
            return CmdType.MOVE; 
        } else if (takeWords.contains(word)) {
            return CmdType.TAKE;
        } else if (inspectWords.contains(word)) {
            return CmdType.INSPECT;
        } else if (useWords.contains(word)) {
            return CmdType.USE;
        } else if (word.equals("help")) {
            return CmdType.HELP; 
        } else if (word.equals("quit") || word.equals("exit")) {
            return CmdType.QUIT;
        }  else return CmdType.UNKNOWN; 

    }

    // extract target from remaining words 
    private String extractTarget(String[] words) {
        if (words.length <= 1) {
            return null; 
        }

        return String.join(" ", Arrays.copyOfRange(words, 1, words.length));
    }

    /**
     * Checks if input is valid.
     * @param input User input
     * @return true if the input corresponds to a known command
     */
    public boolean isValid(String input) { 
        Command cmd = parse(input);
        return cmd.getType() != CmdType.UNKNOWN;
    }

}
