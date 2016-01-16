/*
    small logging class,
    just for development and error message handling
 */
class Log {
    public static void info(String s) {
        System.err.println("[Info]  " + s);
    }
    public static void error(String s) {
        System.err.println("[Error] " + s);
    }
}