package com.datastax.astra.cmd;

/**
 * Parsing the command line in command and arguments.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ShellCommand {
    
    /** current command. */
    private String cmd;
    
    /** parameters. */
    private String[] args;

    /**
     * Command line entered by the user.
     *
     * @param uslserCommand
     *      user command
     */
    public ShellCommand(String userCommand) {
        String[] cmdLineParts = userCommand.split(" ");
        cmd  = cmdLineParts[0];
        
        if (cmdLineParts.length > 1) {
            args = new String[cmdLineParts.length-1];
            for (int i=0; i < cmdLineParts.length-1;i++) {
                args[i] = cmdLineParts[i+1];
            }
        }
    }
    
    /**
     * Getter accessor for attribute 'cmd'.
     *
     * @return
     *       current value of 'cmd'
     */
    public String getCmd() {
        return cmd;
    }

    /**
     * Setter accessor for attribute 'cmd'.
     * @param cmd
     * 		new value for 'cmd '
     */
    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    /**
     * Getter accessor for attribute 'args'.
     *
     * @return
     *       current value of 'args'
     */
    public String[] getArgs() {
        return args;
    }

    /**
     * Setter accessor for attribute 'args'.
     * @param args
     * 		new value for 'args '
     */
    public void setArgs(String[] args) {
        this.args = args;
    }
    
    

}
