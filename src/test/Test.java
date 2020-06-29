package test;

import gui.ScreenManager;

import datastructures.table.component.FileStructure;

import java.util.ArrayList;

public class Test {

    private ScreenManager screenManager;

    public static void main(String[] args) {

        /*ArrayList<User> users = UserIO.readAndReturnUserData();

        for(User u : users)
            System.out.println(u);

        Scanner sc = new Scanner(System.in);
        String userName = sc.nextLine();
        ArrayList<String> priviliges = new ArrayList<>();
        while(true) {
            String priv = sc.nextLine();
            if(priv.equals("exit")) break;
            else
                priviliges.add(priv);
        }

        UserIO.writeUserData(userName, priviliges);*/

        String string = "t1 col1 Number 3 , col2 Char 2 , col3 Char 7 : t2  col1 Char 16 : t3  col1 Number 4 , col2 Char 5 ";
        String b = "bob t1 priv1 priv2 priv3 , t2 priv1 priv3 , t3 : john t2 priv2 priv1 : dan t1 priv3 priv9 , t3 priv1";

        String[] tables = string.split(":");
        //for(String x : tables) System.out.println(x);

        ArrayList<String[]> data = new ArrayList<>();

        for(String table : tables) {
            String[] columns = table.split("\\s+");
            data.add(columns);
        }
        for(String[] x : data) {
            for(String xx : x) {
                System.out.println(xx);
            }System.out.println("========");
        }

       // launch(args);
    }

   /* @Override
    public void start(Stage primaryStage) throws Exception {
        /*Label l1 = new Label("Terminal scene");
        Label l2 = new Label("Schema scene");
        Label l3 = new Label("Users scene");
        Label l4 = new Label("Help scene");

        Button b1 = new Button("Terminal");
        Button b2 = new Button("Schema");
        Button b3 = new Button("Users");
        Button b4 = new Button("Help");

        b1.setOnAction(e -> primaryStage.setScene(screens[0].getScene()));
        b2.setOnAction(e -> primaryStage.setScene(screens[1].getScene()));
        b3.setOnAction(e -> primaryStage.setScene(screens[2].getScene()));
        b4.setOnAction(e -> primaryStage.setScene(screens[3].getScene()));

        HBox buttonLayout = new HBox();
        buttonLayout.getChildren().addAll(b1, b2, b3, b4);

        BorderPane terminalLayout = new BorderPane();
        terminalLayout.setTop(buttonLayout);
        terminalLayout.setCenter(l1);
        BorderPane schemaLayout = new BorderPane();
        schemaLayout.setTop(buttonLayout);
        schemaLayout.setCenter(l2);
        BorderPane usersLayout = new BorderPane();
        usersLayout.setTop(buttonLayout);
        usersLayout.setCenter(l3);
        BorderPane helpLayout = new BorderPane();
        helpLayout.setTop(buttonLayout);
        helpLayout.setCenter(l4);

        terminal = new Scene(terminalLayout, 1080, 720);
        schema = new Scene(schemaLayout, 1080, 720);
        users = new Scene(usersLayout, 1080, 720);
        help = new Scene(helpLayout, 1080, 720);*/

        //screenManager = ScreenManager.getInstance(primaryStage);
        //screenManager.setCurrentScreen(ScreenManager.HELP_SCREEN);
        //primaryStage.setScene(screenManager.setCurrentScreen(););
       // primaryStage.show();
    //}
}
