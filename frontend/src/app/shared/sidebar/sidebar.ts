import { Component } from '@angular/core';

type SidebarNavItem = {
  label: string;
  icon: string;
  route?: string;
};

@Component({
  selector: 'app-sidebar',
  styleUrl: './sidebar.scss',
  templateUrl: './sidebar.html',
})
export class Sidebar {
  protected readonly mainNavItems: SidebarNavItem[] = [
    { label: 'Accueil', icon: 'home' },
    { label: 'Promotions', icon: 'groups' },
    { label: 'Mon calendrier', icon: 'calendar_month' },
    { label: 'Mes inscriptions', icon: 'assignment' },
    { label: 'Mes cours', icon: 'menu_book' },
    { label: 'Mes promotions', icon: 'school' },
    { label: 'Inscriptions', icon: 'how_to_reg' },
    { label: 'Eleves', icon: 'person_search' },
    { label: 'Filieres', icon: 'account_tree' },
    { label: 'Cursus', icon: 'route' },
    { label: 'Cours', icon: 'library_books' },
    { label: 'Formateurs', icon: 'badge' },
    { label: 'Utilisateurs', icon: 'manage_accounts' },
  ];
}
