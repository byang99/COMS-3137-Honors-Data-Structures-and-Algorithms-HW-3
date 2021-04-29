/*
 * Name: Brian Yang
 * UNI: by2289
 */


import java.io.*;
import java.util.*;

public class Problem1
{
    private File myFile;
    private BufferedReader inputFile;
    private FileReader read;


    public void setFile(String filename)
    {
        try
        {
            myFile = new File(filename);
            read = new FileReader(myFile);
            inputFile = new BufferedReader(read);
        }
        catch(FileNotFoundException e)
        {
            System.out.println("File " + filename + " not found...");
        }
    }


    public AvlTree<String> makeTree()
    {
        AvlTree<String> wordTree = new AvlTree<>();

        try
        {
            int lineNumber = 1;
            String line;
            String word;

            while( (line = inputFile.readLine()) != null )
            {
                //split line into array of words
                String[] arr = line.split("\\s+");

                //process each word in the line
                for (String s : arr)
                {
                    //remove punctuation and convert to lower case
                    word = s.replaceAll("\\p{Punct}", "").toLowerCase();

                    //insert into AvlTree
                    //methods of AvlTree take care of duplicates and adding unique line numbers
                    //if word's length is 0, then it was a punctuation, so don't add it
                    //to the tree
                    if(word.length() != 0)
                    {
                        wordTree.insert(word, lineNumber);
                    }
                }

                //move to next line
                lineNumber++;
            }
        }
        catch (IOException e)
        {
            System.out.println("IO Exception");
        }

        return wordTree;
    }


    public static void main(String [] args)
    {
        Problem1 test = new Problem1();

        test.setFile(args[0]);
        AvlTree<String> wordTree = test.makeTree();

        //print each unique word and line numbers
        //use printTree method
        wordTree.printTree();
    }
}
