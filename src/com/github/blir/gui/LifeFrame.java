package com.github.blir.gui;

import com.github.blir.Counter;
import com.github.blir.Direction;
import com.github.blir.Life;
import com.github.blir.Location;
import com.github.blir.file.DesignReader;
import com.github.blir.file.Design;
import com.github.blir.file.DesignWriter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Blir
 */
public class LifeFrame extends javax.swing.JFrame {

    private final JFileChooser chooser;
    private final Set<Location> clipboard = new HashSet<>();
    private final int defaultCloseOp;

    private Life life;

    /**
     * Creates new form LifeFrame
     *
     * @param defaultCloseOp
     */
    public LifeFrame(int defaultCloseOp) {
        this.defaultCloseOp = defaultCloseOp;
        initComponents();
        setLocationRelativeTo(null);
        chooser = new JFileChooser();
    }

    public void init(Life life) {
        this.life = life;
        addFocusListener(life.listener);
        addKeyListener(life.listener);
        chooser.addActionListener(evt -> {
            System.out.printf("chooser:%s\n", evt);
            if (evt.getActionCommand().equals("ApproveSelection")) {
                if (chooser.getDialogType() == JFileChooser.OPEN_DIALOG) {
                    File file = chooser.getSelectedFile();
                    if (file != null) {
                        open(file);
                    }
                } else if (chooser.getDialogType() == JFileChooser.SAVE_DIALOG) {
                    File file = chooser.getSelectedFile();
                    if (file != null) {
                        boolean json = chooser.getFileFilter().getDescription().equals("JSON files");
                        String desiredExtension = json ? ".json" : ".gol";
                        String path = file.getPath();
                        int delim = path.lastIndexOf('.');
                        if (delim > 0) {
                            String actualExtension = path.substring(delim);
                            if (!actualExtension.equals(desiredExtension)) {
                                file = new File(path + desiredExtension);
                            }
                        } else {
                            file = new File(path + desiredExtension);
                        }
                        save(file);
                    }
                }
            }
        });
        chooser.addChoosableFileFilter(new FileFilter() {

            @Override
            public boolean accept(File file) {
                String s = file.getName().toLowerCase();
                return file.isDirectory() || s.endsWith(".json");
            }

            @Override
            public String getDescription() {
                return "JSON files";
            }
            
        });
        chooser.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File file) {
                String s = file.getName().toLowerCase();
                return file.isDirectory() || s.endsWith(".gol") || s.endsWith(".rle");
            }

