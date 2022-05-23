package com.datastax.astra;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;

public class TestCommandLines {
    
    @Test
    public void showHelp() {
        
    }
    
    @Test
    public void testCommandLines() throws ParseException {
        Options opt =  new Options()
                .addOption(Option
                        .builder("org").longOpt("configuration")
                        .hasArg()
                        .build())
                .addOption(Option
                        .builder("t").longOpt("token")
                        .build());
        
        String cmdLine = "ls db ss -t -org org";
        
        // Read Command line
        String[] cmdLineParts = cmdLine.toLowerCase().split(" ");
        //String cmd = cmdLineParts[0];
        String[] args = null;
        if (cmdLineParts.length > 1) {
            args = new String[cmdLineParts.length-1];
            for (int i=0; i < cmdLineParts.length-1;i++) {
                args[i] = cmdLineParts[i+1];
            }
        }
        
        CommandLine cli = new DefaultParser().parse(opt, args);
        System.out.println(cli.getArgList());
        
        //String aa = "🅓🅐🅣🅐🅢🅣🅐🅧 🅓🅔🅥🅢";
        //String bb = "🅳🅰🆃🅰🆂🆃🅰🆇 🅳🅴🆅🆂";
        
    }
}
