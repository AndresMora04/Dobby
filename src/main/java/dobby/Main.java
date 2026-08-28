package dobby;

import dobby.controlador.Controlador;
import dobby.flow.FlowController;
import dobby.vista.MainView;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainView ventanaPrincipal = new MainView();
            FlowController flowController = new FlowController();
            new Controlador(ventanaPrincipal, flowController);
            ventanaPrincipal.setVisible(true);
        });
    }
}
