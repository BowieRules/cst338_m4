/**
 * Group Assignment for Module 4: Optical Barcode Readers and Writers
 * This assignment combines 2D arrays, interfaces (including Cloneable),
 * and optical scanning and pattern recognition. 
 * 
 * by Cohort 12, Pyrocumulus team:
 * Joshan Dillon, Trenton Fengel, Tabitha Micheels, Marianna Petrovich
 * 
 * 11/24/2020
 */

import java.util.*; 

class Assig4
{
   public static void main(String[] args) 
   {      
   
      String[] sImageIn =
      {
         "                                               ",
         "                                               ",
         "                                               ",
         "     * * * * * * * * * * * * * * * * * * * * * ",
         "     *                                       * ",
         "     ****** **** ****** ******* ** *** *****   ",
         "     *     *    ****************************** ",
         "     * **    * *        **  *    * * *   *     ",
         "     *   *    *  *****    *   * *   *  **  *** ",
         "     *  **     * *** **   **  *    **  ***  *  ",
         "     ***  * **   **  *   ****    *  *  ** * ** ",
         "     *****  ***  *  * *   ** ** **  *   * *    ",
         "     ***************************************** ",  
         "                                               ",
         "                                               ",
         "                                               "
      };
   
      String[] sImageIn_2 =
      {
            "                                          ",
            "                                          ",
            "* * * * * * * * * * * * * * * * * * *     ",
            "*                                    *    ",
            "**** *** **   ***** ****   *********      ",
            "* ************ ************ **********    ",
            "** *      *    *  * * *         * *       ",
            "***   *  *           * **    *      **    ",
            "* ** * *  *   * * * **  *   ***   ***     ",
            "* *           **    *****  *   **   **    ",
            "****  *  * *  * **  ** *   ** *  * *      ",
            "**************************************    ",
            "                                          ",
            "                                          ",
            "                                          ",
            "                                          "
      };
          
      BarcodeImage bc = new BarcodeImage(sImageIn);
      DataMatrix dm = new DataMatrix(bc);

      // First secret message
      dm.translateImageToText();
      dm.displayTextToConsole();
      dm.displayImageToConsole();

      // second secret message
      bc = new BarcodeImage(sImageIn_2);
      dm.scan(bc);
      dm.translateImageToText();
      dm.displayTextToConsole();
      dm.displayImageToConsole();
      
      // create your own message
      dm.readText("What a great resume builder this is!");
      dm.generateImageFromText();
      dm.displayTextToConsole();
      dm.displayImageToConsole();
   }   
}
// any class that implements BarcodeIO is expected to store some version
// of an image and some version of the text associated with that image
interface BarcodeIO {   
   public boolean scan(BarcodeImage bc);
   public boolean readText(String text);
   public boolean generateImageFromText();
   public boolean translateImageToText();
   public void displayTextToConsole();
   public void displayImageToConsole();
}

// conceptual data and methods for 2D pattern of a bar code
// blank = false, * = true
class BarcodeImage implements Cloneable
{
   public static final int MAX_HEIGHT = 30, MAX_WIDTH = 65;
   // image storage
   private boolean[][] imageData;

   //Default Constructor
   public BarcodeImage()
   {
      imageData = new boolean[MAX_HEIGHT][MAX_WIDTH];

      // stuff array with blanks (false)
      for (int row = 0; row < MAX_HEIGHT; row++)
      {
         for (int col = 0; col < MAX_WIDTH; col++)
         {
            imageData[row][col] = false;
         }
      }
   }
   
   // Constructor to convert from string
   public BarcodeImage(String[] strData)
   {
      // call default constructor to build a new storage
      this();
      
      // validate size
      if (checkSize(strData))
      {         
         for (int row = 0; row < strData.length; row++)
         {
            for (int col = 0; col < strData[row].length(); col++)
            {
               if (strData[row].charAt(col) == '*')
               {
                  imageData[MAX_HEIGHT - strData.length + row][col] = true;
               }
            }
         }      
      }
   }
   
   boolean getPixel(int row, int col)
   {
      // reverse row number here
      if(row >= MAX_HEIGHT || col >= MAX_WIDTH)
      {
         return false;
      }
      else
      {
         return imageData[row][col];
      }
   }
   
   boolean setPixel(int row, int col, boolean value)
   {
      if(row >= MAX_HEIGHT || col >= MAX_WIDTH)
      {
         return false;
      }
      else
      {
         imageData[row][col] = value;
         return true;
      }
   }
   
   // validates input for null, empty strings or bigger size
   private boolean checkSize(String[] data)
   {
      if (data == null || data.length == 0 || data.length > MAX_HEIGHT )
      {
         return false;
      }
      
      //for each enhanced loop
      for (String element : data)
      {
         if (element == null || element.length() == 0 ||
               element.length() > MAX_WIDTH)
         {
            return false;
         }
      }
      return true;
   }
   
