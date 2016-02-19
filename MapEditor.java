/*
*
* Author: Grero, Kondagamage Sanjan Chamara
* 
* Student ID: A1204014
*
* Assignment: Map Editor
*
* Subject: Event Driven Computing 
*
* Class: Singapore - Trimester 2, 2013
* 
* Features:
* - limit drag to panel size on a maximized window
* - deselect objects when clicked in a blank area
* - most events are handled properly (ie: prompts users clearly)
* - enable/disable menuitems depend on the state
* 
* Known Bugs:
* - Maximize is limited to main screen's size in a multi-monitor environment (intentional due to the bug mentioned right below)
* - Unable to show up places with location coordinates beyond the maximum size of main screen width or height (can be done using GraphicsEnvironment)
* - Option/Error Dialogs are shown in center relative to the main screen not the center of main window (can be done using custom dialogs)
*
*/

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MapEditor extends JFrame implements ActionListener {
    
	private MenuBar menuBar = new MenuBar();
	private Menu file = new Menu();
	private MenuItem openFile = new MenuItem();
	private MenuItem saveFile = new MenuItem();
        private MenuItem appendFile = new MenuItem();
	private MenuItem quit = new MenuItem();
	private Menu edit = new Menu();
        private MenuItem newPlace = new MenuItem();
	private MenuItem newRoad = new MenuItem(); 
	private MenuItem setStart = new MenuItem();
        private MenuItem unsetStart = new MenuItem();
	private MenuItem setEnd = new MenuItem();
	private MenuItem unsetEnd = new MenuItem();
        private MenuItem delete = new MenuItem();
	private MapReaderWriter mrw = new MapReaderWriter();
        private Map myMap,tmpMap;
        private MapPanel mapPanel;
        private JPanel jp;
        private JLabel td,tip;
        private String fileName="Untitled";
        private JScrollPane scroller;
        private boolean closeok=false;
        
        
	public MapEditor() {
            
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                this.setMinimumSize(new Dimension(765,530));
                this.setMaximizedBounds(new Rectangle(dim));
                this.setMaximumSize(dim);
                this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		this.setTitle(this.fileName + " - Map Editor");
                this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

                this.addWindowListener( new WindowAdapter()
                {
                    @Override
                    public void windowClosing(WindowEvent e)
                    {
                        MapEditor me = (MapEditor)e.getSource();
                        me.closeCall();
                        
                    }
                });
		
                this.addComponentListener(new ComponentListener() {  
                        // component listener
                        @Override
                        public void componentHidden(ComponentEvent e) {
                        //    displayMessage(e.getComponent().getClass().getName() + " --- Hidden");
                        }

                        @Override
                        public void componentMoved(ComponentEvent e) {
                          //  displayMessage(e.getComponent().getClass().getName() + " --- Moved");
                        }

                        @Override
                        public void componentResized(ComponentEvent e) {
                        mapPanel.roadsChanged();
                        }

                        @Override
                        public void componentShown(ComponentEvent e) {
                            //displayMessage(e.getComponent().getClass().getName() + " --- Shown");
                        }
                });

                //add panels

                 jp = new JPanel();
                 jp.setLayout((LayoutManager) new FlowLayout(FlowLayout.LEFT));
                 td = new JLabel("");
                 td.setFont(new Font("default", Font.BOLD, 14));
                 tip=new JLabel("");
                 tip.setFont(new Font("default", Font.PLAIN, 12));
                 jp.add(td);
                 jp.add(tip);
                 
                 myMap = new MapImpl();
                 this.mapPanel = new MapPanel(myMap, this);
                 myMap.addListener(this.mapPanel);
                
                scroller = new JScrollPane(this.mapPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                scroller.setBorder(null);
                scroller.getViewport().addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                            mapPanel.roadsChanged();
                }
                });
                
                this.getContentPane().add(jp, BorderLayout.PAGE_START);
                this.getContentPane().add(scroller, BorderLayout.CENTER);
                
		this.setMenuBar(this.menuBar);
		this.menuBar.add(this.file);
		this.menuBar.add(this.edit);
		this.file.setLabel("File");
                this.edit.setLabel("Edit");
		
		//File menu items
                this.openFile.setLabel("Open"); 
		this.openFile.addActionListener(this);
		this.openFile.setShortcut(new MenuShortcut(KeyEvent.VK_O, false));
		this.file.add(this.openFile);
		
		this.appendFile.setLabel("Append");
		this.appendFile.addActionListener(this);
		this.appendFile.setShortcut(new MenuShortcut(KeyEvent.VK_A, false));
                this.appendFile.setEnabled(true);
		this.file.add(this.appendFile);
                
		this.saveFile.setLabel("Save as");
		this.saveFile.addActionListener(this);
		this.saveFile.setShortcut(new MenuShortcut(KeyEvent.VK_S, false));
                this.saveFile.setEnabled(true);
		this.file.add(this.saveFile);
		
                this.quit.setLabel("Quit");
		this.quit.setShortcut(new MenuShortcut(KeyEvent.VK_Q, false));
		this.quit.addActionListener(this);
		this.file.add(this.quit);
                
                //Edit menu items
                this.newPlace.setLabel("New Place");
		this.newPlace.addActionListener(this);
		this.edit.add(this.newPlace);
                
                this.newRoad.setLabel("New Road");
		this.newRoad.addActionListener(this);
                this.newRoad.setEnabled(false);
		this.edit.add(this.newRoad);
                
                this.setStart.setLabel("Set Start");
		this.setStart.addActionListener(this);
                this.setStart.setEnabled(false);
		this.edit.add(this.setStart);
                
                this.unsetStart.setLabel("Unset Start");
		this.unsetStart.addActionListener(this);
                this.unsetStart.setEnabled(false);
		this.edit.add(this.unsetStart);
                
                this.setEnd.setLabel("Set End");
		this.setEnd.addActionListener(this);
                this.setEnd.setEnabled(false);
		this.edit.add(this.setEnd);
                
                this.unsetEnd.setLabel("Unset End");
		this.unsetEnd.addActionListener(this);
                this.unsetEnd.setEnabled(false);
		this.edit.add(this.unsetEnd);
                
                this.delete.setLabel("Delete");
                this.delete.setShortcut(new MenuShortcut(KeyEvent.VK_D, false));
		this.delete.addActionListener(this);
                this.delete.setEnabled(false);
		this.edit.add(this.delete);
                
                
	} // end constructor
	
        @Override
	public void actionPerformed (ActionEvent e) {
		
                if (e.getSource() == this.quit){
                    this.closeCall();
                }
                
		// if the source was the "open" option
		else if (e.getSource() == this.openFile) {
                        if (this.mapPanel.getSave()){
                            int result = JOptionPane.showConfirmDialog(null, "Save changes to current map?",
                                "Save changes", JOptionPane.YES_NO_OPTION);
                            if (result == JOptionPane.YES_OPTION) {
                                this.savefile();
                            }                     
                        }
                    
			JFileChooser open = new JFileChooser();
                        open.setDialogTitle("Open Map");
                        ExtensionFileFilter filter1 = new ExtensionFileFilter("Map Files (*.map)", new String[] { "MAP" });
                        open.setFileFilter(filter1);
			int option = open.showOpenDialog(this);
			if (option == JFileChooser.APPROVE_OPTION) {
				
				boolean readsuccess=true;
                                String file= open.getSelectedFile().getName();
                                try {
                                    tmpMap= new MapImpl();
                                    Reader r = new FileReader(open.getSelectedFile().getPath());
                                    this.fileName=open.getSelectedFile().getName();
                                    mrw.read(r, tmpMap);
                                    
				} catch (MapFormatException ex) {
                                    
                                        readsuccess=false;
                                        String[] errorstring=ex.toString().split(":");
                                        int success = Integer.parseInt(errorstring[0]) - 1;
                                        if (success>0){
                                            int result = JOptionPane.showConfirmDialog(null, "Error while parsing line " + errorstring[0] +" in \"" + file + "\"\n" + ex.toString() +"\n\nOpen file with data until line "+ success +"?",
                                            "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                                            if (result == JOptionPane.YES_OPTION) {
                                                readsuccess = true;
                                            }
                                        }
                                        else{
                                            JOptionPane.showMessageDialog(null, "Error while parsing line " + errorstring[0] +" in \"" + file + "\"\n" + ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                                        }
                                
				}
                                catch (IOException ex){
                                      readsuccess=false;
                                      JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                                }
                                if (readsuccess){
                                   this.setTitle(this.fileName + " - Map Editor");
                                   this.mapPanel.removeAll();
                                   emptymap(myMap);
                                   copymap(tmpMap,myMap);
                                   this.resizeCall();
                                }
			}
		}
                
                //appender 
                else if (e.getSource() == this.appendFile) {
		JFileChooser append = new JFileChooser();
                append.setApproveButtonText("Append");
                append.setApproveButtonToolTipText("Append selected file");
                append.setDialogTitle("Append Map");
                ExtensionFileFilter filter1 = new ExtensionFileFilter("Map Files (*.map)", new String[] { "MAP" });
                append.setFileFilter(filter1);
		int option = append.showOpenDialog(this);

			if (option == JFileChooser.APPROVE_OPTION) {
				
                                tmpMap= new MapImpl();
                                copymap(myMap,tmpMap);
                                
                                boolean readsuccess=true;
                                String file= append.getSelectedFile().getName();
                                
				try {
                                    Reader r = new FileReader(append.getSelectedFile().getPath());
                                    
                                    mrw.read(r, tmpMap);

                                } catch (MapFormatException ex) {
                                    
                                        readsuccess=false;
                                        String[] errorstring=ex.toString().split(":");
                                        int success = Integer.parseInt(errorstring[0]) - 1;
                                        if (success>0){
                                            int result = JOptionPane.showConfirmDialog(null, "Error while parsing line " + errorstring[0] +" in \"" + file + "\"\n" + ex.toString() +"\n\nApply changes until line "+ success +"?",
                                            "File append error", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                                            if (result == JOptionPane.YES_OPTION) {
                                                readsuccess = true;
                                            }
                                        }
                                        else{
                                            JOptionPane.showMessageDialog(null, "Error while parsing line " + errorstring[0] +" in \"" + file + "\"\n" + ex.toString(), "File Append Error", JOptionPane.ERROR_MESSAGE);
                                        }
                                
				}
                                catch (IOException ex){
                                      readsuccess=false;
                                      JOptionPane.showMessageDialog(null, ex.getMessage(), "File Append Error", JOptionPane.ERROR_MESSAGE);
                                }
                                
                                 if (readsuccess==true){
                                   mapPanel.removeAll();
                                   emptymap(myMap);
                                   copymap(tmpMap,myMap);
                                   mapPanel.setSave(true);
                                   this.resizeCall();
                                }
			}
                    }
		
                    else if (e.getSource() == this.saveFile) {
                       this.savefile();
                    }
                
                    else if (e.getSource() == this.newPlace) {
                       
                        String pname = JOptionPane.showInputDialog(null, "Place Name:","Add New place",JOptionPane.PLAIN_MESSAGE);
                        
                        try{
                            if (pname!=null){
                            myMap.newPlace(pname, ((this.getContentPane().getWidth())/2)-16, ((this.getContentPane().getHeight())/2)-16);
                            mapPanel.otherChanged();
                            mapPanel.setSave(true);
                            }
                        }
                        catch(Exception ex){
                            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    else if (e.getSource() == this.newRoad) {
                        if (myMap.getPlaces().size()<2){
                            JOptionPane.showMessageDialog(null, "Add at least two places to create a road", "Warning", JOptionPane.WARNING_MESSAGE);
                        }
                        else{                      
                            JTextField rname = new JTextField();
                            JTextField rlength = new JTextField();

                            final JComponent[] inputs = new JComponent[] {
                            new JLabel("Road name:"),
                            rname,
                            new JLabel("Road length:"),
                            rlength
                            };
                            int status = JOptionPane.showConfirmDialog(null, inputs,"Add new road", JOptionPane.OK_CANCEL_OPTION);
                            
                            if (status == JOptionPane.OK_OPTION){

                              String road = rname.getText();
                              String length = rlength.getText();
                              mapPanel.setNRMode(road, length);

                            }
                         }  
                    }
                    else if (e.getSource() == this.setStart) {
                        Set<Place> myplaces =  mapPanel.getSelPlaces();

                        int size = myplaces.size();
                        
                        if (size > 1){
                        JOptionPane.showMessageDialog(null, "More than one place is selected", "Warning", JOptionPane.WARNING_MESSAGE);
                        }
                        else if (size == 1){
                            for (Place p : myplaces){
                            if (p!=myMap.getStartPlace()){
                            myMap.setStartPlace(p);
                            mapPanel.setSave(true);
                            }
                            }
                        }
                        else{
                        JOptionPane.showMessageDialog(null, "No place is selected", "Warning", JOptionPane.WARNING_MESSAGE);
                        }  
                    }
                    else if (e.getSource() == this.unsetStart) {
                    if (myMap.getStartPlace()!=null){
                    myMap.setStartPlace(null);
                    mapPanel.setSave(true);
                    }
                        
                    }
                    else if (e.getSource() == this.setEnd) {
                        Set<Place> myplaces =  mapPanel.getSelPlaces();

                        int size = myplaces.size();
                        
                        if (size > 1){
                        JOptionPane.showMessageDialog(null, "More than one place is selected", "Warning", JOptionPane.WARNING_MESSAGE);
                        }
                        else if (size == 1){
                            for (Place p : myplaces){
                            if (p!=myMap.getEndPlace()){
                            myMap.setEndPlace(p);
                            mapPanel.setSave(true);
                            }
                            }
                        }
                        else{
                        JOptionPane.showMessageDialog(null, "No place is selected", "Warning", JOptionPane.WARNING_MESSAGE);
                        }
                    
                    }
                    else if (e.getSource() == this.unsetEnd) {
                    if (myMap.getEndPlace()!=null){
                        myMap.setEndPlace(null);
                        mapPanel.setSave(true);
                    }
                    }
                    else if (e.getSource() == this.delete) {
                        
                      Set<Place> myplaces =  mapPanel.getSelPlaces();
                      Set<Road> myroads =  mapPanel.getSelRoads();
                    
                      if ((myplaces.isEmpty())&&(myroads.isEmpty())){
                        JOptionPane.showMessageDialog(null, "Empty Selection", "Warning", JOptionPane.WARNING_MESSAGE);
                      } 
                      else{
                        if (!myroads.isEmpty()){                    
                        for (Road r : myroads ){
                         myMap.deleteRoad(r);
                        }
                        } 
                        if (!myplaces.isEmpty()){
                        for (Place p : myplaces ){
                         myMap.deletePlace(p);
                        }
                        
                          }
                        mapPanel.setSave(true);
                        mapPanel.otherChanged();
                        
                     }
                      
                      
                    }
                
	} // end action listener
        
    public static void main(String args[]) {
       MapEditor app = new MapEditor();
       app.setVisible(true);
    }
    
    private void copymap(Map srcMap, Map destMap){
       
        Set<Place> newPlaces = srcMap.getPlaces();
        for (Place p : newPlaces){
            destMap.newPlace(p.getName(), p.getX(), p.getY());
        }

        Set<Road> newRoads = srcMap.getRoads();
        for (Road r : newRoads){
            destMap.newRoad(destMap.findPlace(r.firstPlace().getName()) , destMap.findPlace(r.secondPlace().getName()), r.roadName(), r.length());
        }
        if (srcMap.getStartPlace()!= null){
        Place start = destMap.findPlace(srcMap.getStartPlace().getName());
        destMap.setStartPlace(start);
        }
        else{
        destMap.setStartPlace(null);
        }
        
        if (srcMap.getEndPlace()!= null){
        Place end = destMap.findPlace(srcMap.getEndPlace().getName());
        destMap.setEndPlace(end);
        }
        else{
        destMap.setEndPlace(null);    
        }
        
        
    }
    
    private void emptymap(Map srcMap){
    
        Set<Place> newPlaces = srcMap.getPlaces();
        Collection<Place> rmPlc = new LinkedList<Place>(newPlaces);
        for (Place p : newPlaces) {
            rmPlc.add(p);
        }
        newPlaces.removeAll(rmPlc);

        Set<Road> newRoads = srcMap.getRoads();
        Collection<Road> rmRd = new LinkedList<Road>(newRoads);
        for (Road r : newRoads) {
            rmRd.add(r);
        }
        newRoads.removeAll(rmRd);
        
        srcMap.setStartPlace(null);
        srcMap.setEndPlace(null);
        
    }
    
    private void savefile(){
    
        //warn the user if saving empty map?
        //if (myMap.getPlaces().isEmpty()){
        //JOptionPane.showMessageDialog(null, "Empty Map. Nothing to Save", "Warning", JOptionPane.WARNING_MESSAGE);
        //  }

        boolean acceptable = false;
        File f = null;
        do {

            f = new File(this.fileName);
            JFileChooser FileChooser = new JFileChooser();
            FileChooser.setDialogTitle("Save Map");
            FileChooser.setSelectedFile(f);
            ExtensionFileFilter filter1 = new ExtensionFileFilter("Map Files (*.map)", new String[] { "MAP" });
            FileChooser.setFileFilter(filter1);

            if (FileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                String theFilepath = FileChooser.getSelectedFile().getAbsolutePath();

                if (theFilepath.lastIndexOf(".map")== -1){
                        theFilepath += ".map";
                } 
                f = new File(theFilepath);

                if (f.exists()) {
                    int result = JOptionPane.showConfirmDialog(null, f.getName() + " already exists, overwrite?",
                        "Existing file", JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        acceptable = true;
                    }
                } 
                else {
                    acceptable = true;
                }
            } else {
                acceptable = true;
                f=null;
                if (this.closeok==true){
                this.closeok=false;
                }
            }
        } while (!acceptable);

        if ((acceptable)&&(f!=null)){
                try {
                    this.fileName=f.getName();
                    Writer out = new BufferedWriter(new FileWriter(f.getAbsolutePath()));   
                    mrw.write(out, myMap);
                    out.close();
                    this.setTitle(this.fileName + " - Map Editor");
                    mapPanel.setSave(false);
                } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                }
        }
        
    
    }
    
    private void closeCall(){
    this.closeok=true;
    if (mapPanel.getSave()){
          int result = JOptionPane.showConfirmDialog(null, "Save changes to current map?",
              "Save changes", JOptionPane.YES_NO_CANCEL_OPTION);
          if (result == JOptionPane.YES_OPTION) {
              this.savefile();
          }
          else if (result == JOptionPane.CANCEL_OPTION){
              this.closeok=false;
          }
      }
        if (this.closeok){
        this.dispose();//dispose all resources
        System.exit(0);//close the application
        }
    }
    
    public void setTriptext(String d){
        this.td.setText(d);
    }
    
    public void setTiptext(String t){
        if ((!t.equals(""))&&(!this.td.getText().equals(""))){
        this.tip.setText(" ("+t+")");
        }
        else{
        this.tip.setText(t);
        }
    }
    
    public void enableNewRoad(boolean b){
    this.newRoad.setEnabled(b);
    }
    
    public void enableEditMenuItems(boolean b){
    this.setEnd.setEnabled(b);
    this.unsetEnd.setEnabled(b);
    this.setStart.setEnabled(b);
    this.unsetStart.setEnabled(b);
    this.delete.setEnabled(b);
    }

    
    private void resizeCall(){
            //setframesize
        int x=0,y=0;
        Set<Place>  actualPlaces= myMap.getPlaces();
        for( Place p: actualPlaces ){
        if (p.getX()>=x)
            x = p.getX();
        
        if (p.getY()>=y)
            y = p.getY();
        }
        
        if ((x>this.getMaximumSize().width)||(y>this.getMaximumSize().height)){
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        JOptionPane.showMessageDialog(null, "Map contains places outside maximum viewable area of your main screen", "Warning", JOptionPane.WARNING_MESSAGE);
        }
        else{
        this.setSize(x+100, y+150);
        }
        
       

    }
    
    

}