            @Override
            public String getDescription() {
                return "Game of Life designs";
            }
        });
    }

    public void open(File file) {
        try {
            clipboard.clear();
            Design design = new DesignReader(file).read();
            life.gen = design.getGeneration();
            clipboard.addAll(design.getDesign());
            onPaste(null);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public void save(File file) {
        try {
            synchronized (life.WORLD_MUTEX) {
                Set<Location> design = Design.make(life.getWorld(), lifePanel1.camX, lifePanel1.camY);
                new DesignWriter(file).write(new Design(design, life.gen));
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public boolean isRunning() {
        return jCheckBoxMenuItem1.getState();
    }

    public boolean doRender() {
        return jCheckBoxMenuItem3.getState();
    }

    public void setState(boolean state) {
        jCheckBoxMenuItem1.setState(state);
    }

    public boolean showColorGuides() {
        return jCheckBoxMenuItem2.getState();
    }
    
    public boolean showGroups() {
        return jCheckBoxMenuItem7.getState();
    }

    public LifePanel getLifePanel() {
        return lifePanel1;
    }

    public Set<Location> getClipboard() {
        return clipboard;
    }
    
    public void setClipboard(Stream<Location> clipboard) {
        this.clipboard.clear();
        clipboard.collect(Collectors.toCollection(() -> this.clipboard));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lifePanel1 = new com.github.blir.gui.LifePanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem14 = new javax.swing.JMenuItem();
        jMenuItem15 = new javax.swing.JMenuItem();
        jMenuItem16 = new javax.swing.JMenuItem();
        jMenuItem17 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jCheckBoxMenuItem2 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem7 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem3 = new javax.swing.JCheckBoxMenuItem();
        jMenuItem18 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem13 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem19 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(defaultCloseOp);

        javax.swing.GroupLayout lifePanel1Layout = new javax.swing.GroupLayout(lifePanel1);
        lifePanel1.setLayout(lifePanel1Layout);
        lifePanel1Layout.setHorizontalGroup(
            lifePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 382, Short.MAX_VALUE)
        );
        lifePanel1Layout.setVerticalGroup(
            lifePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 259, Short.MAX_VALUE)
        );

        jMenu1.setText("File");

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setText("Save");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onSave(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem3.setText("Open");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onLoad(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Simulation");

        jCheckBoxMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
        jCheckBoxMenuItem1.setText("Toggle");
        jCheckBoxMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onToggle(evt);
            }
        });
        jMenu2.add(jCheckBoxMenuItem1);

        jMenuItem1.setText("Change Delay");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onChangeDelay(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        jMenuItem6.setText("Stop at...");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onSetStopAt(evt);
            }
        });
        jMenu2.add(jMenuItem6);

        jMenuItem4.setText("Clear");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onClear(evt);
            }
        });
        jMenu2.add(jMenuItem4);

        jMenuItem7.setText("Interrupt");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onInterrupt(evt);
            }
        });
        jMenu2.add(jMenuItem7);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Navigate");

        jMenuItem12.setText("Go to...");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onGoto(evt);
            }
        });
        jMenu3.add(jMenuItem12);

        jMenuItem8.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_UP, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem8.setText("Furthest Up");
        jMenuItem8.setActionCommand("UP");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onFurthest(evt);
            }
        });
        jMenu3.add(jMenuItem8);

        jMenuItem9.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_RIGHT, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem9.setText("Furthest Right");
        jMenuItem9.setActionCommand("RIGHT");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onFurthest(evt);
            }
        });
        jMenu3.add(jMenuItem9);

        jMenuItem10.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DOWN, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem10.setText("Furthest Down");
        jMenuItem10.setActionCommand("DOWN");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onFurthest(evt);
            }
        });
        jMenu3.add(jMenuItem10);

        jMenuItem11.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_LEFT, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem11.setText("Furthest Left");
        jMenuItem11.setActionCommand("LEFT");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onFurthest(evt);
            }
        });
        jMenu3.add(jMenuItem11);

        jMenuItem14.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_UP, 0));
        jMenuItem14.setText("Up");
        jMenuItem14.setActionCommand("UP");
        jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onDirection(evt);
            }
        });
        jMenu3.add(jMenuItem14);

        jMenuItem15.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_RIGHT, 0));
        jMenuItem15.setText("Right");
        jMenuItem15.setActionCommand("RIGHT");
        jMenuItem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onDirection(evt);
            }
        });
        jMenu3.add(jMenuItem15);

        jMenuItem16.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DOWN, 0));
        jMenuItem16.setText("Down");
        jMenuItem16.setActionCommand("DOWN");
        jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onDirection(evt);
            }
        });
        jMenu3.add(jMenuItem16);

        jMenuItem17.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_LEFT, 0));
        jMenuItem17.setText("Left");
        jMenuItem17.setActionCommand("LEFT");
        jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onDirection(evt);
            }
        });
        jMenu3.add(jMenuItem17);

        jMenuBar1.add(jMenu3);

        jMenu4.setText("Display");

        jCheckBoxMenuItem2.setSelected(true);
        jCheckBoxMenuItem2.setText("Show Color Guides");
        jMenu4.add(jCheckBoxMenuItem2);

        jCheckBoxMenuItem7.setSelected(true);
        jCheckBoxMenuItem7.setText("Show Groups");
        jMenu4.add(jCheckBoxMenuItem7);

        jCheckBoxMenuItem3.setSelected(true);
        jCheckBoxMenuItem3.setText("Toggle Render");
        jMenu4.add(jCheckBoxMenuItem3);

        jMenuItem18.setText("Reset Zoom");
        jMenuItem18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onResetZoom(evt);
            }
        });
        jMenu4.add(jMenuItem18);

        jMenuBar1.add(jMenu4);

        jMenu5.setText("Misc");

        jMenuItem13.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem13.setText("Copy");
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onCopy(evt);
            }
        });
        jMenu5.add(jMenuItem13);

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem5.setText("Paste");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onPaste(evt);
            }
        });
        jMenu5.add(jMenuItem5);

        jMenuItem19.setText("Collision Analysis");
        jMenuItem19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onCollisionAnalysis(evt);
            }
        });
        jMenu5.add(jMenuItem19);

        jMenuBar1.add(jMenu5);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lifePanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lifePanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void onToggle(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onToggle
        life.restart();
    }//GEN-LAST:event_onToggle

    private void onChangeDelay(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onChangeDelay
        String input = JOptionPane.showInputDialog(this, "Enter delay (ms):", life.delayMillis);
        if (input != null) {
            life.delayMillis = Integer.parseInt(input);
        }
    }//GEN-LAST:event_onChangeDelay

    private void onSave(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onSave
        chooser.showSaveDialog(this);
    }//GEN-LAST:event_onSave

    private void onLoad(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onLoad
        chooser.showOpenDialog(this);
    }//GEN-LAST:event_onLoad

    private void onClear(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onClear
        life.clearWorld();
    }//GEN-LAST:event_onClear

    private void onPaste(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onPaste
        life.loadDesign();
    }//GEN-LAST:event_onPaste

    private void onSetStopAt(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onSetStopAt
        String input = JOptionPane.showInputDialog(this, "Enter generation:", life.pausegen);
        if (input != null) {
            life.pausegen = Integer.parseInt(input);
        }
    }//GEN-LAST:event_onSetStopAt

    private void onInterrupt(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onInterrupt
        life.interrupt();
    }//GEN-LAST:event_onInterrupt

    private void onFurthest(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onFurthest
        Location furthest = life.getFurthest(Direction.valueOf(evt.getActionCommand()));
        lifePanel1.camX = furthest.x;
        lifePanel1.camY = furthest.y;
    }//GEN-LAST:event_onFurthest

    private void onGoto(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onGoto
        String input = JOptionPane.showInputDialog(this, "Enter a coordinate pair (e.g. 80, -60): ");
        if (input != null) {
            String[] parts = input.split(",");
            lifePanel1.camX = Integer.parseInt(parts[0].trim());
            lifePanel1.camY = Integer.parseInt(parts[1].trim());
        }
    }//GEN-LAST:event_onGoto

    private void onCopy(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onCopy
        life.listener.copy();
    }//GEN-LAST:event_onCopy

    private void onDirection(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onDirection
        
        LifePanel panel = life.frame.getLifePanel();
        int shamt = panel.objectSize > 0
                ? (panel.getWidth() / panel.objectSize) / 8
                : (panel.getWidth() * (2 - panel.objectSize) / 8);
        switch (evt.getActionCommand()) {
            case "UP":
                panel.camY -= shamt;
                break;
            case "LEFT":
                panel.camX -= shamt;
                break;
            case "RIGHT":
                panel.camX += shamt;
                break;
            case "DOWN":
                panel.camY += shamt;
                break;
        }
    }//GEN-LAST:event_onDirection

    private void onResetZoom(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onResetZoom
        lifePanel1.objectSize = 10;
    }//GEN-LAST:event_onResetZoom

    private void onCollisionAnalysis(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onCollisionAnalysis
        Map<Integer, Counter> hashOccurrences = new HashMap<>();
        Stream<Integer> hashCodes;
        synchronized (life.WORLD_MUTEX) {
            hashCodes = life.getWorld().stream().map(loc -> loc.hashCode());
        }
        hashCodes.forEach(hashCode -> {
            Counter counter = hashOccurrences.get(hashCode);
            if (counter == null) {
                hashOccurrences.put(hashCode, counter = new Counter());
            }
            counter.increment();
        });
        int count = hashOccurrences.values().stream().filter(counter -> counter.count() > 1).collect(Collectors.summingInt(counter -> counter.count() - 1));
        JOptionPane.showMessageDialog(this, count + " collisions.");
        //JOptionPane.showMessageDialog(this, hashOccurrences);
    }//GEN-LAST:event_onCollisionAnalysis

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem2;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem3;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem7;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem18;
    private javax.swing.JMenuItem jMenuItem19;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private com.github.blir.gui.LifePanel lifePanel1;
    // End of variables declaration//GEN-END:variables
}