   // for debugging purposes
   public void displayToConsole()
   {
      for(int row = 0; row < MAX_HEIGHT; row++)
      {
         for(int col = 0; col < MAX_WIDTH; col++)
         {
            if(imageData[row][col])
            {
               System.out.print('*');
            }
            else
            {
               System.out.print(' ');
            }
         }
         System.out.println();
      }
   }
   
   //Implements the clonable interface
   public BarcodeImage clone() throws CloneNotSupportedException
   {
      BarcodeImage clonedCopy = new BarcodeImage();
      for (int row = 0; row < MAX_HEIGHT; row++)
      {
         for (int col = 0; col < MAX_WIDTH; col++)
         {
            clonedCopy.setPixel(row, col, imageData[row][col]);
         }
      }
      return clonedCopy;     
   }  
}

class DataMatrix implements BarcodeIO
{
   // constants
   public static final char BLACK_CHAR = '*', WHITE_CHAR = ' ';
   
   // single internal copy of image
   private BarcodeImage image;
 
   // single internal copy of text
   private String text;
   
   // computed from the "spine" of the image
   private int actualWidth, actualHeight; 
   
   // Default constructor for empty, but non-null, image and text value
   public DataMatrix()
   {
      this.image = new BarcodeImage();
      this.text = "undefined"; 
      this.actualWidth = 0; 
      this.actualHeight = 0;
   }
   
   // sets the image but leaves default text
   public DataMatrix(BarcodeImage image)
   {
      if (!scan(image))
      {
         this.image = new BarcodeImage();
         this.text = "undefined"; 
         this.actualWidth = 0; 
         this.actualHeight = 0;
      }
   }
   
   // mutator for image
   public boolean scan(BarcodeImage image)
   {
      try
      {
         this.image = image.clone();
      }
      catch (CloneNotSupportedException e)
      {
         return false;
      }
      
      //cleans and sets image to bottom left corner
      cleanImage();
     
      // set actualWidth and actualHeight
      actualHeight = computeSignalHeight();
      actualWidth = computeSignalWidth();
      return true; 
   }
   
   public boolean translateImageToText()
   {   
      String strText = "";
      // ignore first column, start from 1
      for(int i = 1; i < actualWidth - 1; i++)
      {
         // read each column individually
         strText += readCharFromCol(i);
      }
      //set the text value.
      this.text = strText;
      
      return true;
   }

   private char readCharFromCol(int col)
   {
      // multipliers for each value in the col
      int thisSum = 0;
      // ignore first row and last
      // as closed and open limitation Lines
      for(int row = 1; row < actualHeight - 1; row++)
      {
         if(image.getPixel(image.MAX_HEIGHT - row - 1, col))
         {
            thisSum += Math.pow(2, row - 1);
         }
      }
      return (char)thisSum;
   }
   
   // error correction in a real class design
   private void cleanImage()
   {
      moveImageToLowerLeft();
   }
   
   
   private void moveImageToLowerLeft()
   {
      // find offset
      int horizontalOffset = 0;
      int verticalOffset = 0;
      
      // start at the bottom row
      for (int row = image.MAX_HEIGHT-1; row >= 0; row--)
      {        
         for (int col = 0; col < image.MAX_WIDTH; col++)
         {
            // measure by Closed Limitation Line
            if (image.getPixel(row, col))
            {
               horizontalOffset = col;
               verticalOffset = image.MAX_HEIGHT - row - 1;
               break;
            }
         }
         if (verticalOffset > 0 || horizontalOffset > 0)
         {
            break;
         }
      }         
      shiftImageLeft(horizontalOffset);
      shiftImageDown(verticalOffset);
   }
   
   // push signal down by offset
   private void shiftImageDown(int offset)
   {
      for (int row = image.MAX_HEIGHT - (offset + 1); row >= 0; row--)
      {
         for (int col = 0; col < image.MAX_WIDTH; col++)
         {
            image.setPixel(row + offset, col, image.getPixel(row, col));
         }
      }
   }
   
   // push signal left by offset
   private void shiftImageLeft(int offset)
   {
      for (int row = 0; row < image.MAX_HEIGHT; row++)
      {
         for (int col = offset; col < image.MAX_WIDTH; col++)
         {
            image.setPixel(row, col - offset, image.getPixel(row, col));
         }
      }
   }
   
   // accessors, no mutators
   public int getActualWidth()
   {
      return actualWidth;
   }
   
   public int getActualHeight()
   {
      return actualHeight;
   }
   
   // assumes a cleaned image in the lower-left corner
   private int computeSignalWidth()
   {
      // use spine to figure out width
      int width = 0;
      for (int col = 0; col < image.MAX_WIDTH; col++)
      {
         // measure by Closed Limitation Line
         if (image.getPixel(image.MAX_HEIGHT-1, col))
         {
            width++;
         }
      }
      return width;
   }
   
