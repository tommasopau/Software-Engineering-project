package it.polimi.ingsw.view;

import it.polimi.ingsw.distributed.Messages.SocketRMIMessages;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.ItemTile;
import it.polimi.ingsw.model.ItemTileType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;

/**
 * The Graphic User Interface
 */
public class GraphicUI extends JFrame implements View{

    private JPanel mainPanel;
    private GridBagConstraints constraints;
    private CustomLabel boardImage;
    //mainPanel dimensions:
    int width = 680;
    int height = 650;
    CustomLabel background;
    CustomLabel labelNorth;
    CustomLabel labelCenter;
    CustomLabel labelSouth;
    JLabel text; //the text with the instructions for the user
    JTextField textField; //where the user will put the info
    JButton okButton;
    List<JButton> joinButtons = new ArrayList<>(); //the buttons to join to the lobbies
    JLabel boardGrid; //the board grid on which the tiles are placed
    JLabel bookshelfGrid; //the bookshelf grid on which the tiles are placed
    List<JLabel> tilesTaken = new ArrayList<>();
    JLabel[][] bookshelfCells;

    //Values returned to the client
    int numberOfPlayers;
    String nickname;
    boolean newGame;
    String gameName;
    int numberOfTilesWanted;
    List<Integer> coordinates = new ArrayList<>();
    List<Integer> order = new ArrayList<>();
    int column;

    /**
     * Method used to create a custom label for every type of tile
     * @param tile ItemTile
     * @param dim of the tile
     * @return the CustomLabel with the image of the corresponded tile
     */
    private CustomLabel createItemTileLabel(ItemTile tile, int dim){
        if(tile.getItemTileType() == ItemTileType.CAT){
            if(tile.getType() == 1){
                return new CustomLabel("item_tiles/Gatti1.1.png", dim, dim);
            }else if(tile.getType() == 2){
                return new CustomLabel("item_tiles/Gatti1.2.png", dim, dim);
            }else if(tile.getType() == 3){
                return new CustomLabel("item_tiles/Gatti1.3.png", dim, dim);
            }
        }else if(tile.getItemTileType() == ItemTileType.BOOK){
            if(tile.getType() == 1){
                return new CustomLabel("item_tiles/Libri1.1.png", dim, dim);
            }else if(tile.getType() == 2){
                return new CustomLabel("item_tiles/Libri1.2.png", dim, dim);
            }else if(tile.getType() == 3){
                return new CustomLabel("item_tiles/Libri1.3.png", dim, dim);
            }
        }else if(tile.getItemTileType() == ItemTileType.GAME){
            if(tile.getType() == 1){
                return new CustomLabel("item_tiles/Giochi1.1.png", dim, dim);
            }else if(tile.getType() == 2){
                return new CustomLabel("item_tiles/Giochi1.2.png", dim, dim);
            }else if(tile.getType() == 3){
                return new CustomLabel("item_tiles/Giochi1.3.png", dim, dim);
            }
        }else if(tile.getItemTileType() == ItemTileType.FRAME){
            if(tile.getType() == 1){
                return new CustomLabel("item_tiles/Cornici1.1.png", dim, dim);
            }else if(tile.getType() == 2){
                return new CustomLabel("item_tiles/Cornici1.2.png", dim, dim);
            }else if(tile.getType() == 3){
                return new CustomLabel("item_tiles/Cornici1.3.png", dim, dim);
            }
        }else if(tile.getItemTileType() == ItemTileType.TROPHIE){
            if(tile.getType() == 1){
                return new CustomLabel("item_tiles/Trofei1.1.png", dim, dim);
            }else if(tile.getType() == 2){
                return new CustomLabel("item_tiles/Trofei1.2.png", dim, dim);
            }else if(tile.getType() == 3){
                return new CustomLabel("item_tiles/Trofei1.3.png", dim, dim);
            }
        }else if(tile.getItemTileType() == ItemTileType.PLANT){
            if(tile.getType() == 1){
                return new CustomLabel("item_tiles/Piante1.1.png", dim, dim);
            }else if(tile.getType() == 2){
                return new CustomLabel("item_tiles/Piante1.2.png", dim, dim);
            }else if(tile.getType() == 3){
                return new CustomLabel("item_tiles/Piante1.3.png", dim, dim);
            }
        }
        return new CustomLabel("empty", dim, dim);
    }

    /**
     * This method is used to avoid duplicate code since it is necessary to recreate the same initial graphics multiple
     * times.
     * @param insertText the text that the user sees
     */
    private void settingFirstGraphic(String insertText){
        //Inserting a background at the bottom of the frame
        labelSouth = new CustomLabel("publisher_material/banner_1386x400px.png", width, height/3);
        mainPanel.add(labelSouth, BorderLayout.SOUTH);

        //Inserting a background at the center of the frame
        labelCenter = new CustomLabel("misc/sfondo_parquet.jpg", width, height/3);
        labelCenter.setLayout(new BoxLayout(labelCenter, BoxLayout.Y_AXIS));
        labelCenter.add(Box.createVerticalGlue());

        //Inserting the text
        text = new JLabel(insertText);
        text.setForeground(Color.WHITE);
        Font font = new Font("Arial", Font.BOLD, 17);
        text.setFont(font);
        text.setHorizontalAlignment(JLabel.CENTER);
        text.setAlignmentX(Component.CENTER_ALIGNMENT);
        labelCenter.add(text);

        labelCenter.add(Box.createVerticalStrut(25));

        textField = new JTextField("");
        textField.setColumns(20);
        textField.setAlignmentX(Component.CENTER_ALIGNMENT);
        textField.setMaximumSize(textField.getPreferredSize());
        labelCenter.add(textField);

        labelCenter.add(Box.createVerticalStrut(25));

        okButton = new JButton("OK");
        okButton.setFont(font);
        okButton.setBackground(Color.WHITE);
        okButton.setForeground(Color.BLACK);
        okButton.setEnabled(false);
        okButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        labelCenter.add(okButton);

        labelCenter.add(Box.createVerticalGlue());
        mainPanel.add(labelCenter, BorderLayout.CENTER);
        pack();
        setVisible(true);
    }

    /**
     * Method that creates the initial frame (with its components) of the game
     */
    @Override
    public void startNewGame(){
        setTitle("My Shelfie");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(width, height));
        //setResizable(false);
        mainPanel = new JPanel(new BorderLayout());

        //Wood
        labelNorth = new CustomLabel("misc/base_pagina2.jpg", width, height/3);
        mainPanel.add(labelNorth, BorderLayout.NORTH);

        //Title MyShelfie
        JLabel title = new CustomLabel("publisher_material/Title_2000x618px.png", 500, 155);
        EmptyBorder emptyBorderTitle = new EmptyBorder(20, 0, 20, 0);
        title.setBorder(emptyBorderTitle);
        labelNorth.setLayout(new FlowLayout(FlowLayout.CENTER));
        labelNorth.add(title);

