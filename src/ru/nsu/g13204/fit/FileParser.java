package ru.nsu.g13204.fit;

import ru.nsu.g13204.fit.pixel2d.vectors.Vec2dI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class FileParser {
    File file;
    private BufferedReader in;
    public int width, height;
    public int lineThickness;
    public int hexaWidthR;
    public Vec2dI[] cells;

    FileParser(File file) throws FileParserError {
        this.file = file;
        if (!file.canRead()) {
            throw new FileParserError("Не могу прочитать файл");
        }
    }

    public void parse() throws FileParserError {
        try {
            //Объект для чтения файла в буфер
            in = new BufferedReader(new FileReader( file.getAbsoluteFile()));

            int[] buf;

            // Первая строчка
            buf = getDataFromFile(2);
            width = buf[0];
            height = buf[1];

            // Вторая строчка
            buf = getDataFromFile(1);
            hexaWidthR = buf[0];

            // Третья строчка
            buf = getDataFromFile(1);
            lineThickness = buf[0];

            // Четвёртая строчка
            buf = getDataFromFile(1);
            int cellsCount = buf[0];

            cells = new Vec2dI[cellsCount];

            // Следующие строчки
            for (int i = 0; i < cellsCount; i++) {
                buf = getDataFromFile(2);
                cells[i] = new Vec2dI(buf[0], buf[1]);
            }

            //Также не забываем закрыть файл
            in.close();

        } catch(IOException e) {
            throw new RuntimeException(e);
        } catch (NumberFormatException e) {
            throw new FileParserError("Ошибка разбора чисел: `" + e.toString() + "`");
        }
    }

    private int[] getDataFromFile(int count) throws IOException, FileParserError {
        String s;
        if ((s = in.readLine()) == null) {
            throw new FileParserError("Файл нежданно-негаданно закончился.");
        }
        int[] buf = parseLine(s);
        if (buf.length != count)
            throw new FileParserError("Ошибка при разборе файла");

        return buf;
    }

    private int[] parseLine(String s) {
        int comment = s.indexOf("//");
        if (comment != -1)
            s = s.substring(0, comment);

        String[] data = s.split(" ");

        int[] numbers = new int[data.length];
        int index = 0;

        for (String number: data) {
            numbers[index] = Integer.parseInt(number);
            index++;
        }

        return numbers;
    }
}
