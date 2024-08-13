package org.intellij.sdk.action;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class OpenBrowserDialog extends JDialog {
    private JPanel contentPane;
    private JTextField textFieldURL;
    private JButton buttonOK;
    private WebDriver driver;

    public OpenBrowserDialog() {
        contentPane = new JPanel(new BorderLayout());
        textFieldURL = new JTextField(30);
        buttonOK = new JButton("Open");

        setTitle("Open Browser");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.add(new JLabel("URL:"));
        panel.add(textFieldURL);
        contentPane.add(panel, BorderLayout.CENTER);
        contentPane.add(buttonOK, BorderLayout.SOUTH);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
    }

    private void onOK() {
        String url = textFieldURL.getText();
        System.out.println("Entered URL: " + url);

        if (!url.isEmpty()) {
            setupAndOpenBrowser(url);
        } else {
            System.err.println("URL is empty.");
        }
    }

    private void setupAndOpenBrowser(String url) {
        try {

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--disable-extensions");
            options.setCapability("goog:loggingPrefs", getLoggingPrefs());
            driver = new ChromeDriver(options);
            driver.get(url);

            addClickListener();


            startLogMonitoringThread();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addClickListener() {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        jsExecutor.executeScript(
                "document.addEventListener('click', function(e) {" +
                        "   var elementInfo = {};" +
                        "   if (e.target.id) {" +
                        "       elementInfo.id = e.target.id;" +
                        "   }" +
                        "   if (e.target.tagName) {" +
                        "       elementInfo.tagName = e.target.tagName;" +
                        "   }" +
                        "   if (e.target.className) {" +
                        "       elementInfo.className = e.target.className;" +
                        "   }" +
                        "   if (e.target.textContent) {" +
                        "       elementInfo.textContent = e.target.textContent;" +
                        "   }" +
                        "   console.log('Element Info: ' + JSON.stringify(elementInfo));" +
                        "});"
        );
    }


    private void startLogMonitoringThread() {
        new Thread(() -> {
            try {
                while (driver != null) {
                    LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
                    for (LogEntry entry : logEntries) {
                        System.out.println(entry.getMessage());
                    }
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static java.util.Map<String, Object> getLoggingPrefs() {
        java.util.Map<String, Object> logs = new java.util.HashMap<>();
        logs.put("browser", "ALL");
        return logs;
    }

    public static void main(String[] args) {

        System.setProperty("webdriver.chrome.driver", "C:\\Users\\LENOVO\\Downloads\\chromedriver-win64.zip\\chromedriver-win64\\chromedriver.exe");

        SwingUtilities.invokeLater(() -> {
            OpenBrowserDialog dialog = new OpenBrowserDialog();
            dialog.setVisible(true);
        });
    }

    @Override
    public void dispose() {
        super.dispose();
        if (driver != null) {
            driver.quit();
        }
    }
}
