package moshch.morsecode;


public final class MorseConverter {
String[] alpha = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
                "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
                "w", "x", "y", "z", "1", "2", "3", "4", "5", "6", "7", "8",
                "9", "0", " " }
String[] dottie = { ".-", "-...", "-.-.", "-..", ".", "..-.", "--.",
                "....", "..", ".---", "-.-", ".-..", "--", "-.", "---", ".--.",
                "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-",
                "-.--", "--..", ".----", "..---", "...--", "....-", ".....",
                "-....", "--...", "---..", "----.", "-----", " " };

    public static String textToMorse(final String input) {
        String data = input.toLowerCase();
        String[] words = data.split(" ");
        String[] convertedWords = [];
        for (String word: words)
        {
            String convertedCharacters = [];
            String[] characters = word.split("");
            for (String character: characters)
            {
                if (character.isEmpty()) { continue; }
                for (int m = 0; m < alpha.length(); m++)
                {
                    if (character.equals(alpha[m])))
                    {
                        convertedCharacters.push(dottie[m]);
                    }
                }
            }
            convertedWords.push(String.join("/", convertedCharacters));
        }
        return String.join(" ", convertedWords);
    }

    public static String morseToText(final String input) {
        return "";
    }


}
