/*
 * Name: Brian Yang
 * UNI: by2289
 */


import java.io.*;
import java.util.*;

public class Problem2
{
    /*

    Implement a spelling checker by using a hash table.
    Assume that the dictionary comes from two sources:
    an existing large dictionary and a second file containing
    a personal dictionary. Output all misspelled words
    and the line numbers in which they occur.


    Also, for each misspelled word, list any words in
    the dictionary that are obtainable by applying any of
    the following rules:

    a. Add one character.
    b. Remove one character.
    c. Exchange adjacent characters.

    */

    public File myFile, largeDictionary, personalDictionary ;
    public BufferedReader inputFile, inputLargeDictionary, inputPersonalDictionary;
    public FileReader readFile, readLargeDictionary, readPersonalDictionary;

    public QuadraticProbingHashTable<String> dictionary;

    public Problem2()
    {
        this.dictionary = new QuadraticProbingHashTable<>();
    }

    private static class MisspelledWord
    {
        String word;
        List<Integer> lineNumber;

        public MisspelledWord()
        {
            word = "";
            lineNumber = new LinkedList<>();
        }

        public MisspelledWord(String word)
        {
            this.word = word;
            lineNumber = new LinkedList<>();
        }

        public int length()
        {
            return word.length();
        }

        public String toString()
        {
            String lineNumbers = "";
            ListIterator<Integer> listIterator = lineNumber.listIterator();
            if(listIterator.hasNext())
            {
                lineNumbers += Integer.toString(listIterator.next());
            }
            while(listIterator.hasNext())
            {
                lineNumbers += ", " + Integer.toString(listIterator.next());
            }
            return "Word: " + word + "\tLine Numbers: " + lineNumbers;
        }
    }

    public void setFile(String filename, String largeDictionary, String personalDictionary)
    {
        try
        {
            myFile = new File(filename);
            readFile = new FileReader(myFile);
            inputFile = new BufferedReader(readFile);

            this.largeDictionary = new File(largeDictionary);
            readLargeDictionary = new FileReader(this.largeDictionary);
            inputLargeDictionary = new BufferedReader(readLargeDictionary);

            this.personalDictionary = new File(personalDictionary);
            readPersonalDictionary = new FileReader(this.personalDictionary);
            inputPersonalDictionary = new BufferedReader(readPersonalDictionary);

            this.dictionary = this.makeDictionary();
        }
        catch(FileNotFoundException e)
        {
            System.out.println("File " + filename + " not found...");
        }
    }

    public QuadraticProbingHashTable<String> makeDictionary()
    {
        QuadraticProbingHashTable<String> dictionaryHash = new QuadraticProbingHashTable<>();
        try
        {
            String line;
            String word;
            //hash the two dictionaries into one hash table

            //hash the large dictionary
            while( (line = inputLargeDictionary.readLine()) != null )
            {
                //split line into array of words - split on whitespace
                String[] arr = line.split("\\s+");

                //process each word in the line
                //add each word to the dictionary hash and dictionary list
                for (String s : arr)
                {
                    word = s.replaceAll("[\\p{Punct}&&[^']]", "").toLowerCase();
                    dictionaryHash.insert(word);
                }
            }

            //hash the personal dictionary
            while( (line = inputPersonalDictionary.readLine()) != null )
            {
                //split line into array of words - split on whitespace
                String[] arr = line.split("\\s+");

                //process each word in the line
                //add each word to the dictionary hash and dictionary list
                for (String s : arr)
                {
                    word = s.replaceAll("[\\p{Punct}&&[^']]", "").toLowerCase();
                    dictionaryHash.insert(word);
                }
            }
        }
        catch (IOException e)
        {
            System.out.println("IO Exception");
        }

        return dictionaryHash;
    }

    public List<MisspelledWord> getMisspelledWords()
    {

        //create list of misspelled word objects that includes the word and line number
        List<MisspelledWord> misspelledWords = new LinkedList<>();
        List<String> wrongWords = new LinkedList<>();

        try
        {
            //iterate through file of words line by line
            //and see if the hash table contains the words
            //if not, add it to list of misspelled words and line number
            int lineNumber = 1;
            String line;
            String word;
            while ((line = inputFile.readLine()) != null)
            {
                //split line into array of words
                String[] arr = line.split("\\s+");

                //process each word in the line
                for (String s : arr)
                {
                    word = s.replaceAll("[\\p{Punct}&&[^']]", "").toLowerCase();
                    //if punctutation is the only "word", skip, because then we are testing
                    //an empty string
                    // if dictionary does not have the word, then it is misspelled
                    //add to list of misspelled words
                    if (word.length() != 0 && !(dictionary.contains(word)))
                    {
                        //if misspelled word has already been seen, just add the line number
                        //to the linked list
                        if(wrongWords.contains(word))
                        {
                            ListIterator<MisspelledWord> iterator = misspelledWords.listIterator();
                            while(iterator.hasNext())
                            {
                                MisspelledWord match = iterator.next();
                                if(match.word.equals(word) && !match.lineNumber.contains(lineNumber))
                                {
                                    match.lineNumber.add(lineNumber);
                                    iterator.set(match);
                                    break;
                                }
                            }
                        }
                        else
                        {
                            //misspelled word is new, so add it to the list
                            wrongWords.add(word);

                            //create new MisspelledWord object to add to the list
                            MisspelledWord misspelledWord = new MisspelledWord(word);
                            misspelledWord.lineNumber.add(lineNumber);

                            //add new misspelled word and line number list to the list
                            misspelledWords.add(misspelledWord);
                        }
                    }
                }

                //move to next line
                lineNumber++;
            }
        }
        catch(IOException e)
        {
            System.out.println("IO Exception");
        }

        return misspelledWords;
    }

