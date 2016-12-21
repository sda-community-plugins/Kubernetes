package com.serena.air.plugin.kubernetes

import com.urbancode.air.CommandHelper
import com.urbancode.air.ExitCodeException;

class KubernetesHelper {
    String home
    File workdir
    boolean debug = false
    private String output
    private CommandHelper ch

    /**
     * Default constructor
     * @param workdir The working directory for the CommandHelper CLI
     * @param home The full path to the "ansible" script location
     */
    public KubernetesHelper(File workDir, String home) {
        if (home) {
            this.home = home
        } else {
            this.home = 'kubectl'
        }
        if (workdir) {
            this.workdir = workdir
        }
        ch = new CommandHelper(workdir)
        ArrayList args = []
        args << 'version'
        if (!runCommand("Checking '${this.home}' exists...", args)) {
            System.exit(1)
        }
    }

    String getHome() {
        return this.home
    }

    def setHome(String home) {
        this.home = home
    }

    String getOutput() {
        return this.output
    }

    def setOutput(String output) {
        this.output = output
    }

    boolean getDebug() {
        return this.debug
    }

    def setDebug(boolean debug) {
        this.debug = debug
    }

    /**
     * @param args An ArrayList of arguments to be executed by the command prompt
     * @return true if the command is run without any Standard Errors, false otherwise
     */
    public boolean runCommand(message, ArrayList args) {
        ArrayList cmd = [home]
        args.each() { arg ->
            cmd << arg
        }
        boolean status
        try {
            ch.runCommand("[INFO] ${message}", cmd) { Process proc ->
                def (String out, String err) = captureCommand(proc)
                setOutput(out)
                if (err) {
                    error(err)
                    status = false
                } else {
                    if (args.size() > 0) {
                        info("Command output:\n${out}")
                    }
                    status = true
                }
            }
        } catch (ExitCodeException ex) {
            error(ex.toString())
            return false
        }
        return status
    }

    /**
     * @param args The list of arguments to add the global configuration options to
     * @param url A url to specify for the Kubernetes server
     * @param username A username to specify for authentication to the Kubernetes server
     * @param password A password to specify for authentication to the kubernetes server
     * @param namespce A namespce to specify the scope on the Kubernetes server of the CLI request
     * @param globals A list of any other global arugments
     */
    public void setGlobals(ArrayList args, String url, String username, String password, String namespace, String globals) {
        if (url) {
            args << '--server=' + url
        }
        if (username) {
            args << '--username=' + username
        }
        if (password) {
            args << '--password=' + password
        }
        if (namespace) {
            args << '--namespace=' + namespace
        }
        if (globals) {
            globals.split("[\r\n]+").each() { global ->
                args << global
            }
        }
    }

    /**
     * @param args The list of arguments to add the global configuration options to
     * @param flags The list of flags to add to the args
     */
    public void setFlags(ArrayList args, String flags) {
        if (flags) {
            flags.split("[\r\n]+").each() { flag ->
                args << flag
            }
        }
    }

    /**
     * Check if a string is null, empty, or all whitespace
     * @param str The string whose value to check
     */
    public boolean isEmpty(String str) {
        return (str == null) || str.trim().isEmpty();
    }

    // ----------------------------------------

    /**
     * @param proc The process to retrieve the standard output and standard error from
     * @return An array containing the standard output and standard error of the process
     */
    private String[] captureCommand(Process proc) {
        StringBuffer out = new StringBuffer()
        StringBuffer err = new StringBuffer()
        proc.waitForProcessOutput(out, err)
        proc.out.close()
        return [out.toString(), err.toString()]
    }

    private debug(String message) {
        if (this.debug) {
            println("[DEBUG] ${message}")
        }
    }

    private info(String message) {
        println("[INFO] ${message}")
    }

    private error(String message) {
        println("[ERROR] ${message}")
    }
}