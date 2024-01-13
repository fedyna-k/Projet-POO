package graphics;

public class Dialogs {
    static public String get(int index) {
        switch (index) {
            case 0: return "I must kill CABEZENON and bring back peace to this world.";
            case 1: return "My sword is heavy, I have to keep that in mind.";
            case 2: return "By blocking, I should be able to half my wounds.";
            case 3: return "By rolling, I should pass through enemies and their slashes with ease.";
            case 4: return "I hope I'm well enough prepared, who knows what's inside that mansion ?";
            case 5: return "I've heard CABEZENON lives nearby...";
            case 6: return "Here it is, I have to be well prepared !";
            case 7: return "For the people of this world !";
        
            default: return "";
        }
    }

    static public int[][] triggers = {
        {640, 704, 7168, 7680},
        {192, 576, 7104, 7168},
        {256, 576, 6848, 6912},
        {640, 960, 5504, 5568},
        {5056, 5120, 7104, 7680},
        {6208, 6528, 6208, 6272},
        {6208, 6528, 5932, 5696}
    };
}
