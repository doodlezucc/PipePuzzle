package pipepuzzle;

public class Obfuscate {
    static String source="ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789#.:- ";
    static String target="Q5A8ZWS0XE#DC6RFVT.9GBY4HNU3J2MI1KO7LP:- ";

    public static String obfuscate(String s) {
        s = s.toUpperCase();
        char[] result= new char[s.length()];
        for (int i=0;i<s.length();i++) {
            char c=s.charAt(i);
            int index=source.indexOf(c);
            result[i]=target.charAt(index);
        }

        return new String(result);
    }

    public static String unobfuscate(String s) {
        s = s.toUpperCase();
        char[] result= new char[s.length()];
        for (int i=0;i<s.length();i++) {
            char c=s.charAt(i);
            int index=target.indexOf(c);
            result[i]=source.charAt(index);
        }

        return new String(result);
    }
}