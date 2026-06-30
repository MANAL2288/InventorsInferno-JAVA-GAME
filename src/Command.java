/**
 * Represents a parsed player command.
 */
public class Command {
    private CmdType type;
    private String target;

    /**
     * Creates a new command.
     * @param type Command type
     * @param target Command target (item, direction, etc.)
     */
    public Command(CmdType type, String target) {
        this.type = type;
        this.target = target;
    }

    /** @return The type of the command */
    public CmdType getType() { return type; }

    /** @return The target of the command */
    public String getTarget(){ return target; }

    /** @return true if the command is valid */
    public boolean isValid() { return type != CmdType.UNKNOWN; }
}
