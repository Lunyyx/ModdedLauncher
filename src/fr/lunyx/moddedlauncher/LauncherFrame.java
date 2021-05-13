package fr.lunyx.moddedlauncher;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class LauncherFrame extends JFrame {
    private static LauncherFrame instance;
    private final LauncherPanel launcherPanel;

    public LauncherFrame() throws IOException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {
            System.out.println("Error setting native LAF: " + e);
        }

        this.setTitle("Launcher");
        this.setContentPane(launcherPanel = new LauncherPanel());
        this.setSize(1000, 600);
        this.setVisible(true);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) throws IOException {
        instance = new LauncherFrame();
    }

    public static LauncherFrame getInstance() {
        return instance;
    }

    public LauncherPanel getLauncherPanel() {
        return launcherPanel;
    }
}