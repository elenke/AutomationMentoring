package Homework2;

import javax.swing.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by admin on 09.03.2017.
 */
public class CalculateFromFile {
   // private static String fileName = "C://student/Autotest1/equations.txt";

    public static void main(String[] args) throws FileNotFoundException {
        int a; //коэф уравнений
        int b;
        int c;
        double d;
        double resX[] = new double[2]; // корни х1 и х2

        //Чтение из файла только строк с валидными уравнениями
        String textFromFile = CalculateFromFile.read();

        // Если кол-во валидных уравнений > 0 тогда продолжаем работу, иначе - выход
        if (textFromFile.length()>0) {
            // System.out.println(textFromFile);  //вывести уравнения на экран

            // Получим из строки массив уравнений public String[] split(String regex)
            String[] eqArray = textFromFile.split("\n");
            // eqArray.length - кол-во валидных уравнений

/*        for (String i : eqArray) { //вывод на экран массива валидных уравнений
            System.out.println(i);
        }*/
            //создаём массив 0 - уравнение, коэф А, коэф В, коэф С, дискриминант Д, корни Х1 и Х2
            String[][] solArray = new String[eqArray.length][7];
            int i = 0;
            String[] resArray;
            for ( int j=0; j<eqArray.length; j++){//String iArr : eqArray) { // проходимся по массиву уравнений, вычисляем дискриминант и корни уравнений в solArray
                resArray = CalculateFromFile.getСoefficientFromEquation(eqArray[j]);
                solArray[i][0] = eqArray[j];
                solArray[i][1] = resArray[0];//заполнение массива коєффициентами
                solArray[i][2] = resArray[1];
                solArray[i][3] = resArray[2];
                a = Integer.parseInt(resArray[0]);
                b = Integer.parseInt(resArray[1]);
                c = Integer.parseInt(resArray[2]);
                d = CalculateFromFile.calculateDiscriminant(a, b, c);

                if((a == 0)&(b == 0)) { //введенные данные не корректны. Заполняем d=0 и корни null, уравнение можем не отображать на экране
                        solArray[i][4] = Double.toString(d);
                        solArray[i][5] = null;
                        solArray[i][6] = null;
                    System.out.println("Input equation is not correct а=0 and b=0 " + solArray[i][0]); //? можно и не выводить
                    }

                else if (d >= 0) {
                    if ((a == 0)&(b!=0)) //это линейное уравнение x = -c / b
                    {
                        solArray[i][4] = Double.toString(d);
                        solArray[i][5] = Double.toString(-c / b);
                        solArray[i][6] = solArray[i][5];
                    }
                    else //a != 0 квадратное уравнение
                    {
                        resX = CalculateFromFile.getAnswer(a, b, c);
                        solArray[i][4] = Double.toString(d);
                        solArray[i][5] = Double.toString(resX[0]);
                        solArray[i][6] = Double.toString(resX[1]);
                    }
                }
                else if (d<0){  // d < 0 дискриминант отрицательный, корни комплексные
                    solArray[i][4] = Double.toString(d);
                    solArray[i][5] = null;
                    solArray[i][6] = null;
                    System.out.println("Equation " + solArray[i][0] + " has complex roots.");

                }
                i++;
            }
            //System.out.println("массив уравнений с решением");
            //System.out.println(Arrays.deepToString(solArray));

            //находим мин и макс єлемент последовательности и выводим его на экран вместе с уравнением
            // корни в [5] и [6] столбце массива solArray, значения double and null
            //ищем первый не null корень
            double[] max = {0, 0};
            double[] min = {0, 0};
            boolean isRoot = false; //флаг, показывающий, есть ли вообще в массиве корни уравнения
            for (int k = 0; (k <= eqArray.length - 1) & (!isRoot); k++) {
                for (int j = 5; (j <= 6) & (!isRoot); j++) {
                    if (solArray[k][j] != null) {
                        min[0] = k; //записываем номер уравнения
                        min[1] = Double.parseDouble(solArray[k][j]);
                        max[0] = k; //записываем номер уравнения
                        max[1] = Double.parseDouble(solArray[k][j]);
                        isRoot = true;
                    }
                }
            }

            //если есть корни уравнения, тогда ищем мин и макс
            if (isRoot) {
                for (int k = 0; (k <= eqArray.length - 1); k++) {
                    for (int j = 5; (j <= 6); j++) {
                        if (solArray[k][j] != null) {
                            if (Double.parseDouble(solArray[k][j]) < min[1]) {
                                min[0] = k; //записываем номер уравнения
                                min[1] = Double.parseDouble(solArray[k][j]);
                            }

                            if (Double.parseDouble(solArray[k][j]) > max[1]) {
                                max[0] = k; //записываем номер уравнения
                                max[1] = Double.parseDouble(solArray[k][j]);
                            }
                        }
                    }
                }
                System.out.println("Min number of sequence is " + min[1] + " from equation: " + eqArray[(int) min[0]]);
                System.out.println("Max number of sequence is " + max[1] + " from equation: " + eqArray[(int) max[0]]);
            }
        }
        // если валидных уравнений = 0 textFromFile.length()=0
        else {System.out.println("File has not a valid equation!");}

    }



