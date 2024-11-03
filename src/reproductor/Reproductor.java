package reproductor;

import java.io.File;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.stage.Stage;
import view.PrincipalView;


public class Reproductor extends Application{

 
    @Override
    public void start(Stage primaryStage) {
        PrincipalView vistaPrincipal = new PrincipalView("listas"+File.separator+"Canciones.txt");
        vistaPrincipal.inicializarComponentes();
        primaryStage.setTitle("Reproductor de Musica");
      primaryStage.setScene(vistaPrincipal.getStage().getScene());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
