package character;

import geometry.Vector2D;

// Classe représentant un petit monstre Slime dans le jeu
public class Slime extends Entity{
    // Nom du Slime
    private String nom;

    // Points de vie actuels du Slime
    private int pointDeVie;

    // Force d'attaque du Slime
    private int force;

    // Constructeur pour initialiser un Slime avec un nom, des points de vie et une force donnés
    public Slime(String nom, int pointDeVie, int force) {
        this.nom = nom;
        this.pointDeVie = pointDeVie;
        this.force = force;
    }

    // Méthodes d'accès pour obtenir et définir le nom du Slime
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    // Méthodes d'accès pour obtenir et définir les points de vie du Slime
    public int getPointDeVie() {
        return pointDeVie;
    }

    public void setPointDeVie(int pointDeVie) {
        this.pointDeVie = pointDeVie;
    }

    // Méthode abstraite à implémenter pour obtenir le décalage du Slime
    @Override
    public Vector2D getOffset() {
        return null;
    }
}
