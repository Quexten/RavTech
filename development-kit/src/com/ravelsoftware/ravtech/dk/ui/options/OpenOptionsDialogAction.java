package com.ravelsoftware.ravtech.dk.ui.options;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.lang.reflect.Field;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.fife.ui.OptionsDialog;
import org.fife.ui.OptionsDialogPanel;
import org.fife.ui.TitledPanel;

import com.ravelsoftware.ravtech.dk.RavTechDK;

public class OpenOptionsDialogAction {

    public void perform () {
        OptionsDialog optionsDialog = new OptionsDialog(RavTechDK.ui.ravtechDKFrame,
            new OptionsDialogPanel[] {new GeneralSettingsPanel()});
        try {
            Field field = optionsDialog.getClass().getDeclaredField("titledPanel");
            field.setAccessible(true);
            UIManager.getColor("TextField.background");
            UIManager.getColor("TextField.selectionBackground");
            TitledPanel panel = (TitledPanel)field.get(optionsDialog);
            UIManager.put("TextField.background", UIManager.getColor("Ravtech.foreground"));
            UIManager.put("TextField.selectionBackground", UIManager.getColor("Ravtech.foreground").darker());
            JPanel component = (JPanel)panel.getComponent(0);
            component.updateUI();
            component.repaint();
            field = panel.getClass().getDeclaredField("label");
            field.setAccessible(true);
            JLabel label = (JLabel)field.get(panel);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("dialog", Font.PLAIN, 12));
            label.repaint();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        optionsDialog.setLocationRelativeTo(null);
        optionsDialog.setMinimumSize(new Dimension(800, 600));
        optionsDialog.setVisible(true);
    }
}
