package com.company;

public class Parser {

    //  Объявление лексем
    final int NONE = 0;         //  FAIL
    final int DELIMITER = 1;    //  Разделитель(+-*/())
    final int NUMBER = 2;       //  Число

    //  Объявление констант синтаксических ошибок
    final int SYNTAXERROR = 0;  //  Синтаксическая ошибка (10 + 5 6 / 1)
    final int UNBALPARENS = 1;  //  Несовпадение количества открытых и закрытых скобок
    final int NOEXP = 2;        //  Отсутствует выражение при запуске анализатора
    final int DIVBYZERO = 3;    //  Ошибка деления на ноль

    //  Лексема, определяющая конец выражения
    final String EOF = "\0";

    private String exp;     //  Ссылка на строку с выражением
    private int explds;     //  Текущий индекс в выражении
    private String token;   //  Сохранение текущей лексемы
    private int tokType;    //  Сохранение типа лексемы

    public String toString() {
        return String.format("Exp =  {0}\nexplds =  {1}\nToken =  {2}\nTokType =  {3}",
                             exp.toString(), explds, token.toString(), tokType);
    }

    //  Получить следующую лексему
    private void getToken() {
        tokType = NONE;
        token = "";

        //  Проверка на окончание выражения
        if(explds == exp.length()) {
            token = EOF;
            return;
        }

        //  Проверка на пробелы, если есть пробел - игнорируем его.
        while(explds < exp.length() && Character.isWhitespace(exp.charAt(explds)))
            ++explds;

        //  Проверка на окончание выражения
        if(explds == exp.length()) {
            token = EOF;
            return;
        }

        if(isDelim(exp.charAt(explds))) {
            token += exp.charAt(explds);
            explds++;
            tokType = DELIMITER;
        }
        else if(Character.isLetter(exp.charAt(explds))) {
            while(!isDelim(exp.charAt(explds))) {
                token += exp.charAt(explds);
                explds++;

                if(explds >= exp.length())
                    break;
            }

            tokType = NONE;
        }
        else if (Character.isDigit(exp.charAt(explds))) {
            while(!isDelim(exp.charAt(explds))) {
                token += exp.charAt(explds);
                explds++;

                if(explds >= exp.length())
                    break;
            }

            tokType = NUMBER;
        }
        else {
            token = EOF;
            return;
        }
    }

    private boolean isDelim(char charAt) {
        if((" +-/*()".indexOf(charAt)) != -1)
            return true;

        return false;
    }

    //  Точка входа анализатора
    public double evaluate(String expstr) throws ParserException {

        double result;

        exp = expstr;
        explds = 0;
        getToken();

        if(token.equals(EOF))
            handleErr(NOEXP);   //  Нет выражения

        //  Анализ и вычисление выражения
        result = evalTerms();

        if(!token.equals(EOF))
            handleErr(SYNTAXERROR);

        return result;
    }

    //  Сложить или вычислить два терма
    private double evalTerms() throws ParserException {

        char op;
        double result;
        double partialResult;

        result = evalFactors();

        while((op = token.charAt(0)) == '+' || op == '-') {
            getToken();
            partialResult = evalFactors();

            switch(op) {
                case '-':
                    result -= partialResult;
                    break;
                case '+':
                    result += partialResult;
                    break;
            }
        }

        return result;
    }

    //  Умножить или разделить два фактора
    private double evalFactors() throws ParserException {

        char op;
        double result;
        double partialResult;

        result = evalUnary();

        while((op = token.charAt(0)) == '*' || op == '/') {
            getToken();
            partialResult = evalUnary();

            switch(op) {
                case '*':
                    result *= partialResult;
                    break;
                case '/':
                    if(partialResult == 0.0)
                        handleErr(DIVBYZERO);

                    result /= partialResult;
                    break;
            }
        }

        return result;
    }

    //  Определить унарные + или -
    private double evalUnary() throws ParserException {
        double result;

        String op;
        op = " ";

        if((tokType == DELIMITER) && token.equals("+") || token.equals("-")) {
            op = token;
            getToken();
        }

        result = evalBrackets();

        if(op.equals("-"))
            result = -result;

        return result;
    }

    //  Обработать выражение в скобках
    private double evalBrackets() throws ParserException {
        double result;

        if(token.equals("(")) {
            getToken();
            result = evalTerms();

            if(!token.equals(")"))
                handleErr(UNBALPARENS);

            getToken();
        }
        else
            result = atom();

        return result;
    }

    //  Получить значение числа
    private double atom()   throws ParserException {

        double result = 0.0;

        switch(tokType) {
            case NUMBER:
                try {
                    result = Double.parseDouble(token);
                }
                catch(NumberFormatException exc) {
                    handleErr(SYNTAXERROR);
                }
                getToken();
                break;
            default:
                handleErr(SYNTAXERROR);
                break;
        }

        return result;
    }

    //  Кинуть ошибку
    private void handleErr(int nOEXP2) throws ParserException {

        String[] err  =  {
                "Syntax error",
                "Unbalanced Parentheses",
                "No Expression Present",
                "Division by zero"
        };

        throw new ParserException(err[nOEXP2]);
    }
}