package com.company;

import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Main {

    /**
     *  Программа анализатор, обрабатывающий арифметические выражения.
     *  Выражение, которые будут обрабатываться данным анализатором
     *  состоят из следующих элементов:
     *      1.  числа
     *      2.  операторы (+, -, *, /)
     *      3.  круглые скобки ()
     *  Ниже показано несколько примеров
     *      1.  10 - 8
     *      2.  (100 - 5) * 14 / 6
     *  Приоритеты операций, от высшего к низшему
     *      1.  +,- (унарные)
     *      2.  *, /
     *      3.  +,-
     *
     *  Ограничения:
     *      1.  Все числовые занчения должны быть числами двойной точности
     *      2.  Производится проверка только на наличие элементарных
     *          ошибок.                    
     * @throws ParserException
     */

    public static void main(String[] args) throws ParserException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Parser myParser = new Parser();

        for(;;) {
            try {
                System.out.print("Введите выражение для вычисления\n-> ");
                String str = reader.readLine();
                if(str.equals(""))
                    break;
                double result = myParser.evaluate(str);

                DecimalFormatSymbols s = new DecimalFormatSymbols();
                s.setDecimalSeparator('.');
                DecimalFormat f = new DecimalFormat("#,###.00", s);

                System.out.printf("%s = %s%n", str, f.format(result));
            }
            catch(ParserException e) {
                System.out.println(e);
            }
            catch(Exception e) {
                System.out.println(e);
            }
        }
    }
}