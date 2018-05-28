package Util;

import javafx.scene.control.TextArea;
import javafx.scene.text.Font;

public class WordStyleSet {
    public static void StyleSet(TextArea textArea, String fontName, double wordSize) {
        //根据字体名和字体大小设置文本

        textArea.setFont(Font.font(fontName, wordSize));
        System.out.println(fontName+"\n"+wordSize);
        //textArea.setFont(Font.font("verdana",15.00));
    }
}
