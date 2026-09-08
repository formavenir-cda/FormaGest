# Diagramme de classes métier

![Diagramme de classes métier de FormaGest](./diagramme-classes-metier.png)

## Présentation

Ce diagramme représente les principales classes du domaine de FormaGest,
leurs attributs et comportements métier ainsi que leurs relations. Les
identifiants techniques et les détails de persistance n'y figurent pas.

## Utilisateurs

`Utilisateur` est une classe abstraite contenant les informations communes à
tous les comptes. Elle généralise quatre types d'utilisateurs :

- `Eleve` ;
- `Formateur` ;
- `ReferenteAdmin` ;
- `Admin`.

Le type de l'utilisateur est donc représenté par l'héritage et non par une
énumération de rôles.

## Structure pédagogique

Une `Filiere` regroupe plusieurs `Cursus`. Chaque cursus organise un ou
plusieurs cours au moyen de `CursusCours`, qui porte leur ordre dans la
progression pédagogique. Un même `Cours` peut ainsi être utilisé dans plusieurs
cursus.

La composition entre `Cursus` et `CursusCours` indique que l'élément ordonné
n'existe que dans le contexte de son cursus.

## Planification

Un `Cursus` peut être planifié en plusieurs `Promotion`. Une promotion contient
des `CoursPlanifié`, chacun associé à un cours positionné dans le cursus.

La composition entre `Promotion` et `CoursPlanifié` exprime qu'un cours
planifié n'existe pas indépendamment de sa promotion. Un `Formateur` peut animer
plusieurs cours planifiés et un cours planifié peut ne pas encore avoir de
formateur ou en avoir un seul.

## Inscriptions

`Inscription` est une classe abstraite spécialisée en :

- `InscriptionPromotion`, qui concerne une `Promotion` ;
- `InscriptionCours`, qui concerne un `CoursPlanifié` particulier.

Un `Eleve` peut posséder plusieurs inscriptions, mais chaque inscription
appartient à un seul élève. L'attribut `force` de `InscriptionCours` indique que
l'inscription a été autorisée malgré le non-respect de l'ordre pédagogique.

Le calendrier personnel n'est pas modélisé comme une classe persistante. Il
est obtenu à partir des cours planifiés de la promotion de l'élève et de ses
inscriptions individuelles.

## Légende UML

- triangle blanc : généralisation ou héritage ;
- losange blanc : agrégation ;
- losange noir : composition ;
- trait continu : association ;
- `1`, `0..1`, `0..*` et `1..*` : multiplicités.
