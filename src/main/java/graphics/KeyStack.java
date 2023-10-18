package graphics;


import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;


public class KeyStack {
    private Set<String> allPressedKeys;
    private InputMap inputs;
    private ActionMap actions;

    /**
     * Create new KeyStack object which is bound to Canvas
     * Allow to have multiple key pressed at the same time for movement purpose
     * @param binding The Canvas that listen to key inputs
     */
    public KeyStack(Canvas binding) {
        this.allPressedKeys = new HashSet<String>();
        this.inputs = binding.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        this.actions = binding.getActionMap();
    }

    /**
     * Add key to the KeyStack inputs
     * @param key The string representation of the key
     */
    public void listenTo(String key) {
        inputs.put(KeyStroke.getKeyStroke("pressed " + key), "pressed" + key);
        inputs.put(KeyStroke.getKeyStroke("released " + key), "released" + key);
        actions.put("pressed" + key, new AddKeyAction(key));
        actions.put("released" + key, new RemoveKeyAction(key));
    }

    /**
     * Check if a key is pressed. Returns false if key is not listenable
     * @param key The string representation of the key
     * @return A boolean equals to true if the key is pressed
     */
    public boolean isPressed(String key) {
        return allPressedKeys.contains(key);
    }

    /**
     * Helper nested class allowing to add a specific key to the KeyStack
     */
    private class AddKeyAction extends AbstractAction {
        private String key;

        public AddKeyAction(String key) {
            this.key = key;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            allPressedKeys.add(this.key);
        }
    }

    /**
     * Helper nested class allowing to remove a specific key to the KeyStack
     */
    private class RemoveKeyAction extends AbstractAction {
        private String key;

        public RemoveKeyAction(String key) {
            this.key = key;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            allPressedKeys.remove(this.key);
        }
    }
}
