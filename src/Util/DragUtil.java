package Util;

import javafx.scene.Node;
import javafx.stage.Stage;

/**
 * 无标题栏窗口拖拽工具
 */

public class DragUtil {
    public static void addDragListener(Stage stage, Node root) {
        new DragListener(stage).enableDrag(root);
    }
}