    public List<String> addOneCharacter(MisspelledWord misspelledWord)
    {
        List<String> addOneCharacterList = new LinkedList<>();

        String misspelled = misspelledWord.word;

        //check if inserting character in the word creates a word in the dictionary
        for(int i = 0; i <= misspelledWord.length(); i++)
        {
            for(int c = 0; c <= 255; c++)
            {
                String newWord = misspelled.substring(0, i) + (char)(c) + misspelled.substring(i);
                if(dictionary.contains(newWord) && !addOneCharacterList.contains(newWord))
                {
                    addOneCharacterList.add(newWord);
                }
            }
        }

        return addOneCharacterList;
    }

    public List<String> removeOneCharacter(MisspelledWord misspelledWord)
    {
        List<String> removeOneCharacterList = new LinkedList<>();

        String misspelled = misspelledWord.word;

        //check if removing character in the word creates a word in the dictionary
        for(int i = 0; i < misspelledWord.length(); i++)
        {
            String newWord = misspelled.substring(0, i) + misspelled.substring(i+1);
            if(dictionary.contains(newWord) && !removeOneCharacterList.contains(newWord))
            {
                removeOneCharacterList.add(newWord);
            }
        }
        return removeOneCharacterList;
    }

    public List<String> exchangeAdjacentCharacters(MisspelledWord misspelledWord)
    {
        List<String> exchangeAdjacentCharacterList = new LinkedList<>();

        String misspelled = misspelledWord.word;

        for(int i = 0; i < misspelled.length() - 1; i++)
        {
            char left = misspelled.charAt(i);
            char right = misspelled.charAt(i+1);
            String newWord = misspelled.substring(0,i) + right + left + misspelled.substring(i+2);
            if(dictionary.contains(newWord) && !exchangeAdjacentCharacterList.contains(newWord))
            {
                exchangeAdjacentCharacterList.add(newWord);
            }
        }

        return exchangeAdjacentCharacterList;
    }

    public List<String> getObtainableWords(MisspelledWord misspelledWord)
    {
        List<String> obtainableWords = new LinkedList<>();
        //don't need to check duplicates because each list contains unique
        // words of different lengths
        obtainableWords.addAll(this.exchangeAdjacentCharacters(misspelledWord));
        obtainableWords.addAll(this.removeOneCharacter(misspelledWord));
        obtainableWords.addAll(this.addOneCharacter(misspelledWord));
        return obtainableWords;
    }


    public static void main(String [] args)
    {
        try
        {
            Problem2 test = new Problem2();

            if (args.length < 3)
            {
                throw new ArrayIndexOutOfBoundsException("Not enough command line arguments.");
            }
            String file = args[0];
            String largeDictionary = args[1];
            String personalDictionary = args[2];
            test.setFile(file, largeDictionary, personalDictionary);

            List<MisspelledWord> misspelledWords = test.getMisspelledWords();
            if(misspelledWords.size() == 0)
            {
                System.out.println("No misspelled words.");
            }
            else
            {
                for (MisspelledWord currentWord : misspelledWords)
                {
                    //Print the misspelled word along with line number using toString method
                    System.out.println(currentWord);

                    //get list of obtainable words
                    List<String> obtainableWords = test.getObtainableWords(currentWord);

                    //Print out the obtainable words from this misspelled word by the 3 rules
                    System.out.println("Obtainable words from this misspelled word:");

                    Iterator<String> stringIterator = obtainableWords.iterator();
                    if (stringIterator.hasNext())
                    {
                        System.out.print(stringIterator.next());
                    }
                    while (stringIterator.hasNext())
                    {
                        System.out.print(", " + stringIterator.next());
                    }
                    System.out.println();
                    System.out.println();
                }
            }
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            System.out.println("ArrayIndexOutOfBoundsException caught.");
        }
    }
}