   // assumes a cleaned image in the lower-left corner
   private int computeSignalHeight()
   {
      // use spine to figure out height
      int height = 0;
      for (int row = image.MAX_HEIGHT-1; row >= 0; row--)
      {
         if (image.getPixel(row, 0))
         {
            height++;
         }       
      }
      return height;
   }
   
   // full image data including the blank top and right
   public void displayRawImage()
   {
      image.displayToConsole();
   }
   
   // a nice utility that sets the image to white = false
   private void clearImage()
   {
      image = new BarcodeImage();
   }
   
   public void displayTextToConsole()
   {
      System.out.println(this.text);
   }
   
   // accepts a text string to translate 
   public boolean readText(String text)
   {
      // leave 2 characters for borders
      if (text == null || text.length() > image.MAX_WIDTH - 2)
      {
         return false;
      }
      else
      {
         this.text = text;
         return true;
      }
   }
   
   public boolean generateImageFromText()
   {
      // check to see if text is valid
      if (text.length() > 0)
      {
         // clean image
         cleanImage();
         // prepare area for the text
         actualWidth = text.length() + 2;
         // assume 10 as our maximum
         actualHeight = 10;
         
         //create left border 511 = 111111111 in binary
         writeCharToCol(0, 511);
         
         // print text
         for (int i = 0; i < text.length(); i++)
         {
            // convert character at space to code
            int code = (int)text.charAt(i);
            writeCharToCol(i + 1, code);
         }
         
         //create right border
         writeCharToCol(text.length() + 1, 170);
   
         //Create top and bottom border
         for (int i = 0; i < text.length() + 2; i++)
         {
            // bottom row
            image.setPixel(image.MAX_HEIGHT - 1, i, true);
                   
            // top row * * * *
            if (i % 2 == 0)
            {
               image.setPixel(image.MAX_HEIGHT - 10, i, true);
            }
         }
         return true;
      }
      else
      {
         return false;
      }
   }
   
   private boolean writeCharToCol(int col, int code)
   {
      // Creates a boolean representation of binaryString
      // Bitwise & AND - Sets each bit to 1 if both bits are 1
      // 1 * pow(2, i)
      // 32 bits limitation? Set to 10 in the meantime
      boolean[] bits = new boolean[10];
      for (int i = 0; i < 10; i++) {
          bits[i] = (code & (1 << i)) != 0;
      }

      //Set corresponding column in image to true/false
      // -2 start with the row before last
      // because of bottom closed limitation line
      int row = image.MAX_HEIGHT - 2;
      //for each enhanced loop
      for (boolean element : bits)
      {
         image.setPixel(row--, col, element);
      }    
      return true;   
   }
   
   public void displayImageToConsole()
   {
      // display with border
      for(int col = 0; col < actualWidth + 2; col++)
      {
         System.out.print("-");
      }
      System.out.println();
      for(int row = 0; row < actualHeight; row++)
      {
         System.out.print("|");
         for(int col = 0; col < actualWidth; col++)
         {
            if(image.getPixel(image.MAX_HEIGHT - actualHeight + row, col))
            {
               System.out.print(BLACK_CHAR);
            }
            else
            {
               System.out.print(WHITE_CHAR);
            }
         }
         System.out.println("|");
      }
      for (int x = 0; x < actualWidth + 2; x++)
      {
         System.out.print("-");
      }
      System.out.println();
   } 
}

/* ---------------------------OUTPUT--------------------------------------------
 * 
CSUMB CSIT online program is top notch.
-------------------------------------------
|* * * * * * * * * * * * * * * * * * * * *|
|*                                       *|
|****** **** ****** ******* ** *** *****  |
|*     *    ******************************|
|* **    * *        **  *    * * *   *    |
|*   *    *  *****    *   * *   *  **  ***|
|*  **     * *** **   **  *    **  ***  * |
|***  * **   **  *   ****    *  *  ** * **|
|*****  ***  *  * *   ** ** **  *   * *   |
|*****************************************|
-------------------------------------------
You did it!  Great work.  Celebrate.
----------------------------------------
|* * * * * * * * * * * * * * * * * * * |
|*                                    *|
|**** *** **   ***** ****   *********  |
|* ************ ************ **********|
|** *      *    *  * * *         * *   |
|***   *  *           * **    *      **|
|* ** * *  *   * * * **  *   ***   *** |
|* *           **    *****  *   **   **|
|****  *  * *  * **  ** *   ** *  * *  |
|**************************************|
----------------------------------------
What a great resume builder this is!
----------------------------------------
|* * * * * * * * * * * * * * * * * * * |
|*                                    *|
|***** * ***** ****** ******* **** **  |
|* ************************************|
|**  *    *  * * **    *    * *  *  *  |
|* *               *    **     **  *  *|
|**  *   * * *  * ***  * ***  *        |
|**      **    * *    *     *    *  * *|
|** *  * * **   *****  **  *    ** *** |
|**************************************|
----------------------------------------

*/