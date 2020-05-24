package main;
/*
 * This is a little tricky to get to work in Eclipse
 * First step: add "implementation 'de.saxsys:mvvmfx:1.8.0'" to the 
 * dependencies in build.gradle if not already present
 * Second step (if needed): install e(fx)clipse: Help->Install new software->
 * All available sites->e(fx)clipse. If this fails, maybe restart Eclipse as an 
 * administrator?
 * Third step (if needed): go to project's Build Path and under "Libraries" 
 * remove JRE System Library and add library back.
 */

import java.util.ResourceBundle;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.MvvmFX;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.MainView;
import viewmodel.MainViewModel;

public class Starter extends Application{
	ResourceBundle resourceBundle = ResourceBundle.getBundle("bundles.menu.mainMenuBundle");
    public static void main(String...args){
        Application.launch(args);
    }

    @Override
    public void start(Stage stage){
    	MvvmFX.setGlobalResourceBundle(resourceBundle);
    	//Demo to show what's accomplished so far
        stage.setTitle("CARP: Demo");
//        Main view
        ViewTuple<MainView, MainViewModel> viewTuple =
        		FluentViewLoader
        		.fxmlView(MainView.class)
        		.load();
        Parent root = viewTuple.getView();
        stage.setScene(new Scene(root));
        stage.show();
    }
}