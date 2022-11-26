package fr.lunyx.moddedlauncher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Scanner;

public class LauncherPanel extends JPanel {
    public static JLabel title;

    public static JButton playButton;

    public static JTextField mailField;
    public static JTextField passwordField;
    public static JCheckBox saveAcc;
    public static JButton loginButton;

    public static JProgressBar progressBar;
    public static JButton statusMojangButton;
    public static JComboBox ramButton;
    public static String ram;
    public static JLabel status;

    public static File accInfos = new File(System.getenv("APPDATA") + "/.ModdedLauncher/accInfos.json");
    public static File ramTxt = new File(System.getenv("APPDATA") + "/.ModdedLauncher/ram.txt");


    public LauncherPanel() throws IOException {
        this.setLayout(null);

        title = new JLabel("Modded Launcher");
        title.setFont(new Font("Tahoma", Font.PLAIN, 30));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBounds(0, 10, 986, 43);
        this.add(title);

        if(accInfos.exists()) {
            playButton = new JButton("Jouer");
            playButton.setFont(new Font("Tahoma", Font.PLAIN, 50));
            playButton.setBounds(250, 200, 500, 100);
            playButton.addActionListener(e -> {
                System.out.println("Bouton cliqué: " + playButton.getText());
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        try {
                            MojangLogin.login();
                        } catch(IOException e) {
                            System.out.println(e.getMessage());
                            if(e.getMessage().equals("Server returned HTTP response code: 403 for URL: https://authserver.mojang.com/authenticate")) {
                                status.setText("Erreur d'authentification, vérifiez votre mail ou votre mot de passe !");
                            }
                            return;
                        }
                    }
                };
                t.start();
            });
            this.add(playButton);
        } else {
            mailField = new JTextField();
            mailField.setHorizontalAlignment(SwingConstants.LEFT);
            mailField.setBounds(250, 218, 483, 30);
            this.add(mailField);

            passwordField = new JPasswordField();
            passwordField.setBounds(250, 293, 483, 30);
            this.add(passwordField);

            saveAcc = new JCheckBox("Sauvegarder mon compte Mojang");
            saveAcc.setBounds(375, 408, 250, 25);
            this.add(saveAcc);

            loginButton = new JButton("Se connecter");
            loginButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
            loginButton.addActionListener(e -> {
                System.out.println("Bouton cliqué: " + loginButton.getText());
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        try {
                            MojangLogin.login();
                        } catch(IOException e) {
                            System.out.println(e.getMessage());
                            if(e.getMessage().equals("Server returned HTTP response code: 403 for URL: https://authserver.mojang.com/authenticate")) {
                                status.setText("Erreur d'authentification, vérifiez votre mail ou votre mot de passe !");
                            }
                            return;
                        }
                    }
                };
                t.start();
            });
            loginButton.setBounds(250, 468, 483, 25);
            this.add(loginButton);
        }

        progressBar = new JProgressBar();
        progressBar.setBounds(10, 500, 960, 21);
        this.add(progressBar);

        statusMojangButton = new JButton("Status des serveurs Mojang");
        statusMojangButton.setBounds(10, 524, 172, 21);
        statusMojangButton.addActionListener(e -> {
            System.out.println("Bouton cliqué: " + statusMojangButton.getText());
            MojangStatus.getStatus();
        });
        this.add(statusMojangButton);

        String[] ramVal = {
                "1G",
                "2G",
                "3G",
                "4G",
                "5G",
                "6G",
                "7G",
                "8G",
                "9G",
                "10G",
                "11G",
                "12G",
                "13G",
                "14G",
                "15G",
                "16G"
        };

        try {
            if(ramTxt.createNewFile()) {
                System.out.println("Fichier ram.txt créé.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ramButton = new JComboBox(ramVal);
        ramButton.setBounds(200, 524, 75, 21);
            try {
                Scanner myReader = new Scanner(ramTxt);
                while (myReader.hasNextLine()) {
                    String CurrRamVal = myReader.nextLine();
                    ramButton.setSelectedItem(CurrRamVal);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        ramButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ram = (String) ramButton.getSelectedItem();
                try {
                    FileWriter fw = new FileWriter(System.getenv("APPDATA") + "/.ModdedLauncher/ram.txt", true); //the true will append the new data
                    PrintWriter writer = new PrintWriter(System.getenv("APPDATA") + "/.ModdedLauncher/ram.txt");
                    writer.print("");
                    writer.close();
                    fw.write(ram);
                    fw.close();
                    System.out.println("Fichier ram.txt mis à jour. (" + ram + ")");
                    status.setText("Fichier ram.txt mis à jour. (" + ram + ")");
                } catch(IOException ioe) {
                    System.err.println("IOException: " + ioe.getMessage());
                }
            }
        });
        this.add(ramButton);

        status = new JLabel();
        status.setBounds(290, 524, 680, 21);
        this.add(status);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