    public static String read() throws FileNotFoundException {

        JFileChooser dialog = new JFileChooser();

        int ret = dialog.showDialog(null, "Оpen file:");
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = new File(String.valueOf(dialog.getSelectedFile()));
        }
        else {
            System.out.println("File is not chosen!");
            System.exit(0);
        }
        File file = new File(String.valueOf(dialog.getSelectedFile()));


        //File file = new File(fileName);

        //Этот спец. объект для построения строки
        StringBuilder sb = new StringBuilder();
        try {
            //Объект для чтения файла в буфер
            BufferedReader in = new BufferedReader(new FileReader(file.getAbsoluteFile()));
            try {
                //В цикле построчно считываем файл
                String s;
                while ((s = in.readLine()) != null) {
                    s = s.trim(); // удалить пробелы до и после уравнения
                    if (CalculateFromFile.isStringEquation(s)) //проверка, является ли cтрока уравнением требуемого вида
                    {
                        sb.append(s);
                        sb.append("\n");
                    }
                }
            } finally {
                // не забываем закрыть файл
                in.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //Возвращаем полученный текст из файла
        return sb.toString();
    }

    public static boolean isStringEquation(String str) {
        // проверяет уравнение на соответствие следующему шаблону  9x^2 - 2x + 4 = 0
        // \\s  - пробел
        str = str.trim(); // удалить пробелы до и после уравнения
        if (str.matches("[0-9]+[x]\\^[2]\\s(-|\\+)\\s[0-9]+[x]\\s(-|\\+)\\s[0-9]+\\s[=]\\s[0]")) {
            // System.out.println("The entered data is true " + str );
            return true;
        } else {
            //System.out.println("The entered data is not true!!! " + str );
            return false;

        }
    }

    //если уравнение прошло проверку, тогда вызываем этот метод и получаем коэф a,b,c
    public static String[] getСoefficientFromEquation(String str) {
        String[] res = new String[3];
        int i = 0;
        //убрать из строки пробелы
        str = str.replaceAll("\\s", "");
        //извлекаем числа - первое без знака, и перед которыми есть знаки + или -
        Pattern pat = Pattern.compile("(^[0-9]+|(-|\\+)[0-9]+)");
        Matcher matcher = pat.matcher(str);
        //в цикле получаются только 3 значения
        while (matcher.find()) {
            res[i] = matcher.group().toString();
            i++;
        }
        return res;
    }

    private static double calculateDiscriminant(int a, int b, int c) {
        double res = b * b - 4 * a * c;
        return res;
    }

    private static double[] getAnswer(int a, int b, int c) {
       /* если а=0 х=-с\b  иначе*/
        double res[] = new double[2];
        res[0] = (-b + Math.sqrt(b * b - 4 * a * c)) / (2 * a);
        res[1] = (-b - Math.sqrt(b * b - 4 * a * c)) / (2 * a);
        return res;

    }

}