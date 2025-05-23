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
        int count = 0;
        Matcher regexMatcher = regex.matcher(text);
        while (regexMatcher.find()) {
            count++;
        }
        if (count < 5)
            throw new TooSmallText("Only found " + count + " words.");
        if (stopword != null && !text.toString().contains(stopword))
            throw new InvalidStopwordException("Couldn't find stopword: " + stopword);
        int finalcount = 0;
        Matcher anotherRegexMatcher = regex.matcher(text);
        while (anotherRegexMatcher.find()) {
            finalcount++;
            if (stopword != null && anotherRegexMatcher.group().equals(stopword))
                break;
        }
        return finalcount;
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
                System.out.println("Re-enter the filename: ");
                file = new File(scanner.nextLine());
            }
        }
        if (!file_scanner.hasNextLine())
            throw new EmptyFileException(path + " was empty");
        try {
            while (file_scanner.hasNextLine())
                fileContents.append(file_scanner.nextLine());
        } finally {
            file_scanner.close();
        }
        return fileContents;
    }

    public static void main(String args[]) throws InvalidStopwordException, TooSmallText {
        int option;
        scanner = new Scanner(System.in);
        while (true) {
            try {
                option = scanner.nextInt();
                if (option < 1 || option > 2)
                    throw new InputMismatchException();
                break;
            } catch (InputMismatchException e) {
                System.out.print("Invalid option. Choose again: ");
            }
        }

        String stopword = null;
        if (args.length > 1)
            stopword = args[1];
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
            stopword = scanner.nextLine();
        } catch (TooSmallText e) {
            System.out.println(e);
        }
    }
}