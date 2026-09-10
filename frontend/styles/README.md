# src/styles — FormaGest

Organisation

    src/styles/
      tokens/        colors, typography, spacing, radius, elevation, breakpoints
      components/    surcouches FormaGest des composants Angular Material
      themes/        thème Material 3 construit à partir des tokens
      styles.scss    point d'entrée (angular.json)

Règles

1. Aucune valeur littérale (#123456, 16px…) dans un composant : tout passe par un token.
2. `@use 'styles/tokens' as t;` en tête de chaque partial ou SCSS de composant.
3. Les composants Angular Material sont thémés via `themes/`, jamais par surcharge ponctuelle.
4. Les états focus utilisent systématiquement `$fg-focus-ring` (3px, offset 2px) — jamais `outline: none`.

Correspondance maquette → Angular Material

| Élément FormaGest        | Angular Material                          |
|--------------------------|-------------------------------------------|
| Bottom navigation mobile | mat-toolbar + mat-icon (ou nav custom)     |
| Navigation rail tablette | mat-nav-list compacte                      |
| Sidenav desktop          | mat-sidenav (mode side) + mat-nav-list     |
| Toolbar                  | mat-toolbar                                |
| Accès rapide             | mat-card (appearance outlined) cliquable   |
| Actualité                | mat-list-item (mobile/desktop) / mat-card (tablette) |
| Badge messages           | matBadge                                   |
| Avatar                   | mat-icon-button + initiales                |
| Voir tout                | mat-button                                 |
| Nouvelle demande         | mat-flat-button color="primary"            |
