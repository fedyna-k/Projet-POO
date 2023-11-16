/**
 * @brief This file contains the public class KeyStack
 * 
 * @file KeyStack.java
 * @author Kevin Fedyna
 * @date 16/11/2023
 * 
 * Part of the `graphics` package. It allows to get all pressed keys and multi-keypress.
 */

package graphics;


import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 * @class KeyStack
 * @author Kevin Fedyna
 * @date 16/11/2023
 * 
 * @brief This class allows to get multi-keypress.
 * 
 * It should only be instancied once per Canvas.
 * 
 * @pre A Canvas object has to be created in order to bind it to the KeyStack.
 * @see graphics.Canvas
 */
public class KeyStack {
    /** @brief Storage for the key pressed */
    private Set<String> allPressedKeys;
    /**
     * @brief The InputMap of the Canvas object.
     * @see javax.swing.InputMap
     */
    private InputMap inputs;
    /**
     * @brief The ActionMap of the Canvas object.
     * @see javax.swing.ActionMap
     */
    private ActionMap actions;

    /**
     * @brief Create new KeyStack object which is bound to Canvas.
     * 
     * Allow to have multiple key pressed at the same time for movement purpose.
     * 
     * @param binding The Canvas that listen to key inputs
     */
    public KeyStack(Canvas binding) {
        this.allPressedKeys = new HashSet<String>();
        this.inputs = binding.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        this.actions = binding.getActionMap();
    }

    /**
     * @brief Add key to the KeyStack inputs.
     * 
     * The key is in the format of what KeyStroke can read.
     * 
     * Whenever the given key is pressed, the KeyStack will raise a `AddKeyAction` event.
     * Whenever the given key is released, the KeyStack will raise a `RemoveKeyAction` event.
     * 
     * @param key The string representation of the key.
     * @see javax.swing.KeyStroke
     */
    public void listenTo(String key) {
        inputs.put(KeyStroke.getKeyStroke("pressed " + key), "pressed" + key);
        inputs.put(KeyStroke.getKeyStroke("released " + key), "released" + key);
        actions.put("pressed" + key, new AddKeyAction(key));
        actions.put("released" + key, new RemoveKeyAction(key));
    }

    /**
     * @brief Check if a key is pressed.
     * @note Returns false if key is not listenable.
     * @param key The string representation of the key
     * @return A boolean equals to true if the key is pressed
     */
    public boolean isPressed(String key) {
        return allPressedKeys.contains(key);
    }

    /**
     * @class AddKeyAction
     * @author Kevin Fedyna
     * @date 16/11/2023
     * 
     * @brief Adds a key to the KeyStack on press.
     * 
     * This class extends AbstractAction with a constructor that allows to
     * add a specific given key on press to the KeyStack.
     * 
     * @see javax.swing.AbstractAction
     */
    private class AddKeyAction extends AbstractAction {
        /** @brief The key to listen to. */
        private String key;

        /**
         * @brief Constructor for AddKeyAction.
         * @param key The key to listen to.
         */
        public AddKeyAction(String key) {
            this.key = key;
        }

        /**
         * @brief Adds the key to KeyStack.
         * 
         * @see javax.swing.AbstractAction
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            allPressedKeys.add(this.key);
        }
    }

    /**
     * @class RemoveKeyAction
     * @author Kevin Fedyna
     * @date 16/11/2023
     * 
     * @brief Removes a key to the KeyStack on press.
     * 
     * This class extends AbstractAction with a constructor that allows to
     * remove a specific given key on release from the KeyStack.
     * 
     * @see javax.swing.AbstractAction
     */
    private class RemoveKeyAction extends AbstractAction {
        /** @brief The key to listen to. */
        private String key;

        /**
         * @brief Constructor for RemoveKeyAction.
         * @param key The key to listen to.
         */
        public RemoveKeyAction(String key) {
            this.key = key;
        }

        /**
         * @brief Removes the key from KeyStack.
         * 
         * @see javax.swing.AbstractAction
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            allPressedKeys.remove(this.key);
        }
    }
}
