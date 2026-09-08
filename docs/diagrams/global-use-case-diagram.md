# Diagramme de cas d'utilisation global

```mermaid
---
config:
  layout: elk
---
flowchart LR
    classDef actor fill:#f5f5f5,stroke:#333,stroke-width:1px
    classDef uc fill:#e8f0fe,stroke:#4285f4,stroke-width:1px

    Utilisateur["Utilisateur"]:::actor
    Eleve -.->|"généralise"| Utilisateur
    Formateur -.->|"généralise"| Utilisateur
    Referente -.->|"généralise"| Utilisateur
    Admin -.->|"généralise"| Utilisateur
    Eleve["Élève"]:::actor
    Formateur["Formateur"]:::actor
    Referente["Référente administrative"]:::actor
    Admin["Administrateur"]:::actor


    subgraph FormaGest["Application FormaGest"]
        Connect(["Se connecter"]):::uc
        Id(["Saisir son identifiant"]):::uc
        Mdp(["Saisir son mot de passe"]):::uc
        Promos(["Consulter les promotions"]):::uc
        Cal(["Accéder à son calendrier"]):::uc
        PromosCharge(["Consulter les promotions dont il a la charge"]):::uc
        CoursAnime(["Consulter les cours qu'il anime"]):::uc
        Filieres(["Gérer les filières"]):::uc
        Cursus(["Gérer les cursus"]):::uc
        Cours(["Gérer les cours"]):::uc
        PlanCursus(["Planifier un cursus"]):::uc
        PlanCours(["Planifier les cours d'une promotion"]):::uc
        InscrCours(["Inscrire un élève à un cours"]):::uc
        InscrPromo(["Inscrire un élève à une promotion"]):::uc
        Comptes(["Gérer les comptes utilisateurs"]):::uc

        Connect -. "«include»" .-> Mdp
        Connect -. "«include»" .-> Id
    end

    Eleve --- Cal
    Formateur --- PromosCharge
    Formateur --- CoursAnime
    Referente --- Filieres
    Referente --- Cursus
    Referente --- Cours
    Referente --- PlanCursus
    Referente --- PlanCours
    Referente --- InscrCours
    Referente --- InscrPromo
    Admin --- Comptes
    Utilisateur --- Connect
    Utilisateur --- Promos
```

![Diagramme de cas d'utilisation global, export UMLet](global-use-case-diagram.svg)

### Acteurs et généralisation

Les quatre acteurs du cahier des charges (l'élève, la référente administrative, le formateur et l'administrateur) accèdent tous à l'application par un compte authentifié. Pour éviter de répéter les cas d'utilisation communs sur chacun, le diagramme introduit un acteur générique, `Utilisateur`, dont les quatre héritent par une relation. Ce qui est rattaché à `Utilisateur` est donc accessible aux quatre : se connecter et consulter les promotions.

### Cas d'utilisation par acteur

`Utilisateur` (donc tout le monde) : **Se connecter**, qui inclut *Saisir son identifiant* et *Saisir son mot de passe* (RG12), et **Consulter les promotions** (RG05).

L'**élève** : **Accéder à son calendrier**, en lecture seule (RG11, RG13). Le calendrier n'est pas stocké, il résulte de l'agrégation des inscriptions.

Le **formateur** consulte **les promotions dont il a la charge** et **les cours qu'il anime**, sans droit de modification. 

La **référente administrative** porte l'essentiel de la charge fonctionnelle, sur trois axes :

- structure pédagogique : **Gérer les filières**, **Gérer les cursus**, **Gérer les cours** (RG01, RG02) ;
- planification : **Planifier un cursus**, qui crée une promotion (RG03, RG04), et **Planifier les cours d'une promotion** ;
- inscriptions : **Inscrire un élève à une promotion** (RG06) et **Inscrire un élève à un cours** à l'unité (RG07).

L'**administrateur** se voit rattacher **Gérer les comptes utilisateurs**.