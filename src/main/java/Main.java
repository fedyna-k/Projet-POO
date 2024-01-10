/**
 * @mainpage Documentation of "Les chevaliers d'Ether".
 * 
 * This documentation is usefull for adding things into the game.
 * 
 * You can use the search bar tool (which is quite usefull when you already know the name of the thing you want more info of)
 * or the left research panel (which is quite usefull when you don't know the name of the thing you want more info of).
 * 
 * All informations on the project are on the [github](https://github.com/fedyna-k/Projet-POO/).
 * 
 */

/**
 * @brief All Java classes related to entities (enemies or player).
 * 
 * @package character
 * @author Kevin Fedyna
 * @author Imene Bousmaha
 * @date 16/11/2023
 */

/**
 * @brief All Java classes related to geometry.
 * 
 * @package geometry
 * @author Kevin Fedyna
 * @date 16/11/2023
 */

/**
 * @brief All Java classes related to graphics.
 * 
 * @package graphics
 * @author Kevin Fedyna
 * @date 16/11/2023
 */

/**
 * @brief All Java classes related to the map.
 * 
 * @package map
 * @author Kevin Fedyna
 * @date 16/11/2023
 */

/**
 * @brief This file contains the public class Main.
 * 
 * @file Main.java
 * @author Kevin Fedyna
 * @date 16/11/2023
 * 
 * This is the file that should be launched.
 */

import graphics.Window;

/**
 * @class Main
 * @author Kevin Fedyna
 * @date 16/11/2023
 * 
 * @brief The main class, only contains the main function.
 */
public class Main {
    /**
     * @brief The main function, calls a new Window only.
     * @param args The arguments given in the console.
     */
    public static void main(String[] args) {
        new Window(true);
    }
}
