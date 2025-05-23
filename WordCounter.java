
import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordCounter {

    private static Scanner scanner = null;

    public static int processText(StringBuffer text, String stopword) throws InvalidStopwordException, TooSmallText {
        Pattern regex = Pattern.compile("[\\w']+");
        Matcher regexMatcher = regex.matcher(text);
        int totalcount = 0;
        int stopword_at = 0;
        while (regexMatcher.find()) {
            totalcount++;
            if (stopword != null && regexMatcher.group().equals(stopword)) {
                if (stopword_at == 0) {
                    stopword_at = totalcount;
                }
            }
        }
        if (totalcount < 5)
            throw new TooSmallText("Only found " + totalcount + " words.");
        if (stopword == null)
            return totalcount;
        if (!text.toString().contains(stopword))
            throw new InvalidStopwordException("Couldn't find stopword: " + stopword);
        return stopword_at;
    }

    public static StringBuffer processFile(String path) throws EmptyFileException {
        File file = new File(path);
        StringBuffer fileContents = new StringBuffer((int) file.length());
        Scanner file_scanner = null;
        while (file_scanner == null) {
            try {
                file_scanner = new Scanner(file);
            } catch (FileNotFoundException e) {
                scanner = new Scanner(System.in);
                file = new File(scanner.nextLine()); // Re-enter the path
            }
        }
        try {
            if (!file_scanner.hasNextLine())
                throw new EmptyFileException(path + " was empty");
            while (file_scanner.hasNextLine())
                fileContents.append(file_scanner.nextLine());
        } finally {
            file_scanner.close();
        }
        return fileContents;
    }

    public static void main(String args[])
            throws InvalidStopwordException, TooSmallText {
        int option;
        scanner = new Scanner(System.in);
        while (true) {
            try {
                option = scanner.nextInt(); // Input option 1 or 2
                if (option < 1 || option > 2)
                    throw new InputMismatchException();
                break;
            } catch (InputMismatchException e) {
                System.out.print("Invalid option. Choose again: ");
            }
        }
        String stopword = null;
        if (args.length > 1) {
            stopword = args[1];
        }
        StringBuffer sb;
        if (option == 1) {
            try {
                sb = processFile(args[0]);
            } catch (EmptyFileException e) {
                System.out.println(e);
                sb = new StringBuffer("");
            }
        } else {
            sb = new StringBuffer(args[0]);
        }
        try {
            System.out.println("Found " + processText(sb, stopword) + " words.");
        } catch (InvalidStopwordException e) {
            scanner = new Scanner(System.in);
            stopword = scanner.nextLine(); // Stopword not found, re-input
            System.out.println("Found " + processText(sb, stopword) + " words.");
        } catch (TooSmallText e) {
            System.out.println(e);
        }
    }
}
