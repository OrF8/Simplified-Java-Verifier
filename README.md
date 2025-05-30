# Simplified Java Verifier
The Simplified Java Verifier is a static analysis tool designed to verify the correctness of programs written in *s-java*, a simplified subset of the Java programming language.\
This project was developed by [**Noam Kimhi**](https://github.com/noam-kimhi) and [**Or Forshmit**](https://github.com/OrF8) as part of the course [**67125 - Introduction to Object-Oriented Programming**](https://shnaton.huji.ac.il/index.php/NewSyl/67125/2/2025/) at The Hebrew University of Jerusalem ([HUJI](https://en.huji.ac.il/)). \
> 🎓 Final Grade: **100**

# 🧾 Table of Contents
- [**Features**](https://github.com/OrF8/Simplified-Java-Verifier?tab=readme-ov-file#-features)
- [**Supported Language Constructs**](https://github.com/OrF8/Simplified-Java-Verifier#-supported-language-constructs)
- [**Project Structure**](https://github.com/OrF8/Simplified-Java-Verifier#-project-structure)
- [**Getting Started**](https://github.com/OrF8/Simplified-Java-Verifier#-getting-started)
- [**Usage**](https://github.com/OrF8/Simplified-Java-Verifier#-usage)
- [**License**](https://github.com/OrF8/Simplified-Java-Verifier#-license)

# ✨ Features
- Lexical Analysis: Tokenizes s-java source code.
- Syntax Analysis: Parses tokens to ensure correct syntax structure.
- Semantic Analysis: Checks for semantic correctness, including type checking and scope resolution.
- Error Reporting: Provides detailed error messages for invalid s-java code.
- Modular Design: Organized into clear modules for maintainability and extensibility.

# 🧱 Supported Language Constructs
s-java is a simplified version of java, which supports the following:
## Variables
- Variable names must start with a letter or an underscore and can contain letters, digits and underscores.
- Variable names are case-sensitive.
- Variable names are unique.
- Variable types can be one of the following:
    - `int` - an integer number.
    - `double` - a floating-point number.
    - `boolean` - a boolean value.
    - `String` - a string.
    - `char` - a character.
- Variable names cannot be a reserved word (`int`, `double`, `boolean`, `String`, `char`, `final`, `if`, `while`, `void`, `return`, `true`, `false`).
- Variable names cannot contain spaces.
- Variable names cannot contain double underscores (__).
- '_' is an illegal variable name.
- A variable (local or global) may have the same name as a method.
- Both variable declaration and assignment must end with a semicolon.
### Variable Declaration
- Variables can be declared with the following types: `int`, `double`, `boolean`, `String`, `char`.
- A variable declaration looks like this: `<type> <name>;` or `<type> <name> = <value>;`.
- Variables can be declared as `final`, which means they cannot be changed after initialization.
    - A `final` variable must be initialized with a value.
    - A `final` variable declaration looks like this: `final <type> <name> = <value>;`.
- Variables can be initialized with a value.
- Multiple variables can be declared in the same line, separated by commas.
### Variable Assignment
- Variables can be assigned a value.
- Variables can be assigned a value whose type is compatible with their type. Compatible types are:
    - int can be assigned to int, double and boolean.
    - double can be assigned to double and boolean.
    - boolean can be assigned to boolean.
    - String can be assigned to String.
    - char can be assigned to char.
- int and double variables can be assigned with leading zeros, '+' and '-' signs.
- double variables can also be assigned values with a decimal point (e.g., 3.14, -.5, 3.).
- String values must be enclosed in double quotes.
- char values must be enclosed in single quotes.
- Multiple variables can be assigned in the same line, separated by commas.
- Variables can be assigned to other variables only if they are of compatible types.
- Variables can be assigned with other variables only if the right-hand side variable is initialized.
### Referring to Variables
- Variables can be referred to only by their name.
- Variables must be declared before they are referred to.
## Methods
- Method names must start with a letter, and can contain letters, digits and underscores.
- Besides the fact that method name cannot start with an underscore, the same rules for variable names apply to method names.
- Method must end with a return statement, followed by a curly brace in a new line.
### Method Declaration
- Methods can be declared only with a void return type.
- A method declaration looks like this: `void <name>(<params>) {`.
- Methods can have zero or more parameters.
- Parameters are declared as `<type> <name>, <type> <name>, ...`.
- As before, parameters' names must be unique and cannot be a reserved word.
- A method may *not* be declared inside another method.
- Method parameters may be final. In this case, they cannot be changed inside the method.
### Method Call
- Methods can be called only from within another method.
- Methods can be called with the following syntax: `<name>(<args>);`, where args are compatible types with the method's parameters.
- Methods can be called with constants such as true, or 0, as long as they are compatible with the method's parameters.
- Methods can be called with variables as arguments, as long as they are initialized, and their type is compatible with the method's parameters.
- A method with a final parameter can be called with a non-final variable as an argument.
- A method with a non-final parameter can be called with a final variable as an argument (The school's solution approves it).
- Recursive calls are allowed.
- A method can be called in a line prior to its declaration.
- A method call must end with a semicolon.
## if-while Statements
- if-while statements can be used only inside a method.
- if-while statements can be used with the following syntax: `if (<condition>) {` or `while (<condition>) {`, where condition is a boolean expression.
- if-while statements can be used with multiple conditions, separated by logical operators (&&, ||).
- Conditions can be a single boolean variable, a boolean constant, or a boolean expression.
    - A boolean variable, constant or expression can also be an int or double variable, constant or expression. See [here](https://github.com/OrF8/Simplified-Java-Verifier?tab=readme-ov-file#variable-assignment).
- if-while statements can be nested.
- if a variable is used in a condition, it must be initialized.
- if-while statements must be closed with a closing curly brace.
## Comments & Whitespaces
- Comments can be used in the code.
- Comments must be single-line comments, starting with `//` and ending with a newline (such as `\n` or `\r`).
- Comments can only be used at the beginning of a line.
- Whitespaces can be used in the code and are ignored (e.g., `int      x=       7  ;` is valid and will be regarded as `int x=7;`).
- Whitespaces inside names and types are not allowed (e.g., `int x y;` and `St ring s;` are invalid).
- Empty lines are allowed.

# 📁 Project Structure
````
Simplified-Java-Verifier/
├── src/                                      # Source code for the verifier
│   ├── UML.pdf                               # A UML diagram of the project
│   ├── README                                # A description of some of the RegEx we've used in this project
│   └── ex5/
|       ├── main/
|           └── Sjavac.java                   # Entry point of the application
│       └── sjava_verifier/
|           ├── preprocessor/
|               └── FileCleaner.java          # Cleans the file from empty lines or comments before starting the verification process
|           └── verifier/                     # Code for the verification process 
├── tests/                                    # Unit tests for various components
├── .gitattributes                            # Git attributes configuration
├── LICENSE                                   # MIT License
└── README.md                                 # Project documentation
````

# 🚀 Getting Started
## Prerequisites
- Java Development Kit (JDK) 8 or higher
- [Apache Maven](https://maven.apache.org/) (optional, for build automation)
## Installation
1. Clone the repository:
   ````
   git clone https://github.com/OrF8/Simplified-Java-Verifier.git
   cd Simplified-Java-Verifier
   ````
2. Compile the project:
   - Using Maven:
     ````
     mvn compile
     ````
   - Using `javac`:
     javac -d bin src/**/*.java

# 📝 Usage
To run the verifier on an s-java source file:
- Using Maven:
  ````
  mvn exec:java -Dexec.mainClass="Main" -Dexec.args="path/to/YourFile.sjava"
  ````
- Using `java`:
  ````
  java -cp bin Main path/to/YourFile.sjava
  ````

# 📄 License
This project is licensed under the MIT License – see the [**LICENSE**](https://github.com/OrF8/Simplified-Java-Verifier/blob/main/LICENSE) file for details.



















   
