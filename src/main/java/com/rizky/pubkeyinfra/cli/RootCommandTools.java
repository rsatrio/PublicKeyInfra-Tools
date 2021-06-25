package com.rizky.pubkeyinfra.cli;

import java.util.concurrent.Callable;

import picocli.CommandLine.Command;

@Command(name = "",mixinStandardHelpOptions = true,
description = "",
subcommands = {OcspCheck.class,ConvertCert.class})
public class RootCommandTools implements Callable<Integer> {

    
    public Integer call() throws Exception {
        // TODO Auto-generated method stub
        System.out.println("use subcommand: OcspCheck or ConvertCert");
        System.exit(0);
        return 0;
    }

    
}