        add(mainPanel);
        pack();
        setVisible(true);
    }

    /**
     * Method that shows the list of the available games' lobbies
     * @param gameName the list with the names of the available games
     * @param numberOfPlayersJoined the number of players that have joined the specific game
     * @param numberOfPlayersGame the maximum number of players of the specific game
     */
    @Override
    public void printAvailableGames(List<String> gameName, List<Integer> numberOfPlayersJoined, List<Integer> numberOfPlayersGame)
    {
        if(gameName.size() == 0) {
            newGame = true;
            settingFirstGraphic("There are no available games right now. Create a new game inserting here its name:");
        }else{
            newGame = false;
            //Printing "choose the lobby" text
            labelCenter = new CustomLabel("misc/base_pagina2.jpg", width, 50);
            labelCenter.setLayout(new FlowLayout());
            text = new JLabel("These are the available lobbies, choose one of them to start the game or create a new one:");
            text.setForeground(Color.WHITE);
            Font font = new Font("Arial", Font.BOLD, 15);
            text.setFont(font);
            text.setHorizontalAlignment(JLabel.CENTER);
            labelCenter.add(text);
            mainPanel.add(labelCenter, BorderLayout.CENTER);

            //Creating the lobbies
            labelSouth = new CustomLabel("misc/sfondo_parquet.jpg", width, 470);
            labelSouth.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 30));
            for(int i = 0; i < gameName.size(); i++){
                JLabel lobby = new CustomLabel("misc/base_pagina2.jpg", 150, 150);
                lobby.setLayout(new BorderLayout());
                lobby.setBorder(new LineBorder(Color.WHITE, 5));

                //Inserting the text of each lobby
                JLabel lobbyName = new JLabel(gameName.get(i));
                JLabel players = new JLabel(numberOfPlayersJoined.get(i) + " / " + numberOfPlayersGame.get(i) + " players");

                //Setting position, font, color and dimension of the text
                lobbyName.setForeground(Color.WHITE);
                Font lobbyFont = new Font("Arial", Font.BOLD, 30);
                lobbyName.setFont(lobbyFont);
                lobbyName.setHorizontalAlignment(JLabel.CENTER);
                EmptyBorder emptyBorderLobby = new EmptyBorder(10, 0, 0, 0);
                lobbyName.setBorder(emptyBorderLobby);

                players.setForeground(Color.WHITE);
                Font playersFont = new Font("Arial", Font.BOLD, 15);
                players.setFont(playersFont);
                players.setHorizontalAlignment(JLabel.CENTER);
                lobby.add(lobbyName, BorderLayout.NORTH);
                lobby.add(players, BorderLayout.CENTER);

                //Inserting a button in each lobby
                JButton joinButton = new JButton("JOIN");
                lobby.add(joinButton, BorderLayout.SOUTH);
                joinButton.setFont(playersFont);
                joinButton.setBackground(Color.WHITE);
                joinButton.setForeground(Color.BLACK);
                joinButton.setEnabled(true);
                if(Objects.equals(numberOfPlayersJoined.get(i), numberOfPlayersGame.get(i))){
                    joinButton.setEnabled(false);
                }
                joinButtons.add(joinButton);

                labelSouth.add(lobby);
            }

            //Inserting the text field and the button to create a new game
            JLabel newGameLabel = new CustomLabel("misc/base_pagina2.jpg", 400, 70);
            newGameLabel.setLayout(new BorderLayout());
            newGameLabel.setBorder(new LineBorder(Color.WHITE, 5));
            JLabel text = new JLabel("Insert here the name of the new game and press 'OK'");
            text.setFont(font);
            text.setHorizontalAlignment(JLabel.CENTER);
            text.setForeground(Color.WHITE);
            EmptyBorder emptyBorderLobby = new EmptyBorder(5, 0, 5, 0);
            text.setBorder(emptyBorderLobby);
            newGameLabel.add(text, BorderLayout.NORTH);
            textField = new JTextField("");
            newGameLabel.add(textField, BorderLayout.CENTER);
            okButton = new JButton("OK");
            okButton.setFont(font);
            okButton.setBackground(Color.WHITE);
            okButton.setForeground(Color.BLACK);
            okButton.setEnabled(false);
            newGameLabel.add(okButton, BorderLayout.EAST);

            //Adding an empty label to create some space in the bottom of the newGameLabel
            JLabel emptyLabelSouth = new JLabel();
            EmptyBorder emptyBorderLabel = new EmptyBorder(0, 0, 5, 0);
            emptyLabelSouth.setBorder(emptyBorderLabel);
            emptyLabelSouth.setOpaque(false);
            newGameLabel.add(emptyLabelSouth, BorderLayout.SOUTH);
            labelSouth.add(newGameLabel);

            mainPanel.add(labelSouth, BorderLayout.SOUTH);
        }
        add(mainPanel);
        pack();
        setVisible(true);
    }

    /**
     * Method used to request the new game's name
     * @return the name of the game
     */
    @Override
    public String askGameName() {
        if(newGame){
            okButton.setEnabled(true);
            okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    gameName = textField.getText();
                }
            });

            while (gameName == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }

        }else{
            okButton.setEnabled(true);
            for(JButton joinButton : joinButtons){
                joinButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        //Obtaining the component in the "north" position of the lobby label that is the name of the game selected
                        JButton button = (JButton) e.getSource();
                        LayoutManager layoutManager = button.getParent().getLayout();
                        BorderLayout borderLayout = (BorderLayout) layoutManager;
                        JLabel lobbyName = (JLabel) borderLayout.getLayoutComponent(BorderLayout.NORTH);
                        gameName = lobbyName.getText();
                    }
                });
            }

            okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    gameName = textField.getText();
                }
            });

            while (gameName == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }

            labelCenter.removeAll();
            labelSouth.removeAll();
            mainPanel.remove(labelCenter);
            mainPanel.remove(labelSouth);
            //Returning to the same configuration
            settingFirstGraphic("");
        }

        pack();
        setVisible(true);
        return gameName;
    }

    /**
     * Method that requests the player's nickname for the game
     * @return the nickname
     */
    @Override
    public String askNickname() {
        nickname = null;
        text.setText("Insert here your nickname and press 'OK' to join to the game:");
        textField.setText("");
        okButton.setEnabled(true);
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nickname = textField.getText();
            }
        };
        okButton.addActionListener(actionListener);
        setVisible(true);
        while (nickname == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }
        okButton.removeActionListener(actionListener);
        okButton.setEnabled(false);
        setTitle("MyShelfie - " + nickname);
        return nickname;
    }

    /**
     * Method that requests the number of players for the game
     * @return the number of players
     */
    @Override
    public int askNumberOfPlayers() {
        numberOfPlayers = -1;
        text.setText("Insert here the number of players and click 'OK':");
        textField.setText("");
        okButton.setEnabled(true);
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    numberOfPlayers = Integer.parseInt(textField.getText());
                    if (numberOfPlayers < 2) {
                        text.setText("Minimum number of players is 2, try again and press 'OK': ");
                    } else if (numberOfPlayers > 4) {
                        text.setText("Maximum number of players is 4, try again and press 'OK': ");
                    }
                    textField.setText("");
                } catch (NumberFormatException ex) {
                    text.setText("The number you inserted is not an integer, try again and press 'OK': ");
                    textField.setText("");
                }
            }
        };
        okButton.addActionListener(actionListener);
        setVisible(true);
        while (numberOfPlayers < 2 || numberOfPlayers > 4 ) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }
        okButton.removeActionListener(actionListener);
        okButton.setEnabled(false);
        return numberOfPlayers;
    }

    /**
     * Method used to advise a general error message
     * @param message of error
     */
    @Override
    public void printMessage(SocketRMIMessages message) {
        if(!message.getErrorDescription().equals("")){
            JDialog dialog = new JDialog(this, "Error Message", true);
            dialog.setLayout(new BorderLayout());

            //Adding the error message
            JLabel error = new JLabel(message.getErrorDescription());
            EmptyBorder border = new EmptyBorder(0, 15, 0, 15);
            error.setBorder(border);
            error.setHorizontalAlignment(JLabel.CENTER);
            dialog.add(error, BorderLayout.CENTER);

            //Setting the dimension and the position of the JDialog
            dialog.setSize(600, 200);
            dialog.setLocationRelativeTo(this);

            //Adding a button
            JButton errorButton = new JButton("OK");
            dialog.add(errorButton, BorderLayout.SOUTH);

            //Adding an icon and its space from the left border
            JLabel icon = new CustomLabel("publisher_material/Box_280x280px.png", 100, 100);
            dialog.add(icon, BorderLayout.WEST);
            icon.setBorder(border);

            //When the user presses the button, the error window closes itself
            errorButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialog.dispose();
                }
            });

            dialog.pack();
            dialog.setVisible(true);
        }
    }

    /**
     * Method used to inform that all the players are joining the game
     */
    @Override
    public void printAllPlayersJoining() {
        labelCenter.removeAll();
        labelCenter.setLayout(new BorderLayout());

        text = new JLabel("Waiting that all players join the game...");
        text.setForeground(Color.WHITE);
        Font font = new Font("Arial", Font.BOLD, 25);
        text.setFont(font);
        text.setHorizontalAlignment(JLabel.CENTER);

        labelCenter.add(text, BorderLayout.CENTER);
        pack();
        setVisible(true);
    }

    /**
     * Method that informs that all the players have joined the game
     */
    @Override
    public void printAllPlayersHaveJoined() {
        text.setText("All players have joined the game");
        pack();
        setVisible(true);

        try {
            Thread.sleep(1800);
        } catch (InterruptedException e) {
            System.err.println("Interrupted Exception: " + e.getMessage());
        }

    }

    /**
     * Method that shows the current common goal cards for the game
     * @param commonGoalCardsDescription a list of the common goal cards' descriptions
     * @param commonGoalPoints a list of the common goal points
     */
    public void printCommonGoalCards(List<String> commonGoalCardsDescription, List<Integer> commonGoalPoints) {
        boolean found1=false;
        boolean found2=false;
        int firstcard=0;
        int secondcard=0;
        int firstpoint = 0;
        int secondpoint = 0;
        final List <CommonGoalCard> list = new ArrayList<>();
        CommonGoalCard card1 = new CommonGoalCard1_2(numberOfPlayers, 2, 6);
        list.add(card1);
        CommonGoalCard card2 = new CommonGoalCard1_2(numberOfPlayers, 4, 4);
        list.add(card2);
        CommonGoalCard card3 = new CommonGoalCard3(numberOfPlayers);
        list.add(card3);
        CommonGoalCard card4 = new CommonGoalCard4(numberOfPlayers);
        list.add(card4);
        CommonGoalCard card5 = new CommonGoalCard5(numberOfPlayers);
        list.add(card5);
        CommonGoalCard card6 = new CommonGoalCard6(numberOfPlayers);
        list.add(card6);
        CommonGoalCard card7 = new CommonGoalCard7(numberOfPlayers);
        list.add(card7);
        CommonGoalCard card8 = new CommonGoalCard8(numberOfPlayers);
        list.add(card8);
        CommonGoalCard card9 = new CommonGoalCard9(numberOfPlayers);
        list.add(card9);
        CommonGoalCard card10 = new CommonGoalCard10(numberOfPlayers);
        list.add(card10);
        CommonGoalCard card11 = new CommonGoalCard11(numberOfPlayers);
        list.add(card11);
        CommonGoalCard card12 = new CommonGoalCard12(numberOfPlayers);
        list.add(card12);

        for(int i=0;i<12;i++){
            if(list.get(i).getDescription().equals(commonGoalCardsDescription.get(0))){

                found1=true;
                firstcard=i+1;
                firstpoint = commonGoalPoints.get(0);
            }
            if(list.get(i).getDescription().equals(commonGoalCardsDescription.get(1))){

                found2=true;
                secondcard=i+1;
                secondpoint = commonGoalPoints.get(1);
            }
            if(found1 && found2) break;
        }

        CustomLabel firstLabelCard = null;
        CustomLabel secondLabelCard = null;
        CustomLabel firstPoints = null;
        CustomLabel secondPoints = null;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.gridheight = 1;

        GridBagConstraints firstConstraints = new GridBagConstraints();
        firstConstraints.fill = GridBagConstraints.BOTH;
        firstConstraints.weightx = 1.0;
        firstConstraints.weighty = 1.0;
        firstConstraints.insets = new Insets(35, 85, 35, 15);

        firstConstraints.gridx = 1;
        firstConstraints.gridy = 0;
        firstConstraints.gridwidth = 2;
        firstConstraints.gridheight = 1;

        int dimPoint = 100;

        switch(firstpoint){
            case 0:
                firstPoints =new CustomLabel("scoring_tokens/scoring.jpg", dimPoint, dimPoint);
                break;
            case 2:
                firstPoints =new CustomLabel("scoring_tokens/scoring_2.jpg", dimPoint, dimPoint);
                break;
            case 4:
                firstPoints =new CustomLabel("scoring_tokens/scoring_4.jpg", dimPoint, dimPoint);
                break;
            case 6:
                firstPoints =new CustomLabel("scoring_tokens/scoring_6.jpg", dimPoint, dimPoint);
                break;
            case 8:
                firstPoints =new CustomLabel("scoring_tokens/scoring_8.jpg", dimPoint, dimPoint);
                break;
        }



        int w=150;
        int h = 100;
        switch(firstcard){
            case 1:
                firstLabelCard =new CustomLabel("common_goal_cards/4.jpg",w,h);
                firstLabelCard.setLayout(new GridBagLayout());
                firstLabelCard.add(firstPoints, firstConstraints);
                background.add(firstLabelCard, constraints);
                break;
            case 2:
                firstLabelCard =new CustomLabel("common_goal_cards/3.jpg",w,h);
                firstLabelCard.setLayout(new GridBagLayout());
                firstLabelCard.add(firstPoints, firstConstraints);
                background.add(firstLabelCard, constraints);
                break;
            case 3:
                firstLabelCard =new CustomLabel("common_goal_cards/8.jpg",w,h);
                firstLabelCard.setLayout(new GridBagLayout());
                firstLabelCard.add(firstPoints, firstConstraints);
                background.add(firstLabelCard, constraints);
                break;
            case 4:
                firstLabelCard=new CustomLabel("common_goal_cards/1.jpg",w, h);
                firstLabelCard.setLayout(new GridBagLayout());
                firstLabelCard.add(firstPoints, firstConstraints);
                background.add(firstLabelCard, constraints);
                break;
            case 5:
                firstLabelCard=new CustomLabel("common_goal_cards/5.jpg", w, h);
                firstLabelCard.setLayout(new GridBagLayout());
                firstLabelCard.add(firstPoints, firstConstraints);
                background.add(firstLabelCard, constraints);
                break;
            case 6:
                firstLabelCard=new CustomLabel("common_goal_cards/9.jpg", w, h);
                firstLabelCard.setLayout(new GridBagLayout());
                firstLabelCard.add(firstPoints, firstConstraints);
                background.add(firstLabelCard, constraints);
                break;
            case 7:
                firstLabelCard=new CustomLabel("common_goal_cards/11.jpg", w, h);
                firstLabelCard.setLayout(new GridBagLayout());
                firstLabelCard.add(firstPoints, firstConstraints);
                background.add(firstLabelCard, constraints);
                break;
            case 8:
                firstLabelCard=new CustomLabel("common_goal_cards/7.jpg", w, h);
                firstLabelCard.setLayout(new GridBagLayout());
                firstLabelCard.add(firstPoints, firstConstraints);
                background.add(firstLabelCard, constraints);
                break;
            case 9:
                firstLabelCard=new CustomLabel("common_goal_cards/2.jpg", w, h);
                firstLabelCard.setLayout(new GridBagLayout());
                firstLabelCard.add(firstPoints, firstConstraints);
                background.add(firstLabelCard, constraints);
                break;
            case 10:
                firstLabelCard=new CustomLabel("common_goal_cards/6.jpg", w, h);
                firstLabelCard.setLayout(new GridBagLayout());
                firstLabelCard.add(firstPoints, firstConstraints);
                background.add(firstLabelCard, constraints);
                break;
            case 11:
                firstLabelCard=new CustomLabel("common_goal_cards/10.jpg", w, h);
                firstLabelCard.setLayout(new GridBagLayout());
                firstLabelCard.add(firstPoints, firstConstraints);
                background.add(firstLabelCard, constraints);
                break;
            case 12:
                firstLabelCard=new CustomLabel("common_goal_cards/12.jpg", w, h);
                firstLabelCard.setLayout(new GridBagLayout());
                firstLabelCard.add(firstPoints, firstConstraints);
                background.add(firstLabelCard, constraints);
                break;
        }


        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.gridheight = 1;

        switch(secondpoint){
            case 0:
                secondPoints =new CustomLabel("scoring_tokens/scoring.jpg", dimPoint, dimPoint);
                break;
            case 2:
                secondPoints =new CustomLabel("scoring_tokens/scoring_2.jpg", dimPoint, dimPoint);
                break;
            case 4:
                secondPoints =new CustomLabel("scoring_tokens/scoring_4.jpg", dimPoint, dimPoint);
                break;
            case 6:
                secondPoints =new CustomLabel("scoring_tokens/scoring_6.jpg", dimPoint, dimPoint);
                break;
            case 8:
                secondPoints =new CustomLabel("scoring_tokens/scoring_8.jpg", dimPoint, dimPoint);
                break;
        }

        switch(secondcard){
            case 1:
                secondLabelCard=new CustomLabel("common_goal_cards/4.jpg", w, h);
                secondLabelCard.setLayout(new GridBagLayout());
                secondLabelCard.add(secondPoints, firstConstraints);
                background.add(secondLabelCard, constraints);
                break;
            case 2:
                secondLabelCard=new CustomLabel("common_goal_cards/3.jpg", w, h);
                secondLabelCard.setLayout(new GridBagLayout());
                secondLabelCard.add(secondPoints, firstConstraints);
                background.add(secondLabelCard, constraints);
                break;
            case 3:
                secondLabelCard=new CustomLabel("common_goal_cards/8.jpg", w, h);
                secondLabelCard.setLayout(new GridBagLayout());
                secondLabelCard.add(secondPoints, firstConstraints);
                background.add(secondLabelCard, constraints);
                break;
            case 4:
                secondLabelCard=new CustomLabel("common_goal_cards/1.jpg", w, h);
                secondLabelCard.setLayout(new GridBagLayout());
                secondLabelCard.add(secondPoints, firstConstraints);
                background.add(secondLabelCard, constraints);
                break;
            case 5:
                secondLabelCard=new CustomLabel("common_goal_cards/5.jpg", w, h);
                secondLabelCard.setLayout(new GridBagLayout());
                secondLabelCard.add(secondPoints, firstConstraints);
                background.add(secondLabelCard, constraints);
                break;
            case 6:
                secondLabelCard=new CustomLabel("common_goal_cards/9.jpg", w, h);
                secondLabelCard.setLayout(new GridBagLayout());
                secondLabelCard.add(secondPoints, firstConstraints);
                background.add(secondLabelCard, constraints);
                break;
            case 7:
                secondLabelCard=new CustomLabel("common_goal_cards/11.jpg", w, h);
                secondLabelCard.setLayout(new GridBagLayout());
                secondLabelCard.add(secondPoints, firstConstraints);
                background.add(secondLabelCard, constraints);
                break;
            case 8:
                secondLabelCard=new CustomLabel("common_goal_cards/7.jpg", w, h);
                secondLabelCard.setLayout(new GridBagLayout());
                secondLabelCard.add(secondPoints, firstConstraints);
                background.add(secondLabelCard, constraints);
                break;
            case 9:
                secondLabelCard=new CustomLabel("common_goal_cards/2.jpg", w, h);
                secondLabelCard.setLayout(new GridBagLayout());
                secondLabelCard.add(secondPoints, firstConstraints);
                background.add(secondLabelCard, constraints);
                break;
            case 10:
                secondLabelCard=new CustomLabel("common_goal_cards/6.jpg", w, h);
                secondLabelCard.setLayout(new GridBagLayout());
                secondLabelCard.add(secondPoints, firstConstraints);
                background.add(secondLabelCard, constraints);
                break;
            case 11:
                secondLabelCard=new CustomLabel("common_goal_cards/10.jpg", w, h);
                secondLabelCard.setLayout(new GridBagLayout());
                secondLabelCard.add(secondPoints, firstConstraints);
                background.add(secondLabelCard, constraints);
                break;
            case 12:
                secondLabelCard=new CustomLabel("common_goal_cards/12.jpg", w, h);
                secondLabelCard.setLayout(new GridBagLayout());
                secondLabelCard.add(secondPoints, firstConstraints);
                background.add(secondLabelCard, constraints);
                break;
        }

        pack();
        setVisible(true);
    }

    /**
     * Method that shows the personal goal card for the current game
     * @param personalGoalCard the card
     * @param numberOfRowsBookshelf rows of the bookshelf
     * @param numberOfColumnsBookshelf columns of the bookshelf
     * @throws URISyntaxException related to an error while reading or writing on file
     * @throws IOException related to an error while reading or writing on file
     */
    @Override
    public void printPersonalGoalCard(String[][] personalGoalCard, int numberOfRowsBookshelf, int numberOfColumnsBookshelf) throws URISyntaxException, IOException {
        final List<ItemTile[][]> list = new ArrayList<>();
        ItemTile[][] personal1 = (new PersonalGoalCard(1)).getPersonalGoalCard();

        list.add(personal1);

        ItemTile[][] personal2 = (new PersonalGoalCard(2)).getPersonalGoalCard();
        list.add(personal2);
        ItemTile[][] personal3 = (new PersonalGoalCard(3)).getPersonalGoalCard();
        list.add(personal3);

        ItemTile[][] personal4 = (new PersonalGoalCard(4)).getPersonalGoalCard();
        list.add(personal4);

        ItemTile[][] personal5 = (new PersonalGoalCard(5)).getPersonalGoalCard();
        list.add(personal5);

        ItemTile[][] personal6 = (new PersonalGoalCard(6)).getPersonalGoalCard();
        list.add(personal6);

        ItemTile[][] personal7 = (new PersonalGoalCard(7)).getPersonalGoalCard();
        list.add(personal7);

        ItemTile[][] personal8 = (new PersonalGoalCard(8)).getPersonalGoalCard();
        list.add(personal8);


        ItemTile[][] personal9 = (new PersonalGoalCard(9)).getPersonalGoalCard();
        list.add(personal9);

        ItemTile[][] personal10 = (new PersonalGoalCard(10)).getPersonalGoalCard();
        list.add(personal10);

        ItemTile[][] personal11 = (new PersonalGoalCard(11)).getPersonalGoalCard();
        list.add(personal11);

        ItemTile[][] personal12 = (new PersonalGoalCard(12)).getPersonalGoalCard();
        list.add(personal12);

        boolean found1 = false;
        boolean isEq = true;
        int personalN = 0;
        for (int i = 0; i < 12; i++) {
            isEq = true;
            for (int x = 0; x < 6; x++) {
                for (int y = 0; y < 5; y++) {
                    if (!list.get(i)[x][y].getCharacter().equals(personalGoalCard[x][y])) isEq = false;

                }
            }
            if (isEq == true) {
                found1 = true;
                personalN = i + 1;
            }
            if (found1 == true) break;

        }


        CustomLabel labelCard;
        constraints.gridx = 8; // colonna 8
        constraints.gridy = 2; // riga 2
        constraints.gridwidth = 1; // occupa 1 cella in orizzontale
        constraints.gridheight = 2; // occupa 1 cella in verticale
        int w = 160;
        int h = 200;

        switch (personalN) {
            case 1:
                labelCard = new CustomLabel("personal_goal_cards/Personal_Goals.png", w, h);
                background.add(labelCard, constraints);
                break;
            case 2:
                labelCard = new CustomLabel("personal_goal_cards/Personal_Goals2.png", w, h);
                background.add(labelCard, constraints);
                break;
            case 3:
                labelCard = new CustomLabel("personal_goal_cards/Personal_Goals3.png", w, h);
                background.add(labelCard, constraints);
                break;
            case 4:
                labelCard = new CustomLabel("personal_goal_cards/Personal_Goals4.png", w, h);
                background.add(labelCard, constraints);
                break;
            case 5:
                labelCard = new CustomLabel("personal_goal_cards/Personal_Goals5.png", w, h);
                background.add(labelCard, constraints);
                break;
            case 6:
                labelCard = new CustomLabel("personal_goal_cards/Personal_Goals6.png", w, h);
                background.add(labelCard, constraints);
                break;
            case 7:
                labelCard = new CustomLabel("personal_goal_cards/Personal_Goals7.png", w, h);
                background.add(labelCard, constraints);
                break;
            case 8:
                labelCard = new CustomLabel("personal_goal_cards/Personal_Goals8.png", w, h);
                background.add(labelCard, constraints);
                break;
            case 9:
                labelCard = new CustomLabel("personal_goal_cards/Personal_Goals9.png", w, h);
                background.add(labelCard, constraints);
                break;
            case 10:
                labelCard = new CustomLabel("personal_goal_cards/Personal_Goals10.png", w, h);
                background.add(labelCard, constraints);
                break;
            case 11:
                labelCard = new CustomLabel("personal_goal_cards/Personal_Goals11.png", w, h);
                background.add(labelCard, constraints);
                break;
            case 12:
                labelCard = new CustomLabel("personal_goal_cards/Personal_Goals12.png", w, h);
                background.add(labelCard, constraints);
                break;

        }

        JLabel label1 = new JLabel();
        constraints.gridx = 8;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        background.add(label1, constraints);

        JLabel label2 = new JLabel();
        constraints.gridx = 8;
        constraints.gridy = 4;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        background.add(label2, constraints);


        pack();
        setVisible(true);
    }

    /**
     * Method that shows how turns are managed for the current game
     * @param turnsNickname list of the nicknames in order of turns
     */
    @Override
    public void printTurns(List<String> turnsNickname) {
        mainPanel.removeAll();

        labelCenter= new CustomLabel("misc/sfondo_parquet.jpg", width, 700);
        labelNorth = new CustomLabel("misc/base_pagina2.jpg", width, 80);
        if(turnsNickname.size()==2){
            labelSouth = new CustomLabel("publisher_material/banner_1386x400px.png", width, height/3);
            mainPanel.add(labelSouth, BorderLayout.SOUTH);
        }else if(turnsNickname.size()==3){
            labelSouth = new CustomLabel("publisher_material/banner_1386x400px.png", width, height/4);
            mainPanel.add(labelSouth, BorderLayout.SOUTH);
        }
        labelCenter.setLayout(new BoxLayout(labelCenter, BoxLayout.Y_AXIS));
        labelNorth.setLayout(new FlowLayout());
        text = new JLabel("This is how turns are managed for this game:");
        text.setForeground(Color.WHITE);
        Font fontTitle = new Font("Arial", Font.BOLD, 25);
        Font font = new Font("Arial", Font.BOLD, 40);
        text.setFont(fontTitle);
        text.setHorizontalAlignment(JLabel.CENTER);
        labelNorth.add(text);
        EmptyBorder emptyBorderinsert = new EmptyBorder(20, 0, 5, 0);
        text.setBorder(emptyBorderinsert);

        labelCenter.add(Box.createRigidArea(new Dimension(0, 30)));

        int size = turnsNickname.size();
        JLabel[] nicknames = new JLabel[size];
        for (int i = 0; i < nicknames.length; i++) {
            nicknames[i] = new JLabel();
        }

        JLabel[] arrows = new JLabel[size-1];
        for (int i = 0; i < arrows.length; i++) {
            arrows[i] = new JLabel();
        }

        JLabel imageLabel = new JLabel();
        imageLabel = new CustomLabel("misc/firstplayertoken.png", 70, 70);
        imageLabel.setVerticalAlignment(JLabel.TOP);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        labelCenter.add(imageLabel);


        for(int j = 0; j<size; j++){
            nicknames[j].setText(turnsNickname.get(j));
            if(turnsNickname.get(j).equals(this.nickname)){
                nicknames[j].setForeground(Color.MAGENTA);
            }else nicknames[j].setForeground(Color.WHITE);
            nicknames[j].setFont(font);
            nicknames[j].setHorizontalAlignment(JLabel.CENTER);
            nicknames[j].setVerticalAlignment(JLabel.TOP);
            nicknames[j].setAlignmentX(Component.CENTER_ALIGNMENT);
            labelCenter.add(nicknames[j]);


            if(j != (size-1)){
                arrows[j].setText("↓");
                arrows[j].setForeground(Color.WHITE);
                arrows[j].setFont(font);
                arrows[j].setHorizontalAlignment(JLabel.CENTER);
                arrows[j].setAlignmentX(Component.CENTER_ALIGNMENT);
                labelCenter.add(arrows[j]);
            }
        }

        mainPanel.add(labelCenter,BorderLayout.CENTER);
        mainPanel.add(labelNorth,BorderLayout.NORTH);
        setVisible(true);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            System.err.println("Interrupted Exception: " + e.getMessage());
        }

        //Adding some components while the game is loading
        setMinimumSize(new Dimension(1050, 700));
        mainPanel.removeAll();
        labelNorth = new CustomLabel("misc/base_pagina2.jpg", 800, 40);
        labelNorth.setLayout(new FlowLayout(FlowLayout.CENTER));
        text.setText("");
        text.setBorder(null);
        labelNorth.add(text);
        mainPanel.add(labelNorth, BorderLayout.NORTH);

        labelSouth = new CustomLabel("misc/base_pagina2.jpg", 800, 40);
        mainPanel.add(labelSouth, BorderLayout.SOUTH);

        background = new CustomLabel("misc/sfondo_parquet.jpg", 700, 450);
        background.setLayout(new GridBagLayout());

        GridBagConstraints waitingConstraints = new GridBagConstraints();
        waitingConstraints.fill = GridBagConstraints.BOTH;
        waitingConstraints.weightx = 1.0;
        waitingConstraints.weighty = 1.0;
        waitingConstraints.insets = new Insets(100, 50, 100, 50);

        CustomLabel openBag = new CustomLabel("misc/sacchetto_aperto.png", 400, 400);
        waitingConstraints.gridx = 0;
        waitingConstraints.gridy = 0;
        waitingConstraints.gridwidth = 4;
        waitingConstraints.gridheight = 4;
        background.add(openBag, waitingConstraints);

        JLabel waitingText = new JLabel("The game will start soon!");
        Font waitingFont = new Font("Arial", Font.BOLD, 30);
        waitingText.setForeground(Color.WHITE);
        waitingText.setFont(waitingFont);
        waitingConstraints.gridx = 4;
        waitingConstraints.gridy = 1;
        waitingConstraints.gridwidth = 3;
        waitingConstraints.gridheight = 2;
        background.add(waitingText, waitingConstraints);

        mainPanel.add(background, BorderLayout.CENTER);

        setVisible(true);
    }

    /**
     * Method that is shown while the player is waiting for his turn
     */
    @Override
    public void printWaitingForYourTurn() {
        text.setText("Waiting your turn...");
        setVisible(true);
    }

    /**
     * Method that inform the player of the beginning of his turn
     */
    @Override
    public void isPlayerTurn() {
        text.setText("It's your turn!");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.err.println("Interrupted Exception: " + e.getMessage());
        }
        setVisible(true);
    }

    /**
     * Method that asks the player to pick the tiles from the board. He can only select 1/2/3 tiles, otherwise the
     * button becomes deactivated.
     * @return number of tiles selected
     */
    @Override
    public int askNumberOfTilesWanted() {
        //Setting the text "choose your tiles"
        text.setText("Choose your tiles by clicking on it and then press this button ");

        //Adding a button that the user has to click when he finishes to take the tiles
        okButton = new JButton("OK");
        Font font = new Font("Arial", Font.PLAIN, 15);
        okButton.setFont(font);
        okButton.setBackground(Color.WHITE);
        okButton.setForeground(Color.BLACK);
        okButton.setEnabled(false);
        labelNorth.add(okButton);

        //Iterating the grid to add a MouseListener on each tile and saving the coordinates
        Component[] tiles = boardGrid.getComponents();
        numberOfTilesWanted = 0;
        for(Component tile : tiles){
            MouseListener[] mouseListeners = tile.getMouseListeners();
            if(mouseListeners.length == 0){
                MouseListener mouseListener = new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        //Obtaining the tile clicked by the user
                        CustomLabel label = (CustomLabel) e.getSource();
                        if(!label.isEmpty()) {
                            LineBorder border = (LineBorder) label.getBorder();
                            //If the tile was already selected, decrement the counter and change the border color to white
                            if (border.getLineColor().equals(Color.RED)) {
                                label.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
                                numberOfTilesWanted--;
                            }
                            //Otherwise, increment the counter and change the border color to red
                            else {
                                label.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
                                numberOfTilesWanted++;
                            }

                            if (numberOfTilesWanted > 0 && numberOfTilesWanted <= 3) {
                                okButton.setEnabled(true);
                            } else {
                                okButton.setEnabled(false);
                            }
                        }
                    }
                };
                tile.addMouseListener(mouseListener);
            }else{
                break;
            }
        }

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                labelNorth.remove(okButton);
            }
        });
        pack();
        setVisible(true);

        while (okButton.isDisplayable()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {
            }
        }

        return numberOfTilesWanted;
    }

    /**
     * Method that finds the coordinates of the tiles selected by the user
     * @param numberOfTilesWanted number of tiles selected by the user
     * @param numberOfRowsBoard rows of the board
     * @param numberOfColumnsBoard columns of the board
     * @return list of coordinates
     */
    @Override
    public List<Integer> askCoordinates(int numberOfTilesWanted, int numberOfRowsBoard, int numberOfColumnsBoard) {
        Set<Point> coordinatesTileWithBorder = new HashSet<>(); //This set prevents the presence of duplicates
        Component[] tiles = boardGrid.getComponents();
        coordinates.clear();
        //Iterating the grid to find those tile with the red border
        for(int i = 0; i < tiles.length; i++){
            JLabel label = (JLabel) tiles[i];
            LineBorder border = (LineBorder) label.getBorder();
            if (border != null && border.getLineColor().equals(Color.RED)) {
                int row = i / numberOfColumnsBoard; //Calculate the row number
                int column = i % numberOfColumnsBoard; //Calculate the column number
                coordinatesTileWithBorder.add(new Point(row, column));
                label.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
            }
        }
        for(Point p : coordinatesTileWithBorder){
            coordinates.add(p.y);
            coordinates.add(p.x);
        }
        while(coordinates.size() != numberOfTilesWanted * 2){
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {
            }
        }
        coordinatesTileWithBorder.clear();
        return coordinates;
    }

    /**
     * Method that shows the tiles just taken from the player
     * @param takenTiles list of the tiles taken from the player
     */
    @Override
    public void printTakenTiles(List<ItemTile> takenTiles) {
        text.setText("");
        background.remove(boardImage);
        boardImage = new CustomLabel("misc/base_pagina2.jpg", 325, 325);
        boardImage.setLayout(new BorderLayout());
        JLabel yourTiles = new JLabel("YOUR TILES");
        Font font = new Font("Arial", Font.BOLD, 40);
        yourTiles.setFont(font);
        yourTiles.setForeground(Color.WHITE);
        yourTiles.setHorizontalAlignment(JLabel.CENTER);
        EmptyBorder emptyBorder = new EmptyBorder(15, 0, 0, 0);
        yourTiles.setBorder(emptyBorder);
        boardImage.add(yourTiles, BorderLayout.NORTH);

        JLabel flowLabel = new JLabel();
        flowLabel.setLayout(new FlowLayout(FlowLayout.CENTER,15,15));
        for(ItemTile tile : takenTiles){
            JLabel tileLabel = createItemTileLabel(tile, 120);
            tileLabel.setBorder(new LineBorder(Color.WHITE, 5));
            flowLabel.add(tileLabel);
            tilesTaken.add(tileLabel);
        }
        boardImage.add(flowLabel, BorderLayout.CENTER);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 4;
        constraints.gridheight = 4;
        background.add(boardImage, constraints);
        pack();
        setVisible(true);
    }


    /**
     * Method that asks the user to select the tiles in order of insertion
     * @param numberOfTilesWanted number of the tiles selected by the user
     * @return list of the order of insertion of each tile
     */
    @Override
    public List<Integer> askOrderTile(int numberOfTilesWanted) {
        text.setText("Select your tiles in the order of inserting and then press this button:");

        //Adding a button that the user has to click when he finishes to order the tiles
        okButton = new JButton("OK");
        Font font = new Font("Arial", Font.PLAIN, 15);
        okButton.setFont(font);
        okButton.setBackground(Color.WHITE);
        okButton.setForeground(Color.BLACK);
        okButton.setEnabled(false);
        labelNorth.add(okButton);

        order.clear();

        for(JLabel tile : tilesTaken){
            MouseListener[] mouseListeners = tile.getMouseListeners();
            if(mouseListeners.length == 0) {
                MouseListener mouseListener = new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        //Obtaining the tile clicked by the user
                        JLabel label = (JLabel) e.getSource();
                        LineBorder border = (LineBorder) label.getBorder();
                        //If the tile was already selected, change the border to white and remove from the list of the
                        //orders the index of the tile selected
                        if (border.getLineColor().equals(Color.RED)) {
                            label.setBorder(BorderFactory.createLineBorder(Color.WHITE, 5));
                            order.remove(Integer.valueOf(tilesTaken.indexOf(label)));
                        }
                        //Otherwise, change the border color to red and add to the list the index of the tile selected
                        else {
                            label.setBorder(BorderFactory.createLineBorder(Color.RED, 5));
                            order.add(tilesTaken.indexOf(label));
                        }

                        //The button is enabled when the user selects all the tiles
                        if(order.size() == numberOfTilesWanted){
                            okButton.setEnabled(true);
                        }else{
                            okButton.setEnabled(false);
                        }
                    }
                };
                tile.addMouseListener(mouseListener);
            }else{
                break;
            }
        }

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                labelNorth.remove(okButton);
            }
        };
        okButton.addActionListener(actionListener);

        setVisible(true);

        while (okButton.isDisplayable()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {
            }
        }

        return order;
    }

    /**
     * Method that asks the user to select the column where the tiles will be placed
     * @param numberOfColumns columns of the bookshelf
     * @return index of the column
     */
    @Override
    public int askColumnPlaceTile(int numberOfColumns) {
        tilesTaken.clear();
        text.setText("Click a cell in the column where you want to insert your tiles");
        column = -1;
        //Iterating the grid to add a MouseListener on each tile and saving the coordinates
        Component[] cells = bookshelfGrid.getComponents();
        for(Component cell : cells){
            MouseListener[] mouseListeners = cell.getMouseListeners();
            if(mouseListeners.length == 0){
                MouseListener mouseListener = new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        //Obtaining the cell clicked by the user
                        JLabel label = (JLabel) e.getSource();
                        for(int i = 0; i < bookshelfCells.length; i++){
                            for(int j = 0; j < numberOfColumns; j++){
                                 if(label.equals(bookshelfCells[i][j])){
                                     column = j;
                                 }
                            }
                        }
                        label.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
                    }
                };
                cell.addMouseListener(mouseListener);
            }else{
                break;
            }
        }

        while (column == -1) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {
            }
        }

        setVisible(true);
        return column;
    }


    /**
     * Method that informs that a common goal is completed
     * @param number of the common goal completed
     */
    @Override
    public void completedCommonGoalCard(int number) {
        JDialog dialog = new JDialog(this, "Common goal"+Integer.toString(number+1)+" completed!", true);
        dialog.setLayout(new BorderLayout());

        //Adding the error message
        JLabel error = new JLabel(" Congratulation! You completed the common goal n° "+ Integer.toString(number));
        EmptyBorder border = new EmptyBorder(0, 15, 0, 15);
        error.setBorder(border);
        error.setHorizontalAlignment(JLabel.CENTER);
        dialog.add(error, BorderLayout.CENTER);

        //Setting the dimension and the position of the JDialog
        dialog.setSize(600, 200);
        dialog.setLocationRelativeTo(this);

        //Adding a button
        JButton errorButton = new JButton("OK");
        dialog.add(errorButton, BorderLayout.SOUTH);

        //Adding an icon and its space from the left border
        JLabel icon = new CustomLabel("publisher_material/Box_280x280px.png", 100, 100);
        dialog.add(icon, BorderLayout.WEST);
        icon.setBorder(border);

        //When the user presses the button, the error window closes itself
        errorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.pack();
        dialog.setVisible(true);

    }

    /**
     * Method that informs the player that he has completed the bookshelf
     */
    @Override
    public void playerHasFullBookshelf() {
        JDialog dialog = new JDialog(this, "FullBookshelf", true);
        dialog.setLayout(new BorderLayout());

        //Adding the error message
        JLabel error = new JLabel("Congratulation! Your bookshelf is full. Game will end soon...");
        EmptyBorder border = new EmptyBorder(0, 15, 0, 15);
        error.setBorder(border);
        error.setHorizontalAlignment(JLabel.CENTER);
        dialog.add(error, BorderLayout.CENTER);

        //Setting the dimension and the position of the JDialog
        dialog.setSize(600, 200);
        dialog.setLocationRelativeTo(this);

        //Adding a button
        JButton errorButton = new JButton("OK");
        dialog.add(errorButton, BorderLayout.SOUTH);

        //Adding an icon and its space from the left border
        JLabel icon = new CustomLabel("publisher_material/Box_280x280px.png", 100, 100);
        dialog.add(icon, BorderLayout.WEST);
        icon.setBorder(border);

        //When the user presses the button, the error window closes itself
        errorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.pack();
        dialog.setVisible(true);


    }

    /**
     * Useless method for the graphic interface.
     */
    @Override
    public void printThisIsYourBookShelf() {
    }

    /**
     * Useless method for the graphic interface. In its place the method "printCurrentPlayerBookshelf" is used.
     */
    @Override
    public void printPersonalBookshelf(String[][] personalBookshelf, int numberOfRowsBookshelf, int numberOfColumnsBookshelf) {
    }

    /**
     * Method that shows the board of the game
     * @param board game board
     * @param numberOfRowsBoard rows of the board
     * @param numberOfColumnsBoard columns of the board
     */
    public void printBoard(ItemTile[][] board, int numberOfRowsBoard, int numberOfColumnsBoard) {
        //Creating the board
        boardImage = new CustomLabel("boards/livingroom.png", 325, 325);
        boardImage.setLayout(new BorderLayout());

        boardGrid = new CustomLabel("empty", 325, 325);
        EmptyBorder gridBorder = new EmptyBorder(19, 19, 19, 24);
        boardGrid.setBorder(gridBorder);
        boardImage.add(boardGrid, BorderLayout.CENTER);

        //Placing the item tiles on the board
        boardGrid.setLayout(new GridBagLayout());
        GridBagConstraints boardConstraints = new GridBagConstraints();
        boardConstraints.fill = GridBagConstraints.BOTH;
        boardConstraints.weightx = 1.0;
        boardConstraints.weighty = 1.0;
        numberOfTilesWanted = 0;
        for(int i = 0; i < numberOfRowsBoard; i++){
            for(int j = 0; j < numberOfColumnsBoard; j++){
                CustomLabel item = createItemTileLabel(board[i][j], 51);
                if(!item.isEmpty()){
                    item.setBorder(new LineBorder(Color.WHITE, 1));
                }
                boardConstraints.gridx = j;
                boardConstraints.gridy = i;
                boardConstraints.gridwidth = 1;
                boardConstraints.gridheight = 1;
                boardGrid.add(item,boardConstraints);
            }
        }

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 4;
        constraints.gridheight = 4;
        background.add(boardImage, constraints);
        pack();
        setVisible(true);
    }

    /**
     * Method that prints the scores of the game
     * @param scores scores of each player
     */
    @Override
    public void printScores(HashMap<String, Integer> scores) {
        background.removeAll();

        background.setLayout(new FlowLayout());

        int lenght=scores.size();
        JPanel helper1=new JPanel();
        JPanel helper2=new JPanel();
        helper1.setLayout (new BoxLayout (helper1, BoxLayout.Y_AXIS));
        helper2.setLayout (new BoxLayout (helper2, BoxLayout.Y_AXIS));


        for(int i=0;i<lenght;i++){
            String nick=(String) scores.keySet().toArray()[i];
            helper1.add(Box.createRigidArea(new Dimension(50, 0)));
            helper2.add(Box.createRigidArea(new Dimension(50, 0)));
            JLabel text1=new JLabel((String) scores.keySet().toArray()[i]);
            Font font = new Font("Arial", Font.BOLD, 25);
            text1.setFont(font);
            helper1.add(text1);
            JLabel text2=new JLabel(Integer.toString(scores.get(nick)));
            text2.setFont(font);
            helper2.add(text2);

        }
        background.add(helper1);
        background.add(helper2);
        background.setAlignmentX(Component.CENTER_ALIGNMENT);

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            System.err.println("Interrupted Exception: " + e.getMessage());
        }
        pack();
        setVisible(true);
    }

    /**
     * Method that prints the name of the winner
     * @param winner the name of the winner
     */
    @Override
    public void printWinner(String winner) {
        text.setText(winner +" wins this game!");
    }

    /**
     * Method that prints the final screen that informs that the game is finished
     */
    @Override
    public void printTheGameHasEnded() {
        setMinimumSize(new Dimension(width, height));
        setPreferredSize(new Dimension(width, height));
        background.removeAll();
        background.setLayout(new BorderLayout());

        text.setText("");

        JLabel end = new JLabel("THE GAME HAS ENDED");
        end.setForeground(Color.WHITE);
        Font font = new Font("Arial", Font.BOLD, 25);
        end.setFont(font);
        end.setHorizontalAlignment(JLabel.CENTER);

        background.add(end, BorderLayout.CENTER);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
        }

        pack();
        setVisible(true);

    }

    /**
     * Method that prints a page containing all the information about the current game (the board,the player bookshelf,
     * other players' bookshelf,the personal goal and the commons goals) and where the player can perform his actions
     * @param CurrentP nickname of the current player
     * @param board the board
     * @param numberOfRowsBoard number of rows of the board
     * @param numberOfColumnsBoard number of columns of the board
     * @param numberOfRowsBookshelf number of rows of the bookshelf
     * @param numberOfColumnsBookshelf number of columns of the bookshelf
     * @param AllPlayersBookshelf bookshelf of all players
     * @param commonGoalDescription description of each common goal of the game
     * @param commonGoalPoints point for each common goal
     * @param nicknames list of all nicknames
     * @param personalGoalCard personal goal card of the player
     * @param currentPlayerBookshelf bookshelf of the player
     * @param otherPlayersBookshelves bookshelves of all players, excluded the one of the user
     * @throws URISyntaxException related to an error while reading or writing on file
     * @throws IOException related to an error while reading or writing on file
     */
    @Override
    public void printAllInformation(String CurrentP, ItemTile[][] board, int numberOfRowsBoard, int numberOfColumnsBoard, int numberOfRowsBookshelf, int numberOfColumnsBookshelf, List<ItemTile[][]> AllPlayersBookshelf, List<String> commonGoalDescription, List<Integer> commonGoalPoints, List<String> nicknames, String[][] personalGoalCard, ItemTile[][] currentPlayerBookshelf, List<ItemTile[][]> otherPlayersBookshelves) throws URISyntaxException, IOException {

        setMinimumSize(new Dimension(1050, 700));
        mainPanel.removeAll();
        labelNorth = new CustomLabel("misc/base_pagina2.jpg", 800, 40);
        labelNorth.setLayout(new FlowLayout(FlowLayout.CENTER));
        labelNorth.add(text);
        mainPanel.add(labelNorth, BorderLayout.NORTH);

        labelSouth = new CustomLabel("misc/base_pagina2.jpg", 800, 40);
        mainPanel.add(labelSouth, BorderLayout.SOUTH);

        background = new CustomLabel("misc/sfondo_parquet.jpg", 700, 450);
        background.setLayout(new GridBagLayout());

        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.insets = new Insets(20, 20, 20, 20);

        // Common goals
        printCommonGoalCards(commonGoalDescription, commonGoalPoints);

        // Board
        printBoard(board, numberOfRowsBoard, numberOfColumnsBoard);

        // Bookshelf
        printCurrentPlayerBookshelf(currentPlayerBookshelf, numberOfRowsBookshelf, numberOfColumnsBookshelf);

        // Personal Goal
        printPersonalGoalCard(personalGoalCard, numberOfRowsBookshelf, numberOfColumnsBookshelf);

        //All bookshelves
        printAllBookshelves(otherPlayersBookshelves);

        mainPanel.add(background, BorderLayout.CENTER);

        pack();
        setVisible(true);
    }

    /**
     * Method that prints the bookshelf of the user
     * @param currentPlayerBookshelf bookshelf of the player
     * @param numberOfRowsBookshelf number of rows of the bookshelf
     * @param numberOfColumnsBookshelf number of columns of the bookshelf
     */
    private void printCurrentPlayerBookshelf(ItemTile[][] currentPlayerBookshelf, int numberOfRowsBookshelf, int numberOfColumnsBookshelf) {
        CustomLabel bookshelf = new CustomLabel("boards/bookshelf.png", 332, 336);
        bookshelf.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.1;
        gbc.weighty = 0.1;

        //Creating some "borders" that adapt to resizing
        JLabel northLabel = new CustomLabel("empty", 332, 21);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 12;
        gbc.gridheight = 1;
        bookshelf.add(northLabel, gbc);

        JLabel westLabel = new CustomLabel("empty", 44, 276);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 16;
        bookshelf.add(westLabel, gbc);

        JLabel southLabel = new CustomLabel("empty", 332, 50);
        gbc.gridx = 0;
        gbc.gridy = 17;
        gbc.gridwidth = 12;
        gbc.gridheight = 3;
        bookshelf.add(southLabel, gbc);

        JLabel eastLabel = new CustomLabel("empty", 44, 276);
        gbc.gridx = 11;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 16;
        bookshelf.add(eastLabel, gbc);

        //The bookshelf grid on which the tiles are placed
        bookshelfGrid = new CustomLabel("empty", 300, 300);
        gbc.weightx = 0.6;
        gbc.weighty = 0.6;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 10;
        gbc.gridheight = 16;
        bookshelf.add(bookshelfGrid, gbc);

        bookshelfGrid.setLayout(new GridBagLayout());
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(3, 5, 3, 5);
        bookshelfCells = new JLabel[numberOfRowsBookshelf][numberOfColumnsBookshelf];
        for(int i = 0; i < numberOfRowsBookshelf; i++){
            for(int j = 0; j < numberOfColumnsBookshelf; j++){
                JLabel item = createItemTileLabel(currentPlayerBookshelf[i][j], 100);
                gbc.gridx = j + 1;
                gbc.gridy = i + 1;
                gbc.gridwidth = 1;
                gbc.gridheight = 1;
                bookshelfGrid.add(item,gbc);
                bookshelfCells[i][j] = item;
            }
        }
        constraints.gridx = 4;
        constraints.gridy = 1;
        constraints.gridwidth = 4;
        constraints.gridheight = 4;
        background.add(bookshelf, constraints);
        pack();
        setVisible(true);
    }

    /**
     * Method that prints all the bookshelves of the other players
     * @param otherPlayersBookshelves bookshelves of all players, excluded the one of the user
     */
    private void printAllBookshelves(List<ItemTile[][]> otherPlayersBookshelves) {
        int numberOfRowsBookshelf = 6;
        int numberOfColumnsBookshelf = 5;

        //A Label with the bookshelves inside of it
        JLabel bookshelvesLabel = new CustomLabel("empty", 500, 100);
        bookshelvesLabel.setLayout(new BoxLayout(bookshelvesLabel, BoxLayout.X_AXIS));
        bookshelvesLabel.add(Box.createHorizontalGlue());

        for(ItemTile[][] bookshelf : otherPlayersBookshelves){
            JLabel currentBookshelf = new CustomLabel("boards/bookshelf_orth.png", 150, 336);
            currentBookshelf.setLayout(new GridBagLayout());
            GridBagConstraints gbc1 = new GridBagConstraints();
            gbc1.fill = GridBagConstraints.BOTH;
            gbc1.weightx = 0.1;
            gbc1.weighty = 0.1;

            JLabel northLabel = new CustomLabel("empty", 80, 8);
            gbc1.gridx = 0;
            gbc1.gridy = 0;
            gbc1.gridwidth = 12;
            gbc1.gridheight = 1;
            currentBookshelf.add(northLabel, gbc1);

            JLabel westLabel = new CustomLabel("empty", 13, 90);
            gbc1.gridx = 0;
            gbc1.gridy = 1;
            gbc1.gridwidth = 1;
            gbc1.gridheight = 64;
            currentBookshelf.add(westLabel, gbc1);

            JLabel southLabel = new CustomLabel("empty", 80, 16);
            gbc1.gridx = 0;
            gbc1.gridy = 68;
            gbc1.gridwidth = 12;
            gbc1.gridheight = 1;
            currentBookshelf.add(southLabel, gbc1);

            JLabel eastLabel = new CustomLabel("empty", 13, 90);
            gbc1.gridx = 11;
            gbc1.gridy = 1;
            gbc1.gridwidth = 1;
            gbc1.gridheight = 64;
            currentBookshelf.add(eastLabel, gbc1);

            JLabel bookshelfGrid1 = new CustomLabel("empty", 100, 100);
            gbc1.weightx = 0.6;
            gbc1.weighty = 0.6;
            gbc1.gridx = 1;
            gbc1.gridy = 2;
            gbc1.gridwidth = 5;
            gbc1.gridheight = 10;
            currentBookshelf.add(bookshelfGrid1, gbc1);

            bookshelfGrid1.setLayout(new GridBagLayout());
            gbc1.weightx = 1;
            gbc1.weighty = 1;
            gbc1.insets = new Insets(1, 2, 1, 2);

            for(int n = 0; n < numberOfRowsBookshelf; n++){
                for(int j = 0; j < numberOfColumnsBookshelf; j++){
                    JLabel item = createItemTileLabel(bookshelf[n][j], 100);
                    gbc1.gridx = j + 1;
                    gbc1.gridy = n + 1;
                    gbc1.gridwidth = 1;
                    gbc1.gridheight = 1;
                    bookshelfGrid1.add(item,gbc1);
                }
            }

            bookshelvesLabel.add(currentBookshelf);
            bookshelvesLabel.add(Box.createHorizontalGlue());
        }

        constraints.gridx = 4;
        constraints.gridy = 0;
        constraints.gridwidth = 6;
        constraints.gridheight = 1;
        background.add(bookshelvesLabel, constraints);
        pack();
        setVisible(true);
    }
    @Override
    public void printDisconnection(int server){
        JDialog dialog = new JDialog(this, "Disconnection Message", true);
        dialog.setLayout(new BorderLayout());
        JLabel disconnection;

        //Adding the disconnection message
        if(server == 0){
            disconnection = new JLabel("The server has disconnected, ending the game...");
        }else if(server == 1){
            disconnection = new JLabel("A player has disconnected, ending the game...");
        }else
        {
            disconnection = new JLabel("A player or the server has disconnected, ending the game...");
        }

        EmptyBorder border = new EmptyBorder(0, 15, 0, 15);
        disconnection.setBorder(border);
        disconnection.setHorizontalAlignment(JLabel.CENTER);
        dialog.add(disconnection, BorderLayout.CENTER);

        //Setting the dimension and the position of the JDialog
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        //Adding a button
        JButton errorButton = new JButton("EXIT");
        dialog.add(errorButton, BorderLayout.SOUTH);

        //Adding an icon and its space from the left border
        JLabel icon = new CustomLabel("publisher_material/Box_280x280px.png", 100, 100);
        dialog.add(icon, BorderLayout.WEST);
        icon.setBorder(border);

        //When the user presses the button, the client disconnects
        errorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.pack();
        dialog.setVisible(true);
    }
}
