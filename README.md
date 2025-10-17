# COSC412-Semester-Project


**--Name--**
Simon Murray

**--Programming Language--**
*Java* (Java SE 25 or Newer)
Version for Development: **Java 25**

**--Compiler/Interpreter--**
Compiler: javac 11.0.27
Interpreter: Java Virtual Machine

**--IDE/TOOLS--**
IDE: *Visual Studio Code
OS: Windows 11

**--BUILD INSTRUCTIONS--**

This project requires the main class file ("Analyser.java") as well as the other class files

**COMPILE**
 1. Use the Java compiler (javac) to compile the file

--------------------------------------------------
Command Prompt |
--------------------------------------------------
cd C:\Users\Your-File-Location
javac Analyser.java
--------------------------------------------------

**Execute**
2. Run the compiled Analyser.java class using the JVM (java)

---------------------------------------------------
Command Prompt |
---------------------------------------------------
java Analyser
---------------------------------------------------

**Provide Input**
3. The program will prompt you for the input file's file path

---------------------------------------------------
Command Prompt |
---------------------------------------------------
Enter the file location: *Input the full path to source file*
---------------------------------------------------

This program serves as a lexical analyser for our 455 programing languages class language. It recognizes keywords, predefinied symbols, identifiers, and numerics as well as an "End-of-File" token

## --ACCEPTED TOKENS-- ##
| Token Type | Value Example | Description |
--------------------------------------------
| *Keyword* | 'for', 'while', 'true', 'false' | Recognized Keywords |
| *Symbol* | '=', '+', ':=', '!=', '>=' | Symbols that are 1 or 2 chars long |
| *ID* | 'i' 'pogo' '_cornball' | Variables starting with a letter or underscore |
| *NUM* | '43' '3' '42235' | Any sequence of digits |
| *End-of-Text*| '$' | Token added at the end to mark end of file |
---------------------------------------------

**ERROR HANDLING**
File-Not-Found: The program will exit with the message "Error: File not Found"
Unrecognized Charachter: The program will exit with the message "Error: Unrecognized charachter c at line X, char Y."

** --Output Format-- **
The program prints a list of tokens, one token per line with the structure:

Token [Index]: [Type: type, Start Position (Line, char)], Value: value

Example
TOKEN 0: [Type: for, Start Position: (1,1)], Value: for
TOKEN 3: [Type: ID, Start Position: (1,9)], Value: i
TOKEN 4: [Type: =, Start Position: (1,10)], Value: =
TOKEN 5: [Type: NUM, Start Position: (1,11)], Value: 0
TOKEN 22: [Type: END-OF-FILE, Start Position: (5,2)], Value: $ 