package dobby;

import dobby.vista.MainView;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainView ventanaPrincipal = new MainView();
            ventanaPrincipal.setVisible(true);
        });
    }
}
