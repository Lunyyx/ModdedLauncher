package fr.lunyx.moddedlauncher;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import fr.theshark34.openlauncherlib.LaunchException;
import fr.theshark34.openlauncherlib.external.ExternalLaunchProfile;
import fr.theshark34.openlauncherlib.external.ExternalLauncher;
import fr.theshark34.openlauncherlib.minecraft.*;
import fr.theshark34.supdate.BarAPI;
import fr.theshark34.supdate.SUpdate;
import fr.theshark34.supdate.application.integrated.FileDeleter;

public class Minecraft {
    private static Thread updateThread;

    private static SUpdate su = new SUpdate("YOUR_SUPDATER_LINK", new File(System.getenv("APPDATA") + "/.ModdedLauncher"));

    public static double calculatePercentage(int obtained, int total) {
        int percentage = obtained * 100 / total;
        if(percentage > 100.0) {
            return 100.0;
        } else {
            return percentage;
        }
    }

    public static void update() throws Exception {
        su.getServerRequester().setRewriteEnabled(true);
        su.addApplication(new FileDeleter());
        Thread updateThread = new Thread() {
            @Override
            public void run() {
                while (!this.isInterrupted()) {
                    if (BarAPI.getNumberOfTotalDownloadedBytes() == 0) {
                        LauncherPanel.status.setText("Vérification des fichiers...");
                        System.out.println("Vérification des fichiers...");
                        continue;
                    }
                    int val = (int) (BarAPI.getNumberOfTotalDownloadedBytes() / 1000);
                    int max = (int) (BarAPI.getNumberOfTotalBytesToDownload() / 1000);
                    int percentage = (int) calculatePercentage(val, max);
                    LauncherPanel.progressBar.setValue(percentage);
                    LauncherPanel.status.setText("Telechargement des fichiers " + BarAPI.getNumberOfDownloadedFiles() + "/" + BarAPI.getNumberOfFileToDownload() + " (" + percentage + "%)");
                    System.out.println(val + "/" + max);
                }
            }
        };

        updateThread.start();
        su.start();
        updateThread.interrupt();
    }

    public static void start(String username, String aToken, String uuid) {
        LauncherPanel.status.setText("Démarrage du jeu... (Le launcher va se fermer)");
        GameInfos infos = new GameInfos("ModdedLauncher", new GameVersion("1.12.2", GameType.V1_8_HIGHER), new GameTweak[]{GameTweak.FORGE});
        GameFolder folder = new GameFolder("assets", "libs", "natives", "minecraft.jar");
        AuthInfos authInfos = new AuthInfos(username, aToken, uuid);
        try {
            ExternalLaunchProfile profile = MinecraftLauncher.createExternalProfile(infos, folder, authInfos);
            try {
                File ramTxt = new File(System.getenv("APPDATA") + "/.ModdedLauncher/ram.txt");

                Scanner myReader = new Scanner(ramTxt);
                String CurrRamVal = null;
                while (myReader.hasNextLine()) {
                    CurrRamVal = myReader.nextLine();
                }
                profile.getVmArgs().add("-Xmx" + CurrRamVal);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            ExternalLauncher launcher = new ExternalLauncher(profile);
            launcher.launch();
            System.exit(0);
        } catch (LaunchException e) {
            e.printStackTrace();
        }
    }

    public static void interruptThread() {
        updateThread.interrupt();
    }

    public static void setUpdateThread(Thread updateThread) {
        Minecraft.updateThread = updateThread;
    }
}
