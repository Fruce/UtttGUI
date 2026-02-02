package main;

import javafx.scene.layout.StackPane;

public interface ResizableScene {
    StackPane getWrapper();   // resizable container
    StackPane getRoot();      // fixed-size root
    double getBaseSize();     // design size